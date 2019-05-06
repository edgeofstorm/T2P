import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import Dictionary.Pos;
import WordNet.*;
import zemberek.core.turkish.PrimaryPos;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.lexicon.DictionaryItem;
import zemberek.morphology.lexicon.RootLexicon;
import zemberek.morphology.lexicon.proto.LexiconProto;
import zemberek.morphology.morphotactics.Morpheme;
import zemberek.morphology.morphotactics.TurkishMorphotactics;
import zemberek.tokenization.TurkishSentenceExtractor;

import java.sql.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;


import org.apache.commons.lang3.*;

public class Linker {

    static String PICTO_FOLDER_PATH = "C:\\Users\\hakac\\T2P\\src\\main\\resources\\Pictograms";

    static public ArrayList<String> listAllFiles(String path) {
        ArrayList<String> fileNames = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        fileNames.add(filePath.getFileName().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    static public void linkerSynsetPictogram(ArrayList<String> fileNames) {
        int i, j, k;

        WordNet turkish = new WordNet();
        ArrayList<SynSet> SynSets;

        Connection conn = null;
        Statement stmt = null;

        int fileSize = fileNames.size();
        List<String> fileNameSplitName;

        String sqlQuery_Insert_1 = "INSERT INTO " + PictogramRetriever.TABLE_NAME + "(`synsetID`, `lemma`, `pictoName`, `posTag`) ";
        String sqlQuery_Insert_2;

        try {
            Class.forName(PictogramRetriever.JDBC_DRIVER);
            conn = DriverManager.getConnection(PictogramRetriever.URL, PictogramRetriever.USER, PictogramRetriever.PASS);
            stmt = conn.createStatement();

            for (i = 0; i < fileSize; i++) {
                SynSets = turkish.getSynSetsWithLiteral(fileNames.get(i).replace(".png", ""));
                if (SynSets.isEmpty()) {
                    fileNameSplitName = MorphologicalAnalysis.deletePossession(fileNames.get(i).replace(".png", ""));
                    for (j = 0; j < fileNameSplitName.size(); j++) {
                        SynSets = turkish.getSynSetsWithLiteral(fileNameSplitName.get(j).substring(0, fileNameSplitName.get(j).indexOf(":")));
                        if (!SynSets.isEmpty()) {
                            for (k = 0; k < SynSets.size(); k++) {
                                sqlQuery_Insert_2 = " VALUES ('" + SynSets.get(k).getId() + "', '" + fileNameSplitName.get(j).substring(0, fileNameSplitName.get(j).indexOf(":")) + "', '" + fileNames.get(i) + "','" + SynSets.get(k).getPos().toString() + "')";
                                stmt.executeUpdate(sqlQuery_Insert_1 + sqlQuery_Insert_2);
                            }
                        }
                    }
                } else {
                    for (j = 0; j < SynSets.size(); j++) {
                        sqlQuery_Insert_2 = " VALUES ('" + SynSets.get(j).getId() + "', '" + fileNames.get(i).replace(".png", "") + "', '" + fileNames.get(i) + "','" + SynSets.get(j).getPos().toString() + "')";
                        stmt.executeUpdate(sqlQuery_Insert_1 + sqlQuery_Insert_2);
                    }
                }

            }
            stmt.close();
            conn.close();
            System.out.println("Insert Completed!");
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }
}
