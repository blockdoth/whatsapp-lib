import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

;


public class Message {

    private String content;
    private LocalDateTime timeStamp;
    private String sender = "";
    private int messageNumber;

    public Message(String attribute, String text) {
        //Example string [17:04, 11/19/2023] Pepijn van Egmond: test 2
        String sanitizedString = attribute.replace("[", "").replace("]", "");
        String[] splitString = sanitizedString.split(" ");
        String[] time = splitString[0].split(":");
        String[] date = splitString[1].split("/");
        String minute = time[1].replace(",", "");
        String hour = time[0].replace("24", "") ;
        String day = date[0];
        String month = date[1];
        String year = date[2];
        if(day.length() == 1){
            day = "0" + day;
        }
        if(month.length() == 1){
            month = "0" + month;
        }

        timeStamp = LocalDateTime.parse( minute + hour + day + month + year, DateTimeFormatter.ofPattern("[mmHHMMddyyyy]"));
        for(int i = 2;i<splitString.length;i++){
            sender += splitString[i] + " ";
        }
        sender = sender.replace(": ","");
        content = text;
    }


    public String getContent() {
        return content;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String toString() {
        return "[" + timeStamp + "] " + sender + ">\t" + content + " number:\t" + messageNumber;
    }

    public int getMessageNumber() {
        return messageNumber;
    }
    @Override
    public int hashCode() {
        return Objects.hash(content, timeStamp, messageNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return messageNumber == message.messageNumber && Objects.equals(content, message.content) && Objects.equals(timeStamp, message.timeStamp);
    }

    public String getSender() {
        return sender;
    }

    public void addOffSet(int offSet) {
        messageNumber = offSet;
    }
}
