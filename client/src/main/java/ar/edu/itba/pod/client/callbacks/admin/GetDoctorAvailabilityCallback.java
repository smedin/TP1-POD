package ar.edu.itba.pod.client.callbacks.admin;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.admin.DoctorAvailability;
import ar.edu.itba.pod.grpc.admin.DoctorAvailabilityResponse;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class GetDoctorAvailabilityCallback extends AbstractFutureCallback<DoctorAvailabilityResponse> {
    public GetDoctorAvailabilityCallback(Logger logger, CountDownLatch latch) {
        super(logger, latch);
    }

    @Override
    public void onSuccess(DoctorAvailabilityResponse result) {
        this.getLogger().info(String.format("Doctor %s (%d) is %s", result.getDoctor().getDoctorName().getName(), result.getDoctor().getLevel(), result.getAvailability()));
        this.getLatch().countDown();
    }
}
