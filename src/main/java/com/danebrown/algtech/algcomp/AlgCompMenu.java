package com.danebrown.algtech.algcomp;

import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Level;
import org.slf4j.MDC;

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
public final class AlgCompMenu<I extends Number, I1 extends Number> {
    public static enum TestMode{
        /**
         * 普通模式
         */
        NORMAL,
        /**
         * 多次模式
         */
        MULTI,
        /**
         * 错题本模式
         */
        WRONGBOOK,
        /**
         * 统计算法复杂度
         */
        ALGCOMPLEX
    }

    /**
     * 错题本标记
     */
    public static final String TEST_NAME_FLG="TEST_NAME_FLG";
    /**
     * 算法名称
     * 算法实现
     * 测试模式
     */
    private static List<Triple<String, AlgCompImpl, TestMode>> algCompList = new ArrayList<>();
    static {
        //0表示结束
        algCompList.add(0,null);
    }

    /**
     * 增加一个对数器
     * @param comp
     */
    public static void addComp(AlgCompImpl comp){
        AlgName algName = comp.getClass().getAnnotation(AlgName.class);
        String name =comp.getClass().getSimpleName();
        if(algName != null && !Strings.isNullOrEmpty(algName.value())){
            name = algName.value();
        }
        addComp(comp,name);
    }

    /**
     * 增加对数器
     * @param comp 算法实现
     * @param name 算法名称
     */
    public static void addComp(AlgCompImpl comp, String name){
        addComp(comp,name,100);
    }

    /**
     *
     * @param comp 测试组件
     * @param name 测试名称
     * @param times 最大随机测试数量
     */
    public static void addComp(AlgCompImpl comp, String name, Integer times) {
        int t = times == null ? 100 : times;
        algCompList.add(Triple.of(name, comp, TestMode.NORMAL));
        algCompList.add(Triple.of(name, comp, TestMode.WRONGBOOK));
        algCompList.add(Triple.of(name, comp, TestMode.MULTI));
        algCompList.add(Triple.of(name, comp, TestMode.ALGCOMPLEX));
    }

    /**
     * 打印
     */
    private static void printMeau() {
        System.out.println("==============================");
        System.out.printf("[%3d]:[%s] \n", 0, "退出");
        for (int i = 1; i < algCompList.size(); i++) {
            String name = algCompList.get(i).getLeft();
            TestMode mode = algCompList.get(i).getRight();
            switch (mode){
                case NORMAL:{
                    name += ":单笔测试";
                };break;
                case WRONGBOOK:{
                    name += ":错题训练";
                };break;
                case MULTI:{
                    name+=":多次循环";
                };break;
                case ALGCOMPLEX:{
                    name+=":算法复杂度";
                }
            }
            System.out.printf("[%3d]:[%s] \n", i, name);
        }
        System.out.println("==============================");
    }

    /**
     * 进行对数器计算
     */
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
        Triple<String, AlgCompImpl, TestMode> triple =
                algCompList.get(idx);
        if (triple == null) {
            log.error("第{}项没有对数器,请重新选择", idx);
            return;
        }
        String name = triple.getLeft();
        AlgCompImpl impl = triple.getMiddle();
        TestMode mode = triple.getRight();
        AlgName algName = impl.getClass().getAnnotation(AlgName.class);
        int times  = algName.times();
        MDC.put(TEST_NAME_FLG,name);
        //TODO 要在单次计算中增加超时判定
        if(mode ==TestMode.NORMAL){
            log.info("第{}位对数器 :{},结果为:[{}]", idx, name, impl.compare(name));
        }
        else if(mode == TestMode.WRONGBOOK){
            log.info("第{}位对数器 :{},结果为:[{}]", idx, name, impl.multiCompareWrongBook(name));
        } else if (mode == TestMode.ALGCOMPLEX) {

        } else {
            log.info("第{}位对数器 :{},循环:[{}]次,结果为:[{}]", idx, name, times,
                    impl.multiCompare(name, times, r ->
                            {
                                if (log.getLevel().intLevel() <= Level.INFO.intLevel()) {
                                    if (r != null)
                                        System.out.print(".");
                                    else {
                                        System.out.println("");
                                    }
                                }
                            }

                    ));
        }
        MDC.remove(TEST_NAME_FLG);
    }

}
