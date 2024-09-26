package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.server.exceptions.*;
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
    private final List<Patient> patientsInAttention;
    private final List<Emergency> finalizedEmergencies;

    public Hospital() {
        this.rooms = new ArrayList<>();
        this.doctors = new TreeSet<>(); // TODO: concurrent set
        this.patientArrivals = new LinkedList<>();
        this.patientsInAttention = new LinkedList<>();
        finalizedEmergencies = new LinkedList<>();
        this.lock = new ReentrantReadWriteLock(true);
        this.patientCount = 0;
    }

    public List<Room> getRooms() {
        lock.readLock().lock();
        try {
            return rooms;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Patient> getWaitingPatients() {
        lock.readLock().lock();
        try {
            return patientArrivals.stream().map(PatientArrival::getPatient).toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Emergency> getFinalizedEmergencies(Integer roomNumber) {
        lock.readLock().lock();
        try {
            if (roomNumber==-1){
                return finalizedEmergencies;
            }

            return finalizedEmergencies.stream().filter(fe -> fe.getRoomNumber()==roomNumber).toList();

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

    public Doctor getDoctorByName(String name) {
        lock.readLock().lock();
        try {
            return doctors.stream().filter(doctor -> doctor.getName().equals(name)).findFirst().orElseThrow(() -> new DoctorNotFoundException(name));
        } finally {
            lock.readLock().unlock();
        }
    }

    public Doctor defineDoctorAvailability(String doctorName, Availability availability) {
        lock.writeLock().lock();
        try {
            Doctor doctor = getDoctorByName(doctorName);

            if (doctor.getAvailability().equals(Availability.ATTENDING)) {
                throw new DoctorIsAttendingException(doctorName);
            }

            doctor.setAvailability(availability);
            return doctor;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addDoctor(Doctor doctor) {
        boolean added;
        lock.writeLock().lock();
        try {
            added = doctors.add(doctor);
            if(!added) {
                throw new DoctorAlreadyExistsException(doctor.getName());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void registerPatient(String name, int emergencyLevel) {
        lock.writeLock().lock();
        try {
            Patient patient = new Patient(name, emergencyLevel);
            PatientArrival patientWithOrder = new PatientArrival(patientCount++, patient);
            if (patientArrivals.contains(patientWithOrder) || patientsInAttention.contains(patientWithOrder.getPatient()) || finalizedEmergencies.stream().anyMatch(e -> e.getPatient().equals(patientWithOrder.getPatient()))) {
                throw new PatientAlreadyInWaitingRoomException(patient.getName());
            }
            patientArrivals.add(patientWithOrder);
            Collections.sort(patientArrivals);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Patient getPatientByName(String name) {
        lock.readLock().lock();
        try {
            Optional<PatientArrival> maybePatientArrival = patientArrivals.stream().filter(patientArrival -> Objects.equals(patientArrival.getPatient().getName(), name)).findFirst();
            return maybePatientArrival.map(PatientArrival::getPatient).orElseThrow(()->new PatientNotFoundException(name));
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Patient> getAttendedPatientByName(String name) {
        lock.readLock().lock();
        try {
            return patientsInAttention.stream().filter(patient -> patient.getName().equals(name)).findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateEmergencyLevel(String name, int emergencyLevel) {
        lock.writeLock().lock();
        try {
            Patient patient = getPatientByName(name);
            patient.setEmergencyLevel(emergencyLevel);
            Collections.sort(patientArrivals);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getWaitingTime(String name) {
        lock.readLock().lock();
        try {
            Patient patient = getPatientByName(name);
            return patientArrivals.indexOf(new PatientArrival(0, patient));
        } finally {
            lock.readLock().unlock();
        }
    }

    public Doctor getNextDoctor(int patientLevel) {
        lock.readLock().lock();
        try {
            for (Doctor doctor : doctors) {
                if (doctor.isAvailable() && doctor.getMaxLevel() >= patientLevel) {
                    return doctor;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Room getRoomById(int roomNumber) {
        lock.readLock().lock();
        try {
            Room room = rooms.stream().filter(r -> r.getId() == roomNumber).findFirst().orElseThrow(() -> new RoomNotFoundException(roomNumber));
            if (!room.isFree()) {
                throw new RoomNotFreeException(roomNumber);
            }
            return room;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Room startEmergencyByRoom(Room room) {
        lock.writeLock().lock();
        try {
            for (PatientArrival patientArrival : patientArrivals) {
                Patient nextPatient = patientArrival.getPatient();
                Doctor nextDoctor = getNextDoctor(nextPatient.getEmergencyLevel());
                if (nextDoctor != null) {
                    room.setDoctor(nextDoctor);
                    room.setPatient(nextPatient);
                    room.setFree(false);
                    room.setNewOccupation(true);
                    nextDoctor.setAvailability(Availability.ATTENDING);
                    patientsInAttention.add(patientArrival.getPatient());
                    patientArrivals.remove(patientArrival);
                    return room;
                }
            }
            return room;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Room> startAllEmergencies() {
        lock.writeLock().lock();
        try {
            for (Room room : this.rooms) {
                if (room.isFree()) {
                    startEmergencyByRoom(room);
                }
            }
            return rooms;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void cleanOccupations() {
        lock.writeLock().lock();
        try {
            for (Room room : rooms) {
                if (room.isNewOccupation()) {
                    room.setNewOccupation(false);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void endEmergency(Doctor doctor, Patient patient, Room room) {
        lock.writeLock().lock();
        try {
            if (room.isFree()) {
                throw new RoomFreeException(room.getId());
            }
            if (!Objects.equals(room.getDoctor().getName(), doctor.getName())) {
                throw new DoctorNotInRoomException(doctor.getName(), room.getId());
            }
            room.setFree(true);
            room.setDoctor(null);
            room.setPatient(null);
            doctor.setAvailability(Availability.AVAILABLE);
            finalizedEmergencies.add(new Emergency(room.getId(), doctor, patient));
            patientsInAttention.remove(patient);

        } finally {
            lock.writeLock().unlock();
        }

    }
}
