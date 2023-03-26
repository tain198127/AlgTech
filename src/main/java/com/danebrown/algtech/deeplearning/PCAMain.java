package com.danebrown.algtech.deeplearning;


import com.danebrown.algtech.jama.Matrix;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.io.FileWriter;
import java.io.IOException;
@Log4j2
public class PCAMain {

    public static double[][] loadData(){
        double[][] iris = {
                {5.1,3.5,1.4,0.2},
                {4.9,3,1.4,0.2},
                {4.7,3.2,1.3,0.2},
                {4.6,3.1,1.5,0.2},
                {5,3.6,1.4,0.2},
                {5.4,3.9,1.7,0.4},
                {4.6,3.4,1.4,0.3},
                {5,3.4,1.5,0.2},
                {4.4,2.9,1.4,0.2},
                {4.9,3.1,1.5,0.1},
                {5.4,3.7,1.5,0.2},
                {4.8,3.4,1.6,0.2},
                {4.8,3,1.4,0.1},
                {4.3,3,1.1,0.1},
                {5.8,4,1.2,0.2},
                {5.7,4.4,1.5,0.4},
                {5.4,3.9,1.3,0.4},
                {5.1,3.5,1.4,0.3},
                {5.7,3.8,1.7,0.3},
                {5.1,3.8,1.5,0.3},
                {5.4,3.4,1.7,0.2},
                {5.1,3.7,1.5,0.4},
                {4.6,3.6,1,0.2},
                {5.1,3.3,1.7,0.5},
                {4.8,3.4,1.9,0.2},
                {5,3,1.6,0.2},
                {5,3.4,1.6,0.4},
                {5.2,3.5,1.5,0.2},
                {5.2,3.4,1.4,0.2},
                {4.7,3.2,1.6,0.2},
                {4.8,3.1,1.6,0.2},
                {5.4,3.4,1.5,0.4},
                {5.2,4.1,1.5,0.1},
                {5.5,4.2,1.4,0.2},
                {4.9,3.1,1.5,0.1},
                {5,3.2,1.2,0.2},
                {5.5,3.5,1.3,0.2},
                {4.9,3.1,1.5,0.1},
                {4.4,3,1.3,0.2},
                {5.1,3.4,1.5,0.2},
                {5,3.5,1.3,0.3},
                {4.5,2.3,1.3,0.3},
                {4.4,3.2,1.3,0.2},
                {5,3.5,1.6,0.6},
                {5.1,3.8,1.9,0.4},
                {4.8,3,1.4,0.3},
                {5.1,3.8,1.6,0.2},
                {4.6,3.2,1.4,0.2},
                {5.3,3.7,1.5,0.2},
                {5,3.3,1.4,0.2},
                {7,3.2,4.7,1.4},
                {6.4,3.2,4.5,1.5},
                {6.9,3.1,4.9,1.5},
                {5.5,2.3,4,1.3},
                {6.5,2.8,4.6,1.5},
                {5.7,2.8,4.5,1.3},
                {6.3,3.3,4.7,1.6},
                {4.9,2.4,3.3,1},
                {6.6,2.9,4.6,1.3},
                {5.2,2.7,3.9,1.4},
                {5,2,3.5,1},
                {5.9,3,4.2,1.5},
                {6,2.2,4,1},
                {6.1,2.9,4.7,1.4},
                {5.6,2.9,3.6,1.3},
                {6.7,3.1,4.4,1.4},
                {5.6,3,4.5,1.5},
                {5.8,2.7,4.1,1},
                {6.2,2.2,4.5,1.5},
                {5.6,2.5,3.9,1.1},
                {5.9,3.2,4.8,1.8},
                {6.1,2.8,4,1.3},
                {6.3,2.5,4.9,1.5},
                {6.1,2.8,4.7,1.2},
                {6.4,2.9,4.3,1.3},
                {6.6,3,4.4,1.4},
                {6.8,2.8,4.8,1.4},
                {6.7,3,5,1.7},
                {6,2.9,4.5,1.5},
                {5.7,2.6,3.5,1},
                {5.5,2.4,3.8,1.1},
                {5.5,2.4,3.7,1},
                {5.8,2.7,3.9,1.2},
                {6,2.7,5.1,1.6},
                {5.4,3,4.5,1.5},
                {6,3.4,4.5,1.6},
                {6.7,3.1,4.7,1.5},
                {6.3,2.3,4.4,1.3},
                {5.6,3,4.1,1.3},
                {5.5,2.5,4,1.3},
                {5.5,2.6,4.4,1.2},
                {6.1,3,4.6,1.4},
                {5.8,2.6,4,1.2},
                {5,2.3,3.3,1},
                {5.6,2.7,4.2,1.3},
                {5.7,3,4.2,1.2},
                {5.7,2.9,4.2,1.3},
                {6.2,2.9,4.3,1.3},
                {5.1,2.5,3,1.1},
                {5.7,2.8,4.1,1.3},
                {6.3,3.3,6,2.5},
                {5.8,2.7,5.1,1.9},
                {7.1,3,5.9,2.1},
                {6.3,2.9,5.6,1.8},
                {6.5,3,5.8,2.2},
                {7.6,3,6.6,2.1},
                {4.9,2.5,4.5,1.7},
                {7.3,2.9,6.3,1.8},
                {6.7,2.5,5.8,1.8},
                {7.2,3.6,6.1,2.5},
                {6.5,3.2,5.1,2},
                {6.4,2.7,5.3,1.9},
                {6.8,3,5.5,2.1},
                {5.7,2.5,5,2},
                {5.8,2.8,5.1,2.4},
                {6.4,3.2,5.3,2.3},
                {6.5,3,5.5,1.8},
                {7.7,3.8,6.7,2.2},
                {7.7,2.6,6.9,2.3},
                {6,2.2,5,1.5},
                {6.9,3.2,5.7,2.3},
                {5.6,2.8,4.9,2},
                {7.7,2.8,6.7,2},
                {6.3,2.7,4.9,1.8},
                {6.7,3.3,5.7,2.1},
                {7.2,3.2,6,1.8},
                {6.2,2.8,4.8,1.8},
                {6.1,3,4.9,1.8},
                {6.4,2.8,5.6,2.1},
                {7.2,3,5.8,1.6},
                {7.4,2.8,6.1,1.9},
                {7.9,3.8,6.4,2},
                {6.4,2.8,5.6,2.2},
                {6.3,2.8,5.1,1.5},
                {6.1,2.6,5.6,1.4},
                {7.7,3,6.1,2.3},
                {6.3,3.4,5.6,2.4},
                {6.4,3.1,5.5,1.8},
                {6,3,4.8,1.8},
                {6.9,3.1,5.4,2.1},
                {6.7,3.1,5.6,2.4},
                {6.9,3.1,5.1,2.3},
                {5.8,2.7,5.1,1.9},
                {6.8,3.2,5.9,2.3},
                {6.7,3.3,5.7,2.5},
                {6.7,3,5.2,2.3},
                {6.3,2.5,5,1.9},
                {6.5,3,5.2,2},
                {6.2,3.4,5.4,2.3},
                {5.9,3,5.1,1.8}
        };
        return iris;
    }

