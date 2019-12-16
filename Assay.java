package com.Pauliaus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class Assay {
    private String AssayName;
    private String AssayID1;
    private String AssayID2;
    private String AssayID3;
    private String AssayID4;

    public Assay(String assayID1) {
        AssayID1 = assayID1;
    }

    public String getAssayName() {
        return AssayName;
    }

    private void setAssayName(String assayName) {
        AssayName = assayName;
    }

    String getAssayID1() {
        return AssayID1;
    }

    public void setAssayID1(String assayID1) {
        AssayID1 = assayID1;
    }

    String getAssayID2() {
        return AssayID2;
    }

    private void setAssayID2(String assayID2) {
        AssayID2 = assayID2;
    }

    String getAssayID3() {
        return AssayID3;
    }

    private void setAssayID3(String assayID3) {
        AssayID3 = assayID3;
    }

    String getAssayID4() {
        return AssayID4;
    }

    private void setAssayID4(String assayID4) {
        AssayID4 = assayID4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assay)) return false;

        Assay assay = (Assay) o;

        return getAssayID1().equals(assay.getAssayID1());
    }

    @Override
    public int hashCode() {
        return getAssayID1().hashCode();
    }

    HashSet<Assay> loadData() {
        File assayDatabase = new File("AssayNames.csv");
        HashSet<Assay> assayList = new HashSet<>(0);
        try {
            Scanner reader = new Scanner(assayDatabase);
            reader.nextLine();
            String[] nextLine;
            while (reader.hasNextLine()) {
                nextLine = reader.nextLine().split(",", -1);
                Assay assay = new Assay(nextLine[1]);
                if (!nextLine[0].equals("")) {
                    assay.setAssayName(nextLine[0]);
                }
                if (!nextLine[2].equals("")) {
                    assay.setAssayID2(nextLine[2]);
                }
                if (!nextLine[3].equals("")) {
                    assay.setAssayID3(nextLine[3]);
                }
                if (!nextLine[4].equals("")) {
                    assay.setAssayID4(nextLine[4]);
                }
                assayList.add(assay);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }return assayList;


    }
}
