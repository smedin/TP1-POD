package ar.edu.itba.pod.server.exceptions;

public class RoomFreeException extends RuntimeException {
    public RoomFreeException(final int roomId) {
        super("Room " + roomId + " is Free");
    }
}