    public static RealMatrix PCAwithCommonMath(double[][] data){
        RealMatrix matrix = MatrixUtils.createRealMatrix(data);
        //进行协方差
        Covariance covariance = new Covariance(matrix);
        //拿到协方差的值
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition eigen = new EigenDecomposition(covarianceMatrix);
        RealMatrix eigenvectors = eigen.getV();

        RealMatrix transformed = eigenvectors.transpose().multiply(matrix.transpose());
        transformed = transformed.transpose();

        return transformed;
    }

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated catch block


        PCA pca = new PCA();
        //获得样本集
        double[][] primaryArray = loadData();
        System.out.println("--------------------------------------------");
        double[][] averageArray = pca.changeAverageToZero(primaryArray);
        System.out.println("--------------------------------------------");
        System.out.println("均值0化后的数据: ");
        System.out.println(averageArray.length + "行，"
                + averageArray[0].length + "列");

        System.out.println("---------------------------------------------");
        System.out.println("协方差矩阵: ");
        double[][] varMatrix = pca.getVarianceMatrix(averageArray);

        System.out.println("--------------------------------------------");
        System.out.println("特征值矩阵: ");
        double[][] eigenvalueMatrix = pca.getEigenvalueMatrix(varMatrix);

        System.out.println("--------------------------------------------");
        System.out.println("特征向量矩阵: ");
        double[][] eigenVectorMatrix = pca.getEigenVectorMatrix(varMatrix);

        System.out.println("--------------------------------------------");
        Matrix principalMatrix = pca.getPrincipalComponent(primaryArray, eigenvalueMatrix, eigenVectorMatrix);
        System.out.println("主成分矩阵: ");
        principalMatrix.print(6, 3);

        System.out.println("--------------------------------------------");
        System.out.println("降维后的矩阵: ");
        Matrix resultMatrix = pca.getResult(primaryArray, principalMatrix);
        resultMatrix.print(6, 3);
        log.info("降维后的结果:{}",resultMatrix.getArrayCopy());
        int c = resultMatrix.getColumnDimension(); //列数
        int r = resultMatrix.getRowDimension();//行数
        System.out.println(resultMatrix.getRowDimension() + "," + resultMatrix.getColumnDimension());

        RealMatrix rst = PCAwithCommonMath(primaryArray);
        System.out.println("-----------------使用apache common math--------------------");
        System.out.println(rst.getColumnDimension());
        System.out.println(rst.getRowDimension());
        System.out.println(rst.getData());

    }
}
