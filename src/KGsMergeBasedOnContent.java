import javax.naming.InterruptedNamingException;
import javax.print.DocFlavor;
import javax.script.ScriptEngine;
import java.awt.*;
import java.net.Inet4Address;
import java.nio.file.Path;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

public class KGsMergeBasedOnContent {
    private List<KG> cleanedKGs;
    private List<String> rawDocuments;


    public KG runMerge(){

        return process();
    }

//    public HashMap<String, List<String>> computeWordsSimilarityBasedOnContent(){
//        return new HashMap<>();
//    }


    public KG process(){
        return new KG();
    }

    static class MergeTool{

        // function1
        // function2
        // function3
        // ...

        public static List<Pair<Node, Node>> geBagsOfSimilarWords(KG kg){
            return new ArrayList<>();
        }
    }

    public static class Encoder{


        public static HashMap<String, Integer> geEntityToIdxMapping(KG kg){
            return new HashMap<>();
        }

        public static HashMap<String, Integer> geRelationToIdxMapping(KG kg){
            return new HashMap<>();
        }

        public static HashMap<Integer, String> geIdxToEntityMapping(HashMap<String, Integer> entityToIdxDict){
            return new HashMap<>();
        }

        public static HashMap<Integer, String> geIdxToRelationMapping(HashMap<String, Integer> relationToIdxDict){
            return new HashMap<>();
        }

        public static List<Triple<Integer, Integer, Integer>> geEncodedKG(HashMap<String, Integer> entityToIdxDict, HashMap<String, Integer> relationToIdxDict){
            return new ArrayList<>();
        }
    }




    public KGsMergeBasedOnContent(List<KG> cleanedKGs, List<String> rawDocuments) {
        this.cleanedKGs = cleanedKGs;
        this.rawDocuments = rawDocuments;
    }

    public List<KG> getCleanedKGs() {
        return cleanedKGs;
    }

    public void setCleanedKGs(List<KG> cleanedKGs) {
        this.cleanedKGs = cleanedKGs;
    }

    public void setRawDocuments(List<String> rawDocuments) {
        this.rawDocuments = rawDocuments;
    }

    public List<String> getRawDocuments() {
        return rawDocuments;
    }

    public static void main(String[] args) {

    }
}
