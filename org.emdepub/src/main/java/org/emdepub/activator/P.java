/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.activator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class P {

	/** Load properties from file */
	public static Properties loadPropertiesFromFile(String fileNameWithPath) {
		
		Properties properties = new Properties();
		try (FileInputStream fileInputStream = new FileInputStream(fileNameWithPath)) {
			properties.load(fileInputStream);
		} catch (FileNotFoundException fileNotFoundException) {
			L.e("FileNotFoundException in loadPropertiesFile", fileNotFoundException);
		} catch (IOException ioException) {
			L.e("IOException in loadPropertiesFile", ioException);
		}

		return properties;
	}

	/** Saves properties in file */
	public static void savePropertiesInFile(Properties properties, String comments, String fileNameWithPath) {
		
		try (FileOutputStream fileOutputStream = new FileOutputStream(fileNameWithPath)) {
			properties.store(fileOutputStream, comments);
		} catch (FileNotFoundException fileNotFoundException) {
			L.e("FileNotFoundException in savePropertiesInFile", fileNotFoundException);
		} catch (IOException ioException) {
			L.e("IOException in savePropertiesInFile", ioException);
		}
	}
}
