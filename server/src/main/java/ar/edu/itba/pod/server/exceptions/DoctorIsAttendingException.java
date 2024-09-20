package ar.edu.itba.pod.server.exceptions;

public class DoctorIsAttendingException extends RuntimeException {
    public DoctorIsAttendingException(final String message) {
        super("Doctor " + message + " is attending, cannot change availability right now");
    }
}
