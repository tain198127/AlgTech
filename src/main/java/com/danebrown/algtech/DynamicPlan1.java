package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 动态规划
 */
@Log4j2
public class DynamicPlan1 {
    public static void main(String[] args) {

        AlgCompMenu.addComp(new Fib());
        AlgCompMenu.addComp(new RobotBestWalk());
        AlgCompMenu.addComp(new SmartestPokerPlayer());
        AlgCompMenu.addComp(new Knapsack());
        AlgCompMenu.run();
    }

    /**
     * 斐波那契数列
     */
    @AlgName("动态规划版斐波那契")
    public static class Fib extends AlgCompImpl<Integer, Integer> {

        public Map<Integer, Integer> cache = new HashMap<>();

        @Override
        public Integer prepare() {
            return ThreadLocalRandom.current().nextInt(90, 100);
        }

        @Override
        protected Integer test(Integer data) {
            if (data <= 2) {
                return 1;
            }
            if (!cache.containsKey(data)) {
                cache.put(data, standard(data - 1) + standard(data - 2));
            }
            return cache.get(data);
        }


        @Override
        protected Integer standard(Integer data) {
            int last = 0;
            int cur = 0;
            for (int i = 0; i <= data; i++) {
                if (i == 1) {
                    cur = 1;
                    continue;
                }
                if (i == 2) {
                    last = cur;
                    cur = 1;
                    continue;
                }
                int tmp = last;
                last = cur;
                cur = last + tmp;
            }
            return cur;
        }
    }

    @Data
    @AllArgsConstructor
    public static class RobotBestWalkInput {
        /**
         * 路径长度
         */
        public int N;
        /**
         * 开始位置
         */
        public int M;
        /**
         * 目标位置
         */
        public int P;
        /**
         * 步数
         */
        public int K;
    }

    /**
     * 假设有排成一行的N个位置，记为1~N，N 一定大于或等于 2
     * 开始时机器人在其中的M位置上(M 一定是 1~N 中的一个)
     * 如果机器人来到1位置，那么下一步只能往右来到2位置；
     * 如果机器人来到N位置，那么下一步只能往左来到 N-1 位置；
     * 如果机器人来到中间位置，那么下一步可以往左走或者往右走；
     * 规定机器人必须走 K 步，最终能来到P位置(P也是1~N中的一个)的方法有多少种
     * 给定四个参数 N、M、K、P，返回方法数。
     */
    @AlgName(value = "机器人巡径数", timout = 10)
    public static class RobotBestWalk extends AlgCompImpl<Integer, RobotBestWalkInput> {

        private static boolean check(int cur, int rest, int aim, int N) {
            return N >= 2 && cur >= 1 && cur <= N && aim >= 1 && aim <= N && rest >= 1;
        }
        
        private static Integer standard_process(int cur, int rest, int aim, int N) {
            if (rest == 0) {
                return cur == aim ? 1 : 0;
            }
            if (cur == 1) {
                return standard_process(cur + 1, rest - 1, aim, N);
            }
            if (cur == N) {
                return standard_process(cur - 1, rest - 1, aim, N);
            }
            return standard_process(cur + 1, rest - 1, aim, N) + standard_process(cur - 1, rest - 1, aim, N);

        }

        /**
         * @param cur   当前位置
         * @param rest  剩余步骤
         * @param aim   目标
         * @param N     边界
         * @param cache 傻缓存
         * @return
         */
        private static Integer standard_process_delbranch(int cur, int rest, int aim, int N, int[][] cache) {
            if (cache[cur][rest] != -1) {
                return cache[cur][rest];
            }
            int ans = 0;
            if (rest == 0) {
                ans = cur == aim ? 1 : 0;
            }
            //剪枝，如果明确到不了了，就不比尝试了
            else if (rest < Math.abs(cur - aim)) {
                ans = 0;
            }
            //左边界
            else if (cur == 1) {
                ans = standard_process(2, rest - 1, aim, N);
            }
            //右边界
            else if (cur == N) {
                ans = standard_process(N - 1, rest - 1, aim, N);
            } else {

                ans = standard_process(cur + 1, rest - 1, aim, N) + standard_process(cur - 1, rest - 1, aim, N);
            }
            cache[cur][rest] = ans;
            return ans;

        }

