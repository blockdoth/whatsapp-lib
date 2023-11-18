import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class BrowserManager {


    private WebDriver driver;
    private Display display;

    private DriverManager driverManager;

    private String WhattsAppurl = "https://web.whatsapp.com/";


    public BrowserManager(){
        this.driverManager = new DriverManager();
        this.display = new Display();
        this.driver = driverManager.getDriver();

    }
    public void authenticate(){
        display.start();
        driver.get(WhattsAppurl);
        //saveQrCode(qrImage);
        display.displayQRCode(extractQRCode());

        findLoadingElement(By.cssSelector("#app > div > div > div._3HbCE"), " Loading Screen Indicator", 10);
        display.closeDisplay();
        findLoadingElement(By.cssSelector("#side > div._3gYev > div > div > div._2vDPL > div > div > p"), "Authenticated Indicator", 10);
        System.out.println("Authenticated");
        driverManager.saveSession();
    }

    public void loadActiveSession(){
        driver.get(WhattsAppurl);
        findLoadingElement(By.cssSelector("#side > div._3gYev > div > div > div._2vDPL > div > div > p"), "Authenticated Indicator", 10);
        System.out.println("Authenticated");
    }

    public boolean hasActiveSession() {
        return driverManager.hasActiveSession();
    }

    private WebElement findLoadingElement(By selector, String description, int timeout){
        //wait until element is visible
        for (int i = 0; i < timeout; i++) {
            try {
                WebElement element = driver.findElement(selector);
                System.out.println("Element found: " + description);
                return element;
            }catch (NoSuchElementException e){
                //wait 1 second
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        throw new NoSuchElementException("Element not found: " + description);
    }


    private BufferedImage extractQRCode(){
        //wait until element is visible
        while(true){
            try {
                driver.findElement(By.className("_19vUU"));

                WebElement canvas = driver.findElement(By.cssSelector("#app > div > div > div.landing-window > div.landing-main > div > div > div._2I5ox > div > canvas"));
                JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

                // Execute JavaScript to get the canvas data URL
                String dataUrl = (String) jsExecutor.executeScript("return arguments[0].toDataURL('image/png').substring(22);", canvas);
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



    public void findChat(String chatName){
        WebElement searchBox = driver.findElement(By.cssSelector("#side > div._3gYev > div > div > div._2vDPL > div > div > p"));
        searchBox.sendKeys(chatName);
        WebElement chat = findLoadingElement(By.cssSelector("#pane-side > div > div > div > div:nth-child(1) > div > div > div._3Dr46 > div._3ExzF > div._3Dr46 > span"), "Chat",10);
        chat.click();
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

}
