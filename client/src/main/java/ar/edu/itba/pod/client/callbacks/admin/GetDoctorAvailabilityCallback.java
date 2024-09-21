package ar.edu.itba.pod.client.callbacks.admin;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.admin.DoctorAvailability;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class GetDoctorAvailabilityCallback extends AbstractFutureCallback<DoctorAvailability> {
    public GetDoctorAvailabilityCallback(Logger logger, CountDownLatch latch) {
        super(logger, latch);
    }

    @Override
    public void onSuccess(DoctorAvailability result) {
        this.getLogger().info("Doctor availability: " + result.getAvailability());
        this.getLatch().countDown();
    }

    @Override
    public void onFailure(Throwable t) {
        this.getLogger().error("Error getting doctor availability: " + t.getMessage());
        this.getLatch().countDown();
    }
}
