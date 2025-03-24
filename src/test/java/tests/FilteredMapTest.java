package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import windowsState.FilteredMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для FilteredMap, проверяющий корректность фильтрации
 * элементов по префиксу ключа.
 */
class FilteredMapTest {
    private Map<String, String> originalMap;

    @BeforeEach
    public void setUp(){
        originalMap = new HashMap<>();
    }

    /**
     * Проверяет, правильно ли возвращается значение по ключу
     */
    @Test
    void testGet() {
        originalMap.put("prefix.key1", "value1");
        originalMap.put("prefix.key2", "value2");

        FilteredMap filteredMap = new FilteredMap(originalMap, "prefix.");

        assertEquals("value1", filteredMap.get("key1"));
        assertEquals("value2", filteredMap.get("key2"));
    }

    /**
     * Проверяет, правильно ли сохраняется ключ-значение в словарь
     */
    @Test
    void testPut() {
        FilteredMap filteredMap = new FilteredMap(originalMap, "prefix.");

        filteredMap.put("key1", "value1");
        filteredMap.put("key2", "value2");

        assertEquals("value1", originalMap.get("prefix.key1"));
        assertEquals("value2", originalMap.get("prefix.key2"));

        assertEquals("value1", filteredMap.get("key1"));
        assertEquals("value2", filteredMap.get("key2"));
    }

    /**
     * Проверяет, правильно ли возвращается множество всех пар ключ-значение с одинаковым
     * префиксом
     */
    @Test
    void testEntrySet() {
        originalMap.put("prefix.key1", "value1");
        originalMap.put("prefix.key2", "value2");

        FilteredMap filteredMap = new FilteredMap(originalMap, "prefix.");

        Set<Map.Entry<String, String>> entries = filteredMap.entrySet();

        assertTrue(entries.contains(new HashMap.SimpleEntry<>("key1", "value1")));
        assertTrue(entries.contains(new HashMap.SimpleEntry<>("key2", "value2")));
    }

    /**
     * Проверяет, правильно ли присваивается префикс к экземпляру класса
     * для дальнейшего поиска пар ключ-значение по обновленному префиксу
     */
    @Test
    void testSetPrefix(){
        originalMap.put("prefix1.key1", "value1");
        originalMap.put("prefix2.key2", "value2");

        FilteredMap filteredMap = new FilteredMap(originalMap, "prefix1.");

        Set<Map.Entry<String, String>> entries = filteredMap.entrySet();
        assertEquals(1, entries.size());
        assertTrue(entries.contains(new HashMap.SimpleEntry<>("key1", "value1")));

        filteredMap.setPrefix("prefix2.");
        entries = filteredMap.entrySet();
        assertEquals(1, entries.size());
        assertTrue(entries.contains(new HashMap.SimpleEntry<>("key2", "value2")));
    }
}