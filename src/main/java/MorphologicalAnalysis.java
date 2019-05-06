import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;

import zemberek.core.turkish.PrimaryPos;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.morphotactics.Morpheme;
import zemberek.tokenization.TurkishSentenceExtractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.*;

//FIXME
// - Show results with and without SyntaxAnalysis
// - fix getTamlama(look getplural)
// - conll converter if(Noun) -> if(!Verb)

@SuppressWarnings("Duplicates")
public class MorphologicalAnalysis {

    private static final String[] edats = {"gibi", "kadar", "için", "dolayı", "ötürü", "yalnız", "ancak", "tek", "üzere", "sanki", "diye", "daha", "bir","bu"};//sadece //[sabah:Noun,Time] sabah:Noun+A3sg+a:Dat [doğru:Adj] doğru:Adj [sabah:Noun,Time] sabah:Noun+A3sg+a:Dat [karşı:Postp,PCDat] karşı:Postp //dat+adj or dat+adv delete it.

    public static TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
    private static TurkishSentenceExtractor sentenceExtractor = TurkishSentenceExtractor.DEFAULT;

    public static void main(String Args[]) throws IOException {

        //Ayşe basketbolu, Mehmet futbolu sever.
            Analyze("Beraber yürüdük biz bu yollarda.");//Ahmet enerji içeceğini içti.//Ahmet gazozun kapağını açtı.
    }

    private static List<String> SentenceExtractor(String input) {
        List<String> sentences = sentenceExtractor.fromParagraph(input);
        return sentences;
    }

    //converts posTags Zemberek->WordNet(Adj->ADJECTIVE)
    private static List<String> ConvertPostag(List<List<String>> zemberekPos) {
        String pos = "";
        String temp = "";
        List<String> list = new ArrayList<String>();

        for (
                List<String> strList : zemberekPos) {
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
            System.out.println("Updated morphedList: " + list);
            //SelectDB(list);
        }
        return list;
    }

    private static List<String> SpecialConditionTagger(List<SingleAnalysis> bestAnalysis, String sentence) throws IOException {
        List<String> morphedList = new ArrayList<>();
        List<String> ner = new ArrayList<String>();
        String[] tamlamaIndexes = new String[10];
        String[] tamlamaSplit = new String[10];

        String originalSentence = "";
        String nertemp = "";

        int index = 0;

        boolean isPronounAdded = false;
        boolean hasPlural = false;
        boolean hasTamlama = false;
        boolean hasPunc = false;
        boolean hasSubject = false;
        boolean hasTamlamaInNer = false;

        ner = NER.NER(sentence);
        for (int i = 0; i < ner.size(); i++) {
            ner.set(i, ner.get(i).replaceAll("\'", ""));//replaceAll("[^a-zA-Z ]", "").toLowerCase());
        }

        for (int i = 0; i < Convert2ConllFormat(bestAnalysis).length; i++) {
            System.out.println(Convert2ConllFormat(bestAnalysis)[i]);
        }

        for (String potentialSubject : ner) {
            if (potentialSubject.contains("PERSON"))
                hasSubject = true;
        }

        if (hasSubject(DependencyParser.Parse2(Convert2ConllFormat(bestAnalysis))) != "") {
            hasSubject = true;
        }

        for (SingleAnalysis s : bestAnalysis) {
            //System.out.println(s.formatMorphemesLexical());
            originalSentence += s.surfaceForm() + " ";

            if (Arrays.asList(edats).contains(s.surfaceForm())) continue; //duzelt s.formatlexical
            if (s.getPos().getStringForm() == "Conj") {
                morphedList.add("+");
                continue;
            }

            //tartisilir
            /*if (s.formatLexical().contains("Pron") || s.formatLexical().contains("Noun,Prop")) {
                hasSubject = true;
            }*/

            //get plural
            if (ContainsPlural(s.getMorphemes()) && !s.getPos().shortForm.equalsIgnoreCase("verb")) {
                hasPlural = true;
            }

            //get tamlama
            if (ContainsPossession(s.getMorphemes())) {
                if (getTamlama(bestAnalysis) != "") {
                    hasTamlama = true;
                    tamlamaSplit = getTamlama(bestAnalysis).split("-");
                    tamlamaIndexes = tamlamaSplit[0].split(",");
                }
            }

            //for adding punctuation at the end if it lacks one
            if (s.getPos().getStringForm() == "Punc") {
                hasPunc = true;
            }

            morphedList.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));

