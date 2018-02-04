package com.haulmont.addon.admintools.web.ssh;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.haulmont.addon.admintools.entity.SshCredentials;
import com.haulmont.addon.admintools.gui.components.XtermJs;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.DialogAction;
import com.haulmont.cuba.gui.components.HBoxLayout;
import com.haulmont.cuba.gui.components.PasswordField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.Datasource;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.haulmont.cuba.gui.components.Timer;

import static com.google.common.base.Preconditions.checkNotNull;
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
    protected XtermJs terminal;

    protected JSch jsch = new JSch();
    protected Session session;
    protected ChannelShell mainChannel;
    protected InputStream mainIn;
    protected PrintStream mainOut;

    protected NonBlockingIOUtils ioUtils = new NonBlockingIOUtils();

    @Override
    public void init(Map<String, Object> params) {
        SshCredentials credentials = metadata.create(SshCredentials.class);
        credentials.setPort(DEFAULT_SSH_PORT);
        sshCredentialsDs.setItem(credentials);

        terminal.setDataListener(this::terminalDataListener);
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

    public void connect() throws ValidationException {
        if (! validateAll()) {
            return;
        }

        if (isMainChannelOpen()) {
            showOptionDialog(getMessage("confirmReconnect.title"), getMessage("confirmReconnect.msg"),
                    MessageType.CONFIRMATION, new Action[]{
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> {
                                disconnect();
                                internalConnect();
                            }),
                            new DialogAction(DialogAction.Type.CANCEL, Action.Status.NORMAL)
                    });
        } else {
            internalConnect();
        }
    }

    protected void internalConnect() {
        SshCredentials credentials = sshCredentialsDs.getItem();
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

            terminal.writeln(formatMessage("console.connected", credentials.getHostname()));
        } catch (JSchException | IOException e) {
            terminal.writeln(formatMessage("console.error",e.getMessage()));

            log.info("User can't create ssh connection", e);
        }
    }

    public Component generateHostnameField(Datasource datasource, String fieldId) {
        TextField hostnameField = componentsFactory.createComponent(TextField.class);
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
        PasswordField component = componentsFactory.createComponent(PasswordField.class);
        component.setDatasource(datasource, fieldId);
        return component;
    }

    public void disconnect() {
        mainChannel.disconnect();
        session.disconnect();

        terminal.writeln(formatMessage("console.disconnected", sshCredentialsDs.getItem().getHostname()));
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