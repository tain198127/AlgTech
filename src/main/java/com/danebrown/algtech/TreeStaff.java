package com.danebrown.algtech;

import cn.hutool.json.JSONUtil;
import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import com.danebrown.algtech.algcomp.base.tree.BalanceSearchBinTreeGenerator;
import com.danebrown.algtech.algcomp.base.tree.BinTreeNode;
import com.danebrown.algtech.algcomp.base.tree.CompleteBinTreeGenerator;
import com.danebrown.algtech.algcomp.base.tree.SimpleNormalBinTreeGenerator;
import com.google.common.collect.Queues;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by danebrown on 2021/9/24
 * mail: tain198127@163.com
 * 树相关算法：
 * 总体来说，用栈是深度遍历；用队列是宽度遍历（图遍历）
 *
 * @author danebrown
 */
@Log4j2
public class TreeStaff {
    //表示空节点
    public static final String NULL_NODE = "#";
    //表示树的分隔符
    public static final String TREE_SPIN = "|";
    //表示节点之间的分隔符
    public static final String NODE_SPAIN = ",";

    public static void main(String[] args) {
        AlgCompMenu.addComp(new TreeDeepWalking());
        AlgCompMenu.addComp(new TreeWideWalking());
        AlgCompMenu.addComp(new NodeSer());
        AlgCompMenu.addComp(new NaryTreeToBTree());
        AlgCompMenu.addComp(new IsCBT());
        AlgCompMenu.addComp(new IsSBT());
        AlgCompMenu.addComp(new TreeMaxPath());
        AlgCompMenu.addComp(new IsAVLTree());
        AlgCompMenu.addComp(new IsFullBT());
        AlgCompMenu.addComp(new FindMaxSBT());
        AlgCompMenu.addComp(new LowestAncestors());
        AlgCompMenu.addComp(new MaxHappy());
        AlgCompMenu.run();
    }

    public static class BTreePrinter {
        /////////=====================

        private static void traverseNodes(StringBuilder sb, String padding, String pointer, TreeNode node, boolean hasRightSibling) {
            if (node != null) {
                sb.append("\n");
                sb.append(padding);
                sb.append(pointer);
                sb.append(node.getValue());

                StringBuilder paddingBuilder = new StringBuilder(padding);
                if (hasRightSibling) {
                    paddingBuilder.append("│  ");
                } else {
                    paddingBuilder.append("   ");
                }

                String paddingForBoth = paddingBuilder.toString();
                String pointerRight = "└──";
                String pointerLeft = (node.getRight() != null) ? "├──" : "└──";

                traverseNodes(sb, paddingForBoth, pointerLeft, node.getLeft(), node.getRight() != null);
                traverseNodes(sb, paddingForBoth, pointerRight, node.getRight(), false);
            }
        }

        public static void traversePreOrder(TreeNode root) {
            if (root == null) {

            }

            StringBuilder sb = new StringBuilder();
            sb.append(root.getValue());

            String pointerRight = "└──";
            String pointerLeft = (root.getRight() != null) ? "├──" : "└──";

            traverseNodes(sb, "", pointerLeft, root.getLeft(), root.getRight() != null);
            traverseNodes(sb, "", pointerRight, root.getRight(), false);
            System.out.println(sb);


        }

        //=============
        public static void show(TreeNode root) {
            if (root == null)
                System.out.println("EMPTY!");
            // 得到树的深度
            int treeDepth = getTreeDepth(root);

            // 最后一行的宽度为2的（n - 1）次方乘3，再加1
            // 作为整个二维数组的宽度
            int arrayHeight = treeDepth * 2 - 1;
            int arrayWidth = (2 << (treeDepth - 2)) * 3 + 1;
            // 用一个字符串数组来存储每个位置应显示的元素
            String[][] res = new String[arrayHeight][arrayWidth];
            // 对数组进行初始化，默认为一个空格
            for (int i = 0; i < arrayHeight; i++) {
                for (int j = 0; j < arrayWidth; j++) {
                    res[i][j] = " ";
                }
            }

            // 从根节点开始，递归处理整个树
            // res[0][(arrayWidth + 1)/ 2] = (char)(root.val + '0');
            writeArray(root, 0, arrayWidth / 2, res, treeDepth);

            // 此时，已经将所有需要显示的元素储存到了二维数组中，将其拼接并打印即可
            for (String[] line : res) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < line.length; i++) {
                    sb.append(line[i]);
                    if (line[i].length() > 1 && i <= line.length - 1) {
                        i += line[i].length() > 4 ? 2 : line[i].length() - 1;
                    }
                }
                System.out.println(sb.toString());
            }
        }

        private static int getTreeDepth(TreeNode root) {
            return root == null ? 0 : (1 + Math.max(getTreeDepth(root.left), getTreeDepth(root.right)));
        }

        private static void writeArray(TreeNode currNode, int rowIndex, int columnIndex, String[][] res, int treeDepth) {
            // 保证输入的树不为空
            if (currNode == null)
                return;
            // 先将当前节点保存到二维数组中
            res[rowIndex][columnIndex] = String.valueOf(currNode.value);

            // 计算当前位于树的第几层
            int currLevel = ((rowIndex + 1) / 2);
            // 若到了最后一层，则返回
            if (currLevel == treeDepth)
                return;
            // 计算当前行到下一行，每个元素之间的间隔（下一行的列索引与当前元素的列索引之间的间隔）
            int gap = treeDepth - currLevel - 1;

            // 对左儿子进行判断，若有左儿子，则记录相应的"/"与左儿子的值
            if (currNode.left != null) {
                res[rowIndex + 1][columnIndex - gap] = "/";
                writeArray(currNode.left, rowIndex + 2, columnIndex - gap * 2, res, treeDepth);
            }

            // 对右儿子进行判断，若有右儿子，则记录相应的"\"与右儿子的值
            if (currNode.right != null) {
                res[rowIndex + 1][columnIndex + gap] = "\\";
                writeArray(currNode.right, rowIndex + 2, columnIndex + gap * 2, res, treeDepth);
            }
        }

        public static String list2string(Collection<String> list) {
            StringBuilder stringBuilder = new StringBuilder();
            list.forEach(item -> {
                stringBuilder.append(item);
                stringBuilder.append(NODE_SPAIN);
            });
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            return stringBuilder.toString();

        }

        public static List<String> string2list(String str) {
            return Arrays.stream(str.split(NODE_SPAIN)).collect(Collectors.toList());

        }

    }

    //六种序：左头右，左右头，右左头，右头左，头左右，头右左
    //先序：头左右，中序：左头右，后续：左右头
    //可是先序、中序、后续的本质是：递归序
    //某个数X，其先序数组，A，后续数组B，X在A的左边A[0,X) 相交 B(X,N]，其交集是且仅是X的祖先节点。这是结论，如何证明？
    //要能证明
    @Data
    public static class TreeNode {
        private String value;
        private TreeNode left;
        private TreeNode right;

        public TreeNode(String value) {
            this.value = value;
        }

        public TreeNode() {

        }
    }

    public static abstract class TreeAlgComImpl<T, R> extends AlgCompImpl<T, R> {
        @Override
        protected Object formatSetupData(R setupData) {
            return JSONUtil.toJsonPrettyStr(setupData);
        }

    }

    @AlgName("树深度遍历")
    public static class TreeDeepWalking extends TreeAlgComImpl<String, TreeNode> {

        volatile AtomicInteger integer = new AtomicInteger(0);

        public TreeNode stackBinaryTreeGenerator(int times) {
            TreeNode root = new TreeNode("root");
            TreeNode cur = root;
            Stack<TreeNode> treeNodeStack = new Stack<>();
            treeNodeStack.push(root);
            for (int i = 0; i < times; i++) {
                //0表示后退，1表示左，2表示右
                int direction = ThreadLocalRandom.current().nextInt(i, times) % 3;
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
                        while (cur.left != null) {
                            treeNodeStack.push(cur.left);
                            //左移
                            cur = cur.left;
                        }
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            cur.left = new TreeNode(String.valueOf(i));
                            treeNodeStack.push(cur.left);
                        } else {
                            cur.right = new TreeNode(String.valueOf(i));
                            treeNodeStack.push(cur.right);
                        }


                    }
                    ;
                    break;
                    case 2: {
                        while (cur.right != null) {
                            treeNodeStack.push(cur.right);
                            cur = cur.right;

                        }
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            cur.left = new TreeNode(String.valueOf(i));
                            treeNodeStack.push(cur.left);
                        } else {
                            cur.right = new TreeNode(String.valueOf(i));
                            treeNodeStack.push(cur.right);
                        }

                    }
                    ;
                    break;
                }
            }
            return root;
        }

        @Override
        public TreeNode prepare() {
            //            TreeNode node = binaryTreeGenerator(1000,0);
            int nodeCount = 10000;
            TreeNode node = stackBinaryTreeGenerator(nodeCount);
            if (nodeCount <= 10)
                BTreePrinter.show(node);

            return node;
        }

        public TreeNode binaryTreeGenerator(int n, int integer) {

            if (n == 0)
                return null;

            TreeNode root = new TreeNode(String.valueOf(integer));

            // Number of nodes in the left subtree (in [0, n-1])
            int leftN = ThreadLocalRandom.current().nextInt(n);

            // Recursively build each subtree
            root.setLeft(binaryTreeGenerator(leftN, integer++));
            root.setRight(binaryTreeGenerator(n - leftN - 1, integer++));

            return root;
        }

        /**
         * 按照前序、中序、后序的方式合并
         *
         * @param data
         * @return
         */
        @Override
        protected String standard(TreeNode data) {
            StringBuilder stringBuilder = new StringBuilder();
            List<String> preNodes = new ArrayList<>();
            pre(data, s -> preNodes.add(s));
            stringBuilder.append(BTreePrinter.list2string(preNodes));
            stringBuilder.append(TREE_SPIN);
            log.debug("preNodes:{}", preNodes);
            List<String> midNodes = new ArrayList<>();
            mid(data, s -> midNodes.add(s));
            stringBuilder.append(BTreePrinter.list2string(midNodes));
            stringBuilder.append(TREE_SPIN);
            log.debug("midNodes:{}", midNodes);
            List<String> lastNodes = new ArrayList<>();
            last(data, s -> lastNodes.add(s));
            stringBuilder.append(BTreePrinter.list2string(lastNodes));
            stringBuilder.append(TREE_SPIN);
            log.debug("lastNodes:{}", lastNodes);

            return stringBuilder.toString();
        }

        /**
         * 先序递归,#表示null
         *
         * @param node
         * @param f
         */
        private void pre(TreeNode node, Consumer<String> f) {
            if (node == null) {
                f.accept(NULL_NODE);
                return;
            }
            //            System.out.println(node.value);
            f.accept(node.value);
            pre(node.left, f);
            pre(node.right, f);

        }

        /**
         * 中序递归
         *
         * @param node
         * @param c
         */
        private void mid(TreeNode node, Consumer<String> c) {
            if (node == null) {
                c.accept(NULL_NODE);
                return;
            }
            mid(node.left, c);
            c.accept(node.value);
            mid(node.right, c);
        }

        /**
         * 后序递归
         *
         * @param node
         * @param c
         */
        private void last(TreeNode node, Consumer<String> c) {
            if (node == null) {
                c.accept(NULL_NODE);
                return;
            }
            last(node.left, c);
            last(node.right, c);
            c.accept(node.value);
        }

        @SneakyThrows
        @Override
        protected String test(TreeNode data) {
            StringBuilder stringBuilder = new StringBuilder();
            List<String> preNodes = new ArrayList<>();
            preStack(data, s -> preNodes.add(s));
            stringBuilder.append(BTreePrinter.list2string(preNodes));
            stringBuilder.append(TREE_SPIN);
            log.debug("preStackNodes:{}", preNodes);
            List<String> midNodes = new ArrayList<>();
            midStack(data, s -> midNodes.add(s));
            stringBuilder.append(BTreePrinter.list2string(midNodes));
            stringBuilder.append(TREE_SPIN);
            log.debug("midStackNodes:{}", midNodes);
            List<String> lastNodes = new ArrayList<>();
            lastStack(data, s -> lastNodes.add(s));
            stringBuilder.append(BTreePrinter.list2string(lastNodes));
            stringBuilder.append(TREE_SPIN);
            log.debug("lastStackNodes:{}", lastNodes);
            return stringBuilder.toString();
        }

        /**
         * 非递归前序
         *
         * @param node
         * @param c
         */
        private void preStack1(TreeNode node, Consumer<String> c) {
            if (node == null) {
                return;
            }
            Stack<TreeNode> stack = new Stack<>();
            stack.push(node);
            while (!stack.isEmpty()) {
                TreeNode cur = stack.pop();
                c.accept(cur.value);
                if (cur.right != null) {
                    stack.push(cur.right);
                }
                if (cur.left != null) {
                    stack.push(cur.left);
                }
            }
        }

        private void preStack(TreeNode node, Consumer<String> c) {
            if (node == null) {
                return;
            }
            TreeNode cur = node;
            if (cur == null) {
                c.accept(NULL_NODE);
            }
            Stack<TreeNode> stack = new Stack<>();
            while (!stack.isEmpty() || cur != null) {
                if (cur != null) {
                    c.accept(cur.value);//放在left赋值之前，就是先序
                    stack.push(cur);
                    cur = cur.left;
                    if (cur == null) {
                        c.accept(NULL_NODE);
                    }
                } else {
                    cur = stack.pop();

                    cur = cur.right;
                    if (cur == null) {
                        c.accept(NULL_NODE);
                    }
                }
            }
        }

        /**
         * 非递归中序
         * 任何二叉树，都可以被左边界分解掉的。即：一直打印左边界，到头以后再打印左边界上最后一个右子树的第一个节点。
         * 在继续他的左边界，一直打印到头。
         * A
         * /    \
         * B      C
         * /  \   /
         * D    E  F
         * \
         * G
         * <p>
         * 分解左边界: A,B,D到头了，发现D的左子孩子和右孩子都没了没了，就退回到B，去找右子树(E)：重复进行左边界分解。
         * 注意：C-F的时候，发现F的左孩子没了。那就找F的右孩子（G）做上述操作
         * 这个过程本质上还是递归序。每个节点要进入三次。关键就看你的打印是在哪个步骤完成的
         * 同样，用右边界分解的方式也可以遍历树。每次边界分解都要入栈。
         * 先序：左边界分解，入栈前打印就是先序
         * 中序：左边界分解，左子树没了，遍历右子树（pop时）打印就是中序
         * 后续：右边界分解，入栈（遍历栈）前放入另外一个临时栈（结果栈），等遍历完以后，再挨个pop临时栈并打印就是后续
         *
         * @param node
         * @param c
         */
        private void midStack(TreeNode node, Consumer<String> c) {
            if (node == null) {
                return;
            }

            TreeNode cur = node;
            if (cur == null) {
                c.accept(NULL_NODE);
            }
            Stack<TreeNode> stack = new Stack<>();
            while (!stack.isEmpty() || cur != null) {
                if (cur != null) {
                    stack.push(cur);
                    cur = cur.left;
                    if (cur == null) {
                        c.accept(NULL_NODE);
                    }
                } else {
                    cur = stack.pop();
                    c.accept(cur.value);
                    cur = cur.right;
                    if (cur == null) {
                        c.accept(NULL_NODE);
                    }
                }
            }

        }

        /**
         * 非递归后序
         *
         * @param node
         * @param c
         */
        private void lastStack1(TreeNode node, Consumer<String> c) {
            if (node == null) {
                c.accept(NULL_NODE);
                return;
            }
            Stack<TreeNode> stack = new Stack<>();
            Stack<String> result = new Stack<>();
            stack.push(node);

            while (!stack.isEmpty()) {
                TreeNode cur = stack.pop();
                result.push(cur.value);
                if (cur.left != null) {
                    stack.push(cur.left);
                }
                if (cur.left == null) {
                    result.push(NULL_NODE);
                }
                if (cur.right != null) {
                    stack.push(cur.right);
                }
                if (cur.right == null) {
                    result.push(NULL_NODE);
                }
            }
            while (!result.isEmpty()) {
                String n = result.pop();
                c.accept(n);
            }
        }

        /**
         * 同一个方法用切分的方法，先切右边界，右边界切完，找右边界中的左子树。
         * 在找到的左子树中，继续切有边界。
         * 在第一个右边界的左子树进入的时候，压栈。
         * 最后全部都吐出来，就是后序遍历了
         * 先序中序后续都可以用一套思路：左边界切分、右边界切分的思路来搞。
         *
         * @param node
         * @param c
         */
        private void lastStack(TreeNode node, Consumer<String> c) {
            if (node == null) {
                return;
            }
            TreeNode cur = node;
            Stack<TreeNode> stack = new Stack<>();
            Stack<String> result = new Stack<>();
            while (!stack.isEmpty() || cur != null) {
                if (cur != null) {
                    result.push(cur.value);
                    stack.push(cur);
                    cur = cur.right;
                    if (cur == null) {
                        result.push(NULL_NODE);
                    }
                } else {
                    cur = stack.pop();
                    cur = cur.left;
                    if (cur == null) {
                        result.push(NULL_NODE);
                    }
                }
            }
            while (!result.isEmpty()) {
                c.accept(result.pop());
            }
        }
    }

    @AlgName("树宽度遍历")
    public static class TreeWideWalking extends AlgCompImpl<String, TreeNode> {
        TreeDeepWalking treeDeepWalking = new TreeDeepWalking();

        @Override
        public TreeNode prepare() {
            TreeNode node = treeDeepWalking.stackBinaryTreeGenerator(10);
            BTreePrinter.show(node);
            BTreePrinter.traversePreOrder(node);
            return node;
        }

        /**
         * 1. 头节点进队列
         * 2. 弹出队列时，有左孩子就左孩子进队列。然后有右孩子就右孩子进队列。
         * 3. 都没有了，就继续从队列的头弹出。循环第二步
         * 4. 直到所有节点都弹出。
         *
         * @param data
         * @return
         */
        @Override
        protected String standard(TreeNode data) {

            List<String> nodes = new ArrayList<>();
            wideSer(data, n -> nodes.add(n));
            String str = BTreePrinter.list2string(nodes);

            return str;
        }

        private void wideSer(TreeNode data, Consumer<String> cbk) {
            if (data == null) {
                cbk.accept(NULL_NODE);

            }
            Queue<TreeNode> queue = new ConcurrentLinkedQueue<>();
            //进队列
            queue.add(data);
            while (!queue.isEmpty()) {
                //每弹出一次
                TreeNode cur = queue.poll();
                if (cur == null) {
                    break;
                }
                cbk.accept(cur.value);
                //如果有左孩子就左孩子进队列
                if (cur.left != null) {
                    queue.add(cur.left);
                }
                //如果有右孩子就右孩子进队列
                if (cur.right != null) {
                    queue.add(cur.right);
                }
            }
        }

        @Override
        protected String test(TreeNode data) {
            List<String> nodes = new ArrayList<>();
            wideSer(data, n -> nodes.add(n));
            String str = BTreePrinter.list2string(nodes);

            return str;
        }
    }

    @AlgName("先序/后续/宽度序列化")
    public static class NodeSer extends AlgCompImpl<String, TreeNode> {
        TreeDeepWalking treeDeepWalking = new TreeDeepWalking();
        TreeWideWalking wideWalking = new TreeWideWalking();

        @Override
        public TreeNode prepare() {
            TreeNode node = treeDeepWalking.stackBinaryTreeGenerator(10);
            BTreePrinter.show(node);
            BTreePrinter.traversePreOrder(node);
            return node;
        }

        @Override
        protected String standard(TreeNode data) {

            Queue<String> preNodes = Queues.newConcurrentLinkedQueue();
            treeDeepWalking.pre(data, s -> {
                preNodes.add(s);
            });
            log.info("先序遍历：{}", preNodes);
            //完成序列化
            String nodes1 = BTreePrinter.list2string(preNodes);

            Queue<String> postNodes = Queues.newConcurrentLinkedQueue();
            treeDeepWalking.last(data, n -> {
                postNodes.add(n);
            });
            log.info("后序遍历序列化：{}", postNodes);

            String postNodeSer = BTreePrinter.list2string(postNodes);


            String wideNodeSer = wideWalking.standard(data);
            log.info("宽度优先遍历:{}", wideNodeSer);
            return nodes1 + TREE_SPIN + postNodeSer + TREE_SPIN + wideNodeSer;
        }

        /**
         * 递归先序反序列化，是队列的方式进行处理
         * 1. 先判空
         * 2. 如果poll的节点是空，则返回null
         * 3. 先左节点 等于递归调用
         * 4. 在右节点 等于递归调用
         * 5. 中间传递的递归是Queue
         *
         * @param queue
         * @return
         */
        public TreeNode preDeserNode(Queue<String> queue) {
            if (queue == null || queue.isEmpty()) {
                return null;
            }
            String val = queue.poll();
            if (val.equals(NULL_NODE)) {
                return null;
            }
            TreeNode node = new TreeNode(val);
            node.left = preDeserNode(queue);
            node.right = preDeserNode(queue);
            return node;
        }
        //后序遍历反序列化用栈的方式，因为最后的一定是root，所以要从栈的顶开始弹。

        /**
         * 后续遍历反序列化
         * 1. 递归后续反序列化，用递归弹栈的方式进行处理
         * 2. 先判空
         * 3. 如果pop出来的是空节点，则返回null
         * 4. 由于是栈的形式，栈中的逻辑是 左、右、头，因此先弹出来的是头，但是不设置值
         * 5. 再右节点 = 递归调用（栈）
         * 6. 再左节点 = 递归调用（栈）
         * 7. 最后设置当前节点的值 = 当前栈的值
         *
         * @param nodeStack
         * @return
         */
        public TreeNode postDeserNode(Stack<String> nodeStack) {
            if (nodeStack == null || nodeStack.isEmpty()) {
                return null;
            }
            String val = nodeStack.pop();
            if (val.equals(NULL_NODE)) {
                return null;
            }
            TreeNode node = new TreeNode();
            node.right = postDeserNode(nodeStack);
            node.left = postDeserNode(nodeStack);
            node.value = val;
            return node;
        }

        /**
         * 宽度遍历反序列化
         *
         * @param nodesStack
         * @return
         */
        public TreeNode wideDeserNode(Queue<String> nodesStack) {
            if (nodesStack == null || nodesStack.isEmpty()) {
                return null;
            }
            TreeNode root = generateNode(nodesStack.poll());
            //队列插值
            //
            Queue<TreeNode> queue = new LinkedList<>();
            if (root != null) {
                queue.add(root);
            }
            TreeNode node = null;
            while (!queue.isEmpty()) {
                node = queue.poll();
                //队列里面拿值，左右生成，并从nodesStack里面poll数据出来
                node.left = generateNode(nodesStack.poll());
                node.right = generateNode(nodesStack.poll());
                //再插回队列
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
            return root;
        }

        private TreeNode generateNode(String nodeVal) {
            if (nodeVal == null || StringUtils.isEmpty(nodeVal) || nodeVal.equals(NULL_NODE)) {
                return null;
            }
            TreeNode node = new TreeNode(nodeVal);
            return node;
        }

        @Override
        protected String test(TreeNode data) {
            Queue<String> preNodes = Queues.newConcurrentLinkedQueue();
            treeDeepWalking.pre(data, s -> {
                preNodes.add(s);
            });
            log.info("先序遍历：{}", preNodes);
            //完成反序列化
            TreeNode rootNode = preDeserNode(preNodes);
            Queue<String> printNodes = Queues.newConcurrentLinkedQueue();
            treeDeepWalking.pre(rootNode, s -> {
                printNodes.add(s);
            });
            //先序遍历反序列化结果
            String deSerNodeStr = BTreePrinter.list2string(printNodes);


            //进行后续遍历序列化和反序列化
            Stack<String> postStackNodes = new Stack<>();
            //后序遍历形成stack
            treeDeepWalking.last(data, n -> postStackNodes.push(n));

            log.info("后序遍历序列化:{}", postStackNodes);
            //后续遍历反序列化
            TreeNode postRootNode = postDeserNode(postStackNodes);
            Queue<String> printPostNodes = Queues.newConcurrentLinkedQueue();
            //后序遍历反序列化后打印结果
            treeDeepWalking.last(postRootNode, n -> {
                printPostNodes.add(n);
            });
            //后序遍历反序列化后打印结果，并形成字符串
            String deSerPostNodeStr = BTreePrinter.list2string(printPostNodes);

            //宽度优先
            Queue<String> wideNodes = Queues.newConcurrentLinkedQueue();
            //宽度优先遍历并序列化
            wideWalking.wideSer(data, n -> wideNodes.add(n));
            log.info("宽度优先遍历:{}", wideNodes);
            //宽度优先遍历反序列化
            TreeNode wideNodeRoot = wideDeserNode(wideNodes);
            //宽度优先遍历反序列化打印
            Queue<String> wideDescNodes = Queues.newConcurrentLinkedQueue();
            wideWalking.wideSer(wideNodeRoot, n -> wideDescNodes.add(n));
            String wideNodeSerStr = BTreePrinter.list2string(wideDescNodes);
            return deSerNodeStr + TREE_SPIN + deSerPostNodeStr + TREE_SPIN + wideNodeSerStr;

        }
    }

    /**
     * 请把一段纸条竖着放在桌子上，然后从纸条的下边向上方对折1次，压出折痕后展开。
     * 此时折痕是凹下去的，即折痕突起的方向指向纸条的背面。 如果从纸条的下边向上方连续对折2次，
     * 压出折痕后展开，此时有三条折痕，从上到下依次是下折痕、下折痕和上折痕。
     * 给定一个输入参数N，代表纸条都从下边向上方连续对折N次。 请从上到下打印所有折痕的方向。
     */
    @AlgName("折纸测试")
    //TODO
    public static class PaperFloading extends AlgCompImpl<String, Integer> {
        /**
         * 折叠次数
         *
         * @return
         */
        @Override
        public Integer prepare() {
            return ThreadLocalRandom.current().nextInt(0, 100);
        }

        /**
         * 从上到下的顺序是
         * 用用0表示凹，用1表示凸。那么折1次，结果是0，折2次结果是001，折3次是0010011
         * 逻辑是
         * 0           折第一次
         * / ↓  \
         * 0  ↓   1        折第二次
         * /↓\ ↓  /↓ \
         * 0 ↓ 1↓ 0 ↓  1      折第三次
         * ↓ ↓ ↓↓ ↓ ↓  ↓
         * 0 0 1 0 0 1 1    这是打开后看到的
         * 所以，本质上是左0右1的二叉树。然后按照先序遍历
         *
         * @param data
         * @return
         */
        @Override
        protected String standard(Integer data) {
            return null;
        }

        @Override
        protected String test(Integer data) {
            return null;
        }
    }

    /**
     * 多叉树序列化成二叉树，然后二叉树还能转换回去。
     * 思路，用左树右边界代表多叉树的孩子。本质上是用左树做深度下一层节点，右边界做同一层的兄弟节点
     * 简单来说，第一个孩子是左子树，然后剩下的所有孩子是左子树的右边的孩子。然后
     *        A
     *      /   \
     *    B       C
     * / / \   / \ \ \
     * D E  F   G H I J
     * 转化为
     *          A
     *       /
     *      B
     *    /  \
     *   D    C
     *    \    /
     *     E   G
     *      \   \
     *      F  H
     *          \
     *           I
     *            \
     *            J
     * 采用的是深度优先策略。
     */
    @AlgName("NaryTree2BTree-Leetcode431")
    public static class NaryTreeToBTree extends AlgCompImpl<String, MultiTreeNode> {
        private static AtomicInteger count = new AtomicInteger(0);

        @Override
        public MultiTreeNode prepare() {

            MultiTreeNode root = new MultiTreeNode("-1");
            prepare(root);
            return root;
        }

        public void prepare(MultiTreeNode node) {

            //每层有多少个孩子
            int child = ThreadLocalRandom.current().nextInt(1, 5);
            if (count.incrementAndGet() < 20) {
                List<MultiTreeNode> childNodes = new ArrayList<>(child);
                for (int j = 0; j < child; j++) {
                    int val = ThreadLocalRandom.current().nextInt(1, 100000);
                    count.incrementAndGet();
                    childNodes.add(j, new MultiTreeNode(String.valueOf(val)));
                }
                for (MultiTreeNode m : childNodes) {
                    prepare(m);
                }
                node.setChild(childNodes);
            }

        }

        @Override
        protected String standard(MultiTreeNode root) {
            log.info("{}", JSONUtil.toJsonStr(root));
            return JSONUtil.toJsonStr(root);
        }

        /**
         * 多叉树转化成二叉树
         */
        public TreeNode encode(MultiTreeNode multiTreeNode) {
            if (multiTreeNode == null) {
                return null;
            }
            TreeNode root = new TreeNode(multiTreeNode.val);
            root.left = en(multiTreeNode.child);
            return root;
        }

        /**
         * @param child
         * @return
         */
        private TreeNode en(List<MultiTreeNode> child) {
            if (child == null || child.isEmpty()) {
                return null;
            }
            TreeNode head = null;
            TreeNode cur = null;
            for (MultiTreeNode n : child) {
                TreeNode tNode = new TreeNode(n.val);
                if (head == null) {
                    head = tNode;
                } else {
                    cur.right = tNode;
                }
                cur = tNode;
                cur.left = en(n.child);
            }
            return head;
        }

        /**
         * 二叉树转化为多叉树
         */
        public MultiTreeNode decode(TreeNode root) {
            if (root == null) {
                return null;
            }
            return new MultiTreeNode(root.value, de(root.left));
        }

        private List<MultiTreeNode> de(TreeNode root) {
            List<MultiTreeNode> child = new ArrayList<>();
            while (root != null) {
                MultiTreeNode cur = new MultiTreeNode(root.value, de(root.left));
                child.add(cur);
                root = root.right;
            }
            if (child == null || child.isEmpty()) {
                return null;
            }
            return child;
        }

        @Override
        protected String test(MultiTreeNode root) {
            TreeNode binTreeRoot = encode(root);
            log.info("二叉树:{}", JSONUtil.toJsonStr(binTreeRoot));
            MultiTreeNode decodeMultiRoot = decode(binTreeRoot);
            log.info("多叉树:{}", JSONUtil.toJsonStr(decodeMultiRoot));
            return JSONUtil.toJsonStr(decodeMultiRoot);
        }
    }

    @Data
    public static class MultiTreeNode {
        //-1表示这个是根节点
        private String val;
        private List<MultiTreeNode> child;

        public MultiTreeNode(String val) {
            this.val = val;
        }

        public MultiTreeNode(String val, List<MultiTreeNode> child) {
            this.val = val;
            this.child = child;
        }

    }

    /**
     * 判断是否是完全二叉树
     * 完全二叉树的检查——按层遍历+某些原则：
     * 原则：
     * 1. 某个节点有右孩子，没有左孩子。肯定不是完全二叉树。
     * 2. 第一个孩子不双全的情况下，那么后面的节点必须是叶节点，否则肯定不是完全二叉树。
     * <p>
     * 边界：null算作完全二叉树。
     * 针对原则1：有右无左，肯定不是完全二叉树。
     * 针对原则2：曾经遇到叶子节点，且当前节点有孩子，肯定不是完全二叉树。
     * 左孩不空左进队，右孩不空右进队。
     * 左右任空，就是叶子节点（影响原则2）
     */
    @AlgName("判断是否为完全二叉树")
    public static class IsCBT extends AlgCompImpl<Boolean,
            BinTreeNode<Integer,String>> {

        @Override
        public BinTreeNode<Integer,String> prepare() {
            int size = 100000;
            SimpleNormalBinTreeGenerator simpleNormalBinTreeGenerator =
                    new SimpleNormalBinTreeGenerator();

            CompleteBinTreeGenerator completeBinTreeGenerator  =
                    new CompleteBinTreeGenerator();
            return ThreadLocalRandom.current().nextBoolean()?
                    simpleNormalBinTreeGenerator.generate(size):
                    completeBinTreeGenerator.generate(size);

            /*
            root
          3
        7
       8
             */
            //            TreeNode wrongNode = new TreeNode("root");
            //            wrongNode.left = new TreeNode("3");
            //            wrongNode.left.left = new TreeNode("7");
            //            wrongNode.left.left.left = new TreeNode("8");
            //            return wrongNode;
        }



        @Override
        protected Boolean standard(BinTreeNode<Integer,String> data) {
            return process2(data).isCBT;
//            if (null == data) {
//                return true;
//            }
//            BinTreeNode<Integer,String> leftChild = null;
//            BinTreeNode<Integer,String> rightChild = null;
//            boolean left = false;
//            Queue<BinTreeNode<Integer,String>> queue = new LinkedList<>();
//            queue.offer(data);
//            while (!queue.isEmpty()) {
//                BinTreeNode<Integer,String> head = queue.poll();
//                leftChild = head.getLeftNode();
//                rightChild = head.getRightNode();
//                if ((null != rightChild && null == leftChild) //右孩子不等于空，左孩子等于空  -> false
//                        || (left && (null != rightChild || null != leftChild)) //开启叶节点判断标志位时，如果层次遍历中的后继结点不是叶节点 -> false
//                ) {
//                    return false;
//                }
//                if (null != leftChild) {
//                    queue.offer(leftChild);
//                }
//                if (null != rightChild) {
//                    queue.offer(rightChild);
//                } else {
//                    left = true;
//                }
//            }
//
//            return true;

        }

        /**
         * 采用递归方式
         * 	1. 左边是满二叉树，右边是满二叉树，左边高度=右边高度。
         * 	2. 左边是完全二叉树，右边是满二叉树，左边高度 = 右边高度+1
         * 	3. 左边是满二叉树，右边是满二叉树，左边高度 = 右边高度+1
         * 	4. 左边是满二叉树，右边是完全二叉树，左边高度=右边高度
         * @param node
         * @return
         */
        public NodeInfo4CBT process2(BinTreeNode<Integer,String >node){
            if(node == null){
                return new NodeInfo4CBT(0,true,true);
            }
            NodeInfo4CBT leftInfo = process2(node.getLeftNode());
            NodeInfo4CBT rightInfo = process2(node.getRightNode());
            boolean isFBT =
                    leftInfo.isFBT && rightInfo.isFBT && leftInfo.height == rightInfo.height;
            boolean isCBT = isFBT;
            //左边是完全二叉树，右边是满二叉树，左边高度 = 右边高度+1
            if(leftInfo.isCBT && rightInfo.isFBT && leftInfo.height ==
                    rightInfo.height+1){
                isCBT = true;
            }
            else if(leftInfo.isFBT && rightInfo.isFBT && leftInfo.height == rightInfo.height+1){
                isCBT = true;
            }
            else if(leftInfo.isFBT && rightInfo.isCBT && leftInfo.height == rightInfo.height){
                isCBT = true;
            }
            int height = Math.max(leftInfo.height,rightInfo.height)+1;
            return new NodeInfo4CBT(height,isCBT,isFBT);

        }
        @Override
        protected Boolean test(BinTreeNode<Integer,String> data) {
            return process(data);
        }

        public boolean process(BinTreeNode<Integer,String> node) {
            Queue<BinTreeNode<Integer,String>> queue = Queues.newLinkedBlockingQueue();
            boolean hasMeetingLeaf = false;
            //base case
            if (node == null) {
                return true;
            }
            queue.add(node);

            while (!queue.isEmpty()) {
                BinTreeNode<Integer,String> curNode = queue.poll();
                BinTreeNode<Integer,String> leftNode = curNode.getLeftNode();
                BinTreeNode<Integer,String> rightNode = curNode.getRightNode();
                if (leftNode != null) {
                    queue.offer(leftNode);
                }
                if (rightNode != null) {
                    queue.offer(rightNode);
                }
                if (leftNode == null && rightNode != null) {
                    //左子树没节点，右子树有节点，肯定不是完全二叉树
                    return false;
                }
                //如果曾经遇到过叶子节点
                if (hasMeetingLeaf && (leftNode != null || rightNode != null)) {
                    return false;
                }
                //注意这里，有任何一个节点是空的，就判定为叶子。这里是大坑
                if (leftNode == null || rightNode == null) {
                    //表示当前节点是叶子节点，因此将遇到过叶子节点设置为true
                    hasMeetingLeaf = true;
                }
            }

            return true;

        }

        @Data
        @AllArgsConstructor
        public static class PopInfo {

            /**
             * 是否是叶子
             */
            private boolean isLeaf;
            /**
             * 该节点下面是否全都是完全二叉树
             */
            private boolean isCBT;
        }
        @Data
        @AllArgsConstructor
        public static class NodeInfo4CBT{
            //树的高度
            private int height;
            //是否为完全二叉树
            private boolean isCBT;
            /**
             * 是否是满二叉树，满二叉树是完全二叉树中一种特殊情况。
             */
            private boolean isFBT;
        }
    }

    /**
     * 判断是否为搜索二叉树
     * 搜索二叉树的定义：左节点比中小，右节点比中大。不存在相同值
     */
    @AlgName("判断是否为搜索二叉树")
    public static class IsSBT extends TreeAlgComImpl<Boolean, BinTreeNode<Integer, String>> {

        @Override
        public BinTreeNode<Integer, String> prepare() {
            SimpleNormalBinTreeGenerator generator = new SimpleNormalBinTreeGenerator();
            int size = 10000;
            BalanceSearchBinTreeGenerator bstGenerator =
                    new BalanceSearchBinTreeGenerator();


                        return ThreadLocalRandom.current().nextBoolean()?
                                bstGenerator.generate(size):
                                generator.generate(size);
        }

        @Override
        protected Boolean standard(BinTreeNode<Integer, String> data) {
            return this.preorder(data, Long.MIN_VALUE, Long.MAX_VALUE);
        }

        public boolean preorder(BinTreeNode<Integer, String> root, long min, long max) {
            if (root == null) {
                return true;
            } else if (root.getKey() <= min || root.getKey() >= max) {
                return false;
            } else {
                boolean b1 = this.preorder(root.getLeftNode(), min, root.getKey());
                boolean b2 = this.preorder(root.getRightNode(), root.getKey(), max);

                return b1 && b2;
            }
        }


        @Override
        protected Boolean test(BinTreeNode<Integer, String> data) {

            SBTPopInfo info = process(data);
            if (info == null) {
                return true;
            }
            return info.isSBT();
        }

        private SBTPopInfo process(BinTreeNode<Integer, String> node) {
            if (node == null) {
                return null;
            }

            SBTPopInfo leftInfo = process(node.getLeftNode());
            SBTPopInfo rightInfo = process(node.getRightNode());

            boolean isSbt = true;
            int min = node.getKey();
            int max = node.getKey();
            //分为经过当前节点和不经过当前节点的
            if (leftInfo != null) {
                isSbt =
                        isSbt && leftInfo.isSBT && leftInfo.getMax() < node.getKey();
                min = leftInfo.getMin();
            }
            if (rightInfo != null) {
                isSbt =
                        isSbt && rightInfo.isSBT && rightInfo.getMin() > node.getKey();
                max = rightInfo.getMax();
            }

            SBTPopInfo result = new SBTPopInfo();
            result.setSBT(isSbt);
            result.setMax(max);
            result.setMin(min);
            return result;
        }

        @Data
        public static class SBTPopInfo {
            private int max;
            private int min;
            private boolean isSBT;
        }
    }

    /**
     * 判断为搜索平衡二叉树
     */
    @AlgName("判断是否为平衡搜索二叉树")
    public static class IsAVLTree extends TreeAlgComImpl<Boolean,
            BinTreeNode<Integer,String>>{

        @Override
        public BinTreeNode<Integer, String> prepare() {
            return null;
        }

        @Override
        protected Boolean standard(BinTreeNode<Integer, String> data) {
            return null;
        }

        @Override
        protected Boolean test(BinTreeNode<Integer, String> data) {
            return null;
        }
    }
    /**
     * 最大距离：指的是树中路径的path
     * 几种可能：
     * 假设X是子树根节点
     * 情况1：最大路径不经过X：X的左子树的最大距离最大
     * 情况2：最大路径不经过X：X的右子树的最大距离最大
     * 情况3：最大路径经过X：左子树高度+右子树高度+1
     * <p>
     * 因此若用递归则需要考虑最后的叶子节点的left或right，此时X为null，可以定义为高度为0，最大距离为0
     * 因此需要向上传导两个信息，分别是当前节点X的高度和最大距离
     * 当前节点的高度=MAX(左节点高度，右子树高度）
     * 当前节点最大距离=MAX(当前节点高度，左子树最大距离，右子树最大距离）
     * 计算下一层节点时，使用上述逻辑
     */
    @AlgName("计算树中的最大距离")
    public static class TreeMaxPath extends TreeAlgComImpl<Boolean,
            BinTreeNode<Integer,String>>{

        @Override
        public BinTreeNode<Integer, String> prepare() {
            return null;
        }

        @Override
        protected Boolean standard(BinTreeNode<Integer, String> data) {
            return null;
        }

        @Override
        protected Boolean test(BinTreeNode<Integer, String> data) {
            return null;
        }
    }

    /**
     * 满二叉树定义：每一个非叶子节点，都有左右两个子节点
     * 满二叉树判定：满二叉树的高度为H， 则所有节点个数应为 2^H -1
     * 判断一棵树是不是满二叉树
     * 在做状态分析，可能性分析，设计递归的时候，要先分析问题。分析问题时，可以根据问题的（性质）、特点、边界作为分析的依据。
     *
     * 满二叉树的特点：满二叉树的节点一定是 2h-1 个节点，其中h是树的高度。节点数指的是所有该高度下所有的子节点数量
     *
     * 递归返回的数据是： 树的高度和树的节点数。
     *
     * 	树的高度：max(左树高度,右树高度)+1
     * 	树的节点数： 左树节点数 + 右树节点数 +1
     *
     * base case： 节点类型为null 则此时数高度是0，树节点数是0
     */
    @AlgName("判断一棵树是不是满二叉树")
    public static class IsFullBT extends TreeAlgComImpl<Boolean,
            BinTreeNode<Integer,String>>{

        @Override
        public BinTreeNode<Integer, String> prepare() {
            return null;
        }

        @Override
        protected Boolean standard(BinTreeNode<Integer, String> data) {
            return null;
        }

        @Override
        protected Boolean test(BinTreeNode<Integer, String> data) {
            return null;
        }
    }

    /**
     * 	1. 设定目标：X作为树头节点上，最大的搜索二叉树节点个数
     * 	2. 分析问题特征：搜索二叉树特点：
     * 	► X的左树是搜索二叉树
     * 	► X的右树是搜索二叉树
     * 	► X的左树的最大值必须比X的值小
     * 	► X的右树的最小值必须比X的值大
     *
     * 	3. 条件和状态：（这步是关键）
     * 		a. 假如X不做头：
     * 			i. 左子树的MaxSBTSize
     * 			ii. 右子树的MaxSBTSize
     * 			iii. 求上面两个MaxBSTSize的最大值，就是这个子树的最大搜索二叉树的节点个数
     * 		b. 假如X做头：
     * 			i. 左子树必须是SBT
     * 			ii. 右子树必须是SBT
     * 			iii. 左子树的最大值小于X的值
     * 			iv. 右子树的最小值大于X的值
     * 			v. 知道左子树和右子树的size，然后再加1，就是这个子树的最大搜索二叉树的节点个数
     * 	4. 状态之间的关系，max(x做头的情况 vs x不做头的情况) --->向上传递，递归。
     * 	5. base case 节点为null的时候，
     */
    @AlgName("找到一棵树中最大的搜索二叉树子树")
    public static class FindMaxSBT extends TreeAlgComImpl<Boolean,
            BinTreeNode<Integer,String>>{

        @Override
        public BinTreeNode<Integer, String> prepare() {
            return null;
        }

        @Override
        protected Boolean standard(BinTreeNode<Integer, String> data) {
            return null;
        }

        @Override
        protected Boolean test(BinTreeNode<Integer, String> data) {
            return null;
        }
    }


    /**
     * 给定一个节点X，以及A,B两个节点，返回他们的高度最低的祖先节点。
     * 注意，这里的节点X，可能不是头节点。所以有一种可能是X节点下面，可能不包含A节点或者B节点
     */
    @AlgName("找到AB节点最低祖先节点")
    public static class LowestAncestors extends TreeAlgComImpl<BinTreeNode<Integer, String>,
            BinTreeNode<Integer,String>[]>{

        /**
         * BinTreeNode<Integer,String>[]中0表示X节点，1表示A，2表示B
         * @return
         */
        @Override
        public BinTreeNode<Integer, String>[] prepare() {
            int size = 100000;
            BalanceSearchBinTreeGenerator bstGenerator =
                    new BalanceSearchBinTreeGenerator();
            BinTreeNode<Integer,String> root = bstGenerator.generate(size);
            LinkedBlockingQueue<BinTreeNode<Integer,String>> queue =
                    new LinkedBlockingQueue<>();
            ArrayList<BinTreeNode<Integer, String>> result = new ArrayList<>();
            queue.add(root);
            while (!queue.isEmpty()){
                BinTreeNode<Integer, String> node =   queue.poll();
                if(node.getLeftNode() != null){
                    queue.add(node.getLeftNode());
                    result.add(node.getLeftNode());
                }
                if(node.getRightNode() != null){
                    queue.add(node.getRightNode());
                    result.add(node.getRightNode());
                }
            }
            int left = ThreadLocalRandom.current().nextInt(0,result.size()-1);
            int right = ThreadLocalRandom.current().nextInt(0,result.size()-1);
            int head = ThreadLocalRandom.current().nextInt(0,result.size()-1);
            while (left == right){
                right = ThreadLocalRandom.current().nextInt(0,result.size()-1);
            }
            BinTreeNode x =
                    ThreadLocalRandom.current().nextBoolean()?root:
                    result.get(head);
            return new BinTreeNode[]{x,result.get(left),result.get(right)};
        }

        @Override
        protected BinTreeNode<Integer, String> standard(BinTreeNode<Integer, String>[] data) {
            return lowestAncestor1(data[0],data[1],data[2]);
//            return null;
        }

        public static BinTreeNode<Integer, String> lowestAncestor1(BinTreeNode<Integer, String> head, BinTreeNode<Integer, String> o1, BinTreeNode<Integer, String> o2) {

            if (head == null) {
                return null;
            }
            // key的父节点是value
            HashMap<BinTreeNode<Integer, String>, BinTreeNode<Integer, String>> parentMap = new HashMap<>();
            parentMap.put(head, null);
            fillParentMap(head, parentMap);
            HashSet<BinTreeNode<Integer, String>> o1Set = new HashSet<>();
            BinTreeNode<Integer, String> cur = o1;
            o1Set.add(cur);
            while (parentMap.get(cur) != null) {
                cur = parentMap.get(cur);
                o1Set.add(cur);
            }
            cur = o2;
            while (!o1Set.contains(cur) && cur != null) {
                cur = parentMap.get(cur);
            }
            return cur;
        }

        public static void fillParentMap(BinTreeNode<Integer, String> head, HashMap<BinTreeNode<Integer, String>, BinTreeNode<Integer, String>> parentMap) {
            if (head.getLeftNode() != null) {
                parentMap.put(head.getLeftNode(), head);
                fillParentMap(head.getLeftNode(), parentMap);
            }
            if (head.getRightNode() != null) {
                parentMap.put(head.getRightNode(), head);
                fillParentMap(head.getRightNode(), parentMap);
            }
        }

        @Override
        protected BinTreeNode<Integer, String> test(BinTreeNode<Integer, String>[] data) {
            LowestAncestorsInfo info = find(data);
            return info.crossNode;
        }
        public LowestAncestorsInfo find(BinTreeNode<Integer, String>[] data){
            BinTreeNode<Integer, String> x = data[0];
            BinTreeNode<Integer, String> A = data[1];
            BinTreeNode<Integer, String> B = data[2];
            if(x == null){
                return new LowestAncestorsInfo(false,false,0,false,null);
            }
            LowestAncestorsInfo leftInfo =
                    find(new BinTreeNode[]{x.getLeftNode(),A,
                            B});
            LowestAncestorsInfo rightInfo =
                    find(new BinTreeNode[]{x.getRightNode(),A,B});
            if (leftInfo.cross){
                return leftInfo;
            }
            if(rightInfo.cross){
                return rightInfo;
            }
            boolean isContainA =
                    leftInfo.isContainA || rightInfo.isContainA ||x.getKey().equals(A.getKey());
            boolean isContainB =
                    leftInfo.isContainB || rightInfo.isContainB || x.getKey().equals(B.getKey());
            boolean cross = isContainA && isContainB;
            int height = Math.max(leftInfo.height,rightInfo.height)+1;
            BinTreeNode<Integer, String> crossNode = null;
            if(cross){
                crossNode = x;
            }
            return new LowestAncestorsInfo(isContainA,isContainB,height,cross
                    ,crossNode);


        }
        @Data
        @AllArgsConstructor
        public static class LowestAncestorsInfo{
            private boolean isContainA;
            private boolean isContainB;
            private int height;
            private boolean cross;
            private BinTreeNode<Integer, String> crossNode;

        }
    }


    /**
     * 最大欢乐树节点
     */
    public static class Employee {
        public int happy;
        public List<Employee> nexts;

        public Employee(int h) {
            happy = h;
            nexts = new ArrayList<>();
        }

    }

    /**
     * 最大欢乐树
     * 公司的每个员工都符合 Employee 类的描述。整个公司的人员结构可以看作是一棵标准的、
     * 没有环的多叉树。树的头节点是公司唯一的老板。除老板之外的每个员工都有唯一的直接上级。
     * 叶节点是没有任何下属的基层员工(subordinates列表为空)，除基层员工外，每个员工都有一个或多个直接下级。
     * 这个公司现在要办party，你可以决定哪些员工来，哪些员工不来，规则：
     * 1.如果某个员工来了，那么这个员工的所有直接下级都不能来
     * 2.派对的整体快乐值是所有到场员工快乐值的累加
     * 3.你的目标是让派对的整体快乐值尽量大
     * 给定一棵多叉树的头节点boss，请返回派对的最大快乐值
     */
    @AlgName("MaxHappy")
    public static class MaxHappy extends AlgCompImpl<Integer, Employee>{

        @Override
        public Employee prepare() {
            int maxLevel = 4;
            int maxNexts = 7;
            int maxHappy = 100;
            Employee boss = genarateBoss(maxLevel, maxNexts, maxHappy);
            return boss;
        }
        public static Employee genarateBoss(int maxLevel, int maxNexts, int maxHappy) {
            if (Math.random() < 0.02) {
                return null;
            }
            Employee boss = new Employee((int) (Math.random() * (maxHappy + 1)));
            genarateNexts(boss, 1, maxLevel, maxNexts, maxHappy);
            return boss;
        }
        public static void genarateNexts(Employee e, int level, int maxLevel, int maxNexts, int maxHappy) {
            if (level > maxLevel) {
                return;
            }
            int nextsSize = (int) (Math.random() * (maxNexts + 1));
            for (int i = 0; i < nextsSize; i++) {
                Employee next = new Employee((int) (Math.random() * (maxHappy + 1)));
                e.nexts.add(next);
                genarateNexts(next, level + 1, maxLevel, maxNexts, maxHappy);
            }
        }

        @Override
        protected Integer standard(Employee boss) {
            if (boss == null) {
                return 0;
            }
            return process1(boss, false);
        }
        // 当前来到的节点叫cur，
        // up表示cur的上级是否来，
        // 该函数含义：
        // 如果up为true，表示在cur上级已经确定来，的情况下，cur整棵树能够提供最大的快乐值是多少？
        // 如果up为false，表示在cur上级已经确定不来，的情况下，cur整棵树能够提供最大的快乐值是多少？
        public static int process1(Employee cur, boolean up) {
            if (up) { // 如果cur的上级来的话，cur没得选，只能不来
                int ans = 0;
                for (Employee next : cur.nexts) {
                    ans += process1(next, false);
                }
                return ans;
            } else { // 如果cur的上级不来的话，cur可以选，可以来也可以不来
                int p1 = cur.happy;
                int p2 = 0;
                for (Employee next : cur.nexts) {
                    p1 += process1(next, true);
                    p2 += process1(next, false);
                }
                return Math.max(p1, p2);
            }
        }

        @Override
        protected Integer test(Employee boss) {
            MaxHappyPopInfo info = process2(boss);
            return Math.max(info.join,info.notJoin);
        }
        public MaxHappyPopInfo process2(Employee x){
            if(x == null){
                return new MaxHappyPopInfo(0,0);
            }
            int join = x.happy;
            int notJoin = 0;
            for(Employee e : x.nexts){

                MaxHappyPopInfo childInfo = process2(e);
                //如果x参加的话，那就只能计算x的孩子们不来的值
                join = join + childInfo.notJoin;
                notJoin = notJoin + Math.max(childInfo.join,childInfo.notJoin);

            }
            return new MaxHappyPopInfo(join,notJoin);
        }
        @Data
        @AllArgsConstructor
        public class MaxHappyPopInfo{
            /**
             * 表示X节点来的最大值
             */
            int join;
            /**
             * 表示X节点不来的最大值
             */
            int notJoin;
        }
    }
}


