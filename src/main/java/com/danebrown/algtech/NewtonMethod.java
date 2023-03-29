package com.danebrown.algtech;

public class NewtonMethod {
    public static void main(String[] args) {
        double v = sqrt(4.0d);
        System.out.println(v);
    }
    public static double sqrt(double x) {
        if (x < 0) {
            throw new IllegalArgumentException("Square root of negative numbers is not real!");
        }
        double eps = 1e-15;  // 精度控制
        double t = x;  // 初值
        while (Math.abs(t - x/t) > eps * t) {  // 迭代计算
            t = (x/t + t) / 2.0;
        }
        return t;
    }

}
