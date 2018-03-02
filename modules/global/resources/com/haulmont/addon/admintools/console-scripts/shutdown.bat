set tomcat_dir=%1\bin\
cd /d "%tomcat_dir%"
set _RUNJAVA=
set _RUNJDB=
set _EXECJAVA=
set JSSE_OPTS=
set JAVA_OPTS=
set DEBUG_OPTS=
shutdown.bat & exit