package windowsState;

import java.util.Map;


/**
 * Интерфейс для объектов, которые могут сохранять и восстанавливать своё состояние
 */
public interface Stateful {
    /**
     * Сохраняет текущее состояние объекта в виде словаря
     */
    Map<String, String> saveState();

    /**
     * Восстанавливает состояние объекта из словаря
     */
    void restoreState(Map<String, String> state);

    /**
     * Имя окна
     */
    String getName();
}
