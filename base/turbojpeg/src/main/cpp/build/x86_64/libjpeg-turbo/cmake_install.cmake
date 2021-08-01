# Install script for directory: E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/opt/turbojpeg")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Debug")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Install shared libraries without execute permission?
if(NOT DEFINED CMAKE_INSTALL_SO_NO_EXE)
  set(CMAKE_INSTALL_SO_NO_EXE "0")
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "TRUE")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib64" TYPE SHARED_LIBRARY FILES "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/out/x86_64/libturbojpeg.so")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib64/libturbojpeg.so" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib64/libturbojpeg.so")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "D:/softwares/Android/ndk/android-ndk-r16b/toolchains/x86_64-4.9/prebuilt/windows-x86_64/bin/x86_64-linux-android-strip.exe" "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib64/libturbojpeg.so")
    endif()
  endif()
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/bin" TYPE EXECUTABLE FILES "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/tjbench")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/tjbench" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/tjbench")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "D:/softwares/Android/ndk/android-ndk-r16b/toolchains/x86_64-4.9/prebuilt/windows-x86_64/bin/x86_64-linux-android-strip.exe" "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/tjbench")
    endif()
  endif()
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib64" TYPE STATIC_LIBRARY FILES "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/libturbojpeg.a")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/include" TYPE FILE FILES "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/turbojpeg.h")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib64" TYPE STATIC_LIBRARY FILES "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/libjpeg.a")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/bin" TYPE EXECUTABLE FILES "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/rdjpgcom")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/rdjpgcom" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/rdjpgcom")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "D:/softwares/Android/ndk/android-ndk-r16b/toolchains/x86_64-4.9/prebuilt/windows-x86_64/bin/x86_64-linux-android-strip.exe" "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/rdjpgcom")
    endif()
  endif()
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/bin" TYPE EXECUTABLE FILES "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/wrjpgcom")
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/wrjpgcom" AND
     NOT IS_SYMLINK "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/wrjpgcom")
    if(CMAKE_INSTALL_DO_STRIP)
      execute_process(COMMAND "D:/softwares/Android/ndk/android-ndk-r16b/toolchains/x86_64-4.9/prebuilt/windows-x86_64/bin/x86_64-linux-android-strip.exe" "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/bin/wrjpgcom")
    endif()
  endif()
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/doc" TYPE FILE FILES
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/README.ijg"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/README.md"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/example.txt"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/tjexample.c"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/libjpeg.txt"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/structure.txt"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/usage.txt"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/wizard.txt"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/LICENSE.md"
    )
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/man/man1" TYPE FILE FILES
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/cjpeg.1"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/djpeg.1"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/jpegtran.1"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/rdjpgcom.1"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/wrjpgcom.1"
    )
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib64/pkgconfig" TYPE FILE FILES
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/pkgscripts/libjpeg.pc"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/pkgscripts/libturbojpeg.pc"
    )
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/include" TYPE FILE FILES
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/jconfig.h"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/jerror.h"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/jmorecfg.h"
    "E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/libjpeg-turbo/jpeglib.h"
    )
endif()

if(NOT CMAKE_INSTALL_LOCAL_ONLY)
  # Include the install script for each subdirectory.
  include("E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/simd/cmake_install.cmake")
  include("E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/sharedlib/cmake_install.cmake")
  include("E:/workspace/android/project/ALib/turbojpeg/src/main/cpp/build/x86_64/libjpeg-turbo/md5/cmake_install.cmake")

endif()

