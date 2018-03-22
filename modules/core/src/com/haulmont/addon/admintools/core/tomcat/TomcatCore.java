package com.haulmont.addon.admintools.core.tomcat;

import com.haulmont.addon.admintools.global.tomcat.Tomcat;
import org.springframework.stereotype.Component;

@Component("cuba-at_TomcatCoreMBean")
public class TomcatCore extends Tomcat implements TomcatCoreMBean {

}
