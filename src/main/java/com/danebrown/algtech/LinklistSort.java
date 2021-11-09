package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
        AlgCompMenu.addComp(new DoubleCycleCrossLink());
        AlgCompMenu.run();
    }

    @Data
    public static class Node {
        private int value;

        private Node next;
        private int index = this.hashCode();
        public Node(int value) {
            this.value = value;
        }
        public Node(int value,int index){
            this.value = value;
            this.index = index;
        }

        @Override
        public String toString() {
            return "Node("+ value + "," + index + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;

            if (!(o instanceof Node))
                return false;
            else{
                Node compNode = (Node)o;
                if(compNode == null){
                    return false;
                }
                return this.index ==compNode.index;
            }
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
     * 如果快指针与慢指针相遇了，那证明有环
     * 如何找到入环节点：相遇后，快指针回到起始点，然后一次走一步，慢指针依旧一次走一步。当他两再次相遇的时候，那个节点
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
    @AlgName("判断链表相交")
    public static class DoubleCycleCrossLink extends AlgCompImpl<Node[], Node[]> {
        GetLoopNode getLoopNode = new GetLoopNode();

        /**
         * 产生随机单向链表
         *
         * @param origin 随机长度开始
         * @param bound 随机长度边界
         * @param isLoop 是否产生环
         * @return
         */
        private LinkedList<Node> generateLink(int origin, int bound, boolean isLoop) {
            int length = ThreadLocalRandom.current().nextInt(origin, bound);
            LinkedList<Node> list = new LinkedList<>();
            for (int i = 0; i < length; i++) {
                Node node = new Node(ThreadLocalRandom.current().nextInt());
                if (!list.isEmpty()) {
                    list.getLast().setNext(node);
                }
                list.add(node);
            }
            //是否产生环
            if (isLoop) {
                //交叉入环节点
                int loopIdx = ThreadLocalRandom.current().nextInt(1, length - 1);
                Node loopNode = list.get(loopIdx);
                list.getLast().setNext(loopNode);
            }
            return list;
        }

        private String formatNode(Node node){
            StringBuilder strB = new StringBuilder();
            Node n1 = node;
            Set<Node> set1 = new HashSet<>();
            while (n1 != null){
                strB.append(n1.toString()+",");
                if(set1.contains(n1)){
                    break;
                }
                set1.add(n1);
                n1 = n1.next;
            }
            strB.append("\n");
            return strB.toString();
        }
        @Override
        protected Object formatSetupData(Node[] setupData) {
            StringBuilder strB = new StringBuilder();
            strB.append(formatNode(setupData[0]));
            strB.append(formatNode(setupData[1]));
            return strB.toString();
        }
//        @Override
        public Node[] prepare1(){
            Node n1 = new  Node(1048224807,475603167);
            n1.next = new Node(-41007412,402249858);
            n1.next.next = new Node(1038040274,2045036434);
            n1.next.next.next = new Node(-1420784352,426394307);
            n1.next.next.next.next = new Node(2094460877,1281414889);
            n1.next.next.next.next.next = new Node(746000008,352598575);
            n1.next.next.next.next.next.next = new Node(-1975244088,1250142026);
            n1.next.next.next.next.next.next.next = n1.next.next;

            Node n2 = new Node(-684301003,20224131);
            n2.next = new Node(-199860696,1261031890);
            n2.next.next = new Node(-65489388,2135449562);
            n2.next.next.next = new Node(-1507930544,673586830);
            n2.next.next.next.next = new Node(1370434071,225672073);
            n2.next.next.next.next.next = new Node(1193984173,139566260);
            n2.next.next.next.next.next.next = new Node(-506506863,903525611);
            n2.next.next.next.next.next.next.next = n1.next.next.next;

            return new Node[]{n1,n2};

        }
//        @Override
        public Node[] prepare3(){
            Node n1 = new Node(-1967626114,1498438472);
            n1.next = new Node(-381167257,1325056130);
            n1.next.next = new Node(157617306,1809194904);
            n1.next.next.next = new Node(-1710114370,1219273867);
            n1.next.next.next.next = new Node(-651615869,335359181);
            n1.next.next.next.next.next = new Node(-1062456093,194707680);

            Node n2 = new Node(-1828684168,2102368942);
            n2.next = new Node(-1452649942,120478350);
            n2.next.next = new Node(1144152484,1424082571);
            n2.next.next.next = new Node(1113809841,1403700359);
            n2.next.next.next.next = new Node(927335654,1387380406);
            n2.next.next.next.next.next = new Node(-588104358,658404420);
            n2.next.next.next.next.next.next = new Node(-184424216,2108763062);
            n2.next.next.next.next.next.next.next = n1.next.next.next.next.next;

            return new Node[]{n1,n2};
        }
        @Override
        public Node[] prepare() {
            Node[] heads = new Node[2];
            boolean createLoop = false;
            LinkedList<Node> nodes1 = this.generateLink(7, 8, createLoop);

            LinkedList<Node> nodes2 = this.generateLink(7, 8, false);

            heads[0] = nodes1.getFirst();
            heads[1] = nodes2.getFirst();
            //是否产生交集
            if (ThreadLocalRandom.current().nextBoolean()) {
//            if (true) {
                log.warn("产生了交集");
                int crossIdx = ThreadLocalRandom.current().nextInt(1, nodes1.size() - 1);
                log.info("node1：{}",formatNode(heads[0]));
                log.info("node2：{}",formatNode(heads[1]));
                log.info("交集节点:{}",nodes1.get(crossIdx));
                nodes2.getLast().setNext(nodes1.get(crossIdx));
            }
            return heads;
        }

        @Override
        protected boolean testEqual(Node[] standard, Node[] test) {
            if (standard == null && test == null) {
                return true;
            }
            if (standard != null && test != null) {
                Set<Node> setStandard = Arrays.stream(standard).collect(Collectors.toSet());
                Set<Node> setTest = Arrays.stream(test).collect(Collectors.toSet());
                Set<Node> intersection = Sets.intersection(setStandard, setTest);
                if (intersection == null) {
                    return false;
                }
                return true;

            }
            return false;
        }

        @Override
        protected Node[] standard(Node[] head) {
            Set<Node> set = new HashSet<>();
            Set<Node> set2 = new HashSet<>();
            Node current = head[0];
            //处理环
            while (current != null && !set.contains(current)) {
                set.add(current);
                current = current.next;
            }
            current = head[1];
            //处理环
            while (current != null && !set2.contains(current)) {
                set2.add(current);
                current = current.next;
            }
            //取交集
            Set<Node> intersection = Sets.intersection(set, set2);
            //没交集，必然不相交
            if (intersection == null||intersection.isEmpty()) {
                return null;
            }
            //有交集
            Node[] ret = intersection.toArray(new Node[0]);
            return ret;

        }

        /**
         * 都没有环，判断是否有环
         *
         * @param head1
         * @param head2
         * @return
         */
        private Node[] noLoop(Node head1, Node head2) {
            int n = 0;
            Node cur1 = head1;
            Node cur2 = head2;
            //计算两个链表差值
            while (cur1 != null) {
                n++;
                cur1 = cur1.next;
            }
            while (cur2 != null) {
                n--;
                cur2 = cur2.next;
            }
            //N>0表示head1长，否则表示head2长
            cur1 = n > 0 ? head1 : head2;
            //cur2是另外一个链表
            cur2 = cur1.equals(head1) ? head2 : head1;
            n = Math.abs(n);
            //调整长度，调整成为一样长，对齐长度，把长的那个调整成短的
            while (n > 0) {
                cur1 = cur1.next;
                n--;
            }
            while (cur1 != null || cur2 != null) {
                if (cur1 == cur2) {
                    return new Node[]{cur1, cur2};
                }
                cur1 = cur1.next;
                cur2 = cur2.next;
            }

            return null;
        }

        /**
         * 都有环
         *
         * @param head1 第一个链表的头节点
         * @param loop1 第一个链表的环的相交节点
         * @param head2 第二个链表的头节点
         * @param loop2 第二个链表的环的相交节点
         * @return
         */
        private Node[] bothLoop(Node head1, Node loop1, Node head2, Node loop2) {
            Node cur1 = null;
            Node cur2 = null;
            //两个环的相交节点是一样的。表明一定相交了。那就找到最早的相交点就行了
            if (loop1 == loop2) {
                cur1 = head1;
                cur2 = head2;
                //计算差值
                int n = 0;
                //计算两个链表差值
                while (cur1 != loop1) {
                    cur1 = cur1.next;
                    n++;
                }

                while (cur2 != loop2) {
                    cur2 = cur2.next;
                    n--;
                }

                cur1 = n > 0 ? head1 : head2;
                cur2 = cur1.equals(head1) ? head2 : head1;
                n = Math.abs(n);
                //调整成一样长度
                while (n > 0) {
                    cur1 = cur1.next;
                    n--;
                }
                while (cur1 != cur2) {
                    cur1 = cur1.next;
                    cur2 = cur2.next;
                }
                return new Node[]{cur1, cur2};
            }
            //两个环的相交节点不一样，那就找个环转一圈，看看能不能碰到另外一个环，能碰上就相交，碰不上就不相交
            else {
                cur1 = loop1.next;
                while (cur1 != loop1) {
                    //转了一圈，如果相遇了表明这个两个环有相交
                    if (cur1 == loop2) {
                        return new Node[]{loop1, loop2};
                    }
                    cur1 = cur1.next;
                }
                return null;
            }
        }

        private Node[] test1(Node[] head) {
            //判断是否有环，有环返回交点，无环返回null
            Node loop1 = getLoopNode.test(head[0]);
            Node loop2 = getLoopNode.test(head[1]);
            //都无环
            if (loop1 == null && loop2 == null) {
                return noLoop(head[0], head[1]);
            }
            //都有环
            else if (loop1 != null && loop2 != null) {
                return bothLoop(head[0], loop1, head[1], loop2);
            }
            //一个无环一个有环，必然不相交
            return null;

        }

        @Override
        protected Node[] test(Node[] head) {
            return test1(head);
        }
    }

    /**
     * 给定一个单链表的头节点head，请判断该链表是否为回文结构。
     */
    @AlgName("回文链表")
    public static class HuiWenLink extends AlgCompImpl<Boolean, Node> {
        private void printNode(Node head) {
            Node current = head;
            StringBuilder stringBuilder = new StringBuilder();
            while (current != null) {
                stringBuilder.append(String.format("%d\n", current.getValue()));
                current = current.next;
            }
            log.debug("原始数据:\n{}", stringBuilder.toString());

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
            if (head == null || head.next == null) {
                return true;
            }
            Node current = head;
            Stack<Node> stack = new Stack<>();
            while (current != null) {
                stack.push(current);
                current = current.next;
            }
            current = head;
            while (current != null) {
                if (current.getValue() != stack.pop().getValue()) {
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
            while (fast != null && fast.next != null) {
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
            while (nextSlow != null) {
                tmp = nextSlow.next;//临时记录一下
                nextSlow.setNext(slow);//进行逆序
                slow = nextSlow;//后移一位
                nextSlow = tmp;//后移一位
            }
            boolean res = true;
            Node lastNode = slow;//记录一下最后一位
            while (nextSlow != null && fast != null) {
                if (nextSlow.value != fast.value) {
                    res = false;
                    break;
                }
                nextSlow = nextSlow.next;
                fast = fast.next;
            }
            //还要恢复回来
            nextSlow = lastNode.next;
            lastNode.setNext(null);
            while (nextSlow != null) {
                tmp = nextSlow.next;
                nextSlow.setNext(lastNode);
                lastNode = nextSlow;
                nextSlow = tmp;
            }

            printNode(head);
            return res;

        }

        /**
         * 只使用一半的stack
         *
         * @param head
         * @return
         */
        //        @Override
        protected Boolean test1(Node head) {
            if (head == null || head.next == null) {
                return true;
            }
            Node right = head.next;
            Node cur = head;
            while (cur.next != null && cur.next.next != null) {
                right = right.next;
                cur = cur.next.next;
            }
            Stack<Node> stack = new Stack<>();
            while (right != null) {
                stack.push(right);
                right = right.next;
            }
            cur = head;
            while (!stack.isEmpty()) {
                if (stack.pop().value != cur.value) {
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
            int length = ThreadLocalRandom.current().nextInt(70000, 80000);
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
            fast = head;//一旦相遇，fast从头开始走，而且fast每次只走一步
            while (fast != slow) {//第二次相遇后，fast和slow都每次走一步，如果不一样，就一直走
                fast = fast.next;
                slow = slow.next;
            }
            //再次相遇后，表明找到了入口点
            return fast;
        }
    }
}
