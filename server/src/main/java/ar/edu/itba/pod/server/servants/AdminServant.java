package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.admin.*;
import com.google.protobuf.BoolValue;
import io.grpc.stub.StreamObserver;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {

    public AdminServant() {

    }

    @Override
    public void addRoom(RoomData request, StreamObserver<BoolValue> response) {

    }

    @Override
    public void addDoctor(DoctorData request, StreamObserver<BoolValue> response) {

    }

    @Override
    public void defineAvailability(DoctorAvailability request, StreamObserver<BoolValue> response) {

    }

    @Override
    public void getDoctorAvailability(Doctor request, StreamObserver<DoctorAvailability> response) {

    }
}
