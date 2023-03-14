package com.codezee.app.frameworkLibrary.Core;

import com.codezee.app.DataHolder.DataHolder;
import com.codezee.app.StepFailureException.StepFailureException;
import com.codezee.app.frameworkLibrary.ConfigHelper.ConfigHelper;
import com.codezee.app.frameworkLibrary.ConfigHelper.LoggerHelper;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;

@Component
@Scope("cucumber-glue")
public class CoreLibrary {

	WebDriver driver;
	@Autowired
	private ConfigHelper configHelper;

	@Autowired
	private DataHolder dataHolder;

	private static String oldTab;

	private static Logger log = LoggerHelper.getLogInstance(CoreLibrary.class);

	public WebDriver getDriverInstance() {
		String browser = configHelper.readProps("browser");
		switch (browser) {
		case "chrome":
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\src\\test\\java\\resources\\chromedriver.exe");
			driver = new ChromeDriver(getChromeOptions());
			break;
		case "firefox":
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\src\\test\\java\\resources\\chromedriver.exe");
			driver = new ChromeDriver(getChromeOptions());
			break;
		default:
			System.out.println("Invalid Browser");
		}
		return driver;
	}

	private ChromeOptions getChromeOptions() {
		ChromeOptions options = new ChromeOptions();
		/*
		 * File ChromeApp = new File(configHelper.readProps("chromeBinaryLocal")); if
		 * (ChromeApp.exists()) {
		 * options.setBinary(configHelper.readProps("chromeBinaryLocal")); } else {
		 * options.setBinary(configHelper.readProps("chromeBinary")); }
		 */
		Map<String, Object> prefs = new HashMap<String, Object>();
		creatDir();
		prefs.put("download.default_directory", dataHolder.getFolderPathForDownload().getAbsolutePath());
		prefs.put("profile.default_content_settings.popups", 0);
		prefs.put("download.directory_upgrade", true);
		prefs.put("download.prompt_for_download", false);
		prefs.put("profile.default_content_setting_values.notifications", 2);
		prefs.put("plugins.always_open_pdf_externally", true);
		prefs.put("profile.default_content_settings.exceptions.automatic_download.*.setting", 1);
		options.setExperimentalOption("prefs", prefs);
		// options.addArguments("headless");
		options.addArguments("window-size=1382,744");
		// options.addArguments("start-maximized");
		return options;
	}

	@Bean
	@Scope("cucumber-glue")
	public WebDriver webDriver() {
		return getDriverInstance();
	}

