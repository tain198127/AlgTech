package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import com.google.common.primitives.Chars;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

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
        AlgCompMenu.addComp(new NumCombine());
        AlgCompMenu.addComp(new CutPaper());
        AlgCompMenu.addComp(new MaxCommonSubsequence());
        AlgCompMenu.addComp(new KillMonster());
        AlgCompMenu.addComp(new LessMoney());
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
                cache.put(data, test(data - 1) + test(data - 2));
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
    @AlgName(value = "机器人巡径数", timeout = 10)
    public static class RobotBestWalk extends AlgCompImpl<Integer, RobotBestWalkInput> {

        private static boolean check(int cur, int rest, int aim, int N) {
            return N >= 2 && cur >= 1 && cur <= N && aim >= 1 && aim <= N && rest >= 1;
        }

        private static Integer standard_process(int cur, int rest, int aim, int N) {
            if (rest == 0) {
                return cur == aim ? 1 : 0;
            }
            if (cur == 1) {
                //到头了，向右走
                return standard_process(cur + 1, rest - 1, aim, N);
            }
            if (cur == N) {
                //到头了，向左走
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


        private static Integer dpVersion(int[] cache) {
            int[][] f_dp = new int[cache.length][cache.length];
            int[][] g_dp = new int[cache.length][cache.length];
            for (int i = 0; i < cache.length; i++) {
                f_dp[i][i] = cache[i];
            }
            for (int i = 1; i < cache.length; i++) {
                int L = 0;
                int R = i;
                while (R < cache.length && L < cache.length) {
                    f_dp[L][R] = Math.max(cache[L] + g_dp[L + 1][R], cache[R] + g_dp[L][R - 1]);
                    g_dp[L][R] = Math.min(f_dp[L + 1][R], f_dp[L][R - 1]);
                    L++;
                    R++;
                }
            }
            return Math.max(f_dp[0][cache.length - 1], g_dp[0][cache.length - 1]);
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

        /**
         * 先手。在left right这个区域上，能获得的最好分数是多少
         *
         * @param left
         * @param right
         * @param array
         * @return
         */
        private static Integer first_cache(int left, int right, List<Integer> array, int[][] firstcache, int[][] secondcache) {
            //base case
            if (firstcache[left][right] != -1) {
                return firstcache[left][right];
            }
            if (left == right) {
                firstcache[left][right] = array.get(left);
                return firstcache[left][right];
            }

            int p1 = array.get(left) + second_cache(left + 1, right, array, firstcache, secondcache);
            int p2 = array.get(right) + second_cache(left, right - 1, array, firstcache, secondcache);
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
        private static Integer second_cache(int left, int right, List<Integer> array, int[][] firstcache, int[][] secondcache) {
            //base case，因为是后手，肯定是对方先走，那么自己只能拿到0。这个地方比较绕
            if (secondcache[left][right] != -1) {
                return secondcache[left][right];
            }
            if (left == right) {
                secondcache[left][right] = 0;
                return secondcache[left][right];
            }
            int p1 = first_cache(left + 1, right, array, firstcache, secondcache);//表示对手拿走了left上的数
            int p2 = first_cache(left, right - 1, array, firstcache, secondcache);//表示对手拿走了right上的数
            int v = Math.min(p1, p2);//作为后手，没办法，只能挑选最小的那个
            secondcache[left][right] = v;
            return secondcache[left][right];
        }

        @Override
        public List<Integer> prepare() {
            int times = ThreadLocalRandom.current().nextInt(1000, 2000);
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < times; i++) {
                int t = ThreadLocalRandom.current().nextInt(1, times * 3);
                while (result.contains(t)) {
                    t = ThreadLocalRandom.current().nextInt(1, times * 3);
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

        @Override
        protected Integer standard(List<Integer> data) {
//            int first = first(0, data.size() - 1, data);
//            int second = second(0, data.size() - 1, data);
//            return Math.max(first, second);

            int[][] first_cache = new int[data.size()][data.size()];
            int[][] second_cache = new int[data.size()][data.size()];
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < data.size(); j++) {
                    first_cache[i][j] = -1;
                    second_cache[i][j] = -1;
                }
            }
            int first = first_cache(0, data.size() - 1, data, first_cache, second_cache);
            int second = second_cache(0, data.size() - 1, data, first_cache, second_cache);
            return Math.max(first, second);
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
    public static class Bag {
        private int[] weights;
        private int[] values;
        private int bagSize;
    }

    @AlgName("背包问题")
    public static class Knapsack extends AlgCompImpl<Long, Bag> {

        private static long process(int[] w, int[] v, int index, int bagSize) {
            //边界
            if (w == null || v == null || w.length != v.length || w.length <= 0 || bagSize < 0) {
                return 0;
            }
            //basecase
            if (bagSize < 0) {
                return -1;
            }
            //数组越界了
            if (index == w.length) {
                return 0;
            }
            //选择不要这个货物
            long p1 = process(w, v, index + 1, bagSize);
            long p2 = bagSize < w[index] ? 0 : (v[index] + process(w, v, index + 1, bagSize - w[index]));
            return Math.max(p1, p2);
        }

        //index的范围是0~N， restBagSize是bag~0。因为index == w.length的时候是0，因此dp表中最后一行是0
        private static long dpVersion(int[] w, int[] v, int bagSize) {
            if (w == null || v == null || w.length != v.length || w.length <= 0 || bagSize < 0) {
                return 0;
            }
            long[][] dp = new long[w.length + 1][bagSize + 1];
            long lastMax = -1;
            //从下往上走
            for (int index = w.length - 1; index >= 0; index--) {
                for (int rest = 0; rest <= bagSize; rest++) {
                    long p1 = dp[index + 1][rest];
                    long p2 = rest < w[index] ? -1 : (dp[index + 1][rest - w[index]] + v[index]);
                    dp[index][rest] = Math.max(p1, p2);
                    if (lastMax < dp[index][rest]) {
                        lastMax = dp[index][rest];
                    }
                }
            }
            return lastMax;
        }

        @Override
        public Bag prepare() {

            //货物列表长度
            int n = ThreadLocalRandom.current().nextInt(1, 100);
            //背包承载数，背包中所有weight不大于bagSize
            int bagSize = ThreadLocalRandom.current().nextInt(50, 10000);
            //每个货物的重量
            int[] weights = new int[n];
            //每个货物的价值
            int[] values = new int[n];
            for (int i = 0; i < n; i++) {
                weights[i] = ThreadLocalRandom.current().nextInt(0, bagSize);
                values[i] = ThreadLocalRandom.current().nextInt(0, 10000);
            }
            Bag bag = new Bag(weights, values, bagSize);
            Bag testBag = new Bag(new int[]{1, 2, 3}, new int[]{3, 4, 5}, 3);
            return bag;
//            return testBag;
        }

        @Override
        protected Long standard(Bag data) {
            return process(data.weights, data.values, 0, data.bagSize);
        }

        @Override
        protected Long test(Bag data) {
            return dpVersion(data.weights, data.values, data.bagSize);
        }
    }

    /**
     * 规定1和A对应、2和B对应、3和C对应...26和Z对应
     * 那么一个数字字符串比如"111”就可以转化为:
     * "AAA"、"KA"和"AK"
     * 给定一个只有数字字符组成的字符串str，返回有多少种转化结果。
     * 例如769,只能转化为['GFI']，那就说明只有一种方法。
     * 再比如305，没有办法转化，因为中间的0没法处理。如果转化成 [3,05]，不对。转化成[30,5]也不对，转化成[3,0,5]也不对
     * 再比如123，可以分成[1,2,3][12,3][1,23],说明有3中分法
     */
    @AlgName(value = "数字转化字符", timeout = -1, times = 5)
    public static class NumCombine extends AlgCompImpl<Long, String> {

        private static HashMap<String, String> map = new HashMap<>();
        private static HashMap<String, AtomicInteger> numCount = new HashMap<>();
        private static HashMap<Integer, Long> cache = new HashMap<>();

        static {
            for (int i = 1; i <= 26; i++) {
                map.put(String.valueOf(i), String.valueOf('a' + i - 1));
                numCount.put(String.valueOf(i), new AtomicInteger(0));
            }
        }

        private static long myProcess(char[] ary, int index) {
            if (index == ary.length) {
                return 1;
            }
            if (ary[index] == '0') {
                return 0;
            }
            long ways = myProcess(ary, index + 1);
            //判断是否越界
            if (index + 1 < ary.length && ((ary[index] - '0') * 10 + (ary[index] - '0')) < 26) {
                ways += myProcess(ary, index + 2);
            }
            return ways;


        }

        private static long processWithDP(char[] ary) {
            int N = ary.length;
            int[] dp = new int[N + 1];
            dp[N] = 1;
            for (int i = N - 1; i >= 0; i--) {
                if (ary[i] == '0') {
                    dp[i] = 0;
                } else {
                    int ways = dp[i + 1];
                    if (i + 1 < ary.length && ((ary[i] - '0') * 10 + (ary[i] - '0')) < 26) {
                        ways += dp[i + 2];
                    }
                    dp[i] = ways;
                }
            }
            return dp[0];

        }

        @Override
        public String prepare() {
            StringBuilder stringBuilder = new StringBuilder();
            int len = ThreadLocalRandom.current().nextInt(10000, 10000000);
            for (int i = 0; i < len; i++) {
                long val = ThreadLocalRandom.current().nextLong(1000000, 1000000000000L);
                stringBuilder.append(val);
            }

            return stringBuilder.toString();
        }

        @Override
        protected Long standard(String data) {
            long ways = myProcess(data.toCharArray(), 0);
            return ways;
        }

        @Override
        protected Long test(String data) {
            return processWithDP(data.toCharArray());
        }
    }

    @Data
    @AllArgsConstructor
    public static class CutPaperData {
        private String paper;
        private String[] array;
    }

    /**
     * 给定一个字符串str，给定一个字符串类型的数组arr，出现的字符都是小写英文
     * arr每一个字符串，代表一张贴纸，你可以把单个字符剪开使用，目的是拼出str来
     * 返回需要至少多少张贴纸可以完成这个任务。
     * 例子：str= "babac"，arr = {"ba","c","abcd"}
     * ba + ba + c  3  abcd + abcd 2  abcd+ba 2
     * 所以返回2
     */
    @AlgName("切纸片游戏")
    public static class CutPaper extends AlgCompImpl<Integer, CutPaperData> {
        public static List<Character> base = new ArrayList<>();

        static {
            for (int i = 'a'; i < 'z'; i++) {
                base.add((char) i);
            }
        }

        public static String randomStr(int len, List<Character> baseChar) {

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < len; i++) {
                int idx = ThreadLocalRandom.current().nextInt(0, 100000) % baseChar.size();
                stringBuilder.append(baseChar.get(idx));
            }
            return stringBuilder.toString();
        }

        private static String minus(String target, String stick) {

            char[] targetChar = target.toCharArray();
            char[] stickChar = stick.toCharArray();

            StringBuilder restBuilder = new StringBuilder();

            int[] count = new int[26];
            for (char t : targetChar) {
                count[t - 'a']++;
            }
            for (char s : stickChar) {
                count[s - 'a']--;
            }
            List<Character> restList = new ArrayList<>();
            for (int i = 0; i < 26; i++) {
                if (count[i] > 0) {
                    for (int j = 0; j < count[i]; j++) {
                        restList.add((char) (i + 'a'));
                    }
                }
            }
            restList.sort(Character::compareTo);
            restList.stream().forEach(c -> restBuilder.append(c));

            return restBuilder.toString();
        }

        /**
         * 变成词频数组
         *
         * @param msg
         * @return
         */
        private static int[] stringToCharTimes(String msg) {
            int[] charsTimes = new int[26];//因为只有26个字母
            char[] chars = msg.toCharArray();
            for (char c : chars) {
                charsTimes[c - 'a']++;
            }
            return charsTimes;
        }

        private static Integer process_cut_branch(String targetStr, int[][] sticks) {
            int N = sticks.length;
            char[] target = targetStr.toCharArray();
            if (target.length == 0) {
                return 0;
            }
            int[] tcount = stringToCharTimes(targetStr);
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < N; i++) {
                int[] row = sticks[i];
                if (row[target[0] - 'a'] > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < 26; j++) {
                        if (tcount[j] > 0) {
                            int num = tcount[j] - row[j];
                            for (int k = 0; k < num; k++) {
                                stringBuilder.append((char) (j + 'a'));
                            }
                        }
                    }

                    min = Math.min(min, process_cut_branch(stringBuilder.toString(), sticks));
                }//end if
            }

            int result = min + (min == Integer.MAX_VALUE ? 0 : 1);
            return result;

        }

        private static Integer process_cut_branch_and_cache(String targetStr, int[][] sticks, Map<String, Integer> cache) {
            if (cache.containsKey(targetStr)) {
                return cache.get(targetStr);
            }
            int N = sticks.length;
            char[] target = targetStr.toCharArray();
            if (target.length == 0) {
                return 0;
            }
            int[] tcount = stringToCharTimes(targetStr);
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < N; i++) {
                int[] row = sticks[i];
                if (row[target[0] - 'a'] > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < 26; j++) {
                        if (tcount[j] > 0) {
                            int num = tcount[j] - row[j];
                            for (int k = 0; k < num; k++) {
                                stringBuilder.append((char) (j + 'a'));
                            }
                        }
                    }

                    min = Math.min(min, process_cut_branch_and_cache(stringBuilder.toString(), sticks, cache));
                }//end if
            }

            int result = min + (min == Integer.MAX_VALUE ? 0 : 1);
            cache.put(targetStr, result);
            return result;

        }

        @Override
        public CutPaperData prepare() {
            int sticks = ThreadLocalRandom.current().nextInt(2, 30);

            String paper = randomStr(ThreadLocalRandom.current().nextInt(10, 30), base);
            String[] array = new String[sticks];
            //生成可解的准备数据
            if (ThreadLocalRandom.current().nextBoolean()) {
                for (int i = 0; i < sticks; i++) {
                    array[i] = randomStr(ThreadLocalRandom.current().nextInt(10, 30), Chars.asList(paper.toCharArray()));
                }
            } else {
                for (int i = 0; i < sticks; i++) {
                    array[i] = randomStr(ThreadLocalRandom.current().nextInt(10, 30), base);
                }
            }
            CutPaperData cutPaperData = new CutPaperData(paper, array);
//            CutPaperData cutPaperData = new CutPaperData("yxsm",new String[]{"ym","s","xx"});
//            CutPaperData cutPaperData = new CutPaperData("a",new String[]{"a"});
            return cutPaperData;
        }

        @Override
        protected Integer standard(CutPaperData data) {
            //第一版，很慢
//            return firstVersion(data);
            int[][] sticks = new int[data.array.length][26];
            for (int i = 0; i < sticks.length; i++) {
                int[] ary = stringToCharTimes(data.array[i]);
                for (int j = 0; j < ary.length; j++) {
                    sticks[i][j] = ary[j];
                }
            }
            Map<String, Integer> dp = new HashMap<>();
            dp.put("", 0);
            return process_cut_branch_and_cache(data.paper, sticks, dp);
        }

        private int firstVersion(CutPaperData data) {
            if (data.paper.length() == 0) {
                return 0;
            }
            int min = Integer.MAX_VALUE;
            for (String first : data.array) {
                String rest = minus(data.paper, first);
                if (rest.length() != data.paper.length()) {//能减掉一点点
                    min = Math.min(min, standard(new CutPaperData(rest, data.array)));
                }
            }
            return min + (min == Integer.MAX_VALUE ? 0 : 1);
        }

        @Override
        protected Integer test(CutPaperData data) {
            int[][] sticks = new int[data.array.length][26];
            for (int i = 0; i < sticks.length; i++) {
                int[] ary = stringToCharTimes(data.array[i]);
                for (int j = 0; j < ary.length; j++) {
                    sticks[i][j] = ary[j];
                }
            }
            int times = process_cut_branch(data.paper, sticks);
            log.info("测试结果是:{}", times);
            return times;
        }

    }

    /**
     * 给定两个字符串str1和str2，
     * 返回这两个字符串的最长公共子序列长度
     * <p>
     * 比如 ： str1 = “a12b3c456d”,str2 = “1ef23ghi4j56k”
     * 最长公共子序列是“123456”，所以返回长度6
     */
    @AlgName(value = "最长公共子序列", timeout = 10)
    public static class MaxCommonSubsequence extends AlgCompImpl<Integer, String[]> {

        private static int process1(char[] str1, char[] str2, int i, int j) {
            if (i == 0 && j == 0) {
                return str1[i] == str2[j] ? 1 : 0;
            } else if (i == 0) {
                if (str1[i] == str2[j]) {
                    return 1;
                } else {
                    return process1(str1, str2, i, j - 1);
                }
            } else if (j == 0) {
                if (str1[i] == str2[j]) {
                    return 1;
                } else {
                    return process1(str1, str2, i - 1, j);
                }
            } else {
                int p1 = process1(str1, str2, i - 1, j);
                int p2 = process1(str1, str2, i, j - 1);
                int p3 = str1[i] == str2[j] ? (1 + process1(str1, str2, i - 1, j - 1)) : 0;
                return Math.max(p1, Math.max(p2, p3));
            }
        }

        private static int dp(char[] str1, char[] str2) {
            int[][] dp = new int[str1.length][str2.length];
            dp[0][0] = str1[0] == str2[0] ? 1 : 0;
            for (int i = 1; i < str1.length; i++) {
                dp[i][0] += str1[i] == str2[0] ? 1 : dp[i - 1][0];
            }
            for (int j = 1; j < str2.length; j++) {
                dp[0][j] += str1[0] == str2[j] ? 1 : dp[0][j - 1];
            }
            for (int i = 1; i < str1.length; i++) {
                for (int j = 1; j < str2.length; j++) {
                    int p1 = dp[i - 1][j];
                    int p2 = dp[i][j - 1];
                    int p3 = str1[i] == str2[j] ? (1 + dp[i - 1][j - 1]) : 0;
                    int max = Math.max(p1, Math.max(p2, p3));
                    dp[i][j] = max;
                }
            }
            return dp[str1.length - 1][str2.length - 1];

        }

        @Override
        public String[] prepare() {
            String[] arr = new String[2];
            arr[0] = CutPaper.randomStr(15, CutPaper.base);
            arr[1] = CutPaper.randomStr(15, CutPaper.base);
            return arr;
        }

        @Override
        protected Integer standard(String[] data) {
            return process1(data[0].toCharArray(), data[1].toCharArray(), data[0].length() - 1, data[1].length() - 1);
        }

        @Override
        protected Integer test(String[] data) {
            return dp(data[0].toCharArray(), data[1].toCharArray());
        }
    }

    /**
     * 给定3个参数，N，M，K
     * 怪兽有N滴血，等着英雄来砍自己
     * 英雄每一次打击，都会让怪兽流失[0~M]的血量
     * 到底流失多少？每一次在[0~M]上等概率的获得一个值
     * 求K次打击之后，英雄把怪兽砍死的概率
     * 分析过程，第一次砍，可能掉0-M点血，第二次砍是在第一次砍的基础上，又进行了0-M点血。那么总次数就是(M+1)^k.这么多次组合
     * 这是一个K层，每层M棵分支的完全展开树。只要收集所有节点上总数大于N的节点数。就可以知道跟总次数的比例了。比如收集了所有砍死
     * 怪兽的点的次数为ALL，总情况数是(M+1)^k，那么比例就是ALL/(M+1)^k.在砍的过程中，如果怪兽死了，不剪枝，继续走。走完所有的树
     */
    @AlgName("英雄杀死怪物")
    public static class KillMonster extends AlgCompImpl<Double, int[]> {
        /**
         * 数组的0,1,2分别代表,N,M,K
         *
         * @return
         */
        @Override
        public int[] prepare() {
            int[] ary = new int[3];
            //N
            ary[0] = 10;
            //M
            ary[1] = 5;
            //K
            ary[2] = 10;
            return ary;
        }

        /**
         * 标准算法
         *
         * @param data
         * @return
         */
        @Override
        protected Double standard(int[] data) {
            int N = data[0];
            int M = data[1];
            int K = data[2];
            if (N < 1 || M < 1 || K < 1) {
                return 0D;
            }
//            long ways =process(K,M,N);

            long ways = process2(N, M, K);

            double all = Math.pow((M + 1), K);
            return Double.valueOf(ways / all);

        }

        private long process2(int hp, int m, int times) {
            if (times == 0) {
                return hp <= 0 ? 1 : 0;
            }
            if (hp <= 0) {
                return (long) Math.pow(m + 1, times);
            }
            long ways = 0;
            for (int i = 0; i <= m; i++) {
                ways += process2(hp - i, m, times - 1);
            }
            return ways;
        }

        /**
         * 测试算法
         *
         * @param data
         * @return
         */
        @Override
        protected Double test(int[] data) {
            int N = data[0];
            int M = data[1];
            int K = data[2];
            if (N < 1 || M < 1 || K < 1) {
                return 0D;
            }
            long ways = dp1(N, M, K);
            double all = Math.pow((M + 1), K);
            return Double.valueOf(ways / all);
        }

        private long dp1(int N, int M, int K) {
            long[][] dp = new long[K + 1][N + 1];
            dp[0][0] = 1;
            for (int times = 1; times <= K; times++) {
                dp[times][0] = (long) Math.pow(M + 1, times);
                for (int hp = 1; hp <= N; hp++) {
                    long ways = 0;
                    for (int i = 0; i <= M; i++) {
                        if (hp - i >= 0) {
                            ways += dp[times - 1][hp - i];
                        } else {
                            ways += Math.pow(M + 1, times - 1);
                        }
                    }//end for
                    dp[times][hp] = ways;
                }//end for
            }//end for
            return dp[K][N];

        }//end pd1

    }

    @Data
    public static class LessMoneyInput {
        private int[] arr;
        private int aim;
    }

    /**
     * arr为数组，其中的值都是正整数，且没有重复的。给定一个正整数aim，每个值都是一种面值，张数无限，返回组成aim的最小货币数
     */
    @AlgName("最小面值")
    public static class LessMoney extends AlgCompImpl<Integer, LessMoneyInput> {


        private static int process(int[] ary, int index, int rest) {
            if (rest < 0) {//钱被扣完了
                return Integer.MAX_VALUE;
            }
            //正好所有的货币都被尝试完了
            if (index == ary.length) {
                //如果钱也正好被扣完，那就最好。否则就是无效解
                return rest == 0 ? 0 : Integer.MAX_VALUE;
            } else {
                int ans = Integer.MAX_VALUE;
                //穷举每张货币，从0张开始，一直到货币面值*张数<=剩余金额；每次加一张
                for (int zhang = 0; zhang * ary[index] <= rest; zhang++) {
                    //对下一个面值尝试。但是尝试时，要把当前已经使用过的金额扣除。
                    int next = process(ary, index + 1, rest - zhang * ary[index]);
                    if (next != Integer.MAX_VALUE) {
                        //把最小的结果记录到ans中。next+zhang是精髓，next是下一面值；再加上当前张数和已经记录的ans进行比较
                        ans = Math.min(ans, next + zhang);
                    }
                }
                return ans;
            }
        }

        private static int dpOld(int[] ary, int aim) {
            int N = ary.length;
            int[][] dp = new int[N + 1][aim + 1];
            dp[N][0] = 0;
            for (int i = 1; i <= aim; i++) {
                dp[N][i] = Integer.MAX_VALUE;
            }
            for (int idx = N - 1; idx >= 0; idx--) {
                for (int rest = 0; rest <= aim; rest++) {
                    int ans = Integer.MAX_VALUE;
                    for (int zhang = 0; zhang * ary[idx] <= rest; zhang++) {
                        //对下一个面值尝试。但是尝试时，要把当前已经使用过的金额扣除。
                        int next = dp[idx + 1][rest - zhang * ary[idx]];
                        if (next != Integer.MAX_VALUE) {
                            //把最小的结果记录到ans中。next+zhang是精髓，next是下一面值；再加上当前张数和已经记录的ans进行比较
                            ans = Math.min(ans, next + zhang);
                        }
                    }
                    dp[idx][rest] = ans;
                }
            }
            return dp[0][aim];
        }

        //做了斜率优化的DP
        private static int dp(int[] ary, int aim) {
            int N = ary.length;
            int[][] dp = new int[N + 1][aim + 1];
            dp[N][0] = 0;
            for (int i = 1; i <= aim; i++) {
                dp[N][i] = Integer.MAX_VALUE;
            }
            for (int idx = N - 1; idx >= 0; idx--) {
                for (int rest = 0; rest <= aim; rest++) {
                    dp[idx][rest] = dp[idx + 1][rest];
                    if (rest - ary[idx] >= 0 && dp[idx][rest - ary[idx]] != Integer.MAX_VALUE) {
                        dp[idx][rest] = Math.min(dp[idx][rest], dp[idx][rest - ary[idx]] + 1);
                    }

                }

            }
            return dp[0][aim];
        }//end fn

        @Override
        public LessMoneyInput prepare() {
            LessMoneyInput input = new LessMoneyInput();
            int len = ThreadLocalRandom.current().nextInt(3, 20);
            List<Integer> ary = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                while (true) {
                    int v = ThreadLocalRandom.current().nextInt(1, 1000);
                    if (ary.contains(v)) {
                        continue;
                    }
                    ary.add(v);
                    break;
                }
            }
            input.arr = ary.stream().mapToInt(Integer::intValue).toArray();
            input.aim = ThreadLocalRandom.current().nextInt(1,100000);
            return input;
        }

        @Override
        protected Integer standard(LessMoneyInput data) {
            return process(data.arr, 0, data.aim);
        }

        @Override
        protected Integer test(LessMoneyInput data) {
            return dpOld(data.arr, data.aim);
        }
    }
}
