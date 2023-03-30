package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompContext;
import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import com.google.common.base.Objects;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Log4j2
public class GraphAlg {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new GraphBFS());
        AlgCompMenu.addComp(new Kruskal());
        AlgCompMenu.run();
    }
    @Data
    @Builder
    @ToString
    public static class GraphNode{
        //点的值
        int value;
        //入度
        int in = 0;
        //出度
        int out = 0;
        //从当前节点指向的点
        public List<GraphNode> nodes = new ArrayList<>();
        //从当前节点触发的边
        public List<Edge> edges = new ArrayList<>();

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof GraphNode))
                return false;
            GraphNode graphNode = (GraphNode) o;
            return getValue() == graphNode.getValue() && getIn() == graphNode.getIn() && getOut() == graphNode.getOut() ;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getValue(), getIn(), getOut());
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
    @Data
    @Builder
    @ToString
    public static class Edge{
        int val;
        GraphNode from;
        GraphNode to;

        @Override
        public String toString() {
            return "[" +  val + "," + from + "," + to + ']';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Edge))
                return false;
            Edge edge = (Edge) o;
            return getVal() == edge.getVal() && Objects.equal(getFrom().value,
                    edge.getFrom().value) && Objects.equal(getTo().value,
                    edge.getTo().value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getVal(), getFrom().value, getTo().value);
        }
    }
    @Data
    public static class Graph{
        HashMap<Integer,GraphNode> nodes = new HashMap<>();
        HashSet<Edge> edges = new HashSet<>();
    }

    @Data
    public static class Record{
        private DirectedGraphNode node;
        private int nodes;
        public Record(DirectedGraphNode node, int nodes){
            this.node = node;
            this.nodes = nodes;
        }
    }

    @AlgName("图")
    public static class GraphBFS extends AlgCompImpl<Integer, GraphNode> {

        @Override
        public GraphNode prepare(AlgCompContext context) {
            GraphNode root = GraphNode.builder().build();
            return root;
        }

        @Override
        protected Integer standard(GraphNode data) {
            log.info("BFS");
            Set<GraphNode> set = new HashSet<>();
            Queue<GraphNode> queue =new LinkedList<>();
            int count = 1;
            queue.add(data);
            set.add(data);
            while (!queue.isEmpty()){
                GraphNode node = queue.poll();
                log.debug("node:{}",node.getValue());
                count++;
                for(GraphNode n: node.nodes){
                    if(!set.contains(n)){
                        set.add(n);
                        queue.add(n);
                    }
                }


            }
            return count;
        }

        @Override
        protected Integer test(GraphNode data) {
            log.info("DFS");
            Set<GraphNode> set = new HashSet<>();
            Stack<GraphNode> stack = new Stack<>();
            set.add(data);
            stack.add(data);
            //入栈就打印，为什么
            log.info("{}",data.value);
            int count = 1;
            while (!stack.isEmpty()){
                count++;
                GraphNode cur =  stack.pop();
                for(GraphNode n: cur.nodes){
                    if(!set.contains(n)){
                        stack.push(cur);
                        stack.push(n);
                        log.info("{}",n.value);
                        set.add(n);
                        break;//这里为什么要加break呢？
                    }
                }
            }
            return count;
        }
    }
    public static  class DirectedGraphNode {
      int label;
      List<DirectedGraphNode> neighbors;
      DirectedGraphNode(int x) {
          label = x;
          neighbors = new ArrayList<DirectedGraphNode>();
      }
    }
    /**
     * Definition for Directed graph.
     * class DirectedGraphNode {
     *     int label;
     *     List<DirectedGraphNode> neighbors;
     *     DirectedGraphNode(int x) {
     *         label = x;
     *         neighbors = new ArrayList<DirectedGraphNode>();
     *     }
     * }
     * 给定一个有向图，图节点的拓扑排序定义如下:
     *
     * 对于图中的每一条有向边 A -> B , 在拓扑排序中A一定在B之前.
     * 拓扑排序中的第一个节点可以是图中的任何一个没有其他节点指向它的节点.
     * 针对给定的有向图找到任意一种拓扑排序的顺序.
     *
     * 输入：
     * graph = {0,1,2,3#1,4#2,4,5#3,4,5#4#5}
     *
     * 输出：
     *
     * [0, 1, 2, 3, 4, 5]
     */
    @AlgName("拓扑排序")
    public static class SortTopology extends AlgCompImpl<List<DirectedGraphNode>,
            List<DirectedGraphNode>>{

        @Override
        public List<DirectedGraphNode> prepare(AlgCompContext context) {
            return null;
        }

        @Override
        protected List<DirectedGraphNode> standard(List<DirectedGraphNode> data) {
            HashMap<DirectedGraphNode, Record> order = new HashMap<>();
            for (DirectedGraphNode cur : data) {
                f(cur, order);
            }
            ArrayList<Record> recordArr = new ArrayList<>();
            for (Record r : order.values()) {
                recordArr.add(r);
            }
            recordArr.sort((o1,o2)-> o2.nodes - o1.nodes);
            ArrayList<DirectedGraphNode> ans = new ArrayList<>();
            for (Record r : recordArr) {
                ans.add(r.node);
            }
            return ans;
        }

        @Override
        protected List<DirectedGraphNode> test(List<DirectedGraphNode> data) {
            HashMap<DirectedGraphNode, Record> order = new HashMap<>();
            for (DirectedGraphNode cur : data) {
                f(cur, order);
            }
            ArrayList<Record> recordArr = new ArrayList<>();
            for (Record r : order.values()) {
                recordArr.add(r);
            }
            recordArr.sort((o1,o2)-> o2.nodes - o1.nodes);
            ArrayList<DirectedGraphNode> ans = new ArrayList<>();
            for (Record r : recordArr) {
                ans.add(r.node);
            }
            return ans;
        }

        public static Record f(DirectedGraphNode cur, HashMap<DirectedGraphNode, Record> order) {
            if (order.containsKey(cur)) {
                return order.get(cur);
            }
            int follow = 0;
            for (DirectedGraphNode next : cur.neighbors) {
                follow = Math.max(follow, f(next, order).nodes);
            }
            Record ans = new Record(cur, follow + 1);
            order.put(cur, ans);
            return ans;
        }

    }

    @AlgName("最小并查集")
    public static class Kruskal extends AlgCompImpl<List<String>,Graph>{
        // Union-Find Set
        public static class UnionFind {
            // key 某一个节点， value key节点往上的节点
            private HashMap<GraphNode, GraphNode> fatherMap;
            // key 某一个集合的代表节点, value key所在集合的节点个数
            private HashMap<GraphNode, Integer> sizeMap;

            public UnionFind() {
                fatherMap = new HashMap<GraphNode, GraphNode>();
                sizeMap = new HashMap<GraphNode, Integer>();
            }

            public void makeSets(Collection<GraphNode> nodes) {
                fatherMap.clear();
                sizeMap.clear();
                for (GraphNode node : nodes) {
                    fatherMap.put(node, node);
                    sizeMap.put(node, 1);
                }
            }

            private GraphNode findFather(GraphNode n) {
                Stack<GraphNode> path = new Stack<>();
                while(n != fatherMap.get(n)) {
                    path.add(n);
                    n = fatherMap.get(n);
                }
                while(!path.isEmpty()) {
                    fatherMap.put(path.pop(), n);
                }
                return n;
            }

            public boolean isSameSet(GraphNode a, GraphNode b) {
                return findFather(a) == findFather(b);
            }

            public void union(GraphNode a, GraphNode b) {
                if (a == null || b == null) {
                    return;
                }
                GraphNode aDai = findFather(a);
                GraphNode bDai = findFather(b);
                if (aDai != bDai) {
                    int aSetSize = sizeMap.get(aDai);
                    int bSetSize = sizeMap.get(bDai);
                    if (aSetSize <= bSetSize) {
                        fatherMap.put(aDai, bDai);
                        sizeMap.put(bDai, aSetSize + bSetSize);
                        sizeMap.remove(aDai);
                    } else {
                        fatherMap.put(bDai, aDai);
                        sizeMap.put(aDai, aSetSize + bSetSize);
                        sizeMap.remove(bDai);
                    }
                }
            }
        }
        public static class EdgeComparator implements Comparator<Edge> {

            @Override
            public int compare(Edge o1, Edge o2) {
                return o1.val - o2.val;
            }

        }

        @Override
        public Graph prepare(AlgCompContext context) {

            int nodes = ThreadLocalRandom.current().nextInt(1, 10);
            int edges = ThreadLocalRandom.current().nextInt(1, nodes * 2);

            Graph graph = new Graph();

            List<GraphNode> nodeList = new ArrayList<>();
            for (int i = 0; i < nodes; i++) {
                GraphNode graphNode =
                        GraphNode.builder().value(i)
                                .nodes(new ArrayList<>())
                                .edges(new ArrayList<>()).build();
                nodeList.add(graphNode);
            }

            HashSet<Edge> edgeSet = new HashSet<>();
            HashSet<String> edgeKeySet = new HashSet<>();
            HashMap<Integer, GraphNode> nodeMap = new HashMap<>();
            for (int i = 0; i < edges; i++) {
                int fromNode = ThreadLocalRandom.current().nextInt(0, nodes);
                int toNode;
                do {
                    toNode = ThreadLocalRandom.current().nextInt(0, nodes);
                } while (toNode == fromNode);

                String edgeKey = String.format("%s_%s", fromNode, toNode);
                if (!edgeKeySet.contains(edgeKey)) {
                    GraphNode from = nodeList.get(fromNode);
                    GraphNode to = nodeList.get(toNode);
                    int val = ThreadLocalRandom.current().nextInt(1, nodes);
                    Edge edge =
                            Edge.builder().from(from)
                                    .to(to).val(val).build();
                    from.getEdges().add(edge);
                    from.getNodes().add(to);
                    from.out++;
                    to.in++;
                    nodeMap.put(from.value, from);
                    nodeMap.put(to.value, to);
                    edgeSet.add(edge);
                    edgeKeySet.add(edgeKey);

                    // add edge to 'to' node as well
                    to.getEdges().add(edge);
                    to.getNodes().add(from);
                    to.out++;
                    from.in++;
                }


                // set up

            }
            return graph;
        }
//        @Override
//        public Graph prepare(AlgCompContext context) {
//            Graph graph = new Graph();
//            int nodes = ThreadLocalRandom.current().nextInt(1,10);
//            int edges = ThreadLocalRandom.current().nextInt(1,nodes*2);
//            List<GraphNode> nodeList = new ArrayList<>();
//            for(int i=0;i < nodes;i++){
//                GraphNode graphNode =
//                        GraphNode.builder().value(i)
//                                .nodes(new ArrayList<>())
//                                .edges(new ArrayList<>()).build();
//                nodeList.add(graphNode);
//            }
//            HashSet<Edge> edgeSet = new HashSet<>();
//            HashSet<String> edgeKeySet = new HashSet<>();
//            HashMap<Integer,GraphNode> nodeMap = new HashMap<>();
//            for(int i=0;i< edges;i++){
//                int fromNode = ThreadLocalRandom.current().nextInt(0,nodes);
//                int toNode;
//                do {
//                    toNode = ThreadLocalRandom.current().nextInt(0, nodes);
//                } while (toNode == fromNode);
//                String edgeKey = String.format("%s_%s",fromNode,toNode);
//                if(!edgeKeySet.contains(edgeKey)){
//                    GraphNode from = nodeList.get(fromNode);
//                    GraphNode to = nodeList.get(toNode);
//                    int val = ThreadLocalRandom.current().nextInt(1,edges);
//                    Edge edge  =
//                            Edge.builder().from(from)
//                                    .to(to).val(val).build();
//                    from.getEdges().add(edge);
//                    from.getNodes().add(to);
//                    from.out++;
//                    to.in++;
//                    nodeMap.put(from.value,from);
//                    nodeMap.put(to.value,to);
//                    edgeSet.add(edge);
//                }
//            }
//
//            for(Edge e: edgeSet){
//                nodeMap.put(e.from.value,e.from);
//                nodeMap.put(e.to.value,e.to);
//            }
//            graph.setEdges(edgeSet);
//            graph.setNodes(nodeMap);
//
//            return graph;
//        }
        private List<String> toNormorlize(Set<Edge> edges){
            PriorityQueue<Edge> resultQueue =
                    new PriorityQueue<>(new EdgeComparator());
            resultQueue.addAll(edges);
            Set<String> allResult = new HashSet<>();
            for(Edge e: resultQueue){
                allResult.add(e.toString());
            }

            return allResult.stream().sorted().collect(Collectors.toList());
        }
        @Override
        protected List<String> standard(Graph graph) {
            UnionFind unionFind = new UnionFind();
            unionFind.makeSets(graph.nodes.values());
            // 从小的边到大的边，依次弹出，小根堆！
            PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(new EdgeComparator());
            for (Edge edge : graph.edges) { // M 条边
                priorityQueue.add(edge);  // O(logM)
            }
            Set<Edge> result = new HashSet<>();
            while (!priorityQueue.isEmpty()) { // M 条边
                Edge edge = priorityQueue.poll(); // O(logM)
                if (!unionFind.isSameSet(edge.from, edge.to)) { // O(1)
                    result.add(edge);
                    unionFind.union(edge.from, edge.to);
                }
            }
            return toNormorlize(result);
        }
        public static Set<Edge> primMST(Graph graph) {
            // 解锁的边进入小根堆
            PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(new EdgeComparator());

            // 哪些点被解锁出来了
            HashSet<GraphNode> nodeSet = new HashSet<>();



            Set<Edge> result = new HashSet<>(); // 依次挑选的的边在result里

            for (GraphNode node : graph.nodes.values()) { // 随便挑了一个点
                // node 是开始点
                if (!nodeSet.contains(node)) {
                    nodeSet.add(node);
                    for (Edge edge : node.edges) { // 由一个点，解锁所有相连的边
                        priorityQueue.add(edge);
                    }
                    while (!priorityQueue.isEmpty()) {
                        Edge edge = priorityQueue.poll(); // 弹出解锁的边中，最小的边
                        GraphNode toNode = edge.to; // 可能的一个新的点
                        if (!nodeSet.contains(toNode)) { // 不含有的时候，就是新的点
                            nodeSet.add(toNode);
                            result.add(edge);
                            for (Edge nextEdge : toNode.edges) {
                                priorityQueue.add(nextEdge);
                            }
                        }
                    }
                }
                // break;
            }
            return result;
        }
        @Override
        protected List<String> test(Graph graph) {
            Set<Edge> result = primMST(graph);
            return toNormorlize(result);
        }
    }
