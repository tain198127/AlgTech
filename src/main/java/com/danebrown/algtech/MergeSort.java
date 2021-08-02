package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;

/**
 * Created by danebrown on 2021/8/2
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
public class MergeSort {
    public static void main(String[] args) {
        RecursionMergeSort recursionMergeSort = new RecursionMergeSort();
        recursionMergeSort.compare("递归归并排序");
    }
    public static class RecursionMergeSort extends AlgCompImpl<int[],int[]> {

        @Override
        public int[] prepare() {
            return new int[0];
        }

        @Override
        protected int[] standard(int[] data) {
            return new int[0];
        }

        @Override
        protected int[] test(int[] data) {
            return new int[0];
        }
    }
}
