package ar.edu.itba.pod.server.exceptions;

public class UnableToStartEmergencyException extends RuntimeException {
    public UnableToStartEmergencyException(final int roomId) {
        super(String.format("Room #%d remains Free", roomId));
    }
}
