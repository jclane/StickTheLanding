import java.awt.EventQueue;
import javax.swing.JFrame;

public class Main extends JFrame {

  public Main() {
    add(new StickTheLanding());
    setResizable(false);
    pack();

    setTitle("StickTheLanding");
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
        JFrame ex = new Main();
        ex.setVisible(true);
    });
  }

}