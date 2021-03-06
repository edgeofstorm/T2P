// need to import xercesimpl.jar(most used parser which is also used in WordNet) manually
// there are conflictions because they do not publish official maven release
// check more info here : https://stackoverflow.com/questions/11677572/dealing-with-xerces-hell-in-java-maven

//FIXME
// - turkishmorptactics.a1pl vs
// - STOPWORDS FILTER.
// - function return type list -> String(containing replaced part and index(list))
// - DEVRIK CUMLE -> verbi en sona at ( MAKE LINKEDLIST MAYBE ? -> YOU CAN TRAVERSE THE WORDS CHECK IF THE VERB IS IN THE END ? )
// - NERTEST GELISTIR + HER CUMLE NERE GIRMESIN
// - COGUL SUAN SADECE CUMLEDE 1 TANE COGUL VARSA CALISIYO DUZELT
// - Bazı Pos enumlar eksik
// - Determiner kalkıyor.

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

import javax.swing.plaf.synth.SynthTextAreaUI;

import static WordNet.SemanticRelationType.*;

public class T2P {

    public enum SentenceType {
        Plural,
        Tamlama,
        GizliOzne,
        Fiilimsi,
        Unknown
    }

    public enum WordPos {//word pos

        Noun("NOUN"),
        NounTime("NOUN"),
        Adj("ADJECTIVE"),
        Adv("ADVERB"),
        Conj("CONJUNCTION"),
        Interj("INTERJECTION"),
        Verb("VERB"),
        Pron("PRONOUN"),
        Num("NOUN"),
        Det("NOUN"),
        PostP("NOUN"),
        Ques("NOUN"),
        Dup("NOUN"),
        Punc("NOUN"),
        Unk("NOUN"),
        PronPers("PRONOUN"),
        PronQuant("PRONOUN"),
        NounProp("NOUN"),
        PronDemons("PRONOUN");
        private String WordnetForm;

        public String getwordnetForm() {
            return this.WordnetForm;
        }

        private WordPos(String wordnetForm) {
            this.WordnetForm = wordnetForm;
        }
    }

    static String URL = "jdbc:mysql://localhost/picto2text?user=&pass=&useUnicode=true&characterEncoding=UTF-8";
    static String USER = "root";
    static String PASS = "248163264:Hakan";
    static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static String PICTO_FOLDER_PATH = "C:\\Users\\hakac\\T2P\\src\\main\\resources\\Pictograms";
    static String TABLE_NAME = "texttopicto";
    static String[] edats = {"gibi", "kadar", "icin", "dolayi", "oturu", "yalniz", "ancak", "tek", "uzere", "sanki", "diye",};//sadece //[sabah:Noun,Time] sabah:Noun+A3sg+a:Dat [doğru:Adj] doğru:Adj [sabah:Noun,Time] sabah:Noun+A3sg+a:Dat [karşı:Postp,PCDat] karşı:Postp //dat+adj or dat+adv delete it.
    static int SYNSET_LIMIT = 5;
// Emine ile Pınar sinemaya gitti. (”İle” yerine ”ve” gelebilir. → Bağlaç) » Bu çalışma ile sonuç alınmaz. (”İle” yerine ”ve” getirilemez. → Edat)

    public static TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
   /* public static TurkishMorphology morphology = TurkishMorphology.builder()
            .setLexicon(RootLexicon.getDefault())
            .ignoreDiacriticsInAnalysis()
            .build();*/

    public static WordNet turkish = new WordNet();

