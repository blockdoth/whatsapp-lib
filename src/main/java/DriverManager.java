import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverManager {


    public WebDriver getDriver() {
        ChromeDriver driver = new ChromeDriver();
        return driver;
    }
}
