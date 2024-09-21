package ar.edu.itba.pod.server.exceptions;

public class DoctorNotRegisteredForNotificationsException extends RuntimeException {
    public DoctorNotRegisteredForNotificationsException(final String doctorName) {
        super(String.format("Doctor %s not registered for notifications",doctorName));
    }
}