    public static void main(String[] args) throws IOException {

        //named entity recognition(look NerTraining.java for more)
        //System.out.println(NerTraining.NER("Ali Kaan yarın Ayvalık Belediyesi'ne gidecek."));
        //System.out.println("Ayşe yarın İstanbul'a gidecek.");

        //linking
        //ArrayList<String> fileNamesTemp = new ArrayList<>(listAllFiles(PICTO_FOLDER_PATH));
        //linkerSynsetPictogram(fileNamesTemp);

        //getting pictoNames and SynSets
        /*getPluralWord("Evle okul arasında mekik dokuyor.Toplantiya sadece 3 kisi katildi.Yuregim tek senin icin atiyor.elma ile armut yaralanan yolcular hastaneye kaldirildi Arabayla Ahmet giderken kitaplari ve kalemleri cantasina koydu");//[elma:Noun] elma:Noun+A3sg+yla:Ins+armut [otobüs:Noun] otobüs:Noun+A3sg+le:Ins+gittim(birinde edat birinde baglac)
        getTamlama("Ahmet enerji icecegi icti.");
        getPersonTag("arabaya gittiler.");*/
        //getPluralWord("kalemleri ve kitaplari cantasina koyarken dusurdu.");
        //getPluralWord("Eve giderken bize uğra.");
        //getTamlama("Ahmet enerji içeceği icti.");
        //getPersonTag("Köpekler saldırdı.");

//        Scanner scan = new Scanner(System.in);
//        System.out.println("Input : ");
//        String input = scan.nextLine();
//        Input2Picto(input);

        /*for (String s : deletePossession("enerji icecegi")) {
            System.out.println(s.substring(0, s.indexOf(":")));
            System.out.println(s.substring(s.indexOf(":") + 1));
            //ArrayList<SynSet> Syn=turkish.getSynSetsWithPossiblyModifiedLiteral("enerji",Pos.NOUN);//does not work?
            ArrayList<SynSet> Syn = turkish.getSynSetsWithLiteral(s.substring(0, s.indexOf(":")));
            for (int i = 0; i < Syn.size(); i++) {
                printSynSet(Syn.get(i));
            }
        }*/

        /*//SynSet SynSetForTest = turkish.getSynSetWithId("TUR10-0650680");
        //SynSet SynSetForTest = turkish.getSynSetWithLiteral("okul", 1);
        ArrayList<SynSet> SynSetForTest = turkish.getSynSetsWithLiteral("muz");



        //Print array synset
        for (int i = 0; i < SynSetForTest.size(); i++) {
            printSynSet(SynSetForTest.get(i));
        }

        //Finds and print specific relations to end
        for (int i = 0; i < SynSetForTest.size(); i++) {
            printSynSet(SynSetForTest.get(i));
            *//*Semantic types
                ANTONYM, HYPERNYM,
                INSTANCE_HYPERNYM, HYPONYM, INSTANCE_HYPONYM, MEMBER_HOLONYM, SUBSTANCE_HOLONYM,
                PART_HOLONYM, MEMBER_MERONYM, SUBSTANCE_MERONYM, PART_MERONYM, ATTRIBUTE,
                DERIVATION_RELATED, DOMAIN_TOPIC, MEMBER_TOPIC, DOMAIN_REGION, MEMBER_REGION,
                DOMAIN_USAGE, MEMBER_USAGE, ENTAILMENT, CAUSE, ALSO_SEE,
                VERB_GROUP, SIMILAR_TO, PARTICIPLE_OF_VERB*//*
            findRelationSynSet(SynSetForTest.get(i), SemanticRelationType.DOMAIN_TOPIC, turkish);
        }*/

        //linkerSynsetPictogram(listAllFiles(PICTO_FOLDER_PATH));
//        System.out.println(findLexialSynonym("gazoz", Pos.NOUN));
//        System.out.println(findLexialSynonym("kapak", Pos.NOUN));
//        System.out.println(findLexialSynonym("açmak", Pos.VERB));


//        List<List<String>> test = new ArrayList<>();
//        List<String> test1 = new ArrayList<>();
//        List<String> test2 = new ArrayList<>();
//        List<String> test3 = new ArrayList<>();
//        test1.add("a");
//        test1.add("b");
//        test2.add("x");
//        test2.add("y");
//        test2.add("z");
//        test3.add("i");
//        test3.add("j");
//
//        test.add(test1);
//        test.add(test2);
//        test.add(test3);
//
//        System.out.println(test);
//        System.out.println(generate2(test));
        List<String> test = new ArrayList<>();
        test.add("P-metaller:NOUN+metal:NOUN");
        System.out.println(findLexialSynonym("muz", Pos.NOUN));
        System.out.println("picto: "+SelectDB(test));


//        for (List<String> s : queryBuilder(test, 3)) {
//            System.out.println(s);
//        }


    }


    @SuppressWarnings("Duplicates")
    static public List<String> SelectDB(List<String> text) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        List<String> pictoList = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            stmt = conn.createStatement();

