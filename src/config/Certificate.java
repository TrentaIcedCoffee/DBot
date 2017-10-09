package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.*;

public class Certificate {
	private String server;
	private String port;
	private String dbName;
	private String username;
	private String password;
	
	public static boolean isValidCertificateJSON(JSONObject certificateJSON) {
		if (certificateJSON == null || 
				!certificateJSON.has("server") || 
				!certificateJSON.has("port") || 
				!certificateJSON.has("dbName") || 
				!certificateJSON.has("username") || 
				!certificateJSON.has("password")) {
			return false;
		}
		return true;
	}
	
	public Certificate(JSONObject certificateJSON) {
		this.server = certificateJSON.getString("server");
		this.port = certificateJSON.getString("port");
		this.dbName = certificateJSON.getString("dbName");
		this.username = certificateJSON.getString("username");
		this.password = certificateJSON.getString("password");
	}
	
	@Deprecated
	public Certificate(String server, String port, String dbName, String username, String password) {
		this.server = server;
		this.port = port;
		this.dbName = dbName;
		this.username = username;
		this.password = password;
	}
	
	@Deprecated
	public Certificate(File file) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder jsonStringSB = new StringBuilder();
		String temp = "";
		while ((temp = br.readLine()) != null) {
			jsonStringSB.append(temp);
		}
		br.close();
		JSONObject certificateJSON = new JSONObject(jsonStringSB.toString());
		this.server = certificateJSON.getString("server");
		this.port = certificateJSON.getString("port");
		this.dbName = certificateJSON.getString("dbName");
		this.username = certificateJSON.getString("username");
		this.password = certificateJSON.getString("password");
	}
	
	@Deprecated
	public Certificate(String path) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		StringBuilder jsonStringSB = new StringBuilder();
		String temp = "";
		while ((temp = br.readLine()) != null) {
			jsonStringSB.append(temp);
		}
		br.close();
		JSONObject certificateJSON = new JSONObject(jsonStringSB.toString());
		this.server = certificateJSON.getString("server");
		this.port = certificateJSON.getString("port");
		this.dbName = certificateJSON.getString("dbName");
		this.username = certificateJSON.getString("username");
		this.password = certificateJSON.getString("password");
	}
	
	public String getServer() {
		return server;
	}
	
	public String getPort() {
		return port;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}
