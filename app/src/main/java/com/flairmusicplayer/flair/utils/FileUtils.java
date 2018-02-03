package com.flairmusicplayer.flair.utils;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: PulakDebasish
 */

public class FileUtils {

    public static String getDefaultStartDirectoryPath() {
        String musicDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
        String startFolderPath;
        if (!musicDirPath.isEmpty() && new File(musicDirPath).isDirectory()) {
            startFolderPath = musicDirPath;
        } else {
            File externalStorage = Environment.getExternalStorageDirectory();
            if (externalStorage.exists() && externalStorage.isDirectory()) {
                startFolderPath = externalStorage.getPath();
            } else {
                startFolderPath = "/";
            }
        }
        return startFolderPath;
    }

    @NonNull
    public static List<File> listFiles(@NonNull File directory, @Nullable FileFilter fileFilter) {
        List<File> fileList = new LinkedList<>();
        File[] found = directory.listFiles(fileFilter);
        if (found != null) {
            Collections.addAll(fileList, found);
        }
        return fileList;
    }

    public static boolean fileIsMimeType(File file, String mimeType, MimeTypeMap mimeTypeMap) {
        if (mimeType == null || mimeType.equals("*/*")) {
            return true;
        } else {
            // get the file mime type
            String filename = file.toURI().toString();
            int dotPos = filename.lastIndexOf('.');
            if (dotPos == -1) {
                return false;
            }
            String fileExtension = filename.substring(dotPos + 1).toLowerCase();
            String fileType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);
            if (fileType == null) {
                return false;
            }
            // check the 'type/subtype' pattern
            if (fileType.equals(mimeType)) {
                return true;
            }
            // check the 'type/*' pattern
            int mimeTypeDelimiter = mimeType.lastIndexOf('/');
            if (mimeTypeDelimiter == -1) {
                return false;
            }
            String mimeTypeMainType = mimeType.substring(0, mimeTypeDelimiter);
            String mimeTypeSubtype = mimeType.substring(mimeTypeDelimiter + 1);
            if (!mimeTypeSubtype.equals("*")) {
                return false;
            }
            int fileTypeDelimiter = fileType.lastIndexOf('/');
            if (fileTypeDelimiter == -1) {
                return false;
            }
            String fileTypeMainType = fileType.substring(0, fileTypeDelimiter);
            if (fileTypeMainType.equals(mimeTypeMainType)) {
                return true;
            }
        }
        return false;
    }

}
