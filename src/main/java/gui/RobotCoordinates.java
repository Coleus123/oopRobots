package gui;

import LocaleManager.LocaleManager;
import game.GameModel;
import windowsState.ComponentState;
import windowsState.Stateful;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Objects;

import static LocaleManager.LocaleManager.getString;

public class RobotCoordinates extends JInternalFrame implements PropertyChangeListener, Stateful {
    private GameModel gameModel;
    private JLabel coordinatesLabel;


    public RobotCoordinates(GameModel gameModel) {
        super(getString("robotcoordinations.name"), true, true, true, true);
        this.gameModel = gameModel;
        gameModel.addPropertyChangeListener(this);
        JPanel panel = new JPanel(new BorderLayout());
        coordinatesLabel = new JLabel("", JLabel.CENTER);
        updateCoordinates(gameModel.getX(), gameModel.getY());

        panel.add(coordinatesLabel, BorderLayout.CENTER);
        getContentPane().add(panel);

        setSize(300, 150);
        LocaleManager.getInstance().addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(GameModel.ROBOT_MOVE.equals(evt.getPropertyName())) {
            updateCoordinates(gameModel.getX(), gameModel.getY());
            repaint();
        }
        if(LocaleManager.LOCALE_CHANGED.equals(evt.getPropertyName())){
            setTitle(getString("robotcoordinations.name"));
        }
    }

    private void updateCoordinates(Double coordinateX, Double coordinateY) {
        String text = String.format("X: %.2f, Y: %.2f",
                coordinateX, coordinateY);
        coordinatesLabel.setText(text);
    }

    @Override
    public Map<String, String> saveState() {
        return new ComponentState().save(this);
    }

    @Override
    public void restoreState(Map<String, String> state) {
        if (!state.isEmpty()) {
            new ComponentState().restore(this, state);
        }
    }

    @Override
    public String getName(){
        return "robotCoordinates";
    }
}
