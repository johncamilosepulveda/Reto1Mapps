package com.example.Reto1.model;

public class PositionContainer {

    private Position location;

    public PositionContainer(Position location) {
        this.location = location;
    }

    public PositionContainer() {
    }

    public Position getLocation() {
        return location;
    }

    public void setLocation(Position location) {
        this.location = location;
    }
}
