package ar.edu.itba.pod.client.callbacks.admin;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import com.google.protobuf.Int32Value;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class AddRoomCallback extends AbstractFutureCallback<Int32Value> {

    public AddRoomCallback(Logger logger, CountDownLatch latch) {
        super(logger, latch);
    }

    @Override
    public void onSuccess(Int32Value result) {
        this.getLogger().info(String.format("Room #%d added successfully", result.getValue()));
        this.getLatch().countDown();
    }
}
