package ar.edu.itba.pod.client.callbacks.emergency;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.emergency.EndEmergencyData;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class DischargeEmergencyCallback extends AbstractFutureCallback<EndEmergencyData> {
    public DischargeEmergencyCallback(Logger logger, CountDownLatch latch) {
        super(logger, latch);
    }

    @Override
    public void onSuccess(EndEmergencyData endEmergencyData) {
        this.getLogger().info(String.format("Patient %s (%d) has been discharged from Doctor %s (%d) and the Room #%d is now Free", endEmergencyData.getRoomData().getPatient().getName(), endEmergencyData.getRoomData().getPatient().getLevel(), endEmergencyData.getRoomData().getDoctor().getName(), endEmergencyData.getRoomData().getDoctor().getLevel(), endEmergencyData.getRoomNumber().getRoomNumber()));
        this.getLatch().countDown();
    }
}
