package tests;

import log.LogEntry;
import log.LogLevel;
import log.LogWindowSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверяет правильность работы методов класса LogWindowSource
 */
class LogWindowSourceTest {
    private LogWindowSource logWindowSource;

    @BeforeEach
    void setUp() {
        logWindowSource = new LogWindowSource(3);
    }

    /**
     * Проверяет правильно ли добавляются элементы и возвращаются итерируемые объекты
     */
    @Test
    void appendAndRangeAndAll() {
        logWindowSource.append(LogLevel.Debug, "a");
        logWindowSource.append(LogLevel.Debug, "b");
        logWindowSource.append(LogLevel.Debug, "c");
        logWindowSource.append(LogLevel.Debug, "d");
        List<String> logList = new ArrayList<>();
        logWindowSource.all().forEach(entry -> logList.add(entry.getMessage()));
        System.out.println(logList);

        assertEquals("b", logList.get(0));
        assertEquals("c", logList.get(1));
        assertEquals("d", logList.get(2));
    }

    /**
     * Проверяет правильно ли возвращается количество сообщений в логе
     */
    @Test
    void size() {
        assertEquals(0, logWindowSource.size());
        logWindowSource.append(LogLevel.Debug, "1");
        assertEquals(1,logWindowSource.size());
        logWindowSource.append(LogLevel.Debug, "1");
        logWindowSource.append(LogLevel.Debug, "1");
        logWindowSource.append(LogLevel.Debug, "1");
        assertEquals(3, logWindowSource.size());
    }

}