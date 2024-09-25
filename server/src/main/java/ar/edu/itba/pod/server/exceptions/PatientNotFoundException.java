package ar.edu.itba.pod.server.exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(final String patientName) {
        super(String.format("Patient %s not found", patientName));
    }
}
