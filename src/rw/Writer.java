package rw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

import config.Certificate;
import db.DBUtil;

public class Writer implements Runnable {
	private Certificate certificate;
	private Sheet sheet;
	private String tableName;
	private List<String> headerStringList;
	private int colCount;
	
	public Writer(File path, String tableName, Certificate certificate) throws ClassNotFoundException, FileNotFoundException, IOException, SQLException, InvalidFormatException {
		String filePath = path + File.separator + tableName + ".xlsx";
		if (!Files.exists(Paths.get(filePath), new LinkOption[] {LinkOption.NOFOLLOW_LINKS})) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			workbook.createSheet();
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			workbook.close();
		}
		
		this.certificate = certificate;
		
		this.tableName = tableName;
		this.headerStringList = makeHeaderStringList();
		this.colCount = headerStringList.size();
		
		this.sheet = new Sheet(new File(filePath));
	}
	
	@Deprecated
	public Writer(String dir, String tableName, Certificate certificate) throws FileNotFoundException, IOException, SQLException, InvalidFormatException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		String filePath = filePath(dir, tableName);
		if (!Files.exists(Paths.get(filePath), new LinkOption[] {LinkOption.NOFOLLOW_LINKS})) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			workbook.createSheet();
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			workbook.close();
		}
		
		this.certificate = certificate;
		
		this.tableName = tableName;
		this.headerStringList = makeHeaderStringList();
		this.colCount = headerStringList.size();
		
		this.sheet = new Sheet(new File(filePath));
	}
	
	public boolean write() throws SQLException, FileNotFoundException, IOException {
		for (int j = 0; j < colCount; j++) {
			sheet.write(0, j, headerStringList.get(j));
		}
		
		Connection connection = DBUtil.connect(certificate);
		
		PreparedStatement ps = connection.prepareStatement(querySelect());
		ResultSet resultSet = ps.executeQuery();
		
		int i = 1;
		while (resultSet.next()) {
			for (int j = 0; j < colCount; j++) {
				sheet.write(i, j, resultSet.getString(j + 2));
			}
			i++;
		}
		
		sheet.overrideOriginal();
		
		ps.close();
		connection.close();
		
		return true;
	}
	
	@Override
	public void run() {
		try {
			write();
		} catch (SQLException | IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private List<String> makeHeaderStringList() throws SQLException {
		List<String> list = new ArrayList<>();
		Connection connection = DBUtil.connect(certificate);
		
		PreparedStatement ps = connection.prepareStatement(queryShowColumns());
		ResultSet resultSet = ps.executeQuery();
		
		resultSet.next();
		while (resultSet.next()) {
			list.add(resultSet.getString(1));
		}
		
		ps.close();
		connection.close();
		
		return list;
	}
	
	private String queryShowColumns() {
		return "SHOW COLUMNS FROM `" + tableName + "`;";
	}
	
	private String querySelect() {
		return "SELECT * FROM `" + tableName + "`;";
	}
	
	@Deprecated
	private String filePath(String dir, String tableName) {
		if (dir.charAt(dir.length() - 1) != File.separatorChar) {
			dir += File.separator;
		}
		return dir + tableName + ".xlsx";
	}
}
