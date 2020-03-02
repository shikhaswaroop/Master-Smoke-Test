package org.j2.faxqa.metrofax.myaccount.tests;

import java.util.UUID;

import org.j2.faxqa.efax.common.BaseTest;
import org.j2.faxqa.efax.common.Config;
import org.j2.faxqa.efax.common.TLDriverFactory;
import org.j2.faxqa.efax.common.TestRail;
import org.j2.faxqa.metrofax.myaccount.pageobjects.AccountDetailsPage;
import org.j2.faxqa.metrofax.myaccount.pageobjects.HomePage;
import org.j2.faxqa.metrofax.myaccount.pageobjects.LoginPage;
import org.j2.faxqa.metrofax.myaccount.pageobjects.NavigationBar;
import org.j2.faxqa.metrofax.myaccount.pageobjects.SendFaxesPage;
import org.j2.faxqa.metrofax.myaccount.pageobjects.ViewFaxesPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

//@Listeners({TestExecutionListener.class, TestNGReportListener.class})
public class SendfaxTests extends BaseTest {

	@TestRail(id = "C8534")
	@Test(enabled = true, groups = { "smoke" }, priority = 1, description = "MetroFax > My Account > Send > Validate fax is sent successfully")
	public void composeAndSendAFax(ITestContext context) throws Exception {
		WebDriver driver = TLDriverFactory.getTLDriver();
		driver.navigate().to(Config.metrofax_myaccountBaseUrl);
		LoginPage loginpage = new LoginPage();
		loginpage.login(Config.metrofax_DID, Config.metrofax_PIN);

		HomePage homepage = new HomePage();
		homepage.gotoacctdetailsview();

		String senderid = UUID.randomUUID().toString().replace("-", "").substring(0, 15);
		AccountDetailsPage acctdetailspage = new AccountDetailsPage();
		acctdetailspage.updatesendCSID(senderid);

		homepage = new HomePage();
		homepage.gotosendfaxesview();

		SendFaxesPage sendpage = new SendFaxesPage();
		sendpage.sendfax(senderid);

		Assert.assertTrue(sendpage.confirmationVerify());
	}

	
	@TestRail(id = "C8535")
	@Test(enabled = true, groups = { "smoke" }, priority = 1, description = "MetroFax > My Account > Receive > Validate fax is retrieved successfully")
	public void sendAFaxToSelfAndVerifyFaxIsReceived(ITestContext context) throws Exception {
		WebDriver driver = TLDriverFactory.getTLDriver();
		driver.navigate().to(Config.metrofax_myaccountBaseUrl);
		LoginPage loginpage = new LoginPage();
		loginpage.login(Config.metrofax_DID, Config.metrofax_PIN);

		HomePage homepage = new HomePage();
		homepage.gotoacctdetailsview();

		String senderid = UUID.randomUUID().toString().replace("-", "").substring(0, 15);
		AccountDetailsPage acctdetailspage = new AccountDetailsPage();
		acctdetailspage.updatesendCSID(senderid);

		homepage = new HomePage();
		homepage.gotosendfaxesview();

		SendFaxesPage sendpage = new SendFaxesPage();
		sendpage.sendfax(senderid);
		sendpage.confirmationVerify();
		sendpage.closeconfirmation();
		boolean flag;

		homepage = new HomePage();
		homepage.gotoacctdetailsview();
		acctdetailspage = new AccountDetailsPage();
		flag = acctdetailspage.isSendActivityLogFound(senderid, Config.myccount_sendWait);
		// Assert.assertTrue(flag);

		acctdetailspage.switchToReceiveLogs();
		acctdetailspage = new AccountDetailsPage();
		flag = acctdetailspage.isReceiveActivityLogFound(senderid, Config.myccount_receiveWait);
		// Assert.assertTrue(flag);

		NavigationBar navigate = new NavigationBar();
		navigate.clickViewFaxesTab();

		ViewFaxesPage viewfaxespage = new ViewFaxesPage();
		flag = viewfaxespage.isFaxReceived(senderid, Config.myccount_inboxWait);
		Assert.assertTrue(flag);

	}
}
