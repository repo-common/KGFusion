package test;

import org.apache.commons.lang3.tuple.Pair;
import pipeLine.SchemaExtraction;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class TestSchema {
    public static void main(String[] args) throws IOException {
        HashMap<String, Pair<String, List<String>>> res = new SchemaExtraction().entranceWithRomWalk();

//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("type_prediction_RandomWalk.txt"));
        File fout = new File("type_prediction_RandomWalk.txt");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (String name: res.keySet()){
            String str = "";
            Pair<String, List<String>> realAndPredicts = res.get(name);
            str = name + "\t" + realAndPredicts.getLeft() + "\t" + realAndPredicts.getRight().toString();

            bw.write(str);
            bw.newLine();
        }

        bw.close();
    }
}
