import java.util.HashMap;

public class SchemeExtraction {
    private KG fusionKg;

    public KG runExtract(){
        return new KG();
    }



    public KG process(HashMap<Entity, Integer> abstractEntityType, HashMap<String, Integer> abstractRelationType){
        return new KG();
    }

    public HashMap<Entity, Integer> geAbstractEntityType(){
        return new HashMap<>();
    }

    public HashMap<String, Integer> geAbstractRelation(){
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
