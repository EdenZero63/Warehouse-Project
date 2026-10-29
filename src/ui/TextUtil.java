package ui;


//small text helpers for boxes, bars and fixed-width columns; plain ASCII only
public class TextUtil{

    //cuts or fills with spaces so the result is exactly `width` chars
    public static String pad(String text, int width){
        if(text.length() > width){
            return text.substring(0, width);
        }
        return text + repeat(' ', width - text.length());
    }

    //the same char n times, "" when n <= 0
    public static String repeat(char ch, int n){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++){
            sb.append(ch);
        }
        return sb.toString();
    }

    //+------+ that is `width` chars wide, or +======+ with '='
    public static String rule(char ch, int width){
        return "+" + repeat(ch, width - 2) + "+";
    }

    //| text   | that is `width` chars wide
    public static String boxed(String text, int width){
        return "| " + pad(text, width - 4) + " |";
    }

    //[######----] with `blocks` blocks, value on a 0..max scale
    public static String bar(int value, int max, int blocks){
        int filled = value * blocks / max;
        if(filled < 0){
            filled = 0;
        }
        if(filled > blocks){
            filled = blocks;
        }
        return "[" + repeat('#', filled) + repeat('-', blocks - filled) + "]";
    }

    //two blocks of lines next to each other, left one padded to its widest line
    public static String sideBySide(String left, String right, int gap){
        String[] l = left.split("\n");
        String[] r = right.split("\n");
        int leftWidth = 0;
        for(int i = 0; i < l.length; i++){
            leftWidth = Math.max(leftWidth, l[i].length());
        }
        StringBuilder sb = new StringBuilder();
        int lines = Math.max(l.length, r.length);
        for(int i = 0; i < lines; i++){
            String a = i < l.length ? l[i] : "";
            String b = i < r.length ? r[i] : "";
            sb.append(pad(a, leftWidth)).append(repeat(' ', gap)).append(b).append("\n");
        }
        return sb.toString();
    }

    //width of the widest line
    public static int widestLine(String text){
        String[] lines = text.split("\n");
        int widest = 0;
        for(int i = 0; i < lines.length; i++){
            widest = Math.max(widest, lines[i].length());
        }
        return widest;
    }
}
