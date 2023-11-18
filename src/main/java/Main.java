import org.openqa.selenium.WebDriver;

public class Main {

    public static void main(String[] args) {
        WhattsAppDriver whattsAppDriver = new WhattsAppDriver();
        whattsAppDriver.authenticate();
        whattsAppDriver.findChat("test");

    }


}
