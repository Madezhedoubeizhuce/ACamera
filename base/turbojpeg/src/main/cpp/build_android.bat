echo off

set CMAKE_HOME=D:\softwares\Android\sdk\cmake\3.10.2.4988404
set NDK_HOME=D:\softwares\Android\ndk\android-ndk-r16b

set BUILD_TYPE=Debug
set API_LEVEL=9

set abi_list[0]=armeabi-v7a
set abi_list[1]=arm64-v8a
set abi_list[2]=x86
set abi_list[3]=x86_64

set BASE_DIR=%CD%
set BUILD_DIR=build
set OUTPUT_DIR=out

if "%1"=="clean" goto clean

set "index=0"

:Loop_Start

if not defined abi_list[%index%] goto end
call set ABI=%%abi_list[%index%]%%

set ABI_BUILD_PATH=%BASE_DIR%\%BUILD_DIR%\%ABI%
set INSTALL_PATH=%BASE_DIR%\%OUTPUT_DIR%\%ABI%

if not exist %ABI_BUILD_PATH% md %ABI_BUILD_PATH%
if not exist %INSTALL_PATH% md %INSTALL_PATH%

cd %ABI_BUILD_PATH%

%CMAKE_HOME%\bin\cmake ^
  -H%BASE_DIR% ^
  -DCMAKE_LIBRARY_OUTPUT_DIRECTORY=%INSTALL_PATH% ^
  -DANDROID_ABI=%ABI% ^
  -DANDROID_NDK=%NDK_HOME% ^
  -DCMAKE_BUILD_TYPE=%BUILD_TYPE% ^
  -DCMAKE_TOOLCHAIN_FILE=%NDK_HOME%\build\cmake\android.toolchain.cmake ^
  -DANDROID_NATIVE_API_LEVEL=%API_LEVEL% ^
  -DANDROID_TOOLCHAIN=clang -DCMAKE_GENERATOR="Ninja" ^
  -DCMAKE_MAKE_PROGRAM=%CMAKE_HOME%\bin\ninja ^
  -B%ABI_BUILD_PATH% ^
  -GNinja

%CMAKE_HOME%\bin\cmake --build %ABI_BUILD_PATH%

cd %BASE_DIR%

set /a "index+=1"
goto Loop_Start

:clean
if exist %BUILD_DIR% rmdir /s /q %BUILD_DIR%
if exist %OUTPUT_DIR% rmdir /s /q %OUTPUT_DIR%

:end