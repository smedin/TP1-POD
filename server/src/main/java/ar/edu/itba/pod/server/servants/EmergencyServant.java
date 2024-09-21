package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.admin.DoctorData;
import ar.edu.itba.pod.grpc.emergency.*;
import ar.edu.itba.pod.grpc.waitingRoom.PatientData;
import ar.edu.itba.pod.server.models.Doctor;
import ar.edu.itba.pod.server.models.Hospital;
import ar.edu.itba.pod.server.models.Patient;
import ar.edu.itba.pod.server.models.Room;
import ar.edu.itba.pod.server.utils.Pair;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.util.Optional;

public class EmergencyServant extends EmergencyServiceGrpc.EmergencyServiceImplBase {
    private final Hospital hospital;

    public EmergencyServant(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    public void startEmergencyByRoom(RoomNumber request, StreamObserver<EndEmergencyData> responseObserver) {
        System.out.println("Before startEmergencyByRoom");
        Room room = hospital.getRoomById(request.getRoomNumber());
        System.out.println("After startEmergencyByRoom");
        Optional<Pair<Patient, Doctor>> nextPatientDoctor = hospital.startEmergencyByRoom(room);


        EndEmergencyData emergencyData = EndEmergencyData
                .newBuilder()
                .setRoomData(RoomData
                        .newBuilder()
                        .setDoctor(PersonData
                                .newBuilder()
                                .setName(nextPatientDoctor.get().getRight().getName())
                                .setLevel(nextPatientDoctor.get().getRight().getMaxLevel())
                                .build())
                        .setPatient(PersonData
                                .newBuilder()
                                .setName(nextPatientDoctor.get().getLeft().getName())
                                .setLevel(nextPatientDoctor.get().getLeft().getEmergencyLevel())
                                .build())
                        .build())
                .setRoomNumber(RoomNumber
                        .newBuilder()
                        .setRoomNumber(room.getId())
                        .build())
                .build();

        responseObserver.onNext(emergencyData);
        responseObserver.onCompleted();
    }

    @Override
    public void startAllEmergencies(Empty request, StreamObserver<ListEmergencyData> responseObserver) {

    }

    @Override
    public void endEmergency(EndEmergencyData request, StreamObserver<BoolValue> responseObserver) {

    }
}
