package game;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static java.lang.Math.*;

/**
 * Содержит данные о роботе и цели куда нужно приехать, а также основную логику работы
 */
public class GameModel {
    private final PropertyChangeSupport propertyChangeSupport =
            new PropertyChangeSupport(this);

    public final static String ROBOT_MOVE = "RobotMoved";
    public final static String TARGET_CHANGE = "TargetChanged";
    public final static Double MAX_DISTANCE = 0.5;
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;
    private final double radius = maxVelocity / maxAngularVelocity;

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
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
        propertyChangeSupport.firePropertyChange(TARGET_CHANGE, null, null);
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

    /**
     *  Определяет направление робота
     */
    private double determineDirection(double angleDiff){
        if (angleDiff > PI) {
            return -maxAngularVelocity;
        }
        else {
            return maxAngularVelocity;
        }

    }
    /**
     * Проверяет находится ли точка внутри окружностей,
     * центры которых лежат на прямой перпендикулярной направлению робота
     */
    private boolean checkTargetInsideCircle(){
        double angle = asNormalizedRadians(m_robotDirection + PI / 2);
        double leftCircleX = m_robotPositionX + radius * cos(angle);
        double leftCircleY = m_robotPositionY + radius * sin(angle);
        angle = asNormalizedRadians(m_robotDirection - PI / 2);
        double rightCircleX = m_robotPositionX + radius * cos(angle);
        double rightCircleY = m_robotPositionY + radius * sin(angle);

        boolean isInCircleLeft = (pow(m_targetPositionX - leftCircleX, 2) +
                pow(m_targetPositionY - leftCircleY, 2)) <= radius * radius;

        boolean isInCircleRight = (pow(m_targetPositionX - rightCircleX, 2) +
                pow(m_targetPositionY - rightCircleY, 2)) <= radius * radius;
        return isInCircleRight || isInCircleLeft;
    }

    protected void onModelUpdateEvent()
    {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < MAX_DISTANCE)
        {
            return;
        }
        if(checkTargetInsideCircle()){
            moveRobot(maxVelocity, 0, 10);
            return;
        }

        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angleDiff = asNormalizedRadians(angleToTarget - m_robotDirection);

        double angularVelocity = determineDirection(angleDiff);
        moveRobot(maxVelocity, angularVelocity, 10);
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
                (sin(m_robotDirection  + angularVelocity * duration) -
                        sin(m_robotDirection));
        if (!Double.isFinite(newX))
        {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection  + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = m_robotPositionY + velocity * duration * sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;
        propertyChangeSupport.firePropertyChange(ROBOT_MOVE, null, null);
    }
}
