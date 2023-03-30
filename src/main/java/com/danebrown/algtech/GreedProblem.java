package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompContext;
import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by danebrown on 2022/3/3
 * mail: tain198127@163.com
 * 贪心算法
 *
 * @author danebrown
 */
@Log4j2
public class GreedProblem {
    private static volatile boolean isShutdown = false;
    private static volatile Queue<String> ioQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {

        AlgCompMenu.addComp(new MinDic());
        AlgCompMenu.addComp(new MeetingRoom());
        AlgCompMenu.addComp(new LightProb());
        AlgCompMenu.run();
    }

    /**
     * 给一个由字符串给定的数组strs
     * 必须把所有字符串拼起来
     * 返回所有可能的拼接结果中，字典序最小的结果
     * 什么是字典序：就是java里面字符串排大小的方式
     * 字典序比大小的逻辑：
     * 1. 两个字符串一样长时，把字符串理解为26/255/6/6535进制的数字。单纯的比两个"数字"的大小
     * 2. 两个字符串不一样长时：把短的右补齐跟长的一样长。补的那些字符是最最小的ascii码。然后按照规则1进行比较
     * <p>
     * 有几种贪心算法：
     * 1. 直接对比两个字符串的字典序，谁小谁在前面。会导致bba，bab问题
     * 2. 两个字符串A,B。A.B > B.A？比较拼接后的大小。谁小谁在前面
     * <p>
     * 重点是看明白问题的本质！
     * 贪心算法反例：bab和 bba问题
     * ["b","ba"]
     * b的字典序比ba小。如果用贪心，找到每个最小的字典序，然后排序。组成的结果是bba。
     * 但是其实最小的字典序应该是 bab。
     * <p>
     * 为了解决这个问题，可以使用如下算法：
     * a拼接b < b拼接a？ true则 a放前面b放后面。否则 b放前面 a放后面。
     * 即 a.b < b.a 则表明的证明过程十分重要！
     * 证明过程
     * 1)a.b <= b.a
     * 2)b.c <= c.b
     * 能否推出 ===>a.c <=c.a
     * 这可以证明传递性！！！
     * 在这里数学的表达式   应该是 a.b 其中.表示拼接。那么假如a,b是K进制的。
     * 则a.b 为 a * Klen(b) + b。 即 a✖️ K进制的b的长度的阶。在加上b。相当于把a放到高位去了。
     * 例如 "123"."456"变成了"123456"，相当于"123" * 103+"456"。
     * 我可以把所有字符串都当做非负的数字，那么，所有字符串拼接的本质都可以用上述的公式来表达
     * a∗K^len(b) +b
     * <p>
     * 其中a可以是任意数字、字母字符(都可以转化为N进制的数字)。k表示a，b的进制。
     * 上述过程，非常复杂，涉及到数学抽象，交换律，公式证明。太复杂了。
     * 因此不要通过这种方式，去证明贪心。即便不证明也可以用贪心。
     * <p>
     * 对于贪心算法，别用数学证明，因为没有套路；用对数器的方式，暴力证明。用实验的方式。然后，再说数学证明的方式。
     * <p>
     * <p>
     * 贪心算法，本身应该很简单。要么用排序，要么用堆。本质上是用一套策略-->一个标准来排序。所以，贪心的成功与否，在于那个标准定的准不准。
     */
    @AlgName("最小字典序")
    public static class MinDic extends AlgCompImpl<String, String[]> {
        List<Character> metas = new ArrayList<>();

        public MinDic() {
            for (int i = 'a'; i <= 'z'; i++) {
                metas.add(Character.valueOf((char) i));
            }
            for (int i = 'A'; i <= 'Z'; i++) {
                metas.add(Character.valueOf((char) i));
            }
        }

        // strs中所有字符串全排列，返回所有可能的结果
        //treeset自然会把字典序最小的方上面
        public static TreeSet<String> process(String[] strs) {
            TreeSet<String> ans = new TreeSet<>();
            if (strs.length == 0) {
                ans.add("");
                return ans;
            }
            for (int i = 0; i < strs.length; i++) {
                String first = strs[i];
                String[] nexts = removeIndexString(strs, i);
                TreeSet<String> next = process(nexts);
                for (String cur : next) {
                    ans.add(first + cur);
                }
            }
            return ans;
        }

        // {"abc", "cks", "bct"}
        // 0 1 2
        // removeIndexString(arr , 1) -> {"abc", "bct"}
        public static String[] removeIndexString(String[] arr, int index) {
            int N = arr.length;
            String[] ans = new String[N - 1];
            int ansIndex = 0;
            for (int i = 0; i < N; i++) {
                if (i != index) {
                    ans[ansIndex++] = arr[i];
                }
            }
            return ans;
        }

