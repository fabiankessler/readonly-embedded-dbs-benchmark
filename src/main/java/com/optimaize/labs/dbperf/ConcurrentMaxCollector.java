package com.optimaize.labs.dbperf;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Keeps track of the largest number, thread-safe, without using expensive synchronization.
 *
 * @author Fabian Kessler
 */
public class ConcurrentMaxCollector {

    private final AtomicLong atomicLong;

    public static ConcurrentMaxCollector create(long defaultValue) {
        return new ConcurrentMaxCollector(defaultValue);
    }

    private ConcurrentMaxCollector(long defaultValue) {
        this.atomicLong = new AtomicLong(defaultValue);
    }

    /**
     * This method can be used from multiple threads without synchronization.
     * @param offered
     */
    public void offer(long offered) {
        long current = atomicLong.get();
        if (offered > current) {
            while (!atomicLong.compareAndSet(current, offered)) {
                current = atomicLong.get();
                if (!(offered > current)) {
                    break;
                }
            }
        }
    }

    /**
     * If this method is used while others are still possibly {@link #offer}ing then you may want to externally
     * synchronize this call, if that timing precision is important to you.
     *
     * @return 0-n
     */
    public long getBest() {
        return atomicLong.get();
    }

}
