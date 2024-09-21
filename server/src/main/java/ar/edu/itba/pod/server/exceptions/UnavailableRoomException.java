package ar.edu.itba.pod.server.exceptions;

public class UnavailableRoomException extends RuntimeException {
    public UnavailableRoomException(final int roomId) {
        super(String.format("Room %d is not available", roomId));
    }
}
