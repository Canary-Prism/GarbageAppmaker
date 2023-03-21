package garbageappmaker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Writer {

    File destination;
    String title;
    int ROOM;
    ArrayList<Maker.Menu> menus;
    boolean existsChangelog;

    /**
     * key:
     * n: nothing
     * c: in a class
     * m: in a method
     * l: in a lambda
     * o: in a constructor
     * f: in a for loop
     */
    char mode = 'n';

    FileWriter fr;

    int indentation = 0;

    String classname;

    public Writer(File destination, String title, int ROOM, ArrayList<Maker.Menu> menus, boolean existsChangelog) {
        this.destination = destination;
        this.title = title;
        this.ROOM = ROOM;
        this.menus = menus;
        this.existsChangelog = existsChangelog;
    }
    
    public void write() {
        try {
            if (!destination.exists())
                destination.createNewFile();
                
            fr = new FileWriter(destination);
            this
            .startClass("Main");

            if (existsChangelog) 
                this
                .field(true, "static String[]", "CHANGELOG", "{}")
                .field(true, "static String", "VERSION", "null")
                .lineBreak();
            
            this
                .field("JFrame", "frame", "new JFrame(\"" + title + "\")")
                .field(true, "static int", "ROOM", String.valueOf(ROOM))
                .lineBreak()
                .field("JLabel", "dud", "new JLabel()")
                .lineBreak()
                .psvm()
                    .codeLine("new Main().mainMenu(null)")
                .endMethod()
                .lineBreak()
                .startConstructor()
                    .codeLine("frame.setResizable(false)")
                    .codeLine("frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)");
            for (Maker.Menu menu : menus) {
                if (menu.getName().equals("changelog")) {
                    this
                    .lineBreak(2)
                    .codeLine("String temp = \"<html><h1>Changelog</h1>\" + Main.VERSION")
                    .startForLoop("int i = 0", "i < Main.CHANGELOG.length", "i++")
                        .codeLine("temp += \"<br />•\" + Main.CHANGELOG[i]")
                    .endForLoop()
                    .codeLine("temp += \"</html>\"")
                    .lineBreak()
                    .codeLine("changelog_label.setText(temp)")
                    .codeLine("changelog_label.setVerticalAlignment(SwingConstants.TOP)")
                    .lineBreak()
                    .codeLine("changelog_back_button.addActionListener((e) -> {frame.setResizable(false);mainMenu(e);})")
                    .lineBreak(2);
                    continue;
                }
                for (Maker.Menu.Component<? extends JComponent> component : menu.getComponents()) {
                    this
                    .lineBreak()
                    .codeLine(menu.getName() + "_" + component.getName() + ".setBounds(Main.ROOM + "
                        + component.posx + ", Main.ROOM + " + component.posy + ", " + component.sizex + ", " + component.sizey + ")"
                    );

                    if (component.component instanceof JLabel) {
                        this
                    .codeLine(menu.getName() + "_" + component.getName() + ".setVerticalAlignment(SwingConstants.TOP)");
                    } else if (component.component instanceof JButton) {
                        if (component.getReferenceMenu() != null)
                            this
                    .codeLine(menu.getName() + "_" + component.getName() + ".addActionListener(this::" + component.getReferenceMenu().getName() + "Menu)");
                    }
                }
            }
            this
                    .codeLine("frame.setVisible(true)")
                .endConstructor()
                .lineBreak();
            
            for (Maker.Menu menu : menus) {
                if (menu.getName().equals("changelog")) {
                    this
                .field("JLabel", "changelog_label", "new JLabel()")
                .field("JButton", "changelog_back_button", "new JButton(\"back\")")
                .lineBreak()
                .startMethod("changelogMenu", "ActionEvent e")
                    .codeLine("frame.getContentPane().removeAll()")
                    .lineBreak()
                    .codeLine("frame.getContentPane().add(changelog_back_button, BorderLayout.PAGE_END)")
                    .codeLine("frame.getContentPane().add(changelog_label)")
                    .lineBreak()
                    .codeLine("frame.setResizable(true)")
                    .codeLine("frame.setSize(400 + Main.ROOM * 2, Main.CHANGELOG.length * 30 + 140 + Main.ROOM * 2)")
                .endMethod()
                .lineBreak(2);
                    continue;
                }
                for (Maker.Menu.Component<? extends JComponent> component : menu.getComponents()) 
                    if (component.component instanceof JLabel) 
                        this
                .field("JLabel", menu.getName() + "_" + component.getName(), "new JLabel(\"" + component.getText() + "\")");
                    else if (component.component instanceof JButton) 
                        this
                .field("JButton", menu.getName() + "_" + component.getName(), "new JButton(\"" + component.getText() + "\")");
                    
                
                this
                .lineBreak()
                .startMethod(menu.getName() + "Menu", "ActionEvent e")
                    .codeLine("frame.getContentPane().removeAll()")
                    .lineBreak()
                    .bulkAddComponents(menu.getComponents().stream().map((component) -> menu.getName() + "_" + component.getName()).toList().toArray(new String[0]))
                    .lineBreak()
                    .codeLine("frame.getContentPane().add(dud)")
                    .lineBreak()
                    .codeLine("frame.setSize(" + ((int)menu.getSize().getWidth() - ROOM * 2) + " + Main.ROOM * 2, " + ((int)menu.getSize().getHeight() - ROOM * 2) + " + Main.ROOM * 2)")
                .endMethod()
                .lineBreak(2);
            }

            this
            .endClass(); 

            fr.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot Access File :c", "well shit", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        
    }

    private Writer bulkAddComponents(String[] component_names) throws IOException {
        for (String component_name : component_names) 
            codeLine("frame.getContentPane().add(" + component_name + ")");

        return this;
    }




    private Writer indent() throws IOException {
        for (int i = 0; i < indentation; i++)
            fr.write("    ");
        
        return this;
    }
    private void check(char mode, char newmode) {
        if (this.mode != mode) {
            throw new RuntimeException("Incorrectly Structured Class");
        }
        this.mode = newmode;
    }

    private Writer startClass(String name) throws IOException {
        check('n', 'c');
        indent();
        indentation++;
        fr.write("class " + name + " {\n");
        classname = name;

        return this;
    }

    private Writer endClass() throws IOException {
        check('c', 'n');
        indentation--;
        indent();
        fr.write("}\n");

        return this;
    }

    private Writer lineBreak() throws IOException {
        return lineBreak(1);
    }
    private Writer lineBreak(int lines) throws IOException {
        indent();
        for (int i = 0; i < lines; i++)
            fr.write("\n");
        
        return this;
    }

    @SuppressWarnings("unused")
    private Writer field(String type, String name) throws IOException {
        check('c', 'c');
        indent();
        fr.write("private " + type + " " + name + ";\n");

        return this;
    }
    private Writer field(String type, String name, String value) throws IOException {
        return field(false, type, name, value);
    }
    private Writer field(boolean isFinal, String type, String name, String value) throws IOException {
        check('c', 'c');
        indent();
        fr.write("private " + (isFinal? "final " : "") + type + " " + name + " = " + value + ";\n");

        return this;

    }

    private Writer psvm() throws IOException {
        check('c', 'm');
        indent();
        indentation++;
        fr.write("public static void main(String args[]) {\n");

        return this;
    }

    @SuppressWarnings("unused")
    private Writer startMethod(String name) throws IOException {
        check('c', 'm');
        indent();
        indentation++;
        fr.write("private void " + name + "() {\n");

        return this;

    }
    private Writer startMethod(String name, String... param) throws IOException {
        check('c', 'm');
        indent();
        indentation++;
        fr.write("private void " + name + "(" + param[0]);
        for (int i = 1; i < param.length; i++) 
            fr.write(", " + param);
        fr.write(") {\n");

        return this;
    }

    private Writer endMethod() throws IOException {
        check('m', 'c');
        indentation--;
        indent();
        fr.write("}\n");

        return this;
    }

    private Writer startConstructor() throws IOException {
        check('c', 'o');
        indent();
        indentation++;
        fr.write("public " + classname + "() {\n");

        return this;
    }

    private Writer endConstructor() throws IOException {
        check('o', 'c');
        indentation--;
        indent();
        fr.write("}\n");

        return this;
    }

    private char modestore;

    private Writer startForLoop(String initialize, String condition, String loopEnd) throws IOException {
        modestore = mode;
        try {
            check('m', 'f');
        } catch (RuntimeException e) {
            check('o', 'f');
        }
        indent();
        indentation++;
        fr.write("for (" + initialize + "; " + condition + "; " + loopEnd + ") {\n");
        
        return this;
    }

    private Writer endForLoop() throws IOException {
        check('f', 'f');
        mode = modestore;
        indentation--;
        indent();
        fr.write("}\n");

        return this;
    }

    private Writer codeLine(String line) throws IOException {
        
        try {
            check('o', 'o');
        } catch (RuntimeException e) {
            try {
                check('m', 'm');
            } catch (RuntimeException s) {
                check('f', 'f');
            }
        }

        indent();
        fr.write(line + ";\n");

        return this;
    }
}
