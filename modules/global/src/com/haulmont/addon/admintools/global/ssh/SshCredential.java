package com.haulmont.addon.admintools.global.ssh;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "CUBAAT_SSH_CREDENTIAL")
@Entity(name = "cubaat$SshCredential")
@NamePattern("%s (%s) - |hostname,port")
public class SshCredential extends StandardEntity {
    private static final long serialVersionUID = -8097580356590206782L;

    @NotNull
    @Column(name = "HOSTNAME", nullable = false)
    protected String hostname;

    @Max(message = "{msg://com.haulmont.addon.admintools.entity/max-port-validation}", value = 65535)
    @Min(message = "{msg://com.haulmont.addon.admintools.entity/min-port-validation}", value = 1)
    @NotNull
    @NumberFormat(pattern = "#")
    @Column(name = "PORT", nullable = false)
    protected Integer port;

    @NotNull
    @Column(name = "LOGIN", nullable = false)
    protected String login;

    @Column(name = "PASSWORD")
    protected String password;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRIVATE_KEY_ID")
    protected FileDescriptor privateKey;

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPrivateKey(FileDescriptor privateKey) {
        this.privateKey = privateKey;
    }

    public FileDescriptor getPrivateKey() {
        return privateKey;
    }


}