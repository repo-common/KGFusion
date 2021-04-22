package pipeLine;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

import com.huaban.analysis.jieba.JiebaSegmenter;

import publicDataStructure.Entity;
import publicDataStructure.Item;
import publicDataStructure.KG;
import publicDataStructure.Node;
import publicDataStructure.Triple;
import Jama.Matrix;
import Jama.SingularValueDecomposition;


public class KGsMergeBasedOnContent {

    private List<KG> cleanedKGs;
    private List<Item> rawDocuments;

    public KG runMerge() throws IOException {
        return process();
    }

    public KG process() throws IOException {
        List<KG> kgs=getCleanedKGs();
        List<Item> items=getRawDocuments();
//        List<String> simNodeIds=MergeTool.ComputeSim(MergeTool.Svd(MergeTool.ComputeTF(kgs, items), 1));
        List<String> simNodeIds=MergeTool.ComputeSim(MergeTool.ComputeTF(kgs, items));

        //0.前期数据处理，此阶段为获取entitylist和triplelist;
        List<Entity> tempentitys = new ArrayList<Entity>();
        List<Entity> entitys = new ArrayList<Entity>();//entitylist集合用于修改
        List<Entity> entitys1 = new ArrayList<Entity>();//entitylist集合用于查询元素（不修改）
        List<Triple> temptriples = new ArrayList<Triple>();
        List<Triple> triples = new ArrayList<Triple>();
        List<Triple> newtriples = new ArrayList<Triple>();
        for (KG kg : kgs) {
            temptriples = kg.getTriples();
            for (int j = 0; j < temptriples.size(); j++) {
                triples.add(temptriples.get(j));
            }
        }
        for (Triple triple : triples) {
            tempentitys.add(triple.getHead());
        }
        for (Triple triple : triples) {
            tempentitys.add(triple.getTail());
        }
        HashMap<String, Integer> removeentity = new HashMap<String, Integer>();//entitys去重
        for (Entity entity : tempentitys) {
            if (!removeentity.containsKey(entity.getEntityId())) {
                removeentity.put(entity.getEntityId(), 1);
                entitys.add(entity);
                entitys1.add(entity);
            }
        }
        for(Triple triple:triples){//triple中的entity集合去重
            for(Entity entity:entitys) {
                if (triple.getHead().getEntityId().equals(entity.getEntityId())) {
                    triple.setHead(entity);
                } else if (triple.getTail().getEntityId().equals(entity.getEntityId())) {
                    triple.setTail(entity);
                }
            }
        }//0.结束


        //合并过程
        //1.寻找被合并ID最小的结点
        for (int i = simNodeIds.size()-1; i >= 0; i--) {
            String[] pair1 = simNodeIds.get(i).split(",");
            String s1=pair1[0];
            String s2=pair1[1];
            for(int j = i-1; j >= 0; j--){
                String[] pair2 = simNodeIds.get(j).split(",");
                if(pair2[1].equals(s1)){
                    s1=pair2[0];
                }
            }
            simNodeIds.set(i,s1+","+s2);
        }//1.结束

        //2.Simnodeids去重
        HashSet Simnodeset = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = simNodeIds.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (Simnodeset.add(element))
                newList.add(element);
        }
        simNodeIds.clear();
        simNodeIds.addAll(newList);
        //2.去重结束

        //3.删除被合并结点，修改相关三元组
        List<String> removeid = new ArrayList<String>();//用于存储要删除的实体ID集
        for (int k = 0; k < simNodeIds.size(); k++) {
            for (Entity entity : entitys) {
                String[] simnodepair = simNodeIds.get(k).split(",");//相似结点对存储在一维数组
                if (entity.getEntityId().equals(simnodepair[1])) {//相似对中后一个相似的结点 更改为前一个结点
                    for (Triple triple:triples) {
                        Entity entity1 = triple.getHead();
                        Entity entity2 = triple.getTail();
                        if (entity2.getEntityId().equals(entity.getEntityId())) {//修改三元组
                            int b = Integer.parseInt(simnodepair[0]) - 1;
                            triple.setTail(entitys1.get(b));
                        } else if (entity1.getEntityId().equals(entity.getEntityId())) {
                            int b = Integer.parseInt(simnodepair[0]) - 1;
                            triple.setHead(entitys1.get(b));
                        }
                    }
                    String i = entity.getEntityId();//修改完成三元组，记录需要删去的已合并的结点ID
                    if (!removeid.contains(i)) {
                        removeid.add(i);
                    }
                }
            }
        }
        for (int iu = 0; iu < removeid.size(); iu++) {//通过记录的实体ID删除结点
            for (Entity entity : entitys) {
                if (entity.getEntityId().equals(removeid.get(iu))) {
                    entitys.remove(entity);
                    break;
                }
            }
        }
        removeid.clear();//清空List，方便后续操作，以记录头尾实体相同的三元组的实体ID并删除。

