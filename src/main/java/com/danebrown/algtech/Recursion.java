package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompContext;
import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Log4j2
public class Recursion {

    public static void main(String[] args) {
        AlgCompMenu.addComp(new HanoiTower());
        AlgCompMenu.addComp(new PrintStrAllSubSeq());
        AlgCompMenu.addComp(new PrintStrAllSubSeq2());
        AlgCompMenu.addComp(new PrintStrAllConnection());
        AlgCompMenu.addComp(new PrintStrAllConnection2());
        AlgCompMenu.addComp(new ReversStackByRecursion());
        AlgCompMenu.run();
    }

    /**
     * 汉诺塔，算法最优解复杂度O(2^n -1) 无论怎么优化，都没用
     */
    @AlgName("汉诺塔")
    public static class HanoiTower extends AlgCompImpl<String, Integer> {

        //暴力递归
        public static String left2right(int n) {
            StringBuilder stringBuilder = new StringBuilder();
            if (n == 1) {
                String msg = "Move 1 from left to right";
                log.debug(msg);
                return msg;
            }
            stringBuilder.append(left2mid(n - 1));
            String msg = "Move " + n + " from left to right";
            log.debug(msg);
            stringBuilder.append(msg);
            stringBuilder.append(mid2right(n - 1));
            return stringBuilder.toString();
        }

        public static String left2mid(int n) {
            StringBuilder stringBuilder = new StringBuilder();
            if (n == 1) {
                String msg = "Move 1 from left to mid";
                log.debug(msg);
                return msg;
            }
            String msg = "Move " + n + " from left to mid";
            stringBuilder.append(left2right(n - 1));
            stringBuilder.append(msg);
            log.debug(msg);
            stringBuilder.append(right2mid(n - 1));
            return stringBuilder.toString();
        }

        public static String right2mid(int n) {
            StringBuilder stringBuilder = new StringBuilder();
            if (n == 1) {
                String msg = "Move 1 from right to mid";
                log.debug(msg);
                return msg;
            }
            String msg = "Move " + n + " from right to mid";
            stringBuilder.append(right2left(n - 1));
            log.debug(msg);
            stringBuilder.append(msg);
            stringBuilder.append(left2mid(n - 1));
            return stringBuilder.toString();
        }

        public static String mid2right(int n) {
            StringBuilder stringBuilder = new StringBuilder();
            if (n == 1) {
                String msg = "Move 1 from mid to right";
                log.debug(msg);
                return msg;
            }
            String msg = "Move " + n + " from mid to right";
            stringBuilder.append(mid2left(n - 1));
            log.debug(msg);
            stringBuilder.append(msg);
            stringBuilder.append(left2right(n - 1));
            return stringBuilder.toString();

        }

        public static String mid2left(int n) {
            StringBuilder stringBuilder = new StringBuilder();
            if (n == 1) {
                String msg = "Move 1 from mid to left";
                log.debug(msg);
                return msg;
            }
            String msg = "Move " + n + " from mid to left";
            stringBuilder.append(mid2right(n - 1));
            log.debug(msg);
            stringBuilder.append(msg);
            stringBuilder.append(right2left(n - 1));
            return stringBuilder.toString();

        }

        public static String right2left(int n) {
            StringBuilder stringBuilder = new StringBuilder();
            if (n == 1) {
                String msg = "Move 1 from right to left";
                log.debug(msg);
                return msg;
            }
            String msg = "Move " + n + " from right to left";
            stringBuilder.append(right2mid(n - 1));
            log.debug(msg);
            stringBuilder.append(msg);
            stringBuilder.append(mid2left(n - 1));
            return stringBuilder.toString();
        }

        /**
         * 把问题转化为规模小一些的同类问题的子问题
         * 有明确的不需要递归的条件
         * 有得到一个子问题之后的决策过程
         * 不记录每个子问题的解
         *
         * @param N
         * @param from
         * @param to
         * @param other
         * @return
         */
        //版本1
        public static String func(int N, String from, String to, String other) {
            if (N == 1) {
                String msg = String.format("Move 1 from " + from + " to " + to);
                log.debug(msg);
                return msg;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(func(N - 1, from, other, to));
                String msg = "Move " + N + " from " + from + " to " + to;
                log.debug(msg);
                stringBuilder.append(msg);
                stringBuilder.append(func(N - 1, other, to, from));
                return stringBuilder.toString();
            }

        }

