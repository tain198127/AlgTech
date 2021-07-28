package com.danebrown.algtech;

import java.util.function.Supplier;

/**
 * Created by danebrown on 2021/7/28
 * mail: tain198127@163.com
 * 算法对数器
 * @author danebrown
 */
public interface AlgComp<T,R> {
    /**
     * 装载数据
     * @return
     */
    R prepare();

    /**
     * 标准结果对数器
     * @return
     */
    T standard(R setupData);

    /**
     * 被测方法
     * @return
     */
    T test(R setupData);

    /**
     * 评估函数
     * @param testName
     * @return
     */
    boolean compare(String testName);


}
