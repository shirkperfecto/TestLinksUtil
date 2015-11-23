package testUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**************************************************************************
 * The Class LinksTest is a utility to test the links on a given web page.
 * This utility collects all links on a given url, and tests them one by one.
 * It tries navigating to each of the links and checks their status.
 * The test utility returns a list of links and their status in a given structure - 
 * 
 * @return ArrayList<ArrayList<String>> list of links
 * @structure each link is built from three strings - 
 * String 0 - The link
 * String1  - The links' Name (in case exists)
 * String2 -  The links' status
 *
 * @Status:
 * @PageLoadSuccess the link page loaded successfully
 * @PageLoadFailure the link page didn't load within the given pageLoadTimeout
 * @BrokenLink the link is broken (example - returns 404)
 * 
 ***********************************************************************************/
public class LinksTest{
	
	/** The driver. */
	private RemoteWebDriver driver;
	
	/** The Log File. */
	private Log Log;
	
	/** The URL to collect its links. */
	private String url;
	
	/** The page errors xpath. */
	private static String pageErrorsXpath;
	
	/** The page load timeout. */
	private long pageLoadTimeout;
	
	/** The given URL page links.
	* 0 -The link
	* 1 -The links Name
	* 2 -The links' status*/
	private ArrayList<ArrayList<String>> pageLinks;
	
	/**********************************************************
	 * Instantiates a new links test.
	 * (by default log files will be created)
	 *
	 * @param driver the driver
	 ***********************************************************/
	public LinksTest(RemoteWebDriver driver) {
		this(driver,"", true);
	}
	
	/***********************************************************
	 * Instantiates a new links test.
	 * (by default log files will be created
	 *
	 * @param driver the driver
	 * @param url the base url to test its links
	 ***********************************************************/
	public LinksTest(RemoteWebDriver driver, String url) {
		
		this(driver,url,true);
	}
	
	/***********************************************************
	 * Instantiates a new links test.
	 *
	 * @param driver the driver
	 * @param url the base url to test its links
	 * @param logging enable or disable log file
	 ***********************************************************/
	public LinksTest(RemoteWebDriver driver, String url, boolean logging) {
		
		this.driver = driver;
		this.pageLinks = new ArrayList<ArrayList<String>>();
		
		//set timeouts:
		setPageLoadTimeout(Constants.pageLoadTimeout);
		
		//set the base URL
		setUrl(url);
		
		//initialize page errors
		buildPageErrorXpath();
		
		//initialize the Log
		Log= new Log();
		if (logging)
			enableLog();
		else
			disableLog();
		
	}

	
	/*******************************************************************************
	 * EnableLog
	 * 		This method enables the logging. Two log files are created by default - 
	 * 		@see test-output/logs/execution.log General Log - concatenates messages 
	 * 			  to end of file 
	 * 		@see test-output/logs/linksTest/ specific execution log - name includes timestamp
	 *******************************************************************************/
	public void enableLog()
	{
		Logger.getRootLogger().setLevel(Level.DEBUG);
		System.out.println("Log Enabled");
	}
	
	/********************************************************************************
	 * Disable
	 * 		This method disables the logging. The next two log files will be disabled-
	 * 		@see test-output/logs/execution.log General Log - concatenates messages to end of file
	 * 		@see test-outpu/logs/linksTest/ specific execution log - name includes timestamp
	 *********************************************************************************/
	public void disableLog(){
		Logger.getRootLogger().setLevel(Level.FATAL);
		System.out.println("Log Disbaled");
	}

	/*********************************************************************************
	 * Sets the page load timeout.
	 *
	 * @param pageLoadTimeout the new page load timeout
	 * @default see Constants.pageLoadTimeout variable
	 ********************************************************************************/
	public void setPageLoadTimeout(long pageLoadTimeout) {
		this.pageLoadTimeout = pageLoadTimeout;
		this.driver.manage().timeouts().pageLoadTimeout(this.pageLoadTimeout, TimeUnit.SECONDS);
		this.driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
	}
	
	/**************************************************************
	 * Sets the base url to collect its links
	 *
	 * @param url the url to collect its links
	 *************************************************************/
	public void setUrl(String url) {
		this.pageLinks.clear();
		this.url = url;
	}

	/***************************************************************
	 * the list of page links.
	 *
	 * @return the list of page links and their status.
	 * See list of statuses below: 
	 * 
	 * @PageLoadSuccess the link page loaded successfully
	 * @PageLoadFailure the link page didnt load within the giver pageLoadTimeout
	 * @BrokenLink the link is broken (example - returns 404)
	 ****************************************************************/
	public ArrayList<ArrayList<String>> getListOfPageLinks(){
		return pageLinks;
	}
	
