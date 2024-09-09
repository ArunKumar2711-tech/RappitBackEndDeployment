package afterPublishToGit;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ReadProperties {
	private static Properties proper = new Properties();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

	}

	public static void read() throws IOException {
		// Properties proper = new Properties();
		String Filepath = System.getProperty("user.dir") + "\\Config\\config.properties";
		FileInputStream file = new FileInputStream(Filepath);
		proper.load(file);

	}

	public String getUsername() {
		return proper.getProperty("username");
	}

	public String getPassword() {
		return proper.getProperty("password");
	}

	public String getRepositoryName() {
		return proper.getProperty("repositoryName");
	}

	public String getValuesForServicesAccountKeyFileJSON() {
		return proper.getProperty("SERVICE_ACCOUNT_KEYFILE_JSON");
	}

	public String getGoogleUsername() {
		return proper.getProperty("Gusername");
	}

	public String getGooglePassword() {
		return proper.getProperty("Gpassword");
	}

}
