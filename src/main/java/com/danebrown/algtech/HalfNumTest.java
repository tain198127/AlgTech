package com.danebrown.algtech;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2021/7/11
 * mail: tain198127@163.com
 * 超级水王
 *
 * @author danebrown
 */
@Log4j2
public class HalfNumTest {
    public static void main(String[] args) {
        HalfNumTest halfNumTest = new HalfNumTest();
        for (int i = 0; i < 100; i++) {
            int[] test =
                    halfNumTest.generate(ThreadLocalRandom.current().nextInt(1000, 19000));

            long begin = System.currentTimeMillis();
            String result4Test = halfNumTest.dpVersion(test);
            long end = System.currentTimeMillis();
            long testTime = end-begin;

            begin = System.currentTimeMillis();
            String verify4Test = halfNumTest.hashVersion(test);
            end = System.currentTimeMillis();
            long verifyTime = end-begin;
            if (!verify4Test.equals(result4Test)) {
                System.err.println(String.format("对数错误！结束！:待测试结果:[%s]," +
                        "对数器结果:[%s]",result4Test,verify4Test));
                System.exit(1);
                break;
            }
            log.debug("测试结果[{}]耗时:{} VS. 对数器[{}]耗时:{}",result4Test,testTime,
                    verify4Test,verifyTime);
            System.out.println("-----");
        }
        System.out.println("对数成功");
        System.exit(0);
    }

    /**
     * 动态规划版超级水王
     *
     * @param ary
     * @return
     */
    public String dpVersion(int[] ary) {
        log.debug("{}",ary);
        int candidate = Integer.MIN_VALUE;
        int hp = 0;
        int count = 0;
        for (int i = 0; i < ary.length; i++) {
            if(hp == 0){
                //对冲完了以后才赋值，否则保留之前的candidate
                candidate = ary[i];
                hp=1;
            }
            else if(candidate!= ary[i]){
                hp--;
            }
            else if(candidate == ary[i]){
                hp++;
            }
        }
        if(hp <=0){
            return String.format("%d:0",Integer.MIN_VALUE);
        }
        else if(hp > 0){
            for (int i = 0; i < ary.length; i++) {
                if(ary[i] == candidate){
                    count++;
                }
            }
        }
        if(count > (ary.length >>1)){
            return String.format("%d:%d",candidate,count);
        }
        else {
            return String.format("%d:0",Integer.MIN_VALUE);
        }
    }

    /**
     * hash版超级水王，作为对数器
     *
     * @param ary
     * @return
     */
    public String hashVersion(int[] ary) {
        List<Integer> integerList = new ArrayList<>();

        for (int i = 0; i < ary.length; i++) {
            integerList.add(ary[i]);
        }
        Map<Integer, Integer> tmpCount = new ConcurrentHashMap<>();
        integerList.stream().forEach(item -> {
            if (!tmpCount.containsKey(item)) {
                tmpCount.putIfAbsent(item, 1);
            } else {
                tmpCount.put(item, tmpCount.get(item) + 1);
            }
        });
        Optional<Integer> tmpKey = tmpCount.keySet().stream().filter(item -> {
            return tmpCount.get(item) > ary.length / 2;
        }).findFirst();
        int realValue = tmpKey.isPresent() ? tmpCount.get(tmpKey.get()) :
                Integer.MIN_VALUE;
        log.debug("{}",tmpCount);
        if (realValue < 0) {
            return String.valueOf(String.format("%d:0",realValue));
        } else
            return String.format("%d:%d", tmpKey.get(), realValue);
    }

    /**
     * 对数生产器
     *
     * @param times
     * @return
     */
    public int[] generate(int times) {
        int[] ret = new int[times];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = ThreadLocalRandom.current().nextInt(1,65535);
        }
        if(ThreadLocalRandom.current().nextBoolean()){
            //随机产生水王
            int halfNum = ThreadLocalRandom.current().nextInt(1,65535);
            //随机产生水王个数
            int length = ThreadLocalRandom.current().nextInt(times>>1,times-1);
            //填充水王
            Arrays.fill(ret,0,length,halfNum);
            //洗牌
            ArrayUtils.shuffle(ret);
        }

        log.debug("{}",ret);
        return ret;
    }

}
