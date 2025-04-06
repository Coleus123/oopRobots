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
    private GameVisualizer m_visualizer;
    private GameController gameController;
    public GameWindow(GameModel gameModel)
    {
        super("Игровое поле", true, true, true, true);
        gameController = new GameController(gameModel);
        m_visualizer = new GameVisualizer(gameModel, gameController);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        setSize(400,  400);
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
