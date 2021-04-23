package module;

import Jama.Matrix;
import utils.MatrixUtil;

import java.util.ArrayList;
import java.util.List;

import static utils.MatrixUtil.printMatrix;

/**
 * RWR 是不用构建统一关系矩阵的
 */
public class RWR {
    private double c;
    private double[][] tfIdf;
    private int iters;

    public RWR(double[][] tfIdf, double c, int iters) {
        this.c = c;
        this.tfIdf = tfIdf;
        this.iters = iters;
    }

    public RWR() {
    }

    public double getC() {
        return c;
    }

    public double[][] getTfIdf() {
        return tfIdf;
    }

    public void setC(double c) {
        this.c = c;
    }

    public void setTfIdf(double[][] tfIdf) {
        this.tfIdf = tfIdf;
    }

    public int getIters() {
        return iters;
    }

    public void setIters(int iters) {
        this.iters = iters;
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

    // ri = cWri + (1-c)ei
    public List<Matrix> entrance() {
        List<Matrix> res = new ArrayList<>(); // 保存每次迭代的结果,例子中matrix: 4x4, eye: 4x4  padding的做法
        Matrix eye = Matrix.constructWithCopy(MatrixUtil.getEye(getTfIdf()[0].length + getTfIdf().length));
        Matrix W = Matrix.constructWithCopy((paddingBi(MatrixUtil.norm(getTfIdf()))));
        res.add(W.times(eye));
        for (int i = 1; i <= iters ; i++) {
            Matrix iterres = MatrixUtil.intChengMatrix(W.times(res.get(i - 1)), 1-c).plus(MatrixUtil.intChengMatrix(eye, c));
            res.add(iterres);
        }
        return res;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        double[][] doubles = new double[][]{{0,1,1,1},{1,0,0,0},{1,0,0,1},{1,0,1,0}};
        printMatrix(doubles);
        System.out.println();
        double[][] norm = MatrixUtil.norm(doubles);
        printMatrix(norm);
        RWR rwr = new RWR(norm, 0.8, 1000);
        System.out.println();
//        double[][] padding = rwr.padding(norm);
//        printMatrix(padding);
//        System.out.println();
        System.out.println("迭代结果");
        List<Matrix> res = rwr.entrance();
        for (int i = 1; i < res.size(); i++) {
            System.out.println("第" + i + "次迭代");
            printMatrix(res.get(i).getArray());
            System.out.println();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
