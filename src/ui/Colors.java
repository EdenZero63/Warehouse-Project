package ui;

import robot.Robot;
import robot.StrandedState;

//colors for the console using ANSI codes, they're OFF by default
//some consoles (old cmd, BlueJ) print junk like "←[32m" instead of colors, so you turn them on with k
public class Colors{
    //the codes, bright versions so they show up on a dark background
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[91m";
    public static final String GREEN = "\u001B[92m";
    public static final String YELLOW = "\u001B[93m";
    public static final String BLUE = "\u001B[94m";
    public static final String MAGENTA = "\u001B[95m";
    public static final String CYAN = "\u001B[96m";
    public static final String GRAY = "\u001B[90m";

    //one color per robot: R1 blue, R2 magenta, R3 yellow, then it starts over
    private static final String[] ROBOT_COLORS = {BLUE, MAGENTA, YELLOW};

    //fields
    private static boolean on = false;

    //getters and setters
    public static boolean isOn(){
        return on;
    }

    public static void setOn(boolean value){
        on = value;
    }

    //wraps text in a color, or gives it back plain when colors are off
    public static String paint(String text, String color){
        if(!on){
            return text;
        }
        return color + text + RESET;
    }

    //red if it's stranded, otherwise its own color from the list
    public static String robotColor(Robot robot){
        if(robot.getState() instanceof StrandedState){
            return RED;
        }
        int number = 1;
        try{
            number = Integer.parseInt(robot.getId().substring(1)); //"R2" -> 2
        }catch(NumberFormatException e){
            //an id without a number just gets the first color
        }
        return ROBOT_COLORS[(number - 1 + ROBOT_COLORS.length) % ROBOT_COLORS.length];
    }

    //green when it's fine, yellow when it's getting low, red when it's almost empty
    public static String batteryColor(int level){
        if(level > 50){
            return GREEN;
        }
        if(level >= 20){
            return YELLOW;
        }
        return RED;
    }

    //how long the text looks on screen, the color codes take up no space so we skip them
    public static int visibleLength(String text){
        return strip(text).length();
    }

    //the text with all the color codes taken out
    public static String strip(String text){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while(i < text.length()){
            char ch = text.charAt(i);
            if(ch == '\u001B'){
                //skip everything up to and including the 'm' at the end of the code
                while(i < text.length() && text.charAt(i) != 'm'){
                    i++;
                }
            }else{
                sb.append(ch);
            }
            i++;
        }
        return sb.toString();
    }
}
