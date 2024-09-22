package ar.edu.itba.pod.server.models;

public class Room {

    private final int id;
    private boolean free;
    private Doctor doctor;
    private Patient patient;
    private boolean newOccupation;

    public Room(int id) {
        this.id = id;
        this.free = true;
        this.doctor = null;
        this.patient = null;
        this.newOccupation = false;
    }

    public int getId() {
        return id;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public boolean isNewOccupation() {
        return newOccupation;
    }

    public void setNewOccupation(boolean newOccupation) {
        this.newOccupation = newOccupation;
    }
}
