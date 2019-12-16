package com.Pauliaus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    private static HashMap<String, HashSet<Assay>> readFile(HashSet<Assay> assayList) {
        File plasmidFile = new File("Plasmids.txt");
        String parsedLine;
        HashMap<String, HashSet<Assay>> plasmidList = new HashMap<>();
        String plasmidName = "";
        HashSet<Assay> plasmidAssayList = new HashSet<>();
        Assay a;

        try {
            Scanner reader = new Scanner(plasmidFile);
            while (reader.hasNextLine()) {
                parsedLine = reader.nextLine();
                if (parsedLine.charAt(0) == '>') {
                    plasmidName = parsedLine.substring(1);
                } else if (!parsedLine.equals("###")) {
                    a = getAssay(assayList, parsedLine);
                    plasmidAssayList.add(a);
                } else {
                    plasmidList.put(plasmidName, new HashSet<>(plasmidAssayList));
                    plasmidName = "";
                    plasmidAssayList.clear();
                }
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.println("Error - Plasmid file not found!");
        } catch (StringIndexOutOfBoundsException f) {
            //f.printStackTrace();
            System.out.println("Error - you probably left a space in the file!");
        }
        return plasmidList;
    }

    private static HashSet<Assay> inputSequence(HashSet<Assay> assayList) {
        Scanner scanner = new Scanner(System.in);
        HashSet<Assay> input = new HashSet<>(0);
        String nextLine;
        Assay a;
        while (scanner.hasNextLine()) {
            nextLine = scanner.nextLine();
            if (nextLine.equals("")) {
                break;
            } else {
                a = getAssay(assayList, nextLine);
            }
            input.add(a);
        }
        return input;
    }

    private static Assay getAssay(HashSet<Assay> assayList, String nextLine) {
        Assay a = new Assay(nextLine);
        for (Assay assay : assayList) {
            if (nextLine.equals(assay.getAssayName()) || nextLine.equals(assay.getAssayID1()) || nextLine.equals(assay.getAssayID2()) || nextLine.equals(assay.getAssayID3()) || nextLine.equals(assay.getAssayID4())) {
                a = new Assay(assay.getAssayID1());
            }
        }
        return a;
    }

    private static String bestMatch(HashSet<Assay> input, HashMap<String, HashSet<Assay>> plasmidList) {
        HashSet<Assay> workingInput;
        int assaysCovered;
        int overloadedAssays;
        int topCoverage = 0;
        int bestOverload = 1000;
        String bestMatch = "default";

        for (Map.Entry<String, HashSet<Assay>> entry : plasmidList.entrySet()) {
            workingInput = new HashSet<>(input);
            workingInput.removeAll(entry.getValue());
            assaysCovered = input.size() - workingInput.size();
            overloadedAssays = entry.getValue().size() - assaysCovered;
            if (assaysCovered > topCoverage && overloadedAssays < bestOverload) {
                bestMatch = entry.getKey();
                topCoverage = assaysCovered;
                bestOverload = overloadedAssays;
            }
        }
        return bestMatch;
    }

    private static HashSet<Assay> parseInput(HashSet<Assay> input, HashMap<String, HashSet<Assay>> plasmidList) {
        HashSet<Assay> parsedInput = new HashSet<>(input);
        parsedInput.removeAll(plasmidList.get(bestMatch(input, plasmidList)));
        return parsedInput;
    }

    public static void main(String[] args) {
        // write your code here
        System.out.println("+---------------------------------------------------------+");
        System.out.println("|    Custom DNA Control Template Creator, version 1.01    |");
        System.out.println("+---------------------------------------------------------+");

        long startLoadTime = System.currentTimeMillis();
        HashSet<Assay> assayList = new Assay("").loadData();
        long endLoadTime = System.currentTimeMillis();
        System.out.println("Loaded " + assayList.size() + " assays in " + (endLoadTime - startLoadTime) + " milliseconds");

        long startReadTime = System.currentTimeMillis();
        HashMap<String, HashSet<Assay>> plasmidList = readFile(assayList);
        long endReadTime = System.currentTimeMillis();
        System.out.println("Loaded " + plasmidList.size() + " plasmids in " + (endReadTime - startReadTime) + " milliseconds");

        Scanner scanner = new Scanner(System.in);
        int selection;
        HashSet<Assay> input = new HashSet<>(0);
        boolean running = true;
        while (running) {
            System.out.println("\nHow would you like to proceed with the program?");
            System.out.println("1 - paste in a list of assays (required before running the Template Creator, can be used to paste in a new list)");
            System.out.println("2 - run the Template Creator with the current assay list");
            System.out.println("3 - reload the Plasmids.txt file");
            System.out.println("4 - print the loaded Plasmids");
            System.out.println("0 - exit the program");


            while (!scanner.hasNextInt()) {
                String string = scanner.next();
                System.out.printf("\"%s\" is not a valid selection.\n", string);
            }
            selection = scanner.nextInt();


            switch (selection) {
                case 1:
                    System.out.println("Please paste in the assay list:");
                    input = inputSequence(assayList);
                    System.out.println("Input " + input.size() + " assays");
                    break;
                case 2:
                    if (input.isEmpty()) {
                        System.out.println("Please put in the assay list first!");
                    } else {
                        long startTime = System.currentTimeMillis();
                        double inputLength = input.size();
                        HashSet<Assay> processingInput = new HashSet<>(input);
                        Count<Assay, Integer> count = new Count<>();

                        System.out.println("Custom Control composition:");
                        while (!processingInput.equals(parseInput(processingInput, plasmidList)) && !processingInput.isEmpty()) {
                            System.out.printf("%-28s || %5.2f %2s Coverage || %d unused assays%n", bestMatch(processingInput, plasmidList), (inputLength - (double) parseInput(processingInput, plasmidList).size()) / inputLength * 100, "%", plasmidList.get(bestMatch(processingInput, plasmidList)).size() - (processingInput.size() - parseInput(processingInput, plasmidList).size()));
                            for (Assay a : plasmidList.get(bestMatch(processingInput, plasmidList))) {
                                count.add(a);
                            }
                            processingInput = new HashSet<>(parseInput(processingInput, plasmidList));
                        }
                        //Prints what is left in the input Set - assays that are left uncovered.
                        if (!processingInput.isEmpty()) {
                            System.out.print("Not covered assays: ");
                            for (Assay assay : processingInput)
                            System.out.print(assay.getAssayID1() + "; ");
                        }
                        System.out.println("\n-------------------------------------------");
                        //Prints assays covered more than one times
                        System.out.println("Assays covered more than once:");
                        int duplexAssays = 0;
                        for (Assay a : count.keySet()) {
                            if (count.get(a) > 1 && input.contains(a)) {
                                System.out.print(a.getAssayID1() + ", " + count.get(a) + " times; ");
                                duplexAssays++;
                                if (duplexAssays % 2 == 0) {
                                    System.out.println();
                                }
                            }
                        }
                        if (duplexAssays == 0) {
                            System.out.println("None\n");
                        }
                        long endTime = System.currentTimeMillis();
                        System.out.println("\nProcess finished in " + (endTime - startTime) + " milliseconds");
                    }
                    break;
                case 3:
                    startReadTime = System.currentTimeMillis();
                    plasmidList = readFile(assayList);
                    endReadTime = System.currentTimeMillis();
                    //System.out.println("This is Custom DNA Control Template Creator, version 1.0");
                    System.out.println("Loaded " + plasmidList.size() + " plasmids in " + (endReadTime - startReadTime) + " milliseconds");
                    break;
                case 4:
                    System.out.println("Loaded Plasmid list: ");
                    for (Map.Entry<String, HashSet<Assay>> entry : plasmidList.entrySet()) {
                        if (!entry.getKey().equals("default")) {
                            System.out.printf("%-26s | %d assays", entry.getKey(), entry.getValue().size());
//                        for (Assay assay : entry.getValue()) {
//                            System.out.print(assay.getAssayID1() + "; ");
//                        }
                            System.out.print("\n");
                        }
                    }
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Input unrecognised, please put in a valid number");
            }
        }
    }

    static class Count<K, V> extends HashMap<K, V> {

        // Counts unique objects
        void add(K o) {
            int count;
            if (this.containsKey(o)) count = (Integer) this.get(o) + 1;
            else count = 1;
            super.put(o, (V) Integer.valueOf(count));
        }
    }
}
