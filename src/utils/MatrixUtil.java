package utils;

import Jama.Matrix;

import java.util.Random;

public class MatrixUtil {
    public static Matrix intChengMatrix(Matrix matrix, double num) {
        double[][] array = matrix.getArrayCopy();
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                array[i][j] *= num;
            }
        }
        return Matrix.constructWithCopy(array);
    }

    public static double[][] geRandomMatrix(int xdim, int ydim) {
        double[][] res = new double[xdim][ydim];
        for (int i = 0; i < xdim; i++) {
            for (int j = 0; j < ydim; j++) {
                res[i][j] = new Random().nextInt(5);
            }
        }
        return res;
    }

    public static void printMatrix(double[][] matrix) {
        for (double[] doubles : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(doubles[j] + "  ");
            }
            System.out.println();
        }
    }

    public static double[][] norm(double[][] matrix) {
        int xdim = matrix.length;
        int ydim = matrix[0].length;
        double[][] norm = new double[xdim][ydim];
        for (int i = 0; i < xdim; i++) {
            double sum = 0.0;
            for (int j = 0; j < ydim; j++) {
                sum += matrix[i][j];
            }

            for (int k = 0; k < ydim; k++) {
                if (sum != 0.0){
                    norm[i][k] = matrix[i][k] / sum;
                }
                else{
                    norm[i][k] = 0.0;
                }
            }

        }
        return norm;
    }

    public static double[][] getEye(int dim) {
        double[][] eye = new double[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (i == j)
                    eye[i][j] = 1.0;
            }
        }
        return eye;
    }

    public static void main(String[] args) {
//        double[][] doubles = RWR.geRandomMatrix(3, 5);
//        RWR.printMatrix(doubles);
//        Matrix matrix = intChengMatrix(Matrix.constructWithCopy(doubles), 10);
//        RWR.printMatrix(matrix.getArray());
//        System.out.println();
//        double[][] doubles1 = RWR.geRandomMatrix(3,5);
//        RWR.printMatrix(doubles1);
//        Matrix plus = Matrix.constructWithCopy(doubles).plus(Matrix.constructWithCopy(doubles1));
//        RWR.printMatrix(plus.getArray());
    }
}