	public void watiForElementClickable(By selectedBy) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.elementToBeClickable(selectedBy));
	}

	public void watiForElementClickable(String locator) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locator)));
	}

	public void waitForVisibilityOfAllElementsLocatedBy(By selectedBy) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(selectedBy));
	}

	public void waitForVisibilityOfAllElementsLocatedBy(List<WebElement> elements) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.visibilityOfAllElements(elements));
	}

	public void waitForElementToBeVisible(String locator) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(60, TimeUnit.SECONDS).pollingEvery(3,
				TimeUnit.SECONDS);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(locator))));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(locator)));

	}

	public void waitForElementToBeVisible(WebElement element, int seconds) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(seconds, TimeUnit.SECONDS)
				.pollingEvery(3, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForElementToBeClickable(WebElement element, int seconds) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(seconds, TimeUnit.SECONDS)
				.pollingEvery(3, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	public void waitUntilElementVisible(String locator) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(locator)));
	}

	public void waitUntilElementIsNotPresent(String locator) {
		try {
			for (int i = 0; i < 10; i++) {
				wait(5000);
				List<WebElement> result = driver.findElements(By.xpath(locator));
				if (result.size() == 0) {
					return;
				}
			}
		} catch (NoSuchElementException e) {
			return;
		} catch (StaleElementReferenceException e) {
			return;
		} catch (Exception e) {
			throw e;
		}
	}

	public void waitUntilElementVisibleWithFluentWait(String locator) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(60, TimeUnit.SECONDS)
				.pollingEvery(3, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(locator)));
	}

	public void waitForIFrameToBePresentAndSwitchToIt(String locator) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath(locator)));
	}

	public void focusIFrame() {
		driver.switchTo().frame(0);
	}

	public void focusIFrame(String id) {
		driver.switchTo().frame(id);
	}

	public void swithToIFrame(String locator) {
		driver.switchTo().frame(driver.findElement(By.xpath(locator)));
	}

	public void focusOutOfIFrame() {
		driver.switchTo().defaultContent();
	}

	private void closeSession() {
		driver.close();
	}

	private void quitDriver() {
		driver.quit();
	}

	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private void waitForPageToLoad() {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(d -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
	}

	public void wait(int milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void switchToChildTab() {
		try {
			oldTab = driver.getWindowHandle();
			log.info("currently on :" + driver.getCurrentUrl());
			Set<String> allTabs = driver.getWindowHandles();
			for (String currentTab : allTabs) {
				if (currentTab != oldTab) {
					driver.switchTo().window(currentTab);
				}
			}
		} catch (Exception e) {
			log.info("An Exception occured changing focus to child tab: " + e);
		}
	}

	private void switchToParentTab() {
		try {
			driver.close();
			driver.switchTo().window(oldTab);
		} catch (Exception e) {
			log.info("An Exception occured when changing focus to main tab: " + e);
		}
	}

	private void creatDir() {
		String folderName = UUID.randomUUID().toString();
		dataHolder.setFolderPathForDownload(new File(System.getProperty("user.dir") + "\\target\\" + folderName));
		boolean folderCreated = dataHolder.getFolderPathForDownload().mkdir();
		if (folderCreated) {
			System.out.println("Directory Created Successfully");
		} else {
			System.out.println("Unable to Create Directory");
		}
	}

	private void verifyLatestFileDownloadWithExt(String extension) {
		wait(5000);
		File[] listFiles = dataHolder.getFolderPathForDownload().listFiles();
		if (listFiles == null || listFiles.length == 0) {
			throw new StepFailureException("No File is Downloaded");
		}

		File lastModifiedFile = listFiles[0];
		for (int i = 1; i < listFiles.length; i++) {
			if (lastModifiedFile.lastModified() < listFiles[i].lastModified()) {
				lastModifiedFile = listFiles[i];
			}
		}
		dataHolder.setFileName(lastModifiedFile.getName());
		log.info("New file download: " + lastModifiedFile.getName());
		Assert.assertTrue(lastModifiedFile.getName().contains(extension));
	}

	private void verifyDownloadedFile(String fileName) {
		wait(5000);
		boolean flag = false;
		File[] listOfFiles = dataHolder.getFolderPathForDownload().listFiles();
		Assert.assertTrue(listOfFiles.length > 0);
		for (File downloadFile : listOfFiles) {
			if (downloadFile.getName().equals(fileName)) {
				log.info("The file has been downloaded successfully: " + fileName);
				flag = true;
			}
		}
		if (!flag) {
			log.info("The file is not downloaded successfully: " + fileName);
		}
	}

	private void javaScriptExecutorClick(String locator) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement clickElement = driver.findElement(By.xpath(locator));
		js.executeScript("arguments[0].click();", clickElement);
	}

	private void javaScriptExecutorClick(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", element);
	}

	private String getRandomAlphaNumbericString(int noOfDigit) {
		String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvwxyz";
		StringBuilder sb = new StringBuilder(noOfDigit);
		for (int i = 0; i < noOfDigit; i++) {
			int index = (int) (alphaNumericString.length() * Math.random());
			sb.append(alphaNumericString.charAt(index));
		}
		return sb.toString();
	}

	private void verifyPdfContent(String pdfText) {
		File absoluteFile = dataHolder.getFolderPathForDownload();
		String fileName = dataHolder.getFileName();
		log.info("file:///" + absoluteFile + "\\" + fileName);
		try {
			URL url = new URL("file:///" + absoluteFile + "\\" + fileName);
			log.info(url);
			InputStream is = url.openStream();
			BufferedInputStream fileToParse = new BufferedInputStream(is);
			PDDocument document = null;
			document = PDDocument.load(fileToParse);
			String pdfContent = new PDFTextStripper().getText(document);
			Assert.assertTrue(pdfContent.contains(pdfText));
		} catch (IOException arg1) {
			log.info("An excception occured when reading the PDF: " + arg1);
		}
	}

//    public void verifyExcelData (String sheetName, String[] values) { 
//    	File absoluteFile = dataHolder.getFolderPathForDownload(); 
//    	String fileName = dataHolder.getFileName();
//    	log.info (absoluteFile + "\\" + fileName);
//    	List<String> actualList = new ArrayList<>();
//    	try {
//    		 File file = new File (absoluteFile + "\\" + fileName); 
//    		    FileInputStream fileInputStream = new FileInputStream(file); 
//    		    Workbook workbook = new XSSFWorkbook (fileInputStream); 
//    		    Sheet sheet =workbook.getSheet(sheetName);
//    		    for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { 
//    		    	    Row row =sheet.getRow(i);
//    		    	    for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) { 
//    		    	    	Cell cell =row.getCell(j);
//    		    	    	try {
//    		    	    		String stringCellValue = cell.getStringCellValue().trim(); 
//    		    	    		actualList.add(stringCellValue);
//    		    	    	} catch (NullPointerException exceptionDueToMergedCells) {
//    		    	    		log.info("Excel having merged cells: " + exceptionDueToMergedCells);
//    		    	    	}
//    		    	    }
//    		    }
//    	}
//    }

	/**
	 * This method helps to hover on webelements
	 */
	public void hoverOnElement(WebElement element) {
		Actions action = new Actions(driver);
		action.moveToElement(element).perform();
	}

	public void hoverOnElementJs(WebElement element) {
		String javaScript = "var evObj = document.createEvent('MouseEvents');"
				+ "evObj.initMouseEvent(\"mouseover\", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
				+ "arguments[0].dispatchEvent (evobj);";
		((JavascriptExecutor) driver).executeScript(javaScript, element);
	}

	/**
	 * This method helps to hover on webelements
	 * 
	 * @param by
	 */
	public void hoverOnElement(By by) {

		Actions action = new Actions(driver);
		WebElement element = driver.findElement(by);
		action.moveToElement(element).perform();
	}

	/* This method helps to hover on webelements and click on it */
	public void hoverOnElementAndClick(WebElement element) {
		Actions action = new Actions(driver);
		action.moveToElement(element).click().build().perform();
	}

	/*
	 * This method helps to generate dynamic xpath
	 */
	public String generateXpath(String xpathExp, Object... args) {

		for (int i = 0; i < args.length; i++) {

			xpathExp = xpathExp.replace("{" + i + "}", (CharSequence) args[i]);
		}
		return xpathExp;
	}

	/*
	 * This method is used when we have to wait for the attribute to become visible.
	 * 
	 * @param web element, @param attributeName, @param attributeValue
	 */
	public void waitForAttributeToBeVisible(WebElement element, String attributeName, String attributeValue) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.attributeToBe(element, attributeName, attributeValue));
	}

	/**
	 * This method is used when we have to wait for the attribute to become
	 * invisible.
	 * 
	 * @param web element, @param attributeName, @param attributeValue
	 */
	public void waitForAttributeToBeInvisible(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.invisibilityOf(element));
	}

	/**
	 * This method is Waiting 30 seconds for an element to be present on the page,
	 * checking for its presence once every 5 seconds. * @param by
	 */
	public void waitUntilElementVisibleWithFluentWait(By by) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS).ignoring(StaleElementReferenceException.class);
		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(by);
			}
		});
	}

	public void waitForElementToBecomeUnstale(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.not(ExpectedConditions.stalenessOf(element)));
	}

	public void WaitforElementToBeUnstaleAndClick(By by) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		Boolean isElementPresent = wait.until(ExpectedConditions.elementToBeClickable(by)).isEnabled();
		try {
			if (isElementPresent) {
			}
			driver.findElement(by).click();
		} catch (StaleElementReferenceException elementUpdated) {
			WebElement element = driver.findElement(by);
			if (isElementPresent) {
			}
			element.click();
		} catch (Exception exceptionDueToElementClick) {
			log.info("An excception occured when clicking on the element: " + exceptionDueToElementClick);
		}
	}

	public void clearTextBoxField(String locator) {
		driver.findElement(By.xpath(locator)).click();
		driver.findElement(By.xpath(locator)).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
	}

