#ifndef LIBCLASH_H
#define LIBCLASH_H

#include "buffer.h"
#include "config.h"
#include "event.h"
#include "general.h"
#include "proxies.h"
#include "traffic.h"

#ifdef __cplusplus
extern "C" {
#endif

void initialize(const_buffer_t *buf, const char *home, const char *version);
void reset();
int setProxyMode(const char *mode);
int setDnsOverride(const char *dns);
int setSelector(const char *group, const char *selector);
void enableLogReport();
void disableLogReport();
int queryGeneral(general_t *g);
int queryBandwidth(traffic_t *t);
int querySpeed(traffic_t *t);
proxy_group_list_t *queryProxyGroups();
char *startTunDevice(int fd, int mtu, const char *gateway, const char *mirror, const char *dns, const char *token);
void stopTunDevice();
int downloadProfileFromFd(int fd, int b, void *ctx);
int downloadProfileFromUrl(const char *url, int b, int o, void *ctx);
int loadProfile(const char *path, int b, void *ctx);
int performHealthCheck(const char *group, void *ctx);

#ifdef __cplusplus
}
#endif

#endif // LIBCLASH_H
