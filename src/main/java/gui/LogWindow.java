package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import LocaleManager.LocaleManager;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import log.Logger;
import windowsState.ComponentState;
import windowsState.Stateful;

import static LocaleManager.LocaleManager.getString;

public class LogWindow extends JInternalFrame implements LogChangeListener, Stateful, PropertyChangeListener
{
    private LogWindowSource m_logSource;
    private TextArea m_logContent;
    private static final Integer X = 10;
    private static final Integer Y = 10;
    private static final Integer WIDTH = 300;
    private static final Integer HEIGHT = 800;


    public LogWindow()
    {
        super(getString("logwindow.name"), true, true, true, true);
        m_logSource = Logger.getDefaultLogSource();
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
        Logger.debug(getString("logwindow.message"));
        LocaleManager.getInstance().addPropertyChangeListener(this);
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
        return new ComponentState().save(this);
    }

    @Override
    public void restoreState(Map<String, String> state) {
        if (state.isEmpty()){
            setBounds(X, Y, WIDTH, HEIGHT);
            return;
        }
        new ComponentState().restore(this, state);
    }

    @Override
    public String getName(){
        return "Log";
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(LocaleManager.LOCALE_CHANGED.equals(evt.getPropertyName())){
            setTitle(getString("logwindow.name"));
        }
    }
}
