import java.util.LinkedHashMap;
import java.util.List;

public class KG {

    private List<Node> nodes; //Node就是Entity
    private List<Triple> triples;
    private LinkedHashMap<Node, List<Node>> edges;//描述了每个节点及其相邻节点（对应与该节点相连的多条边）。可由triples生成//相当于图论中的List<Edge> edges;
    private LinkedHashMap<Node, List<Integer>> directions; //方向


}
