/* Log Values; log-values.com 2023 */
package com.staticwebsite.commons;

import java.beans.Expression;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

/** Streams, files and other u t i l s */
public class CU {

	/* Strings */

	/** _ as thousands separator */
	private static final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
	static {
		DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
		symbols.setGroupingSeparator('_');
		decimalFormat.setDecimalFormatSymbols(symbols);
	}

	/** "" */
	public static final String ES = ""; 

	/** CR, LF, etc. */
	public static final String E = System.lineSeparator(); 
	
	/* Streams */
    
	/** Loads an entire input stream in one string */
	public static String loadBufferInString(byte[] buffer, boolean replaceBom) {
		
		if (buffer.length > 0 ) {
			String content = new String(buffer, StandardCharsets.UTF_8);
	        /* UTF8_BOM */
	        if (replaceBom && content.startsWith("\uFEFF")) {
	        	content = content.substring(1);
	        }
	        return content;
		}
		
		return "";
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
//			CL.e("loadInputStreamInString", ioException);
		}
		
		return new byte[0];
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

	/** Not null and not void */
	public static String trimStrip(String text) {
		
		if (text == null) {
			return null;
		}

		return text.trim().strip();
	}

	/** Not null and not strip trim empty string */
	public static boolean isEmpty(String text) {
		
		return (text == null) || (trimStrip(text).length() == 0);
	}
	
	/* Files */
	
	/** File into string */
	@SneakyThrows(IOException.class)
	public static byte[] loadFileInBuffer(String fileName) {

		return Files.readAllBytes(Paths.get(fileName));
	}

