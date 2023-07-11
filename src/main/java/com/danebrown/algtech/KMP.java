package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompContext;
import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KMP {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new StringContains());
        AlgCompMenu.run();
    }

    @AlgName(value = "KMP",timeout = 5,range = 1000000)
    public static class StringContains extends AlgCompImpl<Boolean, String[]>{


        @Override
        public String[] prepare(AlgCompContext context) {
            String[] ret = new String[2];
            long range = context.getRange();
            int begin = 'A';
            int end = 'z';
            List<Character> alpha = new ArrayList<>();
            for(int i = begin;i < end+1; i++){
                if(Character.isLetter((char)i)){
                    alpha.add((char)i);
                }

            }
            char[] c1 = new char[ThreadLocalRandom.current().nextInt((int) (range/2),(int) range)];
            char[] c2 = new char[ThreadLocalRandom.current().nextInt(c1.length/2)];
            for(int i = 0; i < c1.length;i++){
                c1[i] = alpha.get(ThreadLocalRandom.current().nextInt(alpha.size()-1));
            }
            for(int i=0; i < c2.length;i++){
                c2[i] = alpha.get(ThreadLocalRandom.current().nextInt(alpha.size()-1));
            }
            ret[0] = String.valueOf(c1);
            ret[1] = String.valueOf(c2);
            if(ThreadLocalRandom.current().nextBoolean() && ret[0].length()>5){
                //随机产生包含的字符串
                //随机开始的位置
                int startIdx = ThreadLocalRandom.current().nextInt(1,ret[0].length()/2);
                //随机长度，但是不能越界
                int endIdx = ThreadLocalRandom.current().nextInt(startIdx,ret[0].length());
                ret[1] = ret[0].substring(startIdx,endIdx);

            }
            return ret;
        }

        @Override
        protected Boolean standard(String[] data) {
            return data[0].contains(data[1]);
        }

        @Override
        protected Boolean test(String[] data) {
            int val = KMP(data[0],data[1]) ;
            return val >=0;
        }
        public static int KMP(String s1, String s2){
            if(s1 == null || s2 == null || s1.length()<1 || s1.length() < s2.length()){
                return -1;
            }

            char[] c1 = s1.toCharArray();
            char[] c2 = s2.toCharArray();
            int[] next = calculateNext(c2);
            int x =0,y=0;
            while(x < c1.length && y < c2.length){
                if(c1[x] == c2[y]){
                    x++;
                    y++;
                } else if (next[y] == -1) {
                    x++;
                }else{
                    y = next[y];
                }
            }
            return y == c2.length? x-y:-1;
        }

        /**
         * 计算s2中每一个字符的最大相同前后缀值
         * @param c2
         * @return
         */
        public static int[] calculateNext(char[] c2){
            if (c2.length ==1){
                return new int[]{-1};
            }
            int[] next = new int[c2.length];
            next[0] = -1;
            next[1] = 0;
            int i = 2;
            int cn = 0;
            while(i < next.length){
                if(c2[i-1] == c2[cn]){
                    next[i++] = ++cn;
                }else if(cn >0){
                    cn = next[cn];
                }else{
                    next[i++] = 0;
                }
            }
            return next;
        }
    }

}
