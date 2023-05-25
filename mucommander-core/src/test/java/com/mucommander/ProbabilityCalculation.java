package com.mucommander;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProbabilityCalculation {
    public static void main(String[] args) throws IOException {
        // Prepare variables for calculation
        Map<String, Integer> sumCount = new HashMap<>();
        Map<String, Integer> executionCount = new HashMap<>();

        // Read the log file line by line and count the number of paths being executed
        File file = new File("mucommander-core/src/test/java/com/mucommander/2023-05-25.log");
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()){
            // Extract the last word from a line
            String line = scanner.nextLine();
            String[] splitLine = line.split(" ");
            String pathName = splitLine[splitLine.length - 1];
            // The last word is a valid execution log only if it's formatted as 'SX->SX'
            if (pathName.length() == 6 && pathName.charAt(0) == 'S' && pathName.charAt(4) == 'S') {
                // Update the number of paths with the same output node
                String outputNode = pathName.substring(0, 2);
                if (sumCount.containsKey(outputNode))
                    sumCount.put(outputNode, sumCount.get(outputNode) + 1);
                else
                    sumCount.put(outputNode, 1);
                // Update the number of execution of a path
                if (executionCount.containsKey(pathName))
                    executionCount.put(pathName, executionCount.get(pathName) + 1);
                else
                    executionCount.put(pathName, 1);
            }
        }
        scanner.close();

        // Calculate the probability of each path
        for (Map.Entry<String, Integer> entry : executionCount.entrySet()) {
            float probability = (float) entry.getValue() / sumCount.get(entry.getKey().substring(0, 2));
            System.out.printf("Probability of %s: %.4f%n", entry.getKey(), probability);
        }
    }
}
