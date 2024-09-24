package ar.edu.itba.pod.client.utils;

import ar.edu.itba.pod.grpc.query.PatientList;
import ar.edu.itba.pod.grpc.query.PersonData;
import ar.edu.itba.pod.grpc.query.RoomData;
import ar.edu.itba.pod.grpc.query.RoomList;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class WriterUtils {

    public static void writeQuery1(String filePath, Logger logger, RoomList data) {
        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("Room,Status,Patient,Doctor\n");

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
        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("Patient,Level\n");

            for (PersonData personData : data.getPatientDataList()) {
                String line = personData.getName() + "," + personData.getLevel();
                writer.write(line+"\n");
                logger.info(line);
            }

        } catch (IOException e) {
            System.out.println("Error while creating/writing to the file: " + e.getMessage());
        }
    }

}
