package com.danebrown.algtech.algcomp.base.tree;

import cn.hutool.core.lang.Pair;
import lombok.Data;

import java.util.Map;

/**
 * Created by danebrown on 2022/2/26
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
@Data
public class BinTreeNode<K,V> {
    private BinTreeNode<K,V> leftNode;
    private BinTreeNode<K,V> rightNode;
    private K key;
    private V value;
    public BinTreeNode(Pair<K,V> entry){
        this.key = entry.getKey();
        this.value = entry.getValue();
    }
}
