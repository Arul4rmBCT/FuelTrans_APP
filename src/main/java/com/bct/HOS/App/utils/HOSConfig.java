package com.bct.HOS.App.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class HOSConfig {

	public String value;

	public String getValue(String key) {
		readProperty(key);
		return value;
	}

	private void setValue(String value) {
		this.value = value;
	}

	private void readProperty(String key) {
		FileReader reader = null;
		Properties p = null;
		try {
			reader = new FileReader("hosinfo.properties");

			p = new Properties();
			p.load(reader);
			setValue(p.getProperty(key));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (p != null)
					p.clear();
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
