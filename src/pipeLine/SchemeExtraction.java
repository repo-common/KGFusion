package pipeLine;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import publicDataStructure.Entity;
import publicDataStructure.KG;
import publicDataStructure.Node;
import publicDataStructure.Triple;


import java.util.*;

public class SchemeExtraction {
    private KG fusionKg;

    public KG runExtract() {
        HashMap<Entity, Integer> entityToInteger = geAbstractEntityType();  // 实体和实体类型映射
//        HashMap<String, Integer> relationToInteger = geAbstractRelationType(); // 关系和实体映射
//        return process(entityToInteger, relationToInteger);

        System.out.println("OK");
        return new KG();

    }


    public KG process(HashMap<Entity, Integer> abstractEntityType, HashMap<String, Integer> abstractRelationType) {

        List<Triple> newTriples = new ArrayList<>();  // KG需要的triples
        List<Node> newNodes = new ArrayList<>();       // KG需要的nodes

        // 获取原始的图谱元素
        List<Triple> originalTriples = fusionKg.getTriples();
        List<Node> originalNodes = fusionKg.getNodes();
        List<String> originalRelation = new ArrayList<>();
        for (Triple triple: originalTriples){
            String relation = triple.getRela();
            originalRelation.add(relation);
        }

        // 根据抽象实体和抽象类型 提取模式图


        return new KG();
    }

    public HashMap<Entity, Integer> geAbstractEntityType() {

        // 按照relation分组
        HashMap<String, Pair<List<Entity>, List<Entity>>>  relationNexus = new HashMap<>();
        KG kg = getFusionKg();

        List<Triple> triples = kg.getTriples();
        for (Triple triple: triples){
            String relation = triple.getRela();
            Entity head = triple.getHead();
            Entity tail = triple.getTail();
            if (relationNexus.containsKey(relation)){
                List<Entity> left = relationNexus.get(relation).getLeft();
                List<Entity> right = relationNexus.get(relation).getRight();
//                left.add(head);
//                right.add(tail);
                boolean hasHead = false;
                boolean hasTail = false;

                for (Entity entity: left){
                    if (entity.getEntityId().equals(head.getEntityId()))
                        hasHead = true;
                }

                if (!hasHead)
                    left.add(head);

                // 避免形成环
                for (Entity entity: right){
                    if (entity.getEntityId().equals(tail.getEntityId()))
                        hasTail = true;
                }

                for (Entity entity: left){
                    if (entity.getEntityId().equals(tail.getEntityId()))
                        hasTail = true;
                }


                if (!hasTail)
                    right.add(tail);

                relationNexus.put(relation, Pair.of(left, right));
            }
            else {
                List<Entity> left = new ArrayList<>();
                List<Entity> right = new ArrayList<>();
                left.add(head);
                right.add(tail);
                relationNexus.put(relation, Pair.of(left, right));
            }

        }

        // 测试nexus里面的节点个数 可以正确识别
//        List<Entity> to = new ArrayList<>();
//        for (String relation: relationNexus.keySet()){
//            List<Entity> left = relationNexus.get(relation).getLeft();
//            List<Entity> right = relationNexus.get(relation).getRight();
//            for (Entity entity: left){
//                if (to == null){
//                    to.add(entity);
//                }
//                else {
//                    boolean hasEntity = false;
//                    for (Entity temp: to){
//                        if (temp.getEntityId().equals(entity.getEntityId()))
//                            hasEntity = true;
//                    }
//                    if (!hasEntity)
//                        to.add(entity);
//                }
//            }
//
//            for (Entity entity: right){
//                if (to == null){
//                    to.add(entity);
//                }
//                else {
//                    boolean hasEntity = false;
//                    for (Entity temp: to){
//                        if (temp.getEntityId().equals(entity.getEntityId()))
//                            hasEntity = true;
//                    }
//                    if (!hasEntity)
//                        to.add(entity);
//                }
//            }
//        }
//
//        int total = to.size();



        // 将relationNexus中的划分出来的实体集合编号

        // 由于以下这种方式是基于上面的方法，所以会出现同一个实体出现两种类型，还需要再内部做另外的判断
        // 所以要再外部维护一个map,已经被标注过的实体不能够再属于另一个类型

        // 构建svd使用的单词文档矩阵 单词就是实体id，文档是关系
        List<Entity> entities = new ArrayList<>();
        List<String> relations =  new ArrayList<>();
        List<Node> nodes = fusionKg.getNodes();
        // 构建nodes对应的entities, 便于查表
        for (Node node : nodes){
            String nodeId = node.getNodeId();
            String nodeName = node.getNodeName();
            String typeId = node.getTypeId();
            String itemId = node.getItemId();

            Entity entity = new Entity(nodeId, nodeName, typeId,"-1", itemId);
            entities.add(entity);
        }
        // 从relationNexus构建relationList
        for (String relation: relationNexus.keySet()){
            relations.add(relation);
        }

        // 构建word, relation matrix
        int lenEntity = entities.size();
        int lenRelation = relations.size();
        double[][] frequencyMatrix = new double[lenEntity][lenRelation];
        for (String relation: relations){
            List<Entity> left = relationNexus.get(relation).getLeft();
            List<Entity> right = relationNexus.get(relation).getRight();
            int relationIndex = relations.indexOf(relation);
            for (Entity leftEntity: left){
                int entityIndex = entities.indexOf(leftEntity);
                frequencyMatrix[entityIndex][relationIndex] += 1;
            }

            for (Entity rightEntity: right){
                int entityIndex = entities.indexOf(rightEntity);
                frequencyMatrix[entityIndex][relationIndex] += 1;
            }
        }

        for (int i = 0; i < lenEntity; i++){
            for (int j = 0; j < lenRelation; j++){
                System.out.print(frequencyMatrix[i][j] + " ");
            }
            System.out.println();
        }


        System.out.println("OK");


        // 有问题
//        HashMap<Entity, Pair<Boolean, Integer>> entityClassMap = new HashMap<>();  // 表示一个实体是否被biao'zhuu'obiao'zhuu'o
//        int count = 0;
//        for (tion: relationNexus.keySet()){
//            Pair<List<Entity>, List<Entity>> item = relationNexus.get(relation);
//            List<Entity> leftSet = item.getLeft();
//            List<Entity> rightSet = item.getRight();
//            for (Entity entity: leftSet){
//                entityClassMap.put(entity, count);
//            }
//            for (Entity entity: rightSet){
//                entityClassMap.put(entity, count + 1);
//                count++;
//            }
//
//            count++;
//        }
//
//        System.out.println("OK");
//
//        return entityClassMap;

        // 先使用svd对关系进行分组


        return new HashMap<>();
    }

    public HashMap<String, Integer> geAbstractRelationType() {
        return new HashMap<>();
    }


    public SchemeExtraction(KG fusionKg) {
        this.fusionKg = fusionKg;
    }

    public void setFusionKg(KG fusionKg) {
        this.fusionKg = fusionKg;
    }

    public KG getFusionKg() {
        return fusionKg;
    }
}
