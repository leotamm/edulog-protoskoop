package ee.protoskoop.gwt.edulog.server;

import java.io.FileReader;

import org.apache.log4j.Logger;
import org.ini4j.Ini;

public class Configuration {

	private static final Logger logger = Logger.getLogger(Configuration.class);
	public static String DB_HOST;
	public static int DB_PORT;
	public static String DB_NAME;
	public static String DB_USER;
	public static String DB_PASS;
	public static String LOG4J_PATH;
	public static String EMAIL_USER;
	public static String EMAIL_PASS;

	public static boolean loadConfiguration(String iniFile) {
		try {
			Ini ini = new Ini(new FileReader(iniFile));
			Ini.Section section = ini.get("databaseDefault");
			DB_HOST = getOrDefault(section.get("DATABASE_HOST"), "localhost");
			DB_PORT = Integer.parseInt(getOrDefault(section.get("DATABASE_PORT"), "5432"));
			DB_NAME = getOrDefault(section.get("DATABASE_NAME"), "eduLogDatabase");
			DB_USER = getOrDefault(section.get("DATABASE_USER"), "postgres");
			DB_PASS = getOrDefault(section.get("DATABASE_PASSWORD"), "docker");
			section = ini.get("log");
			LOG4J_PATH = getOrDefault(section.get("LOG4J_PATH"), "log4j.conf");
			section = ini.get("emailService");
			EMAIL_USER = getOrDefault(section.get("EMAIL_USER"), "");
			EMAIL_PASS = getOrDefault(section.get("EMAIL_PASS"), "");

			return true;
		}
		catch (Exception e) {
			logger.error("Error reading configuration file");
			return false;
		}
	}

	private static String getOrDefault(String value, String defValue) {
		if (value != null) {
			return value;
		}
		else {
			return defValue;
		}
	}

}
