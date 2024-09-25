package ar.edu.itba.pod.server.exceptions;

public class DoctorIsAttendingException extends RuntimeException {
    public DoctorIsAttendingException(final String doctorName) {
        super(String.format("Doctor %s is attending, cannot change availability right now", doctorName));
    }
}
