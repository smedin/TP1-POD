package ar.edu.itba.pod.server.models;

public class Room {

    private final int id;
    private boolean free;
    private Doctor doctor;
    private Patient patient;

    public Room(int id) {
        this.id = id;
        this.free = true;
        this.doctor = null;
        this.patient = null;
    }

    public int getId() {
        return id;
    }

    public boolean isFree() {
        return free;
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

    public void setFree(boolean free) {
        this.free = free;
    }

}
