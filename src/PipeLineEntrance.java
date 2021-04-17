
import javax.swing.plaf.synth.SynthCheckBoxMenuItemUI;
import java.util.List;

public class PipeLineEntrance {

    public static void main(String[] args) {

        PathUtil util = new PathUtil("1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15");
        GraphCleaning.runClean(util);
        GraphMergeBasedOnContent.runMerge(util);
        int[][] unifiedRelationMatrix = HeterogeneousRelationRegularize.generateUnifiedRelationMatrix(util);

        double[][] similarityMatrix = SimilarityLinking.generateSimilarityMatrix(unifiedRelationMatrix);
        GraphFusion graphFusion = new GraphFusion(3,0.8);
        graphFusion.generateFusionGraph(similarityMatrix, graphFusion.getNumOfSelectedPairs(), graphFusion.getThreshold(),util);
        SchemeExtraction.generateSchemaGraph(util);


    }
}
