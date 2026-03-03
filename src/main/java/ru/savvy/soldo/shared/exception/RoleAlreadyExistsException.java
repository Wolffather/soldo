package ru.savvy.soldo.shared.exception;

public class RoleAlreadyExistsException extends RuntimeException {

    public RoleAlreadyExistsException(String message) {
        super(message);
    }
}
