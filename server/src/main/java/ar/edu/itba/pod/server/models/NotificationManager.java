package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.grpc.notifications.Notification;
import ar.edu.itba.pod.server.exceptions.DoctorAlreadyRegisteredForNotificationsException;
import ar.edu.itba.pod.server.exceptions.DoctorNotRegisteredForNotificationsException;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NotificationManager {
    private final Map<String, StreamObserver<Notification>> registeredDoctors = new ConcurrentHashMap<>();
    private final Lock notificationLock = new ReentrantLock();

    public void registerDoctor(String doctorName, StreamObserver<Notification> observer) {
        //TODO: Before registering check outside this function if the doctor exists in hospital
        notificationLock.lock();
        try {
            if (registeredDoctors.containsKey(doctorName)) {
                throw new DoctorAlreadyRegisteredForNotificationsException(doctorName);
            } else {
                registeredDoctors.put(doctorName, observer);
            }
        } finally {
            notificationLock.unlock();
        }
    }

    public StreamObserver<Notification> UnregisterDoctor(String doctorName) {
        notificationLock.lock();
        try {
            if (!registeredDoctors.containsKey(doctorName)) {
                throw new DoctorNotRegisteredForNotificationsException(doctorName);
            } else {
                return registeredDoctors.remove(doctorName); //TODO: Check if it really return the value associated with the key
            }
        } finally {
            notificationLock.unlock();
        }
    }

    public void notify(String doctorName, String message) {
        notificationLock.lock();
        try {
            if (registeredDoctors.containsKey(doctorName)) {
                registeredDoctors.get(doctorName).onNext(Notification.newBuilder().setMessage(message).build());
            }
        } finally {
            notificationLock.unlock();
        }
    }

    public void notifyRegistration(Doctor doctor) {
        String doctorName = doctor.getName();
        int level = doctor.getMaxLevel();
        String message = String.format("%s (%d) registered successfuly for pager", doctorName, level);
        notify(doctorName, message);
    }

    public void notifyAvailabilityDefinition(Doctor doctor, Availability availability) {
        String doctorName = doctor.getName();
        int level = doctor.getMaxLevel();
        String message = String.format("%s (%d) is %s", doctorName, level, availability);
        notify(doctorName, message);
    }

    public void notifyEmergencyTaken(Room room) {
        Doctor doctor = room.getDoctor();
        String doctorName = doctor.getName();
        int doctorLevel = room.getDoctor().getMaxLevel();
        Patient patient = room.getPatient();
        String patientName = patient.getName();
        int patientLevel = patient.getEmergencyLevel();
        int roomNumber = room.getId();
        String message = String.format("Patient %s (%d) and Doctor %s (%d) are now in Room #%d", patientName, patientLevel, doctorName, doctorLevel, roomNumber);
        notify(doctorName, message);
    }

    public void notifyEmergencyIsOver(Patient patient, Doctor doctor, Room room) {
        String doctorName = doctor.getName();
        int doctorLevel = doctor.getMaxLevel();
        String patientName = patient.getName();
        int patientLevel = patient.getEmergencyLevel();
        int roomNumber = room.getId();
        String message = String.format("Patient %s (%d) has been discharged from Doctor %s (%d) and the Room #%d is now Free", patientName, patientLevel, doctorName, doctorLevel, roomNumber);
        notify(doctorName, message);
    }
}
