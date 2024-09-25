package ar.edu.itba.pod.server.exceptions;

public class RoomNotFreeException extends RuntimeException {
    public RoomNotFreeException(final int roomId) {
        super(String.format("Room %d is not Free", roomId));
    }
}