        private static Integer dpVersion(int cur, int rest, int aim, int N) {
            int[][] dp = new int[N + 1][rest + 1];
            dp[aim][0] = 1;
            for (int r = 1; r <= rest; r++) {
                dp[1][r] = dp[2][r - 1];
                for (int c = 2; c < N; c++) {
                    dp[c][r] = dp[c - 1][r - 1] + dp[c + 1][r - 1];
                }
                dp[N][r] = dp[N - 1][r - 1];
            }
            return dp[cur][rest];
        }

        @Override
        public RobotBestWalkInput prepare() {
            int N = ThreadLocalRandom.current().nextInt(2, 30);
            int M = ThreadLocalRandom.current().nextInt(1, N);
            int P = ThreadLocalRandom.current().nextInt(1, N);
            int K = ThreadLocalRandom.current().nextInt(Math.abs(P - M), 35);
            K = 4;
            N = 5;
            M = 1;
            P = 3;
            

            RobotBestWalkInput input = new RobotBestWalkInput(N, M, P, K);
            return input;
        }

        @Override
        protected Integer standard(RobotBestWalkInput data) {
            if (!check(data.M, data.K, data.P, data.N)) {
                return -1;
            }
            int result = standard_process(data.M, data.K, data.P, data.N);
            log.info("standard结果是:{}", result);
            return result;
        }

        @Override
        protected Integer test(RobotBestWalkInput data) {
            if (!check(data.M, data.K, data.P, data.N)) {
                return -1;
            }
            int[][] dp = new int[data.M + 1][data.K + 1];
            for (int i = 0; i < dp.length; i++) {
                for (int j = 0; j < dp[0].length; j++) {
                    dp[i][j] = -1;
                }
            }
//            int result = standard_process_delbranch(data.M, data.K, data.P, data.N, dp);
            int result = dpVersion(data.M, data.K, data.P, data.N);
            log.info("test结果是:{}", result);
            return result;
        }
    }

    /**
     * 给定一个整型数组arr，代表数值不同的纸牌排成一条线
     * 玩家A和玩家B依次拿走每张纸牌
     * 规定玩家A先拿，玩家B后拿
     * 但是每个玩家每次只能拿走最左或最右的纸牌
     * 玩家A和玩家B都绝顶聪明
     * 请返回最后获胜者的分数。
     */
    @AlgName("扑克博弈")
    public static class SmartestPokerPlayer extends AlgCompImpl<Integer, List<Integer>> {


        @Override
        public List<Integer> prepare() {
            int times = ThreadLocalRandom.current().nextInt(1000, 2000);
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < times; i++) {
                int t = ThreadLocalRandom.current().nextInt(1, times*3);
                while (result.contains(t)) {
                    t = ThreadLocalRandom.current().nextInt(1, times*3);
                }
                result.add(t);
            }
            return result;
        }

        @Override
        protected Integer test(List<Integer> data) {
            if (null == data || 0 == data.size()) {
                return 0;
            }
            return dpVersion(data.stream().mapToInt(Integer::intValue).toArray());
//            int first = first(0, data.size() - 1, data);
//            int second = second(0, data.size() - 1, data);
//            return Math.max(first, second);
        }
        
