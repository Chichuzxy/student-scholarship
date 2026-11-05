@echo off
if "%JAVA_HOME%"=="" (
  echo Please set JAVA_HOME to a JDK 8+ directory first.
  exit /b 1
)
if not exist out mkdir out
"%JAVA_HOME%\bin\javac.exe" -encoding UTF-8 -d out src\edu\scvtc\scholarship\*.java
if errorlevel 1 exit /b 1
echo Compiled to out\
