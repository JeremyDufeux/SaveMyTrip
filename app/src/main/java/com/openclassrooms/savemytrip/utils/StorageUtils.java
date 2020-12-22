package com.openclassrooms.savemytrip.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.openclassrooms.savemytrip.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class StorageUtils {

    private static File createOrGetFile(File destination, String filename, String folderName){
        File folder = new File(destination, folderName);
        return new File(folder, filename);
    }

    public static String getTextFromStorage(File rootDestination, Context context, String fileName, String folderName){
        File file = createOrGetFile(rootDestination, fileName, folderName);
        return readOnFile(context, file);
    }

    public static void setTextInStorage(File rootDestination, Context context, String fileName, String folderName, String text){
        File file = createOrGetFile(rootDestination, fileName, folderName);
        writeOnFile(context, text, file);
    }

    public static File getFileFromStorage(File rootDestination, Context context, String fileName, String folderName){
        return createOrGetFile(rootDestination, fileName, folderName);
    }

    // ----------------------------
    //  External Storage
    // ----------------------------

    public static boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    public static boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    // ----------------------------
    //  Read and write on storage
    // ----------------------------

    private static String readOnFile(Context context, File file){
        String result = null;
        if(file.exists()){
            BufferedReader br;
            try{
                br = new BufferedReader(new FileReader(file));
                try{
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null){
                        sb.append(line);
                        sb.append("\n");
                        line = br.readLine();
                    }
                    result = sb.toString();
                }
                finally {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getString(R.string.error_happened), Toast.LENGTH_SHORT).show();
            }
        }
        return result;
    }

    private static void writeOnFile(Context context, String text, File file){
        try{
            file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            Writer w = new BufferedWriter(new OutputStreamWriter(fos));

            try{
                w.write(text);
                w.flush();
                fos.getFD().sync();
            } finally {
                w.close();
                Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.error_happened), Toast.LENGTH_SHORT).show();
        }
    }
}
