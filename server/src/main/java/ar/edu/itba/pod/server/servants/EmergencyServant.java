package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.admin.DoctorData;
import ar.edu.itba.pod.grpc.emergency.*;
import ar.edu.itba.pod.grpc.waitingRoom.PatientData;
import ar.edu.itba.pod.server.exceptions.DoctorNotFoundException;
import ar.edu.itba.pod.server.models.*;
import ar.edu.itba.pod.server.utils.Pair;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Optional;

public class EmergencyServant extends EmergencyServiceGrpc.EmergencyServiceImplBase {
    private final Hospital hospital;
    private final NotificationManager notificationManager;

    public EmergencyServant(Hospital hospital, NotificationManager notificationManager) {
        this.hospital = hospital;
        this.notificationManager = notificationManager;
    }

    @Override
    public void startEmergencyByRoom(RoomNumber request, StreamObserver<EndEmergencyData> responseObserver) {
        Room room = hospital.getRoomById(request.getRoomNumber());
        System.out.println("Antes de empezar la emergencia");
        room = hospital.startEmergencyByRoom(room);
        EndEmergencyData emergencyData;

        if (room.isFree()) {
            emergencyData = EndEmergencyData
                    .newBuilder()
                    .setRoomNumber(RoomNumber
                            .newBuilder()
                            .setRoomNumber(room.getId())
                            .build())
                    .build();
        } else {
            Patient nextPatient = room.getPatient();
            Doctor nextDoctor = room.getDoctor();

            emergencyData = EndEmergencyData
                    .newBuilder()
                    .setRoomData(RoomData
                            .newBuilder()
                            .setDoctor(PersonData
                                    .newBuilder()
                                    .setName(nextDoctor.getName())
                                    .setLevel(nextDoctor.getMaxLevel())
                                    .build())
                            .setPatient(PersonData
                                    .newBuilder()
                                    .setName(nextPatient.getName())
                                    .setLevel(nextPatient.getEmergencyLevel())
                                    .build())
                            .build())
                    .setRoomNumber(RoomNumber
                            .newBuilder()
                            .setRoomNumber(room.getId())
                            .build())
                    .build();
        }

        System.out.println("Antes del oncomplete");

        responseObserver.onNext(emergencyData);
        responseObserver.onCompleted();

        System.out.println("Pase el oncomplete");

        notificationManager.notifyEmergencyTaken(room);
    }

    @Override
    public void startAllEmergencies(Empty request, StreamObserver<ListEmergencyData> responseObserver) {
        List<Room> rooms = hospital.startAllEmergencies();

        ListEmergencyData.Builder listEmergencyDataBuilder = ListEmergencyData.newBuilder();

        for (Room room : rooms) {
            EmergencyData emergencyData;
            if (room.isFree()) {
                emergencyData = EmergencyData
                        .newBuilder()
                        .setRoomData(RoomData
                                .newBuilder()
                                .build())
                        .setRoomNumber(RoomOccupation
                                .newBuilder()
                                .setRoomNumber(RoomNumber
                                        .newBuilder()
                                        .setRoomNumber(room.getId())
                                        .build())
                                .setFree(room.isFree())
                                .setNewOccupation(room.isNewOccupation())
                                .build())
                        .build();
            } else {
                Patient nextPatient = room.getPatient();
                Doctor nextDoctor = room.getDoctor();

                emergencyData = EmergencyData
                        .newBuilder()
                        .setRoomData(RoomData
                                .newBuilder()
                                .setDoctor(PersonData
                                        .newBuilder()
                                        .setName(nextDoctor.getName())
                                        .setLevel(nextDoctor.getMaxLevel())
                                        .build())
                                .setPatient(PersonData
                                        .newBuilder()
                                        .setName(nextPatient.getName())
                                        .setLevel(nextPatient.getEmergencyLevel())
                                        .build())
                                .build())
                        .setRoomNumber(RoomOccupation
                                .newBuilder()
                                .setRoomNumber(RoomNumber
                                        .newBuilder()
                                        .setRoomNumber(room.getId())
                                        .build())
                                .setFree(room.isFree())
                                .setNewOccupation(room.isNewOccupation())
                                .build())
                        .build();
            }

            listEmergencyDataBuilder.addEmergencyData(emergencyData);
        }

        hospital.cleanOccupations();

        responseObserver.onNext(listEmergencyDataBuilder.build());
        responseObserver.onCompleted();
    }

        @Override
    public void endEmergency(EndEmergencyData request, StreamObserver<EndEmergencyData> responseObserver) {
        String doctorName = request.getRoomData().getDoctor().getName();
        String patientName = request.getRoomData().getPatient().getName();
        Doctor doctor = hospital.getDoctorByName(doctorName).orElseThrow(() -> new DoctorNotFoundException(doctorName));
        Patient patient = hospital.getPatientByName(patientName);
        int roomN = request.getRoomNumber().getRoomNumber();
        hospital.endEmergency(doctor, roomN);
        EndEmergencyData emergencyData;
        RoomData roomData = RoomData
                .newBuilder()
                .setDoctor(PersonData
                        .newBuilder()
                        .setName(doctorName)
                        .setLevel(doctor.getMaxLevel())
                        .build())
                .setPatient(PersonData
                        .newBuilder()
                        .setName(patientName)
                        .setLevel(patient.getEmergencyLevel())
                        .build())
                .build();
        RoomNumber roomNumber = RoomNumber.newBuilder().setRoomNumber(roomN).build();
        emergencyData = EndEmergencyData
                .newBuilder()
                .setRoomData(roomData)
                .setRoomNumber(roomNumber)
                .build();
    }
}
