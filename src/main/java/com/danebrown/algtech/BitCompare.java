package com.danebrown.algtech;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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

        bitSwap.multiCompare("异或对比", 1);
        OddNumSearch oddNumSearch = new OddNumSearch();

        oddNumSearch.compare("奇数次数字校验");
        oddNumSearch.multiCompare("奇数次数字校验", 1);

        LastRightOne lastRightOne = new LastRightOne();
        lastRightOne.compare("保持最右边的一个1");


        TwoOddNumSearch twoOddNumSearch = new TwoOddNumSearch();
        twoOddNumSearch.multiCompare("查找两个奇数",1);

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
     * 其中只有一个奇数次，剩下的都是偶数次
     */
    public static class OddNumSearch extends AlgCompImpl<String, int[]> {

        @Override
        protected int[] prepare() {
            int times = ThreadLocalRandom.current().nextInt(3, 10);

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
                int randomEven = ThreadLocalRandom.current().nextInt(1, evenTimes);
                //保证是偶数，因为随机数范围是1~evenTimes，因此最小是1，因此+1保证是偶数；
                randomEven = randomEven % 2 == 0 ? randomEven : randomEven + 1;
                //从cur_idx到cur_idx+randomEven，填充一个随机数
                Arrays.fill(num, cur_idx, cur_idx += randomEven, ThreadLocalRandom.current().nextInt());
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
                    log.debug("{} 是 {} 次", i, map.get(i));
                    stringBuilder.append(i);
                } else if (log.isDebugEnabled()) {
                    log.debug("偶数:{} 是 {} 次", i, map.get(i));
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

    /**
     * 数据中有两个奇数次数据，其他两个数是偶数次。找到这两个数
     */
    public static class TwoOddNumSearch extends AlgCompImpl<int[], int[]> {
        OddNumSearch inner = new OddNumSearch();

        @Override
        protected int[] prepare() {
            int times = ThreadLocalRandom.current().nextInt(3, 10000000);

            //保证是奇数长度
            int[] num = new int[times % 2 == 0 ? times - 1 : times];
            Arrays.fill(num, ThreadLocalRandom.current().nextInt());
            int[] oldNum = inner.prepare();
            int[] sumarray = new int[num.length + oldNum.length];
            for (int i = 0; i < oldNum.length; i++) {
                sumarray[i] = oldNum[i];
            }
            for (int i = oldNum.length; i < oldNum.length + num.length; i++) {
                sumarray[i] = num[i - oldNum.length];
            }

            Collections.shuffle(Collections.singletonList(sumarray));

            return sumarray;
        }

        @Override
        protected int[] standard(int[] data) {
            Map<Integer, Integer> map = new ConcurrentHashMap<>();
            for (int i = 0; i < data.length; i++) {
                if (!map.containsKey(data[i])) {
                    map.put(data[i], 1);
                } else {
                    map.put(data[i], map.get(data[i]) + 1);
                }
            }
            List<Integer> list = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i : map.keySet().stream().sorted().collect(Collectors.toList())) {
                if (map.get(i) % 2 != 0) {
                    log.debug("{} 是 {} 次", i, map.get(i));
                    list.add(i);
                    stringBuilder.append(i);
                } else if (log.isDebugEnabled()) {
                    log.debug("偶数:{} 是 {} 次", i, map.get(i));
                }
            }
            return list.stream().sorted().mapToInt(v -> v).toArray();
        }

        @Override
        protected int[] test(int[] data) {
            int eor = 0;
            for (int i = 0; i < data.length; i++) {
                eor ^= data[i];
            }
            //此时eor一定是a ^ b的
            //eor' 表示的是 eor只剩下最右边的1的那个数
            //
            int rightOne = eor & (-eor);
            int eor_dash = 0;

            for (int i = 0; i < data.length; i++) {
                if ((data[i] & rightOne) != 0) {
                    eor_dash ^= data[i];
                }
            }
            int eor_fin = eor ^ eor_dash;
            List<Integer> arr = new ArrayList<>();
            arr.add(eor_dash);
            arr.add(eor_fin);
            return arr.stream().sorted().mapToInt(v -> v).toArray();

        }
    }

    /**
     *
     */
    public static class LastRightOne extends AlgCompImpl<Integer, Integer> {

        @Override
        protected Integer prepare() {
            return ThreadLocalRandom.current().nextInt(1000, 1000000);
            //            return 128+256;
        }

        /**
         * a & (~a +1) 相当于提取了最右侧的1
         *
         * @param data
         * @return
         */
        @Override
        protected Integer standard(Integer data) {
            int ret = data & (~data + 1);
            return ret;
        }

        /**
         * 重点： a &(~a+1) = a &(-a)
         * 取负数和取反+1是一码事！！！
         * a & (-a) 提取了最右侧的1
         * ->       1001 0010 1111 0000
         * 取反      0110 1101 0000 1111
         * +1       0110 1101 0001 0000
         * 取反以后再加一相当于把最右边的1给怼上来了
         * ===============================
         * 与运算    1001 0010 1111 0000
         * 0110 1101 0001 0000
         * -------------------
         * 0000 0000 0001 0000
         * 这样就把最右边的1提取出来了。
         * 取反+1的位运算，就是负数
         *
         * @param data
         * @return
         */
        @Override
        protected Integer test(Integer data) {
            return data & (-data);
        }
    }
}
