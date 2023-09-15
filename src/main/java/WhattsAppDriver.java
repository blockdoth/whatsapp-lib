import org.openqa.selenium.WebDriver;

public class WhattsAppDriver {



    private BrowserManager browserManager;
    private boolean  isAuthenticated = false;


    public WhattsAppDriver(BrowserManager browserManager){
        this.browserManager = browserManager;
    }

    public void authenticate(){
        browserManager.getQRCode();
    }
}
