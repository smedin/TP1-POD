package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.query.*;
import ar.edu.itba.pod.server.models.Hospital;
import io.grpc.stub.StreamObserver;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {
    private final Hospital hospital;

    public QueryServant(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    public void getRooms(OutPath request, StreamObserver<RoomList> responseObserver) {

    }

    @Override
    public void getWaitingRoom(OutPath request, StreamObserver<PatientList> responseObserver) {

    }

    @Override
    public void getFinalizedEmergencies(FinalizedInput request, StreamObserver<FinalizedEmergencyList> responseObserver) {

    }
}
