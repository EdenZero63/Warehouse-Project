
package app;

import sim.Scenario;
import sim.Simulation;
import ui.Menu;

// owner: Role 1 (integration lead)
// Entry point for the Warehouse Robot Fleet Simulation.
// Initializes the scenario, starts the simulation, and launches the menu.
public class Main {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   WAREHOUSE ROBOT FLEET SIMULATION");
        System.out.println("========================================");
        System.out.println("Initializing warehouse system...");
        System.out.println();

        try {
            // Step 1: Create the default warehouse scenario
            Scenario scenario = Scenario.createDefault();

            // Step 2: Initialize the simulation
            Simulation simulation = new Simulation(scenario);

            // Step 3: Display startup information
            System.out.println("Warehouse scenario loaded successfully.");
            System.out.println("Simulation initialized.");
            System.out.println();

            // Step 4: Create the console menu
            Menu menu = new Menu(simulation);

            // Step 5: Start the application
            System.out.println("Starting console interface...");
            System.out.println();

            menu.run();

            // Step 6: Display shutdown message
            System.out.println();
            System.out.println("Warehouse simulation has been closed.");
            System.out.println("Thank you for using the system.");

        } catch (UnsupportedOperationException e) {
            System.err.println("Error: A required feature has not been implemented yet.");
            System.err.println("Details: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("An unexpected error occurred.");
            System.err.println("Details: " + e.getMessage());
        }
    }
}
