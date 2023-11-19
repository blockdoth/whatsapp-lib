import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    private static WhatsAppDriver whatsAppDriver;
    private static Set<Message> messages = new HashSet<>();
    private static int responseTimeOut = 1000;
    private static int pollDelay = 200;


    public static void main(String[] args) {
        whatsAppDriver = new WhatsAppDriver();
        whatsAppDriver.authenticate();
        whatsAppDriver.findChat("Charlotte Zusje");

        rickroll();
    }

    private static void rickroll() {
        //load txt file
        List<String> lines = readTxtFile("src/main/resources/texts/rickroll.txt");
        while (true){
            System.out.println("Monitoring for new messages");
            whatsAppDriver.waitForNewMessage("!rickroll",messages,false,pollDelay);
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

    private static void monitorChat() {
        while(true){
            System.out.println("Monitoring for new messages");
            whatsAppDriver.waitForNewMessage("!rickroll",messages,false,pollDelay);
            System.out.println("messsage found");
            //whatsAppDriver.sendMessage("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
            try {
                Thread.sleep(responseTimeOut);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
