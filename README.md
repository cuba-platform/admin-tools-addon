# CUBA Platform Component - Admin Tools

The component comprises the following parts:
* [Generator of SQL scripts](#sql-scripts-generator);
* [Auto Import subsystem](#auto-import);
* [JPQL and SQL console](#jpql-and-sql-console);
* [Groovy console](#groovy-console);
* [Shell Console](#shell-console);
* [SSH Console](#ssh-console);
* [JMX Tomcat](#jmx-tomcat).

## Installation

The process of the component installation consists of several steps and is described below.

1. Add the following maven repository `https://repo.cuba-platform.com/content/repositories/premium-snapshots`
to the build.gradle file of your CUBA application:
   
     ```groovy
     buildscript {
           
        //...
            
        repositories {
            
           // ...
            
           maven {
              url  "https://repo.cuba-platform.com/content/repositories/premium-snapshots"
           }
        }
            
        // ...
     }
     ```

2. Select a version of the add-on which is compatible with the platform version used in your project:

| Platform Version | Add-on Version |
| ---------------- | -------------- |
| 6.8.1            | 0.1-SNAPSHOT   |

Add a custom application component to your project:
   
   * Artifact group: `com.haulmont.addon.admintools`
   * Artifact name: `cuba-at-global`
   * Version: *add-on version*
   
**Note:** To activate the Auto Import subsystem, additional configurations are required (for more details, please refer to
this [paragraph](#creating-an-auto-import-configuration-file)).
  
## SQL Scripts Generator

This functionality of the Admin Tools component allows generating SQL scripts for selected entities of a project.

![generate-scripts-menu](img/gen_scripts_menu.png)

JPQL requests are used for entity selection. Start by specifying a metaclass, view and type of a script to be generated 
(insert, update, insert update). Selecting a metaclass automatically generates a JPQL request:

```sql
select e from example$Entity e
```

![generate-scripts-dialog](img/gen_scripts_dialog.png)

After that, SQL scripts of the specified type are generated for the found entities. If there are no results found, then 
the system shows a corresponding notification: 'No data found'.

*Note: if you cancel the process, it won't be stopped on the middleware level*

## Auto Import

The AutoImport subsystem is designed to preconfigure servers and transfer data among servers. The process is launched 
automatically during the server start/restart. 

For importing data, specify a path to a zip-archive or a json file in a configuration file. If an archive with the same name has already
 been processed, then it is not considered by the system and skipped.
 
There are several options for exporting various entities:

* To export access groups, open Menu: Administration > Access Groups. There, select the required groups in the table and click the 
__Export as ZIP__ button  or  __Export as JSON__ button.
(learn more about this functionality [here﻿](https://doc.cuba-platform.com/manual-6.8/groups.html)). 
* To export user roles, open Menu: Administration > Roles. There, select the required roles and click 
the __Export as ZIP__ button  or  __Export as JSON__ button.
(learn more about this functionality [here﻿](https://doc.cuba-platform.com/manual-6.8/roles.html)). 
* To export any other entities, open Menu: Administration > Entity Inspector and specify an entity type in the corresponding field.
 Then select the required entities and click The __Export as ZIP__ button  or  __Export as JSON__ button. (learn more about this functionality 
[here﻿](https://doc.cuba-platform.com/manual-6.8/entity_inspector.html)). 

#### Creating an auto-import configuration file

1. Configuration file example:
       
     ```xml
     <?xml version="1.0" encoding="UTF-8" standalone="no"?>
     <auto-import>
         <!--default processor-->
         <auto-import-file path="com/company/example/Roles.zip" bean="admintools_DefaultAutoImportProcessor"/>
         <auto-import-file path="com/company/example/Groups.json" bean="admintools_DefaultAutoImportProcessor"/>
        
     </auto-import>
     ```

     Where path is a path to the data file, bean/class — a processor. Bean = [bean name], class = [class path].
   
2. Add the `admin.autoImportConfig` property to `app.properties` and, additionally, specify the configuration file path.
The example of `app-properties` with the auto-import configuration is given below:

    ```properties
    admin.autoImportConfig = +com/haulmont/addon/admintools/auto-import.xml
    ```

### Custom import processor

A class-processor is responsible for file processing and can be implemented as a bean or a simple java-class. 
If necessary, you can provide a custom implementation of a processor for any entity within a project by applying the 
AutoImportProcessor interface.

#### Creating a custom import processor

To create a custom processor, the next steps should be taken:

1. Create a class that implements the AutoImportProcessor interface
   
     ```java
     @Component("admintools_ReportsAutoImportProcessor")
     public class ReportsAutoImportProcessor implements AutoImportProcessor {
         @Inject
         protected ReportService reportService;
         @Inject
         protected Resources resources;
     
         @Override
         public void processFile(String filePath) throws Exception {
             try (InputStream inputStream = resources.getResourceAsStream(filePath)) {
                 byte[] fileBytes = IOUtils.toByteArray(inputStream);
                 reportService.importReports(fileBytes);
             }
         }
     }
     ```
   
2. If a processor is implemented as a java bean, then specify a component name and a path
to the required file in a configuration file. If a processor is implemented as a class,
then provide a path to the class.
   
     ```xml
     <?xml version="1.0" encoding="UTF-8" standalone="no"?>
     <auto-import>
         ...
      
         <auto-import-file path="com/company/example/Reports.zip" bean="admintools_ReportsAutoImportProcessor"/>
         ...
     </auto-import>
     ```
   
### Additional information.

#### Logging

See logging information in the `app.log` file.

##### Successful import

```
com.haulmont.addon.admintools.listeners.AutoImportListener - Importing file com/company/autoimporttest/Roles.zip by bean autoimport_RolesAutoImportProcessor
...
com.haulmont.addon.admintools.processors.DefaultAutoImportProcessor - Successful importing file com/company/autoimporttest/Roles.zip
```

##### Incorrect name of a processor

```
com.haulmont.addon.admintools.listeners.AutoImportListener - Importing file com/company/example/Groups.zip by bean autoimport_InvalidAutoImportProcessor
...
com.haulmont.addon.admintools.listeners.AutoImportListener - org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'autoimport_InvalidAutoImportProcessor' available
```

```
com.haulmont.addon.admintools.listeners.AutoImportListener - Importing file com/company/example/Groups.zip by class com.example.InvalidAutoImportProcessor ... com.haulmont.addon.admintools.listeners.AutoImportListener - java.lang.ClassNotFoundException: com.example.InvalidAutoImportProcessor
```

##### Uploaded archive is not found

```
com.haulmont.addon.admintools.listeners.AutoImportListener - Importing file com/example/invalid.zip by bean autoimport_ReportsAutoImportProcessor
com.haulmont.addon.admintools.processors.ReportsAutoImportProcessor - File com/example/invalid.zip not found.
```

## JPQL and SQL Console
JPQL and SQL Console allows interacting with an application database by using JPQL or SQL. 
These components are imported from **CUBA Platform Component - Runtime diagnose**.
See [Runtime diagnose documentation](https://github.com/mariodavid/cuba-component-runtime-diagnose/blob/master/README.md).

## Groovy Console
Groovy Console enables to interactively inspect the running application by entering a groovy script and executing it 
in an ad-hoc fashion. This component is imported from **CUBA Platform Component - Runtime diagnose**. 
See [Runtime diagnose documentation](https://github.com/mariodavid/cuba-component-runtime-diagnose/blob/master/README.md).

## Load Config
Using the Load Config functionality it is possible upload configuration files and various scripts to a configuration 
directory right from the system UI without stopping the application. 

![Load-config-menu-item](img/load-config-menu-item.png)

The location of the configuration directory is determined by the `cuba.confDir` application property. Additionally, you can
specify a relative path in the corresponding field.

![load-config](img/load-config.png)

When trying to upload a config that already exists in the configuration directory or if names of two configs coincide, 
a message requesting to confirm file replacement appears.

![confirm-file-replace](img/confirm-file-replacement.png)

## Shell Console
Shell Console is a functionality for running UNIX shell scripts (sh files). It allows operating with data efficiently and 
enables to run various OS commands right from the application UI. Note that this functionality is available only if you 
use UNIX systems.

![shell_console_menu_item](img/shell_console_menu_item.png)

![shell_console](img/shell_console.png)

The screen consists of two sections: the first section allows inputting and managing scripts and the second one provides functionalities
for operating with results.

The toolbar of the first section comprises action buttons that enable to run scripts, cancel the operation, clear input data
and generate diagnose file requests. 
In addition to the console, there is the 'Arguments' field for specifying positional parameters.

The second section allows viewing results of running scripts, saving and clearing them.

When scripts are run, the system generates temporary files, which are stored in the `.\tomcat\temp` directory. Note
that the component does not remove these files automatically. 

## SSH Console
SSH Console allows operating network services on remote servers right from the application UI.
 
 ![ssh_console_menu_item](img/SSH-Console_menu_item.png)
 
Before connecting to a remote server, it is required to specify credentials and a hostname in the corresponding section.
After that, use action buttons to connect to a server via SSH or to disconnect. The toolbar of SSH Console also comprises
the __Fit__ button, which allows managing the size of a terminal.

![ssh_console_connected](img/SSH-Console_connected.png)

### Known issues

- The `screen` utility does not work in the console

## JMX Tomcat
JMX Tomcat is a managed bean, which allows operating with Tomcat. JMX Tomcat is supported on Windows and Unix OS.
The bean can be accessed from Menu: Administration → JMX Console. Start searching by the object name 'Tomcat'
and the domain 'cuba-at'.

![find jmx tomcat](img/find-jmx-tomcat.png) 

JMX Tomcat allows executing the following operations:

* getTomcatAbsolutePath: returns an absolute path to the Tomcat directory;
* shutdown: shutdowns a Tomcat process;
* reboot: shutdowns an existing Tomcat process and runs a new one;
* runShellScript: runs a script in a Tomcat workspace with the next arguments:
    1. Path - a relative of a Tomcat directory;
    2. Arguments - arguments that can be specified for a script.

![jmx tomcat](img/jmx-tomcat.png)