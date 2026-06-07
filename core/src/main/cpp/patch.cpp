#include "main.h"

#include <cstring>

extern "C"
JNIEXPORT void JNICALL
Java_com_pclash_core_core_bridge_Bridge_setProxyMode(JNIEnv *env, jclass clazz,
                                                            jstring proxy_mode) {
    Master::runWithContext<void>(env, [&](Master::Context *context) {
        const char *m = context->getString(proxy_mode);

        setProxyMode(m);
    });
}

extern "C"
JNIEXPORT void JNICALL
Java_com_pclash_core_core_bridge_Bridge_setDnsOverride(JNIEnv *env, jclass clazz,
                                                              jboolean override_dns,
                                                              jstring append_dns) {
    Master::runWithContext<void>(env, [&](Master::Context *context) {
        const char *appendDns = context->getString(append_dns);
        int override = override_dns ? 1 : 0;

        // Build JSON string for Go function
        std::string dnsJson = "{\"override_dns\":" + std::to_string(override) + ",\"append_dns\":\"" + std::string(appendDns) + "\"}";

        setDnsOverride(dnsJson.c_str());

        context->releaseString(append_dns, appendDns);
    });
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_pclash_core_core_bridge_Bridge_setSelector(JNIEnv *env, jclass clazz, jstring group,
                                                           jstring selected) {
    UNUSED(clazz);

    return Master::runWithContext<bool>(env, [&](Master::Context *context) -> bool {
        const char *g = context->getString(group);
        const char *s = context->getString(selected);

        int r = setSelector(g, s);

        context->releaseString(group, g);
        context->releaseString(selected, s);

        return r == 0;
    });
}