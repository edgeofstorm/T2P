// need to import xercesimpl.jar(most used parser which is also used in WordNet) manually
// there are conflictions because they do not publish official maven release
// check more info here : https://stackoverflow.com/questions/11677572/dealing-with-xerces-hell-in-java-maven

//FIXME
// - NER kullan.
// - zemberek sacma sacma kokler aliyo reyizin morfolojisine bak(maybe ITS BECAUSE .ignoreDiacriticsInAnalysis())
// - SyntaxAnalysis
// - deletePossession updatele
// - linkersynsetpictogram (yuklerken orjinal kelimeyi al possession yoksa).

import WordNet.*;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.lexicon.RootLexicon;
import zemberek.morphology.morphotactics.Morpheme;
import zemberek.morphology.morphotactics.MorphemeState;
import zemberek.morphology.morphotactics.TurkishMorphotactics;
import zemberek.tokenization.TurkishSentenceExtractor;

import java.sql.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.*;

public class T2P {
    static String URL = "jdbc:mysql://localhost/test?user=&pass=&useUnicode=true&characterEncoding=UTF-8";
    static String USER = "root";
    static String PASS = "hakki1996";
    static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static String PICTO_FOLDER_PATH = "C:\\Users\\haQQi\\Desktop\\FINISH HIM\\Translated Pictos Test";
    static String TABLE_NAME = "text2pic";

    public static void main(String[] args) throws IOException {
        //WordNet turkish = new WordNet();
        // WordNet english = new WordNet("english_wordnet_version_31.xml");

        //named entity recognition(look NerTraining.java for more)
//        System.out.println(NerTraining.NER("Ali Kaan yarın İstanbul'a gidecek."));

        //linking
//        ArrayList<String> fileNamesTemp = new ArrayList<>(listAllFiles(PICTO_FOLDER_PATH));
//        linkerSynsetPictogram(fileNamesTemp);

        //getting pictoNames and SynSets
//        Input2Picto();

//        SynSet SynSetForTest = turkish.getSynSetWithId("TUR10-0650680");
//        SynSet SynSetForTest = turkish.getSynSetWithLiteral("okul", 3);
//        ArrayList<SynSet> SynSetForTest = turkish.getSynSetsWithLiteral("muz");

        //Print array synset
//        for (i = 0; i < SynSetForTest.size(); i++) {
//            printSynSet(SynSetForTest.get(i));
//            printSynSet(SynSetForTest);
//        }

        //Finds and print specific relations to end
//        for (i = 0; i < SynSetForTest.size(); i++) {
//            //Semantic types
////                ANTONYM, HYPERNYM,
////                INSTANCE_HYPERNYM, HYPONYM, INSTANCE_HYPONYM, MEMBER_HOLONYM, SUBSTANCE_HOLONYM,
////                PART_HOLONYM, MEMBER_MERONYM, SUBSTANCE_MERONYM, PART_MERONYM, ATTRIBUTE,
////                DERIVATION_RELATED, DOMAIN_TOPIC, MEMBER_TOPIC, DOMAIN_REGION, MEMBER_REGION,
////                DOMAIN_USAGE, MEMBER_USAGE, ENTAILMENT, CAUSE, ALSO_SEE,
////                VERB_GROUP, SIMILAR_TO, PARTICIPLE_OF_VERB
//            findRelationSynSet(SynSetForTest.get(i), SemanticRelationType.DOMAIN_TOPIC, turkish);
//        }
    }

