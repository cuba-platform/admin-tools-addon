package com.haulmont.addon.admintools.global.ssh;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s (%s) - |hostname,port")
@MetaClass(name = "cubaat$SshCredentials")
public class SshCredentials extends BaseUuidEntity {
    private static final long serialVersionUID = 6669520364335521043L;

    @NotNull
    @MetaProperty(mandatory = true)
    protected String hostname;

    @Max(message = "{msg://com.haulmont.addon.admintools.entity/max-port-validation}", value = 65535)
    @Min(message = "{msg://com.haulmont.addon.admintools.entity/min-port-validation}", value = 1)
    @NotNull
    @NumberFormat(pattern = "#")
    @MetaProperty(mandatory = true)
    protected Integer port;

    @NotNull
    @MetaProperty(mandatory = true)
    protected String login;

    @NotNull
    @MetaProperty(mandatory = true)
    protected String password;

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


}