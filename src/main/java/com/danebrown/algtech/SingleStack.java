package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 单调栈
 */
@Log4j2
public class SingleStack {
    public static void main(String[] args) {
        SingleStack singleStack = new SingleStack();
        AlgCompMenu.addComp(new SingleStack.MinIndex());
        AlgCompMenu.run();
    }

    /**
     * 给定一个可能含有重复值的数组arr，i位置的数一定存在如下两个信息
     * 1）arr[i]的左侧离i最近并且小于(或者大于)arr[i]的数在哪？
     * 2）arr[i]的右侧离i最近并且小于(或者大于)arr[i]的数在哪？
     * 如果想得到arr中所有位置的两个信息，怎么能让得到信息的过程尽量快。
     *
     * 那么到底怎么设计呢？
     */
    @AlgName("左右最左序列")
    public static class MinIndex extends AlgCompImpl<int[][], int[]>{
        @Data
        @AllArgsConstructor
        public class StackInfo{
            private int val;
            private List<Integer> list;
        }
        @Override
        public int[] prepare() {
            int num = ThreadLocalRandom.current().nextInt(0,100);
            int[] ret = new int[num];
            for(int i = 0 ; i < ret.length;i++){
                ret[i] = ThreadLocalRandom.current().nextInt(0,100);
            }
//            return new int[]{9,9,88,71,33};
            return ret;
        }

        @Override
        public int[] prepare(long range) {
            int num = (int) range;
            int[] ret = new int[num];
            for(int i = 0 ; i < ret.length;i++){
                ret[i] = ThreadLocalRandom.current().nextInt(0,100);
            }
//            return new int[]{9,9,88,71,33};
            return ret;
        }

        @Override
        protected int[][] standard(int[] arr) {
            int[][] res = new int[arr.length][2];
            Stack<List<Integer>> stack = new Stack<>();
            for (int i = 0; i < arr.length; i++) { // i -> arr[i] 进栈
                while (!stack.isEmpty() && arr[stack.peek().get(0)] > arr[i]) {
                    List<Integer> popIs = stack.pop();
                    int leftLessIndex = stack.isEmpty() ? -1 : stack.peek().get(stack.peek().size() - 1);
                    for (Integer popi : popIs) {
                        res[popi][0] = leftLessIndex;
                        res[popi][1] = i;
                    }
                }
                if (!stack.isEmpty() && arr[stack.peek().get(0)] == arr[i]) {
                    stack.peek().add(Integer.valueOf(i));
                } else {
                    ArrayList<Integer> list = new ArrayList<>();
                    list.add(i);
                    stack.push(list);
                }
            }
            while (!stack.isEmpty()) {
                List<Integer> popIs = stack.pop();
                int leftLessIndex = stack.isEmpty() ? -1 : stack.peek().get(stack.peek().size() - 1);
                for (Integer popi : popIs) {
                    res[popi][0] = leftLessIndex;
                    res[popi][1] = -1;
                }
            }
            return res;
        }

        @Override
        protected int[][] test(int[] data) {
            Stack<StackInfo> stack = new Stack<>();
            // 其中的val 是值
            //[][]第一个维度中，表示data中的idx。
            // 第二个维度中，0位表示左边最近最小的idx，1位表示右边最近最大的idx
            int[][] result = new int[data.length][2];
            try {
                for (int i = 0; i < data.length; i++) {
                    if (stack.isEmpty()) {
                        StackInfo stackInfo = new StackInfo(data[i], new ArrayList<>(Arrays.asList(i)));
                        stack.push(stackInfo);
                        result[i][0] = -1;
                        continue;
                    }
                    while (!stack.isEmpty() && stack.peek().val > data[i]) {
                        StackInfo pre = stack.pop();//这里记录信息
                        int leftestIdx = stack.isEmpty() ? -1 : stack.peek().list.get(stack.peek().list.size() - 1);
                        for (int idx : pre.list) {
                            result[idx][0] = leftestIdx;
                            result[idx][1] = i;
                        }
                    }
                    if (!stack.isEmpty() && stack.peek().val == data[i]) {
                        stack.peek().list.add(i);//这里记录信息
                    } else {
                        StackInfo stackInfo = new StackInfo(data[i], new ArrayList<>(Arrays.asList(i)));
                        stack.push(stackInfo);
                        continue;
                    }
                }
                while (!stack.isEmpty()) {
                    StackInfo pop = stack.pop();
                    int leftestIdx = stack.isEmpty() ? -1 : stack.peek().list.get(stack.peek().list.size() - 1);
                    for (int i : pop.list) {
                        result[i][0] = leftestIdx;
                        result[i][1] = -1;
                    }

                }
            }catch (Exception ex){
                log.error(ex);
            }finally {
                return result;
            }
        }
    }

    /**
     * 给定一个正整数的数组arr，arr中任意一个子数组sub。
     * sub一定都可以算出 (sub累加和)*(sub中的最小值)
     * 求所有子数组中，这个最大值是多少？
     */
    public static class MaxSumSubArray extends AlgCompImpl<Long,int[]>{

        @Override
        public int[] prepare() {
            return new int[0];
        }

        @Override
        public int[] prepare(long range) {
            return new int[0];
        }

        @Override
        protected Long standard(int[] data) {
            return null;
        }

        @Override
        protected Long test(int[] data) {
            return null;
        }
    }
}