    public static void Input2Picto(){

//        TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
        TurkishMorphology morphology = TurkishMorphology.builder()
                .setLexicon(RootLexicon.getDefault())
                .ignoreDiacriticsInAnalysis()//ignoring(fixing) Turkish diacritics marks[ç,ğ,i,ö,ü,ş vs] exp: t�kenmez -> tükenmez
                .build();

        //sentence extractor ()
        TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;

        Scanner scan = new Scanner(System.in);
        System.out.println("Input : ");
        String input = scan.nextLine();
        Boolean hasPNoun = false;
        List<String> sentences = extractor.fromParagraph(input);

        for (String str : sentences) {
            List<String> list = new ArrayList<String>();
            String lemma = "";
            String posTag = "";
            //?morphology.analyzeAndDisambiguate(str);
            List<WordAnalysis> analysis = morphology.analyzeSentence(str);
            SentenceAnalysis disambiguation = morphology.disambiguate(str, analysis);
            disambiguation.bestAnalysis().forEach(s -> {
                list.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));
            });
            for (String s : list) {//TODO to use list.contains you need to override the method which uses equals (check later)
                if (s.contains("Prop")) {
                    hasPNoun = true;
                    break;
                }
            }
            for (String s : list) {
                lemma = s.substring(0, s.indexOf(":"));
                switch (s.substring(s.indexOf(":") + 1)) {//Multidimensional array kullan -> 2x8 vb
                    case "Noun":
                        posTag = "NOUN";
                        break;
                    case "Verb":
                        posTag = "VERB";
                        break;
                    case "Prop":
                        //do ner here
                        hasPNoun = true;
                        break;
                    default:
                        posTag = "X";
                        break;

                    //other cases like adj,adv...
                }
                System.out.println("lemma -> " + lemma + "  posTag -> " + posTag);
                SelectDB(lemma, posTag);
                //todo eger farkli kelimeler ayni pictoya gitmisse tamlama gibi davran --> S Y N T A X  A N A L Y S I S
            }

        }

    }

    public static void printSynSet(SynSet SynSetPrint) {
        int i;

        System.out.println("****************************************************************************************");
        System.out.println("ID: " + SynSetPrint.getId());
        System.out.println("Pos: " + SynSetPrint.getPos());// Return Pos object
        //Synonyms and senses
        for (i = 0; i < SynSetPrint.getSynonym().literalSize(); i++) {
            System.out.println("Synonym " + (i + 1) + ": " + SynSetPrint.getSynonym().getLiteral(i));
        }
        for (i = 0; i < SynSetPrint.relationSize(); i++) {
            System.out.println("Relation " + (i + 1) + ": " + SynSetPrint.getRelation(i));
        }
        System.out.println("Interlingual: " + SynSetPrint.getInterlingual());//Return ArrayList<String>
        System.out.println("Def: " + SynSetPrint.getDefinition());
        System.out.println("LongDef: " + SynSetPrint.getLongDefinition());
        System.out.println("Example: " + SynSetPrint.getExample());
        System.out.println("Note:" + SynSetPrint.getNote());
        System.out.println("getBcs: " + SynSetPrint.getBcs());
    }

    public static void findRelationSynSet(SynSet SynSetPrint, SemanticRelationType relationType, WordNet wordNet) {
        int i;
        String SynSetIDTemp;

        printSynSet(SynSetPrint);//Firstly print SynSet

        //Relations
        for (i = 0; i < SynSetPrint.relationSize(); i++) {
            Relation r = SynSetPrint.getRelation(i);
            if (r instanceof SemanticRelation) {
                if (((SemanticRelation) r).getRelationType().equals(relationType)) {
                    System.out.println("\tFind->Relation " + (i + 1) + ": " + SynSetPrint.getRelation(i));
                    SynSetIDTemp = SynSetPrint.getRelation(i).getName();
                    findRelationSynSet(wordNet.getSynSetWithId(SynSetIDTemp), relationType, wordNet);
                }
            }
        }
    }

    static public void linkerSynsetPictogram(ArrayList<String> fileNames) {
        int i, j, k;

        WordNet turkish = new WordNet();
        ArrayList<SynSet> SynSets;

        Connection conn = null;
        Statement stmt = null;

        int fileSize = fileNames.size();
        List<String> fileNameSplitName;

        String sqlQuery_Insert_1 = "INSERT INTO "+TABLE_NAME+"(`synsetID`, `lemma`, `pictoName`, `posTag`) ";
        String sqlQuery_Insert_2;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            //execute
            stmt = conn.createStatement();
            for (i = 0; i < fileSize; i++) {


                SynSets = turkish.getSynSetsWithLiteral(fileNames.get(i).replace(".png",""));
                if (SynSets.isEmpty()) {
                    fileNameSplitName=deletePossession(fileNames.get(i).replace(".png", ""));
                    //fileNameSplitName = fileNames.get(i).replace(".png", "").split("\\s+");//split file names to string array
                    for (j = 0; j < fileNameSplitName.size(); j++) {
                        SynSets = turkish.getSynSetsWithLiteral(fileNameSplitName.get(j));
                        if(!SynSets.isEmpty()){
                        for (k = 0; k < SynSets.size(); k++) {
                            sqlQuery_Insert_2 = " VALUES ('" + SynSets.get(k).getId() + "', '" + fileNameSplitName.get(j) + "', '" + fileNames.get(i) + "','" + SynSets.get(k).getPos().toString() + "')";
                            stmt.executeUpdate(sqlQuery_Insert_1 + sqlQuery_Insert_2);
                        }}
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

    static public void SelectDB(String lemma, String posTag) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            //execute
            stmt = conn.createStatement();

            String sqlQuery_Select = "SELECT pictoName,synsetID FROM "+TABLE_NAME+" WHERE lemma = '" + lemma + "' AND posTag = '" + posTag + "';";
            rs = stmt.executeQuery(sqlQuery_Select);
            while (rs.next()) {
                System.out.println("pictoName -> " + rs.getString("pictoName") + " SynsetID -> " + rs.getString("synsetID") + " WHERE lemma=" + lemma + "posTag=" + posTag);
            }

            rs.close();
            stmt.close();
            conn.close();
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

    //todo  (cumle yerine kelime alabilir) + postagleri de al(baglancak synset sayisi azalmis olur(bi kelimenin hem noun hem hem verb hem adj butun synsetlerini almamis olur)),maybe input olarak morph tag de alabilir -(eklendi)BUTUN POSSESSION EKLERINI SIL(suan sadece p3sg siliyor.)
    static public List<String> deletePossession(String input) {
        TurkishMorphology morphology = TurkishMorphology.builder()
                .setLexicon(RootLexicon.getDefault())
                .ignoreDiacriticsInAnalysis()
                .build();

        List<String> list = new ArrayList<String>();
        List<WordAnalysis> analysis = morphology.analyzeSentence(input);
        SentenceAnalysis disambiguate = morphology.disambiguate(input, analysis);
        disambiguate.bestAnalysis().forEach(s -> {//postagleri ekle
            //[kedi:Noun] kedi:Noun+ler:A3pl+i:P3sg  ekleri ayir(+...+) -> p3sg yoksa && : varsa +...: arasini al
            //[kedi:Noun] kedi:Noun+ler:A3pl+i:P3pl
            if (ContainsPossession(s.getMorphemes())) {//Possession varsa eki sil list e ekle
                String possessionTag = getPossessionTag(s.getMorphemes());
                if (StringUtils.countMatches(s.formatLong().substring(0, s.formatLong().indexOf(possessionTag) - 1), ':') <= 2)//p3sg den once baska ek yoksa kokunu al yumusama sertlesme gibi bozulmalarin onune gecer(kitab,kapag)
                    list.add(s.formatLexical().substring(1, s.formatLexical().indexOf(":")));
                else {
                    String[] splits = StringUtils.split(s.formatLong().substring(s.formatLong().indexOf(' ') + 1), '+');
                    StringBuilder strBuild = new StringBuilder();
                    for (String str : splits) {
                        if (!str.contains(possessionTag) && str.contains(":")) {
                            String a = str.substring(0, str.indexOf(":"));
                            strBuild.append(a);
                            /*for(String inner:StringUtils.split(str,':')){ //check out later for postag
                                    System.out.println("inner -> "+inner.);
                            }*/
                        }
                    }
                    list.add(strBuild.toString());
                }
            } else {//Possession yoksa direk ekle (belki ilerde baska tur eklerde sorun cikarabilir)
                list.add(s.surfaceForm());
            }
        });
        return list;
    }

    static public String getPossessionTag(List<Morpheme> morphList) {
        String posTag = "";
        String[] possession = {"P1sg", "P2sg", "P3sg", "P1pl", "P2pl", "P3pl"};

        for (int i = 0; i < possession.length; i++) {
            for (int j = 0; j < morphList.size(); j++) {
                if (possession[i] == morphList.get(j).id) {
                    posTag = possession[i];
                    return posTag;
                }
            }
        }
        return posTag;
    }

    static public boolean ContainsPossession(List<Morpheme> morphList) {
        if (getPossessionTag(morphList) == "")
            return false;
        return true;
    }

    // Uses Files.walk method
    static public ArrayList<String> listAllFiles(String path) {
        ArrayList<String> fileNames = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        fileNames.add(filePath.getFileName().toString());
                        //fileContent.addAll(readContent(filePath));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileNames;
    }

}
