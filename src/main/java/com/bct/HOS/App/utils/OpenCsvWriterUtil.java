package com.bct.HOS.App.utils;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.sql.ResultSet;

public class OpenCsvWriterUtil {

    public static boolean convertRSToCSV(ResultSet rs, String filePath) {
        try {
            //DatabaseConnection dbcon=new DatabaseConnection();
        	filePath = filePath + "/" + "almfs104.csv";
        	System.out.println("TEST LOG | The CSV path is "+filePath);
            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            //String[] header="FIRSTNAME,LASTNAME,AGE".split(",");
            //writer.writeNext(header);
            //dbcon.CreateConnection();
            //ResultSet rs=dbcon.getData();
            writer.writeAll(rs, true); //And the second argument is boolean which represents whether you want to write header columns (table column names) to file or not.
            writer.close();
            //dbcon.closeConnections();
            System.out.println("CSV file created succesfully");
            return true;
        } catch (Exception e) {
            System.out.println("exception :" + e.getMessage());
            return false;
        }
    }
}