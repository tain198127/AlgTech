package com.danebrown.algtech.algcomp.base.tree;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2022/2/26
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
public class BalanceSearchBinTreeGenerator<V> extends AbstraceBinTreeGenerator<Integer
        , V> {
    /**
     * 随机生成固定长度的不重复的平衡搜索二叉树
     * @param size
     * @return
     */
    @Override
    public BinTreeNode<Integer, V> generate(int size) {
        //起始值必须在0-100，如果过大会导致star+size超过int上线的情况
        int min = ThreadLocalRandom.current().nextInt(0,100);
        List<Integer> array = new ArrayList<>(size);
        array.add(Integer.valueOf(min) );
        int maxStep = (int) Math.ceil((Integer.MAX_VALUE-min)/size);
        Integer cur =Integer.valueOf(min);
        // 以内，否则如果size过大可能导致出现超过int最大值长度的值的情况
        //使用这种方式可以保证数组足够散列，也足够随机
        for(int i = 0; i < size;i++){
            cur = cur + ThreadLocalRandom.current().nextInt(1,maxStep);
            array.add(cur);
        }
        Collections.sort(array);
        int[] intArray = array.stream().mapToInt(Integer::intValue).toArray();
        BinTreeNode<Integer, V> root =generate(intArray,0,intArray.length-1);
        return root;

    }
    public BinTreeNode<Integer, V> generate(int[] sortArr, int start, int end) {
        if (start > end) {
            return null;
        }
        int mid = (start + end) / 2;
        BinTreeNode<Integer, V> head = new BinTreeNode<>(sortArr[mid], null);
        head.setLeftNode(generate(sortArr, start, mid - 1));
        head.setRightNode(generate(sortArr, mid + 1, end));
        return head;
    }
}
