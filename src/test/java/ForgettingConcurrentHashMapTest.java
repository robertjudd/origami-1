import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the ForgettingConcurrentHashMap
 *
 * @author Robert Judd
 */
public class ForgettingConcurrentHashMapTest
{
    /**
     * Constant test capacity for the class
     */
    private final int TEST_CAPACITY = 100;

    /* Test methods */

    /**
     * Tests that when an item is added to a map where there are fewer entries
     * than the maximum capacity, initial entries are not removed.
     */
    @Test
    public void addingItemToNonFullCollectionShouldNotRemoveValues()
    {
        /* Create a ForgettingMap with a set capacity and a number of items less than the capacity */
        ForgettingConcurrentHashMap<String, Long> forgettingMap =
                createTestPopulatedMap(TEST_CAPACITY, TEST_CAPACITY / 2);

        /* Copy the initial contents for later comparison */
        HashMap<String, Long> initialContentsMap = new HashMap<>(forgettingMap.size());
        initialContentsMap.putAll(forgettingMap.underlyingMap());

        /* Add a new value */
        forgettingMap.add("TestKey", 1L);

        /* Assert that the modified map contains all keys from the initially populated map */
        assertTrue(forgettingMap.keySet().containsAll(initialContentsMap.keySet()));
    }

    /**
     * Tests that when a map is at capacity and a new item is added,
     * the least searched item is removed.
     */
    @Test
    public void mostSearchedItemShouldBeRemoved()
    {
        /* Create a ForgettingMap with a set capacity */
        ForgettingConcurrentHashMap<String, Long> forgettingMap = createTestPopulatedMap(TEST_CAPACITY, TEST_CAPACITY);

        /* Find keys to use as the most searched and least searched */
        List<String> keys = forgettingMap.keySet().stream().collect(Collectors.toList());
        String mostSearchedKey = keys.get(TEST_CAPACITY / 2);
        String leastSearchedKey = keys.get(TEST_CAPACITY / 3);

        /* Execute searches for the most searched key */
        forgettingMap.find(mostSearchedKey);
        forgettingMap.find(mostSearchedKey);
        forgettingMap.find(mostSearchedKey);

        /* Execute less searches for the least searched key */
        forgettingMap.find(leastSearchedKey);
        forgettingMap.find(leastSearchedKey);

        /* Add a new value to the map */
        String newKey = Integer.toString(TEST_CAPACITY + 1);
        Long newValue = 1L;
        addTestMapValue(forgettingMap, newKey, newValue);

        /* Assert that the maximum capacity is enforced */
        maximumCapacityShouldBeEnforced();

        /* Assert that the map contains the new key */
        assertTrue(forgettingMap.containsKey(newKey));

        /* Assert that the map contains the new value */
        assertTrue(forgettingMap.find(newKey).equals(newValue));

        /* Assert that the map contains the most searched item's key */
        assertTrue(forgettingMap.containsKey(mostSearchedKey));

        /* Assert that the map does not contain the least searched item's key */
        assertFalse(forgettingMap.containsKey(leastSearchedKey));
    }

    /**
     * Tests that a collection cannot grow beyond its capacity.
     */
    @Test
    public void maximumCapacityShouldBeEnforced()
    {
        /* Establish a capacity and a number of values greater than the capacity */
        int maximumCapacity = 10;
        int numberOfValues = 30;

        /* Assert that the maximum capacity is less than the number of values to be entered */
        assertTrue(maximumCapacity < numberOfValues);

        /* Create a populated map */
        ForgettingConcurrentHashMap<String, Long> forgettingMap = createTestPopulatedMap(maximumCapacity, numberOfValues);

        /* Assert that the size of the map is that of the maximum capacity */
        assertTrue(forgettingMap.size() == maximumCapacity);
    }

    /* /End test methods */

    /**
     * Creates a populated ForgettingMap with the specified capacity and number of values.
     *
     * @param maximumCapacity maximum capacity of the collection
     * @param numberOfValues  number of values to enter into the collection
     * @return populated collection
     */
    private ForgettingConcurrentHashMap<String, Long> createTestPopulatedMap(int maximumCapacity, int numberOfValues)
    {
        Random random = new Random();

        ForgettingConcurrentHashMap<String, Long> forgettingMap = new ForgettingConcurrentHashMap<>(maximumCapacity);

        IntStream.range(0, numberOfValues).forEach(i -> addTestMapValue(forgettingMap, i, random.nextLong()));

        return forgettingMap;
    }

    /**
     * Adds a test value to a ForgettingMap for the specified key
     *
     * @param forgettingMap map to add value to
     * @param key           key for the value
     * @param value         value to add
     */
    private void addTestMapValue(ForgettingConcurrentHashMap<String, Long> forgettingMap, Object key, Long value)
    {
        forgettingMap.add(key.toString(), value);
    }
}
