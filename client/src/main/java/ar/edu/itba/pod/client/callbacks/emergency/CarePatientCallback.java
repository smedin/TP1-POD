package ar.edu.itba.pod.client.callbacks.emergency;

import ar.edu.itba.pod.client.callbacks.AbstractFutureCallback;
import ar.edu.itba.pod.grpc.emergency.EndEmergencyData;
import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;

public class CarePatientCallback extends AbstractFutureCallback<EndEmergencyData> {
    public CarePatientCallback(Logger logger, CountDownLatch latch) {
        super(logger, latch);
    }

    @Override
    public void onSuccess(EndEmergencyData endEmergencyData) {

        if (endEmergencyData.getRoomData().getPatient().getName().isEmpty()) {
            this.getLogger().info("Room #{} remains free", endEmergencyData.getRoomNumber().getRoomNumber());
            this.getLatch().countDown();
        } else {
            this.getLogger().info("Patient {} ({}) and Doctor {} ({}) are now in Room #{}", endEmergencyData.getRoomData().getPatient().getName(), endEmergencyData.getRoomData().getPatient().getLevel(), endEmergencyData.getRoomData().getDoctor().getName(), endEmergencyData.getRoomData().getDoctor().getLevel(), endEmergencyData.getRoomNumber().getRoomNumber());
            this.getLatch().countDown();
        }
    }
}
