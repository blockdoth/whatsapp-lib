import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

;


public class Message {

    private String content;
    private LocalDateTime timeStamp;
    private int messageNumber;

    public Message(String attribute, String text, int messageNumber) {
        this.content = text;
        //Example string [17:04, 11/19/2023] Pepijn van Egmond: test 2
        String sanitizedString = attribute.substring(0,19).replace("[","").replace("]","").replace("24","00").stripTrailing();

        try{
            try{
                timeStamp = LocalDateTime.parse(sanitizedString, DateTimeFormatter.ofPattern("[HH:mm, MM/dd/yyyy]"));
            } catch (DateTimeParseException x){
                timeStamp = LocalDateTime.parse(sanitizedString, DateTimeFormatter.ofPattern("[HH:mm, MM/d/yyyy]"));
            }
        }catch (DateTimeParseException e){
            try{
                timeStamp = LocalDateTime.parse(sanitizedString, DateTimeFormatter.ofPattern("[HH:mm, M/dd/yyyy]"));
            } catch (DateTimeParseException x){
                timeStamp = LocalDateTime.parse(sanitizedString, DateTimeFormatter.ofPattern("[HH:mm, M/d/yyyy]"));
            }
        }
        this.messageNumber = messageNumber;
    }


    public String getContent() {
        return content;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String toString() {
        return "[" + timeStamp + "]\t" + content;
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
}
