package ru.savvy.soldo.shared.exception;

import java.util.Optional;

public final class EntityFinder {

    private EntityFinder() {}

    public static <T> T findOrThrow(Optional<T> opt, String notFoundMessage) {
        return opt.orElseThrow(() -> new NotFoundException(notFoundMessage));
    }
}
