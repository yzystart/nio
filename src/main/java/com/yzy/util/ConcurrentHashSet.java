

package com.yzy.util;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ConcurrentHashSet<E>
    extends AbstractSet<E>
    implements Set<E>, java.io.Serializable
{
    private final transient ConcurrentHashMap<E,Object> map;
    private static final Object PRESENT = new Object();
    public ConcurrentHashSet() {
        map = new ConcurrentHashMap<>();
    }
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }
    public int size() {
        return map.size();
    }
    public boolean isEmpty() {
        return map.isEmpty();
    }
    public boolean contains(Object o) {
        return map.containsKey(o);
    }
    public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }
    public boolean remove(Object o) {
        return map.remove(o)==PRESENT;
    }
    public void clear() {
        map.clear();
    }

}
