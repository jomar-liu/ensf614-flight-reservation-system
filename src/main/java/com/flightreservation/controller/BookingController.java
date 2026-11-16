package com.flightreservation.controller;

import com.flightreservation.data.DatabaseManager;
import com.flightreservation.data.PaymentDAO;
import com.flightreservation.data.ReservationDAO;
import com.flightreservation.data.SeatDAO;
import com.flightreservation.model.Payment;
import com.flightreservation.model.PaymentMethod;
import com.flightreservation.model.PaymentStatus;
import com.flightreservation.model.Reservation;
import com.flightreservation.model.ReservationStatus;
import com.flightreservation.model.Seat;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Controller coordinating flight reservation creation, cancellation, and modification.
 * Stereotype: <<control>>
 */
public class BookingController {
    private final ReservationDAO reservationDAO;
    private final SeatDAO seatDAO;
    private final PaymentDAO paymentDAO;
    private final DatabaseManager dbManager;

    public BookingController() {
        this.reservationDAO = new ReservationDAO();
        this.seatDAO = new SeatDAO();
        this.paymentDAO = new PaymentDAO();
        this.dbManager = DatabaseManager.getInstance();
    }

    public Reservation createBooking(int customerId, int flightId, int seatId,
                                     PaymentMethod paymentMethod, String cardNumberMasked,
                                     double finalAmount) {
        Seat seat = seatDAO.getSeatById(seatId);
        if (seat == null || !seat.isAvailable()) {
            return null;
        }

        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false);

            // Reserve seat
            seatDAO.setSeatAvailability(seatId, false, conn);

            // Generate unique booking confirmation code (e.g. BK-A1B2C3)
            String bookingCode = "BK-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

            Reservation reservation = new Reservation(0, bookingCode, customerId, flightId, seatId, null, ReservationStatus.CONFIRMED);
            int resId = reservationDAO.createReservation(reservation, conn);
            if (resId <= 0) {
                conn.rollback();
                return null;
            }

            // Record simulated payment
            Payment payment = new Payment(0, resId, finalAmount, paymentMethod, cardNumberMasked, null, PaymentStatus.COMPLETED);
            paymentDAO.createPayment(payment, conn);

            conn.commit();
            return reservationDAO.getReservationById(resId);
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean cancelBooking(int reservationId) {
        return reservationDAO.cancelReservation(reservationId);
    }

    public boolean modifyBookingSeat(int reservationId, int newSeatId) {
        return reservationDAO.modifyReservationSeat(reservationId, newSeatId);
    }

    public List<Reservation> getCustomerReservations(int customerId) {
        return reservationDAO.getReservationsByCustomerId(customerId);
    }

    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    public Reservation getReservationByCode(String code) {
        return reservationDAO.getReservationByCode(code);
    }
}
