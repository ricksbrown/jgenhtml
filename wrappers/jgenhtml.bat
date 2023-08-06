@ECHO OFF
REM This can be used to run java jgenhtml as a command line utility on Windows systems.
REM Download the jgenhtml jar.
REM Put this wrapper script in the same directory.
REM Make this wrapper script executable: chmod +x jgenhtml
REM Ensure the directory that contains this wrapper script is in your Path.

for /f %%i in ('dir %~dp0\jgenhtml*.jar /b/a-d/od/t:c') do set LATEST=%%i
java -jar %~dp0\%LATEST% %*
