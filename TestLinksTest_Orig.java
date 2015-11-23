
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
public class TestLinksTest_Orig {
	
	static RemoteWebDriver driver;
	
	public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException {

		System.out.println("Run started");
		//Create the driver
		driver = setupDriver(true);
		
		try {
         
			ArrayList<ArrayList<String>> list1 = new ArrayList<ArrayList<String>>();
			LinksTest listLinks1 = new LinksTest(driver);
			
			list1 = listLinks1.linksTest("http://www.google.com");
			list1 = listLinks1.getBrokenLinks();
			list1 = listLinks1.getListOfPageLinks();
			list1 = listLinks1.getLoadFailedLinks();
			list1 = listLinks1.getSucceededLinks();
			listLinks1.printPageLinksStatus();
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.quit();
		}
		
		System.out.println("Run ended");
	}
	
	
		
	private static RemoteWebDriver setupDriver(boolean isMobileDriver) {
		
		URL url;
		DesiredCapabilities capabilities;
		
		try {
			if (isMobileDriver){
				String browserName = "mobileOS";
				capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
				String host = "demo.perfectomobile.com";
				String user = URLEncoder.encode("shirk@perfectomobile.com", "UTF-8");
				String password = URLEncoder.encode("Shitaki1", "UTF-8");
				capabilities.setCapability("description", "ShirNate");
				capabilities.setCapability("platformName", "iOS");
				
				
				//The below code shares the test execution with the Eclipse plug-in, thus enabling sharing the devices. 
				try { 
				    EclipseConnector connector = new EclipseConnector(); 
				    String eclipseExecutionId = connector.getExecutionId();                  
				    capabilities.setCapability("eclipseExecutionId", eclipseExecutionId); 
				} catch (IOException ex) { 
				    ex.printStackTrace(); 
				    return null; 
				}
				
				url = new URL("https://" + user + ':' + password + '@' + host + "/nexperience/wd/hub");
				//create the driver:
		
			}
			else{
				url = new URL("http://localhost:4444/wd/hub");
				//capabilities = DesiredCapabilities.firefox();
				
				
				//chrome
				
				capabilities = DesiredCapabilities.chrome();
			
			}
			return (new RemoteWebDriver(url, capabilities));
			
		} catch (IOException ex) { 
		    ex.printStackTrace(); 
		    return null; 
		}
		
		
	}
	
	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Download the report. 
	 * type - pdf, html, csv, xml
	 * Example: downloadReport(driver, "pdf", "C:\\test\\report");
	 * 
	 */
	private void downloadReport(RemoteWebDriver driver, String type, String fileName) throws IOException {
		try { 
			String command = "mobile:report:download"; 
			Map<String, Object> params = new HashMap<>(); 
			params.put("type", type); 
			String report = (String)driver.executeScript(command, params); 
			File reportFile = new File(fileName + "." + type); 
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(reportFile)); 
			byte[] reportBytes = OutputType.BYTES.convertFromBase64Png(report); 
			output.write(reportBytes); output.close(); 
		} catch (Exception ex) { 
			System.out.println("Got exception " + ex); }
		}

	/**
	 * Download all the report attachments with a certain type.
	 * type - video, image, vital, network
	 * Examples:
	 * downloadAttachment("video", "C:\\test\\video", "flv");
	 * downloadAttachment("image", "C:\\test\\Image", "jpg");
	 */
	private void downloadAttachment(RemoteWebDriver driver, String type, String fileName, String suffix) throws IOException {
		try {
			String command = "mobile:report:attachment";
			boolean done = false;
			int index = 0;

			while (!done) {
				Map<String, Object> params = new HashMap<>();	

				params.put("type", type);
				params.put("index", Integer.toString(index));

				String attachment = (String)driver.executeScript(command, params);
				
				if (attachment == null) { 
					done = true; 
				}
				else { 
					File file = new File(fileName + index + "." + suffix); 
					BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file)); 
					byte[] bytes = OutputType.BYTES.convertFromBase64Png(attachment);	
					output.write(bytes); 
					output.close(); 
					index++; }
			}
		} catch (Exception ex) { 
			System.out.println("Got exception " + ex); 
		}
	}


	private void switchToContext(RemoteWebDriver driver, String context) {
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		Map<String,String> params = new HashMap<String,String>();
		params.put("name", context);
		executeMethod.execute(DriverCommand.SWITCH_TO_CONTEXT, params);
	}

	private String getCurrentContextHandle(RemoteWebDriver driver) {		  
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		String context =  (String) executeMethod.execute(DriverCommand.GET_CURRENT_CONTEXT_HANDLE, null);
		return context;
	}

	private List<String> getContextHandles(RemoteWebDriver driver) {		  
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		List<String> contexts =  (List<String>) executeMethod.execute(DriverCommand.GET_CONTEXT_HANDLES, null);
		return contexts;
	}	
}
