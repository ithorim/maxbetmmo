package com.tihomir.maxbet.exception.custom;

public class CharacterClassNotFoundException extends RuntimeException{
    public CharacterClassNotFoundException(String message) {
        super(message);
    }

    public CharacterClassNotFoundException(Integer id) {
        super("Character class not found with id: " + id);
    }
}
