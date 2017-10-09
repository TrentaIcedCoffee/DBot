package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

public class JSONUtil {
	public static JSONObject JSONof(File file) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder JSONStringSB = new StringBuilder();
		String temp = "";
		while ((temp = br.readLine()) != null) {
			JSONStringSB.append(temp);
		}
		br.close();
		return new JSONObject(JSONStringSB.toString());
	}	
}