@echo off
if not exist out (
  echo Run build.bat first.
  exit /b 1
)
"%JAVA_HOME%\bin\java.exe" -Dfile.encoding=UTF-8 -cp out edu.scvtc.scholarship.Main
