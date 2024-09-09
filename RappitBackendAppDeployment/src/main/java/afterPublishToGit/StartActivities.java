package afterPublishToGit;

import java.io.IOException;

import org.openqa.selenium.WebDriver;

public class StartActivities extends BaseClass {
	
	WebDriver driver = getDriver();
	static StartActivities refer;

	public static void main(String[] args) throws IOException, InterruptedException {
		
		refer = new StartActivities();
		refer.loginToGitlab();
		refer.checkForCloseIcon();
		refer.searchingAndSelectingTheRequiredRepository();
        refer.findingEnvironmentInGitLab();
        refer.navigatingToPipelineToCheckThePipelineStatus();
        refer.actionsBasedOnPipelineStatus();
        refer.generatingSQLScript();
        refer.sqlDBSchema();
        refer.logInToSqlStudio();
        refer.createDataBaseAndExecuteSchema();
        
        
        
	}

}
