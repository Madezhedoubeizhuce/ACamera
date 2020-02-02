LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)  
LOCAL_MODULE := libjpeg-turbo

MY_SRC_FILES :=
ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
  MY_SRC_FILES += $(LOCAL_PATH)/libs/ARMv7/libturbojpeg.so
endif
ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
  MY_SRC_FILES += $(LOCAL_PATH)/libs/ARMv8/libturbojpeg.so
endif
ifeq ($(TARGET_ARCH_ABI),x86)
  MY_SRC_FILES += $(LOCAL_PATH)/libs/x86/libturbojpeg.so
endif
ifeq ($(TARGET_ARCH_ABI),x86_64)
  MY_SRC_FILES += $(LOCAL_PATH)/libs/x86_64/libturbojpeg.so
endif

LOCAL_SRC_FILES := $(MY_SRC_FILES)

include $(PREBUILT_SHARED_LIBRARY)
