package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import log.Logger;
import windowsState.Stateful;

public class LogWindow extends JInternalFrame implements LogChangeListener, Stateful
{
    private LogWindowSource m_logSource;
    private TextArea m_logContent;
    private static final Integer X = 10;
    private static final Integer Y = 10;
    private static final Integer WIDTH = 300;
    private static final Integer HEIGHT = 800;
    private static final Integer ISCON = 1;

    public LogWindow()
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = Logger.getDefaultLogSource();
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
        Logger.debug("Протокол работает");
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public Map<String, String> saveState() {
        Map<String, String> state = new HashMap<>();
        state.put("x", Integer.toString(getX()));
        state.put("y", Integer.toString(getY()));
        state.put("width", Integer.toString(getWidth()));
        state.put("height", Integer.toString(getHeight()));
        state.put("isIcon", Boolean.toString(isIcon()));
        return state;
    }

    @Override
    public void restoreState(Map<String, String> state) {
        if (state.isEmpty()){
            setBounds(X, Y, WIDTH, HEIGHT);
            return;
        }
        int x = Integer.parseInt(state.get("x"));
        int y = Integer.parseInt(state.get("y"));
        int width = Integer.parseInt(state.get("width"));
        int height = Integer.parseInt(state.get("height"));
        boolean isIcon = Boolean.parseBoolean(state.get("isIcon"));

        setBounds(x, y, width, height);
        if (isIcon) {
            try {
                setIcon(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName(){
        return "Log";
    }
}
