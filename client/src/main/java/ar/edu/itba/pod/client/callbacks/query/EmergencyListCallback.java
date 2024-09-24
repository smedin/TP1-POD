package ar.edu.itba.pod.client.callbacks.query;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.client.utils.WriterUtils;
import ar.edu.itba.pod.grpc.query.EmergencyList;
import ar.edu.itba.pod.grpc.query.RoomList;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class EmergencyListCallback extends AbstractFutureCallback<EmergencyList> {
    private final String filePath;

    public EmergencyListCallback(Logger logger, CountDownLatch latch, String filePath) {
        super(logger, latch);
        this.filePath = filePath;
    }

    @Override
    public void onSuccess(EmergencyList emergencyList) {
        WriterUtils.writeQuery3(filePath, this.getLogger(), emergencyList);
        this.getLatch().countDown();

    }
}
