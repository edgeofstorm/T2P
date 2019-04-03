// need to import xercesimpl.jar(most used parser which is also used in WordNet) manually
// there are conflictions because they do not publish official maven release
// check more info here : https://stackoverflow.com/questions/11677572/dealing-with-xerces-hell-in-java-maven

//FIXME
// - NER kullan.
// - zemberek sacma sacma kokler aliyo reyizin morfolojisine bak(maybe ITS BECAUSE .ignoreDiacriticsInAnalysis())
// - SyntaxAnalysis
// - deletePossession updatele(posTag iptal)
// - linkersynsetpictogram (yuklerken orjinal kelimeyi al possession yoksa).
// - selectdb ye ınput olarak belirsizIsimTamlamalari,pluralWords yollanabilir sadece 1 kere calissin fonksiyon(nereye koyulcak eger bu pictoalr varsa)
// - enerji icecegini -> enerji icecegi
// - turkishmorptactics.a1pl vs

import Dictionary.Pos;
import WordNet.*;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.lexicon.RootLexicon;
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

import org.apache.commons.lang3.*;

public class T2P {
    static String URL = "jdbc:mysql://localhost/test?user=&pass=&useUnicode=true&characterEncoding=UTF-8";
    static String USER = "root";
    static String PASS = "hakki1996";
    static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static String PICTO_FOLDER_PATH = "C:\\Users\\haQQi\\Desktop\\FINISH HIM\\Translated Pictos Test";
    static String TABLE_NAME = "text2pic";

    //TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
    public static TurkishMorphology morphology = TurkishMorphology.builder()
            .setLexicon(RootLexicon.getDefault())
            .ignoreDiacriticsInAnalysis()
            .build();

    public static void main(String[] args) throws IOException {
        //WordNet turkish = new WordNet();
        // WordNet english = new WordNet("english_wordnet_version_31.xml");

        //named entity recognition(look NerTraining.java for more)
        //System.out.println(NerTraining.NER("Ali Kaan yarın İstanbul'a gidecek."));

        //linking
        //ArrayList<String> fileNamesTemp = new ArrayList<>(listAllFiles(PICTO_FOLDER_PATH));
        //linkerSynsetPictogram(fileNamesTemp);

        //getting pictoNames and SynSets
        //Input2Picto();

        getTamlama("Ahmet enerji icecegi icti.");
        //Input2Picto();
        //PluralWord("kalemlerin kitaplarin");
        /*for (String s : deletePossession("enerji icecegi")) {
            System.out.println(s.substring(0, s.indexOf(":")));
            System.out.println(s.substring(s.indexOf(":") + 1));
            //ArrayList<SynSet> Syn=turkish.getSynSetsWithPossiblyModifiedLiteral("enerji",Pos.NOUN);//does not work?
            ArrayList<SynSet> Syn = turkish.getSynSetsWithLiteral(s.substring(0, s.indexOf(":")));
            for (int i = 0; i < Syn.size(); i++) {
                printSynSet(Syn.get(i));
            }
        }*/

        /*SynSet SynSetForTest = turkish.getSynSetWithId("TUR10-0650680");
        SynSet SynSetForTest = turkish.getSynSetWithLiteral("okul", 3);
        ArrayList<SynSet> SynSetForTest = turkish.getSynSetsWithLiteral("muz");

        Print array synset
        for (i = 0; i < SynSetForTest.size(); i++) {
            printSynSet(SynSetForTest.get(i));
            printSynSet(SynSetForTest);
        }

        Finds and print specific relations to end
        for (i = 0; i < SynSetForTest.size(); i++) {
            *//*Semantic types
                ANTONYM, HYPERNYM,
                INSTANCE_HYPERNYM, HYPONYM, INSTANCE_HYPONYM, MEMBER_HOLONYM, SUBSTANCE_HOLONYM,
                PART_HOLONYM, MEMBER_MERONYM, SUBSTANCE_MERONYM, PART_MERONYM, ATTRIBUTE,
                DERIVATION_RELATED, DOMAIN_TOPIC, MEMBER_TOPIC, DOMAIN_REGION, MEMBER_REGION,
                DOMAIN_USAGE, MEMBER_USAGE, ENTAILMENT, CAUSE, ALSO_SEE,
                VERB_GROUP, SIMILAR_TO, PARTICIPLE_OF_VERB*//*
            findRelationSynSet(SynSetForTest.get(i), SemanticRelationType.DOMAIN_TOPIC, turkish);
        }*/
    }

