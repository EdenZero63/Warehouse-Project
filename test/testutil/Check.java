package testutil;

//owner: Role 8 (QA and errors)
//tiny assertion helper so we don't need JUnit; TestRunner prints the totals
public class Check{
    //counters
    private static int passed = 0;
    private static int failed = 0;

    //passes if condition is true, otherwise prints FAIL with the test name
    public static void isTrue(boolean condition, String name){
        if(condition){
            passed++;
        }else{
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    //compare numbers, or any objects with equals
    public static void equal(int expected, int actual, String name){
        isTrue(expected == actual, name + " (expected " + expected + ", got " + actual + ")");
    }

    public static void equal(Object expected, Object actual, String name){
        boolean same = expected == null ? actual == null : expected.equals(actual);
        isTrue(same, name + " (expected " + expected + ", got " + actual + ")");
    }

    //getters
    public static int getPassed(){
        return passed;
    }

    public static int getFailed(){
        return failed;
    }
}