        public static String hanoi(int N) {
            StringBuilder stringBuilder = new StringBuilder();
            if (N < 1) {
                return "";
            }
            Stack<Record> stack = new Stack<>();
            stack.add(new Record(false, N, "left", "right", "mid"));
            while (!stack.isEmpty()) {
                Record cur = stack.pop();
                if (cur.base == 1) {
                    String msg = String.format("Move 1 from " + cur.from + " to " + cur.to);
                    stringBuilder.append(msg);
                    log.debug(msg);
                    if (!stack.isEmpty()) {
                        stack.peek().finish1 = true;
                    }
                } else {
                    if (!cur.finish1) {
                        stack.push(cur);
                        stack.push(new Record(false, cur.base - 1, cur.from, cur.other, cur.to));
                    } else {
                        String msg = "Move " + cur.base + " from " + cur.from + " to " + cur.to;
                        log.debug(msg);
                        stringBuilder.append(msg);
                        stack.push(new Record(false, cur.base - 1, cur.other, cur.to, cur.from));
                    }
                }
            }
            return stringBuilder.toString();
        }

        @Override
        public Integer prepare(AlgCompContext context) {
            return ThreadLocalRandom.current().nextInt(10, 20);
        }

        @Override
        protected String standard(Integer data) {
            if (data > 0) {
                return func(data, "left", "right", "mid");
//                String msg = left2right(data);
//                return msg;
            }
            return null;
        }

        @Override
        protected String test(Integer data) {

            return hanoi(data);
        }

        public static class Record {
            public boolean finish1;
            public int base;
            public String from;
            public String to;
            public String other;

            public Record(boolean f1, int b, String f, String t, String o) {
                finish1 = false;
                base = b;
                from = f;
                to = t;
                other = o;
            }
        }
    }

    @AlgName("打印字符串全部子序列")
    public static class PrintStrAllSubSeq extends AlgCompImpl<List<String>, String> {

        public static void process(char[] str, int idx, List<String> ans, String path) {
            if (idx == str.length) {
                ans.add(path);
                return;
            }
            process(str, idx + 1, ans, path);
            process(str, idx + 1, ans, path + str[idx]);
        }

        public static List<String> process2(char[] str, String path) {
            List<String> paths = new ArrayList<>();
            String tmp = "";
            paths.add(tmp);
            for (int i = 0; i < str.length; i++) {
                tmp += str[i];
                paths.add(tmp);
                paths.addAll(process2(Arrays.copyOfRange(str, i + 1, str.length), tmp));
            }
            return paths;
        }

        public static void process1(char[] str, int idx, Set<String> ans, String path) {
            if (idx == str.length) {
                ans.add(path);
                return;
            }
            process1(str, idx + 1, ans, path);
            process1(str, idx + 1, ans, path + str[idx]);
        }

        @Override
        public String prepare(AlgCompContext context) {
            return "ABCD";
        }

        @Override
        protected List<String> standard(String data) {
            char[] chars = data.toCharArray();
            List<String> result = new ArrayList<>();

            process(chars, 0, result, "");
            result.forEach(s -> System.out.println(s));
            result.sort(String::compareTo);
            return result;
        }

        @Override
        protected List<String> test(String data) {
            char[] chars = data.toCharArray();
            Set<String> result = new HashSet<>();
            process1(chars, 0, result, "");
            List<String> realResult = result.stream().collect(Collectors.toList());
            realResult.sort(String::compareTo);
            return realResult;
//            List<String> realResult = process2(chars,"");
//            realResult.add("");
//            realResult.sort(String::compareTo);
//            return realResult;
        }
    }

    @AlgName("打印字符串全部子序列-不出现重复字面值")
    public static class PrintStrAllSubSeq2 extends AlgCompImpl<List<String>, String> {

