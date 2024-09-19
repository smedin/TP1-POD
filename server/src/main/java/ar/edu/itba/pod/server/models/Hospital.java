package ar.edu.itba.pod.server.models;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Hospital {

    private final List<Room> rooms;
    private final Set<Doctor> doctors;
    private final List<Patient> patients; // Waiting room
    private final ReadWriteLock lock;

    public Hospital() {
        this.rooms = new ArrayList<>();
        this.doctors = new HashSet<>(); // TODO: concurrent set
        this.patients = new LinkedList<>();
        this.lock = new ReentrantReadWriteLock(true);
    }

    public List<Room> getRooms() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(rooms);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addRoom(Room room) {
        lock.writeLock().lock();
        try {
            rooms.add(room);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Set<Doctor> getDoctors() {
        lock.readLock().lock();
        try {
            return new HashSet<>(doctors);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Doctor> getDoctorByName(String name) {
        lock.readLock().lock();
        try {
            return doctors.stream().filter(doctor -> doctor.getName().equals(name)).findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean defineDoctorAvailability(String doctorName, String availability) {
        lock.writeLock().lock();
        try {
            Optional<Doctor> maybeDoctor = getDoctorByName(doctorName);
            if (maybeDoctor.isPresent()) {
                Doctor doctor = maybeDoctor.get();

                if (doctor.getAvailability().equals("attending")) {
                    return false;
                }

                doctor.setAvailability(availability);
                return true;
            } else {
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String getDoctorAvailability(String doctorName) {
        lock.readLock().lock();
        try {
            Optional<Doctor> maybeDoctor = getDoctorByName(doctorName);
            return maybeDoctor
                    .map(Doctor::getAvailability)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorName)); // TODO: check
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean addDoctor(Doctor doctor) {
        lock.writeLock().lock();
        try {
            return doctors.add(doctor);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean registerPatient(String name, int emergencyLevel) {
        lock.writeLock().lock();
        try {
            Patient patient = new Patient(name, emergencyLevel);
            patients.add(patient);
            patients.sort(Patient::compareTo);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Patient getPatientByName(String name) {
        lock.readLock().lock();
        try {
            return patients.stream().filter(patient -> patient.getName().equals(name)).findFirst().orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean updateEmergencyLevel(String name, int emergencyLevel) {
        lock.writeLock().lock();
        try {
            Patient patient = getPatientByName(name);
            if (patient == null) {
                return false;
            }
            patient.setEmergencyLevel(emergencyLevel);
            patients.sort(Patient::compareTo); //TODO: check/optimize
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getWaitingTime(String name) {
        lock.readLock().lock();
        try {
            Patient patient = getPatientByName(name);
            if (patient == null) {
                return -1;
            }
            return patients.indexOf(patient);
        } finally {
            lock.readLock().unlock();
        }
    }
}
