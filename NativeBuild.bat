call "D:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"
call .\gradlew clean
call .\gradlew nativeBuild
call .\rcedit-x64.exe .\build\native\nativeCompile\TyuShare.exe --set-icon .\favicon-128.ico
EDITBIN /SUBSYSTEM:WINDOWS .\build\native\nativeCompile\TyuShare.exe
mkdir .\build\native\nativeCompile\bin
copy .\build\native\nativeCompile\*.dll .\build\native\nativeCompile\bin
copy .\bin\windows\audio-exporter.exe .\build\native\nativeCompile\bin
call 7z a -tzip .\build\native\nativeCompile\TyuShare_Native_Windows.zip .\build\native\nativeCompile\*
echo "Build Done"
pause