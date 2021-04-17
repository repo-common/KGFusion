import java.util.HashMap;

public class SchemeExtraction {


    /*
     * @description: TODO 运行此文件生成模式图
     * @params: util 使用到融合后的图文件路径
     * @return:
     * @author: DAI
     * @dateTime: 2021/2/21 0021 11:18
     */
    public static void generateSchemaGraph(PathUtil util){

        HashMap<String, String> entityClassMap = generateAbstractItemClass(util);
        HashMap<String, String>  relationClassMap = generateAbstractRelationClass(util);

        generate(entityClassMap, relationClassMap, util);

        System.out.println("Scheme process");
    }


    /*
     * @description: TODO  生成模式图
     * @params: HashMap<String, String> entityClassMap： 实体-类型字典，
     *          HashMap<String, String>  relationClassMap：关系-类型字典
     *          util 使用模式图转存路径
     * @return:
     * @author: DAI
     * @dateTime: 2021/2/21 0021 11:20
     */
    private static void generate(HashMap<String, String> entityClassMap, HashMap<String, String>  relationClassMap, PathUtil util) {

    }

    /*
     * @description: TODO 生成 实体-类型 字典
     * @params: util 使用融合后的图文件
     * @return: HashMap<String, String>  实体-类型
     * @author: DAI
     * @dateTime: 2021/2/21 0021 11:24
     */
    public static HashMap<String, String> generateAbstractItemClass(PathUtil util){
            return new HashMap<String, String>();
    }


    /*
     * @description: TODO 生成 关系-类型 字典
     * @params: util 使用融合后的图文件
     * @return: HashMap<String, String>  关系-类型
     * @author: DAI
     * @dateTime: 2021/2/21 0021 11:24
     */
    public static HashMap<String, String> generateAbstractRelationClass(PathUtil util){
            return new HashMap<String, String>();
    }




    public static void main(String[] args) {

    }
}
