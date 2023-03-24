package com.danebrown.algtech.deeplearning;

import com.danebrown.algtech.algcomp.AlgCompImpl;
import com.danebrown.algtech.algcomp.AlgCompMenu;
import com.danebrown.algtech.algcomp.AlgName;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
@Log4j2
public class SoftMax {
    public static void main(String[] args) {
        AlgCompMenu.addComp(new SoftMaxJava());
        AlgCompMenu.run();
    }
    @AlgName("SoftMax")
    public static class SoftMaxJava extends AlgCompImpl<double[], double[]> {


        @Override
        public double[] prepare() {
            int random = ThreadLocalRandom.current().nextInt(5,10);
            double[] result = new double[random];
            BigDecimal sum = new BigDecimal(0);
            for(int i = 0; i < random;i++){
                result[i]=ThreadLocalRandom.current().nextDouble(10,20);
            }

            return result;
        }

        @Override
        protected double[] standard(double[] data) {
            double[] result = new double[data.length];
            double sum = 0;
            for(int i=0;i < data.length;i++){
                result[i] = (Math.exp(data[i]));
                sum+= result[i];
            }
            for(int i=0;i < result.length;i++){
                result[i] = result[i]/sum;
            }
            log.info("after softmax:{}",result);
            return result;
        }

        @Override
        protected double[] test(double[] data) {
            double[] result = new double[data.length];
            for(int i=0;i < data.length;i++){
                result[i] = data[i];
            }
            log.info("after softmax:{}",result);
            return result;
        }
    }

}
