package org.j2.faxqa.efax.efax_us.funnel.tests;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.j2.faxqa.efax.common.Config;
import org.j2.faxqa.efax.common.TLDriverFactory;
import org.j2.faxqa.efax.common.TestRail;
import org.j2.faxqa.efax.common.Utils;
import org.j2.faxqa.efax.efax_us.funnel.pageobjects.SignUpPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

//@Listeners({TestExecutionListener.class, TestNGReportListener.class})
public class SignUpTests {

	protected static final Logger logger = LogManager.getLogger();
	
	// If uploadresults=true, then the results get uploaded to location
	// https://testrail.test.j2noc.com/

	@TestRail(id = "C7862")
	@Test(enabled = true, groups = { "smoke", "regression" }, priority = 1, description = "US > SignUp for a new user account")
	public void testcase1(ITestContext context) throws Exception {
		WebDriver driver = null;
		try {
			driver = TLDriverFactory.getTLDriver();
			logger.info("Navigating to - " + Config.efax_funnelBaseUrl);
			driver.navigate().to(Config.efax_funnelBaseUrl);

			String random = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
			String firstname = "QATest"; 
			String lastname = new Faker().address().firstName();
			String email = firstname + "." + lastname + "@" + random.substring(3, 8) + ".com";
			String phone = String.format("%1$s%2$s", ThreadLocalRandom.current().nextInt(10000, 99999),
					ThreadLocalRandom.current().nextInt(10000, 99999));
			String address1 = new Faker().address().streetAddress();
			String address2 = new Faker().address().zipCode();
			String city = "";
			String pcode = String.format("%s", ThreadLocalRandom.current().nextInt(10000, 99999));
			String state = "";
			String country = "United States";
			String creditcardnumber = "4133738662043055"; //"4872906545490653"; // "441506691331";
			String creditcardmonth = "DEC";
			String creditcardyear = "2025";
			String creditcardcvv = "321";

			SignUpPage signup = new SignUpPage();
			signup.selectCountry(country);
			// signup.selectAreaCode();
			// signup.enterAreaCode("212");
			signup.selectState();
			state = signup.setState();
			city = signup.setCity();
			while (signup.noInventory()) {
				logger.info("Couldn't get a DID, retrying...");
				state = signup.setState();
				city = signup.setCity();
			}
			signup.proceedNext();
			signup.setFirstName(firstname);
			signup.setLastName(lastname);
			signup.setEmail(email);
			signup.proceedToBilling();
			signup.setBillingCardName(firstname + " " + lastname);
			signup.setBillingPhoneNumber(phone);
			signup.setBillingAddress1(address1);
			signup.setBillingAddress2(address2);
			signup.setBillingCountry(country);
			signup.setBillingState(state);
			signup.setBillingCity(city);
			signup.setBillingPostalCode(pcode);
			signup.setBillingCardTypeVisa();
			signup.setBillingCreditCardNumber(creditcardnumber);
			signup.setBillingCreditCardMonth(creditcardmonth);
			signup.setBillingCreditCardYear(creditcardyear);
			signup.setBillingCreditCardCVV(creditcardcvv);
			signup.agreeToTermsConditions();
			signup.activateAccount();
			
			boolean flag = signup.isSignUpSuccess();
			Assert.assertTrue(flag);
			
			flag = signup.loginToNewAccount();
			Assert.assertTrue(flag);
			
			flag = signup.logout();
			Assert.assertTrue(flag);
		
		} catch (Throwable e) {
			e.printStackTrace();
			logger.info(e.toString());
			new Utils().captureScreen(context);
			Assert.fail();
		}
	}
}