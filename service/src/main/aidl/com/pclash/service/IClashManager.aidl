package com.pclash.service;

import com.pclash.service.transact.IStreamCallback;

interface IClashManager {
    // Control
    void setSelector(String group, String selected);
    void performHealthCheck(String group, IStreamCallback callback);
    void setProxyMode(String mode);

    // Query
    ProxyGroupWrapper queryProxyGroups();
    General queryGeneral();
    long queryBandwidth();

    // Events
    void registerLogListener(String key, IStreamCallback callback);
    void unregisterLogListener(String key);
}
