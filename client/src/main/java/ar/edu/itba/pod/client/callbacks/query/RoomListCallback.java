package ar.edu.itba.pod.client.callbacks.query;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.client.utils.WriterUtils;
import ar.edu.itba.pod.grpc.query.RoomData;
import ar.edu.itba.pod.grpc.query.RoomList;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class RoomListCallback extends AbstractFutureCallback<RoomList> {
    private final String filePath;

    public RoomListCallback(Logger logger, CountDownLatch latch, String filePath) {
        super(logger, latch);
        this.filePath = filePath;
    }

    @Override
    public void onSuccess(RoomList roomList) {
        WriterUtils.writeQuery1(filePath, this.getLogger(), roomList);
        this.getLatch().countDown();

    }
}
