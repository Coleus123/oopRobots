package windowsState;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс для работы с подсловарями на основе префиксов ключей.
 */
public class FilteredMap extends AbstractMap<String, String> {
    private final Map<String, String> originalMap;
    private String prefix;

    /**
     * Конструктор для создания фильтрованного словаря
     */
    public FilteredMap(Map<String, String> originalMap, String prefix) {
        this.originalMap = originalMap;
        this.prefix = prefix;
    }

    /**
     * Установить новый префикс для поиска по словарю
     */
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }

    /**
     * Получает значение по ключу, добавляя префикс
     */
    @Override
    public String get(Object key) {
        return originalMap.get(prefix + key);
    }

    /**
     * Добавляет значение в словарь с учётом префикса
     */
    @Override
    public String put(String key, String value) {
        return originalMap.put(prefix + key, value);
    }

    /**
     * Возвращает множество записей (ключ-значение) для ключей с префиксом
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        return originalMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .map(entry -> new SimpleEntry<>(
                        entry.getKey().substring(prefix.length()),
                        entry.getValue()
                ))
                .collect(Collectors.toSet());
    }
}