package com.danebrown.algtech;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * Created by danebrown on 2021/7/28
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
@Log4j2
public abstract class AlgCompImpl<T,R> implements AlgComp<T,R> {
    /**
     * 准备原始测试数据
     * @return
     */
    @Override
    public abstract R prepare();

    /**
     * 标准对数器
     * @param data
     * @return
     */
    @Override
    public abstract T standard(R data) ;

    /**
     * 测试函数
     * @param data
     * @return
     */
    @Override
    public abstract T test(R data);

    /**
     * 对数
     * @param testName
     * @return
     */
    @Override
    public boolean compare(String testName) {
        R setupData = prepare();
        R forTest = ObjectUtils.clone(setupData);
        R forStandard = ObjectUtils.clone(setupData);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        T testResult = test(forTest);
        stopWatch.stop();
        log.info("测试计算耗时:{} 毫秒", stopWatch.getTime(TimeUnit.MILLISECONDS));

        boolean result = true;
        stopWatch.reset();
        stopWatch.start();
        T standardResult = standard(forStandard);
        stopWatch.stop();
        log.info("标准计算耗时:{} 毫秒", stopWatch.getTime(TimeUnit.MILLISECONDS));

        result = testEqual(testResult, standardResult);
        log.debug("标准结果:{}", standardResult);
        log.debug("测试结果:{}", testResult);
        if (!result) {
            log.error("{}测试失败", testName);
        } else {
            log.info("{}测试成功", testName);
        }
        return result;
    }


    /**
     * 值比较器
     * @param standard
     * @param test
     * @return
     */
    protected boolean testEqual(T standard, T test){
        return standard.equals(test);
    }
}
