package com.haulmont.addon.admintools.web.tomcat;

import com.haulmont.addon.admintools.global.tomcat.Tomcat;
import org.springframework.stereotype.Component;

@Component("cuba-at_TomcatWebMBean")
public class TomcatWeb extends Tomcat implements TomcatWebMBean {

}
