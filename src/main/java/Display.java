import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.nio.IntBuffer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Display extends Application {

    private Stage stage;

    private ImageView imageView = new ImageView();

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        // Set the stage (window) title
        stage.setTitle("QR Code Display");

        Image loadingGif = new Image("file:src/main/resources/loading.gif");
        //Image qrCode = new Image("file:src/main/resources/QRCode.png");


        imageView.setImage(loadingGif);

        Group root = new Group(imageView);

        Scene scene = new Scene(root, 264, 264); // Set the scene dimensions

        stage.setScene(scene);
        stage.show();
        System.out.println("GUI started");
        //waitForQrCode();
    }

    private void waitForQrCode() {
        while(true){
            try{
                File file = new File("file:src/main/resources/QRCode.png");
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            }catch (Exception e){
                System.out.println("No QR Code found");
            }
        }
    }

    public void displayQRCode() {
        //Opens a window with the QRcode using javaFX

        launch();
        System.out.println("QR Code displayed");
    }

    public void closeDisplay() {
        //Closes a window
    }
}
