import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class WhatsAppDriver {


    private enum BrowserState {
        LOADING,
        AUTHENTICATED,
        NOT_AUTHENTICATED,
        HOME_SCREEN, IN_CHAT;

    }
    private WebDriver driver;
    private Display display;
    private DriverManager driverManager;
    private String WhatsAppUrl = "https://web.whatsapp.com/";

    private BrowserState state = BrowserState.NOT_AUTHENTICATED;

    private boolean debug = false;

    private LocalDateTime minuteTimeStamp;

    private Set<Message> messageBuffer = new HashSet<>();



    public WhatsAppDriver(){
        this.driverManager = new DriverManager();
        this.driver = driverManager.getDriver(false);
        this.display = new Display();

    }


    public void authenticate(){
        driver.get(WhatsAppUrl);
        try {
            findLoadingElement("Loading Screen Authentication Indicator", By.cssSelector("#app > div > div > div._3HbCE"),10);
            findLoadingElement("Authenticated Indicator", By.cssSelector("#side > div._3gYev > div > div > div._2vDPL > div > div > p"),5);
            state = BrowserState.AUTHENTICATED;
            System.out.println("Authenticated");
            findElement("Home Screen Indicator",By.cssSelector("#app > div > div > div._2Ts6i._3RGKj > header > div._3WByx > div > img"));
            state = BrowserState.HOME_SCREEN;

        } catch (NoSuchElementException e) {
            state = BrowserState.NOT_AUTHENTICATED;
            System.out.println("Not Authenticated, please scan QR");
            display.start();
            display.displayQRCode(extractQRCode());
            findLoadingElement("Authenticated Indicator", By.cssSelector("#side > div._3gYev > div > div > div._2vDPL > div > div > p"),100);
            display.closeDisplay();
        }
    }

    private WebElement findElement(String name, By selector) throws NoSuchElementException{
        if(debug){
            System.out.println("Finding element: " + name);
        }
        return driver.findElement(selector);
    }

    private WebElement findLoadingElement(String description,By selector, int timeout) throws NoSuchElementException{
        //wait until element is visible
        if(debug){
            System.out.println("Waiting for element: " + description);
        }
        for (int i = 0; i < timeout; i++) {
            try {
                WebElement element = driver.findElement(selector);
                if(debug){
                    System.out.println("Element found: " + description);
                }
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
            if(debug){
                System.out.println("QR code extracted");
            }
            return bufferedImage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void findChat(String chatName) throws NoSuchElementException{
        if(state != BrowserState.HOME_SCREEN && state != BrowserState.IN_CHAT){
            throw new RuntimeException("Not on home screen");
        }
        WebElement newChat = findLoadingElement("NewChat",By.cssSelector("#app > div > div > div._2Ts6i._3RGKj > header > div._604FD > div > span > div:nth-child(4) > div > span"),1);
        newChat.click();
        WebElement searchBar = findElement("Search bar",By.cssSelector("#app > div > div > div._2QgSC > div._2Ts6i._3RGKj._318SY > span > div > span > div > div._1tRmd._3wQ5i.o7fBL > div._1EUay > div._2vDPL > div > div.to2l77zo.gfz4du6o.ag5g9lrv.bze30y65.kao4egtt.qh0vvdkp > p"));
        searchBar.sendKeys(chatName);
        //WebElement matchedChat = findLoadingElement("matchedChat",By.cssSelector("#app > div > div > div._2QgSC > div._2Ts6i._3RGKj._318SY > span > div > span > div > div.g0rxnol2.g0rxnol2.thghmljt.p357zi0d.rjo8vgbg.ggj6brxn.f8m0rgwh.gfz4du6o.ag5g9lrv.bs7a17vp.ov67bkzj > div > div > div > div:nth-child(2) > div > div > div._8nE1Y > div.y_sn4 > div > span"),5);

        List<WebElement> matchedChats = driver.findElements(By.cssSelector("[title='" + chatName + "']"));
        if(matchedChats.isEmpty()){
            throw new NoSuchElementException("Chat not found");
        }
//        if(matchedChats.size() > 1){
//            throw new RuntimeException("Multiple chats found");
//        }
        WebElement matchedChat = matchedChats.get(0);
        if(matchedChat.getText().equals(chatName)){
            searchBar.sendKeys(Keys.ENTER);
            WebElement chatTitle;
            try{
                chatTitle = findElement("Chat name",By.cssSelector("#main > header > div._2au8k > div > div > div > span"));
            }catch (NoSuchElementException e){
                chatTitle = findElement("Chat name",By.cssSelector("#main > header > div._2au8k > div._6u0RM > div > span"));
            }
            if(!chatTitle.getText().equals(chatName)){
                throw new NoSuchElementException("Wrong chat found");
            }
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
        messageBox.sendKeys(message + Keys.ENTER);
    }

    public void checkMessageSent(String test) {
        if(state != BrowserState.IN_CHAT){
            throw new RuntimeException("Not in chat");
        }
        List<WebElement> messagesSent = driver.findElements( By.className("_21Ahp"));
        WebElement lastMessage = messagesSent.get(messagesSent.size()-1);
        if(lastMessage.getText().equals(test)){
            System.out.println("Message sent: " + lastMessage.getText());
        }
    }



    public String waitForReply(String trigger, boolean anyTrigger,int pollDelay) {
        if(state != BrowserState.IN_CHAT){
            throw new RuntimeException("Not in chat");
        }

        while (true){
            LocalDateTime lastMessageTime = LocalDateTime.now().withSecond(0).withNano(0);
            if(minuteTimeStamp == null || !minuteTimeStamp.isEqual(lastMessageTime)){
                System.out.println("New minute");
                minuteTimeStamp = lastMessageTime;
                messageBuffer.clear();
            }

            List<WebElement> attributeContainers = driver.findElements(By.cssSelector("div._1BOF7._2AOIt > div > div > div.copyable-text"));
            List<String> messageAttributes;
            List<String> messageText;
            try{
                messageText= attributeContainers.stream().map(webElement -> webElement.findElement(By.cssSelector("span._11JPr> span")).getText()).toList();
                messageAttributes = attributeContainers.stream().map((webElement -> webElement.getAttribute("data-pre-plain-text"))).toList();
            }catch (StaleElementReferenceException | NoSuchElementException e) {
                continue;
            }

            int offSet = 0;
            for (int i = 0; i < messageAttributes.size(); i++) {
                Message newMessage = new Message(messageAttributes.get(i), messageText.get(i));
                if(newMessage.getTimeStamp().equals(lastMessageTime)){
                    newMessage.addOffSet(offSet);
                    offSet++;
                    if(!messageBuffer.contains(newMessage)){
                        messageBuffer.add(newMessage);
                        System.out.println(newMessage);
                        String messageContent = newMessage.getContent();
                        if(messageContent.contains(trigger) || anyTrigger){
                            int option = 1;
                            if(newMessage.getSender().equals("Pepijn van Egmond")){
                                option = 2;
                            }
                            WebElement messageContainer = driver.findElement(By.cssSelector("[role=\"row\"]:nth-child(" +(i+2)+")> div > div > div._1uv-a div._1BOF7._2AOIt > div> div > div.copyable-text > div._21Ahp > span._11JPr> span"));
                            new Actions(driver).moveToElement(messageContainer).perform();
                            WebElement dropUpBox;
                            try {
                                dropUpBox = driver.findElement(By.cssSelector("div._1BOF7._2AOIt > span:nth-child(2) > div > div"));
                            } catch (NoSuchElementException e) {
                                dropUpBox = driver.findElement(By.cssSelector("div._1BOF7._2AOIt > span:nth-child(3) > div > div"));
                            }
                            dropUpBox.click();
                            WebElement selectedOptions = driver.findElement(By.cssSelector("#app > div > span:nth-child(4) > div > ul > div > li:nth-child(" + option +")"));
                            selectedOptions.click();
                            return messageContent.replace(trigger,"");
                        }
                    }
                }
            }
            try {
                Thread.sleep(pollDelay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


