package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.admin.*;
import ar.edu.itba.pod.server.models.Hospital;
import ar.edu.itba.pod.server.models.Room;
import com.google.protobuf.BoolValue;
import io.grpc.stub.StreamObserver;

import java.util.Optional;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {
    private final Hospital hospital;
    private int roomCounter = 1;
    private final Object roomLock = new Object();


    public AdminServant(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    public void addRoom(RoomData request, StreamObserver<BoolValue> response) {
        synchronized (roomLock) { // TODO: check
            hospital.addRoom(new Room(roomCounter++));
        }

        response.onNext(BoolValue.newBuilder().setValue(true).build());
        response.onCompleted();
    }

    @Override
    public void addDoctor(DoctorData request, StreamObserver<BoolValue> response) {
        String doctorName = request.getName();
        int maxLevel = Integer.parseInt(request.getLevel());

        if (maxLevel < 1 || maxLevel > 5) {
            response.onNext(BoolValue.newBuilder().setValue(false).build());  // Or throw an exception
            response.onCompleted();
            return;
        }

        // Change the doctor message in the .proto to avoid this import
        ar.edu.itba.pod.server.models.Doctor doctor = new ar.edu.itba.pod.server.models.Doctor(doctorName, maxLevel);

        synchronized (hospital) {
            boolean added = hospital.addDoctor(doctor);
            if (!added) {
                response.onNext(BoolValue.newBuilder().setValue(false).build());
                response.onCompleted();
                return;
            }
        }

        response.onNext(BoolValue.newBuilder().setValue(true).build());
        response.onCompleted();
    }

    @Override
    public void defineAvailability(DoctorAvailability request, StreamObserver<BoolValue> response) {
        String doctorName = request.getDoctor();
        String availability = request.getAvailability();

        synchronized (hospital) {
            Optional<ar.edu.itba.pod.server.models.Doctor> maybeDoctor = hospital.getDoctorByName(doctorName);

            if (maybeDoctor.isEmpty()) {
                response.onNext(BoolValue.newBuilder().setValue(false).build());
                response.onCompleted();
                return;
            }

            ar.edu.itba.pod.server.models.Doctor doctor = maybeDoctor.get();

            if (doctor.getAvailability().equals("attending")) {
                response.onError(new IllegalStateException("Doctor is attending another patient and cannot change availability"));
                return;
            }

            if (availability.equals("available") || availability.equals("unavailable")) {
                doctor.setAvailability(availability);
            } else {
                response.onError(new IllegalArgumentException("Invalid availability status: " + availability));
                return;
            }
        }

        response.onNext(BoolValue.newBuilder().setValue(true).build());
        response.onCompleted();
    }

    @Override
    public void getDoctorAvailability(Doctor request, StreamObserver<DoctorAvailability> response) {
        String doctorName = request.getName();

        synchronized (hospital) {
            Optional<ar.edu.itba.pod.server.models.Doctor> maybeDoctor = hospital.getDoctorByName(doctorName);

            if (maybeDoctor.isEmpty()) {
                response.onError(new IllegalArgumentException("Doctor not found"));
                return;
            }

            ar.edu.itba.pod.server.models.Doctor doctor = maybeDoctor.get();

            response.onNext(DoctorAvailability.newBuilder()
                    .setDoctor(doctorName)
                    .setAvailability(doctor.getAvailability())
                    .build());
            response.onCompleted();
        }
    }
}
