import org.openqa.selenium.NoSuchElementException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    private static WhatsAppDriver whatsAppDriver;
    private static int responseTimeOut = 1000;
    private static int pollDelay = 200;


    public static void main(String[] args) {
        whatsAppDriver = new WhatsAppDriver();
        whatsAppDriver.authenticate();
        try{
            whatsAppDriver.findChat("test");
        }catch (NoSuchElementException e){
            System.out.println("Chat not found");
            System.exit(0);
        }
        reply("test");
    }

    private static void reply(String message){
        while(true){
            System.out.println("Monitoring for new messages");
            whatsAppDriver.replyToMessage("test",message, pollDelay);
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
            whatsAppDriver.waitForNewMessage("!rickroll",pollDelay);
            for(String line : lines){
                whatsAppDriver.sendMessage(line);
            }

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
