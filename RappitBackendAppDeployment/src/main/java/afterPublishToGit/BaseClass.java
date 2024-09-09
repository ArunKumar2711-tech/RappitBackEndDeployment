package afterPublishToGit;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import io.github.bonigarcia.wdm.WebDriverManager;
import afterPublishToGit.ReadProperties;

public class BaseClass {

	protected static WebDriver driver;
	public static WebDriverWait wait;
	static ReadProperties reads;
	protected String environment;
	public Actions actions;
	public String serviceURL;
    public String authorised_Redirect_URLs;
    public String downloadedSchemaFileName;
	protected WebDriver getDriver() {

		if (driver == null) {
			//WebDriverManager.chromedriver();
			//driver = new ChromeDriver();
			//driver.manage().window().maximize();
			
			ChromeOptions options = new ChromeOptions();
	        options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
	        String userDataDir = "C:\\Users\\darunkumar\\AppData\\Local\\Google\\Chrome\\User Data\\Default";
	        options.addArguments("user-data-dir=" + userDataDir);
	        driver = new ChromeDriver(options);

		}
		return driver;
	}

	public static void WAIT(String locator, boolean isXpath) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(6000)); // Updated to use Duration
		if (isXpath) {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locator))).click();
		} else {
			wait.until(ExpectedConditions.elementToBeClickable(By.id(locator))).click();
		}
	}
	
	public void googleConsoleLogin() throws InterruptedException {
		
		//driver.get("https://console.cloud.google.com");
		driver.get("https://console.cloud.google.com/artifacts?referrer=search&project=vb-eva-gen");

		//Thread.sleep(2000);
		//driver.findElement(By.id("identifierId")).sendKeys(reads.getGoogleUsername());
		//Thread.sleep(3000);
		//WAIT("//span[text()='Next']", true);
		//driver.findElement(By.xpath("//span[text()='Next']")).click();
		Thread.sleep(3000);
		String check = "https://console.cloud.google.com/artifacts?referrer=search&project=vb-eva-gen";
		if(check.equalsIgnoreCase("https://console.cloud.google.com/artifacts?referrer=search&project=vb-eva-gen")) {
			System.out.println("Already logged in for Console, no credentials needed");
		}
		else {
		
		Thread.sleep(5000);
		driver.findElement(By.xpath("//input[@name='Passwd']")).sendKeys(reads.getGooglePassword());
		Thread.sleep(2000);
		WAIT("//span[text()='Next']", true);
		Thread.sleep(30000);
		
		}
	}

	public void createRepository() throws InterruptedException {
		
		//driver.get("https://console.cloud.google.com/artifacts?referrer=search&project=vb-eva-gen");
        WAIT("//span[contains(text(),'Create repository')]", true);
        Thread.sleep(3000);
		driver.findElement(By.xpath("//body[1]/pan-shell[1]/pcc-shell[1]/cfc-panel-container[1]/div[1]/div[1]/cfc-panel[1]/div[1]/div[1]/div[3]/cfc-panel-container[1]/div[1]/div[1]/cfc-panel[1]/div[1]/div[1]/cfc-panel-container[1]/div[1]/div[1]/cfc-panel[1]/div[1]/div[1]/cfc-panel-container[1]/div[1]/div[1]/cfc-panel[2]/div[1]/div[1]/central-page-area[1]/div[1]/div[1]/pcc-content-viewport[1]/div[1]/div[1]/pangolin-home-wrapper[1]/pangolin-home[1]/cfc-router-outlet[1]/div[1]/ng-component[1]/cfc-single-panel-layout[1]/cfc-panel-container[1]/div[1]/div[1]/cfc-panel[1]/div[1]/div[1]/cfc-panel-body[1]/cfc-virtual-viewport[1]/div[1]/div[1]/ar-repository-form[1]/form[1]/mat-form-field[1]/div[1]/div[1]/div[2]/input[1]"))
				.sendKeys(reads.getRepositoryName() + "-repository");
		Thread.sleep(3000);
		WAIT("//div[contains(@class,'cfc-select-value ng-star-inserted')]", true);
		WAIT("//span[contains(text(),'europe-west1 (Belgium)')]", true);
		WAIT("//span[@class='mdc-button__label'][contains(.,'Create')]", true);
		
	}
	
	
	public void loginToGitlab() throws IOException, InterruptedException {
		reads = new ReadProperties();
		reads.read();
		driver = getDriver();
		driver.get("https://rappit-gitlab.vanenburg.com");
		Thread.sleep(3000);
	//driver.findElement(By.xpath("//div[@class='gl-flex-grow-1 gl-text-gray-900'][contains(.,'Projects')]")).click();
		driver.findElement(By.id("user_login")).sendKeys(reads.getUsername());
		driver.findElement(By.id("user_password")).sendKeys(reads.getPassword());
		WAIT("//span[@class='gl-button-text']", true);
		
	}

	public void checkForCloseIcon() {
		By closeIconLocator = By.cssSelector("svg[data-testid='close-icon']");
		boolean isCloseIconPresent = driver.findElements(closeIconLocator).size() > 0;
		if (isCloseIconPresent) {
			driver.findElement(By.cssSelector("svg[data-testid='close-icon']")).click();
		} else {
			System.out.println("Close icon is not present.");
		}
	}

	public void searchingAndSelectingTheRequiredRepository() throws InterruptedException {
	   // Thread.sleep(4000);
		WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@rel='next'][contains(.,'Next')]")));
		String expectedProjectName = reads.getRepositoryName();
	    boolean projectFound = false;

	    while (!projectFound) {
	        // Find all project names on the current page
	        List<WebElement> projectNameSpan = driver.findElements(By.xpath("//span[@class='project-name']"));
	        
	        // Check if the project is on the current page
	        for (WebElement project : projectNameSpan) {
	            String actualProjectName = project.getText();
	            if (actualProjectName.equals(expectedProjectName)) {
	                System.out.println("Found project name: " + actualProjectName);
	                project.click();
	                projectFound = true;
	                break;
	            }
	        }

	        // If the project was not found and there is a next page
	        if (!projectFound) {
	             nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@rel='next'][contains(.,'Next')]")));
	            
	            // Check if the next button is present and enabled
	            if (nextButton.isEnabled()) {
	                nextButton.click();
	                Thread.sleep(5000);  // Wait for the next page to load (consider using WebDriverWait for better practice)
	            } else {
	                System.out.println("Project not found and no more pages.");
	                break;
	            }
	        }
	    }
	}

	public void findingEnvironmentInGitLab() {
		String URL = driver.getCurrentUrl();
		System.out.println(URL);
		String[] splitting = URL.split("/");
	       environment = splitting[4];
	       System.out.println("Found Environment is:"+" "+environment);
	       }
	
	public void navigatingToPipelineToCheckThePipelineStatus() {
		if("development".equals(environment)) {
			driver.get("https://rappit-gitlab.vanenburg.com/rappit-dev/"+environment+"/eva-applications/"+reads.getRepositoryName()+"/-/pipelines");
		}else {
			driver.get("https://rappit-gitlab.vanenburg.com/rappit-dev/acceptance/eva-applications/"+reads.getRepositoryName()+"/-/pipelines");
}}
	
	public void addingCI_CDVariables() throws InterruptedException {
		WAIT("(//button/span[@class='gl-button-text'])[11]", true);
		WAIT("(//button[@title='Edit'])[3]", true);
		driver.findElement(By.id("ci-variable-value")).clear();
		driver.findElement(By.id("ci-variable-value")).sendKeys("1");
		driver.findElement(By.xpath("(//span[@class='gl-button-text'][contains(.,'Save changes')])[3]")).click();
		driver.findElement(By.xpath("//span[@class='gl-button-text' and normalize-space(text()) = 'Cancel']")).click();
		WAIT("(//button[@title='Edit'])[7]", true);
		driver.findElement(By.id("ci-variable-value")).clear();
		driver.findElement(By.id("ci-variable-value")).sendKeys("firebase-adminsdk-bv2c8@vb-eva-gen.iam.gserviceaccount.com");
		driver.findElement(By.xpath("(//span[@class='gl-button-text'][contains(.,'Save changes')])[3]")).click();
		driver.findElement(By.xpath("//span[@class='gl-button-text' and normalize-space(text()) = 'Cancel']")).click();
		WAIT("(//button[@title='Edit'])[11]", true);
		driver.findElement(By.id("ci-variable-value")).clear();
		driver.findElement(By.id("ci-variable-value")).sendKeys("1");
		driver.findElement(By.xpath("(//span[@class='gl-button-text'][contains(.,'Save changes')])[3]")).click();
		driver.findElement(By.xpath("//span[@class='gl-button-text' and normalize-space(text()) = 'Cancel']")).click();
		driver.findElement(By.xpath("(//button[@title='Edit'])[23]")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//textarea[contains(@rows,'5')]")).clear();
		Thread.sleep(2000);
		//driver.findElement(By.xpath("//textarea[contains(@rows,'5')]")).sendKeys(reads.getValuesForServicesAccountKeyFileJSON());
		driver.findElement(By.xpath("//textarea[contains(@rows,'5')]")).sendKeys("{\r\n"
				+ "  \"type\": \"service_account\",\r\n"
				+ "  \"project_id\": \"vb-eva-gen\",\r\n"
				+ "  \"private_key_id\": \"c3e30ba28f86eb28c81f0ded5b4dc05e1bd7a6de\",\r\n"
				+ "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDQFmiiT8uGbg4X\\nLbi9aHqgZTOWsv8LDVKsspXbf2Ddr27RD+a5lH77vkiPACO3dM8yro9Cl0Eolca2\\na1/7a32/40YCPnI5hlBR6kQ3CRKx3MXoqpNnEsEWqWP9+4B+hrLxgSVMPRAc+Vva\\ni+05zBddtlW5O34wy5oNFI/80+lTBgtkmqosceHsBiP2fkC0XdFWtxcsjHAYdrbE\\nJma8bd7egZDFJ4z71Cx1X+2Q6I3/RWmKrznb1FUyljQNDUqzmDO5N5UeOoDJft9G\\nmheMvMrXc3+qV9bMkFyWEqekR0NXFKKaMEbV/uP8SakLUrW+qd7Lks2a5LUpyj4b\\nAhPL2oTPAgMBAAECggEAIuAemVWWXumDIw9eryAbrVZI1zEY3dlVyewEgc1Phtgo\\nUpG/scrZnYe+0XjMg6roLy5ihF6lZQ/1Rg2pAJJ7ePW2fhnJOyrvu0rwM+kpYPYH\\nlX9BRT03b3zkbWUBnDMnOqLShXyWfsSsSGy7TbyPDf3JUEpQVA1LvHV9zHoX2PoU\\n6dZ9B5gdh7uKNUlWl1YNOzYTGmBv5f5P36wRjV0BqxSDjUZO4FDmwwo7xuWAEc+C\\nGzH8P8dC8BjqaZI/xFdtddc5a8p7ARK1RkBHf+n04nm2zQtKQ5KiIGYVi8fGmo92\\nr0/aTELHPPtbkFXH+ZL/cOkKQXlmxKqZEq8B8VnHwQKBgQDyNmzY2DDiwvcZ2IBk\\nqdLYVXtNzAbNviv5W+YFG5imSgYsx0qmh8WV9pDUZMcNcVfV6/QfzteZhIgoqTrE\\n6OQROUHzgu2Omm8vmWHLdSrzjXkGsTldbPPCo+OOlkw2Llwro4kqdsXXs735XOlx\\nLeLseCrpCkVPqJkbgA76DuITGwKBgQDb7rSmTZuuDo628JnUkT18+++RDsXoTyV/\\nvP7oZoG2bwwcKmG+lZ+reQF33TtBvF1bb0bEnThVtH2r3jad4ImU2/VGjBtgqutd\\nzSkPfYaKvWzsyhWKXgGGq7MnjfMqMPgz3nKuFRUq3gU9rCR1niHpeK9qwKRs0kRk\\nfI5O3nH8XQKBgQCI870X3yXFEfgl5QSz5lIRgEP3+STfsGes1wl4WeI2JCTZrNXI\\n03wlDwVVEqZ6M1w22PNwCMRfSVCzrPxN2mEaXAWuaplnyGqVv2RMZjOjApsr7YlC\\nChjKNWC1fsYY3J3BDva/y+iRzk/cb2yPUiK9EjUxD12hbPMlWYMeYMvzgQKBgQCh\\nStPDbwEwSk7RzWwTn5ynj6BnEp2DvJ+0qaeMSrjwUUphUZezjMm3mJvCC0ZM9FbH\\nGrmcsezBtgfARsxpRmW//PxJi7D8WP0aIr7tNFK6Zyd+FMptWnnmCP/vj0P4+kbc\\ny1PxZVbLxF21mAx5EcqvDaogMT2k7SPpSguXvb18pQKBgQCfkocufrY3hxBW+DQh\\nbH/69EjBxumPxuvgIm1VtJ9AeIh+GsZyRgU3+Xs6SKP0zsw/7m/PF9zoTyAdFKrl\\nXrJgh/Xn0t1Hu7xRmogtEOIrZrfJ/nnllVz4tR85FoeF5sR3YzF3kb4o+Q+G1bcg\\nlIjDmBwy+U73Dr70YY8EGgZ3nQ==\\n-----END PRIVATE KEY-----\\n\",\r\n"
				+ "  \"client_email\": \"sa-cloud-run-deployer@vb-eva-gen.iam.gserviceaccount.com\",\r\n"
				+ "  \"client_id\": \"108644958708504972122\",\r\n"
				+ "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\r\n"
				+ "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\r\n"
				+ "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\r\n"
				+ "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/sa-cloud-run-deployer%40vb-eva-gen.iam.gserviceaccount.com\",\r\n"
				+ "  \"universe_domain\": \"googleapis.com\"\r\n"
				+ "}");
		driver.findElement(By.xpath("(//span[@class='gl-button-text'][contains(.,'Save changes')])[3]")).click();
		driver.findElement(By.xpath("//span[@class='gl-button-text' and normalize-space(text()) = 'Cancel']")).click();
		WAIT("(//button[@title='Edit'])[30]", true);
		driver.findElement(By.id("ci-variable-value")).clear();
		driver.findElement(By.id("ci-variable-value")).sendKeys("1");
		driver.findElement(By.xpath("(//span[@class='gl-button-text'][contains(.,'Save changes')])[3]")).click();
		driver.findElement(By.xpath("//span[@class='gl-button-text' and normalize-space(text()) = 'Cancel']")).click();
		
}
	
	
	
	
	public void actionsBasedOnPipelineStatus() throws InterruptedException, ClientProtocolException, IOException {
		
		final By statusLocator = By.xpath("//span[@data-testid='ci-icon-text']");

        // Create a custom ExpectedCondition to wait for the status to be either "Passed" or "Failed"
        ExpectedCondition<WebElement> statusCondition = new ExpectedCondition<WebElement>() {
            public WebElement apply(WebDriver driver) {
                WebElement element = driver.findElement(statusLocator);
                String text = element.getText();
                if (text.equals("Passed") || text.equals("Failed")) {
                    return element;
                }
                return null;
            }
        };

        // Wait for the status element to be in the expected state
        WebElement statusElement = wait.until(statusCondition);

        // Retrieve the status text
        if (statusElement != null) {
            String statusText = statusElement.getText().trim();
            System.out.println("The status is: " + statusText);

            // Perform actions based on the status
            if (statusText.equals("Passed")) {
                // Perform actions for Passed status
                Thread.sleep(2000);
            driver.findElement(By.xpath("//span[@data-testid='ci-icon-text'][contains(.,'Passed')]")).click();
            Thread.sleep(2000);
            driver.findElement(By.xpath("//div[@title='deploy to dev'][contains(.,'deploy to dev')]")).click();
            Thread.sleep(5000);
            List<WebElement> linkElements = driver.findElements(By.xpath("//a[@class='!gl-text-inherit gl-underline']"));
            
            if (linkElements.size() >= 2) {
                // Select the second element
                WebElement secondLinkElement = linkElements.get(1); // Index 1 for the second element
                // Extract the href attribute value
                String URL = secondLinkElement.getAttribute("href");
                serviceURL = URL != null ? URL.replaceAll("/$", "") : URL;
                // Print the URL
                System.out.println("Pipeline for this repository is already passed, you can use this Service URL for further steps: "+" " + serviceURL);
                googleConsoleLogin();
               // Thread.sleep(25000);
    //            addingCredentialsinOauth();
    //            addingSecretManager();
                generatingSQLScript();
                            
            
            }} else if (statusText.equals("Failed")) {
                // Perform actions for Failed status
            	Thread.sleep(2000);
            	System.out.println("The Pipeline status is Failed. So updating necessary CI/CD variables and settings.xml before that creating Repository in Google Console");
                googleConsoleLogin();
                createRepository();
                Thread.sleep(3000);
                if("development".equals(environment)) {
        			driver.get("https://rappit-gitlab.vanenburg.com/rappit-dev/"+environment+"/eva-applications/"+reads.getRepositoryName()+"/-/settings/ci_cd");
        			addingCI_CDVariables();
        			driver.get("https://rappit-gitlab.vanenburg.com/rappit-dev/"+environment+"/eva-applications/"+reads.getRepositoryName()+"/-/tree/master");
        			driver.findElement(By.xpath("//span[@class='gl-new-dropdown-button-text'][contains(.,'master')]")).click();
        			Thread.sleep(2000);
        			driver.findElement(By.xpath("//span[@class='gl-new-dropdown-item-text-wrapper'][contains(.,'development')]")).click();
        			Thread.sleep(2000);
        			driver.findElement(By.xpath("//a[contains(@title,'.m2')]")).click();
        			Thread.sleep(2000);
        			driver.findElement(By.xpath("//a[contains(@class,'tree-item-link str-truncated')]")).click();
        			Thread.sleep(2000);
        			driver.findElement(By.xpath("(//span/span[@class='gl-new-dropdown-button-text'])[4]")).click();
        			driver.findElement(By.xpath("//span[contains(text(),'Edit single file')]")).click();
        			Thread.sleep(6000);
        			Actions actions = new Actions(driver);
        	        actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();
        			Thread.sleep(2000);
        			actions.sendKeys(Keys.DELETE).perform();
        	        String emoji = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\r\n"
        					+ "                    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
       					+ "                    xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0\r\n"
        					+ "                      http://maven.apache.org/xsd/settings-1.0.0.xsd\">\r\n" + "	<servers>\r\n"
        					+ "		<server>\r\n" + "			<id>vb-central</id>\r\n"
        					+ "			<username>${env.ARTIFACTORY_USER}</username>\r\n"
        					+ "			<password>${env.ARTIFACTORY_PASS}</password>\r\n" + "		</server>\r\n"
        					+ "	</servers>\r\n" + "	<pluginGroups>\r\n"
        					+ "        <pluginGroup>org.sonarsource.scanner.maven</pluginGroup>\r\n" + "    </pluginGroups>\r\n"
        					+ "	<profiles>\r\n" + "		<profile>\r\n" + "		  <repositories>\r\n"
        					+ "			<repository>\r\n" + "			  <snapshots>\r\n"
        					+ "				<enabled>false</enabled>\r\n" + "			  </snapshots>\r\n"
        					+ "			  <id>vb-central</id>\r\n" + "			  <name>rappit-maven-repo</name>\r\n"
        					+ "              <url>https://artifactory.vanenburg.com:443/artifactory/dev-rappit-maven-repo/</url>\r\n"
        					+ "			</repository>\r\n" + "		  </repositories>\r\n" + "		  <pluginRepositories>\r\n"
        					+ "			<pluginRepository>\r\n" + "			  <snapshots>\r\n"
        					+ "				<enabled>false</enabled>\r\n" + "			  </snapshots>\r\n"
        					+ "			  <id>vb-central</id>\r\n" + "			  <name>rappit-maven-repo</name>\r\n"
        					+ "           	  <url>https://artifactory.vanenburg.com:443/artifactory/dev-rappit-maven-repo/</url>\r\n"
        					+ "			</pluginRepository>\r\n" + "		  </pluginRepositories>\r\n"
        					+ "		  <properties>\r\n" + "			<sonar.host.url>${env.SONAR_HOST_URL}</sonar.host.url>\r\n"
        					+ "			<sonar.login>${env.SONAR_AUTH_TOKEN}</sonar.login>\r\n" + "		  </properties>\r\n"
        					+ "		  <id>artifactory</id>\r\n" + "		</profile>\r\n" + "	</profiles>\r\n"
        					+ "	<activeProfiles>\r\n" + "		<activeProfile>artifactory</activeProfile>\r\n"
        					+ "	</activeProfiles>\r\n" + "</settings>\r\n" + "";
        			StringSelection stringSelection = new StringSelection(emoji);
        			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        			clipboard.setContents(stringSelection, null);
        			Thread.sleep(2000);
        			WebElement element2 = driver.findElement(By.xpath("//textarea[@class='inputarea monaco-mouse-cursor-text']\r\n" + ""));
        			element2.click();
        			actions.keyDown(Keys.CONTROL);
        			actions.sendKeys("v");
        			actions.keyUp(Keys.CONTROL);
       			    actions.build().perform();
        	        Thread.sleep(2000);
        			driver.findElement(By.xpath("//div/button[@id='commit-changes']")).click();
        			Thread.sleep(3000);
        			driver.get("https://rappit-gitlab.vanenburg.com/rappit-dev/development/eva-applications/" + reads.getRepositoryName()
        					+ "/-/pipelines");
        			driver.navigate().refresh();
        			Thread.sleep(10000);
               final By statusLocator_another = By.xpath("//span[@data-testid='ci-icon-text']");

               
   			// Define a condition to check for the status being "Passed"
   		    ExpectedCondition<WebElement> passedCondition = new ExpectedCondition<WebElement>() {
   		        public WebElement apply(WebDriver driver) {
   		            WebElement elementP = driver.findElement(statusLocator_another);
   		            String text = elementP.getText();
   		            if (text.equals("Passed") || text.equals("Failed")) {
   		                return elementP;
   		            }
   		            return null;
   		        }
   		    };

   		   

   		    // Wait for the status to be "Passed"
   		    WebElement statusElement_another = wait.until(passedCondition);
   		    if (statusElement_another != null) {
   		    	String statusText1 = statusElement_another.getText().trim();
   	            System.out.println("The status for development is: " + statusText1);
   	            if (statusText1.equals("Passed")) {
   		        // Status is "Passed", perform actions
   		        System.out.println("The status is Passed in new method.");
   		        // Example actions for Passed status
   		        Thread.sleep(2000);
   	            driver.findElement(By.xpath("//span[@data-testid='ci-icon-text'][contains(.,'Passed')]")).click();
   	            Thread.sleep(2000);
   	            driver.findElement(By.xpath("//div[@title='deploy to dev'][contains(.,'deploy to dev')]")).click();
   	            Thread.sleep(5000);
   	            List<WebElement> linkElements = driver.findElements(By.xpath("//a[@class='!gl-text-inherit gl-underline']"));
   	            
   	            if (linkElements.size() >= 2) {
   	                // Select the second element
   	                WebElement secondLinkElement = linkElements.get(1); // Index 1 for the second element
   	                // Extract the href attribute value
   	                String URL = secondLinkElement.getAttribute("href");
   	           serviceURL = URL != null ? URL.replaceAll("/$", "") : URL; 
   	                // Print the URL
   	                System.out.println("Pipeline for this repository is already passed, you can use this Service URL for further steps: "+" " + serviceURL);
   	                addingCredentialsinOauth();
   	                addingSecretManager();
	                generatingSQLScript();
   	                }

   		        // Further actions...
   		    } else {
   		        System.out.println("Pipeline for this repository is failed again even after updating necessary CI/CD variables and settings.xml. So please contact dev team ");
   		    }}
              
                           
               
               
               
               
               
                         } 
               
           

                     
           
                
                
                
                
                                
                
                 else {
                	
        			driver.get("https://rappit-gitlab.vanenburg.com/rappit-dev/acceptance/eva-applications/"+reads.getRepositoryName()+"/-/settings/ci_cd");
        			addingCI_CDVariables();
        			driver.get("https://rappit-gitlab.vanenburg.com/rappit-dev/acceptance/eva-applications/"+reads.getRepositoryName()+"/-/tree/master");
        			driver.findElement(By.xpath("//span[@class='gl-new-dropdown-button-text'][contains(.,'master')]")).click();
        			Thread.sleep(2000);
        			driver.findElement(By.xpath("//span[@class='gl-new-dropdown-item-text-wrapper'][contains(.,'development')]")).click();
        			Thread.sleep(2000);
        			driver.findElement(By.xpath("//a[contains(@title,'.m2')]")).click();
        			Thread.sleep(2000);
        			driver.findElement(By.xpath("//a[contains(@class,'tree-item-link str-truncated')]")).click();
        			Thread.sleep(2000);
        			driver.findElement(By.xpath("(//span/span[@class='gl-new-dropdown-button-text'])[4]")).click();
        			driver.findElement(By.xpath("//span[contains(text(),'Edit single file')]")).click();        	
        			Thread.sleep(6000);
        			 actions = new Actions(driver);
        	        actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();
        			Thread.sleep(2000);
        			actions.sendKeys(Keys.DELETE).perform();
        	        String emoji1 = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\r\n"
        					+ "                    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
       					+ "                    xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0\r\n"
        					+ "                      http://maven.apache.org/xsd/settings-1.0.0.xsd\">\r\n" + "	<servers>\r\n"
        					+ "		<server>\r\n" + "			<id>vb-central</id>\r\n"
        					+ "			<username>${env.ARTIFACTORY_USER}</username>\r\n"
        					+ "			<password>${env.ARTIFACTORY_PASS}</password>\r\n" + "		</server>\r\n"
        					+ "	</servers>\r\n" + "	<pluginGroups>\r\n"
        					+ "        <pluginGroup>org.sonarsource.scanner.maven</pluginGroup>\r\n" + "    </pluginGroups>\r\n"
        					+ "	<profiles>\r\n" + "		<profile>\r\n" + "		  <repositories>\r\n"
        					+ "			<repository>\r\n" + "			  <snapshots>\r\n"
        					+ "				<enabled>false</enabled>\r\n" + "			  </snapshots>\r\n"
        					+ "			  <id>vb-central</id>\r\n" + "			  <name>rappit-maven-repo</name>\r\n"
        					+ "              <url>https://artifactory.vanenburg.com:443/artifactory/acc-rappit-maven-repo/</url>\r\n"
        					+ "			</repository>\r\n" + "		  </repositories>\r\n" + "		  <pluginRepositories>\r\n"
        					+ "			<pluginRepository>\r\n" + "			  <snapshots>\r\n"
        					+ "				<enabled>false</enabled>\r\n" + "			  </snapshots>\r\n"
        					+ "			  <id>vb-central</id>\r\n" + "			  <name>rappit-maven-repo</name>\r\n"
        					+ "           	  <url>https://artifactory.vanenburg.com:443/artifactory/acc-rappit-maven-repo/</url>\r\n"
        					+ "			</pluginRepository>\r\n" + "		  </pluginRepositories>\r\n"
        					+ "		  <properties>\r\n" + "			<sonar.host.url>${env.SONAR_HOST_URL}</sonar.host.url>\r\n"
        					+ "			<sonar.login>${env.SONAR_AUTH_TOKEN}</sonar.login>\r\n" + "		  </properties>\r\n"
        					+ "		  <id>artifactory</id>\r\n" + "		</profile>\r\n" + "	</profiles>\r\n"
        					+ "	<activeProfiles>\r\n" + "		<activeProfile>artifactory</activeProfile>\r\n"
        					+ "	</activeProfiles>\r\n" + "</settings>\r\n" + "";
        			StringSelection stringSelection1 = new StringSelection(emoji1);
        			Clipboard clipboard1 = Toolkit.getDefaultToolkit().getSystemClipboard();
        			clipboard1.setContents(stringSelection1, null);
        			Thread.sleep(2000);
        			WebElement element3 = driver.findElement(By.xpath("//textarea[@class='inputarea monaco-mouse-cursor-text']\r\n" + ""));
        			element3.click();
        			actions.keyDown(Keys.CONTROL);
        			actions.sendKeys("v");
        			actions.keyUp(Keys.CONTROL);
       			    actions.build().perform();
        	        Thread.sleep(2000);
        			driver.findElement(By.xpath("//div/button[@id='commit-changes']")).click();
        			Thread.sleep(3000);
        			driver.get("https://rappit-gitlab.vanenburg.com/rappit-dev/acceptance/eva-applications/" + reads.getRepositoryName()
					+ "/-/pipelines");
        			driver.navigate().refresh();
        			Thread.sleep(10000);
        			
        		
        			final By statusLocator_another = By.xpath("//span[@data-testid='ci-icon-text']");

              
        			// Define a condition to check for the status being "Passed"
        		    ExpectedCondition<WebElement> passedCondition = new ExpectedCondition<WebElement>() {
        		        public WebElement apply(WebDriver driver) {
        		            WebElement elementP = driver.findElement(statusLocator_another);
        		            String text = elementP.getText();
        		            if (text.equals("Passed") || text.equals("Failed")) {
        		                return elementP;
        		            }
        		            return null;
        		        }
        		    };

        		   

        		    // Wait for the status to be "Passed"
        		    WebElement statusElement_another = wait.until(passedCondition);
        		    if (statusElement_another != null) {
        		    	String statusText1 = statusElement_another.getText().trim();
        	            System.out.println("The status is: " + statusText1);
        	            if (statusText1.equals("Passed")) {
        		        // Status is "Passed", perform actions
        		        System.out.println("The status is Passed in new method.");
        		        // Example actions for Passed status
        		        Thread.sleep(2000);
        	            driver.findElement(By.xpath("//span[@data-testid='ci-icon-text'][contains(.,'Passed')]")).click();
        	            Thread.sleep(2000);
        	            driver.findElement(By.xpath("//div[@title='deploy to dev'][contains(.,'deploy to dev')]")).click();
        	            Thread.sleep(5000);
        	            List<WebElement> linkElements = driver.findElements(By.xpath("//a[@class='!gl-text-inherit gl-underline']"));
        	            
        	            if (linkElements.size() >= 2) {
        	                // Select the second element
        	                WebElement secondLinkElement = linkElements.get(1); // Index 1 for the second element
        	                // Extract the href attribute value
        	                String URL = secondLinkElement.getAttribute("href");
        	                serviceURL = URL != null ? URL.replaceAll("/$", "") : URL;        	                // Print the URL
        	                System.out.println("Pipeline for this repository is already passed, you can use this Service URL for further steps: "+" " + serviceURL);
        	                //googleConsoleLogin();
        	                //Thread.sleep(25000);
        	                addingCredentialsinOauth();
        	                addingSecretManager();
        	                generatingSQLScript();
        	                }

        		        // Further actions...
        		    } else {
        		        System.out.println("Pipeline for this repository is failed again even after updating necessary CI/CD variables and settings.xml. So please contact dev team ");
        		    }}
	
        			
        			
        		
                 
                 
                 }	
        			
        			        			
        			
        			
        			
        			
                }}
	}

	
	
	public void addingCredentialsinOauth() throws InterruptedException {
		actions = new Actions(driver);
		driver.get("https://console.cloud.google.com/apis/credentials?referrer=search&project=vb-eva-gen");
		
		
		WAIT("//a[@track-type='api'][contains(.,'Web client  (auto created by Google Service)')]", true);
		System.out.println("The Service URL is:"+serviceURL);
		Thread.sleep(10000);
        WAIT("//span[@class='cfc-form-stack-add-text']", true);
        List<WebElement> firstList = driver.findElements(By.xpath("//input[@formcontrolname='uri']"));

		for (int i = 0; i < firstList.size(); i++) {
			String Authorised_JavaScript_origins = firstList.get(i).getAttribute("value");
			// System.out.println(Authorised_JavaScript_origins);
			if (Authorised_JavaScript_origins.isEmpty()) {
				driver.findElements(By.xpath("//input[@formcontrolname='uri']")).get(i)
						.sendKeys(serviceURL);
				actions.sendKeys(Keys.TAB).perform();
				Thread.sleep(3000);
				driver.findElement(By.xpath("(//span[@class='cfc-form-stack-add-text'])[2]")).click();
			}

		}
		
		
		List<WebElement> firstList1 = driver.findElements(By.xpath("//input[@formcontrolname='uri']"));

		for (int i = 0; i < firstList1.size(); i++) {
			String Authorised_JavaScript_origins1 = firstList1.get(i).getAttribute("value");
			// System.out.println(Authorised_JavaScript_origins1);
			if (Authorised_JavaScript_origins1.isEmpty()) {
				authorised_Redirect_URLs=serviceURL;
				String modifiedUrl = authorised_Redirect_URLs.replace("https://", "http://");
				driver.findElements(By.xpath("//input[@formcontrolname='uri']")).get(i).sendKeys(
						modifiedUrl + "/login/oauth2/code/google");
				actions.sendKeys(Keys.TAB).perform();
				Thread.sleep(3000);
				driver.findElement(By.xpath("(//span[@class='cfc-form-stack-add-text'])[2]")).click();
			}

		}

	
		
		List<WebElement> firstList2 = driver.findElements(By.xpath("//input[@formcontrolname='uri']"));

		for (int i = 0; i < firstList2.size(); i++) {
			String Authorised_JavaScript_origins2 = firstList2.get(i).getAttribute("value");
			if (Authorised_JavaScript_origins2.isEmpty()) {
				authorised_Redirect_URLs=serviceURL;
				String modifiedUrl = authorised_Redirect_URLs.replace("https://", "http://");
				driver.findElements(By.xpath("//input[@formcontrolname='uri']")).get(i).sendKeys(
					modifiedUrl+"/oauth2/authorization/google");
				actions.sendKeys(Keys.TAB).perform();
				Thread.sleep(3000);

				
			}

		}

		driver.findElement(By.xpath("//span[@class='mdc-button__label'][contains(.,'Save')]")).click();
        Thread.sleep(6000);

	}
	
	
	public void addingSecretManager() throws InterruptedException {
		
		driver.get("https://console.cloud.google.com/security/secret-manager?project=vb-eva-gen");
		
		Thread.sleep(3000);
		String check1 = "https://console.cloud.google.com/security/secret-manager?project=vb-eva-gen";
		if(check1.equalsIgnoreCase("https://console.cloud.google.com/security/secret-manager?project=vb-eva-gen")) {
			System.out.println("Already logged in for SECRET MANAGER, no credentials needed");
		}
		else {
		
		Thread.sleep(5000);
		driver.findElement(By.xpath("//input[@name='Passwd']")).sendKeys(reads.getGooglePassword());
		Thread.sleep(2000);
		WAIT("//span[text()='Next']", true);
		Thread.sleep(30000);
		
		}
		
		
		
		
		
		
		
		
		
		WAIT("(//span[contains(.,'Create')])[3]", true);
		Thread.sleep(4000);
		driver.findElement(By.cssSelector("#_0rif_mat-input-0")).sendKeys(reads.getRepositoryName() + "_configuration_json");
		Thread.sleep(3000);
		driver.findElement(By.cssSelector("#_0rif_mat-input-1")).sendKeys("[\r\n" + "  {\r\n"
				+ "    \"value\": \"/rest/task/submit/{id}\",\r\n" + "    \"key\": \"task_base_url\"\r\n" + "  },\r\n"
				+ "  {\r\n" + "    \"value\": \"!@#$%$&pass!@##$\",\r\n" + "    \"key\": \"jwt_secret\"\r\n"
				+ "  },\r\n" + "  {\r\n" + "    \"value\": \"vb-eva-gen-attachments\",\r\n"
				+ "    \"key\": \"attachment_bucket_name\"\r\n" + "  },\r\n" + "  {\r\n"
				+ "    \"value\": \"{\\\"db_url\\\": \\\"jdbc:mysql://10.46.1.3:3306/schema8\\\",\\\"db_user\\\": \\\"devuser\\\",\\\"db_password\\\": \\\"kGqxF04S9'4z89>T\\\"}\",\r\n"
				+ "    \"key\": \"db_connection_info\"\r\n" + "  },\r\n" + "  {\r\n"
				+ "    \"value\": \"{\\\"host\\\" : \\\"10.132.0.2\\\",\\\"port\\\" : 9200,\\\"user\\\" : \\\"evagenusr\\\",\\\"pwd\\\" : \\\"New@123\\\"}\",\r\n"
				+ "    \"key\": \"elasticsearch_config\"\r\n" + "  },\r\n" + "  {\r\n"
				+ "    \"key\": \"client-id\",\r\n"
				+ "    \"value\": \"542250723797-6udeop4covmrjn4t14f35p8j0qmh8g8j.apps.googleusercontent.com\"\r\n"
				+ "  },\r\n" + "  {\r\n" + "    \"key\": \"client-secret\",\r\n"
				+ "    \"value\": \"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\"\r\n" + "  },\r\n" + "  {\r\n"
				+ "    \"key\": \"from.name\",\r\n" + "    \"value\": \"rappit-minato\"\r\n" + "  },\r\n" + "  {\r\n"
				+ "    \"value\": \"yogiyogesh518@gmail.com\",\r\n" + "    \"key\": \"from.email\"\r\n" + "  }\r\n"
				+ "]\r\n" + "");
//		driver.findElement(By.cssSelector("#_0rif_mat-input-1")).sendKeys("[\r\n"
//				+ "  {\r\n"
//				+ "    \"value\": \"/rest/task/submit/{id}\",\r\n"
//				+ "    \"key\": \"task_base_url\"\r\n"
//				+ "  },\r\n"
//				+ "  {\r\n"
//				+ "    \"value\": \"!@#$%$&pass!@##$\",\r\n"
//				+ "    \"key\": \"jwt_secret\"\r\n"
//				+ "  },\r\n"
//				+ "  {\r\n"
//				+ "    \"value\": \"vb-eva-gen-attachments\",\r\n"
//				+ "    \"key\": \"attachment_bucket_name\"\r\n"
//				+ "  },\r\n"
//				+ "  {\r\n"
//				+ "    \"key\": \"db_connection_info\",\r\n"
//				+ "    \"value\": \"{\\\"db_url\\\": \\\"jdbc:postgresql://10.46.1.11:5432/postgresschema1\\\",\\\"db_user\\\": \\\"postgres\\\",\\\"db_password\\\": \\\"k!$JLPWds$fIoJ&9\\\"}\"\r\n"
//				+ "  },\r\n"
//				+ "  {\r\n"
//				+ "    \"value\": \"https://www.googleapis.com/service_accounts/v1/metadata/jwk/\",\r\n"
//				+ "    \"key\": \"public_key_api\"\r\n"
//				+ "  }\r\n"
//				+ "]");
		Thread.sleep(3000);

		driver.findElement(By.xpath("//span[@class='mdc-button__label'][contains(.,'Create secret')]")).click();
		Thread.sleep(10000);
		driver.get("https://console.cloud.google.com/security/secret-manager?project=vb-eva-gen");
		Thread.sleep(5000);
		WAIT("(//span[contains(.,'Create')])[3]", true);
		Thread.sleep(4000);
		driver.findElement(By.cssSelector("#_0rif_mat-input-0")).sendKeys(reads.getRepositoryName() + "_sa-fs-connection-json");
		Thread.sleep(3000);
		driver.findElement(By.cssSelector("#_0rif_mat-input-1")).sendKeys("{\r\n"
				+ "  \"type\": \"service_account\",\r\n" + "  \"project_id\": \"vb-eva-gen\",\r\n"
				+ "  \"private_key_id\": \"b27e15f4f8c0a8420574be728737b96cbf514820\",\r\n"
				+ "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCgyqcbTKzBq3vV\\nBe67fqUxFtYgxmiLIpvjmDqLrpoJmTQcuv0aj6QpHMb5wPhEV2rODhCbs98g3+H1\\naPxJbjJUhib6d+HHdtehi/J5EN0smr73Xzb1m6DW8D9zQCATleU7HcEfLWPSdisv\\nP92vV5lzEN6GGgnZYKotVDilAXEKZmNEkfX727wP0enHQufhUvC35p10M3mz1L22\\nClBc/29xUcPzcY1cw3XTUZ9BkKFtJP5H+ZqYDnp4XsFF0/ckNIa1lCHnC3AVzKEy\\n5HvR/hGKbnbLoWD9ZlkClGxO8dPVGPYa34KxcJVlOBsy05Arj61H1Yd7j3zReSYP\\nHvGdVcZlAgMBAAECggEAGSwgxTFXymoiVNM88LV+/xRoKXFmZIHijdsDL2MCUoZd\\nljHf9gyO0rDipa8ELw7QdbnV98pNIERsh5MMDCLoVjs6VvP1sNh2IaBbMYDmX1lK\\nlEefvwvCPmQWSuC7JNXOsVaTKWJdwlElqZwCBLVeHJmDfNMOv61Wdwo8Y/i5zbrC\\nQIO99W5BPyEMbsrPpGe17r6LRPjAE31lb9I1IM0LgO+8wQr/rXm/6sCGuJMhp6hj\\nm1XJXu5kNG59wrkawQTaRBdXg3W4+1/s7ehjJQV76LM7/nGxs67VZl7tILJDHUkf\\nIDYWTN36/f4GnCNwAY6sNG4RJ7YJxDh8P8GCxKyH8QKBgQDMrf6bNMbOLB+HHgod\\nLxAsgtjmNuIqin2dvEnx19CScOlBhseqPaKlOJKrMTFB+b1+hKGRHdzEGdAQQaX8\\nyAD1v78Zz82/eVkS9A4CAys3N8UmcWyozPRCRXMj61SDfBovn1JzFmxfd7Cr6w6f\\nHgv6Ika12N5j17h+JPPcC8kLGwKBgQDJG5DlxuB2/LDMqHE4gF/Z50slPJ5VJM/3\\nOYknQMMqgC17hLkKIi/AOVM6GYImMWHUGvAfURSXQnHkldLRr7+Cw7Cc1IpUQ5os\\nMO6aNZtwE2gGJ8MjPuduvJ9y6l3XsdDaz0HYVvFATunT1HGAP4y88zMO+KTibPii\\nhOwLVXwMfwKBgGElzIzaNeoxox08ssw7RE/8IvrR3fMXHJw7fFVfe82l7fB+ClLR\\nQlttSKAcjyajZL+iMBVyuRASuGCe6CvcuifqRMf5i8xvyklmsSdfXamtvNOMTmio\\nLt8tm4LW9Zwa0Ur9MeMSJ4oAg0h2HtCDMGge0LULJDL7dsPYwi4VIe01AoGARPhb\\nPTJzkNsgl0+9ZK9CMC0OrHqZooG5gQQOcZPYWx8SeiYOf9cxoS8HyAkvhYGyF1gV\\nuefItAdpoAdyAc/QUjxiuaZ6umNk4Hr3mZOA93LwXgwM9G2CWYv+8x5FiM/G3QPv\\nhQ5sNq3Zq3hUZLNmxPZzMFM92RmeO75yZYCvxmkCgYB6eoZiV3XKYO+1GTo05UCy\\n6fZzwMTvX04LI1y00dt0QvvZG42ZiOGNX+/WY9uGqQnlQ17PO34gDGsxnpKopeJT\\nMwnpnByt+6LjZjFfzNf4KFXAWej5FkwSWuh8HNweTMKQVrC6qQ8SQX+o9RqKp8PQ\\nPlpSLmM/2zRjJ/xncWGB8g==\\n-----END PRIVATE KEY-----\\n\",\r\n"
				+ "  \"client_email\": \"firebase-adminsdk-bv2c8@vb-eva-gen.iam.gserviceaccount.com\",\r\n"
				+ "  \"client_id\": \"113398304591968615991\",\r\n"
				+ "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\r\n"
				+ "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\r\n"
				+ "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\r\n"
				+ "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-bv2c8%40vb-eva-gen.iam.gserviceaccount.com\",\r\n"
				+ "  \"universe_domain\": \"googleapis.com\"\r\n" + "}\r\n" + "");
		Thread.sleep(3000);
		driver.findElement(By.xpath("//span[@class='mdc-button__label'][contains(.,'Create secret')]")).click();
        Thread.sleep(5000);
	}
	
	
	public void generatingSQLScript() throws ClientProtocolException, IOException {
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// Create a POST request
				HttpPost post = new HttpPost(
						serviceURL+"/rest/rdbms/generatesqlscript");

		// Execute the request
		CloseableHttpResponse response = httpClient.execute(post);
		// Get the response code
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("Response Code: " + statusCode);

		// Get the response body
		String responseBody = EntityUtils.toString(response.getEntity());
		System.out.println("Response Body: " + responseBody);

	}
	
	
	
	public void sqlDBSchema() throws InterruptedException {
		driver.get("https://console.cloud.google.com/storage/browser/vb-eva-gen-configuration/sqlDB_Schema;tab=objects?project=vb-eva-gen&pageState=(%22StorageObjectListTable%22:(%22f%22:%22%255B%255D%22,%22s%22:%5B(%22i%22:%22displayName%22,%22s%22:%221%22)%5D))&prefix=&forceOnObjectsSortingFiltering=true&rapt=AEjHL4MKpvkRhrwu1ZQtr3MJaG-y-UMiWQ06WgGVqJ8JD1zP2SksOfvJgdYUjH-6k4z1i2N_ZRw65BR0wGlcjU3-XBzkoEH_pwER-tt34gcqyuz5sNfbH94");
	
		Thread.sleep(5000);
		
		if(driver.findElement(By.xpath("//input[@name='Passwd']")).isDisplayed()) {
			Thread.sleep(5000);
			driver.findElement(By.xpath("//input[@name='Passwd']")).sendKeys(reads.getGooglePassword());
			Thread.sleep(2000);
			WAIT("//span[text()='Next']", true);
			Thread.sleep(30000);		}
		else {
		
		System.out.println("Already logged in to SQLDBSchema, trying to download the schema file");
		
		}	
		
		WAIT("//span[@class='cfc-table-column-header-content'][contains(.,'Name')]", true);
	Thread.sleep(4000);
    WebElement firstRecord = driver.findElement(By.cssSelector("a.object-link.ng-star-inserted"));
     downloadedSchemaFileName = firstRecord.getText();
  System.out.println("HREF is:"+ downloadedSchemaFileName);
     firstRecord.click();
    Thread.sleep(4000);
    driver.findElement(By.xpath("(//span[@sandboxuid='0'][contains(.,'Download')])[2]")).click();
    }
	
	
	
	public void logInToSqlStudio() throws InterruptedException {
		driver.get("https://console.cloud.google.com/sql/instances/eva-gen-sql-instance/studio?project=vb-eva-gen");
		
		Thread.sleep(3000);
		String check3 = "https://console.cloud.google.com/sql/instances/eva-gen-sql-instance/studio?project=vb-eva-gen";
		if(check3.equalsIgnoreCase("https://console.cloud.google.com/sql/instances/eva-gen-sql-instance/studio?project=vb-eva-gen")) {
			System.out.println("Already logged in for SQL Studio, no credentials needed");
		}
		else {
		
		Thread.sleep(5000);
		driver.findElement(By.xpath("//input[@name='Passwd']")).sendKeys(reads.getGooglePassword());
		Thread.sleep(2000);
		WAIT("//span[text()='Next']", true);
		Thread.sleep(30000);
		
		}
		
		
		
		
		Thread.sleep(3000);
		WAIT("//div[@class='cfc-select-value ng-star-inserted']", true);
		Thread.sleep(3000);
        driver.findElement(By.xpath("//span[text()='schema4']")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("(//div[contains(@class,'cfc-select-value ng-star-inserted')])[2]")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//span[text()='devuser']")).click();
        Thread.sleep(3000);
       driver.findElement(By.xpath("(//input[@autocomplete='off'][contains(@id,'mat-input-0')])[2]")).sendKeys("kGqxF04S9'4z89>T");
        Thread.sleep(2000);
        driver.findElement(By.xpath("//span[@class='mdc-button__label'][contains(.,'Authenticate')]")).click();
		}
	
	
	
	public void logInToSqlStudioWithCreatedDataBase() throws InterruptedException {
		driver.get("https://console.cloud.google.com/sql/instances/eva-gen-sql-instance/studio?project=vb-eva-gen");
		Thread.sleep(3000);
		String check30 = "https://console.cloud.google.com/sql/instances/eva-gen-sql-instance/studio?project=vb-eva-gen";
		if(check30.equalsIgnoreCase("https://console.cloud.google.com/sql/instances/eva-gen-sql-instance/studio?project=vb-eva-gen")) {
			System.out.println("Already logged in for SQL Studio after database created, no credentials needed");
		}
		else {
		
		Thread.sleep(5000);
		driver.findElement(By.xpath("//input[@name='Passwd']")).sendKeys(reads.getGooglePassword());
		Thread.sleep(2000);
		WAIT("//span[text()='Next']", true);
		Thread.sleep(30000);
		
		}
		
		
		Thread.sleep(3000);
		WAIT("//div[@class='cfc-select-value ng-star-inserted']", true);
		Thread.sleep(3000);
        driver.findElement(By.xpath("//span[text()='"+reads.getRepositoryName()+"']")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("(//div[contains(@class,'cfc-select-value ng-star-inserted')])[2]")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//span[text()='devuser']")).click();
        Thread.sleep(3000);
       driver.findElement(By.xpath("(//input[@autocomplete='off'][contains(@id,'mat-input-0')])[2]")).sendKeys("kGqxF04S9'4z89>T");
        Thread.sleep(2000);
        driver.findElement(By.xpath("//span[@class='mdc-button__label'][contains(.,'Authenticate')]")).click();
		}
	
	public void createDataBaseAndExecuteSchema() throws InterruptedException, IOException {
		
		driver.get("https://console.cloud.google.com/sql/instances/eva-gen-sql-instance/databases?project=vb-eva-gen");
        Thread.sleep(5000);
        driver.findElement(By.xpath("//button[@aria-label='Create database'][contains(.,'Create database')]")).click();
        Thread.sleep(3000);
        driver.findElement(By.xpath("//input[@type='text'][contains(@id,'mat-input-0')]")).sendKeys(reads.getRepositoryName());
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[@type='submit'][contains(.,'Create')]")).click();
        logInToSqlStudioWithCreatedDataBase();
        Thread.sleep(5000);
        driver.findElement(By.xpath("//div[@name='title'][contains(.,'Editor 1')]")).click();
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Wait and find the editor
       // WebElement editor = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='mtk11']")));
        WebElement editor = driver.findElement(By.cssSelector(".view-lines"));

        // Use JavaScript to click and interact if direct interaction fails
        try {
        	Thread.sleep(5000);
            editor.click();
            Thread.sleep(2000);
            editor.sendKeys("Lets See");
        } catch (ElementNotInteractableException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editor);
        }

        // Read the SQL schema file
        String schemaFilePath = "C:\\Users\\darunkumar\\Downloads\\"+"sqlDB_Schema_"+downloadedSchemaFileName;
        String schemaContent = new String(Files.readAllBytes(Paths.get(schemaFilePath)));
        //System.out.println(schemaContent);
        
     // Input schema content
        try {
        	Thread.sleep(5000);
        	
        	
			Actions actions = new Actions(driver);
			String emoji = schemaContent;
			StringSelection stringSelection = new StringSelection(emoji);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			editor.click();
			Thread.sleep(2000);
			actions.keyDown(Keys.CONTROL);
			actions.sendKeys("v");
			//actions.keyUp(Keys.CONTROL);
			actions.build().perform();
	        Thread.sleep(2000);
			
			
			
        	
            //editor.sendKeys(schemaContent); // or use JavaScript to set the value
        } catch (ElementNotInteractableException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].innerText = arguments[1];", editor, schemaContent);
        }

	Thread.sleep(5000);
	driver.findElement(By.xpath("(//button[contains(@color,'primary')])[5]")).click();
	
	
	
	
	
	}
	
	
	}
	
	
	

