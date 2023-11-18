import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class WhattsAppDriver {


    private BrowserManager browserManager;
    private boolean  savedSesion = false;

    private boolean isAuthenticated = false;


    public WhattsAppDriver(){
        this.browserManager = new BrowserManager();

    }

    public void authenticate(){
        if(browserManager.hasActiveSession()){
            System.out.println("Session found, skipping authentication");
            browserManager.loadActiveSession();
            isAuthenticated = true;
            return;
        }else {
            System.out.println("No session found, authenticating");
            browserManager.authenticate();
            isAuthenticated = true;
        }

    }


    public void findChat(String chatName){
        if(!isAuthenticated){
            throw new RuntimeException("You must authenticate before finding a chat");
        }
        browserManager.findChat(chatName);
    }
}
