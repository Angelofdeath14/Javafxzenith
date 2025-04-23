package tn.esprit.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Command {
    private int id;
    private int id_user;
    private LocalDateTime create_at;
    private String status;
    private double total_amount;
    private String delivery_address;
    private String notes;

    public Command() {
    }

    public Command(int id_user, LocalDateTime create_at, String status, double total_amount, String delivery_address, String notes) {
        this.id_user = id_user;
        this.create_at = create_at;
        this.status = status;
        this.total_amount = total_amount;
        this.delivery_address = delivery_address;
        this.notes = notes;
    }

    public Command(int id, int id_user, LocalDateTime create_at, String status, double total_amount, String delivery_address, String notes) {
        this.id = id;
        this.id_user = id_user;
        this.create_at = create_at;
        this.status = status;
        this.total_amount = total_amount;
        this.delivery_address = delivery_address;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }

    public void setCreate_at(LocalDateTime create_at) {
        this.create_at = create_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Command command)) return false;
        return getId_user() == command.getId_user() && Double.compare(getTotal_amount(), command.getTotal_amount()) == 0 && Objects.equals(getCreate_at(), command.getCreate_at()) && Objects.equals(getStatus(), command.getStatus()) && Objects.equals(getDelivery_address(), command.getDelivery_address()) && Objects.equals(getNotes(), command.getNotes());
    }

    @Override
    public String toString() {
        return "Command Details:\n" +
                "Date: " + create_at + "\n" +
                "Status: " + status + "\n" +
                "Total Amount: " + total_amount + "\n" +
                "Delivery Address: " + delivery_address + "\n" +
                "Notes: " + notes;
    }

}
