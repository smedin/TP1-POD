package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.query.*;
import ar.edu.itba.pod.server.models.Hospital;
import ar.edu.itba.pod.server.models.Patient;
import ar.edu.itba.pod.server.models.Room;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {
    private final Hospital hospital;

    public QueryServant(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    public void getRooms(Empty request, StreamObserver<RoomList> responseObserver) {
        List<Room> rooms = hospital.getRooms();

        RoomList.Builder roomListBuilder = RoomList.newBuilder();
        for (Room room : rooms) {
            RoomData roomData;
            if (room.isFree()) {
                roomData = RoomData.newBuilder()
                        .setRoomNumber(room.getId())
                        .setFree(true)
                        .build();
                roomListBuilder.addRoomData(roomData);
            } else {
                roomData = RoomData.newBuilder()
                        .setRoomNumber(room.getId())
                        .setFree(false)
                        .setDoctor(PersonData.newBuilder().setName(room.getDoctor().getName()).setLevel(room.getDoctor().getMaxLevel()).build())
                        .setPatient(PersonData.newBuilder().setName(room.getPatient().getName()).setLevel(room.getPatient().getEmergencyLevel()).build())
                        .build();
                roomListBuilder.addRoomData(roomData);
            }
        }
        responseObserver.onNext(roomListBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getWaitingRoom(Empty request, StreamObserver<PatientList> responseObserver) {
        List<Patient> patients = hospital.getWaitingPatients();

        PatientList.Builder patientListBuilder = PatientList.newBuilder();
        for (Patient patient : patients) {
            PersonData personData = PersonData.newBuilder()
                    .setName(patient.getName())
                    .setLevel(patient.getEmergencyLevel())
                    .build();
            patientListBuilder.addPatientData(personData);
        }
        responseObserver.onNext(patientListBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getFinalizedEmergencies(RoomNumber request, StreamObserver<FinalizedEmergencyList> responseObserver) {

    }
}