        @Override
        public String[] prepare(AlgCompContext context) {
            int times = ThreadLocalRandom.current().nextInt(1, 10);
            List<String> result = new ArrayList<>();
            for (int i = 0; i < times; i++) {
                String tmp = "";
                int len = ThreadLocalRandom.current().nextInt(1, 100);


                for (int j = 0; j < len; j++) {
                    int ascii = ThreadLocalRandom.current().nextInt(0, metas.size());
                    tmp += metas.get(ascii);
                }
                result.add(tmp);
            }
            return result.toArray(new String[]{});
        }

        @Override
        protected String standard(String[] strs) {
            if (strs == null || strs.length == 0) {
                return "";
            }
            TreeSet<String> ans = process(strs);
            return ans.size() == 0 ? "" : ans.first();

        }

        /**
         * 贪心算法，重点在于理解问题的本质。然后定一个规则。
         *
         * @param data
         * @return
         */
        @Override
        protected String test(String[] data) {
            if (data == null || data.length == 0) {
                return "";
            }
            Arrays.sort(data, new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return (a + b).compareTo(b + a);
                }
            });
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                str.append(data[i]);
            }
            return str.toString();
        }
    }

    /**
     * 项目
     */
    @Data
    @AllArgsConstructor
    public static class Project {
        private int begin;
        private int end;
    }

    /**
     * 一些项目要占用一个会议室宣讲，会议室不能同时容纳两个项目的宣讲。
     * 给你每一个项目开始的时间和结束的时间
     * 你来安排宣讲的日程，要求会议室进行的宣讲的场次最多。
     * 返回最多的宣讲场次。
     * 思路：按照最晚结束时间排序。然后按照开始时间比较。
     */
    @AlgName("项目排程")
    public static class MeetingRoom extends AlgCompImpl<Integer, List<Project>> {

        public static Project[] generatePrograms(int programSize, int timeMax) {
            Project[] ans = new Project[(int) (Math.random() * (programSize + 1))];
            for (int i = 0; i < ans.length; i++) {
                int r1 = (int) (Math.random() * (timeMax + 1));
                int r2 = (int) (Math.random() * (timeMax + 1));
                if (r1 == r2) {
                    ans[i] = new Project(r1, r1 + 1);
                } else {
                    ans[i] = new Project(Math.min(r1, r2), Math.max(r1, r2));
                }
            }
            return ans;
        }

        public static int bestArrange1(Project[] programs) {
            if (programs == null || programs.length == 0) {
                return 0;
            }
            return process(programs, 0, 0);
        }

        // 目前来到timeLine的时间点，已经安排了done多的会议，剩下的会议programs可以自由安排
        // 返回能安排的最多会议数量
        public static int process(Project[] programs, int done, int timeLine) {
            if (programs.length == 0) {
                return done;
            }
            // 还剩下会议
            int max = done;
            // 当前安排的会议是什么会，每一个都枚举
            for (int i = 0; i < programs.length; i++) {
                if (programs[i].begin >= timeLine) {
                    Project[] next = copyButExcept(programs, i);
                    max = Math.max(max, process(next, done + 1, programs[i].end));
                }
            }
            return max;
        }

        public static Project[] copyButExcept(Project[] programs, int i) {
            Project[] ans = new Project[programs.length - 1];
            int index = 0;
            for (int k = 0; k < programs.length; k++) {
                if (k != i) {
                    ans[index++] = programs[k];
                }
            }
            return ans;
        }
        // 还剩下的会议都放在programs里
        // done之前已经安排了多少会议的数量
        // timeLine目前来到的时间点是什么

        @Override
        public List<Project> prepare(AlgCompContext context) {
            return Arrays.stream(generatePrograms(100, 100000)).collect(Collectors.toList());
        }

        @Override
        protected Integer standard(List<Project> data) {
            return bestArrange1(data.toArray(new Project[]{}));
        }

        /**
         * 以结束时间进行排序。谁的结束时间早，谁在前面。
         * 然后以会议开始的时间，对比最后一个会议的结束时间。如果
         * 下一个会议的开始时间比 lastTimeLine（最后一个会议结束时间）晚，那么
         * 就把会议数加一。并且跟新lastTimeLine
         * @param data
         * @return
         */
        @Override
        protected Integer test(List<Project> data) {
            data.sort(new Comparator<Project>() {
                @Override
                public int compare(Project o1, Project o2) {
                    return o1.end - o2.end;
                }
            });
            int result = 0;
            int lastTimeLine = 0;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).begin >= lastTimeLine) {
                    result++;
                    lastTimeLine = data.get(i).end;
                }


            }
            return result;
        }
    }

    /**
     * 投资汇报对象
     */
    @Data
    public static class Portfolio {
        /**
         * 成本
         */
        private long cost;
        /**
         * 利润，注意，这个是利润，不是收入。因此只要成本能cover的住，利润就是纯利。这里面已经
         * 把成本算过了
         */
        private long profit;

    }

    /**
     * 输入: 正数数组costs、正数数组profits、正数K、正数M
     * costs[i]表示i号项目的花费
     * profits[i]表示i号项目在扣除花费之后还能挣到的钱(利润)
     * K表示你只能串行的最多做k个项目
     * M表示你初始的资金
     * 说明: 每做完一个项目，马上获得的收益，可以支持你去做下一个项目。不能并行的做项目。
     * 输出：你最后获得的最大钱数。
     *
     * 思路：按照cost正排（从小到大），再按照利润倒排（从大到小）
     *
     * 具体实现：建立一个按照cost建立的小根堆。再建立一个按照利润组织的大根堆。大根堆里一开始是空的。把所有项目都扔到小根堆里去
     * 步骤1：M，把小根堆中能做的项目，弹出来放到大根堆里（只要cost小于M，就弹出。不用管加一起是否超过）。
     * 步骤2：然后选择大根堆里堆顶的元素的利润。（K次数减一）
     * 步骤3：更新M的初始资金，重复步骤1，2
     * 步骤1,2,3完成一次，K次数减一。直至K次数减完
     *
     * Triple<Integer,Integer,List<Portfolio>>
     *     第一个int是原始资金
     *     第二个int是次数
     */
    public static class MaxProfilo extends AlgCompImpl<Long, Triple<Integer,Integer,List<Portfolio>>>{

        @Override
        public Triple<Integer,Integer,List<Portfolio>> prepare(AlgCompContext context) {
            return null;
        }

        @Override
        protected Long standard(Triple<Integer, Integer, List<Portfolio>> data) {
            return null;
        }

        @Override
        protected Long test(Triple<Integer, Integer, List<Portfolio>> data) {
            PriorityQueue<Portfolio> minCost = new PriorityQueue<>((t0, t1) -> (int) (t0.getCost() - t1.getCost()));
            PriorityQueue<Portfolio> maxProfit =
                    new PriorityQueue<>((t0,t1)->(int)(t1.getProfit() - t0.getProfit()));
            minCost.addAll(data.getRight());
            int M = data.getLeft();
            int K = data.getMiddle();
            for(int i=0;i < K;i++){
                while (!minCost.isEmpty() && minCost.peek().getCost()<=M){
                    //选出一堆可以投资的项目，放到大根堆
                    maxProfit.add(minCost.poll());
                }
                if(maxProfit.isEmpty()){
                    //这里可能是一个合适的项目都没有，或者说已经都消费完了。
                    return Long.valueOf(M);
                }
                //每次从大根堆拿一个出来，把M修改一下
                M += maxProfit.poll().profit;
            }

            return null;
        }


    }

    /**
     * 给定一个字符串str，只由‘X’和‘.’两种字符构成。
     * ‘X’表示墙，不能放灯，也不需要点亮
     * ‘.’表示居民点，可以放灯，需要点亮
     * 如果灯放在i位置，可以让i-1，i和i+1三个位置被点亮
     * 返回如果点亮str中所有需要点亮的位置，至少需要几盏灯
     * 这道题有动态规划的解，有暴力解，有贪心解
     */
    @AlgName("路灯安排")
    public static class LightProb extends AlgCompImpl<Integer,String>{


        @Override
        public String prepare(AlgCompContext context) {
            return "XX..XX.X....XX...XX.....X.";
        }

        @Override
        protected Integer standard(String data) {
            return 8;
        }

        @Override
        protected Integer test(String data) {
            int count = 0;
            char[] array = data.toCharArray();
            Stack<Character> stack = new Stack<>();
            for(int i=0;i < array.length;i++){
                if(array[i] == 'X'){
                    if(!stack.isEmpty()){
                        count++;
                    }
                    stack.clear();
                    continue;
                }
                stack.push(array[i]);
                if(stack.size() == 3){
                    count++;
                    stack.clear();
                    continue;
                }
            }
            if(!stack.isEmpty()){
                count++;
                stack.clear();
            }
            return count;
        }
    }
}
