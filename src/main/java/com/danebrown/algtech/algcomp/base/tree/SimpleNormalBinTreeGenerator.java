package com.danebrown.algtech.algcomp.base.tree;

import cn.hutool.core.lang.Pair;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by danebrown on 2022/2/26
 * mail: tain198127@163.com
 *
 * @author danebrown
 */
public class SimpleNormalBinTreeGenerator extends NormalBinTreeGenerator<Integer,String> {

    public SimpleNormalBinTreeGenerator() {
        this.supplier =
                (()-> new Pair<>(java.lang.Integer.valueOf(ThreadLocalRandom.current().nextInt()), ""));
    }
}
