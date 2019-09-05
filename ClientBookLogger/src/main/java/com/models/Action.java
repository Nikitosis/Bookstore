package com.models;

public enum Action {
    TAKE("take"),
    RETURN("return");

    private String action;

    Action(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}
