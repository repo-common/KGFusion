import javax.naming.PartialResultException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GraphFusion {

    /**
     * Integer numOfSelectedPairs: 筛选对个数
     * Double threshold: 筛选阈值
     */
    private Integer numOfSelectedPairs;
    private Double threshold;

    public GraphFusion(Integer numOfSelectedPairs, Double threshold) {
        this.numOfSelectedPairs = numOfSelectedPairs;
        this.threshold = threshold;
    }

    public void setNumOfSelectedPairs(Integer numOfSelectedPairs) {
        this.numOfSelectedPairs = numOfSelectedPairs;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Integer getNumOfSelectedPairs() {
        return numOfSelectedPairs;
    }

    public Double getThreshold() {
        return threshold;
    }

    /*
     * @description: TODO 执行此方法生成融合后的图
     * @params: similarityMatrix：相似度矩阵，numOfSelectedPairs：筛选的相似实体对数量，threshold：筛选阈值，util 使用处理文件和处理后转存配置路径
     * @return:
     * @author: DAI
     * @dateTime: 2021/2/21 0021 11:03
     */

    public void generateFusionGraph(double[][] similarityMatrix, int numOfSelectedPairs, double threshold, PathUtil util){
        // getTopKSimilarityPairs()
        System.out.println("Fusion graph process");
        // processAndDump()
        // transformAndDump(util);
    }


    /*
     * @description: TODO 生成相似实体对
     * @params: similarityMatrix：相似度矩阵，numOfSelectedPairs：筛选的相似实体对数量，threshold
     * @return: ArrayList<ArrayList<Integer>>
     * @author: DAI
     * @dateTime: 2021/2/21 0021 11:05
     */
    public ArrayList<ArrayList<Integer>> getTopKSimilarityPairs(double[][] similarityMatrix, int numOfSelectedPairs, double threshold){

        return new ArrayList<ArrayList<Integer>>();
    }



    /*
     * @description: TODO 处理并转存融合后的图文件 用整数表示的三元组
     * @params: topKSimilarityPairs：筛选对数量，util 使用到编码后的图和相应的字典转存路径
     * @return:
     * @author: DAI
     * @dateTime: 2021/2/21 0021 11:09
     */
    public void processAndDump(ArrayList<ArrayList<Integer>> topKSimilarityPairs, PathUtil util){

    }

    /*
     * @description: TODO 将数字表示的图转换成字面值表示的图文件
     * @params: util 使用到转存路径
     * @return:
     * @author: DAI
     * @dateTime: 2021/2/21 0021 11:17
     */
    private void transformAndDump(PathUtil util) {

    }



    public static void main(String[] args) {

//        int[][] similarityMatrix = SimilarityLinking.generateSimilarityMatrix(new int[1][1]);
        GraphFusion graphFusion = new GraphFusion(3, 0.8);
//        graphFusion.generateFusionGraph(similarityMatrix, graphFusion.getNumOfSelectedPairs(), graphFusion.getThreshold(),);


    }
}
