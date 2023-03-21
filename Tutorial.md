# i figured i should tell people how to use this garbage thing

## First, some words
This thing is not meant to make a fully functioning app. i don't have the patience to code anything that can make something that phosisticated this is only meant to make the more tedious parts of making a GUI (guessing dimensions and sizes) a little easier by making a GUI to make GUIs. I might add support for more different types of components (like textboxes), or even variables, but not anything crazy  
to me, the trouble of writing code to do those in a GUI heavily outweighs just making it normally by typing

now that that's out of the way

## How to use this dumb thing!

1. Select `Start`
2. Choose a directory to save to
3. Enter your app's title and margins

the title will be shown on the top of the window


the margins will be an area around the edge of the window  
that is inaccessible by any component, making things look nicerish

### you are now in the main menu of the maker

### Click `save` to save your current menus into a .java file

## Some important things
when you click `edit` on a menu, another window will show up  
this is the **preview** window, and it lets you visualize the menu you're currently making

the preview window is resizable, so you can either define the menu size with the input fields on the panel window, or simply resizing the preview window  
*note: it will only save your size settings when you click `confirm`, **regardless of which method of defining the size you use***

all buttons in the preview window **actually function** and will link to the menus you set it to

it is required that exactly **one** menu be called `main`, and it will be the one that is first shown when the app is opened  
(aka, be invoked in the `main(String[] args)` method)

There is a preset available: `changelog`, and you access it by naming a menu `changelog`. exactly **one** menu is allowed to be the `changelog` menu  
if a `changelog` menu is present, the program will automatically create a `static final String[] CHANGELOG` and a `static final String VERSION` in the output for you to put stuff in

## how this POS structures apps
GarbageAppMaker makes an app with a list of `menus`, and each menu has its own set of `components` 

currently, a component can be:
* a JLabel (a text box)
    * can display text
* a JButton (a button)
    * can display text
    * can link to other menus

and the structure would be something like: 
* menu 1
    * JLabel 1
        * text "hello there"
    * JButton 1
        * text "click me"
        * onclick goto menu 2
* menu 2
    * JButton 1
        * text "back"
        * onclick goto menu 1