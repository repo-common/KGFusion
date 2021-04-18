package pipeLine;

import java.io.*;
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
        List<String> simNodeIds=MergeTool.ComputeSim(RWR.RWRmatrix(MergeTool.ComputeTF(kgs,items)));

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
        //public static double[][] ComputeTF(List<KG> kgs, List<Item> items) {
        public static HashMap<Integer,HashMap<Integer,Double>> ComputeTF(List<KG> kgs, List<Item> items) {
            List<Node> tempentitys = new ArrayList<Node>();
            List<Node> entitys = new ArrayList<Node>();
            for (KG kg : kgs) {
                tempentitys = kg.getNodes();
                for (int i = 0; i < tempentitys.size(); i++) {
                    entitys.add(tempentitys.get(i));
                }
            }
            for(Node node:entitys){
                entityidname.put(node.getNodeId(),node.getNodeName());
            }
            double[][] tfidfMatrix = new double[items.size()][entitys.size()];//矩阵存放TFIDF
            HashMap<Integer,HashMap<Integer,Double>> tfidf1 = new HashMap<Integer,HashMap<Integer,Double>>();//文档ID，词ID，值
            HashMap<Integer, Double> idf = new HashMap<Integer, Double>();
            for (Node node : entitys) {//IDF
                int D = items.size(); //总条目数目
                int Dt = 0;// Dt为出现该实体的条目数目
/*            for (String word1 : cutWords) {
                if (word1.contains(node.getNodeName())) {
                    Dt++;
                }
            }*/
                for(Item item : items){
                    if(item.getItemText().contains(node.getNodeName())){
                        Dt++;
                    }
                }
                double idfvalue = (double) Math.log(Float.valueOf(D) / (1 + Dt));
                idf.put(Integer.parseInt(node.getNodeId()), idfvalue);
            }
            for(Node node :entitys){//TF * IDF
                String nodename=node.getNodeName();
                for(Item item :items){
                    int count=0;
                    String itemtext=item.getItemText();
                    JiebaSegmenter segmenter = new JiebaSegmenter();
                    List<String> cutwords=segmenter.sentenceProcess(itemtext);
                    for(int i=0;i<cutwords.size();i++){
                        if(cutwords.get(i).contains(nodename)){
                            count++;
                        }
                    }
                    double TF=(double) count/cutwords.size();
                    double IDF=idf.get(Integer.parseInt(node.getNodeId()));
                    if(TF*IDF >0.0){
                        HashMap<Integer, Double> value = tfidf1.getOrDefault(Integer.parseInt(item.getItemId()),new HashMap<Integer, Double>());
                        value.put((Integer.parseInt(node.getNodeId())), TF * IDF );
                        tfidf1.put(Integer.parseInt(item.getItemId()),value);
                    }

                    int i = Integer.parseInt(item.getItemId())-1;
                    int j = Integer.parseInt(node.getNodeId())-1;
                    tfidfMatrix[i][j] = TF * IDF;
                }
            }

            return tfidf1;
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

        public static List<String> ComputeSim(double[][] tfidfmatrix) throws IOException {
            List<String> simNodeIds = new ArrayList<>();//List存放相似度大于0.9的实体ID对
            for (int j = 0; j < tfidfmatrix[0].length  ; j++) {
                ArrayList va = new ArrayList();
                for (int i = 0; i < tfidfmatrix.length ; i++) { // va=martix[1][]
                    va.add(tfidfmatrix[i][j]);
                }
                for (int b = 0; b < tfidfmatrix[0].length ; b++) {
                    ArrayList vb = new ArrayList();
                    for (int a = 0; a < tfidfmatrix.length ; a++) { //vb=martix[1][] [2][] [3][]...[331][]
                        vb.add(tfidfmatrix[a][b]);
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
                    int aid = j + 1;
                    int bid = b + 1;
                    if(j<b && simVal>=0.9){
                        simNodeIds.add(aid + "," + bid);
                    }

                }
            }

            return simNodeIds;
        }//方法：计算余弦相似度，返回相似结点对
    }

    static class RWR{
        //	public static HashMap<Integer, HashMap<Integer, Double>> tfidf_value1 = new HashMap<Integer, HashMap<Integer, Double>>();// 文档号、词、tfidf值；
        static HashMap<Integer, HashMap<Integer, Double>> RWRTT_value = new HashMap<Integer, HashMap<Integer, Double>>();
        public static HashMap<Integer, HashMap<Integer, Double>> InvertedIndex = new HashMap<Integer, HashMap<Integer, Double>>();// 词、tfidf值
        public static HashMap<Integer, Double> invertedindex = new HashMap<Integer, Double>();// 词、IDF值
        public static HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();// 词、tfidf值
        //	static HashMap<Integer, HashMap<Integer, Double>> Trans_TFIDF = new HashMap<Integer, HashMap<Integer, Double>>();
        static HashMap<Integer, HashMap<Integer, Double>> probability = new HashMap<Integer, HashMap<Integer, Double>>();
        static HashMap<Integer, HashMap<Integer, Double>> probability1 = new HashMap<Integer, HashMap<Integer, Double>>();
        static HashMap<Integer, HashMap<Integer, Double>> probability2 = new HashMap<Integer, HashMap<Integer, Double>>();
        static HashMap<Integer, HashMap<Integer, Double>> probability3 = new HashMap<Integer, HashMap<Integer, Double>>();
        static HashMap<Integer, HashMap<Integer, Double>> probability4 = new HashMap<Integer, HashMap<Integer, Double>>();
        static HashMap<Integer, HashMap<Integer, Double>> probability5 = new HashMap<Integer, HashMap<Integer, Double>>();
        public static HashMap<Integer, HashMap<Integer, Double>> Normalize_TT = new HashMap<Integer, HashMap<Integer, Double>>();// 词、tfidf值
        static double c = 0.8;

        // public static int Number;
        public static double[][] RWRmatrix(HashMap<Integer,HashMap<Integer,Double>> m1) throws IOException {

            double[][] rwrmatrix=new double[135][331];

            HashMap<Integer, HashMap<Integer, Double>> Trans_TFIDF = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, HashMap<Integer, Double>> tfidf_value1 = new HashMap<Integer, HashMap<Integer, Double>>();// 文档号、词、tfidf值；
            HashMap<Integer, HashMap<Integer, Double>> probability8 = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, HashMap<Integer, Double>> probability9 = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, HashMap<Integer, Double>> probability10 = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, HashMap<Integer, Double>> probability11 = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, HashMap<Integer, Double>> probability12 = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, HashMap<Integer, Double>> probability13 = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, HashMap<Integer, Double>> probability14 = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, HashMap<Integer, Double>> probability15 = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, HashMap<Integer, Double>> probability16 = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, HashMap<Integer, Double>> probability17 = new HashMap<Integer, HashMap<Integer, Double>>();

            long startTime = System.currentTimeMillis();
            HashMap<Integer,HashMap<Integer,Double>> M = m1;
//		System.out.println("打印在txt中数据转为hashmap==="+M);//测试

            tfidf_value1 = M;
//		System.out.println("将读取的hashmap赋值给tfidf_value1==="+tfidf_value1);//测试

            Trans_TFIDF = trans_matrix(M);// 7*3
//		System.out.println("打印测试=转置矩阵="+Trans_TFIDF);
            // System.out.println("=============");
            // TT
            Normalize_TT = matrix_multiplication(normalize(Trans_TFIDF), trans_matrix(normalize(tfidf_value1))); // yu
/*
		System.out.println("================测试矩阵是否准确 ================");
		System.out.println("打印测试=转置矩阵=归一化="+normalize(Trans_TFIDF));//打印测试
		System.out.println("归一化=  = =原来矩阵=="+(normalize(tfidf_value1)));
		System.out.println("归一化=再转置  = =原来矩阵=="+trans_matrix(normalize(tfidf_value1)));
		System.out.println("相乘后的结果"+Normalize_TT);
*/
            // TTRWR
            probability1 = multiplication(Normalize_TT);// c(1-c)p=R1
            result = matrix_multiplication(tfidf_value1, probability1);//游走第一步
       //     print_matrix(result,"E:\\ASDN-Introcuction\\data1001\\F2_online_" + "c0.8_1" + ".txt");

            probability2 = multiplication1(Normalize_TT);// (1-c)p
            probability3 = matrix_multiplication1(probability2, probability1);// (1-c)p*R1
            probability4 = matrix_addition(probability1, probability3);// R2=c(1-c)p+(1-c)p*R1 游走第二步

            result = matrix_multiplication(tfidf_value1, probability4);//
       //     print_matrix(result,"E:\\ASDN-Introcuction\\data1001\\F2_online_" + "c0.8_2" + ".txt");

            probability5 = matrix_multiplication1(probability2, probability4);// (1-c)p*R2
            // RWRTT_value
            RWRTT_value = matrix_addition(probability1, probability5);// R3=c(1-c)P+(1-c)p*R2 游走的第三步
            // DT
            result = matrix_multiplication(tfidf_value1, RWRTT_value); //最后的值乘以TFIDF值
            long endTime = System.currentTimeMillis(); // 获取结束时间
            System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
     //       print_matrix(result,"E:\\ASDN-Introcuction\\data1001\\F2_online_" + "c0.8_3" + ".txt");

            probability8 = matrix_multiplication1(probability2, RWRTT_value);
            probability9 = matrix_addition(probability1, probability8);
            result = matrix_multiplication(tfidf_value1, probability9);
    //        print_matrix(result,"E:\\ASDN-Introcuction\\data1001\\F2_online_" + "c0.8_4" + ".txt");

            for(int i=1;i<=rwrmatrix.length;i++){
                if(result.containsKey(i)){
                    HashMap<Integer,Double> RWRtemp=new HashMap<>();
                    RWRtemp=result.get(i);
                    for(int key:RWRtemp.keySet()){
                        rwrmatrix[i-1][key-1]=RWRtemp.get(key);
                    }
                }
            }

            return rwrmatrix;

        }

        public static HashMap<Integer, HashMap<Integer, Double>> multiplication(// c(1-c)p
                                                                                HashMap<Integer, HashMap<Integer, Double>> TFIDF1) throws IOException {
            HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, Double> m1 = new HashMap<Integer, Double>();
            Iterator<Integer> it = TFIDF1.keySet().iterator();//
//		double c = 0.8;
            while (it.hasNext()) {
                int k1 = it.next();
                m1 = TFIDF1.get(k1);// (k1,m1)
                HashMap<Integer, Double> m3 = new HashMap<Integer, Double>();
                Iterator<Integer> it1 = m1.keySet().iterator();//
                while (it1.hasNext()) {
                    int k2 = it1.next();
                    double d1 = m1.get(k2);
                    double d2 = d1 * (1 - c);// (1-c)p*(RL)
                    //double d2 = d1 * c* (1 - c);// (1-c)p*(RL)
                    m3.put(k2, d2);
                }
                result.put(k1, m3);// <k1,<k2, d2>>
            }
            return result;
        }

        public static HashMap<Integer, HashMap<Integer, Double>> multiplication1(// (1-c)p
                                                                                 HashMap<Integer, HashMap<Integer, Double>> TFIDF1) throws IOException {
            HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, Double> m1 = new HashMap<Integer, Double>();
            Iterator<Integer> it = TFIDF1.keySet().iterator();//
//		double c = 0.8;
            while (it.hasNext()) {
                int k1 = it.next();
                m1 = TFIDF1.get(k1);// (k1,m1)
                HashMap<Integer, Double> m3 = new HashMap<Integer, Double>();
                Iterator<Integer> it1 = m1.keySet().iterator();//
                while (it1.hasNext()) {
                    int k2 = it1.next();
                    double d1 = m1.get(k2);
                    double d2 = d1 * (1 - c);// (1-c)p
                    m3.put(k2, d2);
                }
                result.put(k1, m3);// <k1,<k2, d2>>
            }

            return result;
        }

        public static HashMap<Integer, HashMap<Integer, Double>> matrix_multiplication(// 矩阵相乘
                                                                                       HashMap<Integer, HashMap<Integer, Double>> TFIDF1, HashMap<Integer, HashMap<Integer, Double>> TFIDF2) {
            // 两个矩阵相乘；
            HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, Double> m1 = new HashMap<Integer, Double>();
            HashMap<Integer, Double> m2 = new HashMap<Integer, Double>();
            double d1 = 0.0;
            for (Map.Entry<Integer, HashMap<Integer, Double>> entry : TFIDF1.entrySet()) {// ①//行i
                int k1 = entry.getKey();
                m1 = entry.getValue();// (k1,m1)通过k1行找列m1

                HashMap<Integer, Double> m3 = new HashMap<Integer, Double>();

                for (Map.Entry<Integer, HashMap<Integer, Double>> entry2 : TFIDF2.entrySet()) {// ②//列j
                    int k2 = entry2.getKey();
                    m2 = TFIDF2.get(k2);// (k2,m2)找k2行m2列
                    double sum = 0;

                    for (Map.Entry<Integer, Double> it2_1 : m2.entrySet()) {// 3 //值
                        int k3 = it2_1.getKey();
                        try {
                            d1 = m1.get(k3);// （k1,m1,d1)d1为值
                            // System.out.println("d1="+d1);
                        } catch (Exception e) {
                            d1 = 0.0;
                        }
                        double d2 = m2.get(k3);// （k2,m2,d2)d2为值
                        // System.out.println("d2="+d2);
                        double d3 = d1 * d2; // A[i,k] * B[k,j]
                        // System.out.println("d3="+d3);
                        sum += d3; // C[i,j] += A[i,k] * B[k,j];
                        // 加算完了第一个值
                    }
                    // System.out.println("sum="+sum);
                    if(sum >0.0001) {
                        m3.put(k2, sum);}
                    // System.out.println(m3);
                }
                // System.out.println("-------------------");
                result.put(k1, m3);// 相乘后的方阵，<k1,<k2, sum1>>
            }
            return result;

            // return result;
            // System.out.println(result);
        }

        public static HashMap<Integer, HashMap<Integer, Double>> matrix_multiplication1(// 矩阵相乘
                                                                                        HashMap<Integer, HashMap<Integer, Double>> TFIDF1, HashMap<Integer, HashMap<Integer, Double>> TFIDF2) {
            // 两个矩阵相乘；
            HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
            HashMap<Integer, Double> m1 = new HashMap<Integer, Double>();
            HashMap<Integer, Double> m2 = new HashMap<Integer, Double>();
            double d1 = 0.0;
            for (Map.Entry<Integer, HashMap<Integer, Double>> entry : TFIDF1.entrySet()) {// ①//行i
                int k1 = entry.getKey();
                m1 = entry.getValue();// (k1,m1)通过k1行找列m1
                HashMap<Integer, Double> m3 = new HashMap<Integer, Double>();
                for (Map.Entry<Integer, HashMap<Integer, Double>> entry2 : TFIDF2.entrySet()) {// ②//列j
                    int k2 = entry2.getKey();
                    m2 = TFIDF2.get(k2);// (k2,m2)找k2行m2列
                    double sum = 0;
                    for (Map.Entry<Integer, Double> it2_1 : m2.entrySet()) {// 3 //值
                        int k3 = it2_1.getKey();
                        try {
                            d1 = m1.get(k3);// （k1,m1,d1)d1为值
                            // System.out.println("d1="+d1);
                        } catch (Exception e) {
                            d1 = 0.0;
                        }
                        double d2 = m2.get(k3);// （k2,m2,d2)d2为值
                        // System.out.println("d2="+d2);
                        double d3 = d1 * d2 * (1 - c); // A[i,k] * B[k,j]
                        // System.out.println("d3="+d3);
                        sum += d3; // C[i,j] += A[i,k] * B[k,j];
                        // 加算完了第一个值
                    }
                    // System.out.println("sum="+sum);
                    if(sum > 0.0001) {
                        m3.put(k2, sum);
                    }
                    // System.out.println(m3);
                }
                // System.out.println("-------------------");
                result.put(k1, m3);// 相乘后的方阵，<k1,<k2, sum1>>
            }
            return result;

            // return result;
            // System.out.println(result);
        }

        public static HashMap<Integer, HashMap<Integer, Double>> normalize(
                HashMap<Integer, HashMap<Integer, Double>> TFIDF) {
            // 矩阵行归一化

            HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
            Iterator<Integer> it = TFIDF.keySet().iterator();
            HashMap<Integer, Double> m7 = new HashMap<Integer, Double>();
            while (it.hasNext()) {
                int k1 = it.next();
                m7 = TFIDF.get(k1);
                Iterator<Integer> it_1 = m7.keySet().iterator();
                double sum = 0;
                while (it_1.hasNext()) {
                    int k2 = it_1.next();
                    double d1 = m7.get(k2);
                    sum += d1;
                }
                Iterator<Integer> it_2 = m7.keySet().iterator();
                HashMap<Integer, Double> m8 = new HashMap<Integer, Double>();
                while (it_2.hasNext()) {
                    int k3 = it_2.next();
                    double d2 = m7.get(k3);
                    double d3 = d2 / sum;
                    m8.put(k3, d3);
                }
                result.put(k1, m8);

            }
            // System.out.println(result);
            return result;
        }

        public static HashMap<Integer, HashMap<Integer, Double>> trans_matrix(
                HashMap<Integer, HashMap<Integer, Double>> hm) { // 转置TFIDF矩阵得到Trans_TFIDF矩阵； // 转置TFIDF矩阵得到Trans_TFIDF矩阵；
            HashMap<Integer, HashMap<Integer, Double>> Trans_TFIDF = new HashMap<Integer, HashMap<Integer, Double>>();
            ArrayList<String> al = new ArrayList<String>();
            Iterator<Integer> it = hm.keySet().iterator();
            HashMap<Integer, Double> m3 = new HashMap<Integer, Double>();
            HashMap<Integer, Double> hm1 = new HashMap<Integer, Double>();

            while (it.hasNext()) {
                int k1 = it.next();
                m3 = hm.get(k1);
                Iterator<Integer> it_1 = m3.keySet().iterator();
                while (it_1.hasNext()) {

                    int k2 = it_1.next();
                    double v = m3.get(k2);
                    al.add(k2 + " " + k1 + " " + v);
//				it_2.remove();//2020.1.1
                }
            }
            Collections.sort(al);
            Iterator<String> it_2 = al.iterator();
            String s1 = it_2.next().toString();//
            String s2[] = s1.split("\\s+");
            String s3 = s2[0];

            hm1.put(Integer.parseInt(s2[1]), Double.parseDouble(s2[2]));
            while (it_2.hasNext()) {
                s1 = it_2.next().toString();
                s2 = s1.split("\\s+");
                String s4 = s2[0];
                if (s4.equals(s3)) {
                    hm1.put(Integer.parseInt(s2[1]), Double.parseDouble(s2[2]));
                    it_2.remove();//2020.1.1
                } else {
                    Trans_TFIDF.put(Integer.parseInt(s3), hm1);
                    HashMap<Integer, Double> hm2 = new HashMap<Integer, Double>();
                    hm1 = hm2;
                    s3 = s4;
                    hm1.put(Integer.parseInt(s2[1]), Double.parseDouble(s2[2]));

                }
                // System.out.println(Integer.parseInt(s3)+" "+Integer.parseInt(s2[1])+" "+
                // Double.parseDouble(s2[2]));
            }
            Trans_TFIDF.put(Integer.parseInt(s3), hm1);
//		System.out.println("==方法里面类中的转置==="+Trans_TFIDF);
            return Trans_TFIDF;

        }

        public static HashMap<Integer, HashMap<Integer, Double>> matrix_addition(
                HashMap<Integer, HashMap<Integer, Double>> probability,
                HashMap<Integer, HashMap<Integer, Double>> probability_result) {
            // 两个矩阵相加；
            HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
//		double c = 0.8;
            HashMap<Integer, Double> m4 = new HashMap<Integer, Double>();
            HashMap<Integer, Double> m5 = new HashMap<Integer, Double>();

            Iterator<Integer> it = probability_result.keySet().iterator();
            while (it.hasNext()) {
                int k1 = it.next();
                HashMap<Integer, Double> m6 = new HashMap<Integer, Double>();
                m4 = probability.get(k1);
                m5 = probability_result.get(k1);
                Iterator<Integer> it_1 = m4.keySet().iterator();
                while (it_1.hasNext()) {
                    int k2 = it_1.next();
                    double d1 = m4.get(k2);
                    if(m5.get(k2) !=null) {
                        double d2 = m5.get(k2);

                        double d3 = d1 + c * d2;
                        m6.put(k2, d3);}
                }
                result.put(k1, m6);
            }
            // System.out.println(result);
            return result;
        }

        public static void print_matrix(HashMap<Integer, HashMap<Integer, Double>> TFIDF, String path) throws IOException {
            /*
             * 还需要从别的java文件的类中获得原有的文章号和单词，然后对value值进行排序；？？？？？（未实现）
             */
            File f1 = new File(path);
            FileWriter fw1 = new FileWriter(f1);
            BufferedWriter bf1 = new BufferedWriter(fw1);
            Iterator<Integer> it_1 = TFIDF.keySet().iterator();
            HashMap<Integer, Double> hm = new HashMap<Integer, Double>();

            while (it_1.hasNext()) {
                Integer s3 = it_1.next();
                hm = TFIDF.get(s3);
                Iterator<Integer> it_2 = hm.keySet().iterator();
                while (it_2.hasNext()) {
                    Integer s4 = it_2.next();
                    Double s5 = hm.get(s4);
                    if (s5 > 0) {
                        bf1.write(s3 + "\t" + s4 + "\t" + s5 + "\r\n");
                    }
                    // System.out.println(s3+" "+s4+" "+s5);
                }
            }
            bf1.flush();
            bf1.close();
            fw1.close();
        }

        public static HashMap<Integer, HashMap<Integer, Double>> txt2HashMap(String filePath) {

//		HashMap<Integer,Double> m1 = new HashMap<Integer,Double>();

            HashMap<Integer, HashMap<Integer, Double>> hm = new HashMap<Integer, HashMap<Integer, Double>>();
            try {
                String encoding = "GBK";
                File file = new File(filePath);

                if (file.isFile() && file.exists()) {
                    InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] split = line.split("\\s+");
                        if (split.length != 3) {
                            continue;
                        }
                        int s3 = Integer.parseInt(split[0]);
                        int s1 = Integer.parseInt(split[1]);
                        double s2 = Double.parseDouble(split[2]);
                        HashMap<Integer, Double> m1 = hm.getOrDefault(s3, new HashMap<Integer, Double>());
                        m1.put(s1, s2);
                        hm.put(s3, m1);
                    }
                    read.close();
                    bufferedReader.close();
                } else {
                    System.out.println("找不到指定的文件");
                }
            } catch (Exception e) {
                System.out.println("读取文件内容出错");
                e.printStackTrace();
            }
            return hm;
        }


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
