/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.activator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import lombok.Cleanup;
import lombok.SneakyThrows;

public class P {

	/** Load properties from file */
	@SneakyThrows({FileNotFoundException.class, IOException.class})
	public static Properties loadPropertiesFromFile(String fileNameWithPath) {
		
		@Cleanup FileInputStream fileInputStream = new FileInputStream(fileNameWithPath);

		Properties properties = new Properties();
		properties.load(fileInputStream);

		return properties;
	}

	/** Saves properties in file */
	@SneakyThrows({FileNotFoundException.class, IOException.class})
	public static void savePropertiesInFile(Properties properties, String comments, String fileNameWithPath) {
		
		@Cleanup FileOutputStream fileOutputStream = new FileOutputStream(fileNameWithPath);
		properties.store(fileOutputStream, comments);
	}
}
