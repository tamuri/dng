package bbk.dng.utils;

import java.util.*;


public class CollectionUtils {

    public static <K,V> Map<K,V> newMap() {
        return new HashMap<K,V>();
    }

    public static <T> List<T> newList() {
        return new ArrayList<T>();
    }

    public static <T> Set<T> newSet() {
        return new HashSet<T>();
    }

    public static <T> Set<T> setOf(T... things) {
        // call: CollectionUtils.setOf("one", "two", "three");
        return new HashSet<T>(Arrays.asList(things));
    }

    public static <T> List<T> listOf(T... things) {
        // call: CollectionUtils.listOf("one", "two", "three");
        return Arrays.asList(things);
    }

    public static <K,V> Map<K,V> mapOf(Object... array) {
        // call: CollectionUtils.mapOf("key1", "value1", "key2", "value2");
        Map<K,V> map = new HashMap<K,V>();
        for (int i = 0; i < array.length; i+=2) {
            map.put((K) array[i], (V) array[i+1]);
        }
        return map;
    }

    public static String join(List list, char delim) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) buf.append(delim);
            buf.append((String)list.get(i));
        }
        return buf.toString();
    }

    public static <K, V extends Comparable<? super V>> List<K> getKeysSortedByValue(Map<K, V> map, boolean desc) {
        final int size = map.size();
        final List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(size);
        list.addAll(map.entrySet());
        final ValueComparator<V> cmp = new ValueComparator<V>();
        Collections.sort(list, cmp);
        if (desc) Collections.reverse(list);
        final List<K> keys = new ArrayList<K>();
        for (int i = 0; i < size; i++) {
            keys.add(list.get(i).getKey());
        }
        return keys;
    }

    private static final class ValueComparator<V extends Comparable<? super V>>
            implements Comparator<Map.Entry<?, V>> {
        public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }
}