//	public void verifyExcelDataAsSameOnUIData(String sheetName, List<String> expectedList) { 
//		File absoluteFile = dataHolder.getFolderPathForDownLoad();
//		String fileName = dataHolder.getFileName();
//		log.info(absoluteFile + "\\" + fileName); 
//		List<String> actualList = new ArrayList<>(); 
//		try {
//			File file = new File (absoluteFile + "\\" + fileName); 
//			FileInputStream fileInputStream = new FileInputStream(file); 
//			Workbook workbook = new XSSFWorkbook(fileInputStream); 
//			Sheet sheet = workbook.getSheet (sheetName);
//			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { 
//				Row row =sheet.getRow(i);
//				for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) { 
//					Cell cell =row.getCell(j);
//					try {
//						String stringCellValue = cell.getStringCellValue().trim(); 
//						if (!stringCellValue.isEmpty()) {
//							if (stringCellValue.contains("  ")) {
//								actualList.add(stringCellValue.replace("  "," "));
//							} else {
//								actualList.add(stringCellValue);
//							}
//						}
//	
//					} catch (NullPointerException exceptionDueToMergedCells) {
//	
//						log.info("Excel having merged cells: " + exceptionDueToMergedCells);
//					} }}}catch (IOException exceptionDueTOFileInputStream) {
//	
//						log.info("An excception occured when reading the Excel: " + exceptionDueTOFileInputStream);
//					}
//					boolean isEquals = actualList.containsAll (expectedList);
//					Assert.assertTrue("Actual list is not equals to expected string", isEquals);
//	}

	public void refreshThePage() {
		driver.navigate().refresh();
	}