        private static Integer dpVersion(int[] cache){
            int[][] f_dp=new int[cache.length][cache.length];
            int[][] g_dp=new int[cache.length][cache.length];
            for(int i=0;i < cache.length;i++){
                f_dp[i][i] = cache[i];
            }
            for(int i=1;i<cache.length;i++){
                int L = 0;
                int R = i;
                while (R< cache.length && L< cache.length){
                    f_dp[L][R] = Math.max(cache[L]+g_dp[L+1][R], cache[R]+g_dp[L][R-1]);
                    g_dp[L][R] = Math.min(f_dp[L+1][R],f_dp[L][R-1]);
                    L++;
                    R++;
                }
            }
            return Math.max(f_dp[0][cache.length-1],g_dp[0][cache.length-1]);
        }
        /**
         * 先手。在left right这个区域上，能获得的最好分数是多少
         *
         * @param left
         * @param right
         * @param array
         * @return
         */
        private static Integer first(int left, int right, List<Integer> array) {
            //base case
            if (left == right) {
                return array.get(left);
            }
            int p1 = array.get(left) + second(left + 1, right, array);
            int p2 = array.get(right) + second(left, right - 1, array);
            int v = Math.max(p1, p2);
            return v;

        }

        /**
         * 后手，后手是对方决定的，对方一定给我们最小的
         *
         * @param left
         * @param right
         * @param array
         * @return
         */
        private static Integer second(int left, int right, List<Integer> array) {
            //base case，因为是后手，肯定是对方先走，那么自己只能拿到0。这个地方比较绕
            if (left == right) {
                return 0;
            }
            int p1 = first(left + 1, right, array);//表示对手拿走了left上的数
            int p2 = first(left, right - 1, array);//表示对手拿走了right上的数
            int v = Math.min(p1, p2);//作为后手，没办法，只能挑选最小的那个
            return v;
        }

        @Override
        protected Integer standard(List<Integer> data) {
//            int first = first(0, data.size() - 1, data);
//            int second = second(0, data.size() - 1, data);
//            return Math.max(first, second);
            
            int[][] first_cache=new int[data.size()][data.size()];
            int[][] second_cache=new int[data.size()][data.size()];
            for(int i=0;i < data.size();i++){
                for(int j=0;j<data.size();j++){
                    first_cache[i][j] = -1;
                    second_cache[i][j] = -1;
                }
            }
            int first = first_cache(0,data.size()-1,data,first_cache,second_cache);
            int second = second_cache(0,data.size()-1,data,first_cache,second_cache);
            return Math.max(first,second);
        }

        /**
         * 先手。在left right这个区域上，能获得的最好分数是多少
         *
         * @param left
         * @param right
         * @param array
         * @return
         */
        private static Integer first_cache(int left, int right, List<Integer> array,int[][] firstcache, int[][] secondcache) {
            //base case
            if(firstcache[left][right]!=-1){
                return firstcache[left][right];
            }
            if (left == right) {
                firstcache[left][right] = array.get(left);
                return firstcache[left][right];
            }
            
            int p1 = array.get(left) + second_cache(left + 1, right, array,firstcache,secondcache);;
            int p2 = array.get(right) + second_cache(left, right - 1, array,firstcache,secondcache);
            int v = Math.max(p1, p2);
            firstcache[left][right] = v;
            return firstcache[left][right];

        }

