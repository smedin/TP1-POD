package ar.edu.itba.pod.server.exceptions;

public class RoomNotFreeException extends RuntimeException {
    public RoomNotFreeException(final int roomId) {
        super("Room " + roomId + " is not Free");
    }
}
