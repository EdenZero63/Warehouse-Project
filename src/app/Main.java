package app;

import sim.Scenario;
import sim.Simulation;
import ui.Menu;

//owner: Role 1 (integration lead)
//entry point, only wires the pieces together; all logic lives in the other packages
public class Main{
    public static void main(String[] args){
        Simulation sim = new Simulation(Scenario.createDefault());
        Menu menu = new Menu(sim);
        menu.run();
    }
}
