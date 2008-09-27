package bbk;

import bbk.dng.utils.CollectionUtils;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;

/**
 * Date: 13-Aug-2008 01:03:53
 */
public class Scratch {


    public static void main(String[] args) {

        Map<String, Integer> test = CollectionUtils.mapOf(new Object[]{"one", 1, "three", 2});

        for (String t: test.keySet()) {
            System.out.printf("%s = %s\n", t, test.get(t));
        }

        


 /*       Set<String> set1 = new HashSet<String>();
        Set<String>  set2 = new HashSet<String>() ;

        String s1 = "hello";
        String s2 = "world";

        char[] i = new char[]{'h', 'e', 'l', 'l', 'o'};

        StringBuffer sb = new StringBuffer();
        sb.append("hello");

        StringBuffer sb2 = new StringBuffer();
        sb2.append("wor").append("ld");

        set1.add(s1);
        set1.add(s2);

        set2.add(sb.toString());
        set2.add(sb2.toString());

        set1.add(new String(i));

        System.out.printf("%s", set1.equals(set2));*/

        /* IndexReader reader = IndexReader.open("/home/aut/project/dng/data_index/sequences");
        TermEnum terms = reader.terms();
        while (terms.next()) {
            String field = terms.term().field();
            if (field.equals("architecture")) {
                String value = terms.term().text();
                int docFreq = terms.docFreq();
                System.out.printf("%s{%s}= %s\n", field, value, docFreq);
            }
        }*/
    }
}


