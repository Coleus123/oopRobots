package gui;

import windowsState.ComponentState;
import windowsState.Stateful;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class RobotCoordinates extends JInternalFrame implements PropertyChangeListener, Stateful {
    private double x = 100;
    private double y = 100;
    private JLabel coordinatesLabel;

    public RobotCoordinates(GameModel robotModel) {
        super("Координаты робота", true, true, true, true);

        robotModel.addPropertyChangeListener(this);

        coordinatesLabel = new JLabel("", JLabel.CENTER);
        coordinatesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        updateCoordinates();

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
        updateCoordinates();
    }

    private void updateCoordinates() {
        String text = String.format("X: %.2f, Y: %.2f",
                x, y);
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
