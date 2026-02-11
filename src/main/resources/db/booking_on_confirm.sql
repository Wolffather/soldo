CREATE OR REPLACE FUNCTION booking_on_confirm(event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET confirmed_reservations = confirmed_reservations + 1
    WHERE event_id = event_id;
END;
$$ LANGUAGE plpgsql;