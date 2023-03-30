package com.danebrown.algtech;

import com.danebrown.algtech.algcomp.AlgCompContext;
import com.danebrown.algtech.algcomp.AlgCompImpl;

/**
 * Created by danebrown on 2021/7/27
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
public class SearchCompare extends AlgCompImpl<Integer, Integer[]> {
    /**
     * 计算中值
     * 原始公式： (L+R)/2，但是L+R可能溢出。
     * 改进公式: (L+R)/2 = L/2+R/2
     * = L/2+ R/2 + L/2 - L/2
     * = L/2+L/2+R/2-L/2
     * = L+(R-L)/2
     * 工程优化： 除以2就是>>1，向右位移1位
     * @param L
     * @param R
     * @return
     */
    public static int mid(int L, int R){
        return (L + ((R-L)>>1));
    }
    public static void main(String[] args) {
        SearchCompare searchCompare = new SearchCompare();

        System.out.println(searchCompare.compare("中值计算"));
    }

    @Override
    public Integer[] prepare(AlgCompContext context) {
        return new Integer[]{1,3};
    }

    @Override
    public Integer standard(Integer[]data) {
        return (data[0]+data[1])/2;
    }

    @Override
    public Integer test(Integer[] data) {
        return (data[0] + ((data[1]-data[0])>>1));
    }




}