        public static void process2(char[] str, int index, HashSet<String> set, String path) {
            if (index == str.length) {
                set.add(path);
                return;
            }
            String no = path;
            process2(str, index + 1, set, no);
            String yes = path + str[index];
            process2(str, index + 1, set, yes);
        }

        public static void process(char[] str, int idx, Set<String> ans, String path) {
            if (idx == str.length) {
                ans.add(path);
                return;
            }
            process(str, idx + 1, ans, path);
            process(str, idx + 1, ans, path + str[idx]);
        }

        @Override
        public String prepare(AlgCompContext context) {
            return "abcadfsdfacdssfcsd";
        }

        @Override
        protected List<String> standard(String s) {
            char[] str = s.toCharArray();
            String path = "";
            HashSet<String> set = new HashSet<>();
            process2(str, 0, set, path);
            List<String> ans = new ArrayList<>();
            for (String cur : set) {
                ans.add(cur);
            }
            ans.sort(String::compareTo);
            return ans;

        }

        @Override
        protected List<String> test(String data) {
            char[] chars = data.toCharArray();
            Set<String> result = new HashSet<>();

            process(chars, 0, result, "");
            List<String> realResult = result.stream().collect(Collectors.toList());
            realResult.sort(String::compareTo);
            return realResult;
        }
    }

    @AlgName("打印字符串全部排列")
    public static class PrintStrAllConnection extends AlgCompImpl<List<String>, String> {

        public static List<String> process1(List<Character> rest, String path) {
            if (rest == null || rest.isEmpty()) {
                return Lists.newArrayList(path);
            }
            List<String> ans = new ArrayList<>();
            for (int i = 0; i < rest.size(); i++) {
                Character tmp = rest.get(i);
                rest.remove(i);
                ans.addAll(process1(rest, path + tmp));
                rest.add(i, tmp);
            }
            return ans;
        }

        public static void process2(List<Character> rest, String path, List<String> ans) {
            if (rest.isEmpty()) {
                ans.add(path);
                return;
            }
            int N = rest.size();
            for (int i = 0; i < N; i++) {
                Character tmp = rest.get(i);
                String inputPath = path + tmp;
                rest.remove(i);
                process2(rest, inputPath, ans);
                rest.add(i, tmp);
            }
        }

        /**
         * 递归3
         *
         * @param rest 剩余的字符
         * @param idx  当前交换的位置
         * @param ans  结果
         */
        public static void process3(char[] rest, int idx, List<String> ans) {
            //重点1：base case
            if (idx >= rest.length) {
                ans.add(String.valueOf(rest));
                return;
            }
            //重点2：从idx开始是为了保证idx之前的，不变
            for (int i = idx; i < rest.length; i++) {
                //交换
                swap(rest, i, idx);
                process3(rest, idx + 1, ans);
                //重点3:恢复现场
                swap(rest, i, idx);
            }
        }

        public static void swap(char[] rest, int i, int j) {
            char tmp = rest[i];
            rest[i] = rest[j];
            rest[j] = tmp;
        }

        @Override
        public String prepare(AlgCompContext context) {
            return "abcdefghij";
        }

        @Override
        protected List<String> standard(String data) {
            List<String> ans = new ArrayList<>();
            if (Strings.isNullOrEmpty(data)) {
                return ans;
            }
            List<Character> input = new ArrayList<>();
            char[] ary = data.toCharArray();
            for (int i = 0; i < ary.length; i++) {
                input.add(ary[i]);
            }
            process2(input, "", ans);
//            ans = process1(input,"");
            ans.sort(String::compareTo);
            return ans;
        }

        @Override
        protected List<String> test(String data) {
            List<String> ans = new ArrayList<>();
            if (Strings.isNullOrEmpty(data)) {
                return ans;
            }
            List<Character> input = new ArrayList<>();
            char[] ary = data.toCharArray();
            process3(ary, 0, ans);
//            for(int i=0;i < ary.length;i++){
//                input.add(ary[i]);
//            }
//            process2(input,"",ans);
            ans.sort(String::compareTo);
            return ans;
        }
    }

    @AlgName("打印字符串全部排列-不出现重复字面值")
    public static class PrintStrAllConnection2 extends AlgCompImpl<List<String>, String> {

