package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2021/8/2
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
@Log4j2
public class MergeSort {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new RecursionMergeSort());
        AlgCompMenu.addComp(new WhileMergeSort());
        AlgCompMenu.addComp(new RecLeftMinSum());
        AlgCompMenu.addComp(new LeftMinSum());
        AlgCompMenu.addComp(new ReversSortPair());
        AlgCompMenu.addComp(new BiggerThanRightTwice());
        AlgCompMenu.run();
    }

    @AlgName("递归归并排序[完成]")
    public static class RecursionMergeSort extends AlgCompImpl<int[], int[]> {

        @Override
        public int[] prepare() {
            int dataSize = ThreadLocalRandom.current().nextInt(2, 500000);
            int[] data = new int[dataSize];
            for (int i = 0; i < dataSize; i++) {
                data[i] = ThreadLocalRandom.current().nextInt();
            }
            return data;
        }

        @Override
        protected int[] standard(int[] data) {
            Arrays.sort(data);
            return data;
        }

        @Override
        protected int[] test(int[] data) {
            mergeSort(data);
            return data;
        }

        private void mergeSort(int[] data) {
            mergeSort(data, 0, data.length - 1);//这里容易出错
        }

        private void mergeSort(int[] data, int L, int R) {
            if (data == null || data.length < 2 || L == R) {
                return;
            }
            /**
             * 这里很容易出错
             */
            int middle = L + ((R - L) >> 1);//L + (R-L)/2 = R/2 + L/2=(R+L)/2
            mergeSort(data, L, middle);
            mergeSort(data, middle + 1, R);//这里容易出错
            merge(data, L, middle, R);
        }

        private void merge(int[] data, int L, int M, int R) {
            int i = 0;
            int[] tmp = new int[R - L + 1];
            int l = L;
            int r = M + 1;
            while (l <= M && r <= R) {//这里容易出错
                tmp[i++] = data[l] <= data[r] ? data[l++] : data[r++];//谁大用谁
            }
            while (l <= M) {
                tmp[i++] = data[l++];//
            }
            while (r <= R) {
                tmp[i++] = data[r++];
            }
            for (i = 0; i < tmp.length; i++) {
                data[L + i] = tmp[i];
            }
        }
    }

    /**
     * 循环版归并排序 第五章
     */
    @AlgName("循环归并排序")
    public static class WhileMergeSort extends AlgCompImpl<int[], int[]> {

        RecursionMergeSort recursionMergeSort = new RecursionMergeSort();

        @Override
        public int[] prepare() {
            return recursionMergeSort.prepare();
        }

        @Override
        protected int[] standard(int[] data) {
            return recursionMergeSort.standard(data);
        }

        @Override
        protected int[] test(int[] data) {
            whileSort(data);
            return data;
        }

        private void whileSort(int[] data) {
            if (data == null || data.length < 2) {
                return;
            }
            int N = data.length;
            int mergeSize = 1;
            while (mergeSize < N) {
                int L = 0;//当前左组的第一个位置
                while (L < N) {//这里很容易忘记
                    int M = L + mergeSize - 1;  //这里容易错
                    if (M >= N) {   //这里容易漏掉
                        break;
                    }
                    int R = Math.min(M + mergeSize, N - 1);   //这里容易错
                    merge(data, L, M, R);
                    L = R + 1; //这里容易漏掉
                }
                if (mergeSize > N / 2) {   //这里容易漏掉
                    break;
                }
                mergeSize <<= 1;

            }
        }

        private void merge(int[] data, int l, int m, int r) {
            int p1 = l;
            int p2 = m + 1;
            int[] tmp = new int[r - l + 1];
            int i = 0;
            while (p1 <= m && p2 <= r) {
                tmp[i++] = data[p1] <= data[p2] ? data[p1++] : data[p2++];
            }
            while (p1 <= m) {
                tmp[i++] = data[p1++];
            }
            while (p2 <= r) {
                tmp[i++] = data[p2++];
            }
            for (i = 0; i < tmp.length; i++) {
                data[i + l] = tmp[i];
            }
        }
    }

    /**
     * 第五章
     */
    /**
     * 在一个数组中，一个数左边比它小的数的总和，叫数的小和，所有数的小和累加起来，叫数组小和。求数组小和。
     * 例子： [1,3,4,2,5]
     * 1左边比1小的数：没有
     * 3左边比3小的数：1
     * 4左边比4小的数：1、3
     * 2左边比2小的数：1
     * 5左边比5小的数：1、3、4、 2
     * 所以数组的小和为1+1+3+1+1+3+4+2=16
     */
    @AlgName("递归版本最小和")
    public static class RecLeftMinSum extends AlgCompImpl<Integer, int[]> {
        @Override
        public int[] prepare() {
            int dataSize = ThreadLocalRandom.current().nextInt(2, 50000);
            int[] data = new int[dataSize];
            for (int i = 0; i < dataSize; i++) {
                data[i] = ThreadLocalRandom.current().nextInt();
            }
            return data;
        }

        @Override
        protected Integer standard(int[] data) {
            int ret = 0;
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < i; j++) {
                    if (data[j] < data[i]) {
                        ret += data[j];
                    }
                }
            }

            return ret;
        }

        @Override
        protected Integer test(int[] data) {
            int result = mergeSort(data, 0, data.length - 1);
            //            log.info("左侧最小和结果:{}", result);
            //            log.info("{}",data);
            return result;
        }

        private int mergeSort(int[] data, int l, int r) {
            int result = 0;

            if (l >= r) {
                return result;
            }
            int m = l + ((r - l) >> 2);
            result += mergeSort(data, l, m);
            result += mergeSort(data, m + 1, r);
            result += merge(data, l, m, r);
            return result;

        }

        private int merge(int[] data, int l, int m, int r) {
            int[] tmp = new int[r - l + 1];
            int p1 = l;
            int p2 = m + 1;
            int i = 0;
            int result = 0;
            while (p1 <= m && p2 <= r) {
                //左组小的时候产生小和.这句是精髓.把
                /**
                 * 把计算小和数的问题，转化成了，右边有几个比这个小数大的。有几个比他大的就乘以几次。
                 * 之所以能乘以几次，是因为每一次左组的时候，都已经是有序的了。也就是说，一旦发生了
                 * 左组数小，那么一定可以判定，右边的都比这个数大。
                 * 每一次递归，只算本次递归内的。下次递归计算下次的，这样算法累加起来，就把数组上所有
                 * 比这个数大的记录下来了
                 * 本质上是因为无论左侧还是右侧的数组，已经是有序的了。利用了上次计算的结果
                 */
                if (data[p1] < data[p2]) {
                    result += data[p1] * (r - p2 + 1);
                }
                tmp[i++] = data[p1] < data[p2] ? data[p1++] : data[p2++];
            }
            while (p1 <= m) {
                //                result += data[p1];
                tmp[i++] = data[p1++];
            }
            while (p2 <= r) {
                tmp[i++] = data[p2++];
            }
            for (i = 0; i < tmp.length; i++) {
                data[i + l] = tmp[i];
            }
            return result;
        }
    }

    @AlgName("非递归版本最小和")
    public static class LeftMinSum extends AlgCompImpl<Integer, int[]> {
        private RecLeftMinSum recLeftMinSum = new RecLeftMinSum();

        @Override
        public int[] prepare() {
            //            return new int[]{2,3,5,1,2,8,0,7,4};
            return recLeftMinSum.prepare();
        }

        @Override
        protected Integer standard(int[] data) {
            return recLeftMinSum.test(data);
        }

        @Override
        protected Integer test(int[] data) {
            int result = 0;

            if (data == null || data.length < 2) {
                return result;
            }
            int N = data.length;
            int step = 1;
            while (step < N) {
                int L = 0;
                while (L <= N) {
                    int M = L + step - 1;
                    if (M >= N) {
                        break;
                    }
                    int R = Math.min(M + step, N - 1);

                    result += merge(data, L, M, R);
                    L = R + 1;
                }
                if (step > N / 2) {
                    break;
                }
                step <<= 1;
            }


            //            log.info("排序结果:{}",data);
            return result;
        }

        protected int merge(int[] data, int l, int m, int r) {
            int p1 = l;
            int p2 = m + 1;
            int i = 0;
            int[] tmp = new int[r - l + 1];
            int result = 0;
            while (p1 <= m && p2 <= r) {
                result += data[p1] < data[p2] ? (data[p1] * (r - p2 + 1)) : 0;
                tmp[i++] = data[p1] < data[p2] ? data[p1++] : data[p2++];
            }
            while (p1 <= m) {
                tmp[i++] = data[p1++];
            }
            while (p2 <= r) {
                tmp[i++] = data[p2++];
            }
            for (i = 0; i < tmp.length; i++) {
                data[i + l] = tmp[i];
            }
            return result;
        }
    }

    /**
     * 第五章
     * 如果一个数组中，左边的数和右边的数组成逆序，我们称之为逆序对
     * 例如[3,1,0,4,3,1]
     * 其中
     * [3,1][3,0][3,1]
     * [1,0]
     * [4,3][4,1]
     * 都是逆序对
     * 这道题的本质就是在问
     * 右边有多少个数比他小
     * 思路：merge中从右往左对比拷贝，相等继续，当出现左边的数比右边的大时，右边有几个数，就表示有几个
     * 逆序对。因为在merge过程中，两边的数组本身已经有序了。利用的就是有序的特性
     * <p>
     * 问有几个逆序对
     */
    @AlgName("逆序对计算，经典考题")
    public static class ReversSortPair extends AlgCompImpl<Integer, int[]> {
        RecursionMergeSort recursionMergeSort = new RecursionMergeSort();

        @Override
        public int[] prepare() {
            int dataSize = ThreadLocalRandom.current().nextInt(2, 5000);
            int[] data = new int[dataSize];
            for (int i = 0; i < dataSize; i++) {
                data[i] = ThreadLocalRandom.current().nextInt();
            }
            return data;
        }

        @Override
        protected Integer standard(int[] data) {
            int count = 0;
            for (int i = 0; i < data.length; i++) {
                for (int j = i + 1; j < data.length; j++) {
                    if (data[i] > data[j]) {
                        count++;
                    }
                    //                    else{
                    //                        break;
                    //                    }
                }
            }
            return count;
            //            log.info("标准逆序对个数{}",count);
            //             recursionMergeSort.mergeSort(data);
            //             return data;
        }

        @Override
        protected Integer test(int[] data) {
            int result = mergeSort(data, 0, data.length - 1);
            //            log.info("测试逆序对个数:{}", result);
            //            return data;
            //            return result;
            return result;
        }

        public int mergeSort(int[] data, int l, int r) {
            int result = 0;
            if (l >= r || data == null || data.length < 2) {
                return result;
            }
            int m = l + ((r - l) >> 2);
            result += mergeSort(data, l, m);
            result += mergeSort(data, m + 1, r);
            result += merge(data, l, m, r);
            return result;
        }

        /**
         * 逆序对归并算法
         *
         * @param data 原始数组
         * @param l    左下标
         * @param m    中值下标
         * @param r    又下标
         * @return 逆序对个数
         */
        public int merge(int[] data, int l, int m, int r) {
            int i = r - l;
            int p1 = m;
            int p2 = r;
            int[] tmp = new int[r - l + 1];
            int result = 0;
            while (l <= p1 && (m + 1) <= p2) {
                if (data[p1] > data[p2]) {
                    result += (p2 - m);
                }
                tmp[i--] = data[p2] < data[p1] ? data[p1--] : data[p2--];
            }
            while (l <= p1) {
                tmp[i--] = data[p1--];
            }
            while (m + 1 <= p2) {
                tmp[i--] = data[p2--];
            }
            for (i = 0; i < tmp.length; i++) {
                data[i + l] = tmp[i];
            }
            return result;
        }
    }

    /**
     * 第五章
     */
    @AlgName("大于右侧数2倍")
    public static class BiggerThanRightTwice extends AlgCompImpl<Integer, int[]> {
        private RecursionMergeSort recursionMergeSort = new RecursionMergeSort();

        @Override
        public int[] prepare() {
            return recursionMergeSort.prepare();
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
     * 给定一个数组，和最大值、最小值，数组中有多少个子数组累加的和在[low,high]内
     */
    @AlgName("滑动范围子数组")
    public static class CountOfRangeSum extends AlgCompImpl<Integer, Triple<int[], Integer, Integer>> {

        RecursionMergeSort recursionMergeSort = new RecursionMergeSort();

        @Override
        public Triple<int[], Integer, Integer> prepare() {
            int[] arr = recursionMergeSort.prepare();
            int low = ThreadLocalRandom.current().nextInt();
            int high = ThreadLocalRandom.current().nextInt(low, arr.length + low);
            return Triple.of(arr, low, high);
        }

        @Override
        protected Integer standard(Triple<int[], Integer, Integer> data) {
            return null;
        }

        @Override
        protected Integer test(Triple<int[], Integer, Integer> data) {
            return null;
        }
    }
}
