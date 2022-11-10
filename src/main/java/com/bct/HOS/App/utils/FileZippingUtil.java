package com.bct.HOS.App.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileZippingUtil {

	
	public static File createZipFile(String folderPath, String zipFilePath) {

		// create zip folder and file
		File zipFile = null;
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		try {
			// create the output stream to zip file result
			fileWriter = new FileOutputStream(zipFilePath);
			zip = new ZipOutputStream(fileWriter);

			// add folders
			addFolderToZip("", folderPath, zip);

			// clean and close the object streams
			zip.flush();
			zip.close();
			fileWriter.close();

			// create the file reference
			zipFile = new File(zipFilePath);
		} catch (Exception e) {
		}
		// finally {
		// zipFile.deleteOnExit();
		// }

		return zipFile;
	}
	
	private static void addFolderToZip(String path, String folderPath,
			ZipOutputStream zip) {

		File folder = null;

		try {
			// create the folder object
			folder = new File(folderPath);

			// check for empty folder
			if (folder.list().length == 0) {

				// add file to zip
				addFileToZip(path, folderPath, zip, true);

			} else {
				// list the files in the folder
				for (String fileName : folder.list()) {
					if (path.equals("")) {
						addFileToZip(folder.getName(), folderPath + "/"
								+ fileName, zip, false);
					} else {
						addFileToZip(path + "/" + folder.getName(), folderPath
								+ "/" + fileName, zip, false);
					}
				}
			}
		} catch (Exception e) {
		}
	}
	
	private static void addFileToZip(String path, String filePath,
			ZipOutputStream zip, boolean emptyFlag) {

		File folder = null;

		try {
			// create input file
			folder = new File(filePath);
			if (emptyFlag) {
				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()
						+ "/"));
			} else {
				if (folder.isDirectory()) {
					addFolderToZip(path, filePath, zip);
				} else {
					byte[] buffer = new byte[1024];
					int len;
					FileInputStream in = new FileInputStream(filePath);
					zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
					while ((len = in.read(buffer)) > 0) {
						zip.write(buffer, 0, len);
					}
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}
	
	public static void main(String abc[]) {
		
		FileZippingUtil fzip = new FileZippingUtil();
		fzip.createZipFile("C:/Users/am110759/Desktop/temp/image", "C:/Users/am110759/Desktop/temp/firstzip.zip");
		
	}
}
