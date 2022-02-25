package com.danebrown.algtech.algcomp.base.tree;

import cn.hutool.core.lang.Pair;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Created by danebrown on 2022/2/25
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
public interface BinTreeGenerator<K, V> {
    void setSupplier(Supplier<Pair<K,V>> entrySupplier);
    /**
     * 生成0-10000个节点的二叉树
     * @return
     */
    default BinTreeNode<K, V> generate(){
        return generate(ThreadLocalRandom.current().nextInt(0,10000));
    }



    /**
     * 生成指定高和宽的二叉树
     * @param high
     * @param width
     * @return
     */
    default BinTreeNode<K, V> generate(int high, int width){
        throw new RuntimeException();
    }

    /**
     * 生成指定数据的二叉树，采用前序遍历
     * @param metaData
     * @return
     */
    default BinTreeNode<K,V> generate(TreeMap<K,V> metaData){
        throw new RuntimeException();
    }
    BinTreeNode<K, V> generate(int size);


}
