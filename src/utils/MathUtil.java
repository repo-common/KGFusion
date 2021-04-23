package utils;

import java.util.List;
import java.util.Random;

public class MathUtil {
    public static double cosineSimilarity(double[] vector1, double[] vector2){
        double inner_dot = 0.0;
        double value1 = 0.0;
        double value2 = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            inner_dot += (vector1[i] * vector2[i]);
            value1 += Math.pow(vector1[i], 2);
            value2 += Math.pow(vector2[i], 2);
        }
        if (value1 == 0 || value2 == 0)
            return 0.0;
        return inner_dot / (Math.sqrt(value1) * Math.sqrt(value2));
    }

    public static double sum(List<Double> list){
        double sumValue = 0.0;
        for (int i = 0; i < list.size(); i++) {
            sumValue += list.get(i);
        }
        return sumValue;
    }

    public static void main(String[] args) {
        double[][] a = new double[3][3];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                a[i][j] = i + j;
                System.out.printf(a[i][j] + " ");
            }
            System.out.println();
        }


        double[] a0 = new double[3];
        for (int i = 0; i < a0.length; i++) {
            System.out.println(a0[i]);
        }

        System.out.println(MathUtil.cosineSimilarity(a[0], a[0]));
    }
}
