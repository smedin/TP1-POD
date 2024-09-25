package ar.edu.itba.pod.server.exceptions;

public class PatientAlreadyAttendedException extends RuntimeException {
    public PatientAlreadyAttendedException(final String patientName) {
        super(String.format("Patient %s is already attended", patientName));
    }
}
