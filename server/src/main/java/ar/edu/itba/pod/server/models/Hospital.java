package ar.edu.itba.pod.server.models;

import java.util.*;

public class Hospital {

    private final List<Room> rooms;
    private final Set<Doctor> doctors;

    public Hospital() {
        this.rooms = new ArrayList<>();
        this.doctors = new HashSet<>();
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public Set<Doctor> getDoctors() {
        return doctors;
    }

    public Optional<Doctor> getDoctorByName(String name) {
        return doctors.stream().filter(doctor -> doctor.getName().equals(name)).findFirst();
    }

    public boolean addDoctor(Doctor doctor) {
        return doctors.add(doctor);
    }

}
