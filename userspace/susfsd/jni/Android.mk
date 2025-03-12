# Modification of KernelSU-Next build susfs using rifsxd
# Building with the susfs filesystem
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := zakomksd
LOCAL_SRC_FILES := susfsd.c
include $(BUILD_EXECUTABLE)
