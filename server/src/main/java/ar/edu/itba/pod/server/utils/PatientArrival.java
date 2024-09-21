package ar.edu.itba.pod.server.utils;

import ar.edu.itba.pod.server.models.Patient;

import java.util.Objects;

public class PatientArrival implements Comparable<PatientArrival>{
    private Integer arrivalOrder;
    private Patient patient;

    public PatientArrival(Integer arrivalOrder, Patient patient) {
        this.arrivalOrder = arrivalOrder;
        this.patient = patient;
    }

    public Integer getArrivalOrder() {
        return arrivalOrder;
    }

    public void setArrivalOrder(Integer arrivalOrder) {
        this.arrivalOrder = arrivalOrder;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public int compareTo(PatientArrival o) {
        int compare = this.patient.compareTo(o.patient);
        if (compare==0){
            return this.arrivalOrder.compareTo(o.arrivalOrder);
        }
        return compare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientArrival that = (PatientArrival) o;
        return Objects.equals(patient, that.patient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arrivalOrder, patient);
    }
}
