package com.danebrown.algtech.algcomp.base.tree;

import cn.hutool.core.lang.Pair;
import com.danebrown.algtech.TreeStaff;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2022/2/26
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
public class CompleteBinTreeGenerator extends AbstraceBinTreeGenerator<Integer,
        String> {
    public CompleteBinTreeGenerator(){
        this.supplier =
                (()-> new Pair<>(java.lang.Integer.valueOf(ThreadLocalRandom.current().nextInt(1,1000)), null));
    }
    @Override
    public BinTreeNode<Integer,String> generate(int size) {
        return _generate(size);
    }

    private BinTreeNode<Integer,String> _generate(int size){
        int length = ThreadLocalRandom.current().nextInt(size);
        if (length <= 0) {
            return null;
        }
        if (length == 1) {
            return new BinTreeNode<>(supplier.get());
        }
        List<BinTreeNode<Integer,String>> nodeList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            nodeList.add(new BinTreeNode<Integer,String>(i,null));
        }
        int temp = 0;
        while (temp <= (length - 2) / 2) { //注意这里，数组的下标是从零开始的
            if (2 * temp + 1 < length)
                nodeList.get(temp).setLeftNode(nodeList.get(2 * temp + 1));
            if (2 * temp + 2 < length)
                nodeList.get(temp).setRightNode(nodeList.get(2 * temp + 2));
            temp++;
        }
        return nodeList.get(0);
    }
}
