LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

MY_CPP_LIST := $(wildcard $(LOCAL_PATH)/*.c)
MY_CPP_INCLUDES := $(LOCAL_PATH)/include

common_shared_libraries := 
common_shared_libraries += libjpeg-turbo

LOCAL_MODULE := tuobojpeg-jni
LOCAL_SRC_FILES:= $(MY_CPP_LIST:$(LOCAL_PATH)/%=%)
LOCAL_C_INCLUDES := $(MY_CPP_INCLUDES)
LOCAL_SHARED_LIBRARIES := $(common_shared_libraries)

LOCAL_LDLIBS += -llog -lz

include $(BUILD_SHARED_LIBRARY)
