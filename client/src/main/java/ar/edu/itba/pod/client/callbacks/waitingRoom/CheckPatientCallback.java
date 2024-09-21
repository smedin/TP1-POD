package ar.edu.itba.pod.client.callbacks.waitingRoom;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.waitingRoom.TimeData;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class CheckPatientCallback extends AbstractFutureCallback<TimeData> {

    public CheckPatientCallback(Logger logger, CountDownLatch latch) {
        super(logger, latch);
    }

    @Override
    public void onSuccess(TimeData response) {
        this.getLogger().info("Patient " + response.getPatient().getPatientName().getName() + " (" + response.getPatient().getLevel() + ") is in the waiting room with " + response.getWaitingTime() + " patients ahead");
        this.getLatch().countDown();
    }

    @Override
    public void onFailure(Throwable t) {
        this.getLogger().error("Error updating patient level", t);
        this.getLatch().countDown();
    }
}
