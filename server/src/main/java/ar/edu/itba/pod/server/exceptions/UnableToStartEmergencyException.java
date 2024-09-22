package ar.edu.itba.pod.server.exceptions;

public class UnableToStartEmergencyException extends RuntimeException {
    public UnableToStartEmergencyException(final int room) {
        super("Room #" + room + " remains Free");
    }
}
