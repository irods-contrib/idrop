@echo off
setlocal

docker run -it --rm --name idrop_builder ^
    -v "%CD%/local_maven_repo:/root/.m2" ^
    -v "%CD%:/idrop" ^
    -w /idrop ^
    maven:3.8.4-openjdk-17 ^
    mvn -Dmaven.test.skip=true clean install

if errorlevel 1 exit /b %errorlevel%

endlocal
