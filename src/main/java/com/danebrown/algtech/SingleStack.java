package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompContext;
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
        AlgCompMenu.addComp(new MaxSumSubArray());
        AlgCompMenu.addComp(new LargestRectangleInHistogram());
        AlgCompMenu.run();
    }

    /**
     * 给定一个可能含有重复值的数组arr，i位置的数一定存在如下两个信息
     * 1）arr[i]的左侧离i最近并且小于(或者大于)arr[i]的数在哪？
     * 2）arr[i]的右侧离i最近并且小于(或者大于)arr[i]的数在哪？
     * 如果想得到arr中所有位置的两个信息，怎么能让得到信息的过程尽量快。
     * <p>
     * 那么到底怎么设计呢？
     */
    @AlgName("左右最左序列")
    public static class MinIndex extends AlgCompImpl<int[][], int[]> {
        @Override
        public int[] prepare() {
            int num = ThreadLocalRandom.current().nextInt(0, 100);
            int[] ret = new int[num];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = ThreadLocalRandom.current().nextInt(0, 100);
            }
//            return new int[]{9,9,88,71,33};
            return ret;
        }

        @Override
        public int[] prepare(AlgCompContext context) {
            int num = (int) context.getRange();
            int[] ret = new int[num];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = ThreadLocalRandom.current().nextInt(0, 100);
            }
//            return new int[]{9,9,88,71,33};
//            return new int[]{0, 2, 4, 1, 2};
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
            } catch (Exception ex) {
                log.error(ex);
            } finally {
                return result;
            }
        }

        @Data
        @AllArgsConstructor
        public class StackInfo {
            private int val;
            private List<Integer> list;
        }
    }

    /**
     * 给定一个正整数的数组arr，arr中任意一个子数组sub。
     * sub一定都可以算出 (sub累加和)*(sub中的最小值)
     * 求所有子数组中，这个最大值是多少？
     */
    @AlgName("子数组累加和乘积最大值")
    public static class MaxSumSubArray extends AlgCompImpl<Long, int[]> {


        @Override
        public int[] prepare(AlgCompContext context) {
            int range = (int) context.getRange();
            int[] data = new int[range];
            for (int i = 0; i < data.length; i++) {
                data[i] = ThreadLocalRandom.current().nextInt(0, range);
            }
            return data;
        }

        @Override
        protected Long standard(int[] arr) {
            int size = arr.length;
            int[] sums = new int[size];
            sums[0] = arr[0];
            for (int i = 1; i < size; i++) {
                sums[i] = sums[i - 1] + arr[i];
            }
            int max = Integer.MIN_VALUE;
            Stack<Integer> stack = new Stack<Integer>();
            for (int i = 0; i < size; i++) {
                while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) {
                    int j = stack.pop();
                    max = Math.max(max, (stack.isEmpty() ? sums[i - 1] : (sums[i - 1] - sums[stack.peek()])) * arr[j]);
                }
                stack.push(i);
            }
            while (!stack.isEmpty()) {
                int j = stack.pop();
                max = Math.max(max, (stack.isEmpty() ? sums[size - 1] : (sums[size - 1] - sums[stack.peek()])) * arr[j]);
            }
            return Long.valueOf(max);
        }

        @Override
        protected Long test(int[] data) {
            //先制造一个累加和的数组，作为滑动窗口的辅助数组
            long[] sumarray = new long[data.length];
            long sum = 0;
            for (int i = 0; i < data.length; i++) {
                sumarray[i] = sum + data[i];
                sum = sumarray[i];
            }

            //单调栈数组
            long max = Integer.MIN_VALUE;
            int[][] range = getRange(data);
            for (int i = 0; i < data.length; i++) {
                int minVal = data[i];
                int left = range[i][0];
                //右边界如果是-1，表示没有比他更大的了，那么就是data.lenght-1。如果有值，因为range里面记录的是下标，在计算累加和时，
                // 需要先-1的
                int right = range[i][1] == -1 ? data.length-1 : range[i][1]-1;
                //计算left的累加和时，如果发现left的边界值是-1，表明没有左边界，此时直接给0即可。
                // 如果此时有值，直接计算累加和sumarray[left]
                long slid_sum = sumarray[right] - (left == -1?0:sumarray[left]);
                max = Math.max(max, minVal * slid_sum);
            }
            return max;
        }
        private static int[][] getRange(int[] data) {
            int[][] result = new int[data.length][2];
            Stack<Integer> singleStack = new Stack<>();
            for (int i = 0; i < data.length; i++) {
                while (!singleStack.isEmpty() && data[singleStack.peek()] >= data[i]) {
                    Integer pop = singleStack.pop();
                    int leftestIdx = singleStack.isEmpty() ? -1 : singleStack.peek();
                    result[pop][0] = leftestIdx;
                    result[pop][1] = i;
                }
                singleStack.push(i);
            }
            while (!singleStack.isEmpty()) {
                Integer pop = singleStack.pop();
                int leftestIdx = singleStack.isEmpty() ? -1 : singleStack.peek();
                result[pop][0] = leftestIdx;
                result[pop][1] = -1;

            }
            return result;

        }

    }

    /**
     * 最大面积直方图
     */
    @AlgName("最大面积直方图")
    public static class LargestRectangleInHistogram extends AlgCompImpl<Long,int[]>{

        @Override
        public int[] prepare(AlgCompContext context) {
            int range = ThreadLocalRandom.current().nextInt((int) context.getRange());
            int[] data = new int[range];
            for (int i = 0; i < data.length; i++) {
                data[i] = ThreadLocalRandom.current().nextInt(0, range);
            }
            return data;
        }

        @Override
        protected Long standard(int[] data) {
            return Long.valueOf(largestRectangleArea2(data));
        }
        public static int largestRectangleArea2(int[] height) {
            if (height == null || height.length == 0) {
                return 0;
            }
            int N = height.length;
            int[] stack = new int[N];
            int si = -1;
            int maxArea = 0;
            for (int i = 0; i < height.length; i++) {
                while (si != -1 && height[i] <= height[stack[si]]) {
                    int j = stack[si--];
                    int k = si == -1 ? -1 : stack[si];
                    int curArea = (i - k - 1) * height[j];
                    maxArea = Math.max(maxArea, curArea);
                }
                stack[++si] = i;
            }
            while (si != -1) {
                int j = stack[si--];
                int k = si == -1 ? -1 : stack[si];
                int curArea = (height.length - k - 1) * height[j];
                maxArea = Math.max(maxArea, curArea);
            }
            return maxArea;
        }

        @Override
        protected Long test(int[] data) {
            long max = Integer.MIN_VALUE;
            Stack<Integer> stack = new Stack<>();
            for(int i=0;i < data.length;i++){
                while (!stack.isEmpty() && data[stack.peek()] >= data[i]){
                    int pop = stack.pop();
                    long curIdx = stack.isEmpty()?i-1:stack.peek();
                    long range = Math.abs(curIdx-i);
                    max = Math.max(max,range*data[i]);
                }
                stack.push(i);
            }
            while (!stack.isEmpty()){
                int pop = stack.pop();
                long curIdx = stack.isEmpty()?data.length-1:stack.peek();
                long range = Math.abs(pop-curIdx);
                max = Math.max(max,range*data[pop]);
            }

            return max;
        }
    }
}