            for (int x=0;x<text.size();x++){
                int index;
                String templateText = text.get(x);
                List<String> pictoListTemp = new ArrayList<>();
                List<List<String>> pictoListsTemp = new ArrayList<>();
                List<String> synonymList = new ArrayList<>();
                List<String> returnList = new ArrayList<>();
                List<String> synonymListTemp = new ArrayList<>();
                List<String> splitTamlamaListTemp = new ArrayList<>();
                boolean found = false;

                if (templateText.equals(",") || templateText.equals("+")) {
                    pictoList.add("artı.png");
                } else if (templateText.equals(".")) {
                    pictoList.add("nokta.png");
                } else if (templateText.equals("PERSON")) {
                    pictoList.add("person.png");
                } else if (templateText.equals("LOCATION")) {
                    pictoList.add("location.png");
                } else if (templateText.equals("ORGANIZATION")) {
                    pictoList.add("organization.png");
                } else if (templateText.substring(0, 2).equals("P-")) {
                    List<String> returnP = new ArrayList<>();
                    for (String splited : templateText.substring(2).split("\\+")) {
                        returnP.add(splited);
                        splitTamlamaListTemp.add(removeLemmaPos(splited, "pos"));
                    }
                    for (String s1 : splitTamlamaListTemp) {
                        synonymListTemp.clear();
                        synonymListTemp.add(0, s1);//for list cast
                        for (List<String> s2 : queryBuilder(synonymListTemp, 1)) {
                            for(int i = 0;i<s2.size();i++){
                                if (found) {
                                    break;
                                }
                                rs = stmt.executeQuery(s2.get(i));
                                if (!rs.next()) {
                                    found = false;
                                    continue;
                                }
                                do {
                                    pictoListTemp.add(rs.getString("A.pictoName"));
                                    found = true;
                                }
                                while (rs.next());
                            }
                        }
                    }
                    if(!found){
                        index=x;
                        text.add(index+1,returnP.get(1));
                    }
                    if (!pictoListTemp.isEmpty()) {
                        pictoList.addAll(selectPicto(pictoListTemp));
                    }
                } else if (templateText.substring(0, 2).equals("T-")) {
                    int wordsSize = 0;
                    List<List<String>> tempSynonyms = new ArrayList<>();
                    for (String splited : templateText.substring(2).split("\\+")) {
                        List<String> tempSynonym = new ArrayList<>();
                        returnList.add(splited);
                        for (String s : findLexialSynonym(removeLemmaPos(splited, "pos"), Pos.valueOf(removeLemmaPos(splited, "lemma")))) {
                            tempSynonym.add(removeLemmaPos(s, "pos"));
                        }
                        tempSynonyms.add(tempSynonym);
                        wordsSize++;
                    }

                    for (int i = wordsSize; i > 0; i--) {
                        if (i == 1) {
                            index=x;
                            for (String s : returnList) {
                                text.add(index+1, s);
                                index++;
                            }
                            break;
                        }
                        for (List<String> s1 : generate2(tempSynonyms)) {
                            pictoListsTemp.clear();
                            for (List<String> s2 : queryBuilder(s1, i)) {
                                pictoListTemp.clear();
                                for (int j = 0; j < s2.size(); j++) {
                                    rs = stmt.executeQuery(s2.get(j));
                                    if (!rs.next()) {
                                        found = false;
                                        break;
                                    }
                                    do {
                                        pictoListTemp.add(rs.getString("A.pictoName"));
                                        found = true;
                                    }
                                    while (rs.next());
                                    pictoListsTemp.add(pictoList);
                                }
                                if (found) {
                                    break;
                                }
                            }
                            if (found) {
                                break;
                            }
                        }
                    }
                    if (!pictoListTemp.isEmpty()) {
                        for (List<String> s : pictoListsTemp) {
                            pictoList.addAll(selectPicto(s));
                        }
                    }
                } else {
                    synonymList.addAll(findLexialSynonym(removeLemmaPos(templateText, "pos"), Pos.valueOf(removeLemmaPos(templateText, "lemma"))));
                    for (String s1 : synonymList) {
                        if (found) {
                            break;
                        }
                        synonymListTemp.clear();
                        synonymListTemp.add(0, removeLemmaPos(s1, "pos"));
                        for (List<String> s2 : queryBuilder(synonymListTemp, synonymListTemp.size())) {
                            for (int i = 0; i < s2.size(); i++) {
                                if (found) {
                                    break;
                                }
                                rs = stmt.executeQuery(s2.get(0));
                                if (!rs.next()) {
                                    found = false;
                                    continue;
                                }
                                do {
                                    pictoListTemp.add(rs.getString("A.pictoName"));
                                    found = true;
                                }
                                while (rs.next());
                            }
                        }
                    }
                    if (!pictoListTemp.isEmpty()) {
                        pictoList.addAll(selectPicto(pictoListTemp));
                    }
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
        return pictoList;
    }

    static public List<String> selectPicto(List<String> pictoList) {
        String[] counter;
        List<String> picto_Selected = new ArrayList<>();
        int i, j;
        for (i = 1; i < 6; i++) {
            for (j = 0; j < pictoList.size(); j++) {
                counter = pictoList.get(j).replace(".png", "").split("\\s");
                System.out.println(counter);
                if (counter.length == i) {
                    picto_Selected.add(pictoList.get(j));
                    i = 10;
                    break;
                }
            }
        }
        return picto_Selected;
    }

    static public List<String> findLexialSynonym(String literal, Pos POStag) {
        int i, j, k, sense = 1;
        List<String> SYNONYM_SearchList = new ArrayList<>();
        List<SynSet> orderedSynSet_List = new ArrayList<SynSet>();
        List<SemanticRelationType> searching_Relations = new ArrayList<SemanticRelationType>();
        searching_Relations.add(HYPONYM);
        searching_Relations.add(HYPERNYM);
        searching_Relations.add(DOMAIN_TOPIC);
        searching_Relations.add(MEMBER_TOPIC);
        String tempString;
        boolean dublucated;
        SYNONYM_SearchList.add(literal + ":" + POStag);
        SynSet tempSynSet;

        //en fazla 5 adet synset alıyor
        for (i = 0; i < SYNSET_LIMIT; i++) {
            tempSynSet = turkish.getSynSetWithLiteral(literal, sense);
            if (tempSynSet == null || tempSynSet.getPos() != POStag) {
                i--;
            } else {
                orderedSynSet_List.add(tempSynSet);
            }
            sense++;
            if (sense == 50) {
                break;
            }
        }
        int staticSize = orderedSynSet_List.size();
        for (i = 0; i < searching_Relations.size(); i++) {
            for (j = 0; j < staticSize; j++) {
                tempSynSet = orderedSynSet_List.get(j);
                for (k = 0; k < tempSynSet.relationSize(); k++) {
                    Relation r = tempSynSet.getRelation(k);
                    if (r instanceof SemanticRelation) {
                        if (((SemanticRelation) r).getRelationType().equals(searching_Relations.get(i))) {
                            orderedSynSet_List.add(turkish.getSynSetWithId(tempSynSet.getRelation(k).getName()));
                        }
                    }
                }

            }
        }

        for (i = 0; i < orderedSynSet_List.size(); i++) {
            tempSynSet = orderedSynSet_List.get(i);
            for (j = 0; j < tempSynSet.getSynonym().literalSize(); j++) {
                dublucated = false;
                tempString = tempSynSet.getSynonym().getLiteral(j).toString().replaceAll("\\d+", "").trim() + ":" + POStag;
                for (k = 0; k < SYNONYM_SearchList.size(); k++) {
                    if (SYNONYM_SearchList.get(k).equals(tempString) || dublucated == true) {
                        dublucated = true;
                    }
                }
                if (tempString != null && dublucated == false) {
                    SYNONYM_SearchList.add(tempString);
                }
            }
        }
        return SYNONYM_SearchList;
    }

    static public List<List<String>> queryBuilder(List<String> ortakArayıcı, int wordCombineCount) {
        int i, j;
        List<List<String>> queriess = new ArrayList<>();
        List<List<List<String>>> combinationss = new ArrayList<>();

        combinationss.addAll(generate(ortakArayıcı, wordCombineCount));

        for (List<List<String>> combinations : combinationss) {
            List<String> queries = new ArrayList<>();
            for (List<String> combination : combinations) {
                String[] Data_objectName = {"A", "B", "C", "D", "E"};
                String SELECT_QueryPart = "SELECT DISTINCT A.pictoName";
                String FROM_QueryPart = " FROM";
                String WHERE_QueryPart = " WHERE ";
                String SearchObject_QueryPart = "";
                String JointMember_QueryPart = "";
                for (i = 0; i < combination.size(); i++) {
                    SELECT_QueryPart += ", " + Data_objectName[i] + ".lemma";
                    FROM_QueryPart += " texttopicto " + Data_objectName[i];
                    if (combination.size() - 1 != i) {
                        FROM_QueryPart += ", ";
                    }
                    SearchObject_QueryPart += Data_objectName[i] + ".lemma='" + combination.get(i) + "'";
                    if (combination.size() != 1) {
                        SearchObject_QueryPart += " AND ";
                        JointMember_QueryPart += Data_objectName[i] + ".pictoName";
                        if (combination.size() - 1 != i) {
                            JointMember_QueryPart += "=";
                        }
                    }
                }
                queries.add(SELECT_QueryPart + FROM_QueryPart + WHERE_QueryPart + SearchObject_QueryPart + JointMember_QueryPart);
            }
            queriess.add(queries);
        }
        return queriess;
    }

    public static void helper(List<List<List<String>>> combinationss, String data[], int startPoint, List<String> lemmas, int index) {
        String[] datas = data.clone();
        if (index == datas.length) {
            List<List<String>> combinations = new ArrayList<>();
            List<String> combination = Arrays.asList(datas);
            combinations.add(combination);
            for (String s1 : lemmas) {
                List<String> tempcombination = new ArrayList<>();
                boolean notExist = true;
                for (String s2 : combination) {
                    if (s1.equals(s2)) {
                        notExist = false;
                    }
                }
                if (notExist) {
                    tempcombination.add(s1);
                    combinations.add(tempcombination);
                }
            }
            combinationss.add(combinations);
        } else if (startPoint <= lemmas.size() - 1) {
            datas[index] = lemmas.get(startPoint);
            helper(combinationss, datas, startPoint + 1, lemmas, index + 1);
            helper(combinationss, datas, startPoint + 1, lemmas, index);
        }
    }

    public static List<List<List<String>>> generate(List<String> lemmas, int r) {
        List<List<List<String>>> combinationss = new ArrayList<>();
        helper(combinationss, new String[r], 0, lemmas, 0);
        return combinationss;
    }

    public static void helper2(List<List<String>> lists, List<String> temp, List<List<String>> result, int index) {
        if (index >= lists.size()) {
            List<String> tempTemp = new ArrayList<>();
            tempTemp.addAll(temp);
            result.add(tempTemp);
            return;
        }
        List<String> list = lists.get(index);
        for (int i = 0; i < list.size(); i++) {
            temp.add(list.get(i));
            helper2(lists, temp, result, index + 1);
            temp.remove(temp.size() - 1);
        }
    }

    public static List<List<String>> generate2(List<List<String>> synonymLists) {
        List<List<String>> result = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        helper2(synonymLists, temp, result, 0);
        return result;
    }

    static public String removeLemmaPos(String morfic, String part) {
        if (part.equals("pos")) {
            return morfic.substring(0, morfic.indexOf(":"));
        } else if (part.equals("lemma")) {
            return morfic.substring(morfic.indexOf(":") + 1);
        } else {
            return morfic;
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
            stmt = conn.createStatement();

            for (i = 0; i < fileSize; i++) {
                SynSets = turkish.getSynSetsWithLiteral(fileNames.get(i).replace(".png", ""));
                if (SynSets.isEmpty()) {
                    fileNameSplitName = deletePossession(fileNames.get(i).replace(".png", ""));
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


    public static List<List<String>> Input2Picto(String input) throws IOException {

        //sentence extractor ()
        TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;

        boolean hasPNoun = false;
        boolean hasTamlama = false;
        boolean hasPlural = false;
        boolean hasTamlamaInNer = false;
        boolean hasPunc = false;
        int indexVerb = 0;
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
            List<String> belirtisizIsimTamlamalari = new ArrayList<String>();
            List<String> pluralWords = new ArrayList<String>();
            String originalSentence = "";
            String lemma = "";
            String posTag = "";
            String replace = "";
            String replaceCogul = "";
            String[] indexess = new String[10];
            int removed = 0;
            List<String> ner = new ArrayList<String>();

            ner = NerTraining.NER(str);
            for (int i = 0; i < ner.size(); i++) {
                ner.set(i, ner.get(i).replaceAll("\'", ""));//replaceAll("[^a-zA-Z ]", "").toLowerCase());
            }
            System.out.println(ner);

            List<WordAnalysis> analysis = morphology.analyzeSentence(str);
            SentenceAnalysis disambiguation = morphology.disambiguate(str, analysis);
            List<SingleAnalysis> bestAnalysis = disambiguation.bestAnalysis();
            System.out.println(bestAnalysis.size());

            for (SingleAnalysis s : bestAnalysis) {

                originalSentence += s.surfaceForm() + " ";

                if (Arrays.asList(edats).contains(s.surfaceForm())) continue; //duzelt s.formatlexical
                if (s.getPos().getStringForm() == "Conj") {
                    list.add("+");
                    continue;
                }
                if (s.formatLexical().contains("Pron") || s.formatLexical().contains("Noun,Prop")) {
                    hasPNoun = true;
                }

                SentenceType sentenceType = SentenceType.Unknown;
                /*if (s.formatLexical().contains("Prop")) {
                    ner = NerTraining.NER(str);
                    *//*for(String prop: ner){
                        str.replace(prop,"");
                    }*//*
                }*/
                if (ContainsPlural(s.getMorphemes()) && !s.getPos().shortForm.equalsIgnoreCase("verb")) {
                    hasPlural = true;
                    sentenceType = SentenceType.Plural;
                    //continue;
                }
                if (ContainsPossession(s.getMorphemes())) {
                    if (getTamlama(str) != "") {
                        hasTamlama = true;
                        String[] splitt = getTamlama(str).split("-");
                        indexess = splitt[0].split(",");
                    }
                    sentenceType = SentenceType.Tamlama;
                }
                list.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));
                for (String nerr : ner) {
                    if (StringUtils.equalsIgnoreCase(nerr.substring(nerr.indexOf(" ") + 1, nerr.length() - 1), s.surfaceForm())) {//CONTAINS AND EQUALS AYRI AYRI CONTAINS-> 2LI OZEL
                        System.out.println("equals girdi");
                        list.remove(list.size() - 1);
                        list.add(nerr.substring(1, nerr.indexOf(" ")));
                    } else if (StringUtils.containsIgnoreCase(nerr.substring(nerr.indexOf(" ") + 1, nerr.length() - 1), s.surfaceForm())) {//CONTAINS AND EQUALS AYRI AYRI CONTAINS-> 2LI OZEL
                        System.out.println("contains girdi");
                        if (!temp.trim().equals(s.surfaceForm()))
                            temp += s.surfaceForm() + " ";
                        if (StringUtils.equalsIgnoreCase(temp.trim(), nerr.substring(nerr.indexOf(" ") + 1, nerr.length() - 1))) {
                            System.out.println("hebele");
                            System.out.println(Arrays.asList(indexess));
                            System.out.println(count);
                            if (ArrayUtils.contains(indexess, Integer.toString(count)))
                                hasTamlamaInNer = true;
                            for (int i = StringUtils.countMatches(temp.trim(), " "); i > -1; i--)
                                list.remove(list.size() - 1);
                            list.add(nerr.substring(1, nerr.indexOf(" ")));
                            temp = "";
                        }
                        System.out.println("temp: " + temp);
                        System.out.println("nerr: " + nerr.substring(nerr.indexOf(" ") + 1, nerr.length() - 1));
                    }
                }
                if (s.getPos().getStringForm() == "Verb") {
                    indexVerb = count;
                }
                if (s.getPos().getStringForm() == "Punc") {
                    hasPunc = true;
                }
                if (s.getPos().getStringForm() == "Verb" && !hasPNoun) {//Pron,Pers suan verb e baiyor tum cumlede aramasi lazim
                    switch (getPersonTag(s.getMorphemes())) {
                        case "A1sg"://TurkishMorphotactics.a1sg.id.toString()(constant expr required)
                            list.add(0, "Ben:" + PrimaryPos.Pronoun.shortForm);
                            break;
                        case "A2sg":
                            list.add(0, "Sen:" + PrimaryPos.Pronoun.shortForm);
                            break;
                        case "A3sg":
                            list.add(0, "O:" + PrimaryPos.Pronoun.shortForm);
                            break;
                        case "A1pl":
                            list.add(0, "Biz:" + PrimaryPos.Pronoun.shortForm);
                            break;
                        case "A2pl":
                            list.add(0, "Siz:" + PrimaryPos.Pronoun.shortForm);
                            break;
                        case "A3pl":
                            list.add(0, "Onlar:" + PrimaryPos.Pronoun.shortForm);
                            break;
                        case "":
                            break;
                        default:
                            break;
                    }
                }
                /*if (!ner.isEmpty()) {
                    String search = "";
                    String target = s.surfaceForm();
                    for (String sss : ner) {
                        search = sss.substring(sss.indexOf(" "), sss.length() - 1).replace("\'", "");
                        if (target.trim().equalsIgnoreCase(search.trim())) {
                            list.remove(list.size() - 1);
                            list.add(sss.substring(1, sss.indexOf(" ")));
                        }
                    }
                }*/
                switch (sentenceType) {
                    case Tamlama:
                        break;
                    case Plural:
                        break;
                    case Fiilimsi:
                        break;
                    case GizliOzne:
                        break;
                    default:
                        break;
                }
                count++;
            }
            if (hasTamlama && !hasPlural && !hasTamlamaInNer) {
                replace = getTamlama(str);
                System.out.println(replace);
                String[] split = replace.split("-");
                String[] indexes = split[0].split(",");
                list.set(Integer.parseInt(indexes[0]), "T-" + split[1]);
                for (int i = 1; i < indexes.length; i++) {
                    list.remove(Integer.parseInt(indexes[i]));
                }
                //list.add(0, "T");
                hasTamlama = false;
            }
            if (hasPlural && !hasTamlama) {
                replace = getPluralWord(str);
                String[] split = replace.split(",");
                int index = Integer.parseInt(split[0]);
                list.set(index, "P-" + split[1]);
                //list.add(0, "P");
                hasPlural = false;
            }
            //TODO this
            if (hasPlural && hasTamlama) {//pron elenıyor onu kontrol et
                replace = getTamlama(str);
                System.out.println(replace);
                String[] split = replace.split("-");
                String[] tamlamaIndexes = split[0].split(",");
                replaceCogul = getPluralWord(str);
                System.out.println("GIRDI");
                String[] split2 = replaceCogul.split(",");
                if (list.get(0).contains("Pron"))
                    for (int k = 0; k < tamlamaIndexes.length; k++) {
                        tamlamaIndexes[k] = Integer.toString(Integer.parseInt(tamlamaIndexes[k]) + 1);
                    }
                list.set(Integer.parseInt(tamlamaIndexes[0]), "T-" + split[1]);
                for (int i = 1; i < tamlamaIndexes.length; i++) {
                    list.remove(Integer.parseInt(tamlamaIndexes[i]));
                    removed++;
                }
                if (!ContainsAny(tamlamaIndexes, split2)) {
                    int index = Integer.parseInt(split2[0]);
                    if (list.get(0).contains("Pron"))
                        index++;
                    if (Integer.parseInt(tamlamaIndexes[0]) < Integer.parseInt(split2[0])) {
                        list.set(index - removed, "P-" + split2[1]);
                        System.out.println("KUCUK");
                    } else {
                        list.set(index, "P-" + split2[1]);
                    }
                }
                hasTamlama = false;
                hasPlural = false;
                //plural tamlama harici biyerdeyse plurali al stringe tamlama listine ekle
                //else tamlama listi al sadece
            }

            System.out.println(originalSentence.trim());
            System.out.println(ner);
            System.out.println(list);
            out.add(list);
        }
        ConvertPostag(out);
        return out;


        //SelectDB(out, sentences);//3.parametre eklenebilir special cumleler(tamlama,cogul vs.)
        //0 -> original sentence,1->lemmazation
        //normal cumleler + tamlamali,cogullu vs cumleler olabilir
    }

    public static List<String> ConvertPostag(List<List<String>> zemberekPos) {
        String pos = "";
        String temp = "";
        List<String> list = new ArrayList<String>();

        for (List<String> strList : zemberekPos) {
            for (String s : strList) {
                if (s.contains("+") && !s.equals("+")) {//tamlama case
                    String[] split = s.split("\\+");
                    for (String str : split) {
                        pos = str.substring(str.indexOf(":") + 1);
                        for (WordPos wordPos : WordPos.values()) {
                            try {
                                if (wordPos.name().equals(pos)) {//== operator avoids null,equals throw nullpointerexception
                                    pos = wordPos.getwordnetForm();
                                    break;
                                }
                            } catch (NullPointerException np) {
                            }
                        }
                        temp += str.substring(0, str.indexOf(":") + 1) + pos + "+";
                    }
                    list.add(temp.substring(0, temp.length() - 1));
                    temp = "";
                    continue;
                }
                pos = s.substring(s.indexOf(":") + 1);
                if (pos.trim().equalsIgnoreCase("Punc")) {
                    list.add(s.substring(0, s.indexOf(":")));
                    continue;
                }
                for (WordPos wordPos : WordPos.values()) {
                    try {
                        if (wordPos.name().equals(pos.replaceAll("[^a-zA-Z ]", ""))) {//== operator avoids null,equals throw nullpointerexception //substrıng yap
                            pos = wordPos.getwordnetForm();
                            break;
                        }
                    } catch (NullPointerException np) {
                    }
                }
                list.add(s.substring(0, s.indexOf(":") + 1) + pos);
            }
            System.out.println(list);
            //SelectDB(list);
        }
        return list;
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

    static public String getTamlama(String sentence) {
        List<String> list = new ArrayList<String>();
        String temp = "";
        String temp2 = "";
        String str = "";
        String tag = "";
        int count = 0;
        boolean isPrevNoun = false;
        List<WordAnalysis> analysis = morphology.analyzeSentence(sentence);
        SentenceAnalysis disambiguate = morphology.disambiguate(sentence, analysis);
        for (SingleAnalysis s : disambiguate.bestAnalysis()) {
            list.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));
            temp = s.formatLexical().substring(s.formatLexical().indexOf(":") + 1, s.formatLexical().indexOf("]"));
            if (temp.contains("Noun") && s.getEnding().isEmpty()) {
                isPrevNoun = true;
                temp2 = s.formatLexical().substring(1, s.formatLexical().indexOf("]"));
            }
            if (temp.contains("Noun") && ContainsPossession(s.getMorphemes()) && isPrevNoun) {
                temp2 = temp2 + "+" + s.formatLexical().substring(1, s.formatLexical().indexOf("]"));
                str = Integer.toString(count - 1) + "," + Integer.toString(count) + "-" + temp2;
                tag = "T-" + temp2;
                list.remove(list.size() - 1);
                list.remove(list.size() - 1);
                list.add(tag);
                isPrevNoun = false;
                temp2 = "";
            }
            count++;
        }
        //check if tamlama exists
        System.out.println(list);
        for (String s : list) {
            if (s.indexOf("T-") > -1) {
                for (String strr : s.substring(2).split("\\+"))
                    System.out.println(strr.substring(0, strr.indexOf(":")));
            }
        }


        return str;
    }

    static public Morpheme getPersonTag(String sentence) {
        Morpheme m = TurkishMorphotactics.zero;
        List<String> list = new ArrayList<String>();

        List<WordAnalysis> analysis = morphology.analyzeSentence(sentence);
        SentenceAnalysis disambiguate = morphology.disambiguate(sentence, analysis);

        for (SingleAnalysis s : disambiguate.bestAnalysis()) {
            list.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));
            if (s.formatLexical().contains("Pron") || s.formatLexical().contains("Noun,Prop")) {//Pron,Pers [biz:Pron,Pers]
                return null;
            }
            if (s.getPos().getStringForm() == "Verb") {
                System.out.println(s.formatLong());
                //System.out.println(TurkishMorphotactics.getAllMorphemes().toString());
                switch (getPersonTag(s.getMorphemes())) {
                    case "A1sg"://TurkishMorphotactics.a1sg.id.toString()(constant expr required)
                        list.add(0, "Ben:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "A2sg":
                        list.add(0, "Sen:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "A3sg":
                        list.add(0, "O:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "A1pl":
                        list.add(0, "Biz:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "A2pl":
                        list.add(0, "Siz:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "A3pl":
                        list.add(0, "Onlar:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "":
                        break;
                    default:
                        break;
                }
            }
        }
        System.out.println(list);
        return m;
    }

    static public String getGizliOzne(TurkishMorphotactics personTag) {
        String go = "";
        List<String> list = new ArrayList<String>();
        String temp = "";
        String temp2 = "";
        String str = "";
        int count = 0;
        boolean isPrevNoun = false;
        if (personTag.equals(TurkishMorphotactics.a1sg)) {

        }
        return go;
    }

    static public String getPersonTag(List<Morpheme> morphList) {
        String posTag = "";
        String[] personTag = {"A1sg", "A2sg", "A3sg", "A1pl", "A2pl", "A3pl"};

        for (int i = 0; i < personTag.length; i++) {
            for (int j = 0; j < morphList.size(); j++) {
                if (personTag[i] == morphList.get(j).id) {
                    posTag = personTag[i];
                    return posTag;
                }
            }
        }
        return posTag;
    }//morphList yerine String yap

    static public boolean ContainsAny(String[] search, String[] target) {
        for (int i = 0; i < search.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (search[i] == target[j])
                    return true;
            }
        }
        return false;
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
    static public String getPluralWord(String sentence) {
        String plural = "";
        String pluralTag = "";
        List<String> list = new ArrayList<String>();
        int count = 0;

        List<WordAnalysis> analysis = morphology.analyzeSentence(sentence);
        SentenceAnalysis disambiguate = morphology.disambiguate(sentence, analysis);

        for (SingleAnalysis s : disambiguate.bestAnalysis()) {
            System.out.println(s.formatLong());
            list.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));
            System.out.println(s.getPos().getStringForm());
            if (ContainsPlural(s.getMorphemes()) && !s.getPos().getStringForm().equals("Verb")) {
                pluralTag = getPluralTag(s.getMorphemes());
                String[] splits = StringUtils.split(s.formatLong().substring(s.formatLong().indexOf(' ') + 1), '+');
                StringBuilder strBuild = new StringBuilder();
                strBuild.append(splits[0].substring(0, splits[0].indexOf(":")));
                for (String str : splits) {
                    if (str.contains(pluralTag) && str.contains(":")) {
                        String a = str.substring(0, str.indexOf(":") + 1) + s.formatLexical().substring(s.formatLexical().indexOf(":") + 1, s.formatLexical().indexOf("]"));
                        strBuild.append(a);
                        break;
                    }
                }
                strBuild.append("+" + s.formatLexical().substring(1, s.formatLexical().indexOf(":")) + ":" + s.formatLexical().substring(s.formatLexical().indexOf(":") + 1, s.formatLexical().indexOf("]")));
                strBuild.insert(0, count + ",");
                plural = strBuild.toString();
                list.remove(list.size() - 1);
                list.add(plural);
                //System.out.println(plural);
                //return plural;
            }
            if (s.getPos().getStringForm() == "Conj") {
                list.remove(list.size() - 1);
                list.add("+");
            }
            count++;
        }
        //check if pluralexists
        list.add(0, "P");
        System.out.println(list);
        System.out.println(plural);
        return plural;
    }

    static public boolean ContainsPossession(List<Morpheme> morphList) {
        if (getPossessionTag(morphList) == "")
            return false;
        return true;
    }

}
