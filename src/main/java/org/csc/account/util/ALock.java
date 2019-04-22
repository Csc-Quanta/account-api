package org.csc.account.util;

import java.util.concurrent.locks.Lock;

public final class ALock implements AutoCloseable {
    private final Lock lock;

    public ALock(Lock l) {
        this.lock = l;
    }

    public final ALock lock() {
        this.lock.lock();
        return this;
    }

    @Override
    public final void close() {
        this.lock.unlock();
    }
}
