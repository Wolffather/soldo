package ru.savvy.soldo.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.savvy.soldo.dto.EventDTO;

public class DateOrderValidator implements ConstraintValidator<ValidDateOrder, EventDTO> {

    @Override
    public boolean isValid(EventDTO event, ConstraintValidatorContext context) {
        if (event == null) {
            return true;
        }

        if (event.getStartDate() == null || event.getEndDate() == null) {
            return true;
        }

        boolean isValid = !event.getEndDate().isBefore(event.getStartDate());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Дата окончания должна быть после даты начала")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
        }

        return isValid;
    }
}