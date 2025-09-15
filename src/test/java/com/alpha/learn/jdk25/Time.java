/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@SuppressWarnings("ALL")
@Slf4j
final class ForTesting {
    @Test
    public void test() {
        Time time = Time.SYSTEM;
        log.info("time is {}", time);
        log.info("time is {}", time.milliseconds());
    }
}

public interface Time {

    Time SYSTEM = new SystemTime();

    long milliseconds();

    default long hiResClockMs() {
        return TimeUnit.NANOSECONDS.toMillis(nanoseconds());
    }
    long nanoseconds();

    void sleep(long ms);
    void waitObject(Object obj, Supplier<Boolean> condition, long deadlineMs) throws InterruptedException;

    default <T> T waitForFuture(
        CompletableFuture<T> future,
        long deadlineNs
    ) throws TimeoutException, InterruptedException, ExecutionException  {
        TimeoutException timeoutException = null;
        while (true) {
            long nowNs = nanoseconds();
            if (deadlineNs <= nowNs) {
                throw (timeoutException == null) ? new TimeoutException() : timeoutException;
            }
            long deltaNs = deadlineNs - nowNs;
            try {
                return future.get(deltaNs, TimeUnit.NANOSECONDS);
            } catch (TimeoutException t) {
                timeoutException = t;
            }
        }
    }

    static class SystemTime implements Time {

        @Override
        public long milliseconds() {
            return System.currentTimeMillis();
        }

        @Override
        public long nanoseconds() {
            return System.nanoTime();
        }

        @Override
        public void sleep(long ms) {

        }

        @Override
        public void waitObject(Object obj, Supplier<Boolean> condition, long deadlineMs) throws InterruptedException {
            synchronized (this) {
                while (true) {
                    if (condition.get())
                        return;

                    long currentTimeMs = milliseconds();
                    if (currentTimeMs >= deadlineMs)
                        throw new org.apache.kafka.common.errors.TimeoutException("Condition not satisfied before deadline");

                    obj.wait(deadlineMs - currentTimeMs);
                }
            }
        }
    }
}
