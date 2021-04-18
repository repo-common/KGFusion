package publicDataStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Entity {
    private String entityId;//实体id
    private String entityName;//实体名称
    private String typeId;
    private String parentId;//父实体（上一层实体）id
    private String itemId;//所属条目的id(从这个条目中抽取出来的)

    public Entity(String entityId, String entityName, String typeId,String parentId, String itemId) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.typeId = typeId;
        this.parentId = parentId;
        this.itemId = itemId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getParentId() {
        return parentId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }



    public static void main(String[] args) {
//        Entity entity1 = new Entity("entity1", "1", "-1", "1");
//        Entity entity2 = new Entity("entity2","1","-1", "1");
//        Entity entity3 = new Entity("entity2", "1", "-1","2");
//        Entity entity4 = new Entity("entity2","1","-1","1");
//
//        System.out.println(entity1.equals(entity2));
//        System.out.println(entity2.equals(entity3));
//        System.out.println(entity3.equals(entity4));
//
//        List<Entity> entities = new ArrayList<>();
//        entities.add(entity1);
//        entities.add(entity2);
//        entities.add(entity3);
//
//        int index = entities.indexOf(entity4);
//        System.out.println(index);
    }
}
