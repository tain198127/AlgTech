package com.danebrown.algtech.algcomp;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by danebrown on 2021/8/19
 * mail: tain198127@163.com
 * 错题本
 *
 * @author danebrown
 */
public interface WrongBook<R>
{
    /**
     * 记录错题本
     *
     * @param testName
     * @param setupData
     *
     */
     void write(String testName, R setupData);

    /**
     * 加载错题本
     * @param testName 测试名称
     * @param type 要加载的数据类型
     * @return
     */
     List<R> load(String testName, Type type);
}
