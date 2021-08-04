package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;

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
        AlgCompMenu.run();
    }
    @AlgName("递归归并排序")
    public static class RecursionMergeSort extends AlgCompImpl<int[],int[]> {

        @Override
        public int[] prepare() {
            int dataSize = ThreadLocalRandom.current().nextInt(200000, 500000);
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
            while (l <= M && r <= R){
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
}
