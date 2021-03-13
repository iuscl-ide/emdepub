/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.activator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Streams, files and other operations */
public class F {


	/* Strings */

	/** CR, LF, etc. */
	public static final String e = System.lineSeparator(); 
	
	/* Streams */
    
	/** Loads an entire input stream in one string */
	public static String loadBufferInString(byte[] buffer, boolean replaceBom) {
		
		if (buffer != null ) {
			String content = new String(buffer, StandardCharsets.UTF_8);
	        /* UTF8_BOM */
	        if (replaceBom && content.startsWith("\uFEFF")) {
	        	content = content.substring(1);
	        }
	        return content;
		}
		
		return null;
	}

	/** String into buffer */
	public static byte[] saveStringToBuffer(String source) {

		return source.getBytes(StandardCharsets.UTF_8);
	}
	
	/** Loads an entire input stream in buffer */
	public static byte[] loadInputStreamInBuffer(InputStream inputStream) {
		
		try (inputStream) {
			return inputStream.readAllBytes();
		}
		catch (IOException ioException) {
			L.e("loadInputStreamInString", ioException);
		}
		
		return null;
	}
	
	/** Loads an entire input stream in one string */
	public static String loadInputStreamInString(InputStream inputStream, boolean replaceBom) {
		
		byte[] buffer = loadInputStreamInBuffer(inputStream);
		
		return loadBufferInString(buffer, replaceBom);
	}

	/** Loads an entire input stream in one string */
	public static String loadInputStreamInString(InputStream inputStream) {
		
		return loadInputStreamInString(inputStream, true);
	}

	/** Load resource */
	public static InputStream getResourceAsInputStream(String resourceName) {
		
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
	}

	/** Load text resource */
	public static String getResourceAsText(String textResourceName) {
		
		return loadInputStreamInString(getResourceAsInputStream(textResourceName));
	}

	/** Not null and not void */
	public static boolean isEmpty(String text) {
		
		if (text == null) {
			return true;
		}

		if (text.trim().strip().length() == 0) {
			return true;
		}

		return false;
	}
	
	/* Files */
	
	/** File into string */
	public static byte[] loadFileInBuffer(String fileName) {
		
		try {
			return Files.readAllBytes(Paths.get(fileName));
		}
		catch (IOException ioException) {
			L.e("loadFileInString => fileName: " + fileName, ioException);
		}
		
		return null;
	}

	/** String into file */
	public static void saveBufferToFile(byte[] buffer, String fileName) {

		try {
			Files.write(Paths.get(fileName), buffer);
		}
		catch (IOException ioException) {
			L.e("saveBufferToFile, fileName: " + fileName, ioException);
			throw new E(ioException);
		}
	}

	/** File into string */
	public static String loadFileInString(String fileName, boolean replaceBom) {

		byte[] buffer = loadFileInBuffer(fileName);
		
		return loadBufferInString(buffer, replaceBom);
	}

	/** File into string */
	public static String loadFileInString(String fileName) {
		
		return loadFileInString(fileName, true);
	}

	/** String into file */
	public static void saveStringToFile(String source, String fileName) {

		byte[] buffer = saveStringToBuffer(source);
		saveBufferToFile(buffer, fileName);
	}
	
	
	/* File operations */
	
	/** "\" or "/" */
	public static final String s = File.separator; 

	/** Exists */
	public static boolean fileExists(String fileNameWithPath) {

		return Files.exists(Paths.get(fileNameWithPath));
	}
	
	/** Delete file */
	public static void deleteFile(String fileNameWithPath) {

		try {
			Files.deleteIfExists(Paths.get(fileNameWithPath));
		} catch (IOException ioException) {
			L.e("deleteFile; fileNameWithPath: " + fileNameWithPath, ioException);
		}
	}
	
	/** Delete folder contents */
	public static void deleteFolderContentsOnly(String folderNameWithPath) {

		deleteFolder(folderNameWithPath, true);
	}

	/** Delete folder and contents */
	public static void deleteFolder(String folderNameWithPath) {

		deleteFolder(folderNameWithPath, false);
	}

