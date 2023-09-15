import org.openqa.selenium.WebDriver;

public class Main {

    public static void main(String[] args) {
        init().authenticate();
    }


    public static WhattsAppDriver init(){
        BrowserManager browserManager = new BrowserManager();

        return new WhattsAppDriver(browserManager);
    }

}
