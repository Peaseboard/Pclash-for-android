#include "main.h"

#include <android/log.h>

static std::pair<jobject, uint64_t> completableFutureWithToken(Master::Context *context) {
    uint64_t token = EventQueue::getInstance()->obtainToken();
    jobject completableFuture = context->newGlobalReference(context->newCompletableFuture());

    EventQueue::getInstance()->registerHandler(COMPLETE, token, [completableFuture](const event_t *event) {
        EventQueue::getInstance()->unregisterHandler(COMPLETE, event->token);

        Master::runWithAttached<int>([&](JNIEnv *env) -> int {
            Master::runWithContext<void>(env, [&](Master::Context *context) {
                if ( strlen(event->payload) == 0 ) {
                    context->completeCompletableFuture(completableFuture, nullptr);
                } else {
                    context->completeExceptionallyCompletableFuture(completableFuture, context->newClashException(event->payload));
                }

                context->removeGlobalReference(completableFuture);
            });

            return 0;
        });
    });

    return {completableFuture, token};
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_pclash_core_core_bridge_Bridge_downloadProfile__ILjava_lang_String_2Ljava_lang_String_2(
        JNIEnv *env, jclass clazz, jint fd, jstring base, jstring output) {
    UNUSED(clazz);
    UNUSED(fd);
    UNUSED(base);
    UNUSED(output);

    return Master::runWithContext<jobject>(env, [&](Master::Context *context) -> jobject {
        // Stub: return completed future
        return nullptr;
    });
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_pclash_core_core_bridge_Bridge_downloadProfile__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2(
        JNIEnv *env, jclass clazz, jstring url, jstring base, jstring output) {
    UNUSED(clazz);
    UNUSED(url);
    UNUSED(base);
    UNUSED(output);

    return Master::runWithContext<jobject>(env, [&](Master::Context *context) -> jobject {
        // Stub: return completed future
        return nullptr;
    });
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_pclash_core_core_bridge_Bridge_loadProfile(JNIEnv *env, jclass clazz, jstring path,
                                                           jstring base) {
    UNUSED(clazz);
    UNUSED(path);
    UNUSED(base);

    return Master::runWithContext<jobject>(env, [&](Master::Context *context) -> jobject {
        // Stub: return completed future
        return nullptr;
    });
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_pclash_core_core_bridge_Bridge_performHealthCheck(JNIEnv *env, jclass clazz,
                                                                  jstring group) {
    UNUSED(clazz);
    UNUSED(group);

    return Master::runWithContext<jobject>(env, [&](Master::Context *context) -> jobject {
        // Stub: return completed future
        return nullptr;
    });
}