package com.tihomir.maxbet.exception.custom;

public class ItemNotFoundException extends RuntimeException{
    public ItemNotFoundException(String message) {
        super(message);
    }
    public ItemNotFoundException(Integer id) {
        super("Item not found with id: " + id);
    }
}
