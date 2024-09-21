package ar.edu.itba.pod.client.callbacks.emergency;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.emergency.EmergencyData;
import ar.edu.itba.pod.grpc.emergency.ListEmergencyData;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class CareAllPatientsCallback extends AbstractFutureCallback<ListEmergencyData> {
    public CareAllPatientsCallback(Logger logger, CountDownLatch latch) {
        super(logger, latch);
    }

    @Override
    public void onSuccess(ListEmergencyData listEmergencyData) {
        for (EmergencyData emergencyData : listEmergencyData.getEmergencyDataList()) {
            if (!emergencyData.getRoomData().isInitialized()) {
                this.getLogger().info("Room #{} remains {}",
                        emergencyData.getRoomNumber().getRoomNumber().getRoomNumber(),
                        emergencyData.getRoomNumber().getFree() ? "Free" : "Occupied");
            } else {
                this.getLogger().info("Patient {} ({}) and Doctor {} ({}) are in room #{}",
                        emergencyData.getRoomData().getPatient().getName(),
                        emergencyData.getRoomData().getPatient().getLevel(),
                        emergencyData.getRoomData().getDoctor().getName(),
                        emergencyData.getRoomData().getDoctor().getLevel(),
                        emergencyData.getRoomNumber().getRoomNumber().getRoomNumber());
            }
        }
    }
}
