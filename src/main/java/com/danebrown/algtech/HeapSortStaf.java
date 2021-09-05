package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by danebrown on 2021/8/23
 * mail: tain198127@163.com
 * 堆排序
 *
 * @author danebrown
 */
@Log4j2
public class HeapSortStaf {
    public static void main(String[] args) {

        AlgCompMenu.addComp(new HeapOp());
        AlgCompMenu.addComp(new HeapSort());
        AlgCompMenu.addComp(new MaxLineCoincidence());
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
        public Integer[] prepare() {
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
        public int[] prepare() {
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
            if(data == null || data.length < 2){
                return data;
            }
            int heapSize = data.length;

//            //自顶向下的建堆(N log N)时间复杂度
//            for(int i = 0; i < data.length; i++){
//                heapInsert(data,i);
//            }
            //自下向上建堆，O(N)时间复杂度
            for(int i = data.length-1; i >=0; i--){
                heapIfy(data,i,heapSize);
            }
            swap(data,0,--heapSize);
            while (heapSize > 0){
                //向下沉，(NlogN)时间复杂度
                heapIfy(data,0,heapSize);
                swap(data,0,--heapSize);
            }
            return data;

        }
        public void heapInsert(int[] data, int i){
            while (data[i] > data[(i -1)/2]){
                swap(data,i, (i -1)/2);
                i = (i -1)/2;
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
        public void swap(int[] data, int from, int to){
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
     */
    @AlgName("最大线段重合数")
    public static class MaxLineCoincidence extends AlgCompImpl< Integer,int[][]>{
        private static int ORIGIN=200;
        private static int BOUND=3000;

        @Override
        public int[][] prepare() {
            int dataSize = ThreadLocalRandom.current().nextInt(ORIGIN, BOUND);
            int[][] data = new int[dataSize][2];
            for (int i = 0; i < dataSize; i++) {
                data[i][0] = ThreadLocalRandom.current().nextInt(0,BOUND);
                data[i][1] =
                        data[i][0] + ThreadLocalRandom.current().nextInt(1,
                                BOUND);
            }
            return data;
        }

        @Override
        protected Integer standard(int[][] data) {
            List<Integer> list = new ArrayList<>();
            for(int i=0;i < data.length;i++){
                list.add(data[i][0]);
                list.add(data[i][1]);
            }
            int min = list.stream().min(Comparator.comparingInt(o -> o)).get();
            int max = list.stream().max(Comparator.comparingInt(o -> o)).get();
            int maxLine = 0;
            for(double i = min;i < max;i=i+0.5){
                int tmpMax = 0;
                for(int j = 0; j < data.length; j++){
                    if(i > (double) data[j][0] && i < (double)data[j][1]){
                        tmpMax++;
                    }
                }
                maxLine = Math.max(maxLine,tmpMax);
            }
            return maxLine;
        }

        @Override
        protected Integer test(int[][] data) {
            List<int[]> tmp =
                    Arrays.stream(data).sorted(Comparator.comparingInt(o -> o[0])).collect(Collectors.toList());
            PriorityQueue<Integer> heap = new PriorityQueue<>();
            int max = 0;
            for(int[] line:tmp){
                while (!heap.isEmpty() && heap.peek()<= line[0]){//这里很容易错
                    heap.poll();
                }
                heap.add(line[1]);
                max = Math.max(max,heap.size());
            }
            return max;
        }

    }

}
