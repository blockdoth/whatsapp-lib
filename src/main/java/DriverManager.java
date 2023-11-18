import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class DriverManager {

    private WebDriver driver;
    private boolean headless = false;

    public WebDriver getDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        //options.addArguments("window-size=1200,1000");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.addArguments("--incognito");
        options.addArguments("user-data-dir=" + "src/main/resources/userData");

        if(headless){
            options.addArguments("--headless");
        }


        this.driver = new ChromeDriver(options);
        return driver;
    }

    public boolean hasActiveSession() {
        return false;
    }

    public void saveSession(){


    }


}
