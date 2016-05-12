package ca.jamiesinn.probabilityproject.test;

import ca.jamiesinn.probabilityproject.Choice;
import ca.jamiesinn.probabilityproject.Main;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

public class MainTest
{
    @Test
    public void runTheoreticalOutput() throws Exception
    {
        for (int i = 0; i <= 1000; i++)
        {
            double stopAt = ThreadLocalRandom.current().nextDouble(0, 360);
            double choice = ThreadLocalRandom.current().nextDouble(-1, 1);
            Choice direction = Choice.valueOf(
                    (ThreadLocalRandom.current().nextInt(0, 2) == 0 ? "ABOVE" : "BELOW"));
            runTheoreticalOutput(stopAt, choice, direction);
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
        Assert.assertFalse(Main.TEST_RESULT);
    }
}
