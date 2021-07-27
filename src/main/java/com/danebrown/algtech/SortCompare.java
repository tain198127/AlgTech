package com.danebrown.algtech;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Created by danebrown on 2021/7/27
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
@Log4j2
public class SortCompare {

    public int size = 50000;
    public int[] array = new int[size];

    /**
     * 装在预备数据
     * @param mode 0：顺序；1：逆序；2：随机；
     */
    @Setup
    public void setupData(int mode){
        for (int i = 0; i < array.length; i++) {
            array[i] = ThreadLocalRandom.current().nextInt();
        }
        Arrays.sort(array);
        if(mode == 1){
            ArrayUtils.reverse(array);
        }
        if(mode == 2){
           ArrayUtils.shuffle(array);
        }

    }
    /**
     * 对数器
     * @param forTest 用于测试的数据
     * @param standard 标准结果
     * @return 是否一致
     */
    public boolean compare(int[] forTest, int[] standard){
        if(forTest == null || standard == null){
            return false;
        }
        if(forTest.length != standard.length){
            return false;
        }
        boolean result = true;
        int length = forTest.length;
        for(int i = 0; i < length && result;i++){
            result = forTest[i]== standard[i];
        }
        return result;
    }
    /**
     * 标准算法，正确性验证
     * @return
     */
    public int[] standardSort(){
        int[] result = Arrays.stream(array).sorted().toArray();
        return result;
    }

    /**
     * 插入排序
     * @return
     */
    public int[] insertSort(){
        int[] cp = ArrayUtils.clone(array);
        for(int i = 1; i < cp.length;i++){
            for(int j = i-1; j >= 0 && cp[j] > cp[j+1]; j--){
                innerSwap(cp,j,j+1);
            }
        }
        return cp;
    }
    private void innerSwap(int[] cp, int j, int i) {
        cp[i] = cp[i] ^ cp[j];
        cp[j] = cp[i] ^ cp[j];
        cp[i] = cp[i] ^ cp[j];
    }

    /**
     * 封装对比方法
     * @param forTest
     * @param standard
     * @return
     */
    public boolean compareWrap(Supplier<int[]> forTest,String testMethodName,
                               Supplier<int[]> standard,boolean isCompare){
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        int[] standardArr = standard.get();
        stopWatch.stop();

        log.info("测试计算耗时:{} 毫秒",stopWatch.getTime(TimeUnit.MILLISECONDS));
        boolean result = !isCompare;
        if(isCompare){
            result = true;
            stopWatch.reset();
            stopWatch.start();
            int[] test = forTest.get();
            stopWatch.stop();
            log.info("标准计算耗时:{} 毫秒",stopWatch.getTime(TimeUnit.MILLISECONDS));


            result = compare(test,standardArr);
            log.debug("标准结果:{}",standardArr);
            log.debug("测试结果:{}",test);
            if(!result){
                log.error("{}测试失败",testMethodName);
            }
            else{
                log.info("{}测试成功",testMethodName);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        SortCompare sortCompare = new SortCompare();
        sortCompare.setupData(2);
        log.debug("原始数据:{}",sortCompare.array);
        int[] result = sortCompare.standardSort();
        log.debug("standardSort:{}",result);

        boolean compareResult =
                sortCompare.compareWrap(() -> sortCompare.insertSort(),
                "insertSort",
                () -> sortCompare.standardSort(),true);


    }
}
