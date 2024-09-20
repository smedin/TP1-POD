package ar.edu.itba.pod.server.exceptions;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(final String message) {
        super("Doctor " + message + " is not registered");
    }
}
