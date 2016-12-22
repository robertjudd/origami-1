import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Ordering;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A thread safe hash map that has a defined capacity and removes values with the
 * least number of access attempts after the capacity has been reached.
 *
 * @author Robert Judd
 */
public class ForgettingConcurrentHashMap<K, V>
{
    /**
     * The number of lookups for a key K where K is present in the collection.
     */
    private final BiMap<K, Integer> findCounts;
    /**
     * The capacity of the collection.
     */
    private final int               capacity;

    /**
     * The decorated map collection.
     */
    private final HashMap<K, V> map;

    /**
     * Creates a new empty ForgettingConcurrentHashMap with the maximum capacity specified.
     *
     * @param capacity maximum capacity of the collection
     */
    public ForgettingConcurrentHashMap(int capacity)
    {
        this.findCounts = HashBiMap.create();
        this.capacity = capacity;
        this.map = new HashMap<>();
    }

    /* Required exercise method signatures */

    /**
     * Adds a value V to the collection with the key K.
     *
     * @param key   key for the specified value
     * @param value value for the specified key
     * @return previous value associated with the key if present
     */
    public synchronized V add(K key, V value)
    {
        if (map.size() == capacity)
        {
            removeLeastSearched();
        }

        return map.put(key, value);
    }

    /**
     * Performs a lookup of the specified key K and returns the
     * associated value V if K is present.
     *
     * @param key key to be found
     * @return value associated with key K if K is present
     */
    public synchronized V find(K key)
    {
        /* Increment find count */
        incrementFindCount(key);
        return map.get(key);
    }

    /* /End Required exercise method signatures */

    /**
     * Increments the find count by one for a given key.
     *
     * @param key The key
     */
    private void incrementFindCount(K key)
    {
        if (map.containsKey(key))
        {
            Integer findCount = findCounts.containsKey(key) ? findCounts.get(key) : 0;
            findCount++;
            findCounts.put(key, findCount);
            findCounts.inverse().put(findCount, key);
        }
    }

    /**
     * Removes the least searched item from the collection.
     * If more than one entry has the same least searched value then the
     * item removed from the collection is undefined.
     */
    private void removeLeastSearched()
    {
        if (!findCounts.isEmpty())
        {
            Integer minSearchCount = Ordering.<Integer>natural().min(findCounts.inverse().keySet());
            K leastSearchedKey = findCounts.inverse().get(minSearchCount);

            findCounts.remove(leastSearchedKey);
            map.remove(leastSearchedKey);
        }
        else
        {
            map.remove(map.keySet().iterator().next());
        }
    }

    /**
     * Returns the number of key to value pairs in the collection.
     *
     * @return the number of key to value pairs
     */
    public int size()
    {
        return map.size();
    }

    /**
     * Returns a set containing all keys in the collection.
     *
     * @return a set containing all keys in the collection
     */
    public Set<K> keySet()
    {
        return map.keySet();
    }

    /**
     * Returns true when the specified key is present in the collection or false
     * if it is not.
     *
     * @param key key to check for presence in the key set
     * @return true when the specified key is present in the collection
     */
    public boolean containsKey(K key)
    {
        return map.containsKey(key);
    }

    /**
     * Returns a the decorated data map.
     *
     * @return the decorated data map
     */
    public Map<K, V> underlyingMap()
    {
        return map;
    }
}
