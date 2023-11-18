import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

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


    public void authenticate(){
        driver.get(WhatsAppUrl);
        try {
            findLoadingElement("Loading Screen Authentication Indicator", By.cssSelector("#app > div > div > div._3HbCE"),10);
            findLoadingElement("Authenticated Indicator", By.cssSelector("#side > div._3gYev > div > div > div._2vDPL > div > div > p"),3);
            state = BrowserState.AUTHENTICATED;
            System.out.println("Authenticated");
        } catch (NoSuchElementException e) {
            state = BrowserState.NOT_AUTHENTICATED;
            System.out.println("Not Authenticated");
        }
        if(state == BrowserState.NOT_AUTHENTICATED){
            display.start();
            display.displayQRCode(extractQRCode());
            findLoadingElement("Authenticated Indicator", By.cssSelector("#side > div._3gYev > div > div > div._2vDPL > div > div > p"),100);
            display.closeDisplay();
        }
        if(state == BrowserState.AUTHENTICATED){
            findElement("Home Screen Indicator",By.cssSelector("#app > div > div > div._2Ts6i._3RGKj > header > div._3WByx > div > img"));
            state = BrowserState.HOME_SCREEN;
        }
    }

    private WebElement findElement(String name, By selector) throws NoSuchElementException{
        System.out.println("Finding element: " + name);
        return driver.findElement(selector);
    }

    private WebElement findLoadingElement(String description,By selector, int timeout) throws NoSuchElementException{
        //wait until element is visible
        System.out.println("Waiting for element: " + description);
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

        findLoadingElement("QR Code", By.className("_19vUU"), 100);
        WebElement canvas = findElement("canvas", By.cssSelector("#app > div > div > div.landing-window > div.landing-main > div > div > div._2I5ox > div > canvas"));
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
        if(state != BrowserState.HOME_SCREEN && state != BrowserState.IN_CHAT){
            throw new RuntimeException("Not on home screen");
        }
        WebElement newChat = findLoadingElement("NewChat",By.cssSelector("#app > div > div > div._2Ts6i._3RGKj > header > div._604FD > div > span > div:nth-child(4) > div > span"),1);
        newChat.click();
        WebElement searchBar = findElement("Search bar",By.cssSelector("#app > div > div > div._2QgSC > div._2Ts6i._3RGKj._318SY > span > div > span > div > div._1tRmd._3wQ5i.o7fBL > div._1EUay > div._2vDPL > div > div.to2l77zo.gfz4du6o.ag5g9lrv.bze30y65.kao4egtt.qh0vvdkp > p"));
        searchBar.sendKeys(chatName);
        //WebElement matchedChat = findLoadingElement("matchedChat",By.cssSelector("#app > div > div > div._2QgSC > div._2Ts6i._3RGKj._318SY > span > div > span > div > div.g0rxnol2.g0rxnol2.thghmljt.p357zi0d.rjo8vgbg.ggj6brxn.f8m0rgwh.gfz4du6o.ag5g9lrv.bs7a17vp.ov67bkzj > div > div > div > div:nth-child(2) > div > div > div._8nE1Y > div.y_sn4 > div > span"),5);

        List<WebElement> matchedChats = driver.findElements(By.xpath("//*[@title='" + chatName + "']"));
        if(matchedChats.size() == 0){
            throw new NoSuchElementException("Chat not found");
        }
        WebElement matchedChat = matchedChats.get(0);

        if(matchedChat.getText().equals(chatName)){
            matchedChat.click();
            //WebElement title = findElement("ChatTitle", By.cssSelector("#main > header > div._2au8k > div._6u0RM > div > span"));
//            if(!title.getText().equals(chatName)){
//                throw new RuntimeException("Chat name does not match");
//            }
            state = BrowserState.IN_CHAT;
        }else {
            throw new NoSuchElementException("Chat not found");
        }
    }

    public void sendMessage(String message){
        if(state != BrowserState.IN_CHAT){
            throw new RuntimeException("Not in chat");
        }
        WebElement messageBox = findElement("Message box",By.cssSelector("#main > footer > div._2lSWV._3cjY2.copyable-area > div > span:nth-child(2) > div > div._1VZX7 > div._3Uu1_ > div > div.to2l77zo.gfz4du6o.ag5g9lrv.bze30y65.kao4egtt > p"));
        messageBox.sendKeys(message);
        WebElement sendButton = findElement("Send button",By.cssSelector("#main > footer > div._2lSWV._3cjY2.copyable-area > div > span:nth-child(2) > div > div._1VZX7 > div._2xy_p._3XKXx"));
        sendButton.click();
    }
}