	/** Delete folder and/only contents */
	private static void deleteFolder(String folderNameWithPath, boolean deleteContentsOnly) {

		try {
			Path rootPath = Paths.get(folderNameWithPath);
			if (Files.notExists(rootPath)) {
				return;
			}
			List<Path> pathsToDelete = Files.walk(rootPath).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
			if (deleteContentsOnly) {
				pathsToDelete.remove(rootPath);
			}
			for(Path path : pathsToDelete) {
			    Files.deleteIfExists(path);
			}
		}
		catch (IOException ioException) {
			L.e("deleteFolder; folderNameWithPath: " + folderNameWithPath + ", deleteContentsOnly: " + deleteContentsOnly, ioException);
			throw new E(ioException);
		}
	}

	/* https://stackoverflow.com/questions/20281835/how-to-delete-a-folder-with-files-using-java */
	public static void deleteFolderAndItsContent(final Path folder) throws IOException {
	    
		Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
	        @Override
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	            Files.delete(file);
	            return FileVisitResult.CONTINUE;
	        }
	        @Override
	        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	            if (exc != null) {
	                throw exc;
	            }
	            Files.delete(dir);
	            return FileVisitResult.CONTINUE;
	        }
	    });
	}

	/** Modern */
	public static void deleteFolderAndItsContents(String folderNameWithPath) {
	    
		try {
			Path rootPath = Paths.get(folderNameWithPath);
			if (Files.notExists(rootPath)) {
				return;
			}
			Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			        Files.delete(file);
			        return FileVisitResult.CONTINUE;
			    }
			    @Override
			    public FileVisitResult postVisitDirectory(Path folder, IOException ioException) throws IOException {
			        if (ioException != null) {
			            throw ioException;
			        }
			        Files.delete(folder);
			        return FileVisitResult.CONTINUE;
			    }
			});
		}
		catch (IOException ioException) {
			L.e("deleteFolderAndItsContents; folderNameWithPath: " + folderNameWithPath, ioException);
		}
	}

	
	/** Create all folders */
	public static void createFoldersIfNotExists(String folderNameWithPath) {

		try {
			Path catalogBlocksFolderPath = Paths.get(folderNameWithPath); 
			if (Files.notExists(catalogBlocksFolderPath)) {
				Files.createDirectories(catalogBlocksFolderPath);
			}
		}
		catch (IOException ioException) {
			L.e("createFoldersIfNotExists; folderNameWithPath: " + folderNameWithPath, ioException);
			throw new E(ioException);
		}
	}

	/** Create all folders */
	public static long findFileSizeInBytes(String fileNameWithPath) {

		long fileSize = -1; 
		try {
			fileSize = Files.size(Paths.get(fileNameWithPath)); 
		}
		catch (IOException ioException) {
			L.e("findFileSizeInBytes; fileNameWithPath: " + fileNameWithPath, ioException);
			throw new E(ioException);
		}
		return fileSize;
	}

    /**
     * https://stackoverflow.com/questions/6214703/copy-entire-directory-contents-to-another-directory/10068306#10068306
     */
	private static class CopyFileVisitor extends SimpleFileVisitor<Path> {
		
		private final Path targetPath;
		private Path sourcePath = null;
		
		private final ArrayList<String> ignoredExtensions = new ArrayList<String>();

		public CopyFileVisitor(Path targetPath, String ignoreExtensions) {
			this.targetPath = targetPath;
			
			for (String ignoredExtension : ignoreExtensions.split(";")) {
				ignoredExtensions.add(ignoredExtension.trim());
			}
		}

		@Override
		public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
			
			if (sourcePath == null) {
				sourcePath = dir;
			}
			else {
				Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
			
			String fileExtension = getExtension(file.toString());
			if (ignoredExtensions.contains(fileExtension)) {
				return FileVisitResult.CONTINUE;
			}
			Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
			return FileVisitResult.CONTINUE;
		}
	}

	/** Copy folders */
	public static void copyFolders(Path sourcePath, Path targetPath) throws IOException {
	
		Files.walkFileTree(sourcePath, new CopyFileVisitor(targetPath, ""));
	}

	/** Copy folders */
	public static void copyFileOrFolderIntoFolder(String sourceFileOrFolder, String targetFolder) {
	
		try {
			Files.walkFileTree(Paths.get(sourceFileOrFolder), new CopyFileVisitor(Paths.get(targetFolder), ""));
		}
		catch (IOException ioException) {
			L.e("copyFileOrFolderIntoFolder; source: " + sourceFileOrFolder + ", target: " + targetFolder, ioException);
		}
	}
	
	/** Copy folders */
	public static void copyFolders(String sourceFolder, String targetFolder, String ignoreForExtensions) {
		
		try {
			Files.walkFileTree(Paths.get(sourceFolder), new CopyFileVisitor(Paths.get(targetFolder), ignoreForExtensions));	
		}
		catch (IOException ioException) {
			L.e("copyFolders; sourceFolder: " + sourceFolder + ", targetFolder: " + targetFolder + ", ignoreForExtensions: " + ignoreForExtensions, ioException);
			throw new E(ioException);
		}
	}

	/** Copy file */
	public static void copyFile(String sourceFileNameWithPath, String targetFileNameWithPath) {
	
		try {
			Files.copy(Paths.get(sourceFileNameWithPath), Paths.get(targetFileNameWithPath), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioException) {
			L.e("copyFile, sourceFileNameWithPath: " + sourceFileNameWithPath + ", targetFileNameWithPath: " + targetFileNameWithPath, ioException);
		}
	}

	/** File / folder path */
	public static String getFileFolder(String fileName) {
		
		Path parentPath = Paths.get(fileName).getParent(); 
		return parentPath == null ? "" : parentPath.toString(); 
	}

	/** File / folder name */
	public static String getFileName(String fileName) {
		
		int fileFolderLength = getFileFolder(fileName).length();
		return fileFolderLength == 0 ? fileName : fileName.substring(fileFolderLength + s.length()); 
	}

	/** File / folder just name */
	public static String getFileNameWithoutExtension(String fileName) {
		
		String fileNameWithExtension = getFileName(fileName);
		String fileExtension = getExtension(fileName);
		return fileNameWithExtension.substring(0, fileNameWithExtension.length() - (fileExtension.length() + 1)); 
	}

	/**
	 * https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java/21974043
	 */
	public static String getExtension(String fileName) {

		char ch;
		int len;
		if (fileName == null || (len = fileName.length()) == 0 || (ch = fileName.charAt(len - 1)) == '/' || ch == '\\'
				|| // in the case of a directory
				ch == '.') // in the case of . or ..
			return "";
		int dotInd = fileName.lastIndexOf('.'),
				sepInd = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if (dotInd <= sepInd)
			return "";
		else
			return fileName.substring(dotInd + 1).toLowerCase();
	}
	
	/** Size */
	public static long getFileSize(String fileName) {

		File javaFile = new File(fileName);
		return Long.valueOf(javaFile.length());
	}
	
	/** From IusCL */
	public static String formatSize(long size) {

		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		
		double b = size / (1024 * 1024 * 1024);
		String suffix = "B";
		if (b > 1) {
			suffix = "GB";
		}
		else {
			b = size / (1024 * 1024);
			if (b > 1) {
				suffix = "MB";
			}
			else {
				b = size / 1024;
				if (b > 1) {
					suffix = "KB";
				}
				else {
					return size + " B";
				}
			}
		}
		
		return decimalFormat.format(b) + " " + suffix;
	};



	/* Utilities */
	
	/** Sort _ before a before A */
	public static void sortStrings(ArrayList<String> unsorted) {

		Collections.sort(unsorted, Collator.getInstance(Locale.ENGLISH));
	}

	/** Sort _ before a before A */
	public static void sortStringsDotAfter(ArrayList<String> unsorted, ArrayList<String> names, ArrayList<String> hiddenNames) {

		/* Sort */
		for (String name : unsorted) {
			if (name.startsWith(".")) {
				hiddenNames.add(name);
			}
			else {
				names.add(name);
			}
		}
		sortStrings(names);
		sortStrings(hiddenNames);
	}

	/** \" */
	public static String escapeQuotes(String source) {
		
		return "\"" + source.replaceAll("\"", "\\\\\"") + "\"";
	}

	public static String[] toArray(Stream<String> stream) {
		
		return stream.toArray(String[]::new);
	}

	public static ArrayList<String> toArrayList(Stream<String> stream) {
		
		return stream.collect(Collectors.toCollection(ArrayList<String>::new));
	}

	public static String[] toArray(ArrayList<String> arrayList) {
		
		return toArray(arrayList.stream());
	}

	public static ArrayList<String> toArrayList(String[] array) {
		
		return toArrayList(Arrays.stream(array));
	}
	
	/** Loads an entire input stream in one map */
	public static void loadInputStreamInHashMap(InputStream inputStream, HashMap<String, String> map, String separator) {
		
		String lines = loadInputStreamInString(inputStream);
		lines.lines().forEachOrdered(line -> {
			String[] keyValue = line.split(separator);
			map.put(keyValue[0].toLowerCase(), keyValue[1]);
		});
	}
}