            //ner process
            if (!ner.isEmpty()) {
                for (String nerr : ner) {
                    //tek kelimeli Named Entities
                    if (StringUtils.equalsIgnoreCase(nerr.substring(nerr.indexOf(" ") + 1, nerr.length() - 1), s.surfaceForm())) {
                        morphedList.remove(morphedList.size() - 1);
                        morphedList.add(nerr.substring(1, nerr.indexOf(" ")));
                    } else if (StringUtils.containsIgnoreCase(nerr.substring(nerr.indexOf(" ") + 1, nerr.length() - 1), s.surfaceForm())) {
                        if (!nertemp.trim().equals(s.surfaceForm()))
                            nertemp += s.surfaceForm() + " ";
                        if (StringUtils.equalsIgnoreCase(nertemp.trim(), nerr.substring(nerr.indexOf(" ") + 1, nerr.length() - 1))) {
                            if (ArrayUtils.contains(tamlamaIndexes, Integer.toString(index)))
                                hasTamlamaInNer = true;
                            for (int i = StringUtils.countMatches(nertemp.trim(), " "); i > -1; i--)
                                morphedList.remove(morphedList.size() - 1);
                            morphedList.add(nerr.substring(1, nerr.indexOf(" ")));
                            nertemp = "";
                        }
                    }
                }
            }

