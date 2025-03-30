package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import windowsState.StateManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверяет класс StateManager на правильную работу с состояниями
 * компонент в общем словаре
 */
class StateManagerTest {
    private StateManager stateManager;

    @TempDir
    static File tempDir;

    @BeforeEach
    public void setUp(){
        File configFile = new File(tempDir, "config.txt");
        stateManager = new StateManager(configFile.toString());
    }

    /**
     * Проверяет, правильно ли сохраняются и восстанавливаются состояния компонент с префиксом,
     * относящийся к каждой из компонент
     */
    @Test
    void testSaveAndRestoreComponentState() {
        Map<String, String> componentState = new HashMap<>();

        componentState.put("key1", "value1");
        componentState.put("key2", "value2");

        stateManager.saveComponentState("component1", componentState);
        Map<String, String> restoredState = stateManager
                .restoreComponentState("component1");

        assertEquals("value1", restoredState.get("key1"));
        assertEquals("value2", restoredState.get("key2"));
    }

    /**
     * Проверяет, правильно ли сохраняется в файл и восстанавливается из него
     * общий словарь
     */
    @Test
    void testSaveAndLoadConfig(){
        Map<String, String> component1State = new HashMap<>();
        component1State.put("key1", "value1");
        component1State.put("key2", "value2");
        stateManager.saveComponentState("component1", component1State);
        stateManager.saveConfig();
        StateManager loadedStateManager = new StateManager();
        loadedStateManager.loadConfig();
        Map<String, String> restoredComponent1State =
                loadedStateManager
                        .restoreComponentState("component1");
        assertEquals("value1", restoredComponent1State.get("key1"));
        assertEquals("value2", restoredComponent1State.get("key2"));
    }
}