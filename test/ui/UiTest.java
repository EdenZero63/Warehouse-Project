package ui;

import java.util.Scanner;

import exception.InvalidCommandException;
import testutil.Check;

//owner: Role 7 (console UI)
//tests for TextUtil, StatusPanel.batteryBar and InputHelper
public class UiTest{
    public static void run(){
        //fixed widths
        Check.equal("ab  ", TextUtil.pad("ab", 4), "pad fills with spaces");
        Check.equal("abcd", TextUtil.pad("abcdef", 4), "pad cuts long text");
        Check.equal(10, TextUtil.boxed("hi", 10).length(), "boxed line is exactly the width");
        Check.equal(10, TextUtil.rule('-', 10).length(), "rule is exactly the width");

        //battery bar on a 10-block scale
        StatusPanel panel = new StatusPanel();
        Check.equal("[##########]", panel.batteryBar(100), "full battery bar");
        Check.equal("[#####-----]", panel.batteryBar(55), "55% shows 5 blocks");
        Check.equal("[----------]", panel.batteryBar(0), "empty battery bar");
        Check.equal("[----------]", panel.batteryBar(-5), "negative level doesn't break the bar");

        //side by side keeps the right column lined up
        String joined = TextUtil.sideBySide("a\nlonger", "x\ny", 1);
        Check.equal("a      x\nlonger y\n", joined, "sideBySide pads the left column");

        //colors: off by default, and the codes never mess up the widths
        Check.isTrue(!Colors.isOn(), "colors start off");
        Check.equal("R1", Colors.paint("R1", Colors.BLUE), "paint does nothing when colors are off");
        Colors.setOn(true);
        String blue = Colors.paint("R1", Colors.BLUE);
        Check.isTrue(!blue.equals("R1"), "paint adds codes when colors are on");
        Check.equal(2, Colors.visibleLength(blue), "color codes take up no space");
        Check.equal(4, Colors.visibleLength(TextUtil.pad(blue, 4)), "pad counts only what you can see");
        Check.equal("R1", Colors.strip(blue), "strip takes the codes back out");
        Check.equal(Colors.RED, Colors.batteryColor(10), "low battery is red");
        Colors.setOn(false);

        //input
        InputHelper input = new InputHelper(new Scanner("  hello  \n7\nabc\n"));
        Check.equal("hello", input.readLine(""), "readLine trims");
        try{
            Check.equal(7, input.readInt("", 1, 10), "readInt reads a number");
        }catch(InvalidCommandException e){
            Check.isTrue(false, "readInt reads a number");
        }
        boolean threw = false;
        try{
            input.readInt("", 1, 10);
        }catch(InvalidCommandException e){
            threw = true;
        }
        Check.isTrue(threw, "readInt rejects text");
        threw = false;
        try{
            input.parseInt("11", 1, 10);
        }catch(InvalidCommandException e){
            threw = true;
        }
        Check.isTrue(threw, "parseInt rejects out of range");
        Check.equal("q", input.readLine(""), "readLine returns q when input runs out");
    }
}
