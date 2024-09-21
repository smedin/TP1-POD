package ar.edu.itba.pod.client.callbacks.admin;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.admin.DoctorData;
import com.google.protobuf.BoolValue;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class AddDoctorCallback extends AbstractFutureCallback<BoolValue> {
    private final DoctorData doctorData;

    public AddDoctorCallback(Logger logger, CountDownLatch latch, DoctorData doctorData) {
        super(logger, latch);
        this.doctorData = doctorData;
    }

    @Override
    public void onSuccess(BoolValue result) {
        this.getLogger().info(doctorData.getDoctorName().getName() + " (" + doctorData.getLevel() + ") added successfully");
        this.getLatch().countDown();
    }
}