        public static void process2(List<Character> rest, String path, Set<String> ans) {
            if (rest.isEmpty()) {
                ans.add(path);
                return;
            }
            int N = rest.size();
            for (int i = 0; i < N; i++) {
                Character tmp = rest.get(i);
                String inputPath = path + tmp;
                rest.remove(i);
                process2(rest, inputPath, ans);
                rest.add(i, tmp);
            }
        }

        /**
         * 递归3
         *
         * @param rest 剩余的字符
         * @param idx  当前交换的位置
         * @param ans  结果
         */
        public static void process3(char[] rest, int idx, List<String> ans) {
            //重点1：base case
            if (idx >= rest.length) {
                ans.add(String.valueOf(rest));
                return;
            }
            boolean[] visit = new boolean[256];

            //重点2：从idx开始是为了保证idx之前的，不变
            for (int i = idx; i < rest.length; i++) {
                //重点4：剪枝，减掉了不必要的分支。用Set也能搞，但是Set用的是过滤。剪枝比过滤好
                if (!visit[rest[i]]) {
                    visit[rest[i]] = true;
                    //交换
                    swap(rest, i, idx);
                    process3(rest, idx + 1, ans);
                    //重点3:恢复现场
                    swap(rest, i, idx);
                }
            }

        }

        public static void swap(char[] rest, int i, int j) {
            char tmp = rest[i];
            rest[i] = rest[j];
            rest[j] = tmp;
        }

        @Override
        public String prepare(AlgCompContext context) {
            return "abccddeff";
        }

        @Override
        protected List<String> standard(String data) {
            Set<String> ans = new HashSet<>();
            if (Strings.isNullOrEmpty(data)) {
                return ans.stream().collect(Collectors.toList());
            }
            List<Character> input = new ArrayList<>();
            char[] ary = data.toCharArray();
            for (int i = 0; i < ary.length; i++) {
                input.add(ary[i]);
            }
            process2(input, "", ans);
            List<String> realAns = ans.stream().collect(Collectors.toList());
            realAns.sort(String::compareTo);
            return realAns;
        }

        @Override
        protected List<String> test(String data) {
            List<String> ans = new ArrayList<>();
            if (Strings.isNullOrEmpty(data)) {
                return ans;
            }
            char[] ary = data.toCharArray();
            process3(ary, 0, ans);
            ans.sort(String::compareTo);
            return ans;
        }
    }

    /**
     * 给一个栈，逆序这个栈
     * 要求不能申请额外的变量
     * 只能用递归实现
     */
    @AlgName("只用递归逆序栈-不用临时空间只用递归")
    public static class ReversStackByRecursion extends AlgCompImpl<Stack<Integer>, Stack<Integer>> {

        
        

        @Override
        public Stack<Integer> prepare(AlgCompContext context) {
            Stack<Integer> stack = new Stack<>();
            int len = ThreadLocalRandom.current().nextInt(10, 100);
            for (int i = 0; i < len; i++) {
                stack.push(ThreadLocalRandom.current().nextInt());
            }
            return stack;
        }

        @Override
        protected Stack<Integer> standard(Stack<Integer> data) {
            Stack<Integer> input = new Stack<>();
            input.addAll(data);
            reversStack(input);
            
            return input;
        }
        private static void reversStack(Stack<Integer> input) {
            Queue<Integer> tmp = new LinkedBlockingQueue<>();
            while (!input.isEmpty()) {
                tmp.add(input.pop());
            }
            while (!tmp.isEmpty()) {
                input.push(tmp.poll());
            }
        }
        

        @Override
        protected Stack<Integer> test(Stack<Integer> data) {
            Stack<Integer> input = new Stack<>();
            input.addAll(data);
            revers(input);
            return input;
        }
        private static void revers(Stack<Integer> input){
            if(input.isEmpty()){
                return;
            }
            Integer rev = reversStack2(input);
            revers(input);
            input.push(rev);
        }
        private static Integer reversStack2(Stack<Integer> input) {
            Integer result = input.pop();
            if (input.isEmpty()) {
                return result;
            }
            Integer last = reversStack2(input);
            input.push(result);
            return last;
        }
    }
}
