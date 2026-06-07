// Stub implementations for missing libclash functions
// These are required for linking but the actual implementation
// depends on libmihomo.so having these symbols exported

#include <cstdint>
#include <cstring>
#include <cstdlib>

// Stub implementations
extern "C" {

void initialize(const void *buf, const char *home, const char *version) {
    // Stub - no-op
}

void reset() {
    // Stub - no-op
}

int setProxyMode(const char *mode) {
    // Stub - return success
    return 0;
}

int setDnsOverride(const char *dns) {
    // Stub - return success
    return 0;
}

int setSelector(const char *group, const char *selector) {
    // Stub - return success
    return 0;
}

void enableLogReport() {
    // Stub - no-op
}

void disableLogReport() {
    // Stub - no-op
}

int queryGeneral(void *g) {
    // Stub - return 0 (success)
    return 0;
}

int queryBandwidth(void *t) {
    // Stub - return 0 (success)
    return 0;
}

int querySpeed(void *t) {
    // Stub - return 0 (success)
    return 0;
}

void* queryProxyGroups() {
    // Stub - return nullptr
    return nullptr;
}

char* startTunDevice(int fd, int mtu, const char *gateway, const char *mirror, const char *dns, const char *token) {
    // Stub - return nullptr (no error)
    return nullptr;
}

void stopTunDevice() {
    // Stub - no-op
}

int downloadProfileFromFd(int fd, void *ctx) {
    // Stub - return 0 (success)
    return 0;
}

int downloadProfileFromUrl(const char *url, void *ctx) {
    // Stub - return 0 (success)
    return 0;
}

int loadProfile(const char *path, void *ctx) {
    // Stub - return 0 (success)
    return 0;
}

int performHealthCheck(const char *group, void *ctx) {
    // Stub - return 0 (success)
    return 0;
}

void set_event_handler(void (*handler)(const void*)) {
    // Stub - no-op
}

void answer_event(void *event) {
    // Stub - no-op
}

} // extern "C"