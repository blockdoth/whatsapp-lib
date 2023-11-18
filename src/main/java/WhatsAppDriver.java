import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class WhatsAppDriver {

    private enum BrowserState {
        LOADING,
        AUTHENTICATED,
        NOT_AUTHENTICATED,
        HOME_SCREEN, IN_CHAT
    }
    private WebDriver driver;
    private Display display;

    private DriverManager driverManager;

    private String WhatsAppUrl = "https://web.whatsapp.com/";

    private BrowserState state = BrowserState.NOT_AUTHENTICATED;

    private int timeout = 10;

    public WhatsAppDriver(){
        this.driverManager = new DriverManager();
        this.driver = driverManager.getDriver(false);
        this.display = new Display();

    }
    public void checkAuthenticated() {
        try {
            findLoadingElement(By.cssSelector("#app > div > div > div._3HbCE"), "Loading Screen Authentication Indicator", timeout);
            findLoadingElement(By.cssSelector("#side > div._3gYev > div > div > div._2vDPL > div > div > p"), "Authenticated Indicator", timeout);
            state = BrowserState.AUTHENTICATED;
        } catch (NoSuchElementException e) {
            System.out.println("Not Authenticated");
            state = BrowserState.NOT_AUTHENTICATED;
        }
        System.out.println("Authenticated");
    }

    public void authenticate(){
        driver.get(WhatsAppUrl);
        checkAuthenticated();
        if(state == BrowserState.NOT_AUTHENTICATED){
            display.start();
            display.displayQRCode(extractQRCode());
            checkAuthenticated();
            display.closeDisplay();
        }
        if(state == BrowserState.AUTHENTICATED){
            findLoadingElement(By.cssSelector("#app > div > div > div._2Ts6i._3RGKj > header > div._3WByx > div > img"), "Home Screen Indicator", timeout);
            state = BrowserState.HOME_SCREEN;
        }
    }


    private WebElement findLoadingElement(By selector, String description, int timeout) throws NoSuchElementException{
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

        findLoadingElement(By.className("_19vUU"), "QR Code", 100);
        WebElement canvas = driver.findElement(By.cssSelector("#app > div > div > div.landing-window > div.landing-main > div > div > div._2I5ox > div > canvas"));
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        String dataUrl = (String) jsExecutor.executeScript("return arguments[0].toDataURL('image/png').substring(22);", canvas);
        if (dataUrl == null || dataUrl.isEmpty()) {
            throw new NoSuchElementException("QR code data not readable");
        }
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



    public void findChat(String chatName){
        if(state != BrowserState.HOME_SCREEN){
            throw new RuntimeException("Not on home screen");
        }
        WebElement searchBox = driver.findElement(By.cssSelector("#side > div._3gYev > div > div > div._2vDPL > div > div > p"));
        searchBox.sendKeys(chatName);
        WebElement matchedText = findLoadingElement(By.cssSelector("#pane-side > div:nth-child(1) > div > div > div:nth-child(19) > div > div > div > div._8nE1Y > div.y_sn4 > div._21S-L > span > span"), "Chat",timeout);
        if(!matchedText.getText().equals(chatName)){
            throw new NoSuchElementException("Chat not found");
        }else {
            matchedText.click();
            state = BrowserState.IN_CHAT;
        }
    }
}