	/***************************************************************
	 * Navigates to a given url and collect its links
	 *
	 * @return the array list of links 
	 ***************************************************************/
	private ArrayList<ArrayList<String>> buildPageLinks(){
		
		//load url:
		try {
			this.driver.get(this.url);
			
		} catch (Exception e) {
			Log.fatal("Failed to open URL: " + this.url);
			Log.fatal("Exception: " + e.getMessage());
			return null;
		}
		//parse page and get all links:
		try {
			//get all links on the page:
			 Document doc = Jsoup.parse(this.driver.getPageSource());
			 Elements links = doc.select("a[href]");
			 String href,text;
			 
			 for (Element link : links) {
				 href = link.attr("abs:href");
				 if (href.isEmpty())
					 href = this.url+"/"+link.attr("href");
				 text = link.text();
				
				 ArrayList<String> singlePageLink = new ArrayList<String>();
				 singlePageLink.add(href);
				 singlePageLink.add(text);
											 
	        	if (text.isEmpty()){
	        		Element img = link.select("img[src]").first();
	        		if(img!=null){
	        			singlePageLink.set(1,img.attr("src"));
	        		}
	        		
	        	}
	        	
	        	pageLinks.add(singlePageLink);
	        	
			 }
					
					
		} catch (Exception e) {
			Log.fatal("Failed to get links from Page: " + this.url);
			Log.fatal("Exception: " + e.getMessage());
			return null;
		}
		
		return pageLinks;
	}
	
	/****************************************************************
	 * This method collects links from a given url and tests them.
	 *
	 * @param url the base url to collect its links
	 * @return the array list of links and their status
	 * @throws IOException Signals that an I/O exception has occurred.
	 * 
	 * @PageLoadSuccess the link page loaded successfully
	 * @PageLoadFailure the link page didnt load within the giver pageLoadTimeout
	 * @BrokenLink the link is broken (example - returns 404)
	 ****************************************************************/
	public ArrayList<ArrayList<String>> linksTest(String url) throws IOException{
		return linksTest(url, this.pageLoadTimeout);
	}
	
	/****************************************************************
	 * This method collects links from a previously given url and tests them.
	 *
	 * @return the array list of links and their status
	 * @throws IOException Signals that an I/O exception has occurred.
	 *
	 * @PageLoadSuccess the link page loaded successfully
	 * @PageLoadFailure the link page didnt load within the giver pageLoadTimeout
	 * @BrokenLink the link is broken (example - returns 404)
	 ****************************************************************/
	public ArrayList<ArrayList<String>> linksTest() throws IOException{
		return linksTest(this.url, this.pageLoadTimeout);
	}
	
	/****************************************************************
	 * This method collects links from a url and tests them.
	 *
	 * @param url the base url to collect its links
	 * @param pageLoadTimeout the pageLoadTimeOut for the links tested (default:Constants.pageLoadTimeout)
	 * @return the array list of links and their status
	 * @throws IOException Signals that an I/O exception has occurred.
	 * 
	 * @PageLoadSuccess the link page loaded successfully
	 * @PageLoadFailure the link page didnt load within the giver pageLoadTimeout
	 * @BrokenLink the link is broken (example - returns 404)
	 ****************************************************************/
	public ArrayList<ArrayList<String>> linksTest(String url, long pageLoadTimeout) throws IOException{
		
		//initialize logs
		Log.setTestLog("testLinks");
		
		Log.debug("*********************************************************************************************");
		setUrl(url);
		setPageLoadTimeout(pageLoadTimeout);
		
		if (this.url.isEmpty()){
			Log.error("URL is empty - Cannot run testLinks ");
			Log.error("******End test due to an error");
			Log.debug("*********************************************************************************************");
			
			return null;
		}
		
		if (buildPageLinks() == null){
			Log.error("******End test due to an error or No Links Found");
			Log.debug("*********************************************************************************************");
			return null;
		}
				
		//check for broken links:
		Log.debug("******Base URL "+ this.url);
		Log.debug("******Links Found: "+pageLinks.size());
		Log.debug("******Page Load timeout is "+ this.pageLoadTimeout);
		Log.debug("******Begin Test:");
		for (ArrayList<String> singlePageLink: pageLinks){
			try {
				//this.driver.navigate().to(singlePageLink.get(0));
				this.driver.get(singlePageLink.get(0));
				Thread.sleep(500);
			} catch (Exception e) {
				singlePageLink.add(Constants.LinkLoadErrorMsg);
				Log.warn(singlePageLink.get(1) + " : " +singlePageLink.get(0) + " ==> " +Constants.LinkLoadErrorMsg + " ==> Link to screenshot: "+Constants.takeScreenshot(this.driver,"Links_Test"));
				continue;
			}
			
			try {
				this.driver.findElement(By.xpath(pageErrorsXpath));
				singlePageLink.add(Constants.LinkBrokenMsg);
				Log.error(singlePageLink.get(1) + " : " +singlePageLink.get(0) + " ==> " +Constants.LinkBrokenMsg + " ==> Link to screenshot: "+Constants.takeScreenshot(this.driver,"Links_Test"));
				
				
			} catch (Exception e) {
				if (this.driver.getTitle().isEmpty()){
					singlePageLink.add(Constants.LinkBrokenMsg);
					Log.error(singlePageLink.get(1) + " : " +singlePageLink.get(0) + " ==> " +Constants.LinkBrokenMsg+" ==> Link to screenshot: "+Constants.takeScreenshot(this.driver,"Links_Test"));
				}
				else {
					singlePageLink.add(Constants.LinkSuccessMsg);
					Log.debug(singlePageLink.get(1) + " : " +singlePageLink.get(0) + " ==> " +Constants.LinkSuccessMsg);
				}
			}
			
		}
		
		Log.debug("*************End Of TestLinks on: " + this.url+ "***************");
		Log.debug("*********************************************************************************************");
		return this.pageLinks;
	}
		
