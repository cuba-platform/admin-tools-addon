[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

# CUBA Platform Component - Admin Tools

This application component can be used to extend the capabilities of a [CUBA.Platform](https://www.cuba-platform.com/) application for interactive runtime application diagnose.

The component comprises the following parts:
* [Runtime diagnose components](#runtime-diagnose-components);
* [SQL Script generator](#sql-script-generator);
* [Shell Executor](#shell-executor);
* [SSH Terminal](#ssh-terminal);
* [Config Loader](#config-loader);
* [Console Script Loader](#console-script-loader);
* [Auto Import Subsystem](#auto-import-subsystem);
* [Tomcat JMX](#tomcat-jmx).

## Installation
The process of the component installation comprises several steps, which are described below.

1. Add the following maven repository `https://repo.cuba-platform.com/content/groups/work`
to the build.gradle file of your CUBA application:
   
     ```groovy
     buildscript {
           
        //...
            
        repositories {
            
           // ...
            
           maven {
              url  "https://repo.cuba-platform.com/content/groups/work"
           }
        }
            
        // ...
     }
     ```

2. Select a version of the add-on, which is compatible with the platform version used in your project:

| Platform Version | Add-on Version |
| ---------------- | -------------- |
| 6.8.x            | 1.0.1   |

3. Add a custom application component to your project:
   
   * Artifact group: `com.haulmont.addon.admintools`
   * Artifact name: `cuba-at-global`
   * Version: *add-on version*
   
**Note:** To activate the Auto Import subsystem, additional configurations are required (for more details, please refer to
this [paragraph](#creating-an-auto-import-configuration-file)).

## Runtime diagnose components
Components 'Groovy Console', 'JPQL Console', 'SQL Console' and 'Diagnose Execution Logs' are imported 
from **CUBA Platform Component - Runtime diagnose**. See the [documentation](https://github.com/mariodavid/cuba-component-runtime-diagnose/blob/master/README.md).

###  Enhancement
* Added an ability to import scripts from zip files for 'Groovy Console', 'JPQL Console', 'SQL Console';
* Added the autocomplete for providing suggestions  while you type jpql request in 'JPQL Console'.
  
## SQL Script Generator
This functionality of the Admin Tools component allows someone generating SQL scripts for selected project entities.

![generate-scripts-menu](img/gen-scripts-menu.png)

JPQL requests are used for entity selection. Start by specifying a metaclass, view and type of a script to be generated 
(insert, update, insert update). Selecting a metaclass automatically generates a JPQL request:

```sql
select e from example$Entity e
```

![generate-scripts-dialog](img/gen-scripts-dialog.png)

After that, SQL scripts of the specified type are generated for the found entities. If there are no results found, then 
the system shows a corresponding notification: 'No data found'. You can limit the number of entities to be loaded using 
the 'Entity Limit' field.

*Note:* if you cancel the process, it will not be stopped on the middleware level.

## Shell Executor
Shell Executor is a functionality for running UNIX shell scripts (sh files). It allows someone operating with data efficiently and 
enables to run various OS commands right from the application UI. Note that this functionality is available only if you 
use UNIX systems.

![shell_console_menu_item](img/shell-executor-menu-item.png)

![shell_console](img/shell-executor.png)

The screen consists of two sections: the first section allows someone inputting and managing scripts and the second one provides functionalities
for operating with results.

The toolbar of the first section comprises action buttons that enable to run scripts, cancel the operation, clear input data
and generate diagnose file requests. 
In addition to the console, there is the 'Arguments' field for specifying positional parameters.

The second section allows someone viewing results of running scripts, saving and clearing them.

When scripts are run, the system generates temporary files, which are stored in the `.\tomcat\temp` directory. Note
that the component does not remove these files automatically. 

## SSH Terminal
SSH Terminal allows someone operating network services on remote servers right from the application UI.
 
 ![ssh_console_menu_item](img/ssh-terminal_menu_item.png)
 
Before connecting to a remote server, it is required to specify credentials and a hostname in the corresponding section.
As an alternative use a private key and a passphrase for a connection instead a password. After that, use action buttons to connect to a server
via SSH or to disconnect. The toolbar of SSH Console also comprises the __Fit__ button, which allows someone managing the size of a terminal.

Connection parameters can be stored in a database (exclude a password and a passphrase). For saving, removing and loading
connection parameters use corresponding buttons. By default connection parameters is saving only for a current user, 
if the checkbox 'Is for everyone users' isn't checked. All available connection parameters are showed in 'Saved Sessions' list.

![ssh_console_connected](img/ssh-terminal_connected.png)

### Known issues

- The `screen` utility does not work in the terminal

## Config Loader
Using the Config Loader functionality it is possible to upload configuration files and various scripts to a configuration 
directory right from the system UI without stopping the application. 

![Load-config-menu-item](img/config-loader-menu-item.png)

The location of the configuration directory is './tomcat/conf'. Additionally, you can specify a relative path
in the corresponding field.

![load-config](img/config-loader.png)

When trying to upload a config that already exists in the configuration directory or if names of two configs coincide, 
a message requesting to confirm file replacement appears.

![confirm-file-replace](img/confirm-file-replacement.png)

## Console Script Loader
![console-script-loader-menu-item](img/console-script-loader-menu-item.png)

Console Script Loader functionality is used to import scripts in the [Groovy, JPQL and SQL consoles](#runtime-diagnose-components).
Upload zip to corresponding field and it redirects to a necessary console with a script in a text field.

![console-script-loader-menu-item](img/config-loader.png)

## Auto Import Subsystem

The AutoImport subsystem is designed to preconfigure servers and transfer data among servers. The process is launched 
automatically during the server start/restart. 

For importing data, specify a path to a zip-archive or a json file in the configuration file. If an archive with the same name has already
 been processed, then it is not considered by the system and skipped.
 
There are several options for exporting various entities:

* To export access groups, open Menu: Administration > Access Groups. There, select the required groups in the table and click the 
__Export as ZIP__  or __Export as JSON__ button.
(learn more about this functionality [here](https://doc.cuba-platform.com/manual-6.8/groups.html)). 
* To export user roles, open Menu: Administration > Roles. There, select the required roles and click 
the __Export as ZIP__ or __Export as JSON__ button.
(learn more about this functionality [here](https://doc.cuba-platform.com/manual-6.8/roles.html)). 
* To export any other entities, open Menu: Administration > Entity Inspector and specify an entity type in the corresponding field.
 Then select the required entities and click The __Export as ZIP__ or __Export as JSON__ button. (learn more about this functionality 
[here](https://doc.cuba-platform.com/manual-6.8/entity_inspector.html)). 

#### Creating an auto-import configuration file

1. Configuration file example:
       
     ```xml
     <?xml version="1.0" encoding="UTF-8" standalone="no"?>
     <auto-import>
         <!--default processor-->
         <auto-import-file path="com/company/example/Roles.zip" bean="admintools_DefaultAutoImportProcessor"/>
         <auto-import-file path="com/company/example/Groups.json" class="com.company.example.SomeProcessor"/>
        
     </auto-import>
     ```

     Where path is a path to the data file, bean/class — a processor. Bean = [bean name], class = [class path].
   
2. Add the `admintools.autoImportConfig` property to `app.properties` and specify the configuration file path.
The example of `app-properties` with the auto-import configuration is given below:

    ```properties
    admintools.autoImportConfig = +com/haulmont/addon/admintools/auto-import.xml
    ```

## Custom import processor

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
to the required file in the configuration file. If a processor is implemented as a class,
then provide a path to the class.
   
     ```xml
     <?xml version="1.0" encoding="UTF-8" standalone="no"?>
     <auto-import>
         ...
      
         <auto-import-file path="com/company/example/Reports.zip" bean="admintools_ReportsAutoImportProcessor"/>
		 <auto-import-file path="com/company/example/Reports.json" class="com.company.example.ReportsAutoImportProcessor"/>
          
         ...
     </auto-import>
     ```
   
### Additional information

#### Logging

Logging information is available in the `app.log` file.

##### Successful import

```
com.haulmont.addon.admintools.core.auto_import.AutoImporterImpl - file com/company/autoimporttest/Roles.zip is importing
...
com.haulmont.addon.admintools.core.auto_import.AutoImporterImpl - file com/company/autoimporttest/Roles.zip has been imported
```

##### Incorrect name of a processor

```
com.haulmont.addon.admintools.core.auto_import.AutoImporterImpl - file com/company/autoimporttest/Roles.zip is importing
...
com.haulmont.addon.admintools.core.auto_import.AutoImporterImpl - org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'autoimport_InvalidAutoImportProcessor' available
```

```
com.haulmont.addon.admintools.core.auto_import.AutoImporterImpl - file com/company/autoimporttest/Roles.zip is importing
...
com.haulmont.addon.admintools.core.auto_import.AutoImporterImpl - java.lang.ClassNotFoundException: com.example.InvalidAutoImportProcessor
```

##### Uploaded archive is not found

```
com.haulmont.addon.admintools.core.auto_import.AutoImporterImpl - file com/company/autoimporttest/Roles.zip is importing
com.haulmont.addon.admintools.core.auto_import.AutoImporterImpl - File not found by the path com/example/invalid.zip
```

### Known Issues
- class com.haulmont.cuba.core.app.importexport.EntityImportViewBuilder by class ExtendedEntityImportViewBuilder
for build json if ONE_TO_MANY meta property has type ASSOCIATION.

## Tomcat JMX
Tomcat JMX is a managed bean, allows you to execute operations with Tomcat server currently running the application.
It is supported on Windows and Unix OS. The bean can be accessed from Menu: Administration → JMX Console. 
Start searching by the object name 'Tomcat' and the domain 'cuba-at'. There are two objects: TomcatCore for operating 
with a core module and TomcatWeb for operating with web module. If your application locates a core and a web module in
one place, then choose any of them.

![find tomcat jmx](img/find-tomcat-jmx.png) 

JMX Tomcat allows executing the following operations:

* getTomcatAbsolutePath: returns an absolute path to the Tomcat directory;
* shutdown: shutdowns a Tomcat process;
* reboot: shutdowns an existing Tomcat process and runs a new one;
* runShellScript: runs a script in a Tomcat workspace with the next arguments:
    1. Path — a relative of a Tomcat directory;
    2. Arguments — arguments that can be specified for a script.

![tomcat jmx](img/jmx-tomcat-core.png)