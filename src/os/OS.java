package os;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;

import config.Certificate;
import config.JSONUtil;
import db.DBUtil;
import exception.ConfigurationFailException;
import exception.DebugException;
import exception.RWException;
import exception.UnimplementedException;

public class OS {
	public static void main(String[] args) {
		try {
			OS os = new OS();
			os.run();
		} catch (DebugException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		} catch (ConfigurationFailException e) {
			System.out.println("configuration exception, " + e.getMessage());
			System.exit(-1);
		} catch (UnimplementedException e) {
			System.out.println("unimplemented exception, " + e.getMessage());
			System.exit(-1);
		}
	}
	
	public static String rootString = "C:/DBot";
	public static String versionString = "0.0.0";
	private String welcomeString;
	private String helpString;
	private Certificate certificate;
	private JSONObject configJSON;
	private OSUtil OSUtil;
	
	public OS() throws DebugException, ConfigurationFailException, UnimplementedException {
		try {
			DBUtil.loadConnector();
		} catch (ClassNotFoundException e) {
			throw new ConfigurationFailException("jdbc driver missing, please check jdbc driver package");
		}
		this.welcomeString = makeWelcomeString();
		this.helpString = makeHelpString();
		this.configJSON = makeConfigJSON(rootString);
		this.certificate = makeCertificate(rootString);
		this.OSUtil = new OSUtil(configJSON, certificate);
	}
	
