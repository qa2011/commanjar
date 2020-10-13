package com.commanjar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class Log implements ITestListener{

	public static ExtentTest extentTest;

	public Log() {  }

	// Initialize Log4j logs

	private static Logger Log = Logger.getLogger(Log.class.getName());



	// This is to print log for the beginning of the test case, as we usually run so many test cases as a test suite

	public static void startTestCase(String sTestCaseName) {
		Log.info("****************************************************************");

		Log.info("$$$$$$$$$$$$$$$$$$$$$ " + sTestCaseName + "  $$$$$$$$$$$$$$$$$$$$$$$$$");

		Log.info("******************************************************************");
		
	}


	public static void endTestCase(String sTestCaseName) {

		Log.info("XXXXXXXXXXXXXXXX " + "-E---N---D-" + "   XXXXXXXXXXXXXX");
	}


	// Need to create these methods, so that they can be called

	public static void info(String message) {
		//Setup.logger.info(message);
		try {
			Log.info(message);
			ExtentTestManager.getTest().log(Status.INFO, message);
		}catch (Exception e) {
		}
	}

	public static void pass(String message) {
		//Setup.logger.info(message);
			try {
				Log.info(message);
				ExtentTestManager.getTest().log(Status.PASS, message);
			}catch (Exception e) {
		}
	}
	
	public static void warn(String message) {
		//Setup.logger.info(message);
		try {
			Log.warn(message);
			ExtentTestManager.getTest().log(Status.WARNING, message);
		}catch (Exception e) {
		}
	}
	public static void error(String message) {
		//Setup.logger.info(message);
		try {
			Log.error(message);
			ExtentTestManager.getTest().log(Status.ERROR, message);
		}catch (Exception e) {
		}
	}

	public static void fatal(String message) {
		//Setup.logger.info(message);
		try {
			Log.fatal(message);
			ExtentTestManager.getTest().log(Status.FATAL, message);
		}catch (Exception e) {
		}
	}

	/*	public static void debug(String message) {
		//Setup.logger.info(message);
		Log.debug(message);
		ExtentTestManager.getTest().log(Status.DEBUG, message);
	}*/
	public void onStart(ITestContext context) {
		System.out.println("*** Test Suite " + context.getName() + " started ***");
	}

	public void onFinish(ITestContext context) {
		System.out.println(("*** Test Suite " + context.getName() + " ending ***"));
		ExtentTestManager.endTest();
		ExtentManager.getInstance().flush();
	}

	public void onTestStart(ITestResult result) {
		System.out.println(("*** Running test method " + result.getMethod().getMethodName() + "..."));
		ExtentTestManager.startTest(result.getMethod().getMethodName());
	}

	public void onTestSuccess(ITestResult result) {
		System.out.println("*** Executed " + result.getMethod().getMethodName() + " test successfully...");
		ExtentTestManager.getTest().log(Status.PASS, "Test passed");
		String passtest=result.getMethod().getMethodName();
		pass(passtest);
	}

	public void onTestFailure(ITestResult result) {
		System.out.println("*** Test execution " + result.getMethod().getMethodName() + " failed...");
		
		System.out.println(result.getName());
		System.out.println(result.getInstanceName());
		
		//Log.error("*** Test execution " + result.getMethod().getMethodName() + " failed...");
		
		//ExtentTestManager.getTest().log(Status.FAIL, "TEST CASE FAILED IS "+result.getThrowable()); //to add error/exception in extent report
		
		Throwable errorMessage=result.getThrowable();
		String errorMsg=errorMessage.toString();
		error("Test Failed due to Assertion >> "+errorMessage);
		
		/*String[] msg=errorMsg.split(",");
		System.out.println(msg.length);
		for (int i = 0; i < msg.length; i++) {
			System.out.println(msg[i]);
		}
		System.out.println("----------------");*/
		String[] msg=errorMsg.split(",");
		//Weblocator.Text2Speech("We have found "+ msg.length+" assertion failed");
		System.out.println("Assertion failed Count >>>  "+msg.length);
		System.out.println("First Error >>>>>>>  "+msg[0]);
		
		System.out.println(">>>>>>>>>>>>>>    "+msg[0].replace("java.lang.AssertionError: The following asserts failed:",""));
		
		//---------------------JIRA API------------------------------
		if (Setup.BugLock==true) {
			for (int i = 0; i < msg.length; i++) {
				
				String asst=msg[i].replace("java.lang.AssertionError: The following asserts failed:","");
				
				String API_login="https://db-hcl.atlassian.net/rest/api/2/issue/";
				String body_login="{\"fields\":{\"project\":{\"key\":\"CLR\"},\"summary\":\"Assertion Failed >> "+asst.trim()+"\",\"description\":\"Due to assertion Failed >> "+asst.trim()+".   TestCase_Name >> "+result.getInstanceName()+"\",\"customfield_10196\":\"1\",\"issuetype\":{\"name\":\"Bug\"},\"assignee\":{\"displayName\":\"AnuragSin\"}}}";
				String ContentType_login="application/json";
				
				Helper.Text2Speech("Now going to Log bug into JIRA");
				//startTestCase("JIRA Bug Locking");
				try {
					String response=Helper.APIPostRequest(API_login, body_login, ContentType_login);
					//System.out.println("Response>>>>>>  "+response);
					pass("Body>>>>>>  "+body_login);
					pass("Response>>>>>>  "+response);
					Helper.Text2Speech("Successfully locked the Bug in to JIRA");
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	
	
		
		
		
		
		//-----------------------------------------------------------
		ExtentTestManager.getTest().log(Status.FAIL, "Test Failed");
		
		try {
			ExtentTestManager.getTest().addScreenCaptureFromPath(takeScreenShot(Setup.driver, result.getMethod().getMethodName()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		
		//extentTest=ExtentTestManager.extent.createTest(result.getName());
		/*try {
			extentTest.fail("Screenshot below: " + extentTest.addScreenCaptureFromPath(takeScreenShot(Setup.driver,result.getMethod().getMethodName())));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */



	}

	public void onTestSkipped(ITestResult result) {
		System.out.println("*** Test " + result.getMethod().getMethodName() + " skipped...");
		ExtentTestManager.getTest().log(Status.SKIP, "Test Skipped");
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		System.out.println("*** Test failed but within percentage % " + result.getMethod().getMethodName());
	}	

	public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException{
		String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		// after execution, you could see a folder "FailedTestsScreenshots"
		// under src folder
		String destination = System.getProperty("user.dir") + "/FailedTestsScreenshots/" + screenshotName + dateName+ ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;
	}

	private String takeScreenShot(WebDriver driver,String methodName) {
		String path = System.getProperty("user.dir") + "\\FailedTestsScreenshots\\" + methodName + ".jpg";
		try {
			File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshotFile, new File(path));
		} catch (Exception e) {
			System.out.println("Could not write screenshot" + e);
		}
		return path;
	}



}
