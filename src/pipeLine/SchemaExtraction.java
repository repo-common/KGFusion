package pipeLine;

import Jama.Matrix;
import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.css.CSSImportRule;
import preprocessing.ConstructGraph;
import publicDataStructure.*;
import utils.MathUtil;
import utils.RWR_4;

import javax.lang.model.type.MirroredTypeException;
import javax.swing.text.StyledEditorKit;
import java.io.IOException;
import java.net.Inet4Address;
import java.security.spec.RSAOtherPrimeInfo;
import java.text.CollationElementIterator;
import java.util.*;
import java.util.stream.Collectors;

import static utils.MathUtil.cosineSimilarity;
import static utils.MathUtil.sum;

public class SchemaExtraction {

    private KG fusionKG;
    private HashMap<String, String> schema = new HashMap<>() {
        {
            put("1", "模块");
            put("2", "用户");
            put("3", "需求");
            put("4", "功能");
            put("5", "目标");
            put("6", "信息");
            put("7", "约束");
            put("8", "业务人员");
            put("9", "流程");
            put("10", "服务");
            put("11", "系统");
            put("12", "任务");
            put("13", "代理");
            put("14", "软件");
            put("15", "数据库");
            put("16", "部门");
        }
    };


    /**
     * 获取tf-idf矩阵
     *
     * @param entities
     * @param typeEntityDic
     * @return double[][]
     */
    public double[][] entity_type_tfIdf(List<Entity> entities, List<String> types, HashMap<String, Set<Entity>> typeEntityDic) {
        int numEntity = entities.size();
        int numType = typeEntityDic.keySet().size();
        double[][] entity_type_matrix = new double[numEntity][numType];
        for (int i = 0; i < numEntity; i++) {
            for (int j = 0; j < numType; j++) {
                entity_type_matrix[i][j] = computeValue(entities.get(i), types.get(j), typeEntityDic);
            }
        }
        return entity_type_matrix;
    }

    public double computeValue(Entity entity, String type, HashMap<String, Set<Entity>> typeEntityDic) {
        double frequency = 0.0;
        for (Entity entity1 : typeEntityDic.get(type)) {
            if (entity.equals(entity1)) frequency += 1;
        }
        double tf = frequency / typeEntityDic.get(type).size();
        double inv_frequency = 0.0;
        for (Set<Entity> set1 : typeEntityDic.values()) {
            if (set1.contains(entity))
                inv_frequency += 1;
        }
        double idf = Math.log(typeEntityDic.keySet().size() / (inv_frequency + 1));
        return tf * idf;
    }

    // 将随机游走的结果转换为矩阵
    public double[][] convertRWR(HashMap<Integer, HashMap<Integer, Double>> randomWalkRes) {
        int numEntity = randomWalkRes.keySet().size();
        HashSet<Integer> values = new HashSet<>();
        for (Integer key : randomWalkRes.keySet()) {
            HashMap<Integer, Double> innerMap = randomWalkRes.get(key);
            values.addAll(innerMap.keySet());
        }
        int numType = values.size();
        double[][] res = new double[numEntity][numType];
        for (int i = 0; i < numEntity; i++) {
            for (int j = 0; j < numType; j++) {
                res[i][j] = 0.0;
            }
        }
        for (Integer key1 : randomWalkRes.keySet()) {
            HashMap<Integer, Double> innerMap = randomWalkRes.get(key1);
            for (Integer key2 : innerMap.keySet()) {
                res[key1][key2] = innerMap.get(key2);
            }
        }

        return res;
    }

    /**
     * 获取typeID-【entity1, entity2, entity3...】, 相当于文档
     *
     * @return HashMap<String, Set < Entity>>
     */
    public HashMap<String, Set<Entity>> typeEntityDic() {
        KG targetKg = this.fusionKG;
        HashMap<String, Set<Entity>> dic = new HashMap<>();
        for (Node node : targetKg.getNodes()) {
            if (!dic.containsKey(node.getTypeId())) {
                HashSet<Entity> entities = new HashSet<>();
                entities.add(copyFromNode(node));
                dic.put(node.getTypeId(), entities);
            } else {
                dic.get(node.getTypeId()).add(copyFromNode(node));
            }
        }
        return dic;
    }

