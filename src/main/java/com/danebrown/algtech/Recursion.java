package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.extern.log4j.Log4j2;

import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
public class Recursion {

    public static void main(String[] args) {
        AlgCompMenu.addComp(new HanoiTower());
        AlgCompMenu.run();
    }
    @AlgName("汉诺塔")
    public static class HanoiTower extends AlgCompImpl<String, Integer>{

        @Override
        public Integer prepare() {
            return ThreadLocalRandom.current().nextInt(10,20);
        }

        @Override
        protected String standard(Integer data) {
            if(data > 0){
                return func(data,"left","right","mid");
//                String msg = left2right(data);
//                return msg;
            }
            return null;
        }
        //暴力递归
        public static String left2right(int n){
            StringBuilder stringBuilder = new StringBuilder();
            if(n == 1){
                String msg ="Move 1 from left to right";
                log.debug(msg);
                return msg;
            }
            stringBuilder.append(left2mid(n-1));
            String msg = "Move " + n + " from left to right";
            log.debug(msg);
            stringBuilder.append(msg);
            stringBuilder.append(mid2right(n-1));
            return stringBuilder.toString();
        }
        public static String left2mid(int n){
            StringBuilder stringBuilder = new StringBuilder();
            if (n == 1) {
                String msg = "Move 1 from left to mid";
                log.debug(msg);
                return msg;
            }
            String msg = "Move " + n + " from left to mid";
            stringBuilder.append(left2right(n-1));
            stringBuilder.append(msg);
            log.debug(msg);
            stringBuilder.append(right2mid(n-1));
            return stringBuilder.toString();
        }
        public static String right2mid(int n){
            StringBuilder stringBuilder = new StringBuilder();
            if(n == 1){
                String msg = "Move 1 from right to mid";
                log.debug(msg);
                return msg;
            }
            String msg = "Move " + n + " from right to mid";
            stringBuilder.append(right2left(n-1));
            log.debug(msg);
            stringBuilder.append(msg);
            stringBuilder.append(left2mid(n-1));
            return stringBuilder.toString();
        }
        public static String mid2right(int n){
            StringBuilder stringBuilder = new StringBuilder();
            if(n == 1){
                String msg = "Move 1 from mid to right";
                log.debug(msg);
                return msg;
            }
            String msg = "Move " + n + " from mid to right";
            stringBuilder.append(mid2left(n-1));
            log.debug(msg);
            stringBuilder.append(msg);
            stringBuilder.append(left2right(n-1));
            return stringBuilder.toString();
            
        }
        public static String mid2left(int n){
            StringBuilder stringBuilder = new StringBuilder();
            if(n == 1){
                String msg = "Move 1 from mid to left";
                log.debug(msg);
                return msg;
            }
            String msg = "Move " + n + " from mid to left";
            stringBuilder.append(mid2right(n-1));
            log.debug(msg);
            stringBuilder.append(msg);
            stringBuilder.append(right2left(n-1));
            return stringBuilder.toString();
            
        }
        public static String right2left(int n){
            StringBuilder stringBuilder = new StringBuilder();
            if(n == 1){
                String msg = "Move 1 from right to left";
                log.debug(msg);
                return msg;
            }
            String msg = "Move " + n + " from right to left";
            stringBuilder.append(right2mid(n-1));
            log.debug(msg);
            stringBuilder.append(msg);
            stringBuilder.append(mid2left(n-1));
            return stringBuilder.toString();
        }
        /**
         * 把问题转化为规模小一些的同类问题的子问题
         * 有明确的不需要递归的条件
         * 有得到一个子问题之后的决策过程
         * 不记录每个子问题的解
         * @param N
         * @param from
         * @param to
         * @param other
         * @return
         */
        //版本1
        public static String func(int N, String from, String to, String other){
            if(N == 1){
                String msg = String.format("Move 1 from "+from+" to "+ to);
                log.debug(msg);
                return msg;
            }else{
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(func(N-1, from, other,to)) ;
                String msg = "Move "+N +" from "+from+" to "+to;
                log.debug(msg);
                stringBuilder.append(msg);
                stringBuilder.append(func(N-1, other,to, from));
                return stringBuilder.toString();
            }
            
        }
        
        public static class Record {
            public boolean finish1;
            public int base;
            public String from;
            public String to;
            public String other;

            public Record(boolean f1, int b, String f, String t, String o) {
                finish1 = false;
                base = b;
                from = f;
                to = t;
                other = o;
            }
        }

        @Override
        protected String test(Integer data) {
            
            return hanoi(data);
        }
        public static String hanoi(int N){
            StringBuilder stringBuilder = new StringBuilder();
            if(N < 1){
                return  "";
            }
            Stack<Record> stack = new Stack<>();
            stack.add(new Record(false,N,"left","right","mid"));
            while (!stack.isEmpty()){
                Record cur  = stack.pop();
                if(cur.base == 1){
                    String msg = String.format("Move 1 from "+cur.from+" to "+ cur.to);
                    stringBuilder.append(msg);
                    log.debug(msg);
                    if(!stack.isEmpty()){
                        stack.peek().finish1=true;
                    }
                }else{
                    if(!cur.finish1){
                        stack.push(cur);
                        stack.push(new Record(false,cur.base-1,cur.from,cur.other,cur.to));
                    }else{
                        String msg = "Move "+cur.base +" from "+cur.from+" to "+cur.to;
                        log.debug(msg);
                        stringBuilder.append(msg);
                        stack.push(new Record(false,cur.base-1,cur.other,cur.to,cur.from));
                    }
                }
            }
            return stringBuilder.toString();
        } 
    }
}
