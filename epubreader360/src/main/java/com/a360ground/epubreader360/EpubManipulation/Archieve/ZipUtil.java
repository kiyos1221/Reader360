package com.a360ground.epubreader360.EpubManipulation.Archieve;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Kiyos Solomon on 11/1/2016.
 */
public class ZipUtil {

	private static final String TAG = "ArchiveUtils";
	private static final int BUFFER_SIZE = 1024 * 2;
	/*Un zip the epub to the app dir*/
	public static final boolean unzipAll(String filename,InputStream zipFile, Context context){
		File epubFolder = context.getDir(filename, Context.MODE_PRIVATE);
		Log.i(TAG, "[METHOD] void unzipAll(zipFile:" + filename + ", targetDir:" + epubFolder + ")");
		ZipInputStream zis = new ZipInputStream(zipFile);
		ZipEntry zentry = null;

		// if exists remove

		// unzip all entries
		try {
			while ((zentry = zis.getNextEntry()) != null) {
                String fileNameToUnzip = zentry.getName();
                File targetFile = new File(epubFolder, fileNameToUnzip);

                // if directory
                if (zentry.isDirectory()) {
                    (new File(targetFile.getAbsolutePath())).mkdirs();
                }
                else {
                    // make parent dir
                    (new File(targetFile.getParent())).mkdirs();
                    unzipEntry(zis, targetFile);
                    Log.d(TAG, "Unzip file: " + targetFile);
                }
            }
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}


		return true;
	}
	private static final File unzipEntry(ZipInputStream zis, File targetFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(targetFile);

		byte[] buffer = new byte[BUFFER_SIZE];
		int len = 0;
		while ((len = zis.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
		}

		return targetFile;
	}

}
