package log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он 
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено 
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений 
 * ограниченного размера) 
 */
public class LogWindowSource
{
    private int m_iQueueLength = 5;

    private final List<LogEntry> m_messages;
    private final List<WeakReference<LogChangeListener>> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;
    private Integer freeNumberMessages;
    private Integer indexMessagesTail;
    private Integer freeSpaceForAdd;

    
    public LogWindowSource(int iQueueLength) 
    {
        m_iQueueLength = iQueueLength;
        m_messages = new ArrayList<>(iQueueLength);
        m_listeners = new ArrayList<>();
        freeNumberMessages = iQueueLength;
        indexMessagesTail = 0;
        freeSpaceForAdd = iQueueLength;
    }
    
    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.add(new WeakReference<>(listener));
            m_activeListeners = null;
        }
    }
    
    public void unregisterListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.removeIf
                    (logChangeListener
                            -> logChangeListener.get().equals(listener));
            m_activeListeners = null;
        }
    }
    
    public void append(LogLevel logLevel, String strMessage)
    {
        synchronized (m_messages) {
            LogEntry entry = new LogEntry(logLevel, strMessage);
            if (freeSpaceForAdd > 0){
                if(size() != 0) {
                    indexMessagesTail = (indexMessagesTail + 1) % m_iQueueLength;
                }
                m_messages.add(entry);
                freeSpaceForAdd -= 1;
            }
            else{
                indexMessagesTail = (indexMessagesTail + 1) % m_iQueueLength;
                m_messages.set(indexMessagesTail, entry);
            }
            if (freeNumberMessages > 0){
                freeNumberMessages -= 1;
            }
        }
        LogChangeListener [] activeListeners = m_activeListeners;
        if (activeListeners == null)
        {
            synchronized (m_listeners)
            {
                if (m_activeListeners == null)
                {
                    List<LogChangeListener> alive = new ArrayList<>();
                    for (WeakReference<LogChangeListener> ref : m_listeners) {
                        LogChangeListener l = ref.get();
                        if (l != null) alive.add(l);
                    }
                    activeListeners = alive.toArray(new LogChangeListener[0]);
                    m_activeListeners = activeListeners;
                }
            }
        }
        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }

    
    public int size()
    {
        synchronized (m_messages) {
            return m_iQueueLength - freeNumberMessages;
        }
    }

    public Iterable<LogEntry> range(int startFrom, int count)
    {

        synchronized (m_messages) {
            if (startFrom < 0 || startFrom >= size()) {
                return Collections.emptyList();
            }
            int start = (indexMessagesTail + 1 + freeNumberMessages + startFrom) % 5;
            int end;
            if (startFrom + count >= m_iQueueLength){
                end = indexMessagesTail;
            }
            else {
                end = (start + count) % m_iQueueLength;
            }
            if (start > indexMessagesTail && end <= indexMessagesTail){
                return Stream.concat(
                        m_messages.subList(start, m_iQueueLength).stream(),
                        m_messages.subList(0, Math.min(end + 1, indexMessagesTail + 1)).stream()
                ).toList();
            }
            return List.copyOf(
                    m_messages.subList(start, Math.min(end + 1, indexMessagesTail + 1)));
        }
    }

    public Iterable<LogEntry> all()
    {
        synchronized (m_messages) {
            return range(0, m_iQueueLength - 1);
        }
    }
}
