package test;

import org.apache.commons.lang3.tuple.Pair;
import pipeLine.KGsMergeBasedOnContent;
import preprocessing.ConstructGraph;
import publicDataStructure.Item;
import publicDataStructure.KG;
import publicDataStructure.Node;

import java.io.IOException;
import java.util.List;

public class testTFIDF {
    public static void main(String[] args) throws IOException {
        Pair<List<KG>, List<Item>> listListPair = ConstructGraph.prepareKGs();
        List<KG> kgs = listListPair.getLeft();
        List<Item> items = listListPair.getRight();
        KGsMergeBasedOnContent kgmerge=new KGsMergeBasedOnContent(kgs,items);
        kgmerge.runMerge();

    }
}
