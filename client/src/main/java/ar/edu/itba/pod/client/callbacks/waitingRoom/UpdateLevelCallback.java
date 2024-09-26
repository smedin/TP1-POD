package ar.edu.itba.pod.client.callbacks.waitingRoom;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.waitingRoom.PatientData;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class UpdateLevelCallback extends AbstractFutureCallback<Empty> {
    private final PatientData patientData;

    public UpdateLevelCallback(Logger logger, CountDownLatch latch, PatientData patientData) {
        super(logger, latch);
        this.patientData = patientData;
    }

    @Override
    public void onSuccess(Empty empty) {
        this.getLogger().info("Patient " + this.patientData.getPatientName().getName() + " (" + this.patientData.getLevel() + ") is in the waiting room");
        this.getLatch().countDown();
    }
}