            //if no subject found in both syntax analysis and NER add pronouns
            if (s.getPos().getStringForm() == "Verb" && !hasSubject) {
                isPronounAdded = true;
                switch (getPersonTag(s.getMorphemes())) {
                    case "A1sg"://TurkishMorphotactics.a1sg.id.toString()(constant expr required)
                        morphedList.add(0, "Ben:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "A2sg":
                        morphedList.add(0, "Sen:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "A3sg":
                        morphedList.add(0, "O:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "A1pl":
                        morphedList.add(0, "Biz:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "A2pl":
                        morphedList.add(0, "Siz:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "A3pl":
                        morphedList.add(0, "Onlar:" + PrimaryPos.Pronoun.shortForm);
                        break;
                    case "":
                        break;
                    default:
                        break;
                }
            }
            index++;
        }

        //update the list
        if (hasTamlama && !hasPlural && !hasTamlamaInNer) {
            tamlamaSplit = getTamlama(bestAnalysis).split("-");
            tamlamaIndexes = tamlamaSplit[0].split(",");
            morphedList.set(Integer.parseInt(tamlamaIndexes[0]), "T-" + tamlamaSplit[1]);
            for (int i = 1; i < tamlamaIndexes.length; i++) {
                morphedList.remove(Integer.parseInt(tamlamaIndexes[i]));
            }
            hasTamlama = false;
        }

        //update the list
        if (hasPlural && !hasTamlama && ner.isEmpty()) {
            for (String plural : getPluralWord(bestAnalysis)) {
                String[] pluralSplit = plural.split(",");
                int pluralIndex = Integer.parseInt(pluralSplit[0]);
                if (isPronounAdded) {
                    morphedList.set(pluralIndex + 1, "P-" + pluralSplit[1]);
                } else {
                    morphedList.set(pluralIndex, "P-" + pluralSplit[1]);
                }
            }
        }

        //update the list
        if (hasPlural && hasTamlama) {
            //tamlama
            if (morphedList.get(0).contains("Pron"))
                for (int k = 0; k < tamlamaIndexes.length; k++) {
                    tamlamaIndexes[k] = Integer.toString(Integer.parseInt(tamlamaIndexes[k]) + 1);
                }
            morphedList.set(Integer.parseInt(tamlamaIndexes[0]), "T-" + tamlamaSplit[1]);
            int removed = 0;
            for (int i = 1; i < tamlamaIndexes.length; i++) {
                morphedList.remove(Integer.parseInt(tamlamaIndexes[i]));
                removed++;
            }
            //cogul
            for (String plural : getPluralWord(bestAnalysis)) {
                String[] pluralSplit = plural.split(",");
                if (!ContainsAny(tamlamaIndexes, pluralSplit)) {
                    int pluralIndex = Integer.parseInt(pluralSplit[0]);
                    if (morphedList.get(0).contains("Pron"))
                        pluralIndex++;
                    if (Integer.parseInt(tamlamaIndexes[0]) < Integer.parseInt(pluralSplit[0])) {
                        morphedList.set(pluralIndex - removed, "P-" + pluralSplit[1]);
                    } else {
                        morphedList.set(pluralIndex, "P-" + pluralSplit[1]);
                    }
                }
            }
        }
        System.out.println("input: " + originalSentence.trim());
        System.out.println("NER: " + ((!ner.isEmpty()) ? ner : "is empty"));
        System.out.println("morphedList: " + morphedList);
        return morphedList;
    }

    private static String hasSubject(ConcurrentDependencyGraph outputGraph) {
        String subject = "";

        for (int i = 1; i < outputGraph.nTokenNodes(); i++) {
            if (outputGraph.getTokenNode(i).getLabel("DEPREL").equals("SUBJECT")) {
                subject = outputGraph.getTokenNode(i).getLabel("LEMMA");
                return subject;
            }
        }
        return subject;
    }

    public static List<String> Analyze(String userInput) throws IOException {
        List<List<String>> morphedSentences = new ArrayList<>();
        List<String> sentences = SentenceExtractor(userInput);
        List<String> morphedList = new ArrayList<>();

        for (String sentence : sentences) {
            morphedSentences.add(AnalyzeSentence(sentence));
        }

        morphedList = ConvertPostag(morphedSentences);
        return morphedList;
    }

    private static List<String> AnalyzeSentence(String sentence) throws IOException {
        List<String> morphedList = new ArrayList<>();

        String originalSentence = "";

        int index = 0;

        List<WordAnalysis> analysis = morphology.analyzeSentence(sentence);
        SentenceAnalysis disambiguation = morphology.disambiguate(sentence, analysis);
        List<SingleAnalysis> bestAnalysis = disambiguation.bestAnalysis();


        if (ContainsSpecialConditions(bestAnalysis))
            return SpecialConditionTagger(bestAnalysis, sentence);

        for (SingleAnalysis s : bestAnalysis) {
            originalSentence += s.surfaceForm() + " ";

            morphedList.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));

            index++;
        }
        return morphedList;
    }

    //if(Noun) -> if(!Verb)
    private static String[] Convert2ConllFormat(List<SingleAnalysis> bestAnalysis) {
        List<String> tokens = new ArrayList<>();
        int index = 1;
        StringBuilder sb = new StringBuilder();

        for (SingleAnalysis s : bestAnalysis) {
            String tokenizedWord = "";
            String morphed = s.formatLong();
            String lexicalMorphes = s.formatMorphemesLexical();
            String surface = s.surfaceForm();

            //getting rid of brackets
            morphed = morphed.replace("[", "");
            morphed = morphed.replace("]", "");


            String lexForm = morphed.split("\\s+")[0];
            String suffixes = morphed.split("\\s+")[1];

            //System.out.println(lexForm + " " + lexicalMorphes + " " + surface);

            //fiilimsi
            if (lexicalMorphes.contains("|") && lexicalMorphes.contains("Verb")) {
                int ID = index;
                String FORM = "_";
                String LEMMA = StringUtils.split(suffixes, ":")[0];
                String UPOS = lexForm.split(":")[1];
                String XPOS = "";
                String FEATS = "";
                if (suffixes.contains("+")) {
                    XPOS = suffixes.split(":")[1].substring(0, suffixes.split(":")[1].indexOf("+"));
                    FEATS = "Pos|" + lexicalMorphes.split("\\|")[0].substring(lexicalMorphes.split("\\|")[0].indexOf("+") + 1);
                } else {
                    //System.out.println( suffixes.split(":")[1].substring(0, suffixes.split(":")[1].indexOf("|")));
                    XPOS = suffixes.split(":")[1].substring(0, suffixes.split(":")[1].indexOf("|"));
                    FEATS = "Pos";
                }
                //String FEATS = "Pos|" + lexicalMorphes.split("\\|")[0].substring(lexicalMorphes.split("\\|")[0].indexOf("+") + 1);
                sb.append(ID + "\t" + FORM + "\t" + LEMMA + "\t" + UPOS + "\t" + XPOS + "\t" + FEATS);
                tokenizedWord = sb.toString();

                sb.delete(0, sb.length());

                tokens.add(tokenizedWord);
                index++;

                ID = index;
                FORM = surface;
                LEMMA = "_";
                UPOS = lexicalMorphes.substring(lexicalMorphes.indexOf("→") + 1);
                XPOS = UPOS;
                FEATS = lexicalMorphes.split("\\|")[1].substring(0, lexicalMorphes.split("\\|")[1].indexOf("→"));
                sb.append(ID + "\t" + FORM + "\t" + LEMMA + "\t" + UPOS + "\t" + XPOS + "\t" + FEATS);
                tokenizedWord = sb.toString();
                tokens.add(tokenizedWord);

                sb.delete(0, sb.length());

                index++;
                continue;
                //ilkinin
                //id index+1
                //form _
                //lemma default
                //upos def
                //xpos def
                //feats lexical.split("|")[0].substr("indexof(+)")
                //2.
                //id index+2
                //form surface
                //lemma _
                //upos lex.substr(→)
                //xpos upos
                //feats lexical.split("|")[1].substr(0,"indexof(→)")
            }

            int ID = index;
            String FORM = surface;
            String LEMMA = "";
            if (lexForm.contains("Noun"))
                LEMMA = StringUtils.split(lexForm, ":")[0];
            else
                LEMMA = StringUtils.split(suffixes, ":")[0];
            String UPOS=lexForm.split(":")[1];
            String XPOS = "";
            String FEATS = "";
            if (suffixes.contains("+")) {
                XPOS = suffixes.split(":")[1].substring(0, suffixes.split(":")[1].indexOf("+"));
            } else {
                XPOS = suffixes.split(":")[1];
            }
            if(lexForm.contains("Prop")){
                FORM=LEMMA;
                UPOS=lexForm.split(":")[1].substring(0,lexForm.split(":")[1].indexOf(","));
                XPOS=lexForm.split(":")[1].substring(lexForm.split(":")[1].indexOf(",")+1);
            }
            if (lexicalMorphes.contains("+")) {
                FEATS = lexicalMorphes.substring(lexicalMorphes.indexOf("+") + 1).replaceAll("\\+", "|");
            } else {
                FEATS = "_";
            }
            if (UPOS.equals("Verb")) {
                FEATS = "Pos|" + lexicalMorphes;
            }

            sb.append(ID + "\t" + FORM + "\t" + LEMMA + "\t" + UPOS + "\t" + XPOS + "\t" + FEATS);
            tokenizedWord = sb.toString();
            sb.delete(0, sb.length());
            //System.out.println(tokenizedWord);
            tokens.add(tokenizedWord);
            index++;
        }
        return tokens.toArray(new String[0]);
    }

    private static boolean ContainsSpecialConditions(List<SingleAnalysis> bestAnalysis) {
        boolean hasPNoun = false;
        for (SingleAnalysis s : bestAnalysis) {
            //if (s.formatLexical().contains("Pron") || s.formatLexical().contains("Noun,Prop")) hasPNoun = true;
            if (s.formatLexical().contains("Pron") || s.formatLexical().contains("Noun,Prop")) return true;

            if (ContainsPlural(s.getMorphemes()) && !s.getPos().shortForm.equalsIgnoreCase("verb")) return true;

            if (ContainsPossession(s.getMorphemes())) {
                if (getTamlama(bestAnalysis) != "") return true;
            }
            if (s.getPos().getStringForm() == "Verb" && !hasPNoun) return true;
            if (Arrays.asList(edats).contains(s.surfaceForm())) return true; //duzelt s.formatlexical
            if (s.getPos().getStringForm() == "Conj") return true;
        }
        return false;
    }

    private static boolean ContainsPossession(List<Morpheme> morphList) {
        if (getPossessionTag(morphList) == "")
            return false;
        return true;
    }

    private static String getPossessionTag(List<Morpheme> morphList) {
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

    //2+ isimli tamlama yoksa last int i kaldir
    private static String getTamlama(List<SingleAnalysis> bestAnalysis) {
        List<String> list = new ArrayList<String>();
        String temp = "";
        String temp2 = "";
        String str = "";
        String tag = "";
        int count = 0;
        boolean isPrevNoun = false;
        for (SingleAnalysis s : bestAnalysis) {
            list.add(s.formatLexical().substring(1, s.formatLexical().indexOf("]")));
            temp = s.formatLexical().substring(s.formatLexical().indexOf(":") + 1, s.formatLexical().indexOf("]"));
            if ((temp.contains("Noun") && s.getEnding().isEmpty() && !temp.contains("Prop")) || (temp.contains("Noun") && s.formatMorphemesLexical().contains("Gen") && !temp.contains("Prop"))) {
                isPrevNoun = true;
                temp2 = s.formatLexical().substring(1, s.formatLexical().indexOf("]"));
            }
            if (temp.contains("Noun") && ContainsPossession(s.getMorphemes()) && isPrevNoun && !temp.contains("Prop")) {
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
        //System.out.println(list);
        for (String s : list) {
            if (s.indexOf("T-") > -1) {
                /*for (String strr : s.substring(2).split("\\+"))
                    System.out.println(strr.substring(0, strr.indexOf(":")));*/
            }
        }
        return str;
    }

    private static String getPluralTag(List<Morpheme> morphList) {
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

    private static boolean ContainsPlural(List<Morpheme> morphList) {
        if (getPluralTag(morphList) == "")
            return false;
        return true;
    }

    //s.containsMorpheme(TurkishMorphotactics.a1pl)
    private static List<String> getPluralWord(List<SingleAnalysis> bestAnalysis) {
        String plural = "";
        String pluralTag = "";
        List<String> list = new ArrayList<String>();
        List<String> pluralList = new ArrayList<String>();
        int count = 0;

        for (SingleAnalysis s : bestAnalysis) {
            //System.out.println(s.formatLong());
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
                pluralList.add(plural);
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
        System.out.println(pluralList);
        //System.out.println(plural);
        return pluralList;
    }

    //equalsAny
    private static boolean ContainsAny(String[] search, String[] target) {
        for (int i = 0; i < search.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (search[i] == target[j])
                    return true;
            }
        }
        return false;
    }

    static public List<String> deletePossession(String input) {

        List<String> list = new ArrayList<String>();
        List<WordAnalysis> analysis = morphology.analyzeSentence(input);
        SentenceAnalysis disambiguate = morphology.disambiguate(input, analysis);
        List<SingleAnalysis> bestAnalysis = disambiguate.bestAnalysis();
        for (SingleAnalysis s : bestAnalysis) {
            //postagleri ekle
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
        }
        return list;
    }

}
