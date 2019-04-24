// need to import xercesimpl.jar(most used parser which is also used in WordNet) manually
// there are conflictions because they do not publish official maven release
// check more info here : https://stackoverflow.com/questions/11677572/dealing-with-xerces-hell-in-java-maven

//FIXME
// - SyntaxAnalysis
// - turkishmorptactics.a1pl vs
// - GET SPECIAL SENTENCE CONDITIONS IN ONE FUNCTION.
// - FUNCTION RETURN TYPE LARI DUZELT.
// - STOPWORDS FILTER.
// - function return type list -> String(containing replaced part and index(list))
// - DEVRIK CUMLE -> verbi en sona at ( MAKE LINKEDLIST MAYBE ? -> YOU CAN TRAVERSE THE WORDS CHECK IF THE VERB IS IN THE END ? )
// - PLURAL AND TAMLAMA AT THE SAME TIME
// - NERTEST GELISTIR + HER CUMLE NERE GIRMESIN
// - P-kitaplar:NOUN+kitap:NOUN
// - COGUL SUAN SADECE CUMLEDE 1 TANE COGUL VARSA CALISIYO DUZELT

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
        Unk("NOUN");

        private String WordnetForm;

        public String getwordnetForm() {
            return this.WordnetForm;
        }

        private WordPos(String wordnetForm) {
            this.WordnetForm = wordnetForm;
        }
    }

    static String URL = "jdbc:mysql://localhost/test?user=&pass=&useUnicode=true&characterEncoding=UTF-8";
    static String USER = "root";
    static String PASS = "hakki1996";
    static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static String PICTO_FOLDER_PATH = "C:\\Users\\haQQi\\Documents\\Projects\\T2P\\src\\main\\resources\\Pictograms";
    static String TABLE_NAME = "text2pic";
    static String[] edats = {"gibi", "kadar", "için", "dolayı", "ötürü", "yalnız", "ancak", "tek", "üzere", "sanki", "diye"};//sadece //[sabah:Noun,Time] sabah:Noun+A3sg+a:Dat [doğru:Adj] doğru:Adj [sabah:Noun,Time] sabah:Noun+A3sg+a:Dat [karşı:Postp,PCDat] karşı:Postp //dat+adj or dat+adv delete it.
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

        Scanner scan = new Scanner(System.in);
        System.out.println("Input : ");
        String input = scan.nextLine();
        Input2Picto(input);

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

    //converts posTags Adj->ADJECTIVE
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

    static public List<String> SelectDB(List<String> text) {
        int i, j, z;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        List<String> pictoList = new ArrayList<>();
        List<String> pictoListTemp = new ArrayList<>();
        List<String> synonymList = new ArrayList<>();
        List<String> synonymListTemp = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            //execute
            stmt = conn.createStatement();

//            -Kelimeler ve POSları arrayda sırayla gelmiştir.
//            -Her lemma için bir synonym, hypernym, hyponym arama fonksiyonu gerekiyor.
//                -En düşük sense synset'inden 5 fazlasına kadar.
//                -
//            -Kalan nounların, cümle içi noun ve verb'lerlen birleşip aranması.
//            -0. eleman P- ise çoğul T- ise tamlama
//            -



            for (String templateText : text) {
                if (templateText.equals(",") || templateText.equals("+")) {
                    pictoList.add("artı.png");
                } else if (templateText.equals(".")) {
                    pictoList.add("nokta.png");
                } else if (templateText.substring(0, 2).equals("T-")) {


                    //                    rs = stmt.executeQuery(queryBuilder(templateText));
//                    if (!rs.next()) {
//                        continue;
//                    }
//                    do {
//                        System.out.println(rs.getString("A.pictoName"));
//                    }
//                    while (rs.next());
                } else {
                 /*
                 - İlk tekli gelen fiil ile eşleşecek
                 - Yoksa diğer kullanılmamış isimlerle
                 - Yoksa kendisi aranacak
                 - Bu aralarda lexial simp. kullanılacak.
                  */

                    synonymList.addAll(findLexialSynonym(removeLemmaPos(templateText, "pos"), Pos.valueOf(removeLemmaPos(templateText, "lemma"))));
                    for (i = 0; i < synonymList.size(); i++) {
                        synonymListTemp.add(synonymList.get(i));
                        synonymListTemp.set(0, removeLemmaPos(synonymListTemp.get(0), "pos"));
                        String s = queryBuilder(synonymListTemp).get(0);//tek lemma gittigi icin ilk index alınsın

                        System.out.println(s);
                        rs = stmt.executeQuery(s);

                        if (!rs.next()) {
                            synonymListTemp.clear();
                            continue;
                        }
                        do {
                            pictoListTemp.add(rs.getString("A.pictoName"));
                            synonymListTemp.clear();
                            i = 100;
                        }
                        while (rs.next());
                    }
                    pictoList.addAll(selectPicto(pictoListTemp));
                    synonymList.clear();
                    pictoListTemp.clear();
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
        /*
            Function = PictoSelectör(picto list)
            En basit pictogramı bul.
             */
        String[] counter;
        List<String> picto_Selected = new ArrayList<>();
        int i, j;
        for (i = 1; i < 6; i++) {
            for (j = 0; j < pictoList.size(); j++) {
                counter = pictoList.get(j).replace(".png", "").split("\\s");
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
//                for (j = 0; j < tempSynSet.getSynonym().literalSize(); j++) {
//                    dublucated = true;
//                    //[0-9]+   \\d+
//                    tempString = tempSynSet.getSynonym().getLiteral(j).toString().replaceAll("\\d+", "").trim()+":"+POStag;//+":"+WordPos
//                    for (k = 0; k < SYNONYM_SearchList.size(); k++) {
//                        if (SYNONYM_SearchList.get(k).equals(tempString)) {
//                            dublucated = false;
//                        }
//                    }
//                    if (dublucated == true && tempString != null) {
//                        SYNONYM_SearchList.add(tempString);
//                    }
//                }
        //                for (j = 0; j < tempSynSet.getSynonym().literalSize(); j++) {
//                    dublucated = true;
//                    //[0-9]+   \\d+
//                    tempString = tempSynSet.getSynonym().getLiteral(j).toString().replaceAll("\\d+", "").trim()+":"+POStag;//+":"+WordPos
//                    for (k = 0; k < SYNONYM_SearchList.size(); k++) {
//                        if (SYNONYM_SearchList.get(k).equals(tempString)) {
//                            dublucated = false;
//                        }
//                    }
//                    if (dublucated == true && tempString != null) {
//                        SYNONYM_SearchList.add(tempString);
//                    }
//                }

//            for (i = 0; i < orderedSynSet_List.size(); i++) {
//                printSynSet(orderedSynSet_List.get(i));
////            printSynSet(SynSetForTest);
//            }
//            for (i = 0; i < SynSetPrint.relationSize(); i++) {
//                Relation r = SynSetPrint.getRelation(i);
//                if (r instanceof SemanticRelation) {
//                    if (((SemanticRelation) r).getRelationType().equals(relationType)) {
//                        System.out.println("\tFind->Relation " + (i + 1) + ": " + SynSetPrint.getRelation(i));
//                        SynSetIDTemp = SynSetPrint.getRelation(i).getName();
//                        findRelationSynSet(wordNet.getSynSetWithId(SynSetIDTemp), relationType, wordNet);
//                    }
//                }
//            }


//        for (i = 0; i<SynSets.size();i++){
//            for(j = 0;j<SynSets.get(i).getSynonym().literalSize();j++){
//                //synonym_SearchList.add(SynSets.get(i).getSynonym().getLiteral(j));
//            }
//        }
        return SYNONYM_SearchList;
    }

    static public List<String> queryBuilder(List<String> ortakArayıcı) {
        int i, j;
        List<String> queries = new ArrayList<>();
        List<List<String>> combinations = new ArrayList<>();

        //String GROUPBY_QueryPart = " GROUP BY A.lemma"; //lemma gruplandirmasi dogru calismiyor
        //System.out.println(ortakArayıcı);

        /*
        - Bütün tamlamalar, falan hepsi tek tek array içinde gelsin
        - Query builder kombinasyon şansını üretip sırayla araye atıp yollasın.
         */
//        if (ortakArayıcı.substring(0, 2).equals("T-") || ortakArayıcı.substring(0, 2).equals("P-")) {
//            for (String splited : ortakArayıcı.substring(2).split("\\+")) {
//                tamlamaTemp.add(splited.substring(0, splited.indexOf(":")));
//            }
//        } else {
//            tamlamaTemp.add(ortakArayıcı.substring(0, ortakArayıcı.indexOf(":")));
//        }

        for (j = ortakArayıcı.size(); j > 0; j--) {
            combinations.addAll(generate(ortakArayıcı, j));
        }
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
        return queries;
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

    public static void helper(List<List<String>> combinations, String data[], int startPoint, List<String> lemmas, int index) {
        String[] datas = data.clone();
        if (index == datas.length) {
            List<String> combination = Arrays.asList(datas);
            combinations.add(combination);
        } else if (startPoint <= lemmas.size() - 1) {
            datas[index] = lemmas.get(startPoint);
            helper(combinations, datas, startPoint + 1, lemmas, index + 1);
            helper(combinations, datas, startPoint + 1, lemmas, index);
        }
    }

    public static List<List<String>> generate(List<String> lemmas, int r) {
        List<List<String>> combinations = new ArrayList<>();
        helper(combinations, new String[r], 0, lemmas, 0);
        return combinations;
    }
//
//    private static void helper(List<int[]> combinations, int data[], int start, int end, int index) {
//        if (index == data.length) {
//            int[] combination = data.clone();
//            combinations.add(combination);
//        } else if (start <= end) {
//            data[index] = start;
//            helper(combinations, data, start + 1, end, index + 1);
//            helper(combinations, data, start + 1, end, index);
//        }
//    }
//
//    public static List<int[]> generate(int n, int r) {
//        List<int[]> combinations = new ArrayList<>();
//        helper(combinations, new int[r], 0, n-1, 0);
//        return combinations;
//    }

    //    public static List<String> combinator(List<String> lemmas){
//        int i, j;
//        List<String> tempList = new ArrayList<>();
//        List<String> combinedList = new ArrayList<>();
//
//        // 1 2 3 | 1 3 | 1 2 | 2 3 | 1 | 2 | 3
//
//        for (i = lemmas.size(); i == 0 ; i--){
//            for (j = 0;j<i;j++)
//            tempList.add(lemmas.get(j));
//        }
//
//        return combinedList;
//    }
//    private static void multiply(List<List<String>> factors, List<String> current, List<List<String>> results) {
//        if (current.size() >= factors.size()) {
//            // Don't really need to make a deeper copy with String values
//            // but might as well in case we change types later.
//            List<String> result = new ArrayList<>();
//            for (String s : current) {
//                result.add(new String(s));
//            }
//            results.add(result);
//
//            return;
//        }
//
//        int currentIndex = current.size();
//        for (String s : factors.get(currentIndex)) {
//            current.add(s);
//            multiply(factors, current, results);
//            current.remove(currentIndex);
//        }
//    }
//
//    private static List<List<String>> multiply(List<List<String>> factors) {
//        List<List<String>> results = new ArrayList<>();
//        multiply(factors, new ArrayList<String>(), results);
//        return results;
//    }

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
                plural = getPluralWord(originals.get(i));
                if (plural != "") {
                    //list yap vs vs.
                }
                for (int j = 0; j < morphed.get(i).size(); j++) {//sentences
                    if (tamlamaFound && j == first) {
                        String sqlQuery_Select2 = "SELECT pictoName FROM " + TABLE_NAME + " WHERE pictoName = '" + tamlama.substring(tamlama.indexOf(":") + 1) + ".png" + "';";
                        rs = stmt.executeQuery(sqlQuery_Select2);
                        if (!rs.next()) {
                            j--;
                            tamlamaFound = false;
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

    //o kitabi ben bitirdim.//kitabini ben bitirdim-senin kitabini ben bitirdim
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
}
