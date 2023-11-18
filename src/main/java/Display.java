
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class Display {

    JLabel label;
    JFrame frame;

    public void start() {
        //Opens a window
        frame = new JFrame("Image Display Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load an image (change the file path to your image)
        ImageIcon imageIcon = new ImageIcon("src/main/resources/loadingGif.gif");

        // Create a JLabel to display the image
        label = new JLabel();
        label.setIcon(imageIcon);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        frame.add(label);
        frame.pack();
        frame.setVisible(true);


    }

    public void displayQRCode(Image qrCode) {
        //Opens a window with the QRcode using javaFX
        //System.out.println(qrCode.getWidth(null) + "" + qrCode.getHeight(null));
        label.setIcon(new ImageIcon(qrCode));
        System.out.println("QR Code displayed");
    }

    public void closeDisplay() {
        frame.setVisible(false);
    }
}
