import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class DriverManager {

    private WebDriver driver;


    private static ChromeDriverService service;

    public WebDriver getDriver(boolean headless) {
        try {
            service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File("src/main/resources/chromedriver.exe"))
                    .usingAnyFreePort()
                    .build();
            service.start();

            ChromeOptions options = new ChromeOptions();
            if(headless){
                options.addArguments("--headless");
            }
            options.addArguments("log-level=3");
            options.addArguments("user-data-dir=" + "C:\\Users\\pepij\\AppData\\Local\\Google\\Chrome\\User Data\\Default");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("window-size=1200,1000");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
            options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));

            driver = new RemoteWebDriver(service.getUrl(), options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            return driver;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void quit() {
        driver.quit();
        service.stop();
    }
}
