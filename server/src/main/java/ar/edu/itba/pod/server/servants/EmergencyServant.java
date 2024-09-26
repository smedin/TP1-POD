package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.emergency.*;
import ar.edu.itba.pod.server.exceptions.PatientNotFoundException;
import ar.edu.itba.pod.server.exceptions.RoomNotFoundException;
import ar.edu.itba.pod.server.models.*;
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

        hospital.cleanOccupations();

        responseObserver.onNext(emergencyData);
        responseObserver.onCompleted();
        //Control flow gets to this point only of no exception was thrown.
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
                if(room.isNewOccupation()) {
                    notificationManager.notifyEmergencyTaken(room);
                }
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
        int roomN = request.getRoomNumber().getRoomNumber();
        Room room = hospital.getRooms().stream().filter(r -> r.getId() == roomN).findFirst().orElseThrow(() -> new RoomNotFoundException(roomN));
        Doctor doctor = hospital.getDoctorByName(doctorName);
        Patient patient = hospital.getAttendedPatientByName(patientName).orElseThrow(() -> new PatientNotFoundException(patientName));
        Patient patientBackUpForNotification = new Patient(patientName, patient.getEmergencyLevel());
        hospital.endEmergency(doctor, patient, room);
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
        responseObserver.onNext(emergencyData);
        responseObserver.onCompleted();
        //Control flow gets to this point only of no exception was thrown.
        notificationManager.notifyEmergencyIsOver(patientBackUpForNotification, doctor, room);
    }
}
