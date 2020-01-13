LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)  
LOCAL_MODULE := libjpeg-turbo
LOCAL_SRC_FILES := libs/libturbojpeg.so
include $(PREBUILT_SHARED_LIBRARY)
