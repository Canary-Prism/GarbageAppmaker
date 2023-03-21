package garbageappmaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatter;


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
            preview.setMinimumSize(new Dimension(ROOM * 2, ROOM * 2));
            mainMenu(e);
        });

        menus_button.setBounds(Main.ROOM, Main.ROOM, 100, 30);
        variables_button.setBounds(Main.ROOM, Main.ROOM + 30, 100, 30);

        save_button.setBounds(Main.ROOM, Main.ROOM + 30, 100, 30);
        save_button.addActionListener(this::done);

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

    private JButton save_button = new JButton("Save");

    public void mainMenu(ActionEvent e) {
        panel.getContentPane().removeAll();

        panel.getContentPane().add(menus_button);

        //panel.getContentPane().add(variables_button);

        panel.getContentPane().add(save_button);
        
        panel.getContentPane().add(dud);

        panel.setSize(100 + Main.ROOM * 2, 150 + Main.ROOM);
    }

    private int mains, changelogs;
    private ArrayList<String> menu_names = new ArrayList<>();
    private ArrayList<String> component_names = new ArrayList<>();

    /**
     * Now we check whether or not this is actually a valid "app" (glob of menus) to save
     * @param e entirely unused... just to make button events happy i guess (you can pass {@code null}, it's fine)
     */
    public void done(ActionEvent e) {
        mains = 0; changelogs = 0;
        menu_names.clear();

        for (Menu menu : menus) {
            if (menu.getName().equals("")) {
                JOptionPane.showMessageDialog(null, "the " + (menus.indexOf(menu) + 1) + "th menu has no name", "No Name", JOptionPane.ERROR_MESSAGE);
                showMenus(e);
                return;
            }
            if (menu.getName().equals("changelog"))
                changelogs++;
            if (menu.getName().equals("main"))
                mains++;
            else if (menu.references == 0) {
                JOptionPane.showMessageDialog(null, menu.getName() + " menu is useless as nothing refers to it", "Dead Menu", JOptionPane.ERROR_MESSAGE);
                showMenus(e);
                return;
            }
            if (!Character.isJavaIdentifierStart(menu.getName().charAt(0))) {
                JOptionPane.showMessageDialog(null, menu.getName() + " menu starts with an invalid character", "Invalid Identifier Start", JOptionPane.ERROR_MESSAGE);
                showMenus(e);
                return;
            }
            if (menu_names.contains(menu.getName())) {
                JOptionPane.showMessageDialog(null, "the " + (menu_names.indexOf(menu.getName()) + 1) + "th and " + (menus.indexOf(menu) + 1) + "th menus share a name", "Duplicate Names", JOptionPane.ERROR_MESSAGE);
                showMenus(e);
                return;
            }
            component_names.clear();
            for (Maker.Menu.Component<?> component : menu.components) {
                if (component.getName().equals("")) {
                    JOptionPane.showMessageDialog(null, "the " + (menu.components.indexOf(component) + 1) + "th component has no name", "No Name", JOptionPane.ERROR_MESSAGE);
                    menu.showMenu(e);
                    return;
                }
                if (component_names.contains(component.getName())) {
                    JOptionPane.showMessageDialog(null, "the " + (component_names.indexOf(component.getName()) + 1) + "th and " + (menu.components.indexOf(component) + 1) + "th components share a name", "Duplicate Names", JOptionPane.ERROR_MESSAGE);
                    menu.showMenu(e);
                    return;
                }
                component_names.add(component.getName());
            }
            menu_names.add(menu.getName());
        }
        if (mains != 1) {
            JOptionPane.showMessageDialog(null, "Invalid number of main menus, must be 1", "Invalid Main Count", JOptionPane.ERROR_MESSAGE);
            showMenus(e);
            return;
        }
        if (changelogs > 1) {
            JOptionPane.showMessageDialog(null, "Too many changelogs, max of 1", "Invalid Changelog Count", JOptionPane.ERROR_MESSAGE);
            showMenus(e);
            return;
        }

        new Writer(destination, title_field.getText(), ROOM, menus, (changelogs == 1)).write();
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

        panel.repaint();
    }
    
    public void start() {
        if (t == null) {
            t = new Thread(this, "Maker");
            t.start();
        }
    }

    public class Menu {

        private int references = 0;
        private String name;

        private Dimension size = new Dimension(300 + ROOM * 2, 300 + ROOM * 2);

        public Dimension getSize() {
            return size;
        }
        

        private Menu() {
            name_field.setOpaque(true);
            name_field.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {}
                public void keyReleased(KeyEvent e) {
                    if (name_field.getText().equals("main")) {
                        name_field.setBackground(Color.GREEN.brighter());
                        edit_button.setEnabled(true);
                    } else if (name_field.getText().equals("changelog")) {
                        name_field.setBackground(Color.LIGHT_GRAY);
                        edit_button.setEnabled(false);
                    } else {
                        name_field.setBackground(Color.WHITE);
                        edit_button.setEnabled(true);
                    }

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

            edit_button.addActionListener(this::showMenu);

            sizex_spinner = new JSpinner(new SpinnerNumberModel(300, 0, 2147483647, 1));
            sizey_spinner = new JSpinner(new SpinnerNumberModel(300, 0, 2147483647, 1));
            ((DefaultFormatter)((JFormattedTextField)sizex_spinner.getEditor().getComponent(0)).getFormatter()).setAllowsInvalid(false);
            ((DefaultFormatter)((JFormattedTextField)sizey_spinner.getEditor().getComponent(0)).getFormatter()).setAllowsInvalid(false);

            sizex_label.setBounds(Main.ROOM, Main.ROOM, 50, 30);
            sizey_label.setBounds(Main.ROOM + 150, Main.ROOM, 50, 30);

            sizex_spinner.setBounds(Main.ROOM + 50, Main.ROOM, 100, 30);
            sizey_spinner.setBounds(Main.ROOM + 200, Main.ROOM, 100, 30);
            
            size_confirm_button.setBounds(Main.ROOM + 300, Main.ROOM, 100, 30);

            size_confirm_button.addActionListener((e) -> {
                size.setSize((int)sizex_spinner.getValue() + ROOM * 2, (int)sizey_spinner.getValue() + ROOM * 2);
                preview.setSize(size);
                components.forEach((item) -> {
                    item.updateBounds(e);
                });
            });
            menu_back_button.addActionListener((e) -> {
                preview.setVisible(false);
                showMenus(e);
            });

            new_component_button.addActionListener((e) -> {
                switch (JOptionPane.showOptionDialog(null, "Choose a Component type", "New Component", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, Component.SUPPORTED_COMPONENTS, 0)) {
                    case JOptionPane.CLOSED_OPTION -> {
                        JOptionPane.showMessageDialog(null, "Cancelled");
                        return;
                    }
                    case 0 ->
                        components.add(new Component<JLabel>(new JLabel()));
                    case 1 ->
                        components.add(new Component<JButton>(new JButton()));
                }
                showMenu(e);
            });

        }

        private ArrayList<Component<? extends JComponent>> components = new ArrayList<>();

        public ArrayList<Component<? extends JComponent>> getComponents() {
            return components;
        }


        private JLabel sizex_label = new JLabel("Width: ");
        private JLabel sizey_label = new JLabel("Height: ");

        private JButton size_confirm_button = new JButton("Confirm");

        private JSpinner sizex_spinner;
        private JSpinner sizey_spinner;

        private void showMenu(ActionEvent e) {
            panel.getContentPane().removeAll();
            preview.getContentPane().removeAll();

            try {
                preview.removeComponentListener(preview.getComponentListeners()[0]);
            } catch (ArrayIndexOutOfBoundsException d) {}

            preview.setSize(size);

            preview.addComponentListener(new ComponentListener() {

                @Override
                public void componentResized(ComponentEvent e) {
                    sizex_spinner.setValue(e.getComponent().getWidth() - ROOM * 2);
                    sizey_spinner.setValue(e.getComponent().getHeight() - ROOM * 2);

                    components.forEach((item) -> {
                        item.updateBounds(e);
                    });
    
                }

                @Override
                public void componentMoved(ComponentEvent e) {}

                @Override
                public void componentShown(ComponentEvent e) {}

                @Override
                public void componentHidden(ComponentEvent e) {}
                
            });

            components.forEach((item) -> item.put());

            panel.getContentPane().add(sizex_spinner);
            panel.getContentPane().add(sizey_spinner);

            panel.getContentPane().add(sizex_label);
            panel.getContentPane().add(sizey_label);
            panel.getContentPane().add(size_confirm_button);

            preview.getContentPane().add(dud);
            panel.repaint();

            preview.setVisible(true);
            preview.repaint();
            
            showComponents(e);
        }

        JButton new_component_button = new JButton("New");
        JButton menu_back_button = new JButton("Back");

        private void showComponents(ActionEvent e) {
            temp = 1;
    
            components.forEach((item) -> {
                item.list(temp);
                temp++;
            });
    
    
            new_component_button.setBounds(Main.ROOM + 200, Main.ROOM + temp * 30 + 50, 100, 30);
            menu_back_button.setBounds(Main.ROOM + 300, Main.ROOM + temp * 30 + 50, 100, 30);
    
            panel.getContentPane().add(new_component_button);
            panel.getContentPane().add(menu_back_button);
    
            panel.getContentPane().add(dud);
    
            panel.setSize(400 + Main.ROOM * 2, temp * 30 + 100 + Main.ROOM * 2);
            panel.repaint();
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

        public String getName() {
            return name;
        }

        /**
         * <p>A compatibility class for multiple different types of JComponents</p>
         */
        public class Component<E extends JComponent> {

            public static final String[] SUPPORTED_COMPONENTS = {
                "JLabel",
                "JButton"
            };

            public E component;
            private String name;

            public String getText() {
                if (component instanceof JLabel) {
                    return ((JLabel)component).getText();
                } else if (component instanceof JButton) {
                    return ((JButton)component).getText();
                }
                return "";
            }

            public String getName() {
                return name;
            }

            public int posx, posy, sizex, sizey;

            private Component(E component) {
                this.component = component;

                posx = 0; posy = 0; sizex = 100; sizey = 30;

                this.component.setBounds(posx, posy, sizex, sizey);

                name_field.addKeyListener(new KeyListener() {
                    public void keyPressed(KeyEvent e) {}
                    public void keyReleased(KeyEvent e) {
                        name = name_field.getText();
                    }
        
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isJavaIdentifierPart(c)) 
                            e.consume();
                        
                    }
                });
    
                delete_button.addActionListener((e) -> {
                    components.remove(this);
                    showMenu(e);
                });

                back_button.addActionListener((e) -> showMenu(e));
    
                edit_button.addActionListener(this::show);

                posx_label.setBounds(Main.ROOM, Main.ROOM, 50, 30);
                posx_spinner.setBounds(Main.ROOM + 50, Main.ROOM, 100, 30);
                posy_label.setBounds(Main.ROOM, Main.ROOM + 30, 50, 30);
                posy_spinner.setBounds(Main.ROOM + 50, Main.ROOM + 30, 100, 30);

                sizex_label.setBounds(Main.ROOM + 150, Main.ROOM, 50, 30);
                sizex_spinner.setBounds(Main.ROOM + 200, Main.ROOM, 100, 30);
                sizey_label.setBounds(Main.ROOM + 150, Main.ROOM + 30, 50, 30);
                sizey_spinner.setBounds(Main.ROOM + 200, Main.ROOM + 30, 100, 30);

                ((DefaultFormatter)((JFormattedTextField)posx_spinner.getEditor().getComponent(0)).getFormatter()).setAllowsInvalid(false);
                ((DefaultFormatter)((JFormattedTextField)posy_spinner.getEditor().getComponent(0)).getFormatter()).setAllowsInvalid(false);
                ((DefaultFormatter)((JFormattedTextField)sizex_spinner.getEditor().getComponent(0)).getFormatter()).setAllowsInvalid(false);
                ((DefaultFormatter)((JFormattedTextField)sizey_spinner.getEditor().getComponent(0)).getFormatter()).setAllowsInvalid(false);

                ((DefaultFormatter)((JFormattedTextField)posx_spinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
                ((DefaultFormatter)((JFormattedTextField)posy_spinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
                ((DefaultFormatter)((JFormattedTextField)sizex_spinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);
                ((DefaultFormatter)((JFormattedTextField)sizey_spinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);


                posx_spinner.addChangeListener(this::updateBounds);
                posy_spinner.addChangeListener(this::updateBounds);
                sizex_spinner.addChangeListener(this::updateBounds);
                sizey_spinner.addChangeListener(this::updateBounds);

                text_field.setLineWrap(true);

                if (component instanceof JLabel) {

                    ((JLabel)component).setVerticalAlignment(SwingConstants.TOP);

                    text_field.setBounds(Main.ROOM, Main.ROOM + 60, 300, 90);

                    text_field.addKeyListener(new KeyListener() {

                        @Override
                        public void keyTyped(KeyEvent e) {}
    
                        @Override
                        public void keyPressed(KeyEvent e) {}
    
                        @Override
                        public void keyReleased(KeyEvent e) {
                            ((JLabel)component).setText(text_field.getText().replaceAll("\n", ""));
                        }
                        
                    });

                    back_button.setBounds(Main.ROOM + 200, Main.ROOM + 150, 100, 30);
                    
                } else if (component instanceof JButton) {
                    text_field.setBounds(Main.ROOM, Main.ROOM + 60, 300, 30);

                    text_field.addKeyListener(new KeyListener() {

                        @Override
                        public void keyTyped(KeyEvent e) {}
    
                        @Override
                        public void keyPressed(KeyEvent e) {
                            char c = e.getKeyChar();
                            if (c == KeyEvent.VK_ENTER)
                                e.consume();
                        }
    
                        @Override
                        public void keyReleased(KeyEvent e) {
                            ((JButton)component).setText(text_field.getText().replaceAll("\n", ""));
                        }
                        
                    });

                    onclick_label = new JLabel("When Clicked:");

                    onclick_options.add("Nothing");

                    menus.forEach((e) -> {
                        onclick_options.add("Go to " + e.getName() + " Menu");
                    });



                    onclick_box = new JComboBox<>(onclick_options.toArray(new String[0]));

                    onclick_label.setBounds(Main.ROOM, Main.ROOM + 90, 100, 30);
                    onclick_box.setBounds(Main.ROOM, Main.ROOM + 120, 300, 30);

                    onclick_box.addActionListener((e) -> {
                        if (reference_menu != null) {
                            reference_menu.references--;
                        }

                        try {
                            ((JButton)component).removeActionListener(((JButton)component).getActionListeners()[0]);
                        } catch (ArrayIndexOutOfBoundsException r) {}

                        if (onclick_box.getSelectedIndex() == 0) 
                            return;

                        reference_menu = menus.get(onclick_box.getSelectedIndex() - 1);

                        reference_menu.references++;

                        ((JButton)component).addActionListener(reference_menu::showMenu);

                    });

                    back_button.setBounds(Main.ROOM + 200, Main.ROOM + 150, 100, 30);
                }

                updateBounds(null);
            }

            /**
             * <p>updates the {@code component} to have the current bounds</p>
             * <p>also recalculates the spinner limits on how much the bounds can vary</p>
             * <p>pushing the {@code component} back in if necessary</p>
             */
            private void updateBounds(EventObject e) {
                posx = (int)posx_spinner.getValue();
                posy = (int)posy_spinner.getValue();
                sizex = (int)sizex_spinner.getValue();
                sizey = (int)sizey_spinner.getValue();

                posx += (((int)size.getWidth() - ROOM * 2) - (posx + sizex)) < 0? 
                    (posx + ((int)size.getWidth() - ROOM * 2) - (posx + sizex)) > 0?
                        ((int)size.getWidth() - ROOM * 2) - (posx + sizex)
                    :
                        0
                : 
                    0;
                
                posy += (((int)size.getHeight() - ROOM * 2) - (posy + sizey)) < 0? 
                    (posy + ((int)size.getHeight() - ROOM * 2) - (posy + sizey)) > 0?
                        ((int)size.getHeight() - ROOM * 2) - (posy + sizey)
                    :
                        0
                : 
                    0;

                sizex = ((int)size.getWidth() - ROOM * 2) < sizex? ((int)size.getWidth() - ROOM * 2) : sizex;
                sizey = ((int)size.getHeight() - ROOM * 2) < sizey? ((int)size.getHeight() - ROOM * 2) : sizey;

                posx_spinner.setValue(posx);
                posy_spinner.setValue(posy);
                sizex_spinner.setValue(sizex);
                sizey_spinner.setValue(sizey);

                component.setBounds(ROOM + posx, ROOM + posy, sizex, sizey);
            }

            private void put() {
                preview.getContentPane().add(this.component);
            }

            private JTextArea text_field = new JTextArea();

            private JLabel posx_label = new JLabel("Pos x:");
            private JLabel posy_label = new JLabel("Pos y:");
            private JLabel sizex_label = new JLabel("Width:");
            private JLabel sizey_label = new JLabel("Height:");

            private SpinnerNumberModel posx_model = new SpinnerNumberModel();
            private SpinnerNumberModel posy_model = new SpinnerNumberModel();
            private SpinnerNumberModel sizex_model = new SpinnerNumberModel();
            private SpinnerNumberModel sizey_model = new SpinnerNumberModel();


            private JSpinner posx_spinner = new JSpinner(posx_model);
            private JSpinner posy_spinner = new JSpinner(posy_model);
            private JSpinner sizex_spinner = new JSpinner(sizex_model);
            private JSpinner sizey_spinner = new JSpinner(sizey_model);

            private JLabel onclick_label;
            private JComboBox<String> onclick_box;
            private Menu reference_menu;

            public Menu getReferenceMenu() {
                return reference_menu;
            }

            private JButton back_button = new JButton("Back");

            private void show(ActionEvent e) {
                panel.getContentPane().removeAll();

                panel.getContentPane().add(posx_label);
                panel.getContentPane().add(posy_label);
                panel.getContentPane().add(sizex_label);
                panel.getContentPane().add(sizey_label);

                panel.getContentPane().add(posx_spinner);
                panel.getContentPane().add(posy_spinner);
                panel.getContentPane().add(sizex_spinner);
                panel.getContentPane().add(sizey_spinner);

                panel.getContentPane().add(text_field);

                panel.getContentPane().add(back_button);

                if (component instanceof JLabel) {
                    panel.setSize(320 + Main.ROOM * 2, 200 + Main.ROOM * 2);
                } else if (component instanceof JButton) {

                    updateOnclickOptions();

                    panel.getContentPane().add(onclick_label);
                    panel.getContentPane().add(onclick_box);

                    panel.setSize(320 + Main.ROOM, 200 + Main.ROOM * 2);
                }

                panel.getContentPane().add(dud);
                panel.repaint();
            }

            ArrayList<String> onclick_options = new ArrayList<>();

            private void updateOnclickOptions() {

                onclick_options.clear();

                onclick_options.add("Nothing");

                int index = -1;

                for (int i = 0; i < menus.size(); i++) {
                    onclick_options.add("Go to " + menus.get(i).getName() + " Menu");
                    if (menus.get(i) == reference_menu)
                        index = i + 1;
                }

                onclick_box = new JComboBox<>(onclick_options.toArray(new String[0]));

                if (index != -1) {
                    onclick_box.setSelectedIndex(index);
                    reference_menu = null;
                }

                onclick_box.setBounds(Main.ROOM, Main.ROOM + 120, 300, 30);

                onclick_box.addActionListener((e) -> {
                    if (reference_menu != null) {
                        reference_menu.references--;
                    }

                    try {
                        ((JButton)component).removeActionListener(((JButton)component).getActionListeners()[0]);
                    } catch (ArrayIndexOutOfBoundsException r) {}

                    reference_menu = null;

                    if (onclick_box.getSelectedIndex() == 0) 
                        return;

                    reference_menu = menus.get(onclick_box.getSelectedIndex() - 1);

                    reference_menu.references++;

                    ((JButton)component).addActionListener(reference_menu::showMenu);

                });

            }


            private JTextField name_field = new JTextField();

            private JButton edit_button = new JButton("Edit");
            private JButton delete_button = new JButton("Delete");
    

            private void list(int number) {
                name_field.setBounds(Main.ROOM, Main.ROOM + number * 30, 200, 30);
                edit_button.setBounds(Main.ROOM + 200, Main.ROOM + number * 30, 100, 30);
                delete_button.setBounds(Main.ROOM + 300, Main.ROOM + number * 30, 100, 30);
    
                panel.getContentPane().add(name_field);
                panel.getContentPane().add(edit_button);
                panel.getContentPane().add(delete_button);

            }
        }

    }
}
