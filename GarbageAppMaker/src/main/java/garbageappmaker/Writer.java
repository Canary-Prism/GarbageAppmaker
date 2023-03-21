package garbageappmaker;

import java.io.File;
import java.util.ArrayList;

public class Writer {

    String title;
    int ROOM;
    ArrayList<Maker.Menu> menus;

    public Writer(File destination, String title, int ROOM, ArrayList<Maker.Menu> menus) {
        this.title = title;
        this.ROOM = ROOM;
        this.menus = menus;
    }
}