    /**
     * 在fusionKG 中，会出现不合理的三元组
     *
     * @return HashMap<String, Pair < Set < Entity>, Set<Entity>>>
     */
    public HashMap<String, Pair<Set<Entity>, Set<Entity>>> getNexus() {
        KG target = fusionKG;
        Set<String> relations = new HashSet<>();
        List<Triple> triples = target.getTriples();
        HashMap<String, Pair<Set<Entity>, Set<Entity>>> nexus = new HashMap<>();
        for (Triple triple : triples) {
            String relation = triple.getRela();
            relations.add(relation);
            if (!nexus.containsKey(relation)) {
                Set<Entity> leftSet = new HashSet<>();
                Set<Entity> rightSet = new HashSet<>();
                leftSet.add(triple.getHead());
                rightSet.add(triple.getTail());
                nexus.put(relation, Pair.of(leftSet, rightSet));
            } else {
                nexus.get(relation).getLeft().add(triple.getHead());
                nexus.get(relation).getRight().add(triple.getTail());
            }
        }

        return nexus;
    }

    // 计算实体条目tf_idf
    public double[][] entity_item_tfidf(List<Entity> entities, List<Item> items) {
        int numEntity = entities.size();
        int numItem = items.size();
        double[][] entity_item_matrix = new double[numEntity][numItem];
        // 只取实体中存在的条目ID对应条目
        for (int i = 0; i < numEntity; i++) {
            for (int j = 0; j < numItem; j++) {
                entity_item_matrix[i][j] = computeEIValue(entities.get(i), items.get(j), items);
            }
        }
        return entity_item_matrix;
    }

    // 计算tfidf[i][j] 实体条目
    private double computeEIValue(Entity entity, Item item, List<Item> filterItems) {
        // 条目内容

        String entityName = entity.getEntityName();
        JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
        List<List<String>> docs = new ArrayList<>();
        for (Item item1 : filterItems) {
            docs.add(jiebaSegmenter.sentenceProcess(item1.getItemText()));
        }
        double frequency = 0.0;
        List<String> processedSentence = jiebaSegmenter.sentenceProcess(item.getItemText());
        for (String itemContent : processedSentence) {
            if (itemContent.equals(entityName))
                frequency += 1;
        }
        if (entity.getItemId().equals(item.getItemId())) {
            System.out.println(entityName + " " + processedSentence.toString());
            if (frequency == 0)
                System.out.println("Exception occur");
        }
        double tf = frequency / processedSentence.size();
        double inv_frequency = 0.0;
        for (List<String> doc : docs) {
            if (doc.contains(entityName))
                inv_frequency += 1;
        }
        double idf = Math.log(filterItems.size() / (1 + inv_frequency));


        return tf * idf;
    }