	/*************************************************************
	 * Gets the broken links. 
	 * to the console and log(in case enabled)
	 *
	 * @return the array of broken links
	 * @structureOfReturnedArray
	 * String0 - The link
	 * String1  - The links' Name (in case exists)
	 * String2 -  The links' status - Broken Link
	 ************************************************************/
	public ArrayList<ArrayList<String>> getBrokenLinks(){
		
		if (pageLinks.isEmpty() || pageLinks.size()==2){
			return null;
		} 
		
		ArrayList<ArrayList<String>> newList = new ArrayList<ArrayList<String>>();
		
		for (ArrayList<String> singleLink : pageLinks){
			if (singleLink.get(2).equals(Constants.LinkBrokenMsg)){
				newList.add(singleLink);
			}
				
		}
		
		return newList;
	}
	
	/*************************************************************
	 * Gets the list of links that failed loading within the given pageLoadTimeout.
	 *
	 * @return the array of links
	 * @structureOfReturnedArray
	 * String0 - The link
	 * String1  - The links' Name (in case exists)
	 * String2 -  The links' status - Failed to Load
	 ************************************************************/
	public ArrayList<ArrayList<String>> getLoadFailedLinks(){
		
		if (pageLinks.isEmpty() || pageLinks.size()==2){
			return null;
		} 
		
		ArrayList<ArrayList<String>> newList = new ArrayList<ArrayList<String>>();
		
		for (ArrayList<String> singleLink : pageLinks){
			if (singleLink.get(2).equals(Constants.LinkLoadErrorMsg)){
				newList.add(singleLink);
			}
				
		}
		
		return newList;
	}
	
	/*************************************************************
	 * Gets the list of links that succeeded loading
	 *
	 * @return the array of links
	 * @structureOfReturnedArray
	 * String0 - The link
	 * String1  - The links' Name (in case exists)
	 * String2 -  The links' status - Succeeded Loading
	 ************************************************************/
	public ArrayList<ArrayList<String>> getSucceededLinks(){
		
		if (pageLinks.isEmpty() || pageLinks.size()==2){
			return null;
		} 
		
		ArrayList<ArrayList<String>> newList = new ArrayList<ArrayList<String>>();
		
		for (ArrayList<String> singleLink : pageLinks){
			if (singleLink.get(2).equals(Constants.LinkSuccessMsg)){
				newList.add(singleLink);
			}
				
		}
		
		return newList;
	}
	
	
	/************************************
	 * Prints the pages' links  and their status.
	 *******************************/
	public void printLinksStatus(){
		printLinksStatusByList(this.pageLinks);
	}
	
	/************************************************
	 * Prints a given list of links and their status.
	 ************************************************/
	public void printLinksStatusByList(ArrayList<ArrayList<String>> specificList){
		if (specificList==null || specificList.isEmpty() || specificList.size()==2){
			return;
		} 
		System.out.println(
				"********************************LINKS LIST******************************************");
		Log.debug(
				"********************************LINKS LIST******************************************");
		System.out.println(
				"                     URL:"+this.url+" pageLoadTimeout: "+ this.pageLoadTimeout);
		Log.debug(
				"                     URL:"+this.url+" pageLoadTimeout: "+ this.pageLoadTimeout);
		
		System.out.println(
				"**************************************************************************");
		System.out.println(
				"**************************************************************************");
		for (ArrayList<String> singleAddress: specificList){
			if (singleAddress.size()>2) {
				System.out.println("***** " + singleAddress.get(1) + " ***** " + singleAddress.get(0)
					+ " ; " + singleAddress.get(2));
				Log.debug("***** " + singleAddress.get(1) + " ***** " + singleAddress.get(0)
				+ " ; " + singleAddress.get(2));
				
			}else{
				System.out.println("***** " + singleAddress.get(1) + " ***** " + singleAddress.get(0));
				Log.debug("***** " + singleAddress.get(1) + " ***** " + singleAddress.get(0));
				
			}
			
		}
		System.out.println(
				"************************************************************************************");
		Log.debug(
				"************************************************************************************");
	
	}
	
	/*********************************************************
	 * Build the xpath for all relevant broken links errors.
	 * to add an error, add it to Constant file
	 ********************************************************/
	private void buildPageErrorXpath(){
		String xpath = "//title[";
		int counter = 0;
		
		for (String msg: Constants.loadPageErrors){
			++counter;
			if (counter!=1){
				xpath = xpath+" or ";
			}
			xpath = xpath+ "contains(text(),'" + msg + "')";
		}
		xpath = xpath + "]";
		pageErrorsXpath = xpath;
		
		//risky xpath test
		//pageErrorsXpath = "//title[starts-with(text(),'4') or starts-with(text(),'5')]";
	}
	
	
}
