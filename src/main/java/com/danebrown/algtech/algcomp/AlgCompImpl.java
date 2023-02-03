package com.danebrown.algtech.algcomp;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.danebrown.algtech.algcomp.wrongbook.JsonWrongBook;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by danebrown on 2021/7/28
 * mail: tain198127@163.com
 * 对数器
 * @author danebrown
 */
@Log4j2
public abstract class AlgCompImpl<T,R>{
    private volatile WrongBook<R> wrongBook = null;
    protected WrongBook initWrongBook(){
        if(wrongBook == null){
            synchronized (this){
                if(wrongBook == null){
                    wrongBook = new JsonWrongBook();
                }
            }
        }
        return wrongBook;
    }

    /**
     * 准备原始测试数据
     * @return
     */
    public abstract R prepare();
    public R prepare(int range, int steps){
        return prepare();
    }


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

    /**
     * 序列化原始测试数据，用于解决类似链表头结点问题
     * @param setupData
     * @return
     */
    protected Object formatSetupData(R setupData){
        return setupData;
    }

    /**
     * 序列换结果数据，用于解决类似链表头节点问题
     * @param result
     * @return
     */
    protected Object formatResultData(T result){
        return result;
    }

    @Data
    @AllArgsConstructor
    public class LoadedData{
        private R setupData;
        private R forTest;
        private R forStandard;
    }
    private LoadedData loadTestData(Supplier<R> prepareSupplier){
        R setupData =prepareSupplier == null? prepare():prepareSupplier.get();

        log.debug("原始数据:\n{}",setupData);
        R forTest=null;
        R forStandard=null;
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
            log.debug("尽力序列化，可能导致出错");
            forTest = ObjectUtil.cloneIfPossible(setupData);
            forStandard = ObjectUtil.cloneIfPossible(setupData);
        }
        return new LoadedData(setupData,forTest,forStandard);
    }
    /**
     * 对数
     * @param testName 对数器名称
     * @param testTime 测试时间消费者
     * @param standardTime 标准程序时间消费者
     * @param prepareSupplier 测试数据提供者，默认使用基类中的prepare方法
     * @return
     */
    public boolean compare(String testName, Consumer<Long> testTime,
                           Consumer<Long> standardTime ,
                           Supplier<R> prepareSupplier){
        this.initWrongBook();

        int timeout = -1;
        AlgName algName = this.getClass().getAnnotation(AlgName.class);
        if(algName!= null && algName.timeout() >0){
            timeout = algName.timeout();
        }
        LoadedData setupData = loadTestData(prepareSupplier);
        R originData = setupData.getSetupData();
        R finalForStandard = setupData.getForStandard();
        R finalForTest = setupData.getForTest();

        CompletableFuture complete = new CompletableFuture();
        
        CompletableFuture standardFuture = CompletableFuture.supplyAsync(()->{
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            T standardResult = standard(finalForStandard);
            stopWatch.stop();
            log.debug("标准结果:{}", standardResult);
            log.debug("标准计算耗时:{} 毫秒", stopWatch.getTime(TimeUnit.MILLISECONDS));
            if(standardTime != null){
                standardTime.accept(stopWatch.getTime(TimeUnit.MILLISECONDS));
            }
            return standardResult;
        });
        T standardResult = null;
        try {
            if(timeout>0) {
                standardResult = (T) standardFuture.get(timeout, TimeUnit.SECONDS);
            }else{
                standardResult = (T)standardFuture.get();
            }
        } catch (InterruptedException e) {
            log.warn(e);
        } catch (ExecutionException e) {
            log.warn(e);
        } catch (TimeoutException e) {
            log.error("标准方法超时",e);
            standardFuture.cancel(true);
        }
        
        
        CompletableFuture testFeature = CompletableFuture.supplyAsync(()->{
            StopWatch stopWatch = new StopWatch();

            stopWatch.start();
            T testResult = test(finalForTest);
            stopWatch.stop();
            log.debug("测试结果:{}", testResult);
            log.debug("测试计算耗时:{} 毫秒", stopWatch.getTime(TimeUnit.MILLISECONDS));
            if(testTime != null){
                testTime.accept(stopWatch.getTime(TimeUnit.MILLISECONDS));
            }
            return testResult;
        });
        
        T testResult = null;
        
        try {
            if(timeout>0) {
                testResult = (T) testFeature.get(timeout, TimeUnit.SECONDS);
            }else{
                testResult = (T) testFeature.get();
            }
        } catch (InterruptedException e) {
            log.warn(e);
        } catch (ExecutionException e) {
            log.warn(e);
        } catch (TimeoutException e) {
            testFeature.cancel(true);
            log.error("测试方法超时",e);
        }


        boolean result = true;

        result = testEqual(testResult, standardResult);
        if (!result) {
            wrongBook.write(testName, originData);

            log.error("{}测试失败,原始数据:\n{}", testName,
                    formatSetupData(originData)
                    );
            log.error("{}测试失败,测试结果:{}",testName,
                    formatResultData(testResult));
            log.error("{}测试失败,标准结果:{}",testName,
                    formatResultData(standardResult));
        } else {
            log.debug("{}测试成功", testName);
        }
        return result;
    }

    /**
     * 对数器
     * @param testName 对数器算法名称
     * @param testTime 测试耗时的callback
     * @param standardTime 标准算法耗时callback
     * @return
     */
    public boolean compare(String testName, Consumer<Long> testTime,
                           Consumer<Long> standardTime){
        return this.compare(testName,testTime,standardTime,null);
    }
    /**
     * 对数
     * @param testName
     * @return
     */
    public boolean compare(String testName) {
        return this.compare(testName,null);
    }
    public boolean compare(String testName,Supplier<R> prepareSupplier){
        return this.compare(testName,t->{
            log.warn("{} 测试程序 耗时[{}]毫秒",testName,t);
        },s->{
            log.warn("{} 标准程序 耗时[{}]毫秒",testName,s);
        },prepareSupplier);
    }


    public boolean multiCompare(String testName, int times){
        return multiCompare(testName,times,null);
    }

    /**
     * 错题本
     * @param testName
     * @return
     */
    public boolean multiCompareWrongBook(String testName){
        WrongBook wrongBook = initWrongBook();
        // TODO: 2023/2/3 这里有问题，尚未解决
        List<R> testCase = wrongBook.load(testName);
        boolean result = true;
        for (R item:testCase){
            log.info("错题集:{}",item);
            result &= compare(testName, () -> item);
        }
        return result;

    }
    /**
     * 多次测试,并统计执行速度
     * @param testName
     * @param times
     * @return
     */
    public boolean multiCompare(String testName, int times,
                                Consumer consumer) {
        boolean result = true;
        List<Long> testTime = new ArrayList<>(times);
        List<Long> standardTime = new ArrayList<>(times);
        for(int i = 0; i < times && result ; i++){
            result = compare(testName, t->{
                testTime.add(t);
            },s->{
                standardTime.add(s);
            });
            if(consumer != null){
                consumer.accept(result);
            }
        }
        if(consumer != null) {
            consumer.accept(null);
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
        log.warn("{} 标准程序 平均耗时[{}]毫秒,最大耗时:[{}]毫秒,最小耗时:[{}]毫秒",testName,
                meanStandard,
                maxStandard,minStandard);
        log.warn("{} 测试程序 平均耗时[{}]毫秒,最大耗时:[{}]毫秒,最小耗时:[{}]毫秒",testName,
                meanTest,
                maxTest,minTest);
        
        return result;
    }

    /**
     *
     * @param testName 测试名称
     * @param times 最终循环次数
     * @param steps 每次循环要执行多少步长，在本次步长内，循环的dataSize是保持不变的
     * @param dataSize 每次循环要执行的数据量
     * @param dataSizeStep 每次数据量增长多少
     * @param consumer 算法
     * @return
     */
    // TODO: 2023/2/3 完成算法复杂度测算
    public boolean algCompleCompare(String testName, int times,int steps, int dataSize,int dataSizeStep,
                                    Consumer consumer){
        boolean result = true;
        List<Long> testTime = new ArrayList<>(times);
        List<Long> standardTime = new ArrayList<>(times);

            throw new RuntimeException("尚未实现");
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
