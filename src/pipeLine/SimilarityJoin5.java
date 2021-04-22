package pipeLine;

import Jama.Matrix;
import org.apache.commons.lang3.tuple.Pair;
import preprocessing.ConstructGraph;
import publicDataStructure.*;

import java.io.IOException;
import java.util.*;

import Jama.Matrix;

import java.io.*;
import java.util.*;

public class SimilarityJoin5 {

    static int numNodes;
    static double C = 0.6;//衰减因子
    static int QUERY_NUM = 200, NUM = 4604;//5242;7115,4604
    static int N = 3;
    static int K = 50;
    static int nodes;
    KG mergeKg;
    private static String filenameTemp;

    //生成节点的入度
    public static Map InDegreeMap(KG kg) {

        List<Triple> triples = kg.getTriples();


        Map<Integer, Set<Integer>> InLinksMap = new HashMap<>();

        Set<Integer> nodelinks = new HashSet<>();
        Set<Integer> nodes = new HashSet<>();

        //kg中对应的实体与边的集合
        int triplesNum = triples.size();
        Integer headEntityID = 0;
        Integer tailEntityID = 0;

        try {
            for (int i = 0; i < triplesNum; i++) {

                headEntityID = Integer.valueOf(triples.get(i).getHead().getEntityId()); //获取第i个元组的头实
                tailEntityID = Integer.valueOf(triples.get(i).getTail().getEntityId());
                nodes.add(headEntityID);
                nodes.add(tailEntityID);

//tail-head indegree
                if (InLinksMap.containsKey(tailEntityID)) {
                    nodelinks = InLinksMap.get(tailEntityID);
                    nodelinks.add(headEntityID);
                    InLinksMap.put(tailEntityID, nodelinks);

                } else {
                    nodelinks = new HashSet<>();
                    nodelinks.add(headEntityID);
                    InLinksMap.put(tailEntityID, nodelinks);
                }
            }
            numNodes = nodes.size();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return InLinksMap;
    }

    public static Map OutDegreeMap(KG kg) {

        List<Triple> triples = kg.getTriples();
        Map<Integer, Set<Integer>> InLinksMap = new HashMap<Integer, Set<Integer>>();

        Set<Integer> nodelinks = new HashSet<>();
        Set<Integer> nodes = new HashSet<>();

        //kg中对应的实体与边的集合
        int triplesNum = triples.size();
        Integer headEntityID = 0;
        Integer tailEntityID = 0;

        try {
            for (int i = 0; i < triplesNum; i++) {

                headEntityID = Integer.valueOf(triples.get(i).getHead().getEntityId()); //获取第i个元组的头实
                tailEntityID = Integer.valueOf(triples.get(i).getTail().getEntityId());
                nodes.add(headEntityID);
                nodes.add(tailEntityID);

//tail-head indegree
                if (InLinksMap.containsKey(headEntityID)) {
                    nodelinks = InLinksMap.get(headEntityID);
                    nodelinks.add(tailEntityID);
                    InLinksMap.put(headEntityID, nodelinks);

                } else {
                    nodelinks = new HashSet<>();
                    nodelinks.add(tailEntityID);
                    InLinksMap.put(headEntityID, nodelinks);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return InLinksMap;
    }




    public Map<Integer, HashMap<Integer, Double>> TopSimSM(Map<Integer, Set<Integer>> IndDegreeMap, Map<Integer, Set<Integer>> OutDegreeMap, KG kg) throws IOException {
        List<Node> Nodes = kg.getNodes();//node
//        System.out.println(Nodes.size());
        Set<Integer> queryNodeId = new HashSet<>();
//        存放长度和节点 就是0 为待查询 1为待查询的入度 2 待查询的入度的入度集合
        Map<Integer, Set<Integer>> mapSet = new HashMap<>();
        Map<Integer, HashMap> storeSM = new HashMap<>();
        Map<Integer, Long> IdTime = new HashMap<>();

        for(Node node:Nodes){
            int q=Integer.parseInt(node.getNodeId());
//            存放相似度分数
            Map<Integer, HashMap> SM = new HashMap();
//            计算l-1暂时存放sm分数
            Map<Integer, HashMap> tempSM = new HashMap();
            Set<Integer> setT1 = new HashSet();
            long queryStartTimeAve = System.currentTimeMillis();
            setT1.add(q);
//            存放待查询的节点 q
            mapSet.put(0, setT1);
            for (int l = 1; l <= N - 1; l++) {
                if (mapSet.containsKey(l - 1)) {
//                    获取待查询的集合 0 为q
                    Set<Integer> set = mapSet.get(l - 1);
                    Set<Integer> setIn = new HashSet<>();
                    for (int i : set) {
                        if (IndDegreeMap.containsKey(i)) {
//                            get入度集合中查询的q的入度集合 再循环。。。
                            Set<Integer> s = IndDegreeMap.get(i);
                            for (int inId : s) {
//                                第一层存放q的入度节点
                                setIn.add(inId);
                            }
                        }
                    }
//                    存放长度和入度节点
                    mapSet.put(l, setIn);
                }
            }

            double score, addScore, tempScore;
            int It, Ij;
            Map<Integer, Double> hm = new HashMap<>();
            Set<Integer> inDegree1 = new HashSet<>();
            Set<Integer> outDegree1 = new HashSet<>();


            for (int l = N - 1; l >= 0; l--) {
                Set<Integer> SetT = mapSet.get(l);
                if (l == N - 1) {
                    for (int t : SetT) {

                        HashMap innerMap = SM.getOrDefault(t, new HashMap<Integer, Double>());
//						如果文件中入度节点包含节点t
                        if (IndDegreeMap.containsKey(t)) {
//							获取节点t的所有出度和权重
                            inDegree1 = IndDegreeMap.get(t);
                            for (int i : inDegree1) {
                                if (OutDegreeMap.containsKey(i)) {
                                    outDegree1 = OutDegreeMap.get(i);
                                    for (int j : outDegree1) {
                                        if (t != j) {
                                            if (IndDegreeMap.containsKey(j)) {

                                                    It = IndDegreeMap.get(t).size();
                                                    Ij = IndDegreeMap.get(j).size();
                                                    score = C / (double) (It * Ij);
//												如果sm已经存在 t的key 加上之前的值
                                                    if (SM.containsKey(t) && SM.get(t).containsKey(j)) {
                                                        tempScore = (double) SM.get(t).get(j);
                                                        addScore = tempScore + score;
                                                        innerMap.put(j, addScore);
                                                        SM.put(t, innerMap);
                                                    } else {
                                                        innerMap.put(j, score);
                                                        SM.put(t, innerMap);
                                                    }

                                            }
                                        }
                                    }
                                }
                            }
                        }
//						如果不包含t第一次添加 为1.0
                        innerMap.put(t, 1.0);
                        SM.put(t, innerMap);
                    }
                    tempSM = SM;
                } else {
//					l=n-1已经结束 现在判断l不等于n-1的情况
                    Map<Integer, HashMap> resultSM = new HashMap();
                    for (int t : SetT) {
                        HashMap innerMap = resultSM.getOrDefault(t, new HashMap<Integer, Double>());
                        if (IndDegreeMap.containsKey(t)) {
                            inDegree1 = IndDegreeMap.get(t);
                            for (int i : inDegree1) {
                                if (tempSM.containsKey(i)) {
                                    hm = tempSM.get(i);
                                    Iterator<Map.Entry<Integer, Double>> it_2 = hm.entrySet().iterator();
                                    while (it_2.hasNext()) {
                                        Map.Entry entry1 = it_2.next();
                                        int key = (int) entry1.getKey();
                                        double value = (double) entry1.getValue();
                                        if (OutDegreeMap.containsKey(key)) {
                                            outDegree1 = OutDegreeMap.get(key);
                                            for (int j : outDegree1) {
                                                if (IndDegreeMap.containsKey(t) && IndDegreeMap.containsKey(j)) {

                                                        if (t != j) {
                                                            It = IndDegreeMap.get(t).size();
                                                            Ij = IndDegreeMap.get(j).size();
                                                            score = (C * value) / (double) (It * Ij);
                                                            if (resultSM.containsKey(t) && resultSM.get(t).containsKey(j)) {
                                                                tempScore = (double) resultSM.get(t).get(j);
                                                                addScore = score + tempScore;
                                                                innerMap.put(j, addScore);
                                                                resultSM.put(t, innerMap);
                                                            } else {
                                                                innerMap.put(j, score);
                                                                resultSM.put(t, innerMap);
                                                            }
                                                        }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        innerMap.put(t, 1.0);
                        resultSM.put(t, innerMap);
                    }
                    tempSM = resultSM;
                }
            }
            storeSM.putAll(tempSM);

        }



        Map<Integer, HashMap> sortMatrix = new HashMap<Integer, HashMap>();
        List<HashMap.Entry<Integer, Double>> list1 = new ArrayList<HashMap.Entry<Integer, Double>>();
        Map<Integer, Double> hm = new HashMap<Integer, Double>();
        Iterator<Map.Entry<Integer, HashMap>> it = storeSM.entrySet().iterator();
//		得到矩阵的迭代器
        while (it.hasNext()) {
            Map.Entry entry1 = it.next();
            int k1 = (int) entry1.getKey();
            hm = (HashMap<Integer, Double>) entry1.getValue();
            //遍历 hm.
            for (Map.Entry<Integer, Double> entry : hm.entrySet()) {
                list1.add(entry); //将map中的元素放入list中
            }
            list1.sort(new Comparator<HashMap.Entry<Integer, Double>>() {
                           public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {
                               double result = o2.getValue() - o1.getValue();
                               if (result > 0)
                                   return 1;
                               else if (result == 0)
                                   return 0;
                               else
                                   return -1;
                           }
                       }
            );
            HashMap innerIdWeight = sortMatrix.getOrDefault(k1, new HashMap<Integer, Double>());
            innerIdWeight = new LinkedHashMap<Integer, Double>();
            //output k similar nodes
            if (list1.size() > K) {
                for (Map.Entry<Integer, Double> temp : list1.subList(0, K)) {
                    innerIdWeight.put(temp.getKey(), temp.getValue());
                    sortMatrix.put(k1, innerIdWeight);
                }
            } else {
                for (Map.Entry<Integer, Double> temp : list1) {
                    innerIdWeight.put(temp.getKey(), temp.getValue());
                    sortMatrix.put(k1, innerIdWeight);
                }
            }
            list1.clear();
        }


//        creat tempkg
        KG tempKg = kg;
//        wirte simscore to txt file

        List<Node> nodeList = new ArrayList<>();
        List<Triple> tripleList = new ArrayList<>();
        Set<Node> ndList = new HashSet<>();
//存放相似节点的map
        Map<Integer, HashMap<Integer, Double>> nodeSimMap = new HashMap<>();
        Map<Integer, Double> simMap = new HashMap<>();
//迭代
        Iterator<Map.Entry<Integer, HashMap>> it_3 = sortMatrix.entrySet().iterator();
        HashMap<Integer, Double> temp1 = new HashMap();
//    temp1是相连的节点 并且与相连节点的相似度得分
        while (it_3.hasNext()) {
//		    遍历所有查询的节点
            Map.Entry entry2 = it_3.next();
            int k = (int) entry2.getKey();
//            System.out.print(k + "\t");
//        遍历所有相连节点的节点id和分数
            temp1 = (HashMap<Integer, Double>) entry2.getValue();
            Iterator<Map.Entry<Integer, Double>> it_4 = temp1.entrySet().iterator();
            tripleList = tempKg.getTriples();

            while (it_4.hasNext()) {
//            遍历相连的节点
                Map.Entry entry3 = it_4.next();
                int k1 = (int) entry3.getKey();
                double v = (double) entry3.getValue();
                if (v != 0) {
//                    存放相似节点和分数
                    if (nodeSimMap.containsKey(k)) {
                        simMap = nodeSimMap.get(k);
                        simMap.put(k1, v);
                        nodeSimMap.put(k, (HashMap<Integer, Double>) simMap);
                    } else {
                        simMap = new HashMap<>();
                        simMap.put(k1, v);
                        nodeSimMap.put(k, (HashMap<Integer, Double>) simMap);
                    }
//                    System.out.print(k1 + "\t" + v + "\t\t");
//                判断相似度得分是否大于0.9 如果大于 就融合
//               获得k节点对应的实体

                }
//                System.out.println("\n");
            }
        }
        return nodeSimMap;
    }
}





