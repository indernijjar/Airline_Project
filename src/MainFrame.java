import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainFrame extends JFrame {
    private final ReservationController controller;
    private final JTextArea searchResultsArea = new JTextArea(12, 60);
    private final JTextArea reservationsArea = new JTextArea(12, 60);

    public MainFrame(ReservationController controller) {
        this.controller = controller;
        setTitle("Airline Reservation System - Part 6 Implementation");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Search Flights", buildSearchPanel());
        tabs.addTab("Book Reservation", buildBookPanel());
        tabs.addTab("Confirm Reservation", buildConfirmPanel());
        tabs.addTab("Cancel Reservation", buildCancelPanel());
        tabs.addTab("View Reservations", buildReservationsPanel());

        add(tabs, BorderLayout.CENTER);
        refreshReservations();
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));

        JTextField originField = new JTextField();
        JTextField destinationField = new JTextField();
        JTextField dateField = new JTextField("2026-04-20");
        JButton searchButton = new JButton("Search Flights");

        form.add(new JLabel("Origin:"));
        form.add(originField);
        form.add(new JLabel("Destination:"));
        form.add(destinationField);
        form.add(new JLabel("Departure Date (YYYY-MM-DD):"));
        form.add(dateField);
        form.add(new JLabel());
        form.add(searchButton);

        searchResultsArea.setEditable(false);
        searchResultsArea.setLineWrap(true);
        searchResultsArea.setWrapStyleWord(true);

        searchButton.addActionListener(e -> {
            try {
                LocalDate date = dateField.getText().isBlank() ? null : LocalDate.parse(dateField.getText().trim());
                List<FlightSchedule> flights = controller.searchFlights(
                        originField.getText(),
                        destinationField.getText(),
                        date
                );
                if (flights.isEmpty()) {
                    searchResultsArea.setText("No flights found for the criteria.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (FlightSchedule flight : flights) {
                        sb.append(flight.toDisplayString()).append("\n");
                    }
                    searchResultsArea.setText(sb.toString());
                }
            } catch (DateTimeParseException ex) {
                showError("Please use date format YYYY-MM-DD.");
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(searchResultsArea), BorderLayout.CENTER);
        return wrap(panel);
    }

    private JPanel buildBookPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));

        JTextField flightIdField = new JTextField();
        JTextField passengerNameField = new JTextField();
        JTextField passengerEmailField = new JTextField();
        JComboBox<String> ticketTypeBox = new JComboBox<>(new String[]{"First Class", "Coach", "Economy"});
        JButton bookButton = new JButton("Book Reservation");
        JTextArea outputArea = buildOutputArea();

        form.add(new JLabel("Flight ID:"));
        form.add(flightIdField);
        form.add(new JLabel("Passenger Name:"));
        form.add(passengerNameField);
        form.add(new JLabel("Passenger Email:"));
        form.add(passengerEmailField);
        form.add(new JLabel("Ticket Type:"));
        form.add(ticketTypeBox);
        form.add(new JLabel());
        form.add(bookButton);

        bookButton.addActionListener(e -> {
            try {
                Reservation reservation = controller.bookReservation(
                        flightIdField.getText(),
                        passengerNameField.getText(),
                        passengerEmailField.getText(),
                        ticketTypeBox.getSelectedItem().toString()
                );
                outputArea.setText(
                        "Reservation created successfully.\n" +
                        reservation.toDisplayString() + "\n" +
                        "Use this reservation ID to confirm payment: " + reservation.getReservationId()
                );
                refreshReservations();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        return wrap(panel);
    }

    private JPanel buildConfirmPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));

        JTextField reservationIdField = new JTextField();
        JTextField amountField = new JTextField();
        JButton confirmButton = new JButton("Confirm Reservation / Process Payment");
        JTextArea outputArea = buildOutputArea();

        form.add(new JLabel("Reservation ID:"));
        form.add(reservationIdField);
        form.add(new JLabel("Payment Amount:"));
        form.add(amountField);
        form.add(new JLabel());
        form.add(confirmButton);

        confirmButton.addActionListener(e -> {
            try {
                Reservation reservation = controller.confirmReservation(
                        reservationIdField.getText(),
                        new BigDecimal(amountField.getText().trim())
                );
                outputArea.setText(
                        "Reservation confirmed successfully.\n" +
                        reservation.toDisplayString() + "\n" +
                        "Payment ID: " + reservation.getPayment().getPaymentId()
                );
                refreshReservations();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        return wrap(panel);
    }

    private JPanel buildCancelPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));

        JTextField reservationIdField = new JTextField();
        JButton cancelButton = new JButton("Cancel Reservation");
        JTextArea outputArea = buildOutputArea();

        form.add(new JLabel("Reservation ID:"));
        form.add(reservationIdField);
        form.add(new JLabel());
        form.add(cancelButton);

        cancelButton.addActionListener(e -> {
            try {
                String result = controller.cancelReservation(reservationIdField.getText());
                outputArea.setText(result);
                refreshReservations();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        return wrap(panel);
    }

    private JPanel buildReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        reservationsArea.setEditable(false);
        reservationsArea.setLineWrap(true);
        reservationsArea.setWrapStyleWord(true);
        JButton refreshButton = new JButton("Refresh Reservations");
        refreshButton.addActionListener(e -> refreshReservations());

        panel.add(refreshButton, BorderLayout.NORTH);
        panel.add(new JScrollPane(reservationsArea), BorderLayout.CENTER);
        return wrap(panel);
    }

    private void refreshReservations() {
        StringBuilder sb = new StringBuilder();
        for (Reservation reservation : controller.getReservations()) {
            sb.append(reservation.toDisplayString()).append('\n');
        }
        if (sb.length() == 0) {
            sb.append("No reservations created yet.");
        }
        reservationsArea.setText(sb.toString());
    }

    private JTextArea buildOutputArea() {
        JTextArea area = new JTextArea(10, 60);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private JPanel wrap(JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
