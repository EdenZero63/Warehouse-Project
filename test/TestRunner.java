import testutil.Check;

//owner: Role 8 (QA and errors)
//runs every test class and prints a summary; add each new test class here
public class TestRunner{
    public static void main(String[] args){
        //one line per test class
        warehouse.WarehouseTest.run();
        navigation.BfsPathFinderTest.run();
        charging.ChargingPolicyTest.run();
        robot.RobotTest.run();
        task.TaskManagerTest.run();
        sim.SimulationTest.run();
        ui.UiTest.run();

        //summary, exit code 1 if anything failed
        System.out.println(Check.getPassed() + " passed, " + Check.getFailed() + " failed");
        if(Check.getFailed() > 0){
            System.exit(1);
        }
    }
}
