package module;

import Jama.Matrix;
import utils.MatrixUtil;

import java.util.ArrayList;
import java.util.List;

import static utils.MatrixUtil.printMatrix;

public class HeRWR {
    private double c;
    private double[][] BiMatrix1;
    private double[][] BiMatrix2;
    private int iters;

    public HeRWR(double c, double[][] biMatrix1, int iters) {
        this.c = c;
        BiMatrix1 = biMatrix1;
        this.iters = iters;
    }

    public HeRWR(double c, double[][] biMatrix1, double[][] biMatrix2, int iters) {
        this.c = c;
        BiMatrix1 = biMatrix1;
        BiMatrix2 = biMatrix2;
        this.iters = iters;
    }

    public HeRWR() {
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double[][] getBiMatrix1() {
        return BiMatrix1;
    }

    public void setBiMatrix1(double[][] biMatrix1) {
        BiMatrix1 = biMatrix1;
    }

    public double[][] getBiMatrix2() {
        return BiMatrix2;
    }

    public void setBiMatrix2(double[][] biMatrix2) {
        BiMatrix2 = biMatrix2;
    }

    public double[][] paddingBi(double[][] matrix) {
        int xdim = matrix.length;
        int ydim = matrix[0].length;
        Matrix newTfIdf = Matrix.constructWithCopy(matrix);
        Matrix transTfIdf = newTfIdf.transpose();
        double[][] paddingMatrix = new double[xdim + ydim][xdim + ydim];
        for (int i = 0; i < xdim; i++) {
            for (int j = xdim; j < xdim + ydim; j++) {
                paddingMatrix[i][j] = matrix[i][j - xdim];
            }
        }
        for (int i = xdim; i < xdim + ydim; i++) {
            for (int j = 0; j < xdim; j++) {
                paddingMatrix[i][j] = transTfIdf.getArray()[i - xdim][j];
            }
        }
        return paddingMatrix;
    }

    /**
     * @param matrix1 word-document 矩阵
     * @param matrix2 type-document 矩阵
     * @return padding 后的统一关系矩阵
     */
    public double[][] paddingTi(double[][] matrix1, double[][] matrix2) {
        int dim1 = matrix1.length;
        int dim2 = matrix1[0].length;
        int dim3 = matrix2.length;
        int dim = dim1 + dim2 + dim3;
        // 局部归一化
        double[][] paddingMatrix = new double[dim][dim];
        for (int i = 0; i < dim1; i++) {
            for (int j = dim1; j < dim1 + dim2; j++) {
                paddingMatrix[i][j] = matrix1[i][j - dim1];
                paddingMatrix[j][i] = matrix1[i][j - dim1];
            }
        }
        for (int m = dim1 + dim2; m < dim; m++) {
            for (int n = dim1; n < dim1 + dim2; n++) {
                paddingMatrix[m][n] = matrix2[m - dim1 - dim2][n - dim1];
                paddingMatrix[n][m] = matrix2[m - dim1 - dim2][n - dim1];
            }
        }
        // 全局归一化
        return paddingMatrix;
    }

    public List<Matrix> entrance() {
        List<Matrix> res = new ArrayList<>();
        Matrix paddingMatrix = null;
        // 二部图的情况
        if (BiMatrix1.length != 0 && BiMatrix2 == null) {
            paddingMatrix = Matrix.constructWithCopy(paddingBi(MatrixUtil.norm(BiMatrix1)));
        } else if (BiMatrix1.length != 0 && BiMatrix2.length != 0) {
            paddingMatrix = Matrix.constructWithCopy(paddingTi(MatrixUtil.norm(BiMatrix1), MatrixUtil.norm(BiMatrix2)));
            MatrixUtil.printMatrix(paddingMatrix.getArray());
        }
        assert paddingMatrix != null;
        res.add(MatrixUtil.intChengMatrix(paddingMatrix, c * (1 - c)));  //R1
        for (int i = 1; i <= iters; i++) {
            Matrix iterres = MatrixUtil.intChengMatrix(paddingMatrix, c * (1 - c)).plus(MatrixUtil.intChengMatrix(paddingMatrix, 1 - c).times(res.get(i - 1)));
            res.add(iterres);
        }

        return res;
    }

    public static void main(String[] args) {
//        double[][] matrix1 = MatrixUtil.geRandomMatrix(3, 5);
//        MatrixUtil.printMatrix(matrix1);
//        System.out.println();
//        double[][] matrix2 = MatrixUtil.geRandomMatrix(4, 5);
//        MatrixUtil.printMatrix(matrix2);
//        System.out.println();
//        double[][] padding = new HeRW().paddingTi(matrix1, matrix2);
//        MatrixUtil.printMatrix(padding);

        long startTime = System.currentTimeMillis();

        double[][] doubles = new double[][]{{0, 1, 1, 1}, {1, 0, 0, 0}, {1, 0, 0, 1}, {1, 0, 1, 0}};
        double[][] doubles1 = new double[][]{{0, 1, 1, 0}, {0, 0, 1, 1}, {1, 1, 0, 1}, {0, 0, 1, 0}, {1, 1, 1, 0}};
        printMatrix(doubles);
        System.out.println();
        HeRWR heRWR = new HeRWR(0.8, doubles, doubles1, 1000);
        double[][] norm = MatrixUtil.norm(doubles);
        printMatrix(norm);
        System.out.println();
        System.out.println("迭代结果");
        List<Matrix> res = heRWR.entrance();
        for (int i = 1; i < res.size(); i++) {
            System.out.println("第" + i + "次迭代");
            printMatrix(res.get(i).getArray());
            System.out.println();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
