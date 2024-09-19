package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.waitingRoom.Patient;
import ar.edu.itba.pod.grpc.waitingRoom.PatientData;
import ar.edu.itba.pod.grpc.waitingRoom.WaitingRoomServiceGrpc;
import ar.edu.itba.pod.server.models.Hospital;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Int32Value;
import io.grpc.stub.StreamObserver;

public class WaitingRoomServant extends WaitingRoomServiceGrpc.WaitingRoomServiceImplBase {
    private final Hospital hospital;

    public WaitingRoomServant(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    public void RegisterPatient(PatientData request, StreamObserver<BoolValue> response) {
        String patientName = request.getPatientName().getName();
        int emergencyLevel = Integer.parseInt(request.getLevel());

        boolean registered = hospital.registerPatient(patientName, emergencyLevel);

        response.onNext(BoolValue.newBuilder().setValue(registered).build());
        response.onCompleted();
    }

    @Override
    public void UpdateEmergencyLevel(PatientData request, StreamObserver<BoolValue> response) {
        String patientName = request.getPatientName().getName();
        int emergencyLevel = Integer.parseInt(request.getLevel());

        boolean updated = hospital.updateEmergencyLevel(patientName, emergencyLevel);

        response.onNext(BoolValue.newBuilder().setValue(updated).build());
        response.onCompleted();
    }

    @Override
    public void WaitingTime(Patient request, StreamObserver<Int32Value> response) {
        String patientName = request.getName();

        int waitingTime = hospital.getWaitingTime(patientName);

        response.onNext(Int32Value.newBuilder().setValue(waitingTime).build());
        response.onCompleted();
    }

}
