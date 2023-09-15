import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class BrowserManager {


    private WebDriver driver;

    private String WhattsAppurl = "https://web.whatsapp.com/";

    private Display display = new Display();

    public BrowserManager(){
        DriverManager driverManager = new DriverManager();
        driver = driverManager.getDriver();
    }

    public void getQRCode(){
        display.displayQRCode();
        navigateTo(WhattsAppurl);

        BufferedImage qrImage = extractQRCode();
        saveQrCode(qrImage);




        System.out.println("QR Code displayed");
        //Wait until authenticated
        while(true){
            try {
                //Reload the QR after timeout
                try {
                    driver.findElement(By.className("_2XiNU")).click();
                }catch (NoSuchElementException e){
                    Thread.sleep(1000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //window.closeDisplay();
    }

    private void saveQrCode(BufferedImage qrImage) {
        File outputFile = new File("src/main/resources/QRCode.png");

        try {
            // Save the BufferedImage to the specified file as PNG
            ImageIO.write(qrImage, "png", outputFile);
            System.out.println("Image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private BufferedImage extractQRCode(){
        //wait until element is visible
        while(true){
            try {
                driver.findElement(By.className("_19vUU"));

                WebElement canvas = driver.findElement(By.cssSelector("#app > div > div > div.landing-window > div.landing-main > div > div > div._2I5ox > div > canvas"));
                //System.out.println(html.);
                //WebElement canvas = null;
                //convert WebElement to Image

                if (canvas != null) {
                    JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

                    // Execute JavaScript to get the canvas data URL
                    String dataUrl = (String) jsExecutor.executeScript("return arguments[0].toDataURL('image/png').substring(22);", canvas);
                    System.out.println(dataUrl);
                    if (dataUrl != null && !dataUrl.isEmpty()) {
                        // Decode the base64 data into a BufferedImage
                        byte[] imageBytes = java.util.Base64.getDecoder().decode(dataUrl);
                        try {
                            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                            System.out.println("QR code extracted");
                            return bufferedImage;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                        System.out.println("No data url");
                    }
                }
            }catch (NoSuchElementException e){
                //wait 1 second
                System.out.println("Waiting for QR code");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }

    }
    private void navigateTo(String url){
        driver.get(url);
        //check if the page has been loaded
    }
}
