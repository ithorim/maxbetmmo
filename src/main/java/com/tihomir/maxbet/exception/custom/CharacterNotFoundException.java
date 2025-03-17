package com.tihomir.maxbet.exception.custom;

public class CharacterNotFoundException extends RuntimeException{
    public CharacterNotFoundException(String message) {
        super(message);
    }

    public CharacterNotFoundException(Integer id) {
        super("Character not found with id: " + id);
    }
}
