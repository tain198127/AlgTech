package com.danebrown.algtech;

import com.google.common.collect.Queues;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by danebrown on 2021/8/2
 * mail: tain198127@163.com
 * 栈和队列
 *
 * @author danebrown
 */
@Log4j2
public class StackAndQueue {
    public static void main(String[] args) {
        ArrayQueue arrayQueue = new ArrayQueue();
        int[] data = {-2037836292, -1463292357, -1285751995, -742265016, -154105910, 113449666, 237217538, 574940591, 972061624, 1007078208, 1059374408, 1483736186, 1652565323, 1696583709, 1722850146, 2017439736};
        Integer[] ops = {null, 1317035272, -336730510, 610757432, null, null, -1362722851, 1048214874};
        Pair<int[], Integer[]> customSupplier = Pair.of(data, ops);
        //        arrayQueue.compare("数组实现队列",()->{return customSupplier;});
        //        arrayQueue.compare("数组实现队列");
        arrayQueue.multiCompare("数组实现队列", 100);
    }

    /**
     * Array实现的队列
     * 思路，头尾指针取模+size控制长度
     */
    public static class ArrayQueue extends AlgCompImpl<int[], Pair<int[], Integer[]>> {
        /**
         * pair 左边为数据，右边为操作序列。 integer中 Null为pop，有数值则为push
         *
         * @return
         */
        @Override
        protected Pair<int[], Integer[]> prepare() {
            int[] result = new int[ThreadLocalRandom.current().nextInt(2, 200)];
            for (int i = 0; i < result.length; i++) {
                result[i] = ThreadLocalRandom.current().nextInt();
            }
            Integer[] opList = new Integer[ThreadLocalRandom.current().nextInt(0, result.length - 1)];

            for (int i = 0; i < opList.length; i++) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    opList[i] = null;
                    continue;
                }
                {
                    opList[i] = ThreadLocalRandom.current().nextInt();
                }
            }
            log.debug("原始数据:{};原始操作:{}", result, opList);

            return Pair.of(Arrays.stream(result).sorted().toArray(), opList);
        }

        @Override
        protected int[] standard(Pair<int[], Integer[]> data) {
            int limit = data.getLeft().length + +data.getRight().length;
            Queue<Integer> queue = Queues.newArrayBlockingQueue(limit);
            queue.addAll(Arrays.stream(data.getLeft()).boxed().collect(Collectors.toList()));
            Integer[] ops = data.getRight();
            List<Integer> integerList = new ArrayList<>();
            log.debug("原始数据:{}", data.getLeft());
            log.debug("原始操作:{}", data.getRight());

            for (int i = 0; i < ops.length; i++) {
                if (ops[i] == null) {
                    if (queue.isEmpty()) {
                        continue;
                    }
                    int val = queue.poll();
                    log.debug("standard->pull:val:{}", val);
                    integerList.add(val);
                } else {
                    if (queue.size() >= limit) {
                        continue;
                    }
                    queue.add(ops[i]);
                    log.debug("standard->add:val:{}", ops[i]);
                }
            }
            return integerList.stream().mapToInt(value -> value).toArray();
        }

        @Override
        protected int[] test(Pair<int[], Integer[]> data) {
            int limit = data.getLeft().length + data.getRight().length;
            int[] arr = new int[limit];
            for (int i = 0; i < data.getLeft().length; i++) {
                arr[i] = data.getLeft()[i];
            }
            Integer[] ops = data.getRight();
            final int[] size = {data.getLeft().length};
            final int[] head = {0};
            final int[] tail = {data.getLeft().length - 1};
            log.debug("原始数据:{}", data.getLeft());
            log.debug("原始操作:{}", data.getRight());
            List<Integer> integerList = new ArrayList<>();
            Function<Integer, Integer> add = new Function<Integer, Integer>() {
                @Override
                public Integer apply(Integer integer) {
                    if (size[0] >= arr.length) {
                        return null;
                    }
                    log.debug("test->add:val:{}", integer);

                    arr[tail[0]] = integer;
                    tail[0] = tail[0] < limit - 1 ? tail[0] + 1 : 0;
                    size[0]++;
                    return integer;
                }
            };
            Function<Void, Integer> poll = new Function<Void, Integer>() {
                @Override
                public Integer apply(Void aVoid) {
                    int val = 0;
                    try {

                        if (size[0] <= 0) {
                            return null;
                        }
                        val = arr[head[0]];
                        log.debug("test->pull:val:{}", val);

                        integerList.add(val);
                        head[0] = head[0] < limit - 1 ? head[0] + 1 : 0;
                        size[0]--;
                    } catch (Exception ex) {
                        log.error("test->poll->size:{};head:{};arr:{}", size[0], head[0], arr);

                    } finally {
                        return val;
                    }
                }
            };
            for (int i = 0; i < ops.length; i++) {
                if (ops[i] == null) {
                    Integer rst = poll.apply(null);
                    if (rst == null)
                        continue;
                } else {
                    Integer rst = add.apply(ops[i]);
                    if (rst == null) {
                        continue;
                    }
                }
            }
            return integerList.stream().mapToInt(v -> v).toArray();
        }

    }
}
