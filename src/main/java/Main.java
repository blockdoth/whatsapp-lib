import org.openqa.selenium.NoSuchElementException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    private static WhatsAppDriver whatsAppDriver;
    private static int responseTimeOut = 50;
    private static int pollDelay = 50;


    public static void main(String[] args) {
        whatsAppDriver = new WhatsAppDriver();
        whatsAppDriver.authenticate();
        try{
            whatsAppDriver.findChat("test"); //De binkie boys
        }catch (NoSuchElementException e){
            System.out.println("Chat not found");
            System.exit(0);
        }

        reply("!GPT","Waarom zijn bananen krom?");

    }

    private static void replyGPT(String trigger, String message){
        while(true){
            System.out.println("Monitoring for new messages");
            String messageGPT = whatsAppDriver.waitForReply("!GPT", false, pollDelay);
            whatsAppDriver.sendMessage(message);

            try {
                Thread.sleep(responseTimeOut);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void reply(String trigger, String message){
        while(true){
            System.out.println("Monitoring for new messages");
            whatsAppDriver.waitForReply("!GPT", false, pollDelay);
            whatsAppDriver.sendMessage(message);

            try {
                Thread.sleep(responseTimeOut);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void rickroll() {
        //load txt file
        List<String> lines = readTxtFile("src/main/resources/texts/rickroll.txt");
        while (true){
            System.out.println("Monitoring for new messages");
            whatsAppDriver.waitForReply("!rickroll",false ,pollDelay);
//            for(String line : lines){
//                whatsAppDriver.sendMessage(line);
//            }
            whatsAppDriver.sendMessage(lines.get(new Random().nextInt(lines.size())));

            try {
                Thread.sleep(responseTimeOut);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static List<String> readTxtFile(String path) {
        List<String> lines = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }
}
