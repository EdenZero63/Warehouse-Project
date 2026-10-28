package ui;

import java.util.Scanner;

import sim.Simulation;

//owner: Role 7 (console UI), wiring with Role 1
//console menu: draw the screen, read one command, call Simulation, repeat
//no game rules in here; catch the domain exceptions and print a friendly message
//
//planned commands:
//  Enter   next tick           n <k>   run k ticks
//  a       add a task          i <n>   inspect robot n
//  c       service a stranded robot
//  r       reset (asks to confirm)     q       quit
public class Menu{
    //fields
    private final Simulation sim;
    private final InputHelper input;
    private final GridRenderer gridRenderer;
    private final StatusPanel statusPanel;

    //constructor
    public Menu(Simulation sim){
        this.sim = sim;
        this.input = new InputHelper(new Scanner(System.in));
        this.gridRenderer = new GridRenderer();
        this.statusPanel = new StatusPanel();
    }

    //main loop
    public void run(){
        //TODO (Role 7): sim.reset(), then loop until "q"; catch InvalidCommandException,
        //InvalidTaskException, UnreachableDestinationException and RobotUnavailableException
        //so the program never crashes
        System.out.println("Warehouse Robot Fleet: skeleton only, nothing is implemented yet.");
    }
}
