package com.danebrown.algtech;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.bag.UnmodifiableBag;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

/**
 * Created by danebrown on 2021/7/28
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
@Log4j2
public abstract class AlgCompImpl<T,R>{
    /**
     * 准备原始测试数据
     * @return
     */
    protected abstract R prepare();

    /**
     * 标准对数器
     * @param data
     * @return
     */
    protected abstract T standard(R data) ;

    /**
     * 测试函数
     * @param data
     * @return
     */
    protected abstract T test(R data);

    public boolean compare(String testName, Consumer<Long> testTime,
                           Consumer<Long> standardTime ){
        R setupData = prepare();
        log.trace("原始数据:{}",setupData);
        R forTest = null;
        R forStandard = null;
        //直接clone
        if(setupData instanceof Cloneable){
            log.debug("克隆接口实现的克隆");
            forTest = ObjectUtil.clone(setupData);
            forStandard = ObjectUtil.clone(setupData);
        }
        //序列化克隆
        else if(setupData instanceof Serializable){
            log.debug("序列化实现的克隆");
            forTest = ObjectUtil.cloneByStream(setupData);
            forStandard = ObjectUtil.cloneByStream(setupData);
        }
        //尽力序列化
        else {
            log.warn("尽力序列化，可能导致出错");
            forTest = ObjectUtil.cloneIfPossible(setupData);
            forStandard = ObjectUtil.cloneIfPossible(setupData);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        T testResult = test(forTest);
        stopWatch.stop();
        log.debug("测试结果:{}", testResult);
        log.debug("测试计算耗时:{} 毫秒", stopWatch.getTime(TimeUnit.MILLISECONDS));
        if(testTime != null){
            testTime.accept(stopWatch.getTime(TimeUnit.MILLISECONDS));
        }

        boolean result = true;
        stopWatch.reset();
        stopWatch.start();
        T standardResult = standard(forStandard);
        stopWatch.stop();
        log.debug("标准结果:{}", standardResult);

        log.debug("标准计算耗时:{} 毫秒", stopWatch.getTime(TimeUnit.MILLISECONDS));
        if(standardTime != null){
            standardTime.accept(stopWatch.getTime(TimeUnit.MILLISECONDS));
        }
        result = testEqual(testResult, standardResult);
        if (!result) {
            log.error("{}测试失败,原始数据:{}", testName,setupData
                    );
            log.error("{}测试失败,测试结果:{}",testName,testResult);
            log.error("{}测试失败,标准结果:{}",testName,standardResult);
        } else {
            log.debug("{}测试成功", testName);
        }
        return result;
    }
    /**
     * 对数
     * @param testName
     * @return
     */
    public boolean compare(String testName) {
        return this.compare(testName,t->{
            log.warn("{} 测试程序 耗时[{}]毫秒",testName,t);
        },s->{
            log.warn("{} 标准程序 耗时[{}]毫秒",testName,s);
        });
    }


    /**
     * 多次测试
     * @param testName
     * @param times
     * @return
     */
    public boolean multiCompare(String testName, int times) {
        boolean result = true;
        List<Long> testTime = new ArrayList<>(times);
        List<Long> standardTime = new ArrayList<>(times);
        for(int i = 0; i < times && result ; i++){
            result = compare(testName, t->{
                testTime.add(t);
            },s->{
                standardTime.add(s);
            });
        }
        long maxTest = testTime.stream().max(Long::compareTo).get();
        long minTest = testTime.stream().min(Long::compareTo).get();
        double meanTest =
                testTime.stream().map(Long::doubleValue
        ).reduce((a, b) -> a+b).get()/times;

        long maxStandard = standardTime.stream().max(Long::compareTo).get();
        long minStandard = standardTime.stream().min(Long::compareTo).get();

        double meanStandard =
                standardTime.stream().map(Long::doubleValue).reduce((a,b)->a+b).get()/times;
        log.warn("{} 测试程序 平均耗时[{}]毫秒,最大耗时:[{}]毫秒,最小耗时:[{}]毫秒",testName,
                meanTest,
                maxTest,minTest);
        log.warn("{} 标准程序 平均耗时[{}]毫秒,最大耗时:[{}]毫秒,最小耗时:[{}]毫秒",testName,
                meanStandard,
                maxStandard,minStandard);
        return result;
    }

    /**
     * 值比较器
     * @param standard
     * @param test
     * @return
     */
    protected boolean testEqual(T standard, T test){
        if(standard == test){
            return true;
        }
        if (standard == null || test == null) {
            log.error("测试数组为空");
            return false;
        }
        if(standard instanceof List && test instanceof List){
            List standardList = (List)standard;
            List testList = (List) test;
            boolean result = true;
            if(standardList.size() != testList.size()){
                return false;
            }
            for (int i = 0; i < standardList.size() && result; i++) {
                result = standardList.get(i).equals(testList.get(i));
                if(!result){
                    break;
                }
            }
            return result;

        }
        if(standard instanceof  Comparable && test instanceof Comparable){
            Comparable standardComp = (Comparable) standard;
            Comparable testComp = (Comparable) test;
            return  ObjectUtils.compare(standardComp,testComp) == 0;

        }
        String standardJson = JSONUtil.toJsonStr(standard);
        String testJson = JSONUtil.toJsonStr(test);


        return standardJson.equals(testJson);
    }
}
