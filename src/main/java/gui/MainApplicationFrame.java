package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;

import LocaleManager.LocaleManager;
import game.GameModel;
import log.Logger;
import windowsState.ComponentState;
import windowsState.StateManager;
import windowsState.Stateful;

import static LocaleManager.LocaleManager.getString;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame implements Stateful, PropertyChangeListener
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private StateManager stateManager;
    private GameModel gameModel;
    private LocaleManager localeManager = LocaleManager.getInstance();


    public MainApplicationFrame() {
        stateManager = new StateManager();
        gameModel = new GameModel();
        localeManager.loadLocale();
        localeManager.addPropertyChangeListener(this);

        setContentPane(desktopPane);

        LogWindow logWindow = new LogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow(gameModel);
        addWindow(gameWindow);

        RobotCoordinates robotCoordinates = new RobotCoordinates(gameModel);
        addWindow(robotCoordinates);
        JInternalFrame[] windows = desktopPane.getAllFrames();

        restoreAllWindowsState(windows);

        setJMenuBar(generateMenuBar());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                UIManager.put("OptionPane.yesButtonText", getString("button.yes"));
                UIManager.put("OptionPane.noButtonText", getString("button.no"));

                int choice = JOptionPane.showConfirmDialog(
                        e.getWindow(),
                        getString("exit.confirm"),
                        getString("exit.title"),
                        JOptionPane.YES_NO_OPTION
                );
                if(choice == JOptionPane.YES_OPTION){
                    saveAllWindowsState(windows);
                    localeManager.saveLocale();
                    dispose();
                    System.exit(0);
                }
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    @Override
    public String getName(){
        return "Main";
    }

    @Override
    public Map<String, String> saveState() {
        return new ComponentState().save(this);
    }

    @Override
    public void restoreState(Map<String, String> state) {
        if (state.isEmpty()){
            int inset = 50;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(inset, inset,
                    screenSize.width  - inset*2,
                    screenSize.height - inset*2);
            return;
        }
        new ComponentState().restore(this, state);
    }

    /**
     * Сохраняет размеры и положение всех окон реализующих интерфейс Stateful
     */
    public void saveAllWindowsState(JInternalFrame[] windows){
        stateManager.saveComponentState(getName(), saveState());
        for (JInternalFrame window : windows) {
            if (window instanceof Stateful) {
                stateManager
                        .saveComponentState(window.getName(),
                                ((Stateful) window).saveState());
            }
        }
        stateManager.saveConfig();
    }

    /**
     * Восстанавливает размеры и положение всех окон
     *  реализующих интерфейс Stateful
     */
    private void restoreAllWindowsState(JInternalFrame[] windows) {
        stateManager.loadConfig();
        for (JInternalFrame window : windows) {
            if (window instanceof Stateful) {
                ((Stateful) window).restoreState(stateManager
                        .restoreComponentState(window.getName()));
            }
        }
        restoreState(stateManager.restoreComponentState(getName()));
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
/// /        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }



    /**
     * Создает пункт меню "Выход" для выхода из приложения
     */
    private JMenuItem createExit(){
        JMenuItem exitItem = new JMenuItem(getString("menu.exit.text"),
                KeyEvent.VK_X | KeyEvent.VK_ALT);
        exitItem.addActionListener((event) -> {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        return exitItem;
    }
    /**
     * Создает меню "Выход"
     * Меню позволяющее закрыть приложение
     */
    private JMenu generateExitMenu(){
        JMenu exitMenu = new JMenu(getString("menu.exit"));
        exitMenu.setMnemonic(KeyEvent.VK_E);
        exitMenu.getAccessibleContext().setAccessibleDescription(
                getString("menu.exit.description"));
        exitMenu.add(createExit());
        return exitMenu;
    }

    /**
     * Создает меню "Язык"
     * Позволяет выбрать язык интерфейса
     */
    private JMenu generateLanguageMenu(){
        JMenu localeMenu = new JMenu(getString("menu.language"));
        localeMenu.setMnemonic(KeyEvent.VK_H);
        localeMenu.getAccessibleContext().setAccessibleDescription(
                getString("menu.language.description"));
        localeMenu.add(createLanguageRu());
        localeMenu.add(createLanguageEn());
        return localeMenu;
    }

    /**
     * Создает подпункт для выбора русского языка
     */
    private JMenuItem createLanguageRu(){
        JMenuItem localeRu = new JMenuItem("Русский");
        localeRu.setMnemonic(KeyEvent.VK_L);
        localeRu.addActionListener((event) -> {
            localeManager.setLocale(new Locale("ru"));
        });

        return localeRu;
    }

    /**
     * Создает подпункт для выбора английского языка
     */
    private JMenuItem createLanguageEn(){
        JMenuItem localeEn = new JMenuItem("English");
        localeEn.setMnemonic(KeyEvent.VK_D);
        localeEn.addActionListener((event) -> {
            localeManager.setLocale(Locale.ENGLISH);
        });

        return localeEn;
    }

    /**
     * Создает пункт меню "Системная схема".
     * Меняет вид интерфейса
     */
    private JMenuItem createSystemScheme(){
        JMenuItem systemLookAndFeel = new JMenuItem(getString("menu.look.system")
                , KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeel;
    }

    /**
     * Создает пункт меню "Универсальная схема".
     * Меняет вид интерфейса
     */
    private JMenuItem createCrossplatformScheme()
    {
        JMenuItem crossplatformLookAndFeel = new JMenuItem(getString("menu.look.crossplatform"),
                KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndFeel;
    }
    /**
     * Создает меню "Режим отображения".
     * Содержит пункты для изменения вида интерфейса
     */
    private JMenu generateLookAndFeelMenu(){
        JMenu lookAndFeelMenu = new JMenu(getString("menu.look"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                getString("menu.look.description"));
        lookAndFeelMenu.add(createSystemScheme());
        lookAndFeelMenu.add(createCrossplatformScheme());
        return lookAndFeelMenu;
    }

    /**
     *  Создает пункт меню "Сообщение в лог".
     *  Добавляет сообщение "Новая строка" в лог
     */
    private JMenuItem addLogTestMenu()
    {
        JMenuItem addLogMessageItem = new JMenuItem(getString("menu.test.log"), KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug(getString("log.new"));
        });
        return addLogMessageItem;
    }

    /**
     *  Создает пункт меню "Другое сообщение в лог".
     *  Добавляет сообщение "Старая строка" в лог
     */
    private JMenuItem addOldLogTestMenu()
    {
        JMenuItem addOldLogMessageItem = new JMenuItem(
                getString("menu.test.oldlog"), KeyEvent.VK_L);
        addOldLogMessageItem.addActionListener((event) -> {
            Logger.debug(getString("log.old"));
        });
        return addOldLogMessageItem;
    }

    /**
     * Создает меню с тестовыми командами
     */
    private JMenu generateTestMenu()
    {
        JMenu testMenu = new JMenu(getString("menu.test"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                getString("menu.test.description"));
        testMenu.add(addLogTestMenu());
        testMenu.add(addOldLogTestMenu());
        return testMenu;
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(generateExitMenu());
        menuBar.add(generateLookAndFeelMenu());
        menuBar.add(generateTestMenu());
        menuBar.add(generateLanguageMenu());
        return menuBar;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (LocaleManager.LOCALE_CHANGED.equals(evt.getPropertyName())) {
            setJMenuBar(generateMenuBar());
        }
    }
}
