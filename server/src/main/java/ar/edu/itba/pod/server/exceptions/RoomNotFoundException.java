package ar.edu.itba.pod.server.exceptions;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(final int roomId) {
        super("Room " + roomId + " not found");
    }
}
