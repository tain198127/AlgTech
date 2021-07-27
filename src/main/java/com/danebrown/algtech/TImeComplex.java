package com.danebrown.algtech;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Created by danebrown on 2021/7/26
 * mail: tain198127@163.com
 * 时间复杂度
 * 结论循环中：数组最快，Arrayist其次，最差的是LinkedList。
 * @author danebrown
 */
@BenchmarkMode(Mode.SampleTime)
@State(Scope.Thread)
@Fork(2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 1)

public class TImeComplex {
    int size = 100000;
    LinkedList<Integer> linkedList = new LinkedList<Integer>();
    ArrayList<Integer> arrayList = new ArrayList<>();
    int[] intArray = new int[size];

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(TImeComplex.class.getSimpleName()).build();
        new Runner(opt).run();


    }

    @Setup
    public void setupData() {
        for (int i = 0; i < size; i++) {
            linkedList.add(i);
            arrayList.add(i);
            intArray[i] = i;
        }
    }

    @Benchmark
    public void linkedForEach() {
        int tmp = 0;
        for (int i = 0; i < linkedList.size(); i++) {
            tmp = linkedList.get(i);
        }
    }
    @Benchmark
    public void linkedIteration() {
        int tmp = 0;
        Iterator<Integer> integerIterator = linkedList.iterator();
        while (integerIterator.hasNext()){
            tmp = integerIterator.next();
        }
    }

    @Benchmark
    public void arrayForEach() {
        int tmp = 0;
        for (int i = 0; i < linkedList.size(); i++) {
            tmp = intArray[i];
        }
    }
    @Benchmark
    public void arrayListForEach(){
        int tmp = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            tmp = arrayList.get(i);
        }
    }
}
