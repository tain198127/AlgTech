package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2021/8/23
 * mail: tain198127@163.com
 * 堆排序
 *
 * @author danebrown
 */
public class HeapSortStaf {
    public static void main(String[] args) {

        AlgCompMenu.addComp(new HeapOp());
        AlgCompMenu.addComp(new HeapSort());
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


}
