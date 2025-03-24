package windowsState;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления состоянием компонентов приложения,
 * позволяет сохранить в файли и считать из файла состояния
 */
public class StateManager {
    private final Map<String, String> globalState;
    private final FilteredMap filteredMap;

    public StateManager(){
        globalState = new HashMap<>();
        filteredMap = new FilteredMap(globalState, "");
    }

    /**
     * Сохраняет состояние компонента в общий словарь
     */
    public void saveComponentState(String componentName, Map<String, String> state) {
        filteredMap.setPrefix(componentName + ".");
        filteredMap.putAll(state);
    }

    /**
     * Восстанавливает состояние компонента из общего словаря
     */
    public Map<String, String> restoreComponentState(String componentName) {
        filteredMap.setPrefix(componentName + ".");
        return filteredMap;
    }

    /**
     * Сохраняет состояния компонент в файл
     */
    public void saveConfig(String fileName)  {
        File configFile = new File(fileName);
        File parentDir = configFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            for (Map.Entry<String, String> entry : globalState.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Загружает состояния компонент из файла
     */
    public void loadConfig(String fileName) {
        File configFile = new File(fileName);
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                reader.lines()
                        .map(line -> line.split("=", 2))
                        .filter(parts -> parts.length == 2)
                        .forEach(parts -> globalState.put(parts[0], parts[1]));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}