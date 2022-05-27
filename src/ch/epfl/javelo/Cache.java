package ch.epfl.javelo;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Represents a cache (The class is generic)
 * @param <K> the key type
 * @param <V> the value type
 * @author Sadgal Lina (342075)
 */
public final class Cache<K,V> {

    private final int cacheCapacity;
    private final LinkedHashMap<K,V> cacheMemory;

    /**
     * Value of load factor
     */
    private final static float LOAD_FACTOR = 0.75f;

    /**
     * Constructor
     * @param cacheCapacity the cache capacity
     */
    public Cache(int cacheCapacity) {
        this.cacheCapacity = cacheCapacity;
        this.cacheMemory = new LinkedHashMap<>(cacheCapacity, LOAD_FACTOR, true);
    }

    /**
     * adds a key and its value to the cache
     * @param key the key we want to add
     * @param value the key's value
     */
    public void addToCache(K key, V value) {
        checkCacheCapacity();
        cacheMemory.put(key, value);
    }

    /**
     *
     * @param key the key we want to know if it is in the cache.
     * @return true if the key is in the cache, false otherwise
     */
    public boolean containsKey(K key) {
        return cacheMemory.containsKey(key);
    }

    /**
     *
     * @param key the key we want the value for.
     * @return the value corresponding to the key given or null if the key does not
     * exist
     */
    public V getValue(K key) {
        return cacheMemory.get(key);
    }

    /**
     * Checks if the cache containing the route is full and, if it is, removes the
     * first added element in it
     */
    private void checkCacheCapacity(){
        if(cacheMemory.size() == cacheCapacity) {
            Iterator<K> iterator = cacheMemory.keySet().iterator();
            cacheMemory.remove(iterator.next());
        }
    }
}
