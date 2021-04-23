package utils;

import java.io.*;
import java.util.List;

public class IO {
    public static void writeFile(List<String> content, String fileName) throws IOException, FileNotFoundException {
        File fout = new File(fileName);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (String s : content) {
            bw.write(s.toString());
            bw.newLine();
        }
        bw.close();
    }

    public static void main(String[] args) throws IOException {

    }
}
