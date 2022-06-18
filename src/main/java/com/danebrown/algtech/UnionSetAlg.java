package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UnionSetAlg {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new UnionSetTest());
        AlgCompMenu.addComp(new FindCircleNum());
        AlgCompMenu.addComp(new NumOfIsland());
        AlgCompMenu.run();
    }

    @Data
    public static class UnionSetNode<V> {
        private V v;
        public UnionSetNode(V v){
            this.v = v;
        }
    }
    public static class UnionSet<V> {
        //存储某个节点的最终祖先节点
        Map<UnionSetNode<V>, UnionSetNode<V>> parentMap = new HashMap();
        //值和对象之间的映射关系
        Map<V, UnionSetNode<V>> nodes = new HashMap<>();
        //保存根节点下所包含的节点个数
        Map<UnionSetNode<V>, Integer> sizeMap = new HashMap<>();
        private transient AtomicBoolean isInited = new AtomicBoolean(false);

        public UnionSet() {

        }

        public UnionSet(List<V> list) {
            init(list);
        }

        public void init(List<V> list) {
            for (V v : list) {
                if (!nodes.keySet().contains(v)) {
                    UnionSetNode<V> node = new UnionSetNode<>(v);
                    parentMap.put(node, node);
                    nodes.put(v, node);
                    sizeMap.put(node, 1);
                }
            }
        }


        /**
         * 找到某个对象的祖先节点
         *
         * @param v
         * @return
         */
        private UnionSetNode<V> findAncestor(UnionSetNode<V> v) {
            Stack<UnionSetNode<V>> path = new Stack<>();
            while (v != parentMap.get(v)) {
                path.push(v);
                v = parentMap.get(v);
            }
            while (!path.isEmpty()) {
                parentMap.put(path.pop(), v);
            }
            return v;
        }

        public void union(V left, V right) {
            UnionSetNode<V> lp = findAncestor(nodes.get(left));
            UnionSetNode<V> rp = findAncestor(nodes.get(right));
            if (lp != rp) {
                int lpSize = sizeMap.get(lp);
                int rpSize = sizeMap.get(rp);
                UnionSetNode<V> big = lpSize > rpSize ? lp : rp;
                UnionSetNode<V> small = big == lp ? rp : lp;
                parentMap.put(small, big);
                sizeMap.put(big, lpSize + rpSize);
                sizeMap.remove(small);
            }
        }

        public boolean isSameSet(UnionSetNode<V> finder, UnionSetNode<V> val) {
            return findAncestor(finder) == findAncestor(val);

        }

        public int size() {
            return sizeMap.size();
        }
    }

    @AlgName("并查集")
    public static class UnionSetTest extends AlgCompImpl<Integer, UnionSet<String>> {


        @Override
        public UnionSet<String> prepare() {

            List<String> initVal = new ArrayList<>();
            for (int i = 0; i < ThreadLocalRandom.current().nextInt(3, 100); i++) {
                initVal.add(UUID.randomUUID().toString());
            }
            UnionSet<String> unionSet = new UnionSet<>(initVal);
            return unionSet;
        }

        @Override
        protected Integer standard(UnionSet<String> data) {
            List<String> testData = new ArrayList<>();
            List<String> allKeys = data.nodes.keySet().stream().collect(Collectors.toList());
            for (int i = 0; i < ThreadLocalRandom.current().nextInt(2, data.nodes.size()); i++) {
                int idx = ThreadLocalRandom.current().nextInt(0, data.nodes.size() - 1);
                testData.add(allKeys.get(idx));
            }
            for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 100); i++) {
                String randomKey1 = testData.get(ThreadLocalRandom.current().nextInt(0, testData.size() - 1));
                String randomKey2 = testData.get(ThreadLocalRandom.current().nextInt(0, testData.size() - 1));
                data.union(randomKey1, randomKey2);
                boolean isSameSet = data.isSameSet(data.nodes.get(randomKey1), data.nodes.get(randomKey2));
                if (!isSameSet) {
                    throw new RuntimeException("查询错误");
                }
            }
            return 0;
        }

        @Override
        protected Integer test(UnionSet<String> data) {
            List<String> testData = new ArrayList<>();
            List<String> allKeys = data.nodes.keySet().stream().collect(Collectors.toList());
            for (int i = 0; i < ThreadLocalRandom.current().nextInt(2, data.nodes.size()); i++) {
                int idx = ThreadLocalRandom.current().nextInt(0, data.nodes.size() - 1);
                testData.add(allKeys.get(idx));
            }
            for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 100); i++) {
                String randomKey1 = testData.get(ThreadLocalRandom.current().nextInt(0, testData.size() - 1));
                String randomKey2 = testData.get(ThreadLocalRandom.current().nextInt(0, testData.size() - 1));
                data.union(randomKey1, randomKey2);
                boolean isSameSet = data.isSameSet(data.nodes.get(randomKey1), data.nodes.get(randomKey2));
                if (!isSameSet) {
                    throw new RuntimeException("查询错误");
                }
            }
            return 0;
        }


    }

    /**
     * LEETCODE 547
     * 有 n 个城市，其中一些彼此相连，另一些没有相连。如果城市 a 与城市 b 直接相连，且城市 b 与城市 c 直接相连，那么城市 a 与城市 c 间接相连。
     * <p>
     * 省份 是一组直接或间接相连的城市，组内不含其他没有相连的城市。
     * <p>
     * 给你一个 n x n 的矩阵 isConnected ，其中 isConnected[i][j] = 1 表示第 i 个城市和第 j 个城市直接相连，而 isConnected[i][j] = 0 表示二者不直接相连。
     * <p>
     * 返回矩阵中 省份 的数量。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * <p>
     * 输入：isConnected = [[1,1,0],[1,1,0],[0,0,1]]
     * 输出：2
     * 示例 2：
     * <p>
     * <p>
     * 输入：isConnected = [[1,0,0],[0,1,0],[0,0,1]]
     * 输出：3
     * 用数组方式实现并查集
     */
    @AlgName("朋友圈数量")
    public static class FindCircleNum extends AlgCompImpl<Integer, int[][]> {

        UnionSet<Integer> integerUnionSet = new UnionSet<>();

        @Override
        public int[][] prepare() {
                        int len = ThreadLocalRandom.current().nextInt(0,100);
                        int[][] matrix = new int[len][len];

                        for(int i=0; i < len; i++){
                            for (int j=i; j<len;j++){
                                if(i == j){
                                    matrix[i][j] = 1;
                                }
                                else {
                                    boolean isConnect =
                                            ThreadLocalRandom.current().nextBoolean();
                                    matrix[i][j] = isConnect?1:0;
                                    matrix[j][i] = isConnect?1:0;
                                }

                            }
                        }
                        return matrix;

//            return new int[][]{{1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0}, {0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1}, {1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0}, {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1}, {1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1}, {1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0}, {0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0}, {0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1}, {0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1}, {0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0}, {0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1}};


            //            return new int[][] {{1}};

            //            return new int[][] {{1,0,0},{0,1,0},{0,0,1}};
            //            return new int[][]{{1, 1, 0}, {1, 1, 0}, {0, 0, 1}};
        }

        @Override
        protected Integer standard(int[][] data) {
            UnionSet<Integer> integerUnionSet = new UnionSet<>();
            Set<Integer> strings = new HashSet<>();
            if(data.length <=0){
                return 0;
            }
            if (data.length == 1){
                return 1;
            }


            for (int i = 0; i < data.length; i++) {
                strings.add(i);
                integerUnionSet.init(strings.stream().collect(Collectors.toList()));

                for (int j = i + 1; j < data.length; j++) {
                    strings.add(j);
                    integerUnionSet.init(strings.stream().collect(Collectors.toList()));
                    if (data[i][j] == 1) {
                        integerUnionSet.union(i, j);
                    }

                }
            }

            return integerUnionSet.size();
        }

        /**
         * 【测试通过】
         * @param M
         * @return
         */
        @Override
        protected Integer test(int[][] M) {

            int N = M.length;
            UnionFind unionFind = new UnionFind(N);
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (M[i][j] == 1) {
                        unionFind.union(i, j);
                    }
                }
            }
            return unionFind.sets();

        }

    }

    public static class UnionFind {
        //记录父亲关系，例如 parent[1] = 2 表示1的父亲是2
        private int[] parent;
        //记录每个并查集的大小，只有代表节点才会有值
        private int[] size;
        //帮助数组，记录沿途内存的
        private int[] help;
        private int sets;

        public UnionFind(int n) {
            parent = new int[n];
            size = new int[n];
            help = new int[n];
            sets = n;
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        /**
         * 查找a的父亲
         *
         * @param i
         * @return
         */
        private int find(int i) {
            int hi = 0;
            while (i != parent[i]) {
                help[hi++] = i;
                i = parent[i];
            }
            //这里之所以要才能够hi-1开始，是因为上面的程序最后一个hi++，虽然当前的index还是hi,但是
            //由于有effort-side作用，在执行完赋值以后，index值变成了hi+1了。这里为了消除effort-side
            //效果，必须从hi-1开始
            for (int f = hi-1; f >= 0; f--) {
                parent[help[f]] = i;
            }
            return i;
        }

        public void union(int i, int j) {
            int f1 = find(i);
            int f2 = find(j);
            if (f1 != f2) {
                if (size[f1] >= size[f2]) {
                    size[f1] += size[f2];
                    parent[f2] = f1;

                } else {
                    size[f2] += size[f1];
                    parent[f1] = f2;
                }
                sets--;
            }
        }


        public int sets() {
            return sets;
        }


    }
    //空对象，只是个标志位
    public static class Dot{

    }

    @AlgName("岛屿数量问题")
    public static class NumOfIsland extends AlgCompImpl<Integer,int[][]>{
        FindCircleNum findCircleNum = new FindCircleNum();
        @Override
        public int[][] prepare() {
//            return new int[][]{{1,1,0,0,0},{0,1,0,0,1},{0,0,0,1,1},{0,0,0,0,0},
//                    {0,0,0,0,1}};
            return findCircleNum.prepare();
        }

        @Override
        protected Integer standard(int[][] data) {
            return numOfIsland(data);

        }

        /**
         * 给一个布尔类型的二维数组, 0 表示海, 1 表示岛。如果两个1是相邻的,那么我们认为他们是同一个岛.我们只考虑 上下左右 相邻.
         * 找到大小在 k 及 k 以上的岛屿的数量
         * https://www.lintcode.com/problem/677/description
         * @param grid
         * @return
         */
        public int numOfIsland2(int[][] grid,int k){
            int islandNum=0;
            int[][] tmp = new int[grid.length][grid[0].length];
            for(int i=0;i<grid.length;i++){
                for (int j=0;j<grid.length;j++){
                    tmp[i][j] = grid[i][j];
                }
            }
            for(int i=0;i < tmp.length;i++){
                for(int j=0;j<tmp[0].length;j++){
                    if(tmp[i][j] == 1){
                        if(influence(tmp,i,j) >= k) {
                            islandNum++;
                        }
                    }
                }
            }
            return islandNum;
        }

        /**
         * https://leetcode.cn/problems/number-of-islands/
         * 给你一个由 '1'（陆地）和 '0'（水）组成的的二维网格，请你计算网格中岛屿的数量。
         *
         * 岛屿总是被水包围，并且每座岛屿只能由水平方向和/或竖直方向上相邻的陆地连接形成。
         *
         * 此外，你可以假设该网格的四条边均被水包围。
         *
         * 来源：力扣（LeetCode）
         * 链接：https://leetcode.cn/problems/number-of-islands
         * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
         * @param grid
         * @return
         */
        public int numOfIsland(int[][] grid){
            int islandNum=0;
            if(grid.length<=0) return 0;
            int[][] tmp = new int[grid.length][grid[0].length];
            for(int i=0;i<grid.length;i++){
                for (int j=0;j<grid.length;j++){
                    tmp[i][j] = grid[i][j];
                }
            }
            for(int i=0;i < tmp.length;i++){
                for(int j=0;j<tmp[0].length;j++){
                    if(tmp[i][j] == 1){
                        islandNum++;
                        influence(tmp,i,j);
                    }
                }
            }
            return islandNum;
        }
        public static int influence(int[][] grid, int i, int j){
            if(i<0 || j <0||i >= grid.length || j >= grid[0].length || grid[i][j] != 1){
                return 0 ;
            }
            grid[i][j] = 2;
            return influence(grid,i+1,j)+
            influence(grid,i-1,j)+
            influence(grid,i,j+1)+
            influence(grid,i,j-1)+1;
        }

        @Override
        protected Integer test(int[][] board) {
            if(board.length <=0)
            {
                return 0;
            }
            int row = board.length;
            int col = board[0].length;
            Dot[][] dot = new Dot[row][col];
            List<Dot> dotList = new ArrayList<>();
            for(int i=0;i<row;i++){
                for(int j=0;j<col;j++){
                    if(board[i][j] == 1){
                        dot[i][j] = new Dot();
                        dotList.add(dot[i][j]);
                    }
                }
            }
            UnionSet<Dot> unionSet = new UnionSet<>(dotList);
            // (0,j)  (0,0)跳过了  (0,1) (0,2) (0,3)
            for(int j=1; j < col;j++){
                if(board[0][j-1] == 1 && board[0][j] == 1){
                    unionSet.union(dot[0][j-1],dot[0][j]);
                }
            }
            for(int i=1;i<row;i++){
                if(board[i-1][0]==1 && board[i][0] == 1){
                    unionSet.union(dot[i-1][0],dot[i][0]);
                }
            }
            for(int i=1;i<row;i++){
                for(int j=1;j<col;j++){
                    if(board[i][j] == 1){
                        if(board[i][j-1] == 1){
                            unionSet.union(dot[i][j-1],dot[i][j]);
                        }
                        if(board[i-1][j] == 1){
                            unionSet.union(dot[i-1][j],dot[i][j]);
                        }
                    }
                }
            }
            return unionSet.size();
        }
    }
}
