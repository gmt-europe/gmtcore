package nl.gmt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sun.management.ThreadInfoCompositeData;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class DelegateFixture {
    @Test
    public void multiThreadedDelegateTest() throws Exception {
        final int threadCount = 20;
        final int iterationCount = 200000;
        final AtomicInteger callCount = new AtomicInteger();
        final AtomicInteger totalCallCount = new AtomicInteger();
        final Delegate<Integer> delegate = new Delegate<>();

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;

            threads[threadIndex] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < iterationCount; j++) {
                        DelegateListener<Integer> listener = new DelegateListener<Integer>() {
                            @Override
                            public void call(Object sender, Integer arg) {
                                totalCallCount.incrementAndGet();

                                if (arg == threadIndex) {
                                    callCount.incrementAndGet();
                                }
                            }
                        };

                        delegate.add(listener);
                        delegate.call(null, threadIndex);
                        delegate.remove(listener);
                    }
                }
            });

            threads[threadIndex].start();
        }

        for (int i = 0; i < threadCount; i++) {
            threads[i].join();
        }

        assertTrue(delegate.isEmpty());
        assertEquals(threadCount * iterationCount, callCount.get());

        System.out.println(String.format("Call count: %d, total call count: %d", callCount.get(), totalCallCount.get()));
    }
}
