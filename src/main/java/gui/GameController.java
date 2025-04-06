package gui;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    private final GameModel model;
    private final Timer timer;

    public GameController(GameModel model) {
        this.model = model;
        this.timer = new Timer("game timer", true);
        startModelUpdates();
    }

    private void startModelUpdates() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.onModelUpdateEvent();
            }
        }, 0, 10);
    }

    public void setTarget(Point p) {
        model.setTargetPosition(p);
    }
}
