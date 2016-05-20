package ca.jamiesinn.probabilityproject.test;

import ca.jamiesinn.probabilityproject.CSVOutput;
import ca.jamiesinn.probabilityproject.Choice;
import ca.jamiesinn.probabilityproject.Main;
import org.junit.Assert;
import org.junit.Test;

public class MainTest
{
    @Test
    public void runTheoreticalOutput() throws Exception
    {
        CSVOutput.clearFile("TESTING.csv");
        for (double i = -1; i <= 1; i += 0.01)
        {
            double stopAt = 0.25;
            runTheoreticalOutput(stopAt, i, Choice.ABOVE);
            runTheoreticalOutput(stopAt, i, Choice.BELOW);
        }
    }

    private void runTheoreticalOutput(double stopAt, double choice, Choice direction) throws Exception
    {
        String[] args = {choice + "", direction.toString(), stopAt + ""};
        Main.main(args);
    }

    @Test
    public void sanityCheck() throws Exception
    {
        runTheoreticalOutput(Main.toDeg(3.00409561512567), 0.25, Choice.ABOVE);
        System.out.println("Sanity Check: True: - " + Main.TEST_RESULT);
        Assert.assertTrue(Main.TEST_RESULT);
        runTheoreticalOutput(Main.toDeg(3.00409561512567), 0.25, Choice.BELOW);
        System.out.println("Sanity Check: False: - " + Main.TEST_RESULT);
        Assert.assertFalse(Main.TEST_RESULT);

    }
}
