package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2021/9/21
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
@Log4j2
public class LinklistSort {

    public static void main(String[] args) {
        AlgCompMenu.addComp(new GetLoopNode());
        AlgCompMenu.addComp(new FastSlowPointer());
        AlgCompMenu.addComp(new HuiWenLink());
        AlgCompMenu.run();
    }

    @Data
    public static class Node {
        private int value;

        private Node next;

        public Node(int value) {
            this.value = value;
        }

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
            return false;
        }
    }


    /**
     * 1:输入链表头节点，奇数长度返回中点，偶数长度返回上中点
     * 2:输入链表头节点，奇数长度返回中点，偶数长度返回下中点
     * 3:输入链表头节点，奇数长度返回中点前一个，偶数长度返回上中点前一个
     * 4:输入链表头节点，奇数长度返回中点前一个，偶数长度返回下中点前一个
     */
    @AlgName("快慢指针计算中点")
    public static class FastSlowPointer extends AlgCompImpl<String, Node> {

        @Override
        public Node prepare() {
            int length = ThreadLocalRandom.current().nextInt(70, 80);
            Node head = new Node(0);
            Node current = head;
            for (int i = 0; i < length; i++) {
                current.next = new Node(i + 1);
                current = current.next;
            }
            return head;
        }

        @Override
        protected String standard(Node head) {
            List<Node> list = new ArrayList<>();
            Node current = head;
            while (current != null) {
                list.add(current);
                current = current.next;
            }
            int len = list.size();
            //奇偶中点，偶数就直接除以2，奇数
            int oddmid = len / 2;

            Node midNode = list.get(oddmid);
            Node preMidNode = list.get(oddmid - 1);
            return String.format("%d-%d", midNode.getValue(), preMidNode.getValue());
        }

        @Override
        protected String test(Node head) {
            Node fast = head;
            Node preSlow = head;
            Node slow = head;

            while (fast != null && fast.next != null) {
                preSlow = slow;
                slow = slow.next;
                fast = fast.next.next;
            }
            return String.format("%d-%d", slow.getValue(), preSlow.getValue());

        }
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
    public static class DoubleCycleCrossLink extends AlgCompImpl<Node[], Node[]> {

        @Override
        public Node[] prepare() {
            return new Node[0];
        }

        @Override
        protected Node[] standard(Node[] data) {
            return new Node[0];
        }

        @Override
        protected Node[] test(Node[] data) {
            return new Node[0];
        }
    }

    /**
     * 给定一个单链表的头节点head，请判断该链表是否为回文结构。
     */
    @AlgName("回文链表")
    public static class HuiWenLink extends AlgCompImpl<Boolean, Node> {
        private void printNode(Node head){
            Node current = head;
            StringBuilder stringBuilder = new StringBuilder();
            while (current != null){
                stringBuilder.append(String.format("%d\n",current.getValue()));
                current = current.next;
            }
            log.debug("原始数据:\n{}",stringBuilder.toString());

        }
        @Override
        public Node prepare() {
            int length = ThreadLocalRandom.current().nextInt(70000, 80000);
            Stack<Integer> huiwenStack = new Stack<>();
            Node current = new Node(-1);
            huiwenStack.push(-1);
            Node head = current;
            for (int i = 0; i < length; i++) {
                int v = ThreadLocalRandom.current().nextInt();
                current.next = new Node(v);
                current = current.next;
                huiwenStack.push(v);
            }
            //处理奇数回文，认为判定length为偶数时，进行奇数回文
            if (length % 2 == 0) {
                int v = ThreadLocalRandom.current().nextInt();
                current.next = new Node(v);
                current = current.next;
            }
            while (!huiwenStack.isEmpty()) {
                int v = huiwenStack.pop();
                current.next = new Node(v);
                current = current.next;
            }
            printNode(head);
            return head;
        }

        @Override
        protected Boolean standard(Node head) {
            if(head == null || head.next == null){
                return true;
            }
            Node current = head;
            Stack<Node> stack = new Stack<>();
            while (current != null) {
                stack.push(current);
                current = current.next;
            }
            current = head;
            while (current != null){
                if(current.getValue() != stack.pop().getValue()){
                    log.error("不是回文结构");
                    return false;
                }
                current = current.next;
            }
            printNode(head);
            return true;


        }

        @Override
        protected Boolean test(Node head) {
            Node fast = head;
            Node slow = head;
            while (fast != null && fast.next != null){
                fast = fast.next.next;
                slow = slow.next;
            }
            //到这里，slow应该是中点
            //从头开始数
            fast = head;//记录一下
            //将后续slow之后的进行逆序连接
            Node nextSlow = slow.next;//从nextSlow开始算
            slow.setNext(null);
            Node tmp = null;
            //做翻转
            while (nextSlow != null){
                tmp = nextSlow.next;//临时记录一下
                nextSlow.setNext(slow);//进行逆序
                slow = nextSlow;//后移一位
                nextSlow = tmp;//后移一位
            }
            boolean res = true;
            Node lastNode = slow;//记录一下最后一位
            while (nextSlow!=null && fast !=null){
                if(nextSlow.value != fast.value){
                    res = false;
                    break;
                }
                nextSlow = nextSlow.next;
                fast = fast.next;
            }
            //还要恢复回来
            nextSlow = lastNode.next;
            lastNode.setNext(null);
            while (nextSlow != null){
                tmp = nextSlow.next;
                nextSlow.setNext(lastNode);
                lastNode = nextSlow;
                nextSlow = tmp;
            }

            printNode(head);
            return res;


            //循环完毕以后，slow一定是在中点
            //完成以后，再将nextSlow之后的逆序回来
        }

        /**
         * 只使用一半的stack
         * @param head
         * @return
         */
//        @Override
        protected Boolean test1(Node head) {
            if(head == null || head.next == null){
                return true;
            }
            Node right = head.next;
            Node cur = head;
            while (cur.next != null && cur.next.next != null){
                right = right.next;
                cur = cur.next.next;
            }
            Stack<Node> stack = new Stack<>();
            while (right != null){
                stack.push(right);
                right = right.next;
            }
            cur = head;
            while (!stack.isEmpty()){
                if(stack.pop().value != cur.value){
                    return false;
                }
                cur = cur.next;
            }
            return true;

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
                Node node = new Node(i);
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