    public double[][] geSimMatrix(double[][] tfIdfMatrix) {
        int dim = tfIdfMatrix.length;
        double[][] simMatrix = new double[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                simMatrix[i][j] = MathUtil.cosineSimilarity(tfIdfMatrix[i], tfIdfMatrix[j]);
            }
        }
        return simMatrix;
    }

    public String geType(Entity entity, List<Entity> entities, double[][] simMatrix, double threshold) {
//        if (entity.getTypeId() != null)
//            return entity.getTypeId();
        HashMap<String, List<Double>> candidates = new HashMap<>();
        double[] relatedRow = simMatrix[entities.indexOf(entity)];
        for (int i = 0; i < relatedRow.length; i++) {
            if (relatedRow[i] >= threshold && relatedRow[i] < 0.9999) {
                String type = entities.get(i).getTypeId();
                if (!candidates.containsKey(type)) {
                    List<Double> li = new ArrayList<>();
                    li.add(relatedRow[i]);
                    candidates.put(type, li);
                }

                candidates.get(type).add(relatedRow[i]);
            }
        }

        double maxAvg = 0.0;
        String bestType = "";
        for (String type1 : candidates.keySet()) {
            if (candidates.get(type1) != null) {
                double appendValue = MathUtil.sum(candidates.get(type1)) / candidates.get(type1).size();
                if (appendValue > maxAvg) {
                    maxAvg = appendValue;
                    bestType = type1;
                }
            }
        }

        return bestType;
    }

    public String geTypeOtherMethod(Entity entity, List<Entity> entities, HashMap<Integer, HashMap<Integer, Double>> simMap) {
        HashMap<String, List<Double>> candidates = new HashMap<>();
        HashMap<Integer, Double> related = simMap.get(Integer.parseInt(entity.getEntityId()));
        List<Pair<Integer, Double>> relatedList = new ArrayList<>();
        for (Integer key : related.keySet()) {
            relatedList.add(Pair.of(key, related.get(key)));
        }
        relatedList.sort(new Comparator<Pair<Integer, Double>>() {
            @Override
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                double val1 = o1.getRight();
                double val2 = o2.getRight();
                if (val1 == val2)
                    return 0;
                else {
                    return val1 > val2 ? -1 : 1; // 降序
                }
            }
        });

        relatedList.remove(0);

        // 打印信息
        List<String> l = new ArrayList<>();
        for (int i = 0; i < relatedList.size(); i++) {
            String name = "";
            for (Entity en: entities){
                if(en.getEntityId().equals(Integer.toString(relatedList.get(i).getLeft()))){
                    name = en.getEntityName();
                    l.add(name);
                }
            }
        }
        System.out.println(entity.getEntityName() + " " + l.toString());


        String bestType = "";
        for (int i = 0; i < relatedList.size(); i++) {
            String type = "";
            double val = relatedList.get(i).getRight();
            for (Entity entity1 : entities) {
                if (entity1.getEntityId().equals(Integer.toString(relatedList.get(i).getLeft())))
                    type = entity1.getTypeId();
            }
            if (!candidates.containsKey(type)) {
                List<Double> li = new ArrayList<>();
                li.add(val);
                candidates.put(type, li);
            }
            else{
                candidates.get(type).add(val);
            }

        }


        double maxAvg = 0.0;
        if (relatedList.size() != 0){
            for (String type1 : candidates.keySet()) {
                if (candidates.get(type1) != null) {
                    double appendValue = MathUtil.sum(candidates.get(type1)) / candidates.get(type1).size();
                    if (appendValue > maxAvg) {
                        maxAvg = appendValue;
                        bestType = type1;
                    }
                }
            }
        }


        return bestType;
    }

    public List<Item> filterItem(List<Entity> entities, List<Item> items) {
        Set<String> entitiesItemIds = new HashSet<>();
        for (Entity entity : entities) {
            entitiesItemIds.add(entity.getItemId());
        }
        List<Item> filterItems = new ArrayList<>();
        for (Item item : items) {
            if (entitiesItemIds.contains(item.getItemId()))
                filterItems.add(item);
        }

        return filterItems;
    }


    public void entrance() throws IOException {
        Pair<List<KG>, List<Item>> pairs = ConstructGraph.prepareKGs();
        KG mergedKg = new KGsMergeBasedOnContent(pairs.getLeft(), pairs.getRight()).runMerge();
        Map inDegreeMap = SimilarityJoin5.InDegreeMap(mergedKg);
        Map outDegreeMap = SimilarityJoin5.OutDegreeMap(mergedKg);
        HashMap<Integer, HashMap<Integer, Double>> simMap = (HashMap<Integer, HashMap<Integer, Double>>)new SimilarityJoin5().TopSimSM(inDegreeMap, outDegreeMap, mergedKg);

        List<Entity> entities = new ArrayList<>();
        List<Node> nodes = mergedKg.getNodes();
        for (Node node : nodes) {
            Entity entity = copyFromNode(node);
            entities.add(entity);
        }

        for (Integer key: simMap.keySet()){
            List<String> str = new ArrayList<>();
            String targetName = "";
            for(Entity e1: entities){
                if (e1.getEntityId().equals(key.toString())){
                    targetName = e1.getEntityName();
                    str.add(targetName);
                }
            }
            HashMap<Integer, Double> values = simMap.get(key);
            for (Integer key1: values.keySet()){
                String name = "";
                for (Entity e: entities){
                    if(e.getEntityId().equals(key1.toString())){
                        name = e.getEntityName();
                        str.add(name);
                    }
                }
            }

//            System.out.println(str.toString());

        }

        for (Entity target : entities) {
            String selected = geTypeOtherMethod(target, entities, (HashMap<Integer, HashMap<Integer, Double>>) simMap);
//            System.out.printf(selected);
            System.out.printf(target.getEntityName() + " " + "真实类别：" + schema.get(target.getTypeId()) + " " + "预测类别：" + schema.get(selected));
            System.out.println();
//            System.out.println(target.getEntityId() + " " + geTypeOtherMethod(target, entities, (HashMap<Integer, HashMap<Integer, Double>>) simMap).toString());
            System.out.println();
        }
    }
