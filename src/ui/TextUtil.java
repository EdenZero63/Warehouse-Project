package ui;


//little helpers for boxes, bars and columns, plain ASCII only
public class TextUtil{

    //cuts it or adds spaces so it's exactly `width` long
    public static String pad(String text, int width){
        if(text.length() > width){
            return text.substring(0, width);
        }
        return text + repeat(' ', width - text.length());
    }

    //the same char n times, "" if n is 0 or less
    public static String repeat(char ch, int n){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++){
            sb.append(ch);
        }
        return sb.toString();
    }

    //a +------+ line that's `width` long, or +======+ if you pass '='
    public static String rule(char ch, int width){
        return "+" + repeat(ch, width - 2) + "+";
    }

    //a | text   | line that's `width` long
    public static String boxed(String text, int width){
        return "| " + pad(text, width - 4) + " |";
    }

    //a bar like [######----], `blocks` is how many blocks total, value goes from 0 to max
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

    //puts two blocks of text side by side, the left one gets padded so the right one lines up
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

    //how long the longest line is
    public static int widestLine(String text){
        String[] lines = text.split("\n");
        int widest = 0;
        for(int i = 0; i < lines.length; i++){
            widest = Math.max(widest, lines[i].length());
        }
        return widest;
    }
}
