package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
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
    @AlgName("机器人巡径数")
    public static class RobotBestWalk extends AlgCompImpl<Integer, RobotBestWalkInput> {

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

        @Override
        public RobotBestWalkInput prepare() {
            int N = ThreadLocalRandom.current().nextInt(2, 30);
            int M = ThreadLocalRandom.current().nextInt(0, N);
            int P = ThreadLocalRandom.current().nextInt(0, N);
            int K = ThreadLocalRandom.current().nextInt(Math.abs(P - M), N);

//            N = 4;
//            M = 2;
//            P = 4;
//            K = 4;

            RobotBestWalkInput input = new RobotBestWalkInput(N, M, P, K);
            return input;
        }

        @Override
        protected Integer standard(RobotBestWalkInput data) {

            int result = standard_process(data.M, data.K, data.P, data.N);
            log.info("standard结果是:{}", result);
            return result;
        }

        @Override
        protected Integer test(RobotBestWalkInput data) {
            int[][] dp = new int[data.M + 1][data.K + 1];
            for (int i = 0; i < dp.length; i++) {
                for (int j = 0; j < dp[0].length; j++) {
                    dp[i][j] = -1;
                }
            }
            int result = standard_process_delbranch(data.M, data.K, data.P, data.N, dp);
            log.info("test结果是:{}", result);
            return result;
        }
    }
}
