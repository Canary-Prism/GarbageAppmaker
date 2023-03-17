package garbageappmaker;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Maker implements Runnable {
    private Thread t;

    private File destination;

    private JFrame panel = new JFrame("Panel");
    private JFrame preview = new JFrame("Preview");

    private ArrayList<Menu> menus = new ArrayList<>();

    private JLabel title_label = new JLabel("Choose an app title:");
    private JTextField title_field = new JTextField();

    private JLabel room_label = new JLabel("Amount of spare room as margin:");
    private JTextField room_field = new JTextField();

    private JButton next_button = new JButton("Next");

    private JLabel dud = new JLabel();

    private int ROOM;

    @Override
    public void run() {
        destination = Main.chooseSaveFile(".java");

        panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        preview.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        prompts();
    }

    private void prompts() {
        title_label.setBounds(Main.ROOM, Main.ROOM, 200, 30);
        title_field.setBounds(Main.ROOM, Main.ROOM + 30, 200, 30);
        
        room_field.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}

            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') ||
                (c == KeyEvent.VK_BACK_SPACE) ||
                (c == KeyEvent.VK_DELETE)))        
                {
                    e.consume();
                }
            }
        });

        //also might as well set the bounds for stuff that doesn't change later :P
        room_label.setBounds(Main.ROOM, Main.ROOM + 60, 200, 30);
        room_field.setBounds(Main.ROOM, Main.ROOM + 90, 200, 30);

        next_button.setBounds(Main.ROOM + 90, Main.ROOM + 120, 100, 30);
        next_button.addActionListener((e) -> {
            preview.setTitle(title_field.getText());
            ROOM = Integer.parseInt(room_field.getText());
            mainMenu(e);
        });

        menus_button.setBounds(Main.ROOM, Main.ROOM, 100, 30);
        variables_button.setBounds(Main.ROOM, Main.ROOM, 100, 30);

        menus_button.addActionListener(this::showMenus);

        new_menu_button.addActionListener((e) -> {
            menus.add(new Menu());
            showMenus(e);
        });

        menus_back_button.addActionListener(this::mainMenu);


        panel.getContentPane().add(title_label);
        panel.getContentPane().add(title_field);
        panel.getContentPane().add(room_label);
        panel.getContentPane().add(room_field);

        panel.getContentPane().add(next_button);

        panel.getContentPane().add(dud);

        panel.setResizable(false);
        
        panel.setSize(200 + Main.ROOM * 2, 170 + Main.ROOM * 2);

        panel.setVisible(true);
    }

    private JButton menus_button = new JButton("Menus");
    private JButton variables_button = new JButton("Variables");

    public void mainMenu(ActionEvent e) {
        panel.getContentPane().removeAll();

        panel.getContentPane().add(menus_button);
        panel.getContentPane().add(variables_button);
        
        panel.getContentPane().add(dud);

        panel.setSize(100 + Main.ROOM * 2, 90 + Main.ROOM);
    }

    private int temp;

    private JButton new_menu_button = new JButton("New");
    private JButton menus_back_button = new JButton("Back");

    public void showMenus(ActionEvent e) {
        temp = 0;
        panel.getContentPane().removeAll();



        menus.forEach((item) -> {
            item.list(temp);
            temp++;
        });


        new_menu_button.setBounds(Main.ROOM + 300, Main.ROOM + temp * 30 + 50, 100, 30);
        menus_back_button.setBounds(Main.ROOM + 400, Main.ROOM + temp * 30 + 50, 100, 30);

        panel.getContentPane().add(new_menu_button);
        panel.getContentPane().add(menus_back_button);

        panel.getContentPane().add(dud);

        panel.setSize(500 + Main.ROOM * 2, temp * 30 + 100 + Main.ROOM * 2);
    }
    
    public void start() {
        if (t == null) {
            t = new Thread(this, "Maker");
            t.start();
        }
    }

    private class Menu {

        private int role;
        private int references = 0;
        private String name;


        private Menu() {
            name_field.setOpaque(true);
            name_field.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {
                    if (name_field.getText().equals("main"))
                        name_field.setBackground(Color.GREEN.brighter());
                    else if (name_field.getText().equals("changelog"))
                        name_field.setBackground(Color.LIGHT_GRAY);
                    else
                        name_field.setBackground(Color.WHITE);

                    name = name_field.getText();
                }
    
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (name_field.getText().isEmpty()) {
                        if (!Character.isJavaIdentifierStart(c)) 
                            e.consume();
                    } else 
                        if (!Character.isJavaIdentifierPart(c)) 
                            e.consume();
                    
                }
            });

            delete_button.addActionListener((e) -> {
                menus.remove(this);
                showMenus(e);
            });

            edit_button.addActionListener(this::show);
        }

        private void show(ActionEvent e) {
            panel.setVisible(false);
        }

        private JTextField name_field = new JTextField();

        private JButton edit_button = new JButton("Edit");
        private JButton delete_button = new JButton("Delete");

        /**
         * <p>proprietary method to put the menus in a human-readable format into the container</p>
         * @param container the container to put the menu options in
         * @param number the order of this menu
         */
        private void list(int number) {
            name_field.setBounds(Main.ROOM, Main.ROOM + number * 30, 300, 30);
            edit_button.setBounds(Main.ROOM + 300, Main.ROOM + number * 30, 100, 30);
            delete_button.setBounds(Main.ROOM + 400, Main.ROOM + number * 30, 100, 30);

            panel.getContentPane().add(name_field);
            panel.getContentPane().add(edit_button);
            panel.getContentPane().add(delete_button);
        }

    }

    private class Variables {

    }
}
