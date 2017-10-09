package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import config.Certificate;

public class DBUtil {
	public static boolean isValidCertificate(Certificate certificate) {
		try {
			connect(certificate).close();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public static void loadConnector() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
	}
	
	public static Connection connect(Certificate certificate) throws SQLException {
		String url = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&useUnicode=true&characterEncoding=UTF8&useSSL=false", 
				certificate.getServer(), 
				certificate.getPort(), 
				certificate.getDbName(), 
				certificate.getUsername(), 
				certificate.getPassword());
		return DriverManager.getConnection(url);
	}
}