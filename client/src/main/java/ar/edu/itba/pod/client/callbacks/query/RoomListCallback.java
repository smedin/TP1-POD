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
        this.getLogger().info("Room,Status,Patient,Doctor");
        for (RoomData roomData : roomList.getRoomDataList()) {
            this.getLogger().info("{},{},{}{},{}{}",
                    roomData.getRoomNumber(),
                    roomData.getFree() ? "Free" : "Occupied",
                    roomData.getPatient().getName(),
                    roomData.getPatient().getLevel() == 0 ? "" : " (" + roomData.getPatient().getLevel() + ")",
                    roomData.getDoctor().getName(),
                    roomData.getDoctor().getLevel() == 0 ? "" : " (" + roomData.getDoctor().getLevel() + ")");
        }
        WriterUtils.writeQuery1(filePath, roomList);
        this.getLatch().countDown();

    }
}