//    public double[][] returnSimMatrixUseSimRank(KG kg){
//        Map graph = SimilarityJoin.Graph(kg);
//        double[][] simMatrix = SimilarityJoin.computeSimRank(kg.getNodes().size(), 5, graph, 0.8);
//        return simMatrix;
//    }


    public Entity copyFromNode(Node node) {
        String nodeId = node.getNodeId();
        String nodeName = node.getNodeName();
        String itemId = node.getItemId();
        String typeId = node.getTypeId();
        String parentId = node.getParentId();
        return new Entity(nodeId, nodeName, typeId, parentId, itemId);
    }

    public SchemaExtraction(KG fusionKG) {
        this.fusionKG = fusionKG;
    }

    public SchemaExtraction() {
    }

    /**
     * 打印三元组
     *
     * @param kg
     */
    public void printKG(KG kg) {
        List<Triple> triples = kg.getTriples();
        for (Triple triple : triples) {
            System.out.println(triple.getHead() + " " + triple.getRela() + " " + triple.getTail());
        }
    }

    public HashMap<String, String> getSchema() {
        return schema;
    }

    public static void main(String[] args) throws IOException {
        Pair<List<KG>, List<Item>> pairs = ConstructGraph.prepareKGs();
        KG mergedKg = new KGsMergeBasedOnContent(pairs.getLeft(), pairs.getRight()).runMerge();
        for (Node node : mergedKg.getNodes()) {
            System.out.printf(node.getItemId() + " ");
        }

        SchemaExtraction schemaExtraction = new SchemaExtraction(mergedKg);
        HashMap<String, Set<Entity>> stringSetHashMap = schemaExtraction.typeEntityDic();
//        HashMap<String, Pair<Set<Entity>, Set<Entity>>> nexus = schemaExtraction.getNexus();
        List<Entity> entities = new ArrayList<>();
//        List<String> types = new ArrayList<>(stringSetHashMap.keySet());
        for (String key : stringSetHashMap.keySet()) {
            Set<Entity> ens = stringSetHashMap.get(key);
            entities.addAll(ens);
        }

        List<Item> filterItems = schemaExtraction.filterItem(entities, pairs.getRight());
        // 测试过滤出来的item是否对
//        List<String> itemIds = new ArrayList<>();
//        for(Item item: filterItems){
//            itemIds.add(item.getItemId());
//        }
//        Boolean flag = true;
//        for (Entity entity: entities){
//            if(!itemIds.contains(entity.getItemId())){
//                flag = false;
//                break;
//            }
//        }


        double[][] entity_item_tfidf = schemaExtraction.entity_item_tfidf(entities, filterItems);
        double[][] simMatrix = schemaExtraction.geSimMatrix(entity_item_tfidf);
        for (int i = 0; i < simMatrix.length; i++) {
            for (int j = i + 1; j < simMatrix.length; j++) {
                if (simMatrix[i][j] > 0.0) {
                    System.out.printf("[" + i + "," + j + "]");
                    System.out.printf("[" + entities.get(i).getEntityName() + "," + entities.get(j).getEntityName() + "]");
                    System.out.println();
                }
            }
        }
        for (Entity entity : entities) {
            String selectTypeId = schemaExtraction.geType(entity, entities, simMatrix, 0.0);
            System.out.printf(entity.getEntityName() + " " + schemaExtraction.getSchema().get(entity.getTypeId()) + " " + schemaExtraction.getSchema().get(selectTypeId));
            System.out.println();
        }
//        for (int i = 0; i < tfIdfMatrix.length; i++){
//            for (int j = 0; j < tfIdfMatrix[0].length; j++){
//                System.out.printf(tfIdfMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }

        // 统计类型分布
//        HashMap<String, Integer> tongji = new HashMap<>();
//        for (String type: stringSetHashMap.keySet()){
//            if (!tongji.containsKey(type)){
//                tongji.put(type, stringSetHashMap.get(type).size());
//            }
//        }
//        for (String key : tongji.keySet()){
//            System.out.printf(key + " " + tongji.get(key));
//            System.out.println();
//        }

//        HashMap<Integer, HashMap<Integer, Double>> res = new RWR_4().entrance(tfIdfMatrix);
//        double[][] convertMatrix = schemaExtraction.convertRWR(res);
//        for (int i = 0; i < convertMatrix.length; i++){
//           for (int j = 0; j < convertMatrix[0].length; j++){
//               System.out.printf(convertMatrix[i][j] + " ");
//           }
//            System.out.println();
//        }

//        for (String relation : nexus.keySet()) {
//            Set<Entity> leftEntities = nexus.get(relation).getLeft();
//            Set<Entity> rightEntities = nexus.get(relation).getRight();
//            List<List<Double>> l = new ArrayList<>();
//            List<List<Double>> r = new ArrayList<>();
//            for (Entity left : leftEntities)
//                l.add(Arrays.stream(convertMatrix[entities.indexOf(left)]).boxed().collect(Collectors.toList()));
//            for (Entity right : rightEntities)
//                r.add(Arrays.stream(convertMatrix[entities.indexOf(right)]).boxed().collect(Collectors.toList()));
//            double maxLeftSum = 0.0;
//            double maxRightSum = 0.0;
//            int maxLeftIndex = 0;
//            int maxRightIndex = 0;
//            for (int j = 0; j < l.get(0).size(); j++) {
//                double temp1 = 0.0;
//                double temp2 = 0.0;
//                for (int i = 0; i < l.size(); i++) {
//                    temp1 += l.get(i).get(j);
//                }
//                if (temp1 > maxLeftSum) {
//                    maxLeftSum = temp1;
//                    maxLeftIndex = j;
//                }
//
//
//                for (int i = 0; i < r.size(); i++) {
//                    temp2 += r.get(i).get(j);
//                }
//                if (temp2 > maxRightSum) {
//                    maxRightSum = temp2;
//                    maxRightIndex = j;
//                }
//            }
//            for (Entity len : leftEntities) {
//                System.out.printf(len.getEntityName() + ",");
//            }
//            System.out.printf(" " + relation + " ");
//            for (Entity ren : rightEntities) {
//                System.out.printf(ren.getEntityName() + ",");
//            }
//            System.out.println(schemaExtraction.schema.get(types.get(maxLeftIndex)) + " " + relation + " " + schemaExtraction.schema.get(types.get(maxRightIndex)));
//
//        }
//
//        System.out.println("OK");
    }
}
