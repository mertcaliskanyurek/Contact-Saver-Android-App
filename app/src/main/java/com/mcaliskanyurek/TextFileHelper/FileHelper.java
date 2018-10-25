package com.mcaliskanyurek.TextFileHelper;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**This class helps creating and reading text files.
 * File directory external storage by default
 * If data parser null {@link #readTextFile()} method will return ArrayList
 *     each element is an string of the text file line.
 * In other case you can define a dataParser for convert string lines to your object
 * For use {@link #writeToTextFile(ArrayList)} <u>you must override toString() method
 *      to your object</u> */
public class FileHelper<T> {

    private Context ctx;
    private File file;
    private ITextDataParser dataParser;

    public FileHelper(@NonNull Context ctx, @NonNull String fileName, @Nullable ITextDataParser<T> dataParser)
    {
        this.ctx = ctx;
        file = new File(Environment.getExternalStorageDirectory()+"/"+fileName);
        this.dataParser = dataParser;
    }

    public FileHelper(@NonNull Context ctx, @NonNull File file,@Nullable ITextDataParser<T> dataParser)
    {
        this.ctx = ctx;
        this.file = file;
        this.dataParser = dataParser;
    }

    /**You must Override toString method on your object*/
    public void writeToTextFile(@NonNull ArrayList<T> lines) throws IOException {

        if(!file.exists())
            file.createNewFile();

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);

        for (Object s:lines) {
            bw.write(s.toString());
            bw.newLine();
        }
        bw.flush();
        fw.close();
        bw.close();

    }

    public ArrayList<T> readTextFile() throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file),"ISO-8859-9"));

        ArrayList<String> lines = new ArrayList<>();

        String line = bufferedReader.readLine();

        while (line!=null)
        {
            lines.add(line);
            line = bufferedReader.readLine();
        }

        fileReader.close();
        bufferedReader.close();

        if(dataParser==null)
            return (ArrayList<T>) lines;
        else
            return dataParser.parseData(lines);

    }


}
