package com.haulmont.addon.admintools.web.ssh;

import com.haulmont.addon.admintools.entity.SshCredentials;
import com.haulmont.addon.admintools.gui.components.EnterReactivePasswordField;
import com.haulmont.addon.admintools.gui.components.XtermJs;
import com.haulmont.addon.admintools.web.utils.NonBlockingIOUtils;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.executors.*;
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
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SshConsole extends AbstractWindow {

    public static final Integer DEFAULT_SSH_PORT = 22;
    public static final Integer CONNECTION_TIMEOUT_SECONDS = 22;

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

    @Inject
    protected BackgroundWorker backgroundWorker;

    protected JSch jsch = new JSch();
    protected Session session;
    protected ChannelShell mainChannel;
    protected InputStream mainIn;
    protected PrintStream mainOut;

    protected NonBlockingIOUtils ioUtils = new NonBlockingIOUtils();
    protected BackgroundTask<Integer, Void> connectionTask;
    protected BackgroundTaskHandler connectionTaskHandler;
    protected SshCredentials credentials;
    protected TextField hostnameField;

    @Override
    public void init(Map<String, Object> params) {
        SshCredentials credentials = metadata.create(SshCredentials.class);
        credentials.setPort(DEFAULT_SSH_PORT);
        sshCredentialsDs.setItem(credentials);

        connectionTask = new BackgroundTask<Integer, Void>(CONNECTION_TIMEOUT_SECONDS, getFrame()) {
            @Override
            public Void run(TaskLifeCycle<Integer> taskLifeCycle) throws JSchException, IOException {
                internalConnect();
                if (connectionTaskHandler.isCancelled()) {
                    disconnectSsh();
                }
                return null;
            }

            @Override
            public boolean handleException(Exception ex) {
                terminal.writeln(formatMessage("console.error", ex.getMessage()));
                disconnectSsh();
                terminalProgressBar.setIndeterminate(false);
                log.info("User can't create ssh connection", ex);
                return true;
            }

            @Override
            public void canceled() {
                terminal.writeln(formatMessage("console.disconnected", sshCredentialsDs.getItem().getHostname()));
                terminalProgressBar.setIndeterminate(false);
            }

            @Override
            public boolean handleTimeoutException() {
                terminal.writeln(formatMessage("console.disconnected.timeout", sshCredentialsDs.getItem().getHostname()));
                terminalProgressBar.setIndeterminate(false);
                return true;
            }

            @Override
            public void done(Void result) {
                terminalProgressBar.setIndeterminate(false);
            }
        };

        terminal.setDataListener(this::terminalDataListener);
    }

    @Override
    public void ready() {
        hostnameField.requestFocus();
    }

    @Override
    protected boolean preClose(String actionId) {
        if (isBackgroundTaskExecuted()) {
            connectionTaskHandler.cancel();
            showNotification(formatMessage("console.disconnected", sshCredentialsDs.getItem().getHostname()));
        } else {
            if (isMainChannelOpen()) {
                mainChannel.disconnect();
            }
            if (isSessionOpen()) {
                session.disconnect();
                showNotification(formatMessage("console.disconnected", sshCredentialsDs.getItem().getHostname()));
            }
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

    protected boolean isSessionOpen() {
        return session != null && session.isConnected();
    }

    protected boolean isBackgroundTaskExecuted() {
        return connectionTaskHandler != null && connectionTaskHandler.isAlive();
    }

    public void connect() {
        if (! validateAll() || isBackgroundTaskExecuted()) {
            return;
        }

        if (isMainChannelOpen()) {
            showOptionDialog(getMessage("confirmReconnect.title"), getMessage("confirmReconnect.msg"),
                    MessageType.CONFIRMATION, new Action[]{
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY).withHandler(e -> {
                                disconnectSsh();
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
        connectionTaskHandler = backgroundWorker.handle(connectionTask);
        connectionTaskHandler.execute();
        terminal.writeln(formatMessage("console.connected", credentials.getHostname()));
    }

    protected void internalConnect() throws JSchException, IOException {
        session = jsch.getSession(credentials.getLogin(), credentials.getHostname(), credentials.getPort());
        session.setPassword(credentials.getPassword());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        mainChannel = (ChannelShell) session.openChannel("shell");
        mainChannel.setPtyType("xterm");
        mainChannel.setEnv("LANG", "en_US.UTF-8");
        mainChannel.connect();

        mainOut = new PrintStream(mainChannel.getOutputStream());
        mainIn = mainChannel.getInputStream();
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
        if (isBackgroundTaskExecuted()) {
            connectionTaskHandler.cancel();
        } else disconnectSsh();
    }

    protected void disconnectSsh() {
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

}