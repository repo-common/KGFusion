package utils;

import org.w3c.dom.html.HTMLTableColElement;

import java.text.CollationElementIterator;
import java.util.*;
import java.util.stream.Collectors;

public class Top_K {

    public static List<Integer> sortAndOriginalIndex(double[] arr, int k, Boolean reverse) {
        int[] sortedIndex = new int[arr.length];
        TreeMap<Double, Integer> map = new TreeMap<Double, Integer>();
        for (int i = 0; i < arr.length; i++) {
            map.put(arr[i], i); // 将arr的“值-索引”关系存入Map集合
        }
        int n = 0;
        for (Map.Entry<Double, Integer> me : map.entrySet()) {
            sortedIndex[n++] = me.getValue();
        }

        List<Integer> indexes = new ArrayList<>();
        for (int index : sortedIndex) {
            indexes.add(index);
        }
        if (reverse)
            Collections.reverse(indexes);
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            res.add(indexes.get(i));
        }
        return res;
    }

    public static void main(String[] args) {
        double[] a = new double[]{1.0, 3.0, 2.0, 4.0, 0.3};
        List<Integer> integers = sortAndOriginalIndex(a, 3, true);
        for (Integer integer : integers) {
            System.out.println(integer);
        }
    }
}