    public static void Input2Picto() {

        //sentence extractor ()
        TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;

        Scanner scan = new Scanner(System.in);
        System.out.println("Input : ");
        String input = scan.nextLine();
        Boolean hasPNoun = false;
        List<String> sentences = extractor.fromParagraph(input);
        List<List<String>> out = new ArrayList<List<String>>();
        List<List<String>> original = new ArrayList<List<String>>();
        String temp = "";
        String temp2 = "";
        int count = 0;
        boolean isPrevNoun = false;

        // every word of each sentence
        /*for (int i = 0; i < sentences.size(); i++) {
            List<String> originalSentence = new ArrayList<String>();
            for (int j = 0; j < sentences.get(i).split("\\s+").length; j++) {
                originalSentence.add(sentences.get(i).split("\\s+")[j]);
            }
            original.add(originalSentence);
        }*/

        for (String str : sentences) {
            ArrayList<String> list = new ArrayList<String>();
            List<String> belirsizIsimTamlamalari = new ArrayList<String>();
            List<String> pluralWords = new ArrayList<String>();
            String lemma = "";
            String posTag = "";

            List<WordAnalysis> analysis = morphology.analyzeSentence(str);
            SentenceAnalysis disambiguation = morphology.disambiguate(str, analysis);
            List<SingleAnalysis> bestAnalysis = disambiguation.bestAnalysis();

            for (SingleAnalysis s : bestAnalysis) {
                list.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));
            }
            out.add(list);
            /*for (String s : list) {//TODO to use list.contains you need to override the method which uses equals (check later)
                if (s.contains("Prop")) {//todo ner here
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
                    case "Conj":
                        //+ picto

                        break;
                    default:
                        posTag = "X";
                        break;

                    //other cases like adj,adv...
                }
                System.out.println("lemma -> " + lemma + "  posTag -> " + posTag);
                SelectDB(lemma, posTag);

            }*/

        }
        SelectDB(out, sentences);//3.parametre eklenebilir special cumleler(tamlama,cogul vs.)
        //0 -> original sentence,1->lemmazation
        //normal cumleler + tamlamali,cogullu vs cumleler olabilir
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

        String sqlQuery_Insert_1 = "INSERT INTO " + TABLE_NAME + "(`synsetID`, `lemma`, `pictoName`, `posTag`) ";
        String sqlQuery_Insert_2;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            //execute
            stmt = conn.createStatement();
            for (i = 0; i < fileSize; i++) {
                SynSets = turkish.getSynSetsWithLiteral(fileNames.get(i).replace(".png", ""));
                if (SynSets.isEmpty()) {//&& containsPos && containsPlural
                    fileNameSplitName = deletePossession(fileNames.get(i).replace(".png", ""));
                    //fileNameSplitName = fileNames.get(i).replace(".png", "").split("\\s+");//split file names to string array
                    for (j = 0; j < fileNameSplitName.size(); j++) {
                        SynSets = turkish.getSynSetsWithLiteral(fileNameSplitName.get(j).substring(0, fileNameSplitName.get(j).indexOf(":")));
                        //SynSets=turkish.getSynSetsWithPossiblyModifiedLiteral(fileNameSplitName.get(j).substring(0,fileNameSplitName.get(j).indexOf(":")),Pos.NOUN);//does not work?
                        if (!SynSets.isEmpty()) {//posTag karsilastirmasi
                            for (k = 0; k < SynSets.size(); k++) {
                                sqlQuery_Insert_2 = " VALUES ('" + SynSets.get(k).getId() + "', '" + fileNameSplitName.get(j) + "', '" + fileNames.get(i) + "','" + SynSets.get(k).getPos().toString() + "')";
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

    static public void SelectDB(List<List<String>> morphed, List<String> originals) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean tamlamaFound = false;
        int first = 0;
        int last = 0;
        String tamlama = "";
        String plural = "";
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            //execute
            stmt = conn.createStatement();
            for (int i = 0; i < morphed.size(); i++) {//paragraph
                //if hasTamlama(original) morph.geti . set -> morphlu morphlu tamlama morph cumle gondersin boyle morphlu IF(TRUE) ->EVALUATETAMLAMA LIST<STRING> DONDURCEK **check if tamlama picto exists
                tamlama = getTamlama(originals.get(i));
                if (tamlama != "") {
                    first = Integer.parseInt(tamlama.substring(0, tamlama.indexOf(":")).split(",")[0]);
                    last = Integer.parseInt(tamlama.substring(0, tamlama.indexOf(":")).split(",")[1]);
                    tamlamaFound = true;
                }
                plural = PluralWord(originals.get(i));
                if(plural!=""){
                    //list yap vs vs.
                }
                for (int j = 0; j < morphed.get(i).size(); j++) {//sentences
                    if (tamlamaFound && j == first) {
                        String sqlQuery_Select2 = "SELECT pictoName FROM " + TABLE_NAME + " WHERE pictoName = '" + tamlama.substring(tamlama.indexOf(":") + 1) + ".png" + "';";
                        rs = stmt.executeQuery(sqlQuery_Select2);
                        if (!rs.next()) {
                            j--;
                            tamlamaFound=false;
                            continue;
                        }
                        System.out.println("pictoName -> " + rs.getString("pictoName"));
                        j++;
                        continue;
                    }
                    String sqlQuery_Select = "SELECT pictoName,synsetID FROM " + TABLE_NAME + " WHERE lemma = '" + morphed.get(i).get(j).substring(0, morphed.get(i).get(j).indexOf(":")) + "' AND posTag = '" + morphed.get(i).get(j).substring(morphed.get(i).get(j).indexOf(":") + 1) + "';";
                    rs = stmt.executeQuery(sqlQuery_Select);
                    if (!rs.next()) {
                        continue;
                    }
                    do {
                        System.out.println("pictoName -> " + rs.getString("pictoName") + " SynsetID -> " + rs.getString("synsetID") + " WHERE lemma=" + morphed.get(i).get(j).substring(0, morphed.get(i).get(j).indexOf(":")) + "posTag=" + morphed.get(i).get(j).substring(morphed.get(i).get(j).indexOf(":") + 1));
                    }
                    while (rs.next());
                }
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

    //todo deleteMorphTag(String input,String morphtag)
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
            //enerji icecegi->i=p3sg , enerji icecekleri->i=Acc
            if (ContainsPossession(s.getMorphemes())) {//Possession varsa eki sil list e ekle
                String possessionTag = getPossessionTag(s.getMorphemes());
                if (StringUtils.countMatches(s.formatLong().substring(0, s.formatLong().indexOf(possessionTag) - 1), ':') <= 2)//Possessiondan once baska ek yoksa kokunu al yumusama sertlesme gibi bozulmalarin onune gecer(kitab,kapag)
                    list.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));//:
                else {
                    String[] splits = StringUtils.split(s.formatLong().substring(s.formatLong().indexOf(' ') + 1), '+');
                    StringBuilder strBuild = new StringBuilder();
                    for (String str : splits) {
                        if (!str.contains(possessionTag) && str.contains(":")) {
                            String a = str.substring(0, str.indexOf(":"));
                            strBuild.append(a);
                        }
                    }
                    list.add(strBuild.toString() + ":" + s.getPos().getStringForm());
                }
            } else {//Possession yoksa direk ekle (belki ilerde baska tur eklerde sorun cikarabilir)
                list.add(s.surfaceForm() + ":" + s.getPos().getStringForm());
            }
        });
        return list;
    }

    static public String getPossessionTag(List<Morpheme> morphList) {
        String posTag = "";
        String[] possession = {"P1sg", "P2sg", "P3sg", "P1pl", "P2pl", "P3pl"};//"Acc"

        for (int i = 0; i < possession.length; i++) {
            for (int j = 0; j < morphList.size(); j++) {
                if (possession[i] == morphList.get(j).id) {
                    posTag = possession[i];
                    return posTag;
                }
            }
        }
        return posTag;
    }//morphList yerine String yap

    static public String getPluralTag(List<Morpheme> morphList) {
        String posTag = "";
        String[] plural = {"A1pl", "A2pl", "A3pl"};

        for (int i = 0; i < plural.length; i++) {
            for (int j = 0; j < morphList.size(); j++) {
                if (plural[i] == morphList.get(j).id) {
                    posTag = plural[i];
                    return posTag;
                }
            }
        }
        return posTag;
    }

    static public boolean ContainsPlural(List<Morpheme> morphList) {
        if (getPluralTag(morphList) == "")
            return false;
        return true;
    }

    //s.containsMorpheme(TurkishMorphotactics.a1pl)
    static public String PluralWord(String sentence) {
        String plural = "";
        String pluralTag = "";
        List<WordAnalysis> analysis = morphology.analyzeSentence(sentence);
        SentenceAnalysis disambiguate = morphology.disambiguate(sentence, analysis);
        int count=0;
        for (SingleAnalysis s : disambiguate.bestAnalysis()) {
            if (ContainsPlural(s.getMorphemes())) {
                pluralTag = getPluralTag(s.getMorphemes());
                String[] splits = StringUtils.split(s.formatLong().substring(s.formatLong().indexOf(' ') + 1), '+');
                StringBuilder strBuild = new StringBuilder();
                strBuild.append(splits[0].substring(0, splits[0].indexOf(":")));
                for (String str : splits) {
                    if (str.contains(pluralTag) && str.contains(":")) {
                        String a = str.substring(0, str.indexOf(":"));
                        strBuild.append(a);
                        break;
                    }
                }
                strBuild.append(":"+Integer.toString(count));
                plural = strBuild.toString();
                System.out.println(plural);
                //return plural;
            }
            count++;
        }


        return plural;
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

    //2+ isimli tamlama yoksa last int i kaldir
    static public String getTamlama(String sentence) {
        List<String> list = new ArrayList<String>();
        String temp = "";
        String temp2 = "";
        String str = "";
        int count = 0;
        boolean isPrevNoun = false;
        List<WordAnalysis> analysis = morphology.analyzeSentence(sentence);
        SentenceAnalysis disambiguate = morphology.disambiguate(sentence, analysis);
        for (SingleAnalysis s : disambiguate.bestAnalysis()) {
            list.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));
            temp = s.formatLexical().substring(s.formatLexical().indexOf(":") + 1, s.formatLexical().indexOf("]"));
            if (temp.contains("Noun") && s.getEnding().isEmpty()) {
                isPrevNoun = true;
                temp2 = s.surfaceForm();
            }
            if (temp.contains("Noun") && ContainsPossession(s.getMorphemes()) && isPrevNoun) {
                temp2 = temp2 + " " + s.surfaceForm();
                str = Integer.toString(count - 1) + "," + Integer.toString(count) + ":" + temp2;
                list.remove(list.size() - 1);
                list.remove(list.size() - 1);
                list.add(temp2);
                isPrevNoun = false;
                temp2 = "";
            }
            count++;
        }
        System.out.println(list);
        return str;
    }
}
