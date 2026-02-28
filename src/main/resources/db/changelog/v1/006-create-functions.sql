--liquibase formatted sql

--changeset savvy:006-fn-on-create-pending splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_create_pending(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET total_bookings = total_bookings + 1,
        last_updated = NOW()
    WHERE event_id = p_event_id;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_create_pending(BIGINT);

--changeset savvy:006-fn-on-create-confirmed splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_create_confirmed(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET total_bookings = total_bookings + 1,
        confirmed_bookings = confirmed_bookings + 1,
        num_of_participants = num_of_participants - 1,
        last_updated = NOW()
    WHERE event_id = p_event_id;

    IF (SELECT num_of_participants FROM event_bookings_summary
        WHERE event_id = p_event_id) < 0 THEN
        RAISE EXCEPTION 'Нет свободных мест для события %', p_event_id;
    END IF;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_create_confirmed(BIGINT);

--changeset savvy:006-fn-on-confirm splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_confirm(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET confirmed_bookings = confirmed_bookings + 1,
        num_of_participants = num_of_participants - 1,
        last_updated = NOW()
    WHERE event_id = p_event_id;

    IF (SELECT num_of_participants FROM event_bookings_summary
        WHERE event_id = p_event_id) < 0 THEN
        RAISE EXCEPTION 'Нет свободных мест для события %', p_event_id;
    END IF;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_confirm(BIGINT);

--changeset savvy:006-fn-on-cancel-from-confirmed splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_cancel_from_confirmed(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET confirmed_bookings = confirmed_bookings - 1,
        num_of_participants = num_of_participants + 1,
        last_updated = NOW()
    WHERE event_id = p_event_id;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_cancel_from_confirmed(BIGINT);

--changeset savvy:006-fn-on-cancel-from-pending splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_cancel_from_pending(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET total_bookings = total_bookings - 1,
        last_updated = NOW()
    WHERE event_id = p_event_id;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_cancel_from_pending(BIGINT);

--changeset savvy:006-fn-on-delete splitStatements:false
CREATE OR REPLACE FUNCTION booking_on_delete(p_event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET total_bookings = total_bookings - 1,
        last_updated = NOW()
    WHERE event_id = p_event_id;
END;
$$ LANGUAGE plpgsql;
--rollback DROP FUNCTION IF EXISTS booking_on_delete(BIGINT);