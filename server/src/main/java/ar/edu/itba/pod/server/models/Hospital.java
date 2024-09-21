package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.server.exceptions.DoctorAlreadyExistsException;
import ar.edu.itba.pod.server.exceptions.DoctorIsAttendingException;
import ar.edu.itba.pod.server.exceptions.DoctorNotFoundException;
import ar.edu.itba.pod.server.exceptions.InvalidLevelException;
import ar.edu.itba.pod.server.utils.PatientArrival;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Hospital {

    private final List<Room> rooms;
    private final Set<Doctor> doctors;
    private final List<PatientArrival> patientArrivals; // Waiting room
    private final ReadWriteLock lock;
    private int patientCount;

    public Hospital() {
        this.rooms = new ArrayList<>();
        this.doctors = new HashSet<>(); // TODO: concurrent set
        this.patientArrivals = new LinkedList<>();
        this.lock = new ReentrantReadWriteLock(true);
        this.patientCount = 0;
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
                    throw new DoctorIsAttendingException(doctorName);
                }

                doctor.setAvailability(availability);
                return true;
            } else {
                throw new DoctorNotFoundException(doctorName);
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
        boolean added;
        lock.writeLock().lock();
        try {
            int level = doctor.getMaxLevel();
            if( level < 1 || level > 5) {
                throw new InvalidLevelException(level);
            }
            added = doctors.add(doctor);
            if(!added) {
                throw new DoctorAlreadyExistsException(doctor.getName());
            }
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    public boolean registerPatient(String name, int emergencyLevel) {
        lock.writeLock().lock();
        try {
            Patient patient = new Patient(name, emergencyLevel);
            PatientArrival patientWithOrder = new PatientArrival(patientCount++, patient);
            patientArrivals.add(patientWithOrder);
            Collections.sort(patientArrivals);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Patient getPatientByName(String name) {
        lock.readLock().lock();
        try {
            Optional<PatientArrival> maybePatientArrival = patientArrivals.stream().filter(patientArrival -> Objects.equals(patientArrival.getPatient().getName(), name)).findFirst();
            return maybePatientArrival.map(PatientArrival::getPatient).orElse(null);
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
            Collections.sort(patientArrivals);
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
            return patientArrivals.indexOf(new PatientArrival(0, patient));
        } finally {
            lock.readLock().unlock();
        }
    }
}
