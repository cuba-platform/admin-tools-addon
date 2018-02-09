package com.haulmont.addon.admintools.web.ssh;

import com.google.common.io.CharStreams;
import com.haulmont.addon.admintools.entity.SshCredentials;
import com.haulmont.addon.admintools.gui.components.EnterReactivePasswordField;
import com.haulmont.addon.admintools.gui.components.XtermJs;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.executors.BackgroundTask;
import com.haulmont.cuba.gui.executors.BackgroundTaskWrapper;
import com.haulmont.cuba.gui.executors.TaskLifeCycle;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.apache.commons.io.Charsets.toCharset;

public class SshConsole extends AbstractWindow {

    public static final Integer DEFAULT_SSH_PORT = 22;

    private Logger log = LoggerFactory.getLogger(SshConsole.class);

    @Inject
    protected Metadata metadata;

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Datasource<SshCredentials> sshCredentialsDs;

    @Inject
    protected ProgressBar terminalProgressBar;

    @Inject
    protected XtermJs terminal;

    protected JSch jsch = new JSch();
    protected Session session;
    protected ChannelShell mainChannel;
    protected InputStream mainIn;
    protected PrintStream mainOut;

    protected NonBlockingIOUtils ioUtils = new NonBlockingIOUtils();
    protected BackgroundTaskWrapper<Integer, Void> connectionTaskWrapper;
    protected SshCredentials credentials;
    protected TextField hostnameField;

    @Override
    public void init(Map<String, Object> params) {
        SshCredentials credentials = metadata.create(SshCredentials.class);
        credentials.setPort(DEFAULT_SSH_PORT);
        sshCredentialsDs.setItem(credentials);

        BackgroundTask<Integer, Void> connectionTask = new BackgroundTask<Integer, Void>(10, getFrame()) {
            @Override
            public Void run(TaskLifeCycle<Integer> taskLifeCycle) throws Exception {
                internalConnect();
                return null;
            }

            @Override
            public void done(Void result) {
                terminalProgressBar.setIndeterminate(false);
            }
        };
        connectionTaskWrapper = new BackgroundTaskWrapper<>(connectionTask);

        terminal.setDataListener(this::terminalDataListener);
    }

    @Override
    public void ready() {
        hostnameField.requestFocus();
    }

    @Override
    protected boolean preClose(String actionId) {
        if (isMainChannelOpen()) {
            mainChannel.disconnect();
        }
        if (isSessionOpen()) {
            session.disconnect();
            showNotification(formatMessage("console.disconnected", sshCredentialsDs.getItem().getHostname()));
        }
        return super.preClose(actionId);
    }

    protected void terminalDataListener(String data) {
        if (! isMainChannelOpen()) {
            return;
        }

        mainOut.append(data);
        mainOut.flush();
    }

    protected boolean isMainChannelOpen() {
        return mainChannel != null && mainChannel.isConnected();
    }

    protected  boolean isSessionOpen() {
        return session != null && session.isConnected();
    }

    public void connect() {
        if (! validateAll()) {
            return;
        }

        if (isMainChannelOpen()) {
            showOptionDialog(getMessage("confirmReconnect.title"), getMessage("confirmReconnect.msg"),
                    MessageType.CONFIRMATION, new Action[]{
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> {
                                disconnect();
                                executeConnectionProgressTask();
                            }),
                            new DialogAction(DialogAction.Type.CANCEL, Action.Status.NORMAL)
                    });
        } else {
            executeConnectionProgressTask();
        }

        // resolve problem with size of console
        terminal.fit();
    }

    protected void executeConnectionProgressTask() {
        credentials = sshCredentialsDs.getItem();
        terminalProgressBar.setIndeterminate(true);
        connectionTaskWrapper.restart();
        terminal.writeln(formatMessage("console.connected", credentials.getHostname()));
    }

    protected void internalConnect() {
        try {
            session = jsch.getSession(
                    credentials.getLogin(), credentials.getHostname(), credentials.getPort());
            session.setPassword(credentials.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            mainChannel = (ChannelShell) session.openChannel("shell");
            mainChannel.setPtyType("xterm");
            mainChannel.setEnv("LANG", "en_US.UTF-8");
            mainChannel.connect();

            mainOut = new PrintStream(mainChannel.getOutputStream());
            mainIn = mainChannel.getInputStream();
        } catch (JSchException | IOException e) {
            terminal.writeln(formatMessage("console.error",e.getMessage()));

            log.info("User can't create ssh connection", e);
        }
    }

    public Component generateHostnameField(Datasource datasource, String fieldId) {
        hostnameField = componentsFactory.createComponent(TextField.class);
        hostnameField.setDatasource(datasource, "hostname");
        hostnameField.setWidth("70%");

        TextField portField = componentsFactory.createComponent(TextField.class);
        portField.setDatasource(datasource, "port");
        portField.setWidth("60px");

        HBoxLayout hostnameBox = componentsFactory.createComponent(HBoxLayout.class);

        hostnameBox.add(hostnameField);
        hostnameBox.add(portField);

        hostnameBox.expand(hostnameField);
        hostnameBox.setSpacing(true);

        return hostnameBox;
    }

    public Component generatePasswordField(Datasource datasource, String fieldId) {
        EnterReactivePasswordField component = componentsFactory.createComponent(EnterReactivePasswordField.class);
        component.addEnterPressListener(e -> connect());
        component.setDatasource(datasource, fieldId);
        return component;
    }

    public void disconnect() {
        if (isMainChannelOpen()) {
            mainChannel.disconnect();
        }
        if (isSessionOpen()) {
            session.disconnect();
            terminal.writeln(formatMessage("console.disconnected", sshCredentialsDs.getItem().getHostname()));
        }
    }

    public void onUpdateConsole(Timer source) throws IOException {
        if (! isMainChannelOpen()) {
            return;
        }

        terminal.write(ioUtils.toStringWithBarrier(mainIn, StandardCharsets.UTF_8, 100));
    }


    public void onFitBtnClick() {
        terminal.fit();
    }

    protected static class NonBlockingIOUtils {

        public static final int BUF_SIZE = 0x800; // 2K chars (4K bytes)

        private Logger log = LoggerFactory.getLogger(NonBlockingIOUtils.class);

        protected CharBuffer buf = CharBuffer.allocate(BUF_SIZE);

        /**
         * Inspired by {@link CharStreams#copy(Readable, Appendable)}
         */
        public String toString(InputStream input,
                               Charset encoding) throws IOException {
            InputStreamReader in = new InputStreamReader(input, toCharset(encoding));
            StringBuilder out = new StringBuilder();

            while (in.ready() && in.read(buf) != -1) {
                buf.flip();
                out.append(buf);
                buf.clear();
            }

            return out.toString();
        }

        /**
         * Inspired by {@link CharStreams#copy(Readable, Appendable)}
         */
        public String toStringWithBarrier(InputStream input,
                                          Charset encoding,
                                          int maxBarrier) throws IOException {
            InputStreamReader in = new InputStreamReader(input, toCharset(encoding));
            StringBuilder out = new StringBuilder();

            int barrier = 0;
            for (; in.ready() && in.read(buf) != -1 && barrier <= maxBarrier; barrier++) {
                buf.flip();
                out.append(buf);
                buf.clear();
            }

            log.warn("barrier " + barrier);
            if (isBarrierAchieved(barrier, maxBarrier)) {
                log.warn("A read has been stopped, because max barrier was achieved");
            }

            return out.toString();
        }

        protected boolean isBarrierAchieved(int barrier, int maxBarrier) {
            return barrier >= maxBarrier;
        }
    }
}