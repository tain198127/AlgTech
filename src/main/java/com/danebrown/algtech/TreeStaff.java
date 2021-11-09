package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.C;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by danebrown on 2021/9/24
 * mail: tain198127@163.com
 * 树相关算法
 * @author danebrown
 */
@Log4j2
public class TreeStaff {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new TreeWalking());
        AlgCompMenu.run();
    }
    //六种序：左头右，左右头，右左头，右头左，头左右，头右左
    //先序：头左右，中序：左头右，后续：左右头
    //可是先序、中序、后续的本质是：递归序
    //某个数X，其先序数组，A，后续数组B，X在A的左边A[0,X) 相交 B(X,N]，其交集是且仅是X的祖先节点。这是结论，如何证明？
    //要能证明
    @Data
    public static class TreeNode{
        private String value;
        private TreeNode left;
        private TreeNode right;
        public TreeNode(String value){
            this.value = value;
        }
    }
    @AlgName("树遍历")
    public static class TreeWalking extends AlgCompImpl<String,
            TreeNode>{
        public static int getTreeDepth(TreeNode root) {
            return root == null ? 0 : (1 + Math.max(getTreeDepth(root.left), getTreeDepth(root.right)));
        }

        private static void writeArray(TreeNode currNode, int rowIndex, int columnIndex, String[][] res, int treeDepth) {
            // 保证输入的树不为空
            if (currNode == null) return;
            // 先将当前节点保存到二维数组中
            res[rowIndex][columnIndex] = String.valueOf(currNode.value);

            // 计算当前位于树的第几层
            int currLevel = ((rowIndex + 1) / 2);
            // 若到了最后一层，则返回
            if (currLevel == treeDepth) return;
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

        public static void show(TreeNode root) {
            if (root == null) System.out.println("EMPTY!");
            // 得到树的深度
            int treeDepth = getTreeDepth(root);

            // 最后一行的宽度为2的（n - 1）次方乘3，再加1
            // 作为整个二维数组的宽度
            int arrayHeight = treeDepth * 2 - 1;
            int arrayWidth = (2 << (treeDepth - 2)) * 3 + 1;
            // 用一个字符串数组来存储每个位置应显示的元素
            String[][] res = new String[arrayHeight][arrayWidth];
            // 对数组进行初始化，默认为一个空格
            for (int i = 0; i < arrayHeight; i ++) {
                for (int j = 0; j < arrayWidth; j ++) {
                    res[i][j] = " ";
                }
            }

            // 从根节点开始，递归处理整个树
            // res[0][(arrayWidth + 1)/ 2] = (char)(root.val + '0');
            writeArray(root, 0, arrayWidth/ 2, res, treeDepth);

            // 此时，已经将所有需要显示的元素储存到了二维数组中，将其拼接并打印即可
            for (String[] line: res) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < line.length; i ++) {
                    sb.append(line[i]);
                    if (line[i].length() > 1 && i <= line.length - 1) {
                        i += line[i].length() > 4 ? 2: line[i].length() - 1;
                    }
                }
                System.out.println(sb.toString());
            }
        }
        public TreeNode stackBinaryTreeGenerator(int times) {
            TreeNode root = new TreeNode("root");
            TreeNode cur = root;
            Stack<TreeNode> treeNodeStack = new Stack<>();
            treeNodeStack.push(root);
            for (int i = 0; i < times; i++) {
                //0表示后退，1表示左，2表示右
                int direction = ThreadLocalRandom.current().nextInt(i,times)%3;
                switch (direction){
                    case 0:{
                        if(treeNodeStack.isEmpty()){
                            continue;
                        }
                        int popUpper = ThreadLocalRandom.current().nextInt(1,
                                treeNodeStack.size()+1);
                        while(!treeNodeStack.isEmpty() && popUpper >0){
                            popUpper--;
                            cur = treeNodeStack.pop();
                        }
                    };break;
                    case 1:{
                        while (cur.left != null){
                            treeNodeStack.push(cur.left);
                            //左移
                            cur = cur.left;
                        }
                        if(ThreadLocalRandom.current().nextBoolean()){
                            cur.left = new TreeNode(String.valueOf(i));
                            treeNodeStack.push(cur.left);
                        }else{
                            cur.right = new TreeNode(String.valueOf(i));
                            treeNodeStack.push(cur.right);
                        }


                    };break;
                    case 2:{
                        while (cur.right != null){
                            treeNodeStack.push(cur.right);
                            cur = cur.right;

                        }
                        if(ThreadLocalRandom.current().nextBoolean()){
                            cur.left = new TreeNode(String.valueOf(i));
                            treeNodeStack.push(cur.left);
                        }else{
                            cur.right = new TreeNode(String.valueOf(i));
                            treeNodeStack.push(cur.right);
                        }

                    };break;
                }
            }
            return root;
        }
        volatile AtomicInteger integer = new AtomicInteger(0);
        @Override
        public TreeNode prepare() {
//            TreeNode node = binaryTreeGenerator(1000,0);
            TreeNode node = stackBinaryTreeGenerator(10000);
//            show(node);
            return node;
        }
        public TreeNode binaryTreeGenerator(int n,int integer){

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
         * @param data
         * @return
         */
        @Override
        protected String standard(TreeNode data) {
//            show(data);
            List<String> preNodes = new ArrayList<>();
            pre(data, s -> preNodes.add(s));
            preNodes.add("|");
            log.debug("preNodes:{}",preNodes);
            List<String> midNodes = new ArrayList<>();
            mid(data,s -> midNodes.add(s));
            midNodes.add("|");
            log.debug("midNodes:{}",midNodes);
            List<String> lastNodes = new ArrayList<>();
            last(data,s->lastNodes.add(s));
            lastNodes.add("|");
            log.debug("lastNodes:{}",lastNodes);
            preNodes.addAll(midNodes);
            preNodes.addAll(lastNodes);
            final String[] result = {""};
            preNodes.forEach(item->{
                result[0] += item;
            });
            log.info("{}",result[0]);
            return result[0];
        }

        /**
         * 先序递归
         * @param node
         * @param f
         */
        private void pre(TreeNode node, Consumer<String> f){
            if(node == null){
                return;
            }
//            System.out.println(node.value);
            f.accept(node.value);
            pre(node.left,f);
            pre(node.right,f);

        }

        /**
         * 中序递归
         * @param node
         * @param c
         */
        private void mid(TreeNode node,Consumer<String> c){
            if(node == null){
                return;
            }
            mid(node.left,c);
            c.accept(node.value);
            mid(node.right,c);
        }

        /**
         * 后序递归
         * @param node
         * @param c
         */
        private void last(TreeNode node,Consumer<String> c){
            if(node == null){
                return;
            }
            last(node.left,c);
            last(node.right,c);
            c.accept(node.value);
        }

        @SneakyThrows
        @Override
        protected String test(TreeNode data) {
            List<String> preNodes = new ArrayList<>();
            preStack(data, s -> preNodes.add(s));
            preNodes.add("|");
            log.debug("preStackNodes:{}",preNodes);
            List<String> midNodes = new ArrayList<>();
            midStack(data,s -> midNodes.add(s));
            midNodes.add("|");
            log.debug("midStackNodes:{}",midNodes);
            List<String> lastNodes = new ArrayList<>();
            lastStack(data,s->lastNodes.add(s));
            lastNodes.add("|");
            log.debug("lastStackNodes:{}",lastNodes);
            preNodes.addAll(midNodes);
            preNodes.addAll(lastNodes);
            final String[] result = {""};
            preNodes.forEach(item->{
                result[0] += item;
            });
            log.info("{}",result[0]);
            return result[0];

        }

        /**
         * 非递归前序
         * @param node
         * @param c
         */
        private void preStack1(TreeNode node, Consumer<String> c){
            if(node == null){
                return;
            }
            Stack<TreeNode> stack = new Stack<>();
            stack.push(node);
            while (!stack.isEmpty()){
                TreeNode cur = stack.pop();
                c.accept(cur.value);
                if(cur.right!=null){
                    stack.push(cur.right);
                }
                if(cur.left != null){
                    stack.push(cur.left);
                }
            }
        }
        private void preStack(TreeNode node, Consumer<String> c){
            if(node == null){
                return;
            }
            TreeNode cur = node;
            Stack<TreeNode> stack = new Stack<>();
            while (!stack.isEmpty() || cur != null){
                if(cur!= null){
                    c.accept(cur.value);//放在left赋值之前，就是先序
                    stack.push(cur);
                    cur = cur.left;
                }
                else{
                    cur = stack.pop();

                    cur = cur.right;
                }
            }
        }

        /**
         * 非递归中序
         * 任何二叉树，都可以被左边界分解掉的。即：一直打印左边界，到头以后再打印右子树的第一个节点。
         * 在继续他的左边界，一直打印到头。
         *                   A
         *               B      C
         *            D    E  F
         *  分解左边界，A,B,D到头了，再退回到B，去找右子树(E)：重复左边界分解。
         *  这个过程本质上还是递归序。每个节点要进入三次。关键就看你的打印是在哪个步骤完成的
         * @param node
         * @param c
         */
        private void midStack(TreeNode node, Consumer<String> c){
            if(node == null){
                return;
            }
            TreeNode cur = node;
            Stack<TreeNode> stack = new Stack<>();
            while (!stack.isEmpty() || cur != null){
                if(cur!= null){
                    stack.push(cur);
                    cur = cur.left;
                }
                else{
                    cur = stack.pop();
                    c.accept(cur.value);
                    cur = cur.right;
                }
            }

        }

        /**
         * 非递归后序
         * @param node
         * @param c
         */
        private void lastStack1(TreeNode node,Consumer<String> c){
            if(node== null){
                return;
            }
            Stack<TreeNode> stack = new Stack<>();
            Stack<TreeNode> result = new Stack<>();
            stack.push(node);

            while (!stack.isEmpty()){
                TreeNode cur = stack.pop();
                result.push(cur);
                if(cur.left != null){
                    stack.push(cur.left);
                }
                if(cur.right!=null){
                    stack.push(cur.right);
                }
            }
            while (!result.isEmpty()){
                TreeNode n = result.pop();
                c.accept(n.value);
            }
        }

        /**
         * 同一个方法用切分的方法，先切右边界，右边界切完，找右边界中的左子树。
         * 在找到的左子树中，继续切有边界。
         * 在第一个右边界的左子树进入的时候，压栈。
         * 最后全部都吐出来，就是后序遍历了
         * 先序中序后续都可以用一套思路：左边界切分、右边界切分的思路来搞。
         * @param node
         * @param c
         */
        private void lastStack(TreeNode node,Consumer<String> c) {
            if(node == null){
                return;
            }
            TreeNode cur = node;
            Stack<TreeNode> stack = new Stack<>();
            Stack<String> result = new Stack<>();
            while (!stack.isEmpty() || cur != null){
                if(cur!= null){
                    result.push(cur.value);
                    stack.push(cur);
                    cur = cur.right;
                }
                else{
                    cur = stack.pop();
                    cur = cur.left;
                }
            }
            while (!result.isEmpty()){
                c.accept(result.pop());
            }
        }
    }
    
}