	private void run() {
		System.out.println(welcomeString);
		String input = "";
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print("DBot > ");
			input = scanner.nextLine().trim();
			switch (input) {
			case "push" :
				try {
					OSUtil.syncForward();
					System.out.println("push success");
				} catch (DebugException e) {
					System.out.println("push fail");
					System.out.println(e.getMessage());
				} catch (RWException e) {
					System.out.println("push fail");
					System.out.println("rw error, " + e.getMessage());
				}
				break;
			case "pull" :
				try {
					OSUtil.syncBackward();
					System.out.println("pull success");
				} catch (DebugException e) {
					System.out.println("pull fail");
					System.out.println(e.getMessage());
				} catch (RWException e) {
					System.out.println("pull fail");
					System.out.println("rw error, " + e.getMessage());
				}
				break;
			case "status" :
				System.out.println(OSUtil.status());
				break;
			case "list" :
				try {
					System.out.println(String.join(", ", OSUtil.tableList()));
				} catch (DebugException e) {
					System.out.println(e.getMessage());
				}
				break;
			case "version" :
				System.out.println("version: " + versionString);
				break;
			case "help" :
				System.out.println(helpString);
				break;
			case "exit" :
				System.out.println("Bye");
				System.out.println("Find a bug? Have an idea? Even a typo? Please contact with opposcript@gmail.com");
				scanner.close();
				System.exit(0);
			default :
				if (input.length() > 6 && input.substring(0, 6).equals("field ")) {
					try {
						String tableName = input.substring(6);
						Set<String> tableSet = OSUtil.tableList().stream().collect(Collectors.toSet());
						if (!tableSet.contains(tableName)) {
							System.out.println(String.format("table %s not in database", tableName));
							break;
						}
						System.out.println(String.join("\n", OSUtil.fieldList(tableName)));
					} catch (DebugException e) {
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
				} else if (input.length() > 3 && input.substring(0, 3).equals("vi ")) {
					// FIXME: ambiguous parse
					try {
						int indexOfSpace = 3;
						while (indexOfSpace < input.length()) {
							if (input.charAt(indexOfSpace) == ' ') {
								break;
							}
							indexOfSpace++;
						}
						if (indexOfSpace == input.length()) {
							System.out.println(String.format("%s is not recognized as a DBot command", input));
							break;
						}
						String tableName = input.substring(3, indexOfSpace);
						String fieldName = input.substring(indexOfSpace + 1);
						Set<String> tableSet = OSUtil.tableList().stream().collect(Collectors.toSet());
						if (!tableSet.contains(tableName)) {
							System.out.println(String.format("table %s not in database", tableName));
							break;
						}
						Set<String> fieldSet = OSUtil.fieldList(tableName).stream().collect(Collectors.toSet());
						if (!fieldSet.contains(fieldName)) {
							System.out.println(String.format("field %s not in table %s", fieldName, tableName));
							break;
						}
						OSUtil.vision(tableName, fieldName);
					} catch (DebugException e) {
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
				} else {
					System.out.println(String.format("%s is not recognized as a DBot command", input));
				} 
			}
		}
	}
	
	private JSONObject makeConfigJSON(String rootString) throws ConfigurationFailException, DebugException, UnimplementedException {
		try {
			File configFile = new File(rootString + File.separator + "config.json");
			JSONObject configJSON = JSONUtil.JSONof(configFile);
			if (!isValidConfig(configJSON)) {
				throw new ConfigurationFailException("config.json not complete, please check config.json");
			}
			validateStatusFile(configJSON);
			return configJSON;
		} catch (FileNotFoundException e) {
			throw new ConfigurationFailException("config.json not found, please check config.json");
		} catch (IOException e) {
			throw new ConfigurationFailException("IO error at JSONUtil.JSONof(config.json), please check config.json");
		}
	}
	
	private Certificate makeCertificate(String rootString) throws ConfigurationFailException {
		try {
			File certificateFile = new File(rootString + File.separator + "certificate.json");
			Certificate certificate = new Certificate(JSONUtil.JSONof(certificateFile));
			if (!DBUtil.isValidCertificate(certificate)) {
				throw new ConfigurationFailException("invalid certificate, please check certificate.json");
			}
			return certificate;
		} catch (FileNotFoundException e) {
			throw new ConfigurationFailException("certificate.json not found, please check config.json and certificate.json");
		} catch (IOException e) {
			throw new ConfigurationFailException("IO error at creating JSON or Certificate, please check certificate.json");
		}
	}
	
	/**
	 * @param configJSON
	 * @return true if configJSON contains folderUri, certificateUri
	 */
	private boolean isValidConfig(JSONObject configJSON) {
		if (configJSON != null && 
			configJSON.getString("folderUri") != null && 
			configJSON.getString("certificateUri") != null) {
			return true;
		} else {
			return false;
		}
	}
	
	private String makeWelcomeString() {
		return String.format("Welcome using DBot v %s, type help to see user manual", versionString);
	}
	
	private String makeHelpString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("DBot v %s\n", versionString));
		sb.append("push : synchronize forward\n");
		sb.append("pull : synchronize backward\n");
		sb.append("status : current status of folder and database\n");
		sb.append("list : view all tables in database\n");
		sb.append("field <tableName> : view all columns in the table\n");
		sb.append("vi <tableName> <fieldName> : view piechart of selected values\n");
		sb.append("help : view help info\n");
		sb.append("version : view version info\n");
		sb.append("exit : exit DBot");
		return sb.toString();
	}
	
	/**
	 * create status.json if not exists, throw ConfigurationFailException if status.json doesn't contain folderUpdateTime, databaseUpdateTime fields
	 * @param configJSON
	 * @throws ConfigurationFailException
	 * @throws DebugException
	 */
	private void validateStatusFile(JSONObject configJSON) throws ConfigurationFailException, DebugException {
		File statusFile = new File(configJSON.getString("folderUri") + File.separator + "status.json");
		if (!statusFile.exists()) {
			JSONObject statusJSON = new JSONObject();
			statusJSON.put("folderUpdateTime", "");
			statusJSON.put("databaseUpdateTime", "");
			
			try {
				PrintWriter pw = new PrintWriter(statusFile);
				pw.println(statusJSON.toString());
				pw.close();
			} catch (FileNotFoundException e) {
				throw new ConfigurationFailException("error at locating status.json, please check config.json and status.json");
			}
		} else {
			try {
				JSONObject statusJSON = JSONUtil.JSONof(statusFile);
				if (statusJSON.getString("folderUpdateTime") == null || statusJSON.getString("databaseUpdateTime") == null) {
					throw new ConfigurationFailException("missing fields in status.json, please check status.json");
				}
			} catch (FileNotFoundException e) {
				throw new ConfigurationFailException("status.json not found, please check status.json");
			} catch (IOException e) {
				throw new ConfigurationFailException("IO exception at status.json, please check status.json");
			}
		}
	}
}