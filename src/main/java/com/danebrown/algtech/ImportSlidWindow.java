package com.danebrown.algtech;

import cn.hutool.core.lang.Pair;
import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 单调栈
 */
@Log4j2
public class ImportSlidWindow {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new SlidWindowMaxArray());
        AlgCompMenu.addComp(new SlidWindowMaxArray2());
        AlgCompMenu.addComp(new SlidSubWindowMaxArray());
        AlgCompMenu.run();
    }
    public enum SlidWindowMaxArrayEnum{
        stander{
            public Integer[] cal(SlidWindowMaxArrayParam param){
                int sum = 0;
                StringBuilder stringBuilder = new StringBuilder();
                for(int i=0;i<param.steps.length;i++){
                    
                    sum += param.steps[i];
                    if(sum <0){
                        throw new IllegalArgumentException("生成的算法错误");
                    }
                }
                log.info("{},总值:{}",param.steps,sum);
                return param.data;
            }
        },
        test{
            public Integer[] cal(SlidWindowMaxArrayParam param){
                return null;
            }
        };
        public abstract Integer[] cal(SlidWindowMaxArrayParam param);
    }
    @Data
    public static class SlidWindowMaxArrayParam{
        private Integer[] data;
        //窗口的L,R一开始均为0，steps中1表示R向右移动1位，-1表示L向右动1位
        private Integer[] steps;
    }
    @AlgName("滑动窗口最大值")
    public static class SlidWindowMaxArray extends AlgCompImpl<Integer[],SlidWindowMaxArrayParam>{

        @Override
        public SlidWindowMaxArrayParam prepare() {
            SlidWindowMaxArrayParam param = new SlidWindowMaxArrayParam();
            int size = ThreadLocalRandom.current().nextInt(1,100);
            //L和R要走几步
            int steps = ThreadLocalRandom.current().nextInt(0,size);
            //L-R之间的间距
            int gap =steps>0? ThreadLocalRandom.current().nextInt(0,steps):0;
            Stack<Integer> stack = new Stack<>();
            for(int i=0;i < gap;i++){
                stack.add(1);
            }
            param.data = new Integer[size];
            param.steps = new Integer[steps];
            for(int i=0 ; i < size;i++){
                param.data[i] = ThreadLocalRandom.current().nextInt(1,size*3);
            }
            int sum=0;
            for(int i = 0; i < steps;i++){
                int nextStep=ThreadLocalRandom.current().nextBoolean()?-1:1;
                while (sum+nextStep <0){
                     nextStep= ThreadLocalRandom.current().nextBoolean()?-1:1;
                }
                if(nextStep <0 && !stack.isEmpty()){
                    nextStep=ThreadLocalRandom.current().nextBoolean()?nextStep:stack.pop();
                }
                sum+=nextStep;
                param.steps[i] = nextStep;
            }
            return param;
        }

        @Override
        protected Integer[] standard(SlidWindowMaxArrayParam param) {
            LinkedList<Pair<Integer,Integer>> dequelist = new LinkedList<>();
            int l=0,r=0;
            for(int i=0 ; i < param.steps.length;i++){
                if(param.steps[i] >0){
                    r++;
                }else{
                    l++;
                }

            }
            
            return SlidWindowMaxArrayEnum.stander.cal(param);
            
        }

        @Override
        protected Integer[] test(SlidWindowMaxArrayParam param) {
            int sum = 0;
            for(int i=0;i<param.steps.length;i++){
                sum += param.steps[i];
                if(sum <0){
                    throw new IllegalArgumentException("生成的算法错误");
                }
            }
            return param.data;
        }
    }
    @Data
    public static class SlidWindowMaxArray2Param{
        //数组
        private int[] arr;
        //窗口大小
        private int w;
    }

    /**
     * 假设一个固定大小为W的窗口，依次划过arr，
     * 返回每一次滑出状况的最大值
     * 例如，arr = [4,3,5,4,3,3,6,7], W = 3
     * 返回：[5,5,5,4,6,7]
     */
    @AlgName("滑动窗口最大值2")
    public static class SlidWindowMaxArray2 extends AlgCompImpl<Integer[],SlidWindowMaxArray2Param>{
        public enum SLID_WINDOW_2_METHOD{
            standard{
                @Override
                public Integer[] cal(SlidWindowMaxArray2Param data) {
                    return new Integer[0];
                }
            },
            test{
                @Override
                public Integer[] cal(SlidWindowMaxArray2Param data) {
                    
                    int index = 0;
                    int w = data.w;
                    int[] arr = data.arr;
                    Integer[] result = new Integer[arr.length-w+1];
                    LinkedList<Integer> window = new LinkedList<>();
                    for(int R=0;R< arr.length;R++){
                        //判断尾巴,并且删除比当前小的所有值
                        while (!window.isEmpty() && arr[window.peekLast()] <= arr[R]){
                            window.pollLast();
                        }
                        //插在后面
                        window.addLast(R);
                        //判断左边已经滑出窗口
                        if(window.peekFirst() == R-w){
                            window.pollFirst();
                        }
                        //如果形成正常窗口，则收集答案
                        if(R >= w -1){
                            result[index++] = arr[window.peekFirst()];
                        }
                    }
                    return result;
                }
            };
            public abstract Integer[] cal(SlidWindowMaxArray2Param data);
        }
        @Override
        public SlidWindowMaxArray2Param prepare() {
            return prepare(1000);
        }

        public SlidWindowMaxArray2Param prepare(int size) {
            SlidWindowMaxArray2Param param = new SlidWindowMaxArray2Param();
            int arrSize = ThreadLocalRandom.current().nextInt(1,size);
            int windw = ThreadLocalRandom.current().nextInt(0,arrSize);
            param.arr = new int[arrSize];
            param.w = windw;
            for(int i=0;i < arrSize;i++){
                param.arr[i] = ThreadLocalRandom.current().nextInt(0,arrSize*3);
            }
            return param;
        }

        @Override
        protected Integer[] standard(SlidWindowMaxArray2Param data) {
            
            int[] arr = data.arr;
            int w = data.w;
            if (arr == null || w < 1 || arr.length < w) {
                return null;
            }
            int N = arr.length;
            Integer[] res = new Integer[N - w + 1];
            int index = 0;
            int L = 0;
            int R = w - 1;
            while (R < N) {
                int max = arr[L];
                for (int i = L + 1; i <= R; i++) {
                    max = Math.max(max, arr[i]);

                }
                res[index++] = max;
                L++;
                R++;
            }
            return res;
        }

        @Override
        protected Integer[] test(SlidWindowMaxArray2Param data) {
//            return SLID_WINDOW_2_METHOD.test.cal(data);
            int w = data.w;
            int[] arr = data.arr;
            if(arr == null || arr.length<1 || arr.length<w){
                return null;
            }
            int index = 0;
            LinkedList<Integer> qmax = new LinkedList<>();
            Integer[] res = new Integer[arr.length-w+1];
            for(int R = 0; R < arr.length;R++){
                //如果双端队列不为空，才进入，而且，qmax最后一个位置(索引)，在arr中小于当前的arr[R]的时候，才进行循环处理
                //如果当前arr[R]上的值比qmax的尾巴上的值大，就把qmax尾巴上的值摘掉
                while(!qmax.isEmpty() && arr[qmax.peekLast()] <= arr[R]){
                    qmax.pollLast();
                }
                //如果前面的while没卡主，证明arr[R]是目前发现的其次大的，就怼到队尾
                qmax.addLast(R);
                //R-W是窗口的过期下标，如果过期下标是当前qmax的头的话，就把qmax的头踢出去
                //也就是窗口的左下标已经划过了队列的头，那么队列的头就需要被踢出去
                if(qmax.peekFirst()== R-w){
                    qmax.pollFirst();
                }
                if(R >= w-1){
                    /*
                    [1,2,3,4,5,6,7,8]
                         <...R>
                    当前R的位置是不是已经超过了W-1的为了，是在问是否已经形成了一个正常的窗口
                     */

                    //这句是在问，当前是否已经形成一个正常的窗口
                    //窗口的左边已经进入数组的左边界了，表示窗口已经形成开始收集答案
                    // R大于等于W -1表示窗口已经形成那么qmax取出的第一个值就是窗口内最大的值的下标。
                    // 这个下标对应数组中的位置就是我们要的答案
                    res[index++] = arr[qmax.peekFirst()];
                }
            }
            return res;
        }
    }

    /**
     * 给定一个整型数组arr，和一个整数num
     * 某个arr中的子数组sub，如果想达标，必须满足：
     * sub中最大值 – sub中最小值 <= num，
     * 返回arr中达标子数组的数量
     */
    @AlgName("滑动达标子数组")
    public static class SlidSubWindowMaxArray extends AlgCompImpl<Integer,SlidWindowMaxArray2Param>{
        public enum SlidSubWindowMaxArrayEnum{
            AI{
                @Override
                public Integer cal(SlidWindowMaxArray2Param data) {
                    int [] arr = data.arr;
                    int num = data.w;
                    int n = arr.length;
                    int res = 0;
                    Deque<Integer> q = new ArrayDeque<>();

                    // 遍历数组
                    for (int i = 0; i < n; i++) {
                        // 将不符合条件的值从队列中删除
                        while (!q.isEmpty() && arr[i] - arr[q.peekFirst()] > num) {
                            q.pollFirst();
                        }
                        // 插入新的值
                        while (!q.isEmpty() && arr[i] <= arr[q.peekLast()]) {
                            q.pollLast();
                        }
                        q.offerLast(i);
                        // 将窗口的左边界向右移动
                        if(i-num>0) {
                            while (arr[q.peekFirst()] - arr[i - num] > num) {
                                q.pollFirst();
                            }
                        }

                        // 统计结果
                        res += i - q.peekFirst() + 1;
                    }

                    return res;
                }
            };
            public abstract Integer cal(SlidWindowMaxArray2Param data);
        }
        SlidWindowMaxArray2 slidWindowMaxArray2 = new SlidWindowMaxArray2();
        @Override
        public SlidWindowMaxArray2Param prepare() {
            return slidWindowMaxArray2.prepare(2000);
        }

        @Override
        protected Integer standard(SlidWindowMaxArray2Param data) {
//            return SlidSubWindowMaxArrayEnum.AI.cal(data);
            int[] arr = data.arr;
            int sum = data.w;
            if (arr == null || arr.length == 0 || sum < 0) {
                return 0;
            }
            int N = arr.length;
            int count = 0;
            for (int L = 0; L < N; L++) {
                for (int R = L; R < N; R++) {
                    int max = arr[L];
                    int min = arr[L];
                    for (int i = L + 1; i <= R; i++) {
                        max = Math.max(max, arr[i]);
                        min = Math.min(min, arr[i]);
                    }
                    if (max - min <= sum) {
                        count++;
                    }
                }
            }
            return count;
            
        }

        @Override
        protected Integer test(SlidWindowMaxArray2Param data) {
            int w = data.w;
            int[] arr = data.arr;
            int N = arr.length;
            int count = 0;
            if (arr == null || arr.length == 0 || w < 0) {
                return 0;
            }
            LinkedList<Integer> maxWindow = new LinkedList<>();
            LinkedList<Integer> minWindow = new LinkedList<>();
            int R = 0;
            for(int L = 0; L < N;L++){
                
                    while (R < N){

                        while (!maxWindow.isEmpty() && arr[maxWindow.peekLast()] <= arr[R]){
                            maxWindow.pollLast();
                        }
                        maxWindow.addLast(R);
                        while (!minWindow.isEmpty() && arr[minWindow.peekLast()] >= arr[R]){
                            minWindow.pollLast();
                        }
                        minWindow.addLast(R);
                        if(arr[maxWindow.peekFirst()] - arr[minWindow.peekFirst()] > w){
                            break;
                        }else{
                            R++;
                        }
                    }
                    count += R-L;
                    if(maxWindow.peekFirst() == L){
                        maxWindow.pollFirst();
                    }
                    if(minWindow.peekFirst() == L){
                        minWindow.pollFirst();
                    }
                
            }
            return count;
        }
    }
}
