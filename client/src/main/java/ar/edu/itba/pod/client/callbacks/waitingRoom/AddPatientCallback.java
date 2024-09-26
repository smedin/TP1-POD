package ar.edu.itba.pod.client.callbacks.waitingRoom;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.waitingRoom.PatientData;
import ar.edu.itba.pod.grpc.waitingRoom.TimeData;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class AddPatientCallback extends AbstractFutureCallback<Empty> {
    private PatientData patientData;

    public AddPatientCallback(Logger logger, CountDownLatch latch, PatientData patientData) {
        super(logger, latch);
        this.patientData = patientData;
    }

    @Override
    public void onSuccess(Empty empty) {
        this.getLogger().info(String.format("Patient %s (%d) is in the waiting room", patientData.getPatientName().getName(), patientData.getLevel()));
        this.getLatch().countDown();
    }
}
