package ca.jamiesinn.probabilityproject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import static java.lang.Math.sin;

public class Main
{
    private static final double M_PI = Math.PI;
    private static final boolean DEBUG = false;
    private static String appid = "U2JAQY-U7787X42QJ";
    public static boolean TEST_RESULT = false;


    public static void main(String[] args) throws Exception
    {
        CSVOutput csv = new CSVOutput();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        if (args.length == 0)
        {
            System.out.println("Please enter the selected choice from -1 to 1, and not 0: ");
            double choice = Double.parseDouble(reader.readLine());
            if (choice > 1 || choice < -1 || choice == 0)
                throw new Exception("Error: Invalid Y value.");
            System.out.println("Please enter the chosen direction: ");
            Choice direction = Choice.valueOf(reader.readLine().toUpperCase());
            HashMap<Double, Double> points = getIntersectionPoints(choice);
            double count = 0;
            boolean reverse = false;
            boolean firstloop = true;
            int iterations = 1;
            System.out.println("Please press enter to stop cursor on the sine curve.");
            while (System.in.available() == 0)
            {
                if (count >= 350)
                {
                    reverse = true;
                    iterations++;
                }
                if (count <= 0)
                    reverse = false;
                if (reverse)
                    count -= 0.01;
                else
                    count += 0.01;

                if(iterations == 1)
                    System.out.println(sin(toRad(count)));
            }
            System.out.println("Value: " + sin(toRad(count)));
            System.out.println("Win: " + csv.writeToCSV(choice, direction, getResult(points, direction, toRad(count), choice), toRad(count)));
            if(DEBUG) System.out.println("Needed to be within: " + getIntersectionPoints(choice).toString());
            if(DEBUG) System.out.println("Value is: " + sin(toRad(count)) + " which is: " + (direction == Choice.ABOVE && sin(toRad(count)) >= choice ? "above" : "below" ) + " your guess." );


        }
        else
        {
            CSVOutput csvTest = new CSVOutput("TESTING.csv");
            double choice = Double.parseDouble(args[0]);
            Choice direction = Choice.valueOf(args[1].toUpperCase());
            double stopAt = Double.parseDouble(args[2]);

            TEST_RESULT = csvTest.writeToCSV(choice,
                    direction,
                    getResult(getIntersectionPoints(choice), direction, toRad(stopAt), choice), toRad(stopAt));
            if (DEBUG) System.out.println("Win: " + TEST_RESULT);

        }

    }

    private static boolean getResult(HashMap<Double, Double> ranges, Choice choice, double result, double guess)
    {
        for (Double from : ranges.keySet())
        {
            double to = ranges.get(from);
            if (result >= from && result <= to)
                if(choice.equals(Choice.BELOW))
                    return sin(toRad(result)) >= guess;
                else if(choice.equals(Choice.ABOVE))
                    return sin(toRad(result)) <= guess;
        }
        return false;

    }

    private static double toRad(double deg)
    {
        return (deg * M_PI / 180.0);
    }

    public static double toDeg(double rad)
    {
        return (rad * 180 / M_PI);
    }

    /**
     * f(x) = sin(x) - y
     * Y is the player's guess, and is the horizontal line
     * Basically shifts the graph to 0,0, and checks if it's equal to it.
     */
    private static double newtonsMethod(double y)
    {
        double x = 0;
        double tolerance = .000000001;
        int max_count = 200;
        for (int count = 1;
             (Math.abs(f(x, y)) > tolerance) && (count < max_count);
             count++)
        {
            x = x - f(x, y) / fprime(x);
            if (DEBUG)
                System.out.println("Step: " + count + " x:" + x + " Value:" + f(x, y));
        }

        if (Math.abs(f(x, y)) <= tolerance)
        {
            if (DEBUG)
                System.out.println("Zero found at x=" + x);
            return x;

        }
        else
        {
            if (DEBUG)
                System.out.println("Failed to find a zero");
            return -1000;
        }
    }


    //TODO: Make this work with negative y values, probably have to modify
    private static HashMap<Double, Double> getIntersectionPoints(double y)
    {
        HashMap<Double, Double> result = new HashMap<>();
        double poi = newtonsMethod(y);
        double radX;
        boolean first = true;
        double[] xVals = new double[2];
        for (double x = poi; x <= 360; x += 0.001)
        {
            radX = toRad(x);
            if (almostEqual(f(radX, y), y, 0.000008))
            {
                if (almostEqual(poi, radX, 0.01) && !first) continue;
                if (DEBUG) System.out.println(poi);
                if (first)
                {
                    if (DEBUG) System.out.println("x1: " + radX);
                    xVals[0] = radX;
                    first = false;
                }
                else
                {
                    if (DEBUG) System.out.println("x2: " + radX);
                    xVals[1] = radX;
                }
            }
        }
        result.put(xVals[0], xVals[1]);
        return result;
    }


    /**
     * @param x     x value
     * @param guess y=guess
     * @return sin(x) - y
     */
    private static double f(double x, double guess)
    {
        return sin(x) + Math.abs(guess);
    }

    /**
     * Derivitive of sine is cos, derivative of a number is just 0.
     *
     * @param x x value
     * @return cos(x)
     */
    private static double fprime(double x)
    {
        return Math.cos(x);
    }

    public static boolean almostEqual(double a, double b, double eps)
    {
        return Math.abs(a - b) < eps;
    }
}
