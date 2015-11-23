import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.*;

import org.openqa.selenium.remote.*;
import com.perfectomobile.selenium.util.EclipseConnector;
import testUtils.*;

public class Example {
	
	public static void main(String[] args) throws MalformedURLException, IOException {

		System.out.println("Run started");
		/**************************************
		 * Example of a mobile Driver
		 **************************************/		
		/*System.out.println("Run started");
		String browserName = "mobileOS";
		DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
		String host = "myHost.perfectomobile.com";		
		capabilities.setCapability("user", "myUser");
		capabilities.setCapability("password", "myPassword");
		capabilities.setCapability("deviceName", "12345");
		
		// Use the automationName capability to define the required framework - Appium (this is the default) or PerfectoMobile.
		// capabilities.setCapability("automationName", "PerfectoMobile");
		
		// Call this method if you want the script to share the devices with the recording plugin.
		setExecutionIdCapability(capabilities);

        RemoteWebDriver driver = new RemoteWebDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
*/
        
		
        //**************************************
		//* Example of a Web Browser Driver
		//**************************************
        
		URL url = new URL("http://localhost:4444/wd/hub");
        //firefox:
		//capabilities = DesiredCapabilities.firefox();
		//chrome:		
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		RemoteWebDriver driver = new RemoteWebDriver(url, capabilities);
		
		
		
        /**************************************
         * Below is a sample of the usage of the linksTest
         *************************************/
		try {
			//Test a Url:
			LinksTest linksTest = new LinksTest(driver);
			ArrayList<ArrayList<String>> listOfLinks = new ArrayList<ArrayList<String>>();
			
			//set pageLoadTimeout
			linksTest.setPageLoadTimeout(5);
			//enable logging
			linksTest.enableLog();
			//test a URL
			listOfLinks = linksTest.linksTest("http://www.google.com");
			//print links
			linksTest.printLinksStatus();
				//or
				//linksTest.printLinksStatusByList(listOfLinks);
			//get only broken links and print them
			linksTest.printLinksStatusByList(linksTest.getBrokenLinks());
			//get only list of links that failed to load within pageLoadTimeout
			linksTest.printLinksStatusByList(linksTest.getLoadFailedLinks());
			//get only list of links that succeeded
			linksTest.printLinksStatusByList(linksTest.getSucceededLinks());
						
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				driver.close();
				
				// In case you want to down the report or the report attachments, do it here.
				// RemoteWebDriverUtils.downloadReport(driver, "pdf", "C:\\test\\report");
				// RemoteWebDriverUtils.downloadAttachment(driver, "video", "C:\\test\\report\\video", "flv");
				// RemoteWebDriverUtils.downloadAttachment(driver, "image", "C:\\test\\report\\images", "jpg");
			} catch (Exception e) {
				e.printStackTrace();
			}
			driver.quit();
		}
		
		System.out.println("Run ended");
	}
	
		
 	private static void setExecutionIdCapability(DesiredCapabilities capabilities) throws IOException {
		EclipseConnector connector = new EclipseConnector();
		String executionId = connector.getExecutionId();
		capabilities.setCapability(EclipseConnector.ECLIPSE_EXECUTION_ID, executionId);
	}
}
