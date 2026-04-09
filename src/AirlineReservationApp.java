import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;

public class AirlineReservationApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ReservationController controller = new ReservationController(Path.of("data"));
                MainFrame frame = new MainFrame(controller);
                frame.setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to initialize data files: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
