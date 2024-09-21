package ar.edu.itba.pod.client.callbacks.admin;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import com.google.protobuf.BoolValue;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class DefineAvailabilityCallback extends AbstractFutureCallback<BoolValue> {
    public DefineAvailabilityCallback(Logger logger, CountDownLatch latch) {
        super(logger, latch);
    }

    @Override
    public void onSuccess(BoolValue result) {
        this.getLogger().info("Availability defined: " + result.getValue());
        this.getLatch().countDown();
    }

    @Override
    public void onFailure(Throwable t) {
        this.getLogger().error("Error defining availability: " + t.getMessage());
        this.getLatch().countDown();
    }
}
