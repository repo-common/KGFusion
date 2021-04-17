import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PathUtil {


    private String rawDocumentsDir;  // 文本目录
    private String rawGraphsDir;  // 图的目录 三元组形式
    private String cleanedGraphsDir;  // 清洗后的三元组图谱目录
    private String mergedGraphPath;  // 合并后的一张图
    private String mergedAndEncodedGraphPath; //
    private String LiteralToIdxDicPathOfMer; // 字面量字典
    private String relationToIdxDicPathOfMer; // 关系字典
    private String fusionGraphPath; //
    private String fusionAndEncodedGraphPath;
    private String literalToIdxDicPathOfFusion;
    private String relationToIdxDicPathOfFusion;
    private String schemeGraphPath;
    private String EncodedSchemePath;
    private String literalToIdxDicPathScheme;
    private String relationToIdxDicPathOfScheme;

    public PathUtil(String rawDocumentsDir,
                    String rawGraphsDir,
                    String cleanedGraphsDir,
                    String mergedGraphPath,
                    String mergedAndEncodedGraphPath,
                    String literalToIdxDicPathOfMer,
                    String relationToIdxDicPathOfMer,
                    String fusionGraphPath,
                    String fusionAndEncodedGraphPath,
                    String literalToIdxDicPathOfFusion,
                    String relationToIdxDicPathOfFusion,
                    String schemeGraphPath,
                    String encodedSchemePath,
                    String literalToIdxDicPathScheme,
                    String relationToIdxDicPathOfScheme) {
        this.rawDocumentsDir = rawDocumentsDir;
        this.rawGraphsDir = rawGraphsDir;
        this.cleanedGraphsDir = cleanedGraphsDir;
        this.mergedGraphPath = mergedGraphPath;
        this.mergedAndEncodedGraphPath = mergedAndEncodedGraphPath;
        LiteralToIdxDicPathOfMer = literalToIdxDicPathOfMer;
        this.relationToIdxDicPathOfMer = relationToIdxDicPathOfMer;
        this.fusionGraphPath = fusionGraphPath;
        this.fusionAndEncodedGraphPath = fusionAndEncodedGraphPath;
        this.literalToIdxDicPathOfFusion = literalToIdxDicPathOfFusion;
        this.relationToIdxDicPathOfFusion = relationToIdxDicPathOfFusion;
        this.schemeGraphPath = schemeGraphPath;
        EncodedSchemePath = encodedSchemePath;
        this.literalToIdxDicPathScheme = literalToIdxDicPathScheme;
        this.relationToIdxDicPathOfScheme = relationToIdxDicPathOfScheme;
    }

    public void setRawDocumentsDir(String rawDocumentsDir) {
        this.rawDocumentsDir = rawDocumentsDir;
    }

    public void setRawGraphsDir(String rawGraphsDir) {
        this.rawGraphsDir = rawGraphsDir;
    }

    public void setCleanedGraphsDir(String cleanedGraphsDir) {
        this.cleanedGraphsDir = cleanedGraphsDir;
    }

    public void setMergedGraphPath(String mergedGraphPath) {
        this.mergedGraphPath = mergedGraphPath;
    }

    public void setMergedAndEncodedGraphPath(String mergedAndEncodedGraphPath) {
        this.mergedAndEncodedGraphPath = mergedAndEncodedGraphPath;
    }

    public void setLiteralToIdxDicPathOfMer(String literalToIdxDicPathOfMer) {
        LiteralToIdxDicPathOfMer = literalToIdxDicPathOfMer;
    }

    public void setRelationToIdxDicPathOfMer(String relationToIdxDicPathOfMer) {
        this.relationToIdxDicPathOfMer = relationToIdxDicPathOfMer;
    }

    public void setFusionGraphPath(String fusionGraphPath) {
        this.fusionGraphPath = fusionGraphPath;
    }

    public void setFusionAndEncodedGraphPath(String fusionAndEncodedGraphPath) {
        this.fusionAndEncodedGraphPath = fusionAndEncodedGraphPath;
    }

    public void setLiteralToIdxDicPathOfFusion(String literalToIdxDicPathOfFusion) {
        this.literalToIdxDicPathOfFusion = literalToIdxDicPathOfFusion;
    }

    public void setRelationToIdxDicPathOfFusion(String relationToIdxDicPathOfFusion) {
        this.relationToIdxDicPathOfFusion = relationToIdxDicPathOfFusion;
    }

    public void setSchemeGraphPath(String schemeGraphPath) {
        this.schemeGraphPath = schemeGraphPath;
    }

    public void setEncodedSchemePath(String encodedSchemePath) {
        EncodedSchemePath = encodedSchemePath;
    }

    public void setLiteralToIdxDicPathScheme(String literalToIdxDicPathScheme) {
        this.literalToIdxDicPathScheme = literalToIdxDicPathScheme;
    }

    public void setRelationToIdxDicPathOfScheme(String relationToIdxDicPathOfScheme) {
        this.relationToIdxDicPathOfScheme = relationToIdxDicPathOfScheme;
    }


    public String getRawDocumentsDir() {
        return rawDocumentsDir;
    }

    public String getRawGraphsDir() {
        return rawGraphsDir;
    }

    public String getCleanedGraphsDir() {
        return cleanedGraphsDir;
    }

    public String getMergedGraphPath() {
        return mergedGraphPath;
    }

    public String getMergedAndEncodedGraphPath() {
        return mergedAndEncodedGraphPath;
    }

    public String getLiteralToIdxDicPathOfMer() {
        return LiteralToIdxDicPathOfMer;
    }

    public String getRelationToIdxDicPathOfMer() {
        return relationToIdxDicPathOfMer;
    }

    public String getFusionGraphPath() {
        return fusionGraphPath;
    }

    public String getFusionAndEncodedGraphPath() {
        return fusionAndEncodedGraphPath;
    }

    public String getLiteralToIdxDicPathOfFusion() {
        return literalToIdxDicPathOfFusion;
    }

    public String getRelationToIdxDicPathOfFusion() {
        return relationToIdxDicPathOfFusion;
    }

    public String getSchemeGraphPath() {
        return schemeGraphPath;
    }

    public String getEncodedSchemePath() {
        return EncodedSchemePath;
    }

    public String getLiteralToIdxDicPathScheme() {
        return literalToIdxDicPathScheme;
    }

    public String getRelationToIdxDicPathOfScheme() {
        return relationToIdxDicPathOfScheme;
    }

    public List<String> getFileList(String fileDir){
        return new ArrayList<>();
    }
}
