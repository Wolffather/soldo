CREATE OR REPLACE FUNCTION booking_on_create_pending(event_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE event_bookings_summary
    SET total_reservations = total_reservations + 1,
        available_spots = available_spots - 1
    WHERE event_id = event_id;
END;
$$ LANGUAGE plpgsql;