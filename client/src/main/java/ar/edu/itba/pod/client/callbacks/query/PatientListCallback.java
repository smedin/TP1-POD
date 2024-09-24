package ar.edu.itba.pod.client.callbacks.query;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.client.utils.WriterUtils;
import ar.edu.itba.pod.grpc.query.PatientList;
import ar.edu.itba.pod.grpc.query.PersonData;
import ar.edu.itba.pod.grpc.query.RoomData;
import ar.edu.itba.pod.grpc.query.RoomList;
import ar.edu.itba.pod.grpc.waitingRoom.PatientData;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class PatientListCallback extends AbstractFutureCallback<PatientList> {
    private final String filePath;

    public PatientListCallback(Logger logger, CountDownLatch latch, String filePath) {
        super(logger, latch);
        this.filePath = filePath;
    }

    @Override
    public void onSuccess(PatientList patientList) {
        this.getLogger().info("Patient,Level");
        WriterUtils.writeQuery2(filePath, this.getLogger(), patientList);
        this.getLatch().countDown();

    }
}
