package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;
import java.beans.PropertyChangeSupport;
import java.util.Timer;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener;
import static java.lang.Math.abs;

public class GameModel {
    private final PropertyChangeSupport propertyChangeSupport =
            new PropertyChangeSupport(this);
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;
    //private final double radius = maxVelocity / maxAngularVelocity + 1;

    public GameModel() {}

    /**
     * Возвращает X координату
     */
    public double getX(){
        return m_robotPositionX;
    }

    /**
     * Возвращает Y координату
     */
    public double getY(){
        return m_robotPositionY;
    }

    /**
     * Возвращает направление робота
     */
    public double getDirection(){
        return m_robotDirection;
    }

    /**
     * Возвращает X координату цели
     */
    public int getTargetX(){
        return m_targetPositionX;
    }

    /**
     * Возвращает Y координату цели
     */
    public int getTargetY(){
        return m_targetPositionY;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    protected void setTargetPosition(Point p)
    {
        propertyChangeSupport.firePropertyChange("targetPositionX", m_targetPositionX, p.x);
        m_targetPositionX = p.x;
        propertyChangeSupport.firePropertyChange("targetPositionY", m_targetPositionY, p.y);
        m_targetPositionY = p.y;
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    protected void onModelUpdateEvent()
    {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5)
        {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angleDiff = asNormalizedRadians(angleToTarget - m_robotDirection);
//
//        double leftOffsetX = -distance * Math.sin(m_robotDirection) + m_robotPositionX;
//        double leftOffsetY = distance * Math.cos(m_robotDirection) + m_robotPositionY;
//        double rightOffsetX = distance * Math.sin(m_robotDirection) + m_robotPositionX;
//        double rightOffsetY = -distance * Math.cos(m_robotDirection) + m_robotPositionY;
//        if (((Math.pow(leftOffsetY - m_robotPositionY, 2)
//                + Math.pow(leftOffsetX - m_robotPositionX,2) < radius * radius ||
//                Math.pow(rightOffsetY - m_robotPositionY, 2)
//                        + Math.pow(rightOffsetX - m_robotPositionX,2) < radius * radius))){
//            System.out.println("укц");
//            moveRobot(velocity, 0, 10);
//            return;
//            if (angleDiff > 2 * Math.PI){
//                angleDiff -= 2 * Math.PI;
//            }
//            if((distance / abs(angleDiff) > distance / maxAngularVelocity)){
//
//            }
//        }


        // Определяем направление поворота (кратчайший путь)
        double angularVelocity;
        if (angleDiff > Math.PI) {
            angularVelocity = -maxAngularVelocity; // Поворот по часовой стрелке
        } else if (angleDiff > 0) {
            angularVelocity = maxAngularVelocity; // Поворот против часовой стрелки
        } else if (angleDiff < -Math.PI) {
            angularVelocity = maxAngularVelocity; // Поворот против часовой стрелки
        } else {
            angularVelocity = -maxAngularVelocity; // Поворот по часовой стрелке
        }
        moveRobot(velocity, angularVelocity, 10);
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection  + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX))
        {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection  + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        propertyChangeSupport.firePropertyChange("robotPositionX",
                m_robotPositionX, newX);
        m_robotPositionX = newX;
        propertyChangeSupport.firePropertyChange("robotPositionY",
                m_robotPositionY, newY);
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        propertyChangeSupport.firePropertyChange("robotDirection",
                m_robotDirection, newDirection);
        m_robotDirection = newDirection;
    }
}
