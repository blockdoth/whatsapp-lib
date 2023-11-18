public class Main {

    public static void main(String[] args) {
        WhatsAppDriver whatsAppDriver = new WhatsAppDriver();
        whatsAppDriver.authenticate();
        whatsAppDriver.findChat("Teun");
        whatsAppDriver.findChat("Teunn");
        whatsAppDriver.findChat("Teun");
        //whatsAppDriver.sendMessage("test");

    }


}
