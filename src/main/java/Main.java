public class Main {

    public static void main(String[] args) {
        WhatsAppDriver whattsAppDriver = new WhatsAppDriver();
        whattsAppDriver.authenticate();
        whattsAppDriver.findChat("test");

    }


}
