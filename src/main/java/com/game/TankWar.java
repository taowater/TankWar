package com.game;

import com.scene.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.util.ImageUtil;

public class TankWar extends JFrame implements KeyListener, ActionListener {

    public static final int WIDTH = 32 * 13 + 32 * 4;// 528 +24; +32;
    public static final int HEIGHT = 32 * 13 + 32 * 2 + 48;//480 + 24 + 32;

    public Title title = null;
    private StageStart start;

    private Stage stage;
    private Data data;
    private MapEditor mapEditor;
    private Count count;
    private JMenuBar menuBar = null;

    private JMenu gamemenu = null;
    private JMenuItem menuItem = null;
    private JMenuItem selectItem = null;

    private JMenu helpmenu = null;
    private JMenuItem about = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TankWar tankWar = new TankWar();
            tankWar.addKeyListener(tankWar);
            tankWar.setVisible(true);
            tankWar.requestFocusInWindow();
        });
    }

    TankWar() {
        setTitle("坦克大战");// 初始化标题
        setBackground(Color.black);
        getContentPane().setBackground(Color.GRAY);
        setIconImage(ImageUtil.getMaterial("player1"));
        setSize(TankWar.WIDTH, TankWar.HEIGHT + 32); // 设置窗口宽度与高度
        setLayout(null);
        setLocationRelativeTo(null); // 设置居中显示
        setResizable(false); // 设置尺寸不可变
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
        toTitle(false);
    }

    private void init() {
        Game.tankWar = this;
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        menuBar = new JMenuBar();
        gamemenu = new JMenu("游戏");
        gamemenu.setMnemonic('G');
        helpmenu = new JMenu("帮助");

        menuItem = new JMenuItem("复位");
        menuItem.setActionCommand("复位");
        menuItem.addActionListener(this);

        selectItem = new JMenuItem("战雾开关");
        selectItem.setActionCommand("战雾");
        selectItem.addActionListener(this);

        about = new JMenuItem("关于");
        about.setActionCommand("关于");
        about.addActionListener(this);

        helpmenu.add(about);

        gamemenu.add(menuItem);
        gamemenu.add(selectItem);

        menuBar.add(gamemenu);
        menuBar.add(helpmenu);
        setJMenuBar(menuBar);
        menuBar.setVisible(true);
        validate();
    }

    private void removeCurrentScene() {
        if (title != null) {
            title.stopScene();
            remove(title);
            title = null;
        } else if (start != null) {
            start.stopScene();
            remove(start);
            start = null;
        } else if (stage != null) {
            stage.stopScene();
            stage.dispose();
            remove(stage);
            stage = null;
            Game.stage = null;
            if (data != null) {
                data.stopScene();
                remove(data);
                data = null;
            }
        } else if (count != null) {
            count.stopScene();
            remove(count);
            count = null;
        } else if (mapEditor != null) {
            mapEditor.stopScene();
            remove(mapEditor);
            mapEditor = null;
        }
        revalidate();
        repaint();
    }

    @Override
    public void dispose() {
        removeCurrentScene();
        super.dispose();
    }

    public void toTitle(boolean flag) {
        removeCurrentScene();
        Game.GameInit();
        title = new Title(this);
        title.open = flag;
        if (flag) {
            title.logo2.clear();
            title.logoRemove.clear();
        }
        add(title);
        title.startScene();
        revalidate();
    }

    public void toGameStart() {
        removeCurrentScene();
        start = new StageStart();
        add(start);
        start.startScene();
        revalidate();
    }

    public void toGame() {
        removeCurrentScene();
        stage = new Stage();
        Game.stage = this.stage;
        add(stage);
        stage.startScene();

        data = new Data(stage);
        add(data);
        data.startScene();
        revalidate();
    }

    public void toEditor() {
        removeCurrentScene();
        mapEditor = new MapEditor(new int[26][26]);
        add(mapEditor);
        mapEditor.addMouseListener(mapEditor);
        mapEditor.addMouseMotionListener(mapEditor);
        mapEditor.startScene();
        revalidate();
    }

    public void toCount() {
        removeCurrentScene();
        count = new Count();
        add(count);
        count.startScene();
        revalidate();
    }

    public void keyPressed(KeyEvent e) {
        if (title != null) {
            title.keyPressed(e);
        } else if (start != null) {
            start.keyPressed(e);
        } else if (stage != null) {
            stage.keyPressed(e);
        } else if (count != null) {
            count.KeyPressed(e);
        } else if (mapEditor != null) {
            mapEditor.KeyPressed(e);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (title != null) {
        } else if (start != null) {
        } else if (stage != null) {
            stage.keyReleased(e);
        } else if (count != null) {
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "复位":
                toTitle(false);
                break;
            case "战雾":
                Game.fogFlag = !Game.fogFlag;
                break;
            case "关于":
                JOptionPane.showMessageDialog(null, "1 2 3 4 5 切换子弹类型\n1P WSAD控制方向 H开火\n2P 方向键控制方向 0开火", "信息", JOptionPane.PLAIN_MESSAGE);
                break;
        }
    }
}
