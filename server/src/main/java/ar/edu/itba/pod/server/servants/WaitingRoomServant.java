package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.waitingRoom.Patient;
import ar.edu.itba.pod.grpc.waitingRoom.PatientData;
import ar.edu.itba.pod.grpc.waitingRoom.WaitingRoomServiceGrpc;
import ar.edu.itba.pod.grpc.waitingRoom.TimeData;
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
    public void registerPatient(PatientData request, StreamObserver<BoolValue> response) {
        String patientName = request.getPatientName().getName();
        int emergencyLevel = request.getLevel();

        boolean registered = hospital.registerPatient(patientName, emergencyLevel);

        response.onNext(BoolValue.newBuilder().setValue(registered).build());
        response.onCompleted();
    }

    @Override
    public void updateEmergencyLevel(PatientData request, StreamObserver<BoolValue> response) {
        String patientName = request.getPatientName().getName();
        int emergencyLevel = request.getLevel();

        boolean updated = hospital.updateEmergencyLevel(patientName, emergencyLevel);

        response.onNext(BoolValue.newBuilder().setValue(updated).build());
        response.onCompleted();
    }

    @Override
    public void waitingTime(Patient request, StreamObserver<TimeData> response) {
        String patientName = request.getName();

        int waitingTime = hospital.getWaitingTime(patientName);

        ar.edu.itba.pod.server.models.Patient patient = hospital.getPatientByName(patientName);

        TimeData timeData = TimeData.newBuilder()
                .setWaitingTime(waitingTime)
                .setPatient(PatientData
                        .newBuilder()
                        .setPatientName(Patient.newBuilder().setName(patient.getName()))
                        .setLevel(patient.getEmergencyLevel())
                        .build())
                .build();

        response.onNext(timeData);
        response.onCompleted();
    }

}
