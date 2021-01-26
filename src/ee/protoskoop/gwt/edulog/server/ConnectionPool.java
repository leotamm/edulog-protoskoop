package ee.protoskoop.gwt.edulog.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class ConnectionPool extends Thread {

	private static final Logger logger = Logger.getLogger(ConnectionPool.class);
	private String url;
	private Connection conn;
	private boolean finished;
	
	private boolean connected;
	

	public ConnectionPool() {
		connected = false;
		this.setName("Connection Monitor Thread");// jdbc:postgresql://localhost/eduLogDatabase"
		url = "jdbc:postgresql://" + Configuration.DB_HOST + ":" + Configuration.DB_PORT + "/" + Configuration.DB_NAME;
		finished = false;
		
		try {
			conn = DriverManager.getConnection(url, Configuration.DB_USER, Configuration.DB_PASS);
			logger.info("Connected to the PostgreSQL server successfully.");
            logger.info(url);
            connected = true;
        }
		catch (SQLException e) {
        	logger.error(e.getMessage());
        }
	}
	
	public void run() {
		while (!finished) {
			try {
				Thread.sleep(15000);
				if ((conn == null) || !conn.isValid(10000)) {
					logger.info("DB connection lost! Trying to re-establish connection..");
					conn = null;
					conn = DriverManager.getConnection(url);
				}
			}
			catch (SQLException e) {
				logger.error(e.getMessage());
			}
			catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void closeConnections() {
		finished = true;
		try {
        	conn.close();
        }
        catch (SQLException e) {
        	logger.error(e.getMessage());
        }
	}

}
