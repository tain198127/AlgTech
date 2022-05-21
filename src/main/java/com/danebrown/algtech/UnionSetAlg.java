package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.AllArgsConstructor;
import lombok.Data;

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

        public void union(UnionSetNode<V> left, UnionSetNode<V> right) {
            UnionSetNode<V> lp = findAncestor(left);
            UnionSetNode<V> rp = findAncestor(right);
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
                data.union(data.nodes.get(randomKey1), data.nodes.get(randomKey2));
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
                data.union(data.nodes.get(randomKey1), data.nodes.get(randomKey2));
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
                        integerUnionSet.union(integerUnionSet.nodes.get(i),
                                integerUnionSet.nodes.get(j));
                    }

                }
            }

            return integerUnionSet.size();
        }

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
            for (hi--; hi >= 0; hi--) {
                parent[help[hi]] = i;
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
}
