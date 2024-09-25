package ar.edu.itba.pod.server.exceptions;

public class PatientAlreadyInWaitingRoomException extends RuntimeException {
    public PatientAlreadyInWaitingRoomException(final String patientName) {
        super(String.format("Patient %s already exists in waiting room", patientName));
    }
}
