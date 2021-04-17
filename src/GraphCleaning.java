import java.util.ArrayList;



public class GraphCleaning {

    /*
     * @description: TODO 执行此方法生成G1，G2...
     * @params: util 文件配置，在该方法中，主要指定清洗后的图存储目录
     * @return:
     * @author: DAI
     * @dateTime: 2021/2/21 0021 10:44
     */
    public static void runClean(PathUtil util){
        ArrayList<String> fileList = (ArrayList<String>) util.getFileList(util.getRawGraphsDir());
        for(String file : fileList){
            processPerFile(file, util.getCleanedGraphsDir());
        }

        System.out.println("clean process");

    }

    /*
     * @description: TODO 处理每一个文件
     * @params: file:未处理的单个图文件  cleanedGraphDir: 处理后存储目录
     * @return:
     * @author: DAI
     * @dateTime: 2021/2/21 0021 10:48
     */
    public static void processPerFile(String file, String cleanedGraphsDir){
        // 处理完毕后写入文件
    }




    public static void main(String[] args) {
//        PathUtil util = new PathUtil();
//        GraphCleaning.runClean(util);
    }
}

