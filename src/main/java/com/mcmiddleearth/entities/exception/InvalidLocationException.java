package com.mcmiddleearth.entities.exception;

import org.bukkit.World;

public class InvalidLocationException extends Exception {

    private final World found;
    private final World required;

    public InvalidLocationException(String message, World found, World required) {
        super(message);
        this.found = found;
        this.required = required;
    }

    public World getFound() {
        return found;
    }

    public World getRequired() {
        return required;
    }
}
