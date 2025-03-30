package windowsState;

import gui.GameWindow;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализует сохранение и восстановление состояний всех объектов наследуемых от Component
 */
public class ComponentState {

    /**
     * Сохраняет состояние
     */
    public Map<String, String> save(Component component){
        Map<String, String> state = new HashMap<>();
        state.put("x", Integer.toString(component.getX()));
        state.put("y", Integer.toString(component.getY()));
        state.put("width", Integer.toString(component.getWidth()));
        state.put("height", Integer.toString(component.getHeight()));
        if (component instanceof JInternalFrame) {
            state.put("isIcon", Boolean.toString(((JInternalFrame) component).isIcon()));
        }
        return state;
    }

    /**
     * Восстанавливает состояние
     */
    public void restore(Component component, Map<String, String> state){
        int x = Integer.parseInt(state.get("x"));
        int y = Integer.parseInt(state.get("y"));
        int width = Integer.parseInt(state.get("width"));
        int height = Integer.parseInt(state.get("height"));
        boolean isIcon = Boolean.parseBoolean(state.get("isIcon"));
        component.setBounds(x, y, width, height);

        if (component instanceof JInternalFrame) {
            if (isIcon) {
                try {
                    ((JInternalFrame) component).setIcon(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
