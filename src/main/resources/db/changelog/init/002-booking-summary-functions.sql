--liquibase formatted sql
--
-- Функции пересчёта event_bookings_summary.
-- Итоговая логика: PENDING бронирование сразу резервирует место
-- (иначе была race condition при одновременных запросах).
--

--changeset savvy:init-fn-on-create-pending splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_create_pending(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET total_bookings      = total_bookings + 1,
        pending_bookings    = COALESCE(pending_bookings, 0) + 1,
        num_of_participants = num_of_participants - 1,
        last_updated        = NOW()
    WHERE event_id = p_event_id;

    IF (SELECT num_of_participants FROM event_bookings_summary
        WHERE event_id = p_event_id) < 0 THEN
        RAISE EXCEPTION 'Нет свободных мест для события %', p_event_id;
    END IF;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_create_pending(BIGINT);

--changeset savvy:init-fn-on-create-confirmed splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_create_confirmed(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET total_bookings      = total_bookings + 1,
        confirmed_bookings  = confirmed_bookings + 1,
        num_of_participants = num_of_participants - 1,
        last_updated        = NOW()
    WHERE event_id = p_event_id;

    IF (SELECT num_of_participants FROM event_bookings_summary
        WHERE event_id = p_event_id) < 0 THEN
        RAISE EXCEPTION 'Нет свободных мест для события %', p_event_id;
    END IF;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_create_confirmed(BIGINT);

--changeset savvy:init-fn-on-confirm splitStatements:false
-- Место уже занято с момента создания PENDING — только переводим счётчики
CREATE OR REPLACE FUNCTION booking_on_confirm(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET confirmed_bookings  = confirmed_bookings + 1,
        pending_bookings    = GREATEST(COALESCE(pending_bookings, 0) - 1, 0),
        last_updated        = NOW()
    WHERE event_id = p_event_id;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_confirm(BIGINT);

--changeset savvy:init-fn-on-cancel-from-confirmed splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_cancel_from_confirmed(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET confirmed_bookings  = GREATEST(confirmed_bookings - 1, 0),
        cancelled_bookings  = COALESCE(cancelled_bookings, 0) + 1,
        num_of_participants = num_of_participants + 1,
        last_updated        = NOW()
    WHERE event_id = p_event_id;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_cancel_from_confirmed(BIGINT);

--changeset savvy:init-fn-on-cancel-from-pending splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_cancel_from_pending(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET total_bookings      = GREATEST(total_bookings - 1, 0),
        pending_bookings    = GREATEST(COALESCE(pending_bookings, 0) - 1, 0),
        cancelled_bookings  = COALESCE(cancelled_bookings, 0) + 1,
        num_of_participants = num_of_participants + 1,
        last_updated        = NOW()
    WHERE event_id = p_event_id;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_cancel_from_pending(BIGINT);

--changeset savvy:init-fn-on-delete splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_delete(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET total_bookings = GREATEST(total_bookings - 1, 0),
        last_updated   = NOW()
    WHERE event_id = p_event_id;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_delete(BIGINT);
