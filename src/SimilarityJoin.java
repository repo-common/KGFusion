    public class SimilarityJoin {

    private double[][] unifiedRelationMatrix;

    public double[][] geSimilarityMatrix(){
        return new double[1][1];
    }

        public SimilarityJoin(double[][] unifiedRelationMatrix) {
            this.unifiedRelationMatrix = unifiedRelationMatrix;
        }

        public double[][] getUnifiedRelationMatrix() {
            return unifiedRelationMatrix;
        }

        public void setUnifiedRelationMatrix(double[][] unifiedRelationMatrix) {
            this.unifiedRelationMatrix = unifiedRelationMatrix;
        }
    }
