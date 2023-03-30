package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import com.danebrown.algtech.standard.HeapGreater;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Triple;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by danebrown on 2021/8/23
 * mail: tain198127@163.com
 * 堆排序 非常重要
 *
 * @author danebrown
 */
//todo 需要继续完成
@Log4j2
public class HeapSortStaf {
    public static void main(String[] args) {

        AlgCompMenu.addComp(new HeapOp());
        AlgCompMenu.addComp(new HeapSort());
        AlgCompMenu.addComp(new MaxLineCoincidence());
        AlgCompMenu.addComp(new EnhanceHeap());
        AlgCompMenu.run();
    }

    @AlgName("优先级队列")
    public static class HeapOp extends AlgCompImpl<int[], Integer[]> {
        int heapSize = 0;

        /**
         * 数字表示add，null表示poll
         *
         * @return
         */
        @Override
        public Integer[] prepare(long range) {
            int dataSize = ThreadLocalRandom.current().nextInt(200, 500);
            Integer[] data = new Integer[dataSize + 1];
            for (int i = 0; i < dataSize; i++) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    data[i] = null;
                } else {
                    data[i] = ThreadLocalRandom.current().nextInt();
                }
            }
            return data;
            //            return new Integer[]{1, 2, null, 4, 5, 9, 2, 5, null, 9};
        }

        @Override
        protected int[] standard(Integer[] data) {
            PriorityQueue<Integer> queue = new PriorityQueue<>(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o2 - o1;
                }
            });
            for (int i = 0; i < data.length; i++) {
                if (data[i] == null) {
                    queue.poll();
                } else {
                    queue.add(data[i]);
                }
            }
            return queue.stream().mapToInt(Integer::intValue).toArray();
        }

        @Override
        protected int[] test(Integer[] data) {
            heapSize = 0;
            //            Integer[] heap = new Integer[data.length];
            for (int i = 0; i < data.length; i++) {
                if (data[i] == null) {
                    heapIfy(data, 0, heapSize);
                    if (heapSize >= 1) {
                        heapSize--;
                    }
                } else {
                    heapInsert(data, i);
                    heapSize++;
                }
            }

            return Arrays.stream(data, 0, heapSize).mapToInt(Integer::intValue).toArray();
        }
        //这个本质上就是个优先级队列

        /**
         * 大根堆插入
         *
         * @param data
         * @param curIdx
         */
        public void heapInsert(Integer[] data, int curIdx) {

            while (data[curIdx] > data[(curIdx - 1) / 2]) {
                swap(data, curIdx, (curIdx - 1) / 2);
                curIdx = (curIdx - 1) / 2;
            }


        }

        /**
         * 给出最大的值并返回，然后重新对heap进行大根堆化
         *
         * @param data
         * @return
         */
        public void heapIfy(Integer[] data, int idx, int heapSize) {
            int left = idx * 2 + 1;
            while (left < heapSize) {
                int largest = left + 1 < heapSize && data[left + 1] > data[left] ? left + 1 : left;
                largest = data[largest] > data[idx] ? largest : idx;
                if (largest == idx) {
                    break;
                }
                swap(data, largest, idx);
                idx = largest;
                left = idx * 2 + 1;
            }

        }

        public void swap(Integer[] data, int from, int to) {
            Integer tmp = data[from];
            data[from] = data[to];
            data[to] = tmp;
        }
    }

    @AlgName("堆排序")
    public static class HeapSort extends AlgCompImpl<int[], int[]> {
        @Override
        public int[] prepare(long range) {
            int dataSize = ThreadLocalRandom.current().nextInt(20000, 1000000);
            int[] data = new int[dataSize + 1];
            for (int i = 0; i < dataSize; i++) {
                data[i] = ThreadLocalRandom.current().nextInt();
            }
            return data;

            //            return new int[]{3,1,2};
        }

        @Override
        protected int[] standard(int[] data) {
            Arrays.sort(data);
            return data;
        }

        @Override
        protected int[] test(int[] data) {
            if (data == null || data.length < 2) {
                return data;
            }
            int heapSize = data.length;

            //            //自顶向下的建堆(N log N)时间复杂度
            //            for(int i = 0; i < data.length; i++){
            //                heapInsert(data,i);
            //            }
            //自下向上建堆，O(N)时间复杂度
            for (int i = data.length - 1; i >= 0; i--) {
                heapIfy(data, i, heapSize);
            }
            swap(data, 0, --heapSize);
            while (heapSize > 0) {
                //向下沉，(NlogN)时间复杂度
                heapIfy(data, 0, heapSize);
                swap(data, 0, --heapSize);
            }
            return data;

        }

        public void heapInsert(int[] data, int i) {
            while (data[i] > data[(i - 1) / 2]) {
                swap(data, i, (i - 1) / 2);
                i = (i - 1) / 2;
            }
        }

        public void heapIfy(int[] data, int idx, int heapSize) {
            int left = idx * 2 + 1;
            while (left < heapSize) {
                int largest = left + 1 < heapSize && data[left + 1] > data[left] ? left + 1 : left;
                largest = data[largest] > data[idx] ? largest : idx;
                if (largest == idx) {
                    break;
                }
                swap(data, largest, idx);
                idx = largest;
                left = idx * 2 + 1;
            }

        }

        public void swap(int[] data, int from, int to) {
            int tmp = data[from];
            data[from] = data[to];
            data[to] = tmp;
        }
    }

    /**
     * 给了很多线段、每个线段都有两个数[START,END]，表示开始和结束
     * 前提假设：线段开始和结束都是整数
     * 线段重合度必须>=1
     * 问：返回线段最多重合区域中，包含了几条线段
     * 例如：[1,3],[2,7]，那么2-3之间就是重合的
     * 再例如 [4,7],[7,9]，那么这两个线段就不是重合的
     * 核心思想：
     * 1：先根据开始位置对线段进行排序
     * 2：用小根堆比较后续线段中开始的位置。如果后续进来的线段的开始位置0大于小根堆的堆顶元素，就一直弹出
     * 3：弹到后续进来的线段开始位置小于堆内的值（就是之前线段的结束位置）把剩下的这个线段的结束位置压进小根堆
     * 4：最后全都弄完以后，小根堆里面有几条线段，最大重合线段就是几条
     */
    @AlgName("最大线段重合数")
    public static class MaxLineCoincidence extends AlgCompImpl<Integer, int[][]> {
        private static int ORIGIN = 200;
        private static int BOUND = 3000;

        @Override
        public int[][] prepare(long range) {
            int dataSize = ThreadLocalRandom.current().nextInt(ORIGIN, BOUND);
            int[][] data = new int[dataSize][2];
            for (int i = 0; i < dataSize; i++) {
                data[i][0] = ThreadLocalRandom.current().nextInt(0, BOUND);
                data[i][1] = data[i][0] + ThreadLocalRandom.current().nextInt(1, BOUND);
            }
            return data;
        }

        @Override
        protected Integer standard(int[][] data) {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < data.length; i++) {
                list.add(data[i][0]);
                list.add(data[i][1]);
            }
            int min = list.stream().min(Comparator.comparingInt(o -> o)).get();
            int max = list.stream().max(Comparator.comparingInt(o -> o)).get();
            int maxLine = 0;
            for (double i = min; i < max; i = i + 0.5) {
                int tmpMax = 0;
                for (int j = 0; j < data.length; j++) {
                    if (i > (double) data[j][0] && i < (double) data[j][1]) {
                        tmpMax++;
                    }
                }
                maxLine = Math.max(maxLine, tmpMax);
            }
            return maxLine;
        }

        @Override
        protected Integer test(int[][] data) {
            List<int[]> tmp = Arrays.stream(data).sorted(Comparator.comparingInt(o -> o[0])).collect(Collectors.toList());
            PriorityQueue<Integer> heap = new PriorityQueue<>();
            int max = 0;
            for (int[] line : tmp) {
                //line中0表示开始的位置，1表示结束的位置
                while (!heap.isEmpty() && heap.peek() <= line[0]) {//这里很容易错
                    heap.poll();
                }
                heap.add(line[1]);
                max = Math.max(max, heap.size());
            }
            return max;
        }

    }

    /**
     * TODO【尚未实现】
     * 加强堆，主要实现了反向索引
     */
    @AlgName("加强堆")
    public static class EnhanceHeap extends AlgCompImpl<int[], Integer[]> {
        private ArrayList<Integer> heap = new ArrayList<>();
        private HashMap<Integer,Integer> indexMap = new HashMap<>();

        //小根堆
        HeapGreater<Integer> heapGreater = new HeapGreater<>(Comparator.comparingInt(o -> o));

        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();

        /**
         *
         * @return null表示pop,数字表示push
         */
        @Override
        public Integer[] prepare(long range) {
            int dataSize = ThreadLocalRandom.current().nextInt(200, 500);
            Integer[] data = new Integer[dataSize + 1];
            for (int i = 0; i < dataSize; i++) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    data[i] = null;
                } else {
                    data[i] = ThreadLocalRandom.current().nextInt();
                }
            }
            return data;
        }

        @Override
        protected int[] standard(Integer[] data) {
            for (int i = 0; i < data.length; i++) {
                if(data[i] == null){
                    heapGreater.pop();
                }
                else{
                    heapGreater.push(data[i]);
                }
            }
            for (int i=0;i < data.length;i++){
                if (i %2 ==0){
//                    heapGreater.resign();
                }
            }
            return heapGreater.getAllElements().stream().mapToInt(Integer::intValue).toArray();
        }

        @Override
        protected int[] test(Integer[] data) {
            for (int i = 0; i < data.length; i++) {
                if(data[i] == null){
                    priorityQueue.poll();
                }
                else{
                    priorityQueue.add(data[i]);
                }
            }
            for (int i=0;i < data.length;i++){
                if (i %2 ==0){
                    //                    heapGreater.resign();
                }
            }
            return priorityQueue.stream().mapToInt(Integer::intValue).toArray();
        }
        private void add(){

        }
        private void pop(){

        }
        private void resign(){

        }
        private void remove(){

        }
    }
    //todo UNFINISH
    /**
     * 一对arr[i]和op[i]就代表一个事件：
     * 用户号为arr[i]，op[i] == T就代表这个用户购买了一件商品
     * op[i] == F就代表这个用户退货了一件商品
     * 现在你作为电商平台负责人，你想在每一个事件到来的时候，
     * 都给购买次数最多的前K名用户颁奖。
     * 所以每个事件发生后，你都需要一个得奖名单（得奖区）。
     *
     * 得奖系统的规则：
     * 1，如果某个用户购买商品数为0，但是又发生了退货事件，
     *      则认为该事件无效，得奖名单和上一个事件发生后一致，例子中的5用户
     * 2，某用户发生购买商品事件，购买商品数+1，发生退货事件，购买商品数-1
     * 3，每次都是最多K个用户得奖，K也为传入的参数
     *       如果根据全部规则，得奖人数确实不够K个，那就以不够的情况输出结果
     * 4，得奖系统分为得奖区和候选区，任何用户只要购买数>0，
     *       一定在这两个区域中的一个
     * 5，购买数最大的前K名用户进入得奖区，
     *       在最初时如果得奖区没有到达K个用户，那么新来的用户直接进入得奖区
     * 6，如果购买数不足以进入得奖区的用户，进入候选区
     * 7，如果候选区购买数最多的用户，已经足以进入得奖区，
     *      该用户就会替换得奖区中购买数最少的用户（大于才能替换），
     *      如果得奖区中购买数最少的用户有多个，就替换最早进入得奖区的用户
     *      如果候选区中购买数最多的用户有多个，机会会给最早进入候选区的用户
     * 8，候选区和得奖区是两套时间，
     *      因用户只会在其中一个区域，所以只会有一个区域的时间，另一个没有
     *      从得奖区出来进入候选区的用户，得奖区时间删除，
     *      进入候选区的时间就是当前事件的时间（可以理解为arr[i]和op[i]中的i）
     *      从候选区出来进入得奖区的用户，候选区时间删除，
     *      进入得奖区的时间就是当前事件的时间（可以理解为arr[i]和op[i]中的i）
     * 9，如果某用户购买数==0，不管在哪个区域都离开，区域时间删除，
     *      离开是指彻底离开，哪个区域也不会找到该用户
     *      如果下次该用户又发生购买行为，产生>0的购买数，
     *      会再次根据之前规则回到某个区域中，进入区域的时间重记
     * 请遍历arr数组和op数组，遍历每一步输出一个得奖名单
     * public List<List<Integer>>  topK (int[] arr, boolean[] op, int k)
     *
     * 为什么要手写堆，因为很多功能系统提供的堆是无法实现的
     * 原因：1、在计算过程中改了堆排序中某个元素的属性，这个属性参与排序
     * 2、性能不够，因为系统的堆没有提供反向索引表。
     * 3、java中没有提供反向索引表，原因是增加反向索引表会增加内存的消耗。而且java偏应用多，使用堆的场景大部分都是简单的插入弹出
     * 没有动力改。但是C++的委员会提供的包里面有反向索引表
     */
    /**
     * @implNote List<List < Integer> 每一个时刻的topK
     * @implNote Triple<int [ ], boolean [ ], Integer> left:够没产品的用户ID，middle:操作:True为购买,
     * False为退货，Right:topK
     */
    public static class TopKReward extends AlgCompImpl<List<List<Integer>>, Triple<int[], boolean[], Integer>> {

        /**
         * @return Triple<int [ ], boolean [ ], Integer> left:够没产品的用户ID，middle:操作:True为购买,
         * False为退货，Right:topK
         */
        @Override
        public Triple<int[], boolean[], Integer> prepare(long range) {
            return null;
        }

        /**
         * @param data Triple<int [ ], boolean [ ], Integer> left:够没产品的用户ID，middle:操作:True为购买,
         *             False为退货，Right:topK
         * @return List<List < Integer> 每一个时刻的topK
         */
        @Override
        protected List<List<Integer>> standard(Triple<int[], boolean[], Integer> data) {
            return null;
        }

        /**
         * @param data Triple<int [ ], boolean [ ], Integer> left:够没产品的用户ID，middle:操作:True为购买,
         *             False为退货，Right:topK
         * @return List<List < Integer> 每一个时刻的topK
         */
        @Override
        protected List<List<Integer>> test(Triple<int[], boolean[], Integer> data) {
            return null;
        }


    }



}
