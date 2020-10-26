package com.example.Reto1.model;

public class Hole {

    private String id;
    private Position position;
    private boolean confirmed;
    private String userReporter;

    public Hole() {
    }

    public Hole(String id, Position position, boolean confirmed, String userReporter) {
        this.id = id;
        this.position = position;
        this.confirmed = confirmed;
        this.userReporter = userReporter;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserReporter() {
        return userReporter;
    }

    public void setUserReporter(String userReporter) {
        this.userReporter = userReporter;
    }
}
