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

public class BitCompare extends AlgCompImpl<String, int[]>{

    public static void main(String[] args) {
        BitCompare compare = new BitCompare();
//        compare.compare("异或对比");
        compare.multiCompare("异或对比",100);
    }
    @Override
    protected int[] prepare() {
//        return ImmutablePair.of(ThreadLocalRandom.current().nextInt(),
//                ThreadLocalRandom.current().nextInt());
        return new int[]{ThreadLocalRandom.current().nextInt(),
                ThreadLocalRandom.current().nextInt()};
//        return new int[]{1,2};
    }

    @Override
    protected String standard(int[] data) {
        int tmp = data[0];
        data[0] = data[1];
        data[1] = tmp;
        return data[0]+":"+data[1];
    }

    /**
     * 异或运算，相同为0，不同为1————>无进位相加
     * 同或运算，相同为1，不同为0
     * 异或运算三大特征：满足交换律；满足结合律；0异或N一定为N,N异或N一定为0；
     * 推理===================================
     * 假设A=甲，B=乙
     * 那么A = A ^ B
     * B= A ^ B
     * A = A ^ B
     * 根据上述交换律，由于 A= A ^ B； 则B = A^B^B =A^0=A
     * 同样A = A^B = A ^A ^B = 0^B = B
     * 实例===================================
     * 如果A = 8，B = 4，那么二进制中 A = 1000 B = 0100
     * 则A = A ^ B = 1000 ^ 0100 = 1100
     * 则B = A ^ B = 1100 ^ 0100 = 1000 = 8
     * 则A = A ^ B = 1100 ^ 1000 = 0100 = 4
     * 交换完毕
     * 使用场景 两个数据无辅助空间互换
     * @param data
     * @return
     */
    @Override
    protected String test(int[] data) {
        data[0] = data[0] ^ data[1];
        data[1] = data[0] ^ data[1];
        data[0] = data[0] ^ data[1];

        return data[0]+":"+data[1];

    }
}
