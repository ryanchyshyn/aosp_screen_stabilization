LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := android-common

LOCAL_SRC_FILES := $(call all-java-files-under, src) $(call all-renderscript-files-under, src) $(call all-Iaidl-files-under, src)
LOCAL_AIDL_INCLUDES := $(call all-Iaidl-files-under, src)

LOCAL_PACKAGE_NAME := ScreenStabilization
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res

LOCAL_AAPT_FLAGS := --auto-add-overlay

include $(BUILD_PACKAGE)
include $(CLEAR_VARS)


include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))
