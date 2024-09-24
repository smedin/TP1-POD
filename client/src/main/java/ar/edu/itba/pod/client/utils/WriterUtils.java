package ar.edu.itba.pod.client.utils;

import ar.edu.itba.pod.grpc.emergency.Emergency;
import ar.edu.itba.pod.grpc.query.*;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class WriterUtils {

    public static void writeQuery1(String filePath, Logger logger, RoomList data) {
        if (data.getRoomDataList().isEmpty()){
            logger.info("No content to show");
            return;
        }
        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("Room,Status,Patient,Doctor\n");
            logger.info("Room,Status,Patient,Doctor");

            for (RoomData roomData : data.getRoomDataList()) {
                String line = roomData.getRoomNumber() + "," +
                        (roomData.getFree() ? "Free" : "Occupied") + "," +
                        roomData.getPatient().getName() + (roomData.getPatient().getLevel() == 0 ? "" : " (" + roomData.getPatient().getLevel() + ")") + "," +
                        roomData.getDoctor().getName() + (roomData.getDoctor().getLevel() == 0 ? "" : " (" + roomData.getDoctor().getLevel() + ")");
                writer.write(line+"\n");
                logger.info(line);
            }

        } catch (IOException e) {
            System.out.println("Error while creating/writing to the file: " + e.getMessage());
        }
    }

    public static void writeQuery2(String filePath, Logger logger, PatientList data) {
        if (data.getPatientDataList().isEmpty()){
            logger.info("No content to show");
            return;
        }
        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("Patient,Level\n");
            logger.info("Patient,Level");

            for (PersonData personData : data.getPatientDataList()) {
                String line = personData.getName() + "," + personData.getLevel();
                writer.write(line+"\n");
                logger.info(line);
            }

        } catch (IOException e) {
            System.out.println("Error while creating/writing to the file: " + e.getMessage());
        }
    }

    public static void writeQuery3(String filePath, Logger logger, EmergencyList data) {
        if (data.getEmergencyList().isEmpty()){
            logger.info("No content to show");
            return;
        }
        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("Room,Patient,Doctor\n");
            logger.info("Room,Patient,Doctor");

            for (FinalizedEmergency emergency : data.getEmergencyList()) {
                String line = emergency.getRoomNumber()+","+emergency.getPatient().getName()+" ("+emergency.getPatient().getLevel()+"),"+emergency.getDoctor().getName()+" ("+emergency.getDoctor().getLevel()+")";
                writer.write(line+"\n");
                logger.info(line);
            }

        } catch (IOException e) {
            System.out.println("Error while creating/writing to the file: " + e.getMessage());
        }
    }

}
