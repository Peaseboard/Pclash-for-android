package com.pclash.service.transact;

import com.pclash.service.transact.ParcelableContainer;

interface IStreamCallback {
    void send(in ParcelableContainer data);
    void complete();
    void completeExceptionally(String reason);
}
