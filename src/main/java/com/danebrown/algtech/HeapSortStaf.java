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
            int dataSize = ThreadLocalRandom.current().nextInt(20000, 1000000);
            Integer[] data = new Integer[dataSize + 1];
            for (int i = 0; i < dataSize; i++) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    data[i] = null;
                } else {
                    data[i] = ThreadLocalRandom.current().nextInt(0, 10);
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
            Integer[] heap = new Integer[data.length];
            for (int i = 0; i < data.length; i++) {
                if (data[i] == null) {
                    heapIfy(heap, 0, heapSize);
                    if (heapSize >= 1) {
                        heapSize--;
                    }
                } else {
                    heapInsert(heap, data[i], heapSize);
                    heapSize++;
                }
            }

            return Arrays.stream(heap, 0, heapSize).mapToInt(Integer::intValue).toArray();
        }
        //这个本质上就是个优先级队列

        /**
         * 大根堆插入
         *
         * @param data
         * @param insert
         */
        public void heapInsert(Integer[] data, int insert, int idx) {
            int curIdx = idx;
            data[curIdx] = insert;
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
        public Integer heapIfy(Integer[] data, int idx, int heapSize) {
            if (heapSize < 1) {
                return null;
            }
            //            int idx = 0;//堆顶
            swap(data, idx, heapSize - 1);
            heapSize--;
            int ret = data[idx];
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

            return ret;

        }

        public void swap(Integer[] data, int from, int to) {
            Integer tmp = data[from];
            data[from] = data[to];
            data[to] = tmp;
        }
    }


}
