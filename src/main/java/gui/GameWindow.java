package gui;

import windowsState.Stateful;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame implements Stateful
{
    private final static Integer HEIGHT = 300;
    private static final Integer WIDTH = 500;
    private final GameVisualizer m_visualizer;
    public GameWindow()
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        setSize(400,  400);
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
            setSize(WIDTH, HEIGHT);
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
        return "Game";
    }
}
