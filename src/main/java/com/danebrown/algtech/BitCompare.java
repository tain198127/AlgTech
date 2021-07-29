package com.danebrown.algtech;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2021/7/29
 * mail: tain198127@163.com
 * 位运算
 * @author danebrown
 */

public class BitCompare extends AlgCompImpl<String, ImmutablePair<Integer,Integer>>{

    public static void main(String[] args) {
        BitCompare compare = new BitCompare();
//        compare.compare("异或对比");
        compare.multiCompare("异或对比",100);
    }
    @Override
    protected ImmutablePair<Integer,Integer> prepare() {
        return ImmutablePair.of(ThreadLocalRandom.current().nextInt(),
                ThreadLocalRandom.current().nextInt());
    }

    @Override
    protected String standard(ImmutablePair<Integer,Integer> data) {
        int l = data.getLeft();
        int r = data.getRight();
        int tmp = l;
        l = r;
        r = tmp;
        return l+":"+r;
    }

    /**
     * 异或运算，相同为0，不同为1————>无进位相加
     * 同或运算，相同为1，不同为0
     * 异或运算三大特征：满足交换律；满足结合律；0异或N一定为N,N异或N一定为0；
     * 使用场景
     * @param data
     * @return
     */
    @Override
    protected String test(ImmutablePair<Integer,Integer> data) {
        int l = data.getLeft();
        int r = data.getRight();
        l = l ^ r;
        r = l ^ r;
        l = l ^ r;
        return l+":"+r;

    }
}
