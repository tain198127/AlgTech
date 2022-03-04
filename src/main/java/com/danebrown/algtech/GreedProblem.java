package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2022/3/3
 * mail: tain198127@163.com
 * 贪心算法
 * @author danebrown
 */
@Log4j2
public class GreedProblem {
    private static volatile boolean isShutdown = false;
    private static volatile Queue<String> ioQueue = new LinkedBlockingQueue<>();
    public static void main(String[] args) {

        AlgCompMenu.addComp(new MinDic());
        AlgCompMenu.run();
    }

    /**
     * 给一个由字符串给定的数组strs
     * 必须把所有字符串拼起来
     * 返回所有可能的拼接结果中，字典序最小的结果
     * 什么是字典序：就是java里面字符串排大小的方式
     * 字典序比大小的逻辑：
     * 1. 两个字符串一样长时，把字符串理解为26/255/6/6535进制的数字。单纯的比两个"数字"的大小
     * 2. 两个字符串不一样长时：把短的右补齐跟长的一样长。补的那些字符是最最小的ascii码。然后按照规则1进行比较
     *
     * 有几种贪心算法：
     * 1. 直接对比两个字符串的字典序，谁小谁在前面。会导致bba，bab问题
     * 2. 两个字符串A,B。A.B > B.A？比较拼接后的大小。谁小谁在前面
     */
    @AlgName("最小字典序")
    public static class MinDic extends AlgCompImpl<String,String[]>{
        List<Character> metas = new ArrayList<>();
        public MinDic(){
            for(int i = 'a'; i <= 'z'; i++){
                metas.add(Character.valueOf((char)i));
            }
            for(int i = 'A'; i <= 'Z';i++){
                metas.add(Character.valueOf((char)i));
            }
        }
        @Override
        public String[] prepare() {
            int times = ThreadLocalRandom.current().nextInt(1,10);
            List<String> result = new ArrayList<>();
            for(int i = 0; i < times;i++){
                String tmp = "";
                int len = ThreadLocalRandom.current().nextInt(1,100);


                for(int j=0;j<len;j++){
                    int ascii = ThreadLocalRandom.current().nextInt(0,
                            metas.size());
                    tmp += metas.get(ascii);
                }
                result.add(tmp);
            }
            return result.toArray(new String[]{});
        }

        @Override
        protected String standard(String[] strs) {
            if (strs == null || strs.length == 0) {
                return "";
            }
            TreeSet<String> ans = process(strs);
            return ans.size() == 0 ? "" : ans.first();

        }
        // strs中所有字符串全排列，返回所有可能的结果
        public static TreeSet<String> process(String[] strs) {
            TreeSet<String> ans = new TreeSet<>();
            if (strs.length == 0) {
                ans.add("");
                return ans;
            }
            for (int i = 0; i < strs.length; i++) {
                String first = strs[i];
                String[] nexts = removeIndexString(strs, i);
                TreeSet<String> next = process(nexts);
                for (String cur : next) {
                    ans.add(first + cur);
                }
            }
            return ans;
        }
        // {"abc", "cks", "bct"}
        // 0 1 2
        // removeIndexString(arr , 1) -> {"abc", "bct"}
        public static String[] removeIndexString(String[] arr, int index) {
            int N = arr.length;
            String[] ans = new String[N - 1];
            int ansIndex = 0;
            for (int i = 0; i < N; i++) {
                if (i != index) {
                    ans[ansIndex++] = arr[i];
                }
            }
            return ans;
        }
        /**
         *
         * @param data
         * @return
         */
        @Override
        protected String test(String[] data) {
            if(data == null || data.length == 0){
                return "";
            }
            Arrays.sort(data, new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return (a+b).compareTo(b+a);
                }
            });
            StringBuilder str = new StringBuilder();
            for(int i=0;i < data.length;i++){
                str.append(data[i]);
            }
            return str.toString();
        }
    }
}