//TODO 未完成
    @AlgName("图最短路径")
    public static class Dijkstra extends AlgCompImpl<Map<GraphNode,Integer>,
            GraphNode>{

        @Override
        public GraphNode prepare(AlgCompContext context) {
            return null;
        }

        //使用Dijkstra算法
        @Override
        protected Map<GraphNode, Integer> standard(GraphNode from) {
            HashMap<GraphNode, Integer> distanceMap = new HashMap<>();
            distanceMap.put(from, 0);
            // 打过对号的点
            HashSet<GraphNode> selectedNodes = new HashSet<>();
            GraphNode minNode = getMinDistanceAndUnselectedNode(distanceMap, selectedNodes);
            while (minNode != null) {
                //  原始点  ->  minNode(跳转点)   最小距离distance
                int distance = distanceMap.get(minNode);
                for (Edge edge : minNode.edges) {
                    GraphNode toNode = edge.to;
                    if (!distanceMap.containsKey(toNode)) {
                        distanceMap.put(toNode, distance + edge.val);
                    } else { // toNode
                        distanceMap.put(edge.to,
                                Math.min(distanceMap.get(toNode),
                                        distance + edge.val));
                    }
                }
                selectedNodes.add(minNode);
                minNode = getMinDistanceAndUnselectedNode(distanceMap, selectedNodes);
            }
            return distanceMap;
        }
    public static GraphNode getMinDistanceAndUnselectedNode(HashMap<GraphNode, Integer> distanceMap, HashSet<GraphNode> touchedNodes) {
        GraphNode minNode = null;
        int minDistance = Integer.MAX_VALUE;
        for (Map.Entry<GraphNode, Integer> entry : distanceMap.entrySet()) {
            GraphNode node = entry.getKey();
            int distance = entry.getValue();
            if (!touchedNodes.contains(node) && distance < minDistance) {
                minNode = node;
                minDistance = distance;
            }
        }
        return minNode;
    }

        //使用改进版Dijkstra算法
        @Override
        protected Map<GraphNode, Integer> test(GraphNode data) {
            PriorityQueue<GraphNode> priorityQueue = new PriorityQueue<>();

            return null;

        }

    }
}
