package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import com.google.common.base.Objects;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2021/9/21
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
public class LinklistSort {

    public static void main(String[] args) {
        AlgCompMenu.addComp(new GetLoopNode());
        AlgCompMenu.run();
    }

    @Data

    public static class Node {
        private int value;

        private Node next;

        @Override
        public String toString() {
            return "Node{" + "value=" + value + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Node))
                return false;
            Node node = (Node) o;
            return getValue() == node.getValue();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getValue());
        }
    }
    //TODO

    /**
     * 1:输入链表头节点，奇数长度返回中点，偶数长度返回上中点
     * 2:输入链表头节点，奇数长度返回中点，偶数长度返回下中点
     * 3:输入链表头节点，奇数长度返回中点前一个，偶数长度返回上中点前一个
     * 4:输入链表头节点，奇数长度返回中点前一个，偶数长度返回下中点前一个
     */
    public static class FastSlowPointer {

    }

    /**
     * 快慢指针，一般快指针每次跳2个，慢指针每次跳1个。
     * 用快慢指针求中点
     * 还可以用快慢指针用来判断一个链表是否存在环。
     * 如果快指针先到NULL，证明没有环，
     * 如果快指针跑到慢指针相遇了，那证明有环
     * 如何找到入环节点：快指针回到起始点，然后一次走一步，慢指针依旧一次走一步。当他两再次相遇的时候，那个节点
     * 就是入环节点！！！
     * --------------------------以上是核心逻辑。下面是题目
     * 一种特殊的单链表节点类描述如下
     * class Node {
     * int value;
     * Node next;
     * Node rand;
     * Node(int val) { value = val; }
     * }
     * rand指针是单链表节点结构中新增的指针，rand可能指向链表中的任意一个节点，也可能指向null。
     * 给定一个由Node节点类型组成的无环单链表的头节点 head，请实现一个函数完成这个链表的复制，并返回复制的新链表的头节点。
     * 【要求】
     * 时间复杂度O(N)，额外空间复杂度O(1)
     */
    public static class DoubleCycleCrossLink {

    }

    /**
     * 给定一个单链表的头节点head，请判断该链表是否为回文结构。
     */
    public static class HuiWenLink extends AlgCompImpl<Boolean, Node> {

        @Override
        public Node prepare() {
            return null;
        }

        @Override
        protected Boolean standard(Node data) {
            return null;
        }

        @Override
        protected Boolean test(Node data) {
            return null;
        }
    }

    /**
     * 判断一个链表是否有环，如果有环则返回入环节点，否则返回null
     * 要求时间复杂度O(N)，空间复杂度O(1)
     */
    @AlgName("判断环链表")
    public static class GetLoopNode extends AlgCompImpl<Node, Node> {

        @Override
        public Node prepare() {
            int length = ThreadLocalRandom.current().nextInt(70000, 800000);
            LinkedList<Node> list = new LinkedList<>();
            for (int i = 0; i < length; i++) {
                Node node = new Node();
                node.setValue(i);
                if (!list.isEmpty()) {
                    list.getLast().setNext(node);
                }
                list.add(node);
            }
            //是否产生环
            if (ThreadLocalRandom.current().nextBoolean()) {
                //交叉入环节点
                int loopIdx = ThreadLocalRandom.current().nextInt(0, length - 1);
                Node loopNode = list.get(loopIdx);
                list.getLast().setNext(loopNode);
            }
            return list.getFirst();
        }

        //
        @Override
        protected Node standard(Node data) {
            while (data == null || data.next == null) {
                return null;
            }
            HashSet<Node> nodeMap = new HashSet<>();
            Node pointer = data;
            while (pointer != null) {
                if (nodeMap.contains(pointer)) {
                    return pointer;
                } else {
                    nodeMap.add(pointer);
                    pointer = pointer.next;
                }

            }
            return null;
        }

        @Override
        protected Node test(Node head) {
            Node fast = null;
            Node slow = null;
            if (head == null || head.next == null || head.next.next == null) {
                return null;
            }
            fast = head.next.next;
            slow = head.next;
            while (fast != slow) {
                if (fast.next == null || fast.next.next == null) {
                    //fast指针先触及null了，表示没有环
                    return null;
                }
                fast = fast.next.next;
                slow = slow.next;
            }
            //能到这里证明fast和slow第一次相遇了,fast回到头节点
            fast = head;
            while (fast != slow) {//第二次相遇后，fast和slow都每次走一步，
                fast = fast.next;
                slow = slow.next;
            }
            //再次相遇后，表明找到了入口点
            return fast;
        }
    }
}