//	public void verifyExcelDataRowsAsSameOnUIData (String sheetName, String rowNumbersInUI) { 
//		File absoluteFile = dataHolder.getFolderPathForDownLoad();
//		String fileName = dataHolder.getFileName();
//		log.info(absoluteFile + "\\" + fileName); 
//		List<String> actualList = new ArrayList<>();
//		try {
//			File file = new File (absoluteFile + "\\" + fileName);
//			FileInputStream fileInputStream = new FileInputStream(file); 
//			Workbook workbook = new XSSFWorkbook(fileInputStream);
//			Sheet sheet = workbook.getSheet (sheetName);
//			int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
//			String excelRowNumber = Integer.toString (physicalNumberOfRows - 2);
//			Assert.assertTrue("Row numbers are Not Matched in Ui and Excel", rowNumbersInUI.contains (excelRowNumber));
//		} catch (IOException exceptionDueTOFileInputStream) {
//			log.info("An excception occured when reading the Excel: " + exceptionDueTOFileInputStream);
//		}
//	}

	public String encodeToBase64(String stringToEncode) {

		return Base64.getEncoder().encodeToString(stringToEncode.getBytes());
	}

	public String decodeToString(String stringToDecode) {
		byte[] actualByte = Base64.getDecoder().decode(stringToDecode);
		return new String(actualByte);
	}

	public void waitForElementToBeVisibleAndClickable(String locator) {
		waitForElementToBeVisible(locator);
		// waitForElementToBeClickable (locator);
		wait(1000);
	}

	public void verifyimageIsValid(String locator) {
		WebElement imgElement = driver.findElement(By.cssSelector(locator));
		String link = imgElement.getCssValue("background-image").replace("url(\"", "").replace("\")", "");
		try {
			URL url = new URL(link);
			HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();
			httpURLConnect.connect();
			Assert.assertFalse("The Image is broken", httpURLConnect.getResponseCode() >= 400);
		} catch (Exception e) {
			throw new StepFailureException(e.getMessage());
		}
	}

	public String generateCurrentDate(String pattern) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
		LocalDate localDate = LocalDate.now();
		return dtf.format(localDate);
	}

	public void clickOnElement(By by) {
		wait(1000);
		WebElement buttonElement = driver.findElement(by);
		waitForElementToBeVisible(buttonElement, 10);
		waitForElementToBeClickable(buttonElement, 10);
		buttonElement.click();
	}

	public String randomStringGenerator(String specialchar) {
		if (specialchar != "<" && specialchar != ">" && specialchar != "=") {
			return null;
		} else {
			String generatedString = RandomStringUtils.randomAlphanumeric(10);
			return generatedString + specialchar;
		}
	}

//	public String getCellData (int index, String cellName, String fileName) { 
//		wait (3000);
//		fileName = fileName.trim();
//		File absoluteFile = dataHolder.getFolderPathForDownLoad();
//		FileInputStream excelFileToRead;
//		XSSFWorkbook wb= null;
//		XSSFCell cell = null;
//		try {
//			File file = new File (absoluteFile + "\\" + fileName); 
//			excelFileToRead = new FileInputStream(file);
//			wb = new XSSFWorkbook (excelFileToRead);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//		Pattern = Pattern.compile("^([A-Z]+) ([0-9]+)$");
//		Matcher m = r.matcher (cellName);
//		if (m.matches()) {
//			String columnName = m.group(1);
//			int rowNumber =Integer.parseInt(m. group (2));
//			if (rowNumber > 0) {
//				try {
//					cell =wb.getSheetAt(index).getRow (rowNumber - 1).getCell(CellReference.convertColStringToIndex (columnName)); } catch (Exception e) {
//				} catch(Exception e) {
//					return "";
//				}}}
//			try {
//				wb.close();
//				excelFileToRead.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return cell.toString();
//	}

	public void deleteDownloadedFile() {
		for (File file : dataHolder.getFolderPathForDownload().listFiles()) {
			file.delete();
			log.info("The file has been deleted successfully: " + file.getName());
		}
	}

	public void waitUntilElementSiteUrlContains(String url) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.urlContains(url));
	}
}