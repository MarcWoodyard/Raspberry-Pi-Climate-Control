package com.marcwoodyard.RaspberryPiThermostat.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileHandler {

    public static void stringToFile(File file, String data) {
        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ArrayListToFile(File file, ArrayList<String> data) {
        stringToFile(file, String.join("\n", data));
    }

    public String fileToString(File file) {
        StringBuilder result = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String b;

            while ((b = br.readLine()) != null)
                result.append(b);

            br.close();
        } catch (FileNotFoundException f) {
            System.out.println("[ERROR] " + file.getName() + " not found.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    // TODO Simplify this method & make it work.
    public ArrayList<String> fileToArrayList(File file) {
        //return new ArrayList<>(Arrays.asList(this.fileToString(file).split("\\r?\\n"))); // This wont work on its own.

        ArrayList<String> data = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String b;

            while ((b = br.readLine()) != null)
                data.add(b);

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }


}



