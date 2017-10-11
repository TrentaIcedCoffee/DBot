package vision;

import java.awt.Color;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;

import config.Certificate;
import config.JSONUtil;
import db.DBUtil;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import exception.DebugException;
import exception.RWException;
import rw.Reader;
import rw.Writer;

import javax.swing.JFrame;

// TODO: remove unused
@SuppressWarnings("unused")
public class Vision extends JFrame {
	public static void main(String[] args) {
//		Vision test = new Vision();
//		test.setVisible(true);
    }
	
	private Certificate certificate;
	private String tableName;
	private String fieldName;
	
	public Vision(Certificate certificate, String tableName, String fieldName) {
		this.certificate = certificate;
		this.tableName = tableName;
		this.fieldName = fieldName;
	}

	
	
	private Map<String, Integer> freqMap() throws DebugException {
		try {
			Map<String, Integer> freqMap = new HashMap<>();
			
			Connection connection = DBUtil.connect(certificate);
			PreparedStatement ps = connection.prepareStatement(querySelectFieldFromTable(tableName, fieldName));
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next()) {
				freqMap.put(resultSet.getString(1), freqMap.getOrDefault(resultSet.getString(1), 1) + 1);
			}
			
			return freqMap;
		} catch (SQLException e) {
			throw new DebugException();
		}
	}
	
	private String querySelectFieldFromTable(String tableName, String fieldName) {
		return "SELECT `" + fieldName + "` FROM `" + tableName + "`;"; 
	}
	
//	private Map<String, Integer> freqMap(String tableName) throws SQLException {
//		
//		
//		Map<String, Integer> freqMap = new HashMap<>();
//		String query = "SELECT * FROM `" + tableName + "`;";
//		Connection connection = DBUtil.connect(certificate);
//		PreparedStatement ps = connection.prepareStatement(query);
//		ResultSet resultSet = ps.executeQuery();
//		
//		
//	}
}

