        HashMap<String,Integer> triplemap = new HashMap<>();//将新生成的triple放入newtriples
        for (Entity entity : entitys) {
            triplemap.put(entity.getEntityId(), 1);
        }
        List<Integer> removelist=new ArrayList<>();//需删除的三元组ID list
        List<String> headtailids= new ArrayList<>(); //头尾实体ID LIST
        for(Triple triple:triples){
            String headid=triple.getHead().getEntityId();
            String tailid=triple.getTail().getEntityId();
            if(headid.equals(tailid)){//头尾实体相同，舍去三元组
                if(!removeid.contains(headid)){
                    removeid.add(headid);//同时去掉该实体
                }
                removelist.add(Integer.parseInt(triple.getTripleId()));//三元组删除ID集合
            }
            headtailids.add(headid+","+tailid);
        }
        for(int i=0;i<headtailids.size();i++){//三元组去重
            String first = headtailids.get(i);
            List<Integer> relist=new ArrayList<>();
            for(int j=i+1;j<headtailids.size();j++){
                if(first.equals(headtailids.get(j))){
                    relist.add(j+1);
                }
            }
            if(relist.size()!=0){
                removelist.addAll(relist);
            }
        }
        for (int iu = 0; iu < removelist.size(); iu++) {//通过记录的三元组ID删除三元组
            for (Triple triple : triples) {
                if (Integer.parseInt(triple.getTripleId())==(removelist.get(iu))) {
                    triples.remove(triple);
                    break;
                }
            }
        }
        List<String> removeids=new ArrayList<>();//如果头尾实体相同的三元组中的实体在别的三元组出现过，则不能删除
        for(int iu = 0; iu < removeid.size(); iu++){
            int count=0;
            for(Triple triple:triples){
                if(triple.getHead().getEntityId().equals(removeid.get(iu))||triple.getTail().getEntityId().equals(removeid.get(iu))) {
                    count++;
                }
            }
            if(count>=1){
                String id = removeid.get(iu);
                removeids.add(id);
            }
        }
        for(int iu = 0; iu < removeids.size(); iu++) {//在待删结点idList中去掉在别的三元组出现过的实体ID
            for(String id :removeid){
                if(removeids.get(iu).equals(id)){
                    removeid.remove(id);
                    break;
                }
            }
        }
        for (int iu = 0; iu < removeid.size(); iu++) {//通过记录的结点ID删除结点
            for (Entity entity : entitys) {
                if (entity.getEntityId().equals(removeid.get(iu))) {
                    entitys.remove(entity);
                    break;
                }
            }
        }

        List<Node> newNodes = new ArrayList<Node>();//修改完成，将新的ENtity集合转成newnodelist
        for (Entity entity : entitys) {
            String nodeid = entity.getEntityId();
            String nodename = entity.getEntityName();
            String typeid=entity.getTypeId();
            String parentid = entity.getParentId();
            String itemid = entity.getItemId();
            Node node = new Node(nodeid, nodename,typeid, parentid, itemid);
            newNodes.add(node);
        }
        List<String> headtailids1= new ArrayList<>();//修改完成，将新的triple集合转为newtriples集合
        for(Triple triple:triples){
            String headid=triple.getHead().getEntityId();
            String tailid=triple.getTail().getEntityId();
            headtailids1.add(headid+","+tailid);
        }
        List<Integer> tripleids=new ArrayList<>();
        for(int c=0;c<headtailids1.size();c++){
            String[] tempid=headtailids1.get(c).split(",");
            if(triplemap.containsKey(tempid[0])&& triplemap.containsKey(tempid[1])){
                tripleids.add(c);
            }
        }
        for(int id:tripleids){
            newtriples.add(triples.get(id));
        }
        //3.结束


