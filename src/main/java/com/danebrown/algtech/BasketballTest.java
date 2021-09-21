package com.danebrown.algtech;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2021/9/8
 * mail: tain198127@163.com
 * 两只队伍打比赛，A队只投两分球，命中率100%，百发百中。B队只投三分球，命中率66.66%。请问如果打一场比赛，A和B谁能赢？
 * 这里要稍微补充一下篮球规则，投不中弹框而出的叫做篮板球，两边的球员要进行争抢，这里假设两边球员的争抢能力相同。谁会赢
 * @author danebrown
 */
public class BasketballTest {
    public static void main(String[] args) {
        Integer scoreA = 0;
        Integer scoreB = 0;
        Integer count = 100000;
        int i = 0;
        String right = ballRight();
        while (i < count) {
            boolean loop = true;
            while (loop) {
                if ("A".equals(right)) {
                    loop = false;
                    scoreA += 2;
                    right = "B";
                } else {
                    boolean pitch = pitch();
                    if (pitch) {
                        scoreB += 3;
                        right = "A";
                        loop = false;
                    } else {
                        right = ballRight();
                    }
                }
            }
            i++;
        }

        System.out.println("A 得分" + scoreA);
        System.out.println("B 得分" + scoreB);
    }


    // B投球
    private static boolean pitch() {
        return ThreadLocalRandom.current().nextDouble(0,1) < 0.6666D;
    }

    // 争夺球权
    private static String ballRight() {
        if (ThreadLocalRandom.current().nextDouble(0,1) > 0.5D) {
            return "A";
        }
        return "B";
    }
}
