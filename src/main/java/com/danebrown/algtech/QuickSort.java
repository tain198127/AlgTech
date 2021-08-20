package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by danebrown on 2021/8/17
 * mail: tain198127@163.com
 * 快排
 * @author danebrown
 */
@Log4j2
public class QuickSort {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new NetherlandsFlag());
        AlgCompMenu.run();
    }
    @AlgName("荷兰国旗问题")
    public static class NetherlandsFlag extends AlgCompImpl<ArrayList<Integer>,
            ArrayList<Double>>{

        @Override
        public ArrayList<Double> prepare() {
            int dataSize = ThreadLocalRandom.current().nextInt(2, 10);
            Double[] data = new Double[dataSize+1];
            for (int i = 0; i < dataSize; i++) {
                data[i] = Double.valueOf(ThreadLocalRandom.current().nextInt(0,10));
            }

            Double flag = data[ThreadLocalRandom.current().nextInt(0, dataSize-1)];
            data[dataSize] = flag;
//            int[] data = new int[]{0, 7, 6, 2, 9, 4, 9,7};
//
//            int flag = 7;
            ArrayList<Double> arrayList = new ArrayList<>();
            arrayList.addAll(Arrays.asList(data));
            return arrayList;
        }

        @Override
        protected ArrayList<Integer> standard(ArrayList<Double> inputData) {

            if(inputData == null||inputData.size()<2){
                return (ArrayList<Integer>) Stream.of(-1,1).collect(Collectors.toList());
            }
            double flag =
                    inputData.get(inputData.size()-1);
            inputData.sort(Comparator.comparingDouble(o -> o));
            int firstIdx = inputData.indexOf(flag);
            int lastIdx = inputData.lastIndexOf(flag);

            log.debug("flag:{},firstIdx:{},lastIdx:{},arr:[{}]",
                    flag,
                    firstIdx,
                    lastIdx,
                    inputData);
            return (ArrayList<Integer>) Stream.of(firstIdx-1,lastIdx).collect(Collectors.toList());


        }

        @Override
        protected ArrayList<Integer> test(ArrayList<Double> inputData) {
            int[] data =
                    inputData.stream().mapToInt(Double::intValue).toArray();
            int flag = data[data.length-1];
            int[] result = netherlandsFlag(data,0,data.length-1);
            int firstIdx = result[0];
            int lastIdx = result[1];

            log.debug("flag:{},firstIdx:{},lastIdx:{},arr:[{}]",
                    flag,
                    firstIdx,
                    lastIdx,
                    data);
            return (ArrayList<Integer>) Stream.of(firstIdx-1,lastIdx).collect(Collectors.toList());



        }

        /**
         * 思路：
         * 靠三种状态控制：
         * lower从-1开始
         * higher从length-1开始
         * i表示当前值，从0开始
         * flag表示国旗
         * data[i] < flag时：
         *      跟小于区的下一个数交换，并且i下一跳
         *      相当于小于区向右扩张
         * data[i] > flag时：
         *      跟大于区的前一个数交换,i不动
         *      相当于大于区向左扩张，但是由于交换过来的数的大小位置，因此i位置保持不动，需要继续计算
         * data[i] == flag时：
         *      跳过，i直接下一跳
         * 口诀：小于左换下一跳，大于右换I不动，相等直接下一跳
         * @param arr
         * @param l
         * @param r
         * @return
         */
        public int[] netherlandsFlag(int[] arr, int l, int r){

            int lower = l-1;
            int high = r;
            int i=l;
            if(arr == null||arr.length<2){
                return new int[]{-1,-1};
            }
            if(l>r){
                return new int[] {-1,-1};
            }
            if(l == r){
                return new int[]{l,r};
            }
            while (i<high){
                //
                if(arr[i] == arr[r]){
                    i++;
                }
                else if(arr[i]<arr[r]){
                    swap(arr,i++,++lower);
                }
                else {
                    swap(arr,i,--high);
                }

            }
            //把最后一位换出来
            swap(arr,high,r);
            return new int[]{lower+1,high};
        }
        public void swap(int[] arr, int a,int b){
            int tmp = arr[a];
            arr[a] = arr[b];
            arr[b] = tmp;
        }

    }
}
