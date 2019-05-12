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

import static WordNet.SemanticRelationType.*;

public class PictogramRetriever {
    static String URL = "jdbc:mysql://localhost/test?user=&pass=&useUnicode=true&characterEncoding=UTF-8";
    static String USER = "root";
    static String PASS = "hakki1996";
    static String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    static String TABLE_NAME = "text2pic";
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

        String text="T-baş:NOUN+uç:NOUN+lamba:NOUN";
        List<String> temp=new ArrayList<>();
        temp.add(text);
        System.out.println(SelectDB(temp));

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
//        List<String> test = new ArrayList<>();
//        test.add("P-metaller:NOUN+metal:NOUN");
//        System.out.println(findLexialSynonym("muz", Pos.NOUN));
//        System.out.println("picto: " + SelectDB(test));

        //Linker.linkerSynsetPictogram(Linker.listAllFiles("C:\\Users\\hakac\\Desktop\\sayılar"));
//        for (List<String> s : queryBuilder(test, 3)) {
//            System.out.println(s);
//        }
    }

    @SuppressWarnings("Duplicates")
    static public List<String> SelectDB(List<String> text) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        System.out.println("initial morphed list: "+text);

        List<String> pictoList = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            stmt = conn.createStatement();

            for (int x = 0; x < text.size(); x++) {
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
                } else if (templateText.contains("PERSON")) {
                    pictoList.add("person.png"+templateText.substring(templateText.indexOf(",")));
                } else if (templateText.contains("LOCATION")) {
                    pictoList.add("location.png"+templateText.substring(templateText.indexOf(",")));
                } else if (templateText.contains("ORGANIZATION")) {
                    pictoList.add("organization.png"+templateText.substring(templateText.indexOf(",")));
                } else if (templateText.equals("!")) {
                    pictoList.add("ünlem.png");
                }else if (templateText.equals("?")) {
                    pictoList.add("soru işareti.png");}

                /*else if (templateText.substring(0, 2).equals("P-")) {
                    List<String> returnP = new ArrayList<>();
                    for (String splited : templateText.substring(2).split("\\+")) {
                        returnP.add(splited);
                        splitTamlamaListTemp.add(removeLemmaPos(splited, "pos"));
                    }
                    for (String s1 : splitTamlamaListTemp) {
                        synonymListTemp.clear();
                        synonymListTemp.add(0, s1);//for list cast
                        for (List<String> s2 : queryBuilder(synonymListTemp, 1)) {
                            for (int i = 0; i < s2.size(); i++) {
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
//                    if (!found) {
//                        index = x;
//                        text.add(index + 1, returnP.get(1));
//                    }
                    if (!pictoListTemp.isEmpty()) {
                        pictoList.addAll(selectPicto(pictoListTemp));
                    }
                } */
                else if (templateText.substring(0, 2).equals("T-")) {
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
                            index = x;
                            for (String s : returnList) {
                                text.add(index + 1, s);
                                index++;
                            }
                            break;
                        }
                        for (List<String> s1 : generate2(tempSynonyms)) {
                            System.out.println(s1);
                            pictoListsTemp.clear();
                            for (List<String> s2 : queryBuilder(s1, i)) {
                                System.out.println(s2);
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
                                    pictoListsTemp.add(pictoListTemp);
                                }
                                if (found) {
                                    break;
                                }
                            }
                            if (found) {
                                break;
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                    if (!pictoListsTemp.isEmpty()) {
                        for (List<String> s : pictoListsTemp) {
                            pictoList.addAll(selectPicto(s));
                        }
                    }
                } else {
                    synonymList.addAll(findLexialSynonym(removeLemmaPos(templateText, "pos"), Pos.valueOf(removeLemmaPos(templateText, "lemma"))));
                    System.out.println(synonymList);
                    for (String s1 : synonymList) {
                        if (found) {
                            break;
                        }
                        synonymListTemp.clear();
                        synonymListTemp.add(0, removeLemmaPos(s1, "pos"));
                        for (List<String> s2 : queryBuilder(synonymListTemp, synonymListTemp.size())) {
                            System.out.println(s2);
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
                        System.out.println(pictoList);
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
        System.out.println("Last morphed list:"+text);
        System.out.println("Last morphed list size:"+text.size());
        System.out.println("Last picto unfound:"+(text.size()-pictoList.size()));
        return pictoList;
    }

    private static List<String> selectPicto(List<String> pictoList) {
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

    private static List<String> findLexialSynonym(String literal, Pos POStag) {
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

    private static List<List<String>> queryBuilder(List<String> ortakArayıcı, int wordCombineCount) {
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
                    FROM_QueryPart += " text2pic " + Data_objectName[i];
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

    private static void helper(List<List<List<String>>> combinationss, String data[], int startPoint, List<String> lemmas, int index) {
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

    private static List<List<List<String>>> generate(List<String> lemmas, int r) {
        List<List<List<String>>> combinationss = new ArrayList<>();
        helper(combinationss, new String[r], 0, lemmas, 0);
        return combinationss;
    }

    private static void helper2(List<List<String>> lists, List<String> temp, List<List<String>> result, int index) {
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

    private static List<List<String>> generate2(List<List<String>> synonymLists) {
        List<List<String>> result = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        helper2(synonymLists, temp, result, 0);
        return result;
    }

    private static String removeLemmaPos(String morfic, String part) {
        if (part.equals("pos")) {
            return morfic.substring(0, morfic.indexOf(":"));
        } else if (part.equals("lemma")) {
            return morfic.substring(morfic.indexOf(":") + 1);
        } else {
            return morfic;
        }
    }
}
