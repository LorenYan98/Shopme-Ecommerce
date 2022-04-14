package com.shopme.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
	// uploadDir is the file address that you want to save;
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);
	public static void saveFile(String uploadDir, String fileName,
			MultipartFile multipartFile) throws IOException {
		//	get(String first, String... more)
		// Converts a path string, or a sequence of strings that when joined form a path string, to a Path.
		Path uploadPath = Paths.get(uploadDir);
		
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		//getInputStream(), Return an InputStream to read the contents of the file from.
		try(InputStream inputStream = multipartFile.getInputStream()){
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new IOException("Could not save file" + fileName, ex);
		}
	}
	
	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);
		try {
			// new function => Stream stream = Files.list(Paths.get(dir))
			
			Files.list(dirPath).forEach(file ->{
				// isDirectory(Path path, LinkOption... options)
				// Tests whether a file is a directory.
				if(!Files.isDirectory(file)) {
					try {
						Files.delete(file);
					} catch(IOException ex) {
						LOGGER.error("Could not delete file: " + file);
//						System.out.println("Could not delete file: " + file);
					}
				}
			});
		}catch(IOException ex) {
			LOGGER.error("Could not list directory: " + dirPath);
		}
	}

	public static void removeDir(String dir) {
		cleanDir(dir);
		
		try {
			Files.delete(Paths.get(dir));
		} catch (IOException e) {
			LOGGER.error("Could not remove directory: " + dir);
		}
		
	}
}
