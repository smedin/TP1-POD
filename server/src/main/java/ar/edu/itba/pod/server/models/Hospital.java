package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.server.exceptions.*;
import ar.edu.itba.pod.server.utils.Pair;
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
    private final List<Patient> patientsAttended;

    public Hospital() {
        this.rooms = new ArrayList<>();
        this.doctors = new TreeSet<>(); // TODO: concurrent set
        this.patientArrivals = new LinkedList<>();
        this.patientsAttended = new LinkedList<>();
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

    public Doctor defineDoctorAvailability(String doctorName, Availability availability) {
        lock.writeLock().lock();
        try {
            Optional<Doctor> maybeDoctor = getDoctorByName(doctorName);
            if (maybeDoctor.isPresent()) {
                Doctor doctor = maybeDoctor.get();

                if (doctor.getAvailability().equals(Availability.ATTENDING)) {
                    throw new DoctorIsAttendingException(doctorName);
                }

                doctor.setAvailability(availability);
                return doctor;
            } else {
                throw new DoctorNotFoundException(doctorName);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Availability getDoctorAvailability(String doctorName) {
        lock.readLock().lock();
        try {
            Optional<Doctor> maybeDoctor = getDoctorByName(doctorName);
            return maybeDoctor
                    .map(Doctor::getAvailability)
                    .orElseThrow(() -> new DoctorNotFoundException(doctorName)); // TODO: check
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
            return maybePatientArrival.map(PatientArrival::getPatient).orElseThrow(()->new PatientNotFoundException(name));
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Patient> getAttendedPatientByName(String name) {
        lock.readLock().lock();
        try {
            return patientsAttended.stream().filter(doctor -> doctor.getName().equals(name)).findFirst();
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

    public Doctor getNextDoctor(int patientLevel) {
        lock.writeLock().lock();
        try {
            for (Doctor doctor : doctors) {
                if (doctor.isAvailable() && doctor.getMaxLevel() >= patientLevel) {
                    return doctor;
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Room getRoomById(int roomNumber) {
        lock.readLock().lock();
        try {
            Room room = rooms.stream().filter(r -> r.getId() == roomNumber).findFirst().orElseThrow(() -> new RoomNotFoundException(roomNumber));
            if (!room.isFree()) {
                throw new RoomFreeException(roomNumber);
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
                    patientsAttended.add(patientArrival.getPatient());
                    patientArrivals.remove(patientArrival);
                    return room;
                }
            }
            //throw new UnableToStartEmergencyException(room.getId());
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
        try {
            lock.writeLock().lock();

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
            patientsAttended.remove(patient);

        } finally {
            lock.writeLock().unlock();
        }

    }
}
