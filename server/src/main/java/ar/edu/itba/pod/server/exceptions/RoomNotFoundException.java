package ar.edu.itba.pod.server.exceptions;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(final int roomId) {
        super(String.format("Room %d not found", roomId));
    }
}
