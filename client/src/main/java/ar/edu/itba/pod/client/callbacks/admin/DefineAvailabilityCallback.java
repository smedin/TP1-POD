package ar.edu.itba.pod.client.callbacks.admin;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.admin.DoctorAvailability;
import ar.edu.itba.pod.grpc.admin.DoctorData;
import com.google.protobuf.BoolValue;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class DefineAvailabilityCallback extends AbstractFutureCallback<DoctorData> {
    private final DoctorAvailability doctorAvailability;

    public DefineAvailabilityCallback(Logger logger, CountDownLatch latch, DoctorAvailability doctorAvailability) {
        super(logger, latch);
        this.doctorAvailability = doctorAvailability;
    }

    @Override
    public void onSuccess(DoctorData result) {
        String availability = doctorAvailability.getAvailability();
        String availabilityCapitalized = availability.substring(0, 1).toUpperCase() + availability.substring(1).toLowerCase();
        this.getLogger().info("Doctor " + result.getDoctorName().getName() + " (" + result.getLevel() + ") is " + availabilityCapitalized);
        this.getLatch().countDown();
    }
}
