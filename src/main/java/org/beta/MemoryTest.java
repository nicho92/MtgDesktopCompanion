package org.beta;

import java.util.ArrayList;
import java.util.List;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

public class MemoryTest {

	public static void main(String[] args) {
        int size = 10;
        List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
      
        System.out.println("CLASSLAYOUT PARSE list\n" + ClassLayout.parseClass(ArrayList.class).toPrintable(list));        
        System.out.println("GRAPHLAYOUT PARSE list\n" + GraphLayout.parseInstance(list).toPrintable());
        System.out.println("GRAPHLAYOUT PARSE foot\n" + GraphLayout.parseInstance(list).toFootprint());
    }
}