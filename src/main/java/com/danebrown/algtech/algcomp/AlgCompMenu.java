package com.danebrown.algtech.algcomp;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.core.util.Integers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by danebrown on 2021/8/2
 * mail: tain198127@163.com
 * 对数器菜单
 *
 * @author danebrown
 */
@Log4j2
public final class AlgCompMenu {
    private static List<Triple<String, AlgCompImpl, Integer>> algCompList = new ArrayList<>();
    static {
        algCompList.add(0,null);
    }
    public static void addComp(AlgCompImpl comp){
        addComp(comp,comp.getClass().getSimpleName());
    }
    public static void addComp(AlgCompImpl comp, String name){
        addComp(comp,name,100);
    }
    public static void addComp(AlgCompImpl comp, String name, Integer times) {
        int t = times == null ? 100 : times;
        algCompList.add(Triple.of(name, comp, 1));
        algCompList.add(Triple.of(name + ":多次循环", comp, t));
    }

    private static void printMeau() {
        System.out.println("==============================");
        System.out.printf("[%3d]:[%s] \n", 0, "退出");
        for (int i = 1; i < algCompList.size(); i++) {
            String name = algCompList.get(i).getLeft();
            System.out.printf("[%3d]:[%s] \n", i, name);
        }
        System.out.println("==============================");
    }

    public static void run() {
        printMeau();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {

            String input= scanner.next();
            int i = StringUtils.isNumeric(input)?Integer.parseInt(input):-1;
            if (i == 0){
                break;
            }
            if(i > algCompList.size()-1 || i<0){
                System.err.printf("请输入[%d-%d]之间的选项\n",0,algCompList.size()-1);
                printMeau();
                continue;
            }
            runTest(i);
            printMeau();
        }
        System.out.println("bye~~");
    }

    private static void runTest(int idx) {
        Triple<String, AlgCompImpl, Integer> triple =
                algCompList.get(idx);
        if (triple == null) {
            log.error("第{}项没有对数器,请重新选择", idx);
            return;
        }
        String name = triple.getLeft();
        AlgCompImpl impl = triple.getMiddle();
        int times = triple.getRight();
        if(times <=1){
            log.info("第{}位对数器 :{},结果为:[{}]", idx, name, impl.compare(name));
        }
        else{
            log.info("第{}位对数器 :{},循环:[{}]次,结果为:[{}]", idx, name, times, impl.multiCompare(name, times));
        }
    }

}
