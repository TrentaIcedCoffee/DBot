package rw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

import config.Certificate;

public class Reader implements Runnable {
	public static String EMPTY_TOKEN = "$_";
	public static String OVERFLOW_TOKEN = "$OVERFLOW";
	private Certificate certificate;
	private Sheet sheet;
	private String tableName;
	private List<String> headerBacktickList;
	
	public Reader(File file, Certificate certificate) throws ClassNotFoundException, InvalidFormatException, IOException {
		this.certificate = certificate;
		this.sheet = new Sheet(file);
		this.tableName = makeTableName(file);
		this.headerBacktickList = makeHeaderBacktickList(makeHeaderStringList());
	}

	public boolean batchRead() {
		try {
			Connection connection = connect();
			
			PreparedStatement ps = connection.prepareStatement(queryDropIfExists());
			ps.executeUpdate();
			ps.close();
			
			ps = connection.prepareStatement(queryCreateTable());
			ps.executeUpdate();
			ps.close();
			
			ps = connection.prepareStatement(queryInsertRow());
			for (int i = 1; i < sheet.getRowCount(); i++) {
				List<String> valueStringList = rowToStringList(i);
				for (int j = 0; j < sheet.getColCount(); j++) {
					if (valueStringList.get(j).equals(OVERFLOW_TOKEN)) {
						sheet.write(i, j, OVERFLOW_TOKEN);
					}
					ps.setString(j + 1, valueStringList.get(j));
				}
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			
			connection.close();
			
			overrideOriginal();
			
			return true;
		} catch (SQLException e) {
			System.out.println("query err during row insertion");
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void run() {
		batchRead();
	}
	
	private String makeTableName(File file) {
		String filename = file.getName();
		int end = filename.length() - 1;
		while (filename.charAt(end) != '.') {
			end--;
		}
		return filename.substring(0, end);
	}
	
	private List<String> makeHeaderBacktickList(List<String> headerStringList) {
		List<String> list = new ArrayList<>();
		for (String val : headerStringList) {
			list.add("`" + val + "`");
		}
		return list;
	}
	
	private Connection connect() throws SQLException {
		String url = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&useUnicode=true&characterEncoding=UTF8&useSSL=false", 
				certificate.getServer(), 
				certificate.getPort(), 
				certificate.getDbName(), 
				certificate.getUsername(), 
				certificate.getPassword());
		return DriverManager.getConnection(url);
	}
	
	private List<String> makeHeaderStringList() {
		List<String> rawList = rowToStringList(0);
		List<String> list = new ArrayList<>();
		int indexEmptyHeader = 0;
		for (int i = 0; i < sheet.getColCount(); i++) {
			if (rawList.get(i).equals("")) {
				sheet.write(0, i, EMPTY_TOKEN + indexEmptyHeader);
				list.add(EMPTY_TOKEN + (indexEmptyHeader++));
			} else {
				list.add(rawList.get(i));
			}
		}
		
		overrideOriginal();
		
		return list;
	}
	
	private boolean overrideOriginal() {
		try {
			sheet.overrideOriginal();
		} catch (FileNotFoundException e) {
			System.out.println("Path error when updating file. Fail to update");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("IO error when updating file. Fail to update");
			e.printStackTrace();
			return false;
		}
		return true;
	}
		
	private List<String> rowToStringList(int i) {
		List<String> stringList = new ArrayList<>();
		for (int j = 0; j < sheet.getColCount(); j++) {
			String val = sheet.get(i, j).toString().trim();
			if (val.length() > 255) {
				stringList.add(OVERFLOW_TOKEN);
			} else {
				stringList.add(val);
			}
		}
		return stringList;
	}
	
	private String queryCreateTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + "`" + tableName + "`" + " (" + "`id` INT NOT NULL AUTO_INCREMENT, ");
		for (String val : headerBacktickList) {
			sb.append(val + " VARCHAR(255) DEFAULT NULL, ");
		}
		sb.append("PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		return sb.toString();
	}
	
	private String queryInsertRow() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + "`" + tableName + "`" + " (" + String.join(", ", headerBacktickList) + ") VALUES (");
		for (int i = 0; i < sheet.getColCount(); i++) {
			if (i == sheet.getColCount() - 1) {
				sb.append("?");
			} else {
				sb.append("?, ");
			}
		}
		sb.append(");");
		return sb.toString();
	}
	
	private String queryDropIfExists() {
		return "DROP TABLE IF EXISTS `" + tableName + "`;";
	}
}
