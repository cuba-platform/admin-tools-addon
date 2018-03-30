package com.haulmont.addon.admintools.global.ssh;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Table(name = "CUBAAT_SSH_CREDENTIAL")
@Entity(name = "cubaat$SshCredential")
@NamePattern("%s@%s|login,hostname")
public class SshCredential extends StandardEntity {
    private static final long serialVersionUID = -8097580356590206782L;

    @NotNull
    @Column(name = "HOSTNAME", nullable = false)
    protected String hostname;

    @Column(name = "IS_FOR_EVERYONE_USERS")
    protected Boolean isForEveryoneUsers;

    @Max(message = "{msg://com.haulmont.addon.admintools.entity/max-port-validation}", value = 65535)
    @Min(message = "{msg://com.haulmont.addon.admintools.entity/min-port-validation}", value = 1)
    @NotNull
    @NumberFormat(pattern = "#")
    @Column(name = "PORT", nullable = false)
    protected Integer port = 22;

    @NotNull
    @Column(name = "LOGIN", nullable = false)
    protected String login;

    @Column(name = "PASSWORD")
    protected String password;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRIVATE_KEY_ID")
    protected FileDescriptor privateKey;

    @Column(name = "PASSPHRASE")
    protected String passphrase;

    public void setIsForEveryoneUsers(Boolean isForEveryoneUsers) {
        this.isForEveryoneUsers = isForEveryoneUsers;
    }

    public Boolean getIsForEveryoneUsers() {
        return isForEveryoneUsers;
    }


    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getPassphrase() {
        return passphrase;
    }


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