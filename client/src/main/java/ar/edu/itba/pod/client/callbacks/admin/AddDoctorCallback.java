package ar.edu.itba.pod.client.callbacks.admin;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.admin.DoctorData;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class AddDoctorCallback extends AbstractFutureCallback<Empty> {
    private final DoctorData doctorData;

    public AddDoctorCallback(Logger logger, CountDownLatch latch, DoctorData doctorData) {
        super(logger, latch);
        this.doctorData = doctorData;
    }

    @Override
    public void onSuccess(Empty empty) {
        this.getLogger().info(String.format("Doctor %s (%d) added successfully", doctorData.getDoctorName().getName(), doctorData.getLevel()));
        this.getLatch().countDown();
    }
}
