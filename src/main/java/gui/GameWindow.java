package gui;

import windowsState.ComponentState;
import windowsState.Stateful;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener;

public class GameWindow extends JInternalFrame implements Stateful
{
    private final static Integer HEIGHT = 300;
    private static final Integer WIDTH = 500;
    private final GameVisualizer m_visualizer;
    private final GameModel gameModel;
    public GameWindow()
    {
        super("Игровое поле", true, true, true, true);
        gameModel = new GameModel();
        m_visualizer = new GameVisualizer(gameModel);
        gameModel.addPropertyChangeListener(m_visualizer);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        setSize(400,  400);
    }

    /**
     * Вернуть модель робота
     */
    protected GameModel getGameModel(){
        return gameModel;
    }

    @Override
    public Map<String, String> saveState() {
        return new ComponentState().save(this);
    }

    @Override
    public void restoreState(Map<String, String> state) {
        if (state.isEmpty()){
            setSize(WIDTH, HEIGHT);
            return;
        }
        new ComponentState().restore(this, state);
    }

    @Override
    public String getName(){
        return "Game";
    }
}
