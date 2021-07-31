package com.danebrown.algtech;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by danebrown on 2021/7/11
 * mail: tain198127@163.com
 * 超级水王
 * 问题描述:在一组数里面，有一个数字重复出现的次数，超过了数组长度的一半，则这个数字是超级水王。找到这个数
 * @author danebrown
 */
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Fork(2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Log4j2
public class HalfNumTest {
    private List<int[]> prepareData = new ArrayList<>();

    public static void printMenu() {
        System.out.println("请选择测试模式:");
        System.out.println("1:正确性校验");
        System.out.println("2:性能校验");
        System.out.println("0:退出");
    }

    public static void main(String[] args) throws RunnerException {
        printMenu();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                switch (input) {
                    case "1": {
                        HalfNumTest halfNumTest = new HalfNumTest();
                        halfNumTest.compare();
                        printMenu();
                    }
                    ;
                    break;
                    case "2": {
                        Options opt = new OptionsBuilder().include(HalfNumTest.class.getSimpleName()).build();
                        new Runner(opt).run();
                        printMenu();
                    }
                    ;
                    break;
                    case "0":
                        return;
                    default: {
                        System.err.println("请输入正确选项");
                        printMenu();
                    }
                    continue;
                }
            }

        }

        //

    }

    @Setup
    public void setupData() {
        for (int i = 0; i < 100; i++) {
            int[] test = generate(ThreadLocalRandom.current().nextInt(1000, 19000));
            prepareData.add(test);
        }
    }

    @Benchmark
    public void dpVersionMH() {
        for (int[] v : prepareData) {
            dpVersion(v);
        }
    }

    @Benchmark
    public void hashVersionMH() {
        for (int[] v : prepareData) {
            hashVersion(v);
        }
    }

    public void compare() {
        setupData();
        for (int i = 0; i < prepareData.size(); i++) {

            long begin = System.currentTimeMillis();
            String result4Test = dpVersion(prepareData.get(i));
            long end = System.currentTimeMillis();
            long testTime = end - begin;

            begin = System.currentTimeMillis();
            String verify4Test = hashVersion(prepareData.get(i));
            end = System.currentTimeMillis();
            long verifyTime = end - begin;
            if (!verify4Test.equals(result4Test)) {
                System.err.println(String.format("对数错误！结束！:待测试结果:[%s]," + "对数器结果:[%s]", result4Test, verify4Test));
                System.exit(1);
                break;
            }
            log.debug("测试结果[{}]耗时:{} VS. 对数器[{}]耗时:{}", result4Test, testTime, verify4Test, verifyTime);
            log.debug("-----");
        }
        System.out.println("对数成功");
    }

    /**
     * 动态规划版超级水王
     *
     * @param ary
     * @return
     */
    public String dpVersion(int[] ary) {
        log.debug("{}", ary);
        int candidate = Integer.MIN_VALUE;
        int hp = 0;
        int count = 0;
        for (int i = 0; i < ary.length; i++) {
            if (hp == 0) {
                //对冲完了以后才赋值，否则保留之前的candidate
                candidate = ary[i];
                hp = 1;
            } else if (candidate != ary[i]) {
                hp--;
            } else if (candidate == ary[i]) {
                hp++;
            }
        }
        //全都对冲完了，表明没有过半的num
        if (hp <= 0) {
            return Integer.MIN_VALUE + ":0";
        } else if (hp > 0) {
            //有幸存者——>有过半num必有幸存者，但是有幸存者不代表有过半num，因此要检查一遍
            for (int i = 0; i < ary.length; i++) {
                if (ary[i] == candidate) {
                    count++;
                }
            }
        }
        //超过1/2 >> 表示右移1位，相当于除以2
        if (count > (ary.length >> 1)) {
            return candidate + ":" + count;
        } else {
            return Integer.MIN_VALUE + ":0";
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
        int realValue = tmpKey.isPresent() ? tmpCount.get(tmpKey.get()) : Integer.MIN_VALUE;
        log.debug("{}", tmpCount);
        if (realValue < 0) {
            return realValue + ":0";
        } else
            return tmpKey.get() + ":" + realValue;

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
            ret[i] = ThreadLocalRandom.current().nextInt(1, 65535);
        }
        if (true) {
            //随机产生水王
            int halfNum = ThreadLocalRandom.current().nextInt(1, 65535);
            //随机产生水王个数
            int length = ThreadLocalRandom.current().nextInt(times >> 1, times - 1);
            //填充水王
            Arrays.fill(ret, 0, length, halfNum);
            //洗牌
            ArrayUtils.shuffle(ret);
        }

        log.debug("{}", ret);
        return ret;
    }

}
