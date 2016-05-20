package ca.jamiesinn.probabilityproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVOutput
{
    private static String fileName = "output.csv";
    private boolean firstLine = true;

    CSVOutput()
    {

    }

    public CSVOutput(String fileName)
    {
        CSVOutput.fileName = fileName;
    }

    public boolean writeToCSV(double guess, Choice choice, boolean win, double value) throws IOException
    {
        return writeToCSV(guess, choice, win, value, -1);
    }

    public boolean writeToCSV(double guess, Choice choice, boolean win, double value, double probability) throws IOException
    {
        String line = guess + "," + choice.toString() + "," + win + "," + value;
        if (probability != -1)
        {
            line += "," + probability;
        }
        File file = new File(fileName);
        if (!file.exists())
            file.createNewFile();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true)))
        {
            bw.append(line);
            bw.newLine();
        }
        return win;
    }

    public static void clearFile(String... fileName)
    {
        File file;
        if (fileName != null)
        {
            file = new File(fileName[0]);

        }
        else
            file = new File(CSVOutput.fileName);
        if (file.exists()) file.delete();
    }
}
