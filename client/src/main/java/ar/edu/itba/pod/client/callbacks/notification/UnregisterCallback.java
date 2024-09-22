package ar.edu.itba.pod.client.callbacks.notification;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.notifications.Notification;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class UnregisterCallback extends AbstractFutureCallback<Notification> {

    public UnregisterCallback(Logger logger, CountDownLatch latch) {
        super(logger, latch);
    }

    @Override
    public void onSuccess(Notification notification) {
        getLogger().info(notification.getMessage());
        getLatch().countDown();
    }
}
