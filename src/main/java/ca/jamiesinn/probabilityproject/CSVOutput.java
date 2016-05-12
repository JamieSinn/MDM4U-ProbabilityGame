package ca.jamiesinn.probabilityproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVOutput
{
    String fileName = "output.csv";
    public CSVOutput()
    {

    }

    public CSVOutput(String fileName)
    {
        this.fileName = fileName;
    }

    public boolean writeToCSV(double guess, Choice choice, boolean win, double value) throws IOException
    {
        String header = "Guess,Choice,Win,ValueStoppedAt";
        String line = guess + "," + choice.toString() + "," + win + "," + value;
        File file = new File(fileName);
        if(!file.exists())
            file.createNewFile();
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, true)))
        {
            bw.append(line);
            bw.newLine();
        }
        return win;
    }
}