        creatTxtFile("kgTriple1");
        File file2 = new File(filenameTemp);
        FileOutputStream fos2 = null;
        PrintWriter pw2 = null;
        fos2 = new FileOutputStream(file2);
        pw2 = new PrintWriter(fos2);
        for(Triple triple:newtriples){
            if(triple!=null&&triple.getTail()!=null&&triple.getHead()!=null) {
                pw2.write(triple.getHead().getEntityId() + "_" + triple.getHead().getEntityName() + " , " + triple.getRela() + " , " + triple.getTail().getEntityId() + "_" + triple.getTail().getEntityName() + "\t");
                pw2.write("\n");
                pw2.flush();
            }
        }
        pw2.close();
        fos2.close();


        return new KG(newNodes,newtriples);
    }

    //文件保存路径
    private static String filenameTemp;
    private static String path = "d:/";
    static boolean creatTxtFile(String name) throws IOException {
        boolean flag = false;
        filenameTemp = path + name + ".txt";
        File filename = new File(filenameTemp);
        if (!filename.exists()) {
            filename.createNewFile();
            flag = true;
        }
        return flag;
    }

    static class MergeTool {
        static HashMap<String,String> entityidname= new HashMap<>();
        public static double[][] ComputeTF(List<KG> kgs, List<Item> items) {
            List<Node> tempentitys = new ArrayList<Node>();
            List<Node> entitys = new ArrayList<Node>();
            int j = 0;
            for (KG kg : kgs) {
                tempentitys = kg.getNodes();
                for (int i = 0; i < tempentitys.size(); i++) {
                    entitys.add(tempentitys.get(i));
                }
            }
            for(Node node:entitys){
                entityidname.put(node.getNodeId(),node.getNodeName());
            }
            double[][] tfidfMatrix = new double[entitys.size()][items.size()];
            List<String> cutWords = new ArrayList<String>();
            //分词模块
            JiebaSegmenter segmenter = new JiebaSegmenter();
            for (Item item : items) {
                String text = item.getItemText();
                String result = segmenter.sentenceProcess(text).toString();
                cutWords.add(result);
            }
            //存放（单词，单词数量）
            HashMap<String, Integer> dict = new HashMap<String, Integer>();
            //存放（单词，单词词频）
            HashMap<String, Double> tf = new HashMap<String, Double>();
            //存放（单词，tfidf）
            HashMap<String, Double> tfidf = new HashMap<String, Double>();
            //计算TFIDF矩阵
            for (String word : cutWords) {
                String[] k = word.split(",");
                int length = k.length + 1;
                int count = 0;
                for (Node node : entitys) {
                    for (int i = 0; i < k.length; i++) {
                        String te = node.getNodeName();
                        if (k[i].contains(te)) {
                            count++;
                        }
                    }
                    dict.put(node.getNodeId(), count);
                    count = 0;
                    for (Map.Entry<String, Integer> entry : dict.entrySet()) {
                        double wordTf = (double) entry.getValue() / length;
                        tf.put(entry.getKey(), wordTf);
                    }
                }
                int D = 135; //总条目数目
                for (Node node : entitys) {
                    int Dt = 0;// Dt为出现该实体的条目数目
                    for (String word1 : cutWords) {
                        if (word1.contains(node.getNodeName())) {
                            Dt++;
                        }
                    }
                    double idfvalue = (double) Math.log(Float.valueOf(D) / (1 + Dt));
                    tfidf.put(node.getNodeId(), idfvalue * tf.get(node.getNodeId()));
                }
                for (Map.Entry<String, Double> entry : tfidf.entrySet()) {
                    String idd = entry.getKey();
                    int i = Integer.parseInt(idd) - 1;
                    double vv = entry.getValue();
                    tfidfMatrix[i][j] = vv;
                }
                j++;
            }
            return tfidfMatrix;
        }// 方法：计算TF-IDF矩阵
        public static double[][] Svd(double[][] tfidfmatrix,int truncatedDimension){
            Matrix matrix= Matrix.constructWithCopy(tfidfmatrix);
            SingularValueDecomposition svd = matrix.svd();
            Matrix u = svd.getU();
            Matrix s = svd.getS();
            // 注意，这里是v的转置
            Matrix v = svd.getV().transpose();

            int notZeroLength = 0;
            for (int i = 0; i < s.getRowDimension(); i++){
                if (s.get(i,i) > truncatedDimension){
                    notZeroLength++; // 统计>truncatedDimension的特征值个数
                }
            }

            Matrix newUMatrix = new Matrix(u.getRowDimension(), notZeroLength);
            Matrix newSMatrix = new Matrix(notZeroLength, notZeroLength);
            Matrix newVMatrix = new Matrix(notZeroLength, v.getColumnDimension());


            for (int i = 0; i < u.getRowDimension(); i++){
                for (int j = 0; j < notZeroLength; j++){
                    newUMatrix.set(i, j, u.get(i,j));
                }
            }
            for (int i = 0; i < notZeroLength; i++){
                for (int j = 0; j < notZeroLength; j++){
                    newSMatrix.set(i, j, s.get(i,j));
                }
            }
            for (int i = 0; i < notZeroLength; i++){
                for (int j = 0; j < v.getColumnDimension(); j++){
                    newVMatrix.set(i, j, v.get(i, j));
                }
            }

            double[][] m1=newUMatrix.times(newSMatrix).times(newVMatrix).getArray();
            return m1;
        }//方法：计算SVD矩阵
        public static List<String> ComputeSim(double[][] matrix) throws IOException {
            creatTxtFile("simnode");
            File file2 = new File(filenameTemp);
            FileOutputStream fos2 = null;
            PrintWriter pw2 = null;
            fos2 = new FileOutputStream(file2);
            pw2 = new PrintWriter(fos2);
            List<String> simNodeIds = new ArrayList<>();//List存放相似度大于0.9的实体ID对
            for (int i = 0; i <= matrix.length - 1; i++) {
                ArrayList va = new ArrayList();
                for (int j = 0; j <= matrix[i].length - 1; j++) { // va=matrix[1][]
                    va.add(matrix[i][j]);
                }
                String inode=String.valueOf(i+1);
                pw2.write(inode+"_"+entityidname.get(inode)+"\t");
                for (int a = 0; a <= matrix.length - 1; a++) {
                    ArrayList vb = new ArrayList();
                    for (int b = 0; b <= matrix[a].length - 1; b++) { //vb=matrix[1][] [2][] [3][]...[331][]
                        vb.add(matrix[a][b]);
                    }
                    int size = va.size();
                    double simVal = 0;
                    double num = 0;
                    double den = 1;
                    double powa_sum = 0;
                    double powb_sum = 0;
                    for (int k = 0; k < size; k++) {
                        String ssa = String.valueOf(va.get(k));
                        String ssb = String.valueOf(vb.get(k));
                        double sa = Double.parseDouble(ssa);
                        double sb = Double.parseDouble(ssb);
                        num = num + sa * sb;
                        powa_sum = powa_sum + (double) Math.pow(sa, 2);
                        powb_sum = powb_sum + (double) Math.pow(sb, 2);
                    }
                    double sqrta = (double) Math.sqrt(powa_sum);
                    double sqrtb = (double) Math.sqrt(powb_sum);
                    den = sqrta * sqrtb;
                    simVal = num / den;

                    if(i<a && simVal>=0.9){
                        int aid=i+1;
                        int bid=a+1;
                        simNodeIds.add(aid + "," + bid);
                        pw2.write("\n"+"距离:"+ String.valueOf(simVal) + " --> " +String.valueOf(bid)+"_"+entityidname.get(String.valueOf(bid))+ "\t");
                    }

                }
                pw2.write("\r\n");
            }
            pw2.close();
            fos2.close();
            return simNodeIds;
        }//方法：计算余弦相似度，返回相似结点对
    }

    public static class Encoder {
        public static HashMap<String, Integer> geEntityToIdxMapping(KG kg) {
            return new HashMap<>();
        }
        public static HashMap<String, Integer> geRelationToIdxMapping(KG kg) {
            return new HashMap<>();
        }
        public static HashMap<Integer, String> geIdxToEntityMapping(HashMap<String, Integer> entityToIdxDict) {
            return new HashMap<>();
        }
        public static HashMap<Integer, String> geIdxToRelationMapping(HashMap<String, Integer> relationToIdxDict) {
            return new HashMap<>();
        }
    }

    public KGsMergeBasedOnContent(List<KG> cleanedKGs, List<Item> rawDocuments) {
        this.cleanedKGs = cleanedKGs;
        this.rawDocuments = rawDocuments;
    }

    public List<KG> getCleanedKGs() {
        return cleanedKGs;
    }

    public void setCleanedKGs(List<KG> cleanedKGs) {
        this.cleanedKGs = cleanedKGs;
    }

    public void setRawDocuments(List<Item> rawDocuments) {
        this.rawDocuments = rawDocuments;
    }

    public List<Item> getRawDocuments() {
        return rawDocuments;
    }

    public static void main(String[] args) {

    }

}
