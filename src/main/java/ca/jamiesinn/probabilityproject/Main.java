package ca.jamiesinn.probabilityproject;

import com.wolfram.alpha.*;

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
            System.out.println("Please press enter to stop cursor on the sine curve.");
            while (System.in.available() == 0)
            {
                if (count >= 360)
                    reverse = true;
                if (count <= 0)
                    reverse = false;
                if (reverse)
                    count -= 0.001;
                else
                    count += 0.001;
            }
            System.out.println("Value: " + toRad(count));
            System.out.println("Win: " + csv.writeToCSV(choice, direction, getResult(points, direction, toRad(count), choice), toRad(count)));

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
            if (from >= result && result <= to)
                return sin(result) > guess && choice.equals(Choice.ABOVE);
            else
                return sin(result) < guess && choice.equals(Choice.BELOW);
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


    private static double getCurveLength(double start, double end)
    {
        double result = 0;
        String squery = "int sqrt(1+(cos(x))^2) from x=" + toRad(start) + " to x=" + toRad(end);
        WAEngine engine = new WAEngine();
        engine.setAppID(appid);
        engine.addFormat("plaintext");

        WAQuery query = engine.createQuery();
        query.setInput(squery);

        try
        {
            if (DEBUG)
            {
                // For educational purposes, print out the URL we are about to send:
                System.out.println("Query URL:");
                System.out.println(engine.toURL(query));
                System.out.println("");

            }
            // This sends the URL to the Wolfram|Alpha server, gets the XML result
            // and parses it into an object hierarchy held by the WAQueryResult object.
            WAQueryResult queryResult = engine.performQuery(query);

            if (queryResult.isError())
            {
                System.out.println("Query error");
                System.out.println("  error code: " + queryResult.getErrorCode());
                System.out.println("  error message: " + queryResult.getErrorMessage());
            }
            else if (!queryResult.isSuccess())
            {
                System.out.println("Query was not understood; no results available.");
            }
            else
            {
                // Got a result.
                if (DEBUG)
                    System.out.println("Successful query. Pods follow:\n");
                for (WAPod pod : queryResult.getPods())
                    if (!pod.isError())
                    {
                        if (!pod.getTitle().equals("Definite integral")) continue;
                        if (DEBUG)
                        {
                            System.out.println(pod.getTitle());
                            System.out.println("------------");
                        }
                        for (WASubpod subpod : pod.getSubpods())
                            for (Object element : subpod.getContents())
                                if (element instanceof WAPlainText)
                                {
                                    String resultS = ((WAPlainText) element).getText();
                                    String finalNum = "";
                                    boolean resultStart = false;
                                    for (int i = 0; i < resultS.length(); i++)
                                    {
                                        //System.out.println(resultS.charAt(i));
                                        if (resultStart)
                                        {
                                            finalNum += Character.toString(resultS.charAt(i));
                                        }
                                        if (String.valueOf(resultS.charAt(i)).equals("\uF7D9"))
                                        {
                                            resultStart = true;
                                        }
                                    }

                                    result = Double.parseDouble(finalNum);
                                }
                    }
                // We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
                // These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
            }
        }
        catch (WAException e)
        {
            e.printStackTrace();
        }
        return result;
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
