package com.danebrown.algtech.algcomp.base.tree;

import cn.hutool.core.lang.Pair;
import com.danebrown.algtech.TreeStaff;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Created by danebrown on 2022/2/26
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
public class NormalBinTreeGenerator<K,V> extends AbstraceBinTreeGenerator<K,V> {



    @Override
    public BinTreeNode<K, V> generate(int size) {
        Pair<K,V> entry = this.supplier.get();
        BinTreeNode<K,V> root = new BinTreeNode<K,V>(entry);
        BinTreeNode<K,V> cur = root;
        Stack<BinTreeNode<K,V>> treeNodeStack = new Stack<>();
        treeNodeStack.push(root);
        for (int i = 0; i < size; i++) {
            //0表示后退，1表示左，2表示右
            int direction = ThreadLocalRandom.current().nextInt(i, size) % 3;
            switch (direction) {
                case 0: {
                    if (treeNodeStack.isEmpty()) {
                        continue;
                    }
                    int popUpper = ThreadLocalRandom.current().nextInt(1, treeNodeStack.size() + 1);
                    while (!treeNodeStack.isEmpty() && popUpper > 0) {
                        popUpper--;
                        cur = treeNodeStack.pop();
                    }
                }
                ;
                break;
                case 1: {
                    while (cur.getLeftNode() != null) {
                        treeNodeStack.push(cur.getLeftNode());
                        //左移
                        cur = cur.getLeftNode();
                    }
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        Pair<K,V> left = this.supplier.get();
                        cur.setLeftNode(new BinTreeNode<>(left));
                        treeNodeStack.push(cur.getLeftNode());
                    } else {
                        Pair<K,V> right = this.supplier.get();
                        cur.setRightNode(new BinTreeNode<>(right));
                        treeNodeStack.push(cur.getRightNode());
                    }


                }
                ;
                break;
                case 2: {
                    while (cur.getRightNode() != null) {
                        treeNodeStack.push(cur.getRightNode());
                        cur = cur.getRightNode();

                    }
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        Pair<K,V> left = this.supplier.get();
                        cur.setLeftNode(new BinTreeNode<>(left));
                        treeNodeStack.push(cur.getLeftNode());
                    } else {
                        Pair<K,V> right = this.supplier.get();
                        cur.setRightNode(new BinTreeNode<>(right));
                        treeNodeStack.push(cur.getRightNode());
                    }

                }
                ;
                break;
            }
        }
        return root;
    }

}
