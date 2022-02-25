package com.danebrown.algtech.algcomp.base.tree;

import cn.hutool.core.lang.Pair;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by danebrown on 2022/2/25
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
public abstract class AbstraceBinTreeGenerator<K,V> implements BinTreeGenerator<K,V>{
    Supplier<Pair<K,V>> supplier;
    @Override
    public void setSupplier(Supplier<Pair<K,V>> entrySupplier) {
        this.supplier = entrySupplier;
    }
}
