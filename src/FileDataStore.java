import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDataStore {
    private static final String FLIGHTS_FILE = "flights.csv";
    private static final String RESERVATIONS_FILE = "reservations.csv";
    private static final String PAYMENTS_FILE = "payments.csv";

    private final Path dataDirectory;

    public FileDataStore(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void initialize() throws IOException {
        Files.createDirectories(dataDirectory);
        Path flightsPath = dataDirectory.resolve(FLIGHTS_FILE);
        if (!Files.exists(flightsPath)) {
            seedFlights(flightsPath);
        }
        Path reservationsPath = dataDirectory.resolve(RESERVATIONS_FILE);
        if (!Files.exists(reservationsPath)) {
            Files.writeString(reservationsPath,
                    "reservationId,passengerName,passengerEmail,flightId,ticketType,ticketNumber,createdAt,status,paymentId,refundAmount\n",
                    StandardCharsets.UTF_8);
        }
        Path paymentsPath = dataDirectory.resolve(PAYMENTS_FILE);
        if (!Files.exists(paymentsPath)) {
            Files.writeString(paymentsPath,
                    "paymentId,reservationId,amount,paidAt\n",
                    StandardCharsets.UTF_8);
        }
    }

    private void seedFlights(Path flightsPath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("id,flightNumber,origin,destination,departure,arrival,baseFare,capacity\n");
        sb.append("F001,AC101,Vancouver,Toronto,2026-04-20T08:00,2026-04-20T15:30,450.00,120\n");
        sb.append("F002,AC205,Calgary,Montreal,2026-04-21T09:15,2026-04-21T15:00,390.00,100\n");
        sb.append("F003,WS310,Vancouver,Calgary,2026-04-22T12:20,2026-04-22T14:40,180.00,90\n");
        sb.append("F004,AC404,Toronto,Vancouver,2026-05-10T16:00,2026-05-10T19:10,470.00,120\n");
        sb.append("F005,WS550,Montreal,Halifax,2026-05-12T11:45,2026-05-12T13:20,150.00,80\n");
        Files.writeString(flightsPath, sb.toString(), StandardCharsets.UTF_8);
    }

    public List<FlightSchedule> loadFlights() throws IOException {
        List<FlightSchedule> flights = new ArrayList<>();
        for (String line : Files.readAllLines(dataDirectory.resolve(FLIGHTS_FILE), StandardCharsets.UTF_8)) {
            if (line.startsWith("id,")) continue;
            if (line.isBlank()) continue;
            String[] parts = line.split(",");
            flights.add(new FlightSchedule(
                    parts[0], parts[1], parts[2], parts[3],
                    LocalDateTime.parse(parts[4]), LocalDateTime.parse(parts[5]),
                    new BigDecimal(parts[6]), Integer.parseInt(parts[7])
            ));
        }
        return flights;
    }

    public List<Payment> loadPayments() throws IOException {
        List<Payment> payments = new ArrayList<>();
        for (String line : Files.readAllLines(dataDirectory.resolve(PAYMENTS_FILE), StandardCharsets.UTF_8)) {
            if (line.startsWith("paymentId,")) continue;
            if (line.isBlank()) continue;
            String[] parts = line.split(",");
            payments.add(new Payment(parts[0], parts[1], new BigDecimal(parts[2]), LocalDateTime.parse(parts[3])));
        }
        return payments;
    }

    public List<Reservation> loadReservations(List<FlightSchedule> flights, List<Payment> payments) throws IOException {
        Map<String, FlightSchedule> flightsById = new HashMap<>();
        for (FlightSchedule flight : flights) {
            flightsById.put(flight.getId(), flight);
        }
        Map<String, Payment> paymentsById = new HashMap<>();
        for (Payment payment : payments) {
            paymentsById.put(payment.getPaymentId(), payment);
        }

        List<Reservation> reservations = new ArrayList<>();
        for (String line : Files.readAllLines(dataDirectory.resolve(RESERVATIONS_FILE), StandardCharsets.UTF_8)) {
            if (line.startsWith("reservationId,")) continue;
            if (line.isBlank()) continue;
            String[] parts = line.split(",", -1);
            Passenger passenger = new Passenger(parts[1], parts[2]);
            FlightSchedule flight = flightsById.get(parts[3]);
            Ticket ticket = createTicket(parts[4], parts[5]);
            Payment payment = parts[8].isBlank() ? null : paymentsById.get(parts[8]);
            Reservation reservation = new Reservation(
                    parts[0],
                    passenger,
                    flight,
                    ticket,
                    LocalDateTime.parse(parts[6]),
                    ReservationStatus.valueOf(parts[7]),
                    payment,
                    parts[9].isBlank() ? BigDecimal.ZERO : new BigDecimal(parts[9])
            );
            reservations.add(reservation);
        }
        return reservations;
    }

    public void saveReservations(List<Reservation> reservations) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("reservationId,passengerName,passengerEmail,flightId,ticketType,ticketNumber,createdAt,status,paymentId,refundAmount\n");
        for (Reservation r : reservations) {
            sb.append(r.getReservationId()).append(',')
                    .append(safe(r.getPassenger().getName())).append(',')
                    .append(safe(r.getPassenger().getEmail())).append(',')
                    .append(r.getFlight().getId()).append(',')
                    .append(ticketCode(r.getTicket())).append(',')
                    .append(r.getTicket().getTicketNumber()).append(',')
                    .append(r.getCreatedAt()).append(',')
                    .append(r.getStatus()).append(',')
                    .append(r.getPayment() == null ? "" : r.getPayment().getPaymentId()).append(',')
                    .append(r.getRefundAmount().toPlainString())
                    .append('\n');
        }
        Files.writeString(dataDirectory.resolve(RESERVATIONS_FILE), sb.toString(), StandardCharsets.UTF_8);
    }

    public void savePayments(List<Payment> payments) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("paymentId,reservationId,amount,paidAt\n");
        for (Payment p : payments) {
            sb.append(p.getPaymentId()).append(',')
                    .append(p.getReservationId()).append(',')
                    .append(p.getAmount().toPlainString()).append(',')
                    .append(p.getPaidAt())
                    .append('\n');
        }
        Files.writeString(dataDirectory.resolve(PAYMENTS_FILE), sb.toString(), StandardCharsets.UTF_8);
    }

    private Ticket createTicket(String code, String ticketNumber) {
        return switch (code) {
            case "FIRST" -> new FirstClassTicket(ticketNumber);
            case "COACH" -> new CoachTicket(ticketNumber);
            default -> new EconomyTicket(ticketNumber);
        };
    }

    private String ticketCode(Ticket ticket) {
        if (ticket instanceof FirstClassTicket) return "FIRST";
        if (ticket instanceof CoachTicket) return "COACH";
        return "ECONOMY";
    }

    private String safe(String value) {
        return value.replace(",", " ");
    }
}
