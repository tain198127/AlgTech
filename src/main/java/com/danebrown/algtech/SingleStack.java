package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompContext;
import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * 单调栈
 */
@Log4j2
public class SingleStack {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new SingleStack.MinIndex());
        AlgCompMenu.addComp(new MaxSumSubArray());
        AlgCompMenu.addComp(new LargestRectangleInHistogram());
        AlgCompMenu.addComp(new MaximalRectangle());
        AlgCompMenu.addComp(new CountSubmatricesWithAllOnes());
        AlgCompMenu.addComp(new MergeStones());
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
    @AlgName(value = "最大面积直方图",range = 100000)
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
                    long leftestIdx = stack.isEmpty()?-1:stack.peek();
                    long range = (i-leftestIdx-1);
                    //注意，这里的高度data[pop]要按照pop出来的来算
                    max = Math.max(max,range*data[pop]);
                }
                stack.push(i);
            }
            while (!stack.isEmpty()){
                int pop = stack.pop();
                long leaftestIdx = stack.isEmpty()?-1:stack.peek();
                long range = Math.abs(data.length-leaftestIdx-1);
                max = Math.max(max,range*data[pop]);
            }

            return max;
        }
    }

    /**
     * 给定一个二维数组matrix，其中的值不是0就是1，
     * 返回全部由1组成的最大子矩形，内部有多少个1
     * 这道题非常难，如果用暴力算法，会达到N^6次方这么多的时间复杂度
     * 本质上做压缩数组技巧，进行压缩后，可以达到N^2的时间复杂度
     * 思路如下，从每一行开始，对数组状态进行压缩，如果有值则本列的数量累加1.如果某一列为'0'，则本列的累加值归零
     * 完成一行的扫描后，并将累加值统计完后，对累加值数组，进行'最大直方图'计算。并将最大值记录到临时变量中
     */
    @AlgName("最大矩形面积")
    public static class MaximalRectangle extends AlgCompImpl<Integer, char[][]>{

        @Override
        public char[][] prepare(AlgCompContext context) {
            int length = (int) context.getRange();
            int height = (int) Math.ceil(length* (ThreadLocalRandom.current().nextInt(1,100))/100);
            char[][] result = new char[length][height];
            for(int i=0; i < length;i++){
                for(int j=0;j < height;j++){
                    result[i][j] = ThreadLocalRandom.current().nextBoolean()?'0':'1';
                }
            }
            if(log.isDebugEnabled()){
                for(int i=0;i<length;i++){
                    log.debug("{}",result[i]);
                }
            }
            return result;
        }

        @Override
        protected Integer standard(char[][] data) {
            return maximalRectangle(data);
        }
        private static int maximalRectangle(char[][] map) {
            if (map == null || map.length == 0 || map[0].length == 0) {
                return 0;
            }
            int maxArea = 0;
            int[] height = new int[map[0].length];
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    height[j] = map[i][j] == '0' ? 0 : height[j] + 1;
                }
                maxArea = Math.max(maxRecFromBottom(height), maxArea);
            }
            return maxArea;
        }

        // height是正方图数组
        private static int maxRecFromBottom(int[] height) {
            if (height == null || height.length == 0) {
                return 0;
            }
            int maxArea = 0;
            Stack<Integer> stack = new Stack<Integer>();
            for (int i = 0; i < height.length; i++) {
                while (!stack.isEmpty() && height[i] <= height[stack.peek()]) {
                    int j = stack.pop();
                    int k = stack.isEmpty() ? -1 : stack.peek();
                    int curArea = (i - k - 1) * height[j];
                    maxArea = Math.max(maxArea, curArea);
                }
                stack.push(i);
            }
            while (!stack.isEmpty()) {
                int j = stack.pop();
                int k = stack.isEmpty() ? -1 : stack.peek();
                int curArea = (height.length - k - 1) * height[j];
                maxArea = Math.max(maxArea, curArea);
            }
            return maxArea;
        }
        @Override
        protected Integer test(char[][] data) {
            if(data == null || data.length ==0 || data[0].length == 0){
                return 0;
            }
            //矩阵的宽
            int length = data.length;
            //矩阵的高
            int high = data[0].length;
            int[] cur = new int[length];
            int max = Integer.MIN_VALUE;
            for(int i=0 ;i < high;i++){
                for(int j=0; j < length;j++){
                    cur[j] = data[j][i]=='0'?0:cur[j]+1;
                }
                int m = largetRectangleInHistogram(cur);
                max = Math.max(max,m);
            }
            return max;
        }
        private static int largetRectangleInHistogram(int[] data){
            int[] stack = new int[data.length];
            int idx = -1;
            int max = 0;
            for(int i=0; i < data.length;i++) {
                while (idx != -1 && data[stack[idx]] >= data[i]){
                    int popIdx = stack[idx--];
                    int leftestIdx = idx ==-1?-1:stack[idx];
                    int range = i - leftestIdx -1;
                    max = Math.max(max, range * data[popIdx]);
                }
                stack[++idx]=i;
            }
            while (idx !=-1){
                int popIdx = stack[idx--];
                int leftestIdx = idx ==-1?-1:stack[idx];
                int range = data.length - leftestIdx -1;
                max = Math.max(max,range*data[popIdx]);
            }
            return max;
        }
    }

    /**
     * 测试链接：https://leetcode.com/problems/count-submatrices-with-all-ones
     * 给定一个二维数组matrix，其中的值不是0就是1，
     * 返回全部由1组成的子矩形数量
     */
    @AlgName("最多矩形数量")
    //https://leetcode.com/problems/count-submatrices-with-all-ones
    public static class CountSubmatricesWithAllOnes extends AlgCompImpl<Integer,int[][]>{

        @Override
        public int[][] prepare(AlgCompContext context) {
//            context.setRange(5);
            int length = (int) context.getRange();
            int height = (int) Math.ceil(length* (ThreadLocalRandom.current().nextInt(1,100))/100);
            int[][] result = new int[length][height];
            for(int i=0; i < length;i++){
                for(int j=0;j < height;j++){
                    result[i][j] = ThreadLocalRandom.current().nextBoolean()?0:1;
                }
            }
            if(log.isDebugEnabled()){
                for(int i=0;i<length;i++){
                    log.debug("{}",result[i]);
                }
            }
            return result;
        }

        @Override
        protected Integer standard(int[][] data) {
            return StandAlg.numSubmat(data);
        }
        public static class StandAlg{
            public static int numSubmat(int[][] mat) {
                if (mat == null || mat.length == 0 || mat[0].length == 0) {
                    return 0;
                }
                int nums = 0;
                int[] height = new int[mat[0].length];
                for (int i = 0; i < mat.length; i++) {
                    for (int j = 0; j < mat[0].length; j++) {
                        height[j] = mat[i][j] == 0 ? 0 : height[j] + 1;
                    }
                    nums += countFromBottom(height);
                }
                return nums;

            }

            public static int countFromBottom(int[] height) {
                if (height == null || height.length == 0) {
                    return 0;
                }
                int nums = 0;
                int[] stack = new int[height.length];
                int si = -1;
                for (int i = 0; i < height.length; i++) {
                    while (si != -1 && height[stack[si]] >= height[i]) {
                        int cur = stack[si--];
                        if (height[cur] > height[i]) {
                            int left = si == -1 ? -1 : stack[si];
                            int n = i - left - 1;
                            int down = Math.max(left == -1 ? 0 : height[left], height[i]);
                            nums += (height[cur] - down) * num(n);
                        }

                    }
                    stack[++si] = i;
                }
                while (si != -1) {
                    int cur = stack[si--];
                    int left = si == -1  ? -1 : stack[si];
                    int n = height.length - left - 1;
                    int down = left == -1 ? 0 : height[left];
                    nums += (height[cur] - down) * num(n);
                }
                return nums;
            }

            public static int num(int n) {
                return ((n * (1 + n)) >> 1);
            }
        }
        @Override
        protected Integer test(int[][] data) {
            if(data==null || data.length<=0||data[0].length <=0){
                return 0;
            }

            int length = data.length;
            int height = data[0].length;
            int[] sum = new int[data.length];
            int max = 0;
            //先扫描高度
            for(int h=0; h < height;h++){
                //再扫描宽度,压缩数组
                for(int w=0;w<length;w++){
                    sum[w] = data[w][h] == 0?0:sum[w]+1;
                }
                max +=maxSubMatrix(sum);
            }
            return max;
        }
        public static int maxSubMatrix(int[] height){
            Stack<Integer> stack = new Stack<>();
            int nums = 0;
            for(int i=0 ;i < height.length;i++){
                //如果 当前的值比stack中指定的值小，就把stack中的值弹出来
                while (!stack.isEmpty() && height[stack.peek()] >= height[i]){
                    int rightEstIdx = stack.pop();
                    if(height[rightEstIdx] > height[i]) {
                        //最左边的idx
                        int leftEstIdx = stack.isEmpty() ? -1 : stack.peek();
                        //算出宽度
                        int range = i - leftEstIdx - 1;
                        //两遍较小值里面最大的值，作为低水位
                        int leftOrRightMax = Math.max(leftEstIdx == -1 ? 0 : height[leftEstIdx], height[i]);
                        //最高水位和最低水位中间的gap
                        int gap = height[rightEstIdx] - leftOrRightMax;
                        //N*N(+1)/2
                        nums += gap* num(range);
                    }
                }
                stack.push(i);
            }
            while (!stack.isEmpty()){
                //最右边的idx
                int rightEstIdx = stack.pop();
                //最左边的idx
                int leftEstIdx = stack.isEmpty()?-1:stack.peek();
                //算出宽度
                int range = height.length-leftEstIdx-1;
                //最左或者最右边最低的高水位
                int leftOrRightMax = leftEstIdx == -1?0:height[leftEstIdx];
                //最高水位和最低水位中间的gap
                int gap = height[rightEstIdx] - leftOrRightMax;
                // N*N(+1)/2
                nums += gap* num(range);
            }
            return nums;
        }
        private static int num(int n){
            return ((n *(n+1)) >> 1);
        }
    }


    /**
     * 给定一个数组arr，
     * 返回所有子数组最小值的累加和
     */
    //TODO 未完成
    public static class SubArrayMinValSum extends AlgCompImpl<Integer,int[]>{

        @Override
        public int[] prepare(AlgCompContext context) {
            int range = (int) context.getRange();
            int[] data = new int[range];
            for(int i=0; i < data.length;i++){
                data[i] = ThreadLocalRandom.current().nextInt(range);
            }
            return data;
        }

        public static int[] leftNearLessEqual2(int[] arr) {
            int N = arr.length;
            int[] left = new int[N];
            for (int i = 0; i < N; i++) {
                int ans = -1;
                for (int j = i - 1; j >= 0; j--) {
                    if (arr[j] <= arr[i]) {
                        ans = j;
                        break;
                    }
                }
                left[i] = ans;
            }
            return left;
        }

        public static int[] rightNearLess2(int[] arr) {
            int N = arr.length;
            int[] right = new int[N];
            for (int i = 0; i < N; i++) {
                int ans = N;
                for (int j = i + 1; j < N; j++) {
                    if (arr[i] > arr[j]) {
                        ans = j;
                        break;
                    }
                }
                right[i] = ans;
            }
            return right;
        }
        @Override
        protected Integer standard(int[] data) {
            // left[i] = x : arr[i]左边，离arr[i]最近，<=arr[i]，位置在x
            int[] left = leftNearLessEqual2(data);
            // right[i] = y : arr[i]右边，离arr[i]最近，< arr[i],的数，位置在y
            int[] right = rightNearLess2(data);
            int ans = 0;
            for (int i = 0; i < data.length; i++) {
                int start = i - left[i];
                int end = right[i] - i;
                ans += start * end * data[i];
            }
            return ans;
        }

        @Override
        protected Integer test(int[] data) {
            return null;
        }
    }

    /**
     * 1）斐波那契数列的线性求解（O(N)）的方式非常好理解
     *
     * 2）同时利用线性代数，也可以改写出另一种表示
     *
     *  | F(N) , F(N-1) | = | F(2), F(1) |  *  某个二阶矩阵的N-2次方
     *
     * 3）求出这个二阶矩阵，进而最快求出这个二阶矩阵的N-2次方
     *
     * 如果某个递归，除了初始项之外，具有如下的形式
     *
     * F(N) = C1 * F(N) + C2 * F(N-1) + … + Ck * F(N-k) ( C1…Ck 和k都是常数)
     *
     * 并且这个递归的表达式是严格的、不随条件转移的
     *
     * 那么都存在类似斐波那契数列的优化，时间复杂度都能优化成O(logN)
     *
     * 斐波那契数列矩阵乘法方式的实现
     */
    //TODO 未完成
    public class Fib extends AlgCompImpl<Integer,int[]>{

        @Override
        public int[] prepare(AlgCompContext context) {
            return new int[0];
        }

        @Override
        protected Integer standard(int[] data) {
            return null;
        }

        @Override
        protected Integer test(int[] data) {
            return null;
        }
    }

    /**
     * 一个人可以一次往上迈1个台阶，也可以迈2个台阶
     *
     * 返回这个人迈上N级台阶的方法数
     */
    public static class ClimbSteps extends AlgCompImpl<Integer,int[]>{

        @Override
        public int[] prepare(AlgCompContext context) {
            return new int[0];
        }

        @Override
        protected Integer standard(int[] data) {
            return null;
        }

        @Override
        protected Integer test(int[] data) {
            return null;
        }
    }

    /**
     * 第一年农场有1只成熟的母牛A，往后的每年：
     *
     * 1）每一只成熟的母牛都会生一只母牛
     *
     * 2）每一只新出生的母牛都在出生的第三年成熟
     *
     * 3）每一只母牛永远不会死
     *
     * 返回N年后牛的数量
     */
    //TODO 未完成
    public static class CowNum extends AlgCompImpl<Integer,Integer>{

        @Override
        public Integer prepare(AlgCompContext context) {
            return null;
        }

        @Override
        protected Integer standard(Integer data) {
            return null;
        }

        @Override
        protected Integer test(Integer data) {
            return null;
        }
    }

    /**
     * 给定一个数N，想象只由0和1两种字符，组成的所有长度为N的字符串
     *
     * 如果某个字符串,任何0字符的左边都有1紧挨着,认为这个字符串达标
     *
     * 返回有多少达标的字符串
     */
    //TODO 未完成
    public static class GoodNum extends AlgCompImpl<Integer,String>{

        @Override
        public String prepare(AlgCompContext context) {
            return null;
        }

        @Override
        protected Integer standard(String data) {
            return null;
        }

        @Override
        protected Integer test(String data) {
            return null;
        }
    }

    /**
     * 用1*2的瓷砖，把N*2的区域填满
     *
     * 返回铺瓷砖的方法数
     */
    //TODO 未完成
    public static class FillRegionWays extends AlgCompImpl<Integer,Integer>{

        @Override
        public Integer prepare(AlgCompContext context) {
            return null;
        }

        @Override
        protected Integer standard(Integer data) {
            return null;
        }

        @Override
        protected Integer test(Integer data) {
            return null;
        }
    }


    @Data
    public static class MergeStonesInput{
        private int k;
        private int[] stones;

    }
    /**
     * 有 N 堆石头排成一排，第 i 堆中有stones[i]块石头。
     *
     * 每次移动（move）需要将连续的K堆石头合并为一堆，而这个移动的成本为这K堆石头的总数。
     *
     * 找出把所有石头合并成一堆的最低成本。如果不可能，返回 -1 。
     * 链接：https://leetcode.cn/problems/minimum-cost-to-merge-stones
     *
     */
    public static class MergeStones extends AlgCompImpl<Integer,MergeStonesInput> {

        @Override
        public MergeStonesInput prepare(AlgCompContext context) {
            MergeStonesInput input = new MergeStonesInput();

            int k = (int) (context.getRange() * ThreadLocalRandom.current().nextDouble(0.1d,0.3));
            int N = 0;
            while (k == 0 || N==0||(N - 1) % (k - 1) != 0) {
                N = ThreadLocalRandom.current().nextInt((int) (context.getRange() * 0.1), (int) context.getRange());
            }
            int[] stones = new int[N];
            for(int i =0; i < N;i++){
                stones[i] = (int) (N *ThreadLocalRandom.current().nextDouble(0.1,1));
            }
            input.setStones(stones);
            input.setK(k);

            input.setStones(new int[]{3,5,1,2,6});
            input.setK(3);
            return input;
        }

        @Override
        protected Integer standard(MergeStonesInput data) {
            int k = data.k;
            int[] stones = data.stones;
            int n = stones.length;
            if ((n - 1) % (k - 1) != 0) {
                return -1; // 无法合并为一堆
            }
            return merge(stones,k);


        }
        private static int merge(int[] stones,int k){
            if(stones ==null|| stones.length<=0){
                return 0;
            }
            int n = stones.length;
            if ((n - 1) % (k - 1) != 0) {
                return -1; // 无法合并为一堆
            }

            int sum = 0;
            List<Integer> tmp = new ArrayList<>();
            for(int i=0;i< n;i=i+k){
                int low = i;
                int height = i+k;
                int[] subrange = Arrays.copyOfRange(stones,low,height);
                IntStream stream = Arrays.stream(subrange).sorted();
                int bigest = stream.max().getAsInt();
                int tmpSum = stream.sum();
                int steps = tmpSum-bigest;
                tmp.add(tmpSum);
                sum += steps;
            }
            int[] input = tmp.stream().mapToInt(Integer::intValue).toArray();
            sum+= merge(input,k);
            return sum;
//            throw new RuntimeException("尚未完成");
        }
        private static int mergeStones(int[] origin, int range) {
            int K = range;
            int[] stones = origin;
            int n = stones.length;
            if ((n - 1) % (K - 1) != 0) {
                return -1; // 无法合并为一堆
            }
            int[][][] dp = new int[n][n][K + 1]; // dp[i][j][k] 表示将第 i 到第 j 堆合并成 k 堆的最小成本
            int[] prefixSum = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                prefixSum[i] = prefixSum[i - 1] + stones[i - 1];
            }
            for (int len = K; len <= n; len++) {
                for (int i = 0; i + len - 1 < n; i++) {
                    int j = i + len - 1;
                    for (int k = 2; k <= K; k++) {
                        dp[i][j][k] = Integer.MAX_VALUE;
                        for (int p = i; p < j; p += k - 1) {
                            dp[i][j][k] = Math.min(dp[i][j][k], dp[i][p][1] + dp[p + 1][j][k - 1]);
                        }
                    }
                    dp[i][j][1] = dp[i][j][K] + prefixSum[j + 1] - prefixSum[i];
                }
            }
            return dp[0][n - 1][1];
        }
        @Override
        protected Integer test(MergeStonesInput data) {
            int K = data.k;
            int[] stones = data.stones;
//            int n = stones.length;
            return mergeStones(stones,K);
        }
    }
}