	/** String into file */
	@SneakyThrows(IOException.class)
	public static void saveBufferToFile(byte[] buffer, String fileName) {

		Files.write(Paths.get(fileName), buffer);
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
	public static final String S = File.separator; 

	/** Exists */
	public static boolean fileExists(String fileNameWithPath) {

		return Files.exists(Paths.get(fileNameWithPath));
	}

	/** Valid */
	public static boolean isValidPath(String fileOrFolderNameWithPath) {

		try {
			Paths.get(fileOrFolderNameWithPath);	
		} catch (InvalidPathException invalidPathException) {
			return false;
		}
		
		return true;
	}

	/** Valid */
	public static String findInvalidPathMessage(String fileOrFolderNameWithPath) {

		try {
			Paths.get(fileOrFolderNameWithPath);	
		} catch (InvalidPathException invalidPathException) {
			return invalidPathException.getMessage();
		}
		
		return "";
	}
	
	/** Delete file */
	@SneakyThrows(IOException.class)
	public static void deleteFile(String fileNameWithPath) {

		Files.deleteIfExists(Paths.get(fileNameWithPath));
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
	@SneakyThrows(IOException.class)
	private static void deleteFolder(String folderNameWithPath, boolean deleteContentsOnly) {

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

	/** Modern */
	@SneakyThrows(IOException.class)
	public static void deleteFolderAndItsContents(String folderNameWithPath) {
	    
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
	
	/** Create all folders */
	@SneakyThrows(IOException.class)
	public static void createFoldersIfNotExists(String folderNameWithPath) {

		Path catalogBlocksFolderPath = Paths.get(folderNameWithPath); 
		if (Files.notExists(catalogBlocksFolderPath)) {
			Files.createDirectories(catalogBlocksFolderPath);
		}
	}

	/** Create all folders */
	@SneakyThrows(IOException.class)
	public static long findFileSizeInBytes(String fileNameWithPath) {

		if (fileExists(fileNameWithPath)) {
			return Files.size(Paths.get(fileNameWithPath));
		}
		
		return -1;
	}

    /**
     * https://stackoverflow.com/questions/6214703/copy-entire-directory-contents-to-another-directory/10068306#10068306
     */
	private static class CopyFileVisitorIgnoreForExtensions extends SimpleFileVisitor<Path> {
		
		private final Path targetPath;
		private Path sourcePath = null;
		
		private final ArrayList<String> ignoredExtensions = new ArrayList<>();

		public CopyFileVisitorIgnoreForExtensions(Path targetPath, String ignoreExtensions) {
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

	private static class CopyFileVisitor extends SimpleFileVisitor<Path> {
		
		private final Path targetPath;
		private Path sourcePath = null;
		
		public CopyFileVisitor(Path targetPath) {
			this.targetPath = targetPath;
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
			
			Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
			return FileVisitResult.CONTINUE;
		}
	}

	/** Copy folders */
	@SneakyThrows(IOException.class)
	public static void copyFolderContentIntoFolder(String sourceFileOrFolder, String targetFolder) {
	
		Files.walkFileTree(Paths.get(sourceFileOrFolder), new CopyFileVisitor(Paths.get(targetFolder)));
	}

	@SneakyThrows(IOException.class)
	public static void copyFolderWithNameIntoFolder(String sourceFileOrFolder, String targetFolder) {
	
		String newTargetFolder = targetFolder + S + getFileName(sourceFileOrFolder);
		createFoldersIfNotExists(newTargetFolder);
		Files.walkFileTree(Paths.get(sourceFileOrFolder), new CopyFileVisitor(Paths.get(newTargetFolder)));
	}

	/** Copy folders */
	@SneakyThrows(IOException.class)
	public static void copyFolderContentIntoFolder(String sourceFolder, String targetFolder, String ignoreForExtensions) {
		
		Files.walkFileTree(Paths.get(sourceFolder), new CopyFileVisitorIgnoreForExtensions(Paths.get(targetFolder), ignoreForExtensions));	
	}

	/** Copy file */
	@SneakyThrows(IOException.class)
	public static void copyFileIntoFolder(String sourceFileNameWithFullPath, String targetFolderWithFullPath) {
	
		Path sourceFilePath = Paths.get(sourceFileNameWithFullPath);
		Files.copy(sourceFilePath, Paths.get(targetFolderWithFullPath, sourceFilePath.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
	}

	/** Copy file */
	@SneakyThrows(IOException.class)
	public static void copyFileIntoRenamedFile(String sourceFileNameWithFullPath, String targetFileWithFullPath) {
	
		Files.copy(Paths.get(sourceFileNameWithFullPath), Paths.get(targetFileWithFullPath), StandardCopyOption.REPLACE_EXISTING);
	}

	/** File / folder path */
	public static String getFileFolder(String fileName) {
		
		Path parentPath = Paths.get(fileName).getParent(); 
		return parentPath == null ? "" : parentPath.toString(); 
	}

	/** File / folder name */
	public static String getFileName(String fileName) {
		
		int fileFolderLength = getFileFolder(fileName).length();
		return fileFolderLength == 0 ? fileName : fileName.substring(fileFolderLength + S.length()); 
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
		if ((fileName == null) || ((len = fileName.length()) == 0) || ((ch = fileName.charAt(len - 1)) == '/') || (ch == '\\') || (ch == '.')) {
			return "";
		}

		int dotInd = fileName.lastIndexOf('.');
		int sepInd = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if (dotInd <= sepInd) {
			return "";
		}
		else {
			return fileName.substring(dotInd + 1).toLowerCase();
		}
	}
	
	/** From IusCL */
	public static String formatSize(long size) {

		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		
		double b = size / (1024d * 1024d * 1024d);
		String suffix;
		if (b >= 1) {
			suffix = "GB";
		}
		else {
			b = size / (1024d * 1024d);
			if (b >= 1) {
				suffix = "MB";
			}
			else {
				b = size / 1024d;
				if (b >= 1) {
					suffix = "KB";
				}
				else {
					return size + " B";
				}
			}
		}
		
		return decimalFormat.format(b).replace(",", ".") + " " + suffix;
	}

	/** From IusCL */
	public static String formatTime(long milliseconds) {

		StringBuilder sb = new StringBuilder();
		
		long hours = milliseconds / (60 * 60 * 1000);
		if (hours > 0) {
			sb.append(hours + "h");
			milliseconds = milliseconds - (hours * 60 * 60 * 1000);
		}
		
		long minutes = milliseconds / (60 * 1000);
		if (minutes > 0) {
			sb.append(minutes + "m");
			milliseconds = milliseconds - (minutes *  60 * 1000);
		}
		else {
			if (hours > 0) {
				sb.append("0m");	
			}
		}

		long seconds = milliseconds / 1000;
		if (seconds > 0) {
			sb.append(seconds + "s");
		}
		else {
			if (minutes > 0) {
				sb.append("0s");	
			}
		}
		
		return sb.toString();
	}

	/** From IusCL */
	public static String formatSeconds(long milliseconds) {

		long seconds = milliseconds / 1000;
		milliseconds = milliseconds - (seconds * 1000);
		
		return "" + Double.parseDouble(seconds + "." + milliseconds);
	}

	/** From IusCL */
	public static String formatCount(long count) {

		return decimalFormat.format(count);
	}

	/** From IusCL */
	public static String formatTotalElements(int size) {

		return decimalFormat.format(size);
	}

	/** List folder files */
	public static List<String> findFolderFiles(String folderName, String filter) {
		
		ArrayList<String> result = new ArrayList<>();
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderName), filter)) {
			
			stream.forEach(fileOrFolder -> {
				if (!Files.isDirectory(fileOrFolder, LinkOption.NOFOLLOW_LINKS)) {
					result.add(fileOrFolder.toString());
				}
			});
		} catch (IOException ioException) {
//			CL.e("findFolderFiles; folderName: " + folderName + ", filter: " + filter, ioException);
		}
		
		return result;
	}

	/* Utilities */
	
	/** Apache lang, text */
	public static String deleteWhitespace(final String str) {
		
		if (isEmpty(str)) {
			return str;
		}
		final int sz = str.length();
		final char[] chs = new char[sz];
		int count = 0;
		for (int i = 0; i < sz; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				chs[count++] = str.charAt(i);
			}
		}
		if (count == sz) {
			return str;
		}
		
		return new String(chs, 0, count);
	}
	
	/** Sort _ before a before A */
	public static void sortStrings(List<String> unsorted) {

		Collections.sort(unsorted, Collator.getInstance(Locale.ENGLISH));
	}

	/** Sort _ before a before A */
	public static void sortStringsDotAfter(List<String> unsorted, List<String> names, List<String> hiddenNames) {

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
		
		return "\"" + source.replace("\"", "\\\\\"") + "\"";
	}

	
	/* Arrays */
	
	public static String[] toArray(Stream<String> stream) {
		
		return stream.toArray(String[]::new);
	}

	public static List<String> toArrayList(Stream<String> stream) {
		
		return stream.collect(Collectors.toCollection(ArrayList<String>::new));
	}

	public static String[] toArray(List<String> arrayList) {
		
		return toArray(arrayList.stream());
	}

	public static List<String> toArrayList(String[] array) {
		
		return toArrayList(Arrays.stream(array));
	}
	
	/** Loads an entire input stream in one map */
	public static void loadInputStreamInHashMap(InputStream inputStream, Map<String, String> map, String separator) {
		
		String lines = loadInputStreamInString(inputStream);
		lines.lines().forEachOrdered(line -> {
			String[] keyValue = line.split(separator);
			map.put(keyValue[0].toLowerCase(), keyValue[1]);
		});
	}
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	@SneakyThrows({DatabindException.class, StreamReadException.class, IOException.class})
	public static <T> T deserialize(String src, Class<T> valueType) {
		
		return objectMapper.readValue(new File(src), valueType);
	}

	/**  */
	@SneakyThrows({DatabindException.class, StreamWriteException.class, IOException.class})
	public static void serialize(String resultFile, Object value) {

		objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File (resultFile), value);
	}
	
	public static void exec(Object target, String methodName, Object argument) {
		
		try {
			Object[] oneArgument = new Object[1];
			oneArgument[0] = argument;
			(new Expression(target, methodName, oneArgument)).execute();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static void exec(Object target, String methodName) {
		
		try {
			(new Expression(target, methodName, new Object[0])).execute();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static Object val(Object target, String methodName) {

		try {
			return (new Expression(target, methodName, new Object[0])).getValue();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	@SneakyThrows({InterruptedException.class, ExecutionException.class})
	public static void await(Future<?> future) {

		future.get();
	}
}
