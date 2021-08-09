package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2021/8/2
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
public class MergeSort {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new RecursionMergeSort());
        AlgCompMenu.addComp(new WhileMergeSort());
        AlgCompMenu.addComp(new LeftMinSum());
        AlgCompMenu.addComp(new ReversSortPair());
        AlgCompMenu.addComp(new BiggerThanRightTwice());
        AlgCompMenu.run();
    }
    @AlgName("递归归并排序[完成]")
    public static class RecursionMergeSort extends AlgCompImpl<int[],int[]> {

        @Override
        public int[] prepare() {
            int dataSize = ThreadLocalRandom.current().nextInt(2, 500000);
            int [] data = new int[dataSize];
            for(int i=0;i < dataSize; i++){
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
        private void mergeSort(int[] data){
            mergeSort(data,0,data.length-1);//这里容易出错
        }
        private void mergeSort(int[] data,int L,int R){
            if(data == null || data.length <2||L ==R){
                return;
            }
            /**
             * 这里很容易出错
             */
            int middle = L + ((R-L)>>1);//L + (R-L)/2 = R/2 + L/2=(R+L)/2
            mergeSort(data,L,middle);
            mergeSort(data,middle+1,R);//这里容易出错
            merge(data,L,middle,R);
        }
        private void merge(int[]data,int L,int M, int R){
            int i=0;
            int[] tmp = new int[R-L+1];
            int l = L;
            int r = M+1;
            while (l <= M && r <= R){//这里容易出错
                tmp[i++] = data[l] <= data[r]?data[l++]:data[r++];//谁大用谁
            }
            while (l <= M){
                tmp[i++] = data[l++];//
            }
            while (r <= R){
                tmp[i++] = data[r++];
            }
            for(i=0;i<tmp.length;i++){
                data[L+i] = tmp[i];
            }
        }
    }

    /**
     * 循环版归并排序 第五章
     */
    @AlgName("循环归并排序")
    public static class WhileMergeSort extends AlgCompImpl<int[],int[]>{

        RecursionMergeSort recursionMergeSort  = new RecursionMergeSort();
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
        private void whileSort(int[] data){
            if(data == null||data.length<2) {
                return;
            }
            int N = data.length;
            int mergeSize=1;
            while (mergeSize < N){
                int L = 0;//当前左组的第一个位置
                while(L<N)   //这里很容易忘记
                {
                    int M = L+mergeSize -1;  //这里容易错
                    if(M >= N){   //这里容易漏掉
                        break;
                    }
                    int R = Math.min(M+mergeSize,N-1);   //这里容易错
                    merge(data,L,M,R);
                    L=R+1; //这里容易漏掉
                }
                if(mergeSize > N/2){   //这里容易漏掉
                    break;
                }
                mergeSize <<= 1;

            }
        }
        private void merge(int[] data,int l,int m,int r){
            int p1= l;
            int p2 = m+1;
            int[] tmp = new int[r-l+1];
            int i = 0;
            while (p1 <= m && p2 <= r){
                tmp[i++] = data[p1]<=data[p2]?data[p1++]:data[p2++];
            }
            while (p1 <= m){
                tmp[i++] = data[p1++];
            }
            while (p2 <= r){
                tmp[i++] = data[p2++];
            }
            for(i=0;i<tmp.length;i++){
                data[i+l] = tmp[i];
            }
        }
    }

    /**
     *  第五章
     */
    @AlgName("小和")
    public static class LeftMinSum extends AlgCompImpl<Integer,int[]>{
        RecursionMergeSort recursionMergeSort = new RecursionMergeSort();
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
     *  第五章
     */
    @AlgName("逆序对计算，经典考题")
    public static class ReversSortPair extends AlgCompImpl<Integer,int[]>{

        @Override
        public int[] prepare() {
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
     *  第五章
     */
    @AlgName("大于右侧数2倍")
    public static class BiggerThanRightTwice extends AlgCompImpl<Integer,int[]>{
        private RecursionMergeSort recursionMergeSort =
                new RecursionMergeSort();
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
    public static class CountOfRangeSum extends AlgCompImpl<Integer,
            Triple<int[],Integer,Integer>>{

        RecursionMergeSort recursionMergeSort = new RecursionMergeSort();
        @Override
        public Triple<int[], Integer, Integer> prepare() {
           int[] arr = recursionMergeSort.prepare();
           int low = ThreadLocalRandom.current().nextInt();
           int high = ThreadLocalRandom.current().nextInt(low,arr.length+low);
           return Triple.of(arr,low,high);
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
