package gui;

import windowsState.ComponentState;
import windowsState.Stateful;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import static javax.management.Query.match;

public class RobotCoordinates extends JInternalFrame implements PropertyChangeListener, Stateful {
    private Double x;
    private Double y;
    private GameModel gameModel;
    private JLabel coordinatesLabel;

    public RobotCoordinates(GameModel gameModel) {
        super("Координаты робота", true, true, true, true);
        this.gameModel = gameModel;
        gameModel.addPropertyChangeListener(this);

        coordinatesLabel = new JLabel("", JLabel.CENTER);
        coordinatesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        updateCoordinates(gameModel.getX(), gameModel.getY());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordinatesLabel, BorderLayout.CENTER);
        getContentPane().add(panel);

        setSize(300, 150);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "robotPositionX":
                this.x = (Double)evt.getNewValue();
                break;
            case "robotPositionY":
                this.y = (Double)evt.getNewValue();
                break;
        }
        updateCoordinates(x, y);
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
