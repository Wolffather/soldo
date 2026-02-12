package ru.savvy.soldo.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.savvy.soldo.dto.EventDTO;

public class DateOrderValidator implements ConstraintValidator<ValidDateOrder, EventDTO> {

    @Override
    public boolean isValid(EventDTO event, ConstraintValidatorContext context) {
        if (event == null) {
            return false;
        }
        if (event.getStartDate() == null || event.getEndDate() == null) {
            return false;
        }
        return event.getEndDate().isAfter(event.getStartDate());
    }
}

