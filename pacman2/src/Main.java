import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main{

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGame);
    }

    private static void createAndShowGame() {
        JFrame frame = new JFrame("Pac Man");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        pacmanGame.requestFocusInWindow();
    }
}
