package os;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;

import config.Certificate;
import config.JSONUtil;
import db.DBUtil;
import exception.DebugException;
import exception.RWException;
import rw.Reader;
import rw.Writer;

/**
 *	OSUtil
 *	public OSUtil(JSONObject configJSON, Certificate certificate) throws DebugException
 *	public void syncForward() throws DebugException, RWException
 *	public void syncBackward() throws DebugException, RWException
 *	public String status()
 */
public class OSUtil {
	private Certificate certificate;
	private File folderFile;
	private String statusUri;
	private JSONObject statusJSON;
	private String queryShowTables;
	private	DateFormat dateFormat; 
	
	public OSUtil(JSONObject configJSON, Certificate certificate) throws DebugException {
		this.certificate = certificate;
		this.folderFile = makeFolderFile(configJSON);
		this.statusUri = makeStatusUri(configJSON);
		this.statusJSON = makeStatusJSON(configJSON);
		this.queryShowTables = makeQueryShowTables();
		this.dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	}
	
	/**
	 * push
	 * TODO: impl multi-thread
	 * @throws DebugException
	 * @throws RWException
	 */
	public void syncForward() throws DebugException, RWException {
		try {
			File[] excelList = folderFile.listFiles();
			for (File excel : excelList) {
				if (excel.isFile() && 
						excel.getName().length() > 4 && 
						excel.getName().substring(excel.getName().length() - 5, excel.getName().length()).equals(".xlsx")) {
					try {
						Reader reader = new Reader(excel, certificate);
						reader.batchRead();
					} catch (InvalidFormatException e) {
						throw new RWException(excel.getName() + " is not valid xlsx, please check file format");
					} catch (IOException e) {
						throw new RWException(excel.getName() + " has error creating XSSFWorkbook, please check file format");
					}
				}
			}
		} catch (ClassNotFoundException e) {
			throw new DebugException();
		}
		statusJSON.put("databaseUpdateTime", timeString());
		overrideStatusFile();
	}
	
	/**
	 * pull
	 * TODO: impl multi-thread
	 * @throws DebugException
	 * @throws RWException
	 */
	public void syncBackward() throws DebugException, RWException {
		try {
			List<String> tableList = tableList();
			for (String tableName : tableList) {
				try {
					Writer writer = new Writer(folderFile, tableName, certificate);
					writer.write();
				} catch (SQLException e) {
					throw new RWException("error in writing file " + tableName + ", no further info provided");
				}
			}
		} catch (ClassNotFoundException e) {
			throw new DebugException();
		} catch (FileNotFoundException e) {
			throw new DebugException();
		} catch (IOException e) {
			throw new DebugException();
		} catch (InvalidFormatException e) {
			throw new DebugException();
		}
		statusJSON.put("folderUpdateTime", timeString());
		overrideStatusFile();
	}
	
	/**
	 * get statusString from status.json
	 * @return statusString
	 */
	public String status() {
		StringBuilder sb = new StringBuilder();
		sb.append("folder last modify time: " + statusJSON.getString("folderUpdateTime") + "\n");
		sb.append("database last modify time: " + statusJSON.getString("databaseUpdateTime"));
		return sb.toString();
	}
	
	public List<String> tableList() throws DebugException {
		try {
			List<String> tableList = new ArrayList<>();
			Connection connection = DBUtil.connect(certificate);
			
			PreparedStatement ps = connection.prepareStatement(queryShowTables);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				tableList.add(resultSet.getString(1));
			}
			
			connection.close();
			
			return tableList;
		} catch (SQLException e) {
			throw new DebugException();
		}
	}
	
	public List<String> fieldList(String tableName) throws DebugException {
		try {
			List<String> fieldList = new ArrayList<>();
			Connection connection = DBUtil.connect(certificate);
			PreparedStatement ps = connection.prepareStatement(queryShowColumnsFrom(tableName));
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next()) {
				fieldList.add(resultSet.getString(1));
			}
			
			connection.close();
			
			return fieldList;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new DebugException();
		}
	}
	
	private String queryShowColumnsFrom(String tableName) {
		return "SHOW COLUMNS FROM `" + tableName + "`;";
	}
	
	private String makeQueryShowTables() {
		return "SHOW TABLES;";
	}
	
	private String makeStatusUri(JSONObject configJSON) {
		return configJSON.getString("statusUri");
	}
	
	private JSONObject makeStatusJSON(JSONObject configJSON) throws DebugException {
		File statusFile = new File(configJSON.getString("statusUri"));
		try {
			return JSONUtil.JSONof(statusFile);
		} catch (FileNotFoundException e) {
			throw new DebugException();
		} catch (IOException e) {
			throw new DebugException();
		}
	}
	
	private void overrideStatusFile() throws RWException, DebugException {
		try {
			PrintWriter pw = new PrintWriter(statusUri, "UTF-8");
			pw.println(statusJSON.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			throw new RWException(String.format("error at overriding %s, please check file permission", statusUri));
		} catch (UnsupportedEncodingException e) {
			throw new DebugException();
		}
	}
	
	private File makeFolderFile(JSONObject configJSON) {
		File folderFile = new File(configJSON.getString("folderUri"));
		return folderFile;
	}
	
	private String timeString() {
		return dateFormat.format(new Date()).trim().toString();
	}
}