        /**
         * 后手，后手是对方决定的，对方一定给我们最小的
         *
         * @param left
         * @param right
         * @param array
         * @return
         */
        private static Integer second_cache(int left, int right, List<Integer> array,int[][] firstcache, int[][] secondcache) {
            //base case，因为是后手，肯定是对方先走，那么自己只能拿到0。这个地方比较绕
            if(secondcache[left][right] != -1){
                return secondcache[left][right];
            }
            if (left == right) {
                secondcache[left][right] = 0;
                return secondcache[left][right];
            }
            int p1 = first_cache(left + 1, right, array,firstcache,secondcache);//表示对手拿走了left上的数
            int p2 = first_cache(left, right - 1, array,firstcache,secondcache);//表示对手拿走了right上的数
            int v = Math.min(p1,p2);//作为后手，没办法，只能挑选最小的那个
            secondcache[left][right] = v;
            return secondcache[left][right];
        }
    }

    /**
     * 给定两个长度都为N的数组weights和values，
     * weights[i]和values[i]分别代表 i号物品的重量和价值。
     * 给定一个正数bag，表示一个载重bag的袋子，
     * 你装的物品不能超过这个重量。
     * 返回你能装下最多的价值是多少? 
     */
    @Data
    @AllArgsConstructor
    public static class Bag{
        private int[] weights;
        private int[] values;
        private int bagSize;
    }
    @AlgName("背包问题")
    public static class Knapsack extends AlgCompImpl<Long, Bag>{

        @Override
        public Bag prepare() {
            
            //货物列表长度
            int n = ThreadLocalRandom.current().nextInt(1,100);
            //背包承载数，背包中所有weight不大于bagSize
            int bagSize = ThreadLocalRandom.current().nextInt(50,10000);
            //每个货物的重量
            int[] weights = new int[n];
            //每个货物的价值
            int[] values = new int[n];
            for(int i=0;i < n;i++){
                weights[i] = ThreadLocalRandom.current().nextInt(0,bagSize);
                values[i] = ThreadLocalRandom.current().nextInt(0,10000);
            }
            Bag bag = new Bag(weights,values,bagSize);
            Bag testBag = new Bag(new int[]{1,2,3},new int[]{3,4,5},3);
            return bag;
//            return testBag;
        }

        @Override
        protected Long standard(Bag data) {
            return process(data.weights,data.values,0,data.bagSize);
        }
        private static long process(int[] w, int[] v, int index, int bagSize){
            //边界
            if(w == null||v == null||w.length != v.length||w.length<=0||bagSize<0){
                return 0;
            }
            //basecase
            if(bagSize <0){
                return -1;
            }
            //数组越界了
            if(index == w.length){
                return 0;
            }
            //选择不要这个货物
            long p1 = process(w,v,index+1,bagSize);
            long p2 = bagSize<w[index]?0:(v[index]+ process(w,v,index+1,bagSize-w[index]));
            return Math.max(p1,p2);
        }
        @Override
        protected Long test(Bag data) {
            return dpVersion(data.weights, data.values, data.bagSize);
        }
        //index的范围是0~N， restBagSize是bag~0。因为index == w.length的时候是0，因此dp表中最后一行是0
        private static long dpVersion(int[] w, int[] v,  int bagSize){
            if(w == null||v == null||w.length != v.length||w.length<=0||bagSize<0){
                return 0;
            }
            long[][] dp = new long [w.length+1][bagSize+1];
            long lastMax = -1;
            //从下往上走
            for(int index = w.length-1;index>=0;index--){
                for(int rest=0;rest<=bagSize;rest++){
                    long p1 = dp[index+1][rest];
                    long p2 =rest < w[index]? -1:(dp[index+1][rest-w[index]]+v[index]);
                    dp[index][rest] = Math.max(p1,p2);
                    if(lastMax < dp[index][rest]){
                        lastMax = dp[index][rest];
                    }
                }
            }
            return lastMax;
        }
    }
    
    /**
     * 给定3个参数，N，M，K
     * 怪兽有N滴血，等着英雄来砍自己
     * 英雄每一次打击，都会让怪兽流失[0~M]的血量
     * 到底流失多少？每一次在[0~M]上等概率的获得一个值
     * 求K次打击之后，英雄把怪兽砍死的概率
     */
    @AlgName("英雄杀死怪物")
    public static class KillMonster extends AlgCompImpl<Double, int[] >{
        /**
         * 数组的0,1,2分别代表,N,M,K
         * @return
         */
        @Override
        public int[] prepare() {
            return new int[0];
        }

        /**
         * 标准算法
         * @param data
         * @return
         */
        @Override
        protected Double standard(int[] data) {
            return null;
        }

        /**
         * 测试算法
         * @param data
         * @return
         */
        @Override
        protected Double test(int[] data) {
            return null;
        }
    }
}
