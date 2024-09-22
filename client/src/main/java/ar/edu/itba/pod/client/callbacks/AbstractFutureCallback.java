package ar.edu.itba.pod.client.callbacks;

import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractFutureCallback<V> implements FutureCallback<V> {
    private final Logger logger;
    private final CountDownLatch latch;

    public AbstractFutureCallback(Logger logger, CountDownLatch latch) {
        this.logger = logger;
        this.latch = latch;
    }

    @Override
    public void onFailure(Throwable t) {
        System.out.println(t.getMessage());
        latch.countDown();
    }

    public Logger getLogger() {
        return logger;
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
