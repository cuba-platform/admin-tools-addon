set tomcat_dir=%1\bin\

call :get_tomcat_pid
call :tomcat_pid_is_number
if %is_number% == "true" (
    taskkill /pid %tomcat_pid%

    :loop
        timeout /t 1
        call :get_tomcat_pid
        call :tomcat_pid_is_number
    if %is_number% == "true" goto loop
)

call :get_tomcat_pid
call :tomcat_pid_is_number
if %is_number% == "false" (
    cd /d "%tomcat_dir%"
    set _RUNJAVA=
    set _RUNJDB=
    set _EXECJAVA=
    set JSSE_OPTS=
    set JAVA_OPTS=
    set DEBUG_OPTS=
    startup.bat & exit
)

:get_tomcat_pid
for /f "tokens=2 delims=," %%a in ('tasklist /nh /v /fo csv /fi "imagename eq java.exe" /fi "windowtitle eq tomcat"')  do set tomcat_pid=%%a
exit /b

:tomcat_pid_is_number
set regexp="^\"[0-9][0-9]*\"$"
echo %tomcat_pid%|findstr /r /c:"%regexp%">nul  && (
   set is_number="true"
) || (
   set is_number="false"
)
exit /b