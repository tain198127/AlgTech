package com.danebrown.algtech;

import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by danebrown on 2021/7/29
 * mail: tain198127@163.com
 * 位运算
 *
 * @author danebrown
 */
@Log4j2
public class BitCompare {

    public static void main(String[] args) {
        BitSwap bitSwap = new BitSwap();
        bitSwap.multiCompare("异或对比", 100);
        OddNumSearch oddNumSearch = new OddNumSearch();
        oddNumSearch.compare("奇数次数字校验");
        oddNumSearch.multiCompare("奇数次数字校验",100);
    }

    public static class BitSwap extends AlgCompImpl<String, int[]> {

        @Override
        protected int[] prepare() {
            return new int[]{ThreadLocalRandom.current().nextInt(), ThreadLocalRandom.current().nextInt()};
        }

        @Override
        protected String standard(int[] data) {
            int tmp = data[0];
            data[0] = data[1];
            data[1] = tmp;
            return data[0] + ":" + data[1];
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
         *
         * @param data
         * @return
         */
        @Override
        protected String test(int[] data) {
            data[0] = data[0] ^ data[1];
            data[1] = data[0] ^ data[1];
            data[0] = data[0] ^ data[1];
            return data[0] + ":" + data[1];

        }
    }

    /**
     * 数组中查找奇数次数据
     */
    public static class OddNumSearch extends AlgCompImpl<String, int[]> {

        @Override
        protected int[] prepare() {
            int times = ThreadLocalRandom.current().nextInt(1, 1000000);

            //保证是奇数长度
            int[] num = new int[times % 2 == 0 ? times - 1 : times];
            int oddTimes = ThreadLocalRandom.current().nextInt(1, num.length);
            //有多少次奇数次
            oddTimes = oddTimes % 2 == 0 ? oddTimes - 1 : oddTimes;
            //有多少偶数次
            int evenTimes = num.length - oddTimes;

            int oddNum = ThreadLocalRandom.current().nextInt();

            int cur_idx = 0;
            //先把奇数次数字填上
            while (oddTimes > 0) {
                num[cur_idx++] = oddNum;
                oddTimes--;
            }
            while (evenTimes > 0) {
                //范围随着eventTimes缩小
                int randomEven = ThreadLocalRandom.current().nextInt(1,
                        evenTimes);
                //保证是偶数，因为随机数范围是1~evenTimes，因此最小是1，因此+1保证是偶数；
                randomEven = randomEven %2 == 0?randomEven:randomEven+1;
                //从cur_idx到cur_idx+randomEven，填充一个随机数
                Arrays.fill(num,cur_idx,cur_idx+=randomEven,
                        ThreadLocalRandom.current().nextInt());
                //从eventTimes中减去用掉的randomEven
                evenTimes = evenTimes - randomEven;
            }
            //乱序
            Collections.shuffle(Collections.singletonList(num));
            return num;
        }

        @Override
        protected String standard(int[] data) {
            Map<Integer, Integer> map = new ConcurrentHashMap<>();
            for (int i = 0; i < data.length; i++) {
                if (!map.containsKey(data[i])) {
                    map.put(data[i], 1);
                } else {
                    map.put(data[i], map.get(data[i]) + 1);
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i : map.keySet()) {
                if (map.get(i) % 2 != 0) {
                    log.debug("{} 是 {} 次",i,map.get(i));
                    stringBuilder.append(i);
                }else if(log.isDebugEnabled()){
                    log.debug("偶数:{} 是 {} 次",i,map.get(i));
                }
            }

            return stringBuilder.toString();
        }

        @Override
        protected String test(int[] data) {
            int tmp = 0;
            for (int i = 0; i < data.length; i++) {
                tmp ^= data[i];
            }
            return String.valueOf(tmp);
        }
    }
}
