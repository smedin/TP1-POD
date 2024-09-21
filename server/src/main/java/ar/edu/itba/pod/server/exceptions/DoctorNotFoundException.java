package ar.edu.itba.pod.server.exceptions;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(final String doctorName) {
        super("Doctor " + doctorName + " is not registered");
    }
}
