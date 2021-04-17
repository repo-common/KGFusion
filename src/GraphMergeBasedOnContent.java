import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GraphMergeBasedOnContent {


    /*
     * @description: TODO 执行此方法生成合并后的图文件
     * @params: util 路径配置文件 此方法中主要使用清洗后的图文件目录以及原始文档目录
     * @return:
     * @author: DAI
     * @dateTime: 2021/2/21 0021 10:51
     */
    public static void runMerge(PathUtil util){
        ArrayList<String> cleanedGraphsList = (ArrayList<String>) util.getFileList(util.getCleanedGraphsDir());
        ArrayList<String> rawDocumentsList = (ArrayList<String>) util.getFileList(util.getRawDocumentsDir());
        ArrayList<ArrayList<String>> bagsOfSimilarWords = generateBagsOfSimilarWords(cleanedGraphsList, rawDocumentsList);
        mergeAndDump(bagsOfSimilarWords, cleanedGraphsList, util);

        System.out.println("merge process");
    }


    /*
     * @description: TODO 生成相似单词组成的词袋
     * @params: cleanedGraphsList：清洗后图文件路径list, rawDocumentsList: 原始文档路径list
     * @return: ArrayList<ArrayList<String>> 词袋
     * @author: DAI
     * @dateTime: 2021/2/21 0021 10:53
     */
    public static ArrayList<ArrayList<String>> generateBagsOfSimilarWords(ArrayList<String> cleanedGraphsList, ArrayList<String> rawDocumentsList){
        return new ArrayList<>();
    }


    public static void mergeAndDump(ArrayList<ArrayList<String>> bagsOfSimilarWords, ArrayList<String> cleanedGraphsList, PathUtil util){
        mergeAndDumpLiteralGraph(bagsOfSimilarWords, cleanedGraphsList, util.getMergedGraphPath());
        GraphEncoder.encodeAndDumpGraph(util);

    }



    private static void mergeAndDumpLiteralGraph(ArrayList<ArrayList<String>> bagsOfSimilarWords, ArrayList<String> cleanedGraphsList, String mergedGraphPath) {

    }

//    public static void dumpStringGraph(){
//
//    }
//
//    public static void dumpEncodedGraph(){
//
//    }
//
//    public static HashMap<String, Integer> relationToIdx(String filePath){
//
//        return new HashMap<>();
//    }
//
//    public static HashMap<String, Integer> LiteralToIdx(String filePath){
//
//        return new HashMap<>();
//    }
//
//    public static HashMap<Integer, String> IdxToRelation(HashMap<String, Integer> relationToIdx){
//        return new HashMap<>();
//    }
//
//    public static HashMap<Integer, String> literalToIdx(HashMap<Integer, String> literalToIdx){
//        return new HashMap<>();
//    }

    public static void main(String[] args) {
//        PathUtil util = new PathUtil();
//        GraphMergeBasedOnContent.runMerge(util);

    }
}
