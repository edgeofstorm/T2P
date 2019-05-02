import ContextFreeGrammar.ContextFreeGrammar;
import Corpus.Corpus;
import Dictionary.Pos;
import WordNet.*;
import org.maltparser.concurrent.*;
import org.maltparser.concurrent.graph.ConcurrentDependencyEdge;
import org.maltparser.core.syntaxgraph.DependencyStructure;
import org.maltparser.parser.SingleMalt;
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

import java.io.*;
import java.sql.*;
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

import java.net.URL;

import org.maltparser.concurrent.ConcurrentMaltParserModel;
import org.maltparser.concurrent.ConcurrentMaltParserService;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;

import org.maltparser.*;
import DependencyParser.*;
import SyntacticParser.*;
import ParseTree.*;

public class DependencyParser {




    public static void Parse(String[] tokens){
        ConcurrentDependencyGraph outputGraph = null;

        // Loading the Swedish model swemalt-mini
        ConcurrentMaltParserModel model = null;
        try {
            URL swemaltMiniModelURL = new File("C:\\Users\\haQQi\\Desktop\\FINISH HIM\\LITERATURE\\Simplification\\Parsing\\test\\maltparser-1.9.2\\test.mco").toURI().toURL();
            model = ConcurrentMaltParserService.initializeParserModel(swemaltMiniModelURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creates an array of tokens, which contains the Swedish sentence 'Samtidigt får du högsta sparränta plus en skattefri sparpremie.'
        // in the CoNLL data format.

        try {
            outputGraph = model.parse(tokens);//model.parseSentences(sentences);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println( outputGraph.);
        System.out.println(outputGraph);
    }

    public static void main(String[] Args) {

        /*String[] tokens = new String[10];
        int count=0;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\\\Users\\\\haQQi\\\\Desktop\\\\FINISH HIM\\\\METUSABANCI_treebank_v-1.conll"), "UTF8"));
            String line = br.readLine();

            for(String treeLine = ""; count<10; line = br.readLine()) {
                if (!line.isEmpty() && count>3) {
                    treeLine = treeLine + line;
                    tokens[count]=treeLine;
                    System.out.println(treeLine);
                }
                treeLine="";
                count++;
            }
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        }*/


        ConcurrentDependencyGraph outputGraph = null;
        //ConcurrentDependencyEdge

        // Loading the Swedish model swemalt-mini
        ConcurrentMaltParserModel model = null;
        try {
            URL swemaltMiniModelURL = new File("C:\\Users\\haQQi\\Desktop\\FINISH HIM\\LITERATURE\\Simplification\\Parsing\\test\\maltparser-1.9.2\\test.mco").toURI().toURL();
            model = ConcurrentMaltParserService.initializeParserModel(swemaltMiniModelURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creates an array of tokens, which contains the Swedish sentence 'Samtidigt får du högsta sparränta plus en skattefri sparpremie.'
        // in the CoNLL data format.

        //zemberekten bu modda getirt
        String[] tokens = new String[7];
        tokens[0] = "1\tKız\tkız\tNoun\tNoun\tA3sg";
        tokens[1] = "2\toyun\toyun\tNoun\tNoun\tA3sg";
        /*tokens[2]="3\toyun\toyna\tVerb\tVerb\tPast|A3sg";
        tokens[3] = "4\t.\t.\tPunc\tPunc\t_";*/
        tokens[2] = "3\t_\toyna\tVerb\tVerb\tPos|Aor";
        tokens[3] = "4\toynarken\t_\tAdv\tAdv\tWhile";
        tokens[4] = "5\tyere\tyer\tNoun\tNoun\tA3sg|Dat";
        tokens[5] = "6\tdüştü\tdüş\tVerb\tVerb\tPos|Past|A3sg";
        tokens[6] = "7\t.\t.\tPunc\tPunc\t_";
       /* tokens[0] = "﻿1\tPeşreve\tpeşrev\tNoun\tNoun\tA3sg|Pnon|Dat";//	2	OBJECT	_	_2	başlamalı	başla	Verb	Verb	Pos|Neces|A3sg
        tokens[1] = "2\tbaşlamalı\tbaşla\tVerb\tVerb\tPos|Neces|A3sg";
        tokens[2] = "3\t.\t.\tPunc\tPunc\t_";*/
        /*tokens[0] = "1\tannemin	\tanne\tNoun\tNoun\tA3sg|P1sg|Gen";
        tokens[1] = "2\tşartları\tşart\tNoun\tNoun\tA3pl|P3sg|Nom";
        tokens[2] = "3\tvardı\tvar\tVerb\tVerb\tPos|Past|A3sg";
        tokens[3] = "4\t.\t.\tPunc\tPunc\t_";*/
        List<String[]> sentences = new ArrayList<>();
        sentences.add(tokens);
//        tokens[4] = "5\t.\t.\tPunc\tPunc\t_";
//        tokens[1] = "2\tfår\t_\tVB\tVB\tPRS|AKT";
//        tokens[2] = "3\tdu\t_\tPN\tPN\tUTR|SIN|DEF|SUB";
//        tokens[3] = "4\thögsta\t_\tJJ\tJJ\tSUV|UTR/NEU|SIN/PLU|DEF|NOM";
//        tokens[4] = "5\tsparränta\t_\tNN\tNN\tUTR|SIN|IND|NOM";
//        tokens[5] = "6\tplus\t_\tAB\tAB\t_";
//        tokens[6] = "7\ten\t_\tDT\tDT\tUTR|SIN|IND";
//        tokens[7] = "8\tskattefri\t_\tJJ\tJJ\tPOS|UTR|SIN|IND|NOM";
//        tokens[8] = "9\tsparpremie\t_\tNN\tNN\tUTR|SIN|IND|NOM";
//        tokens[9] = "10\t.\t_\tMAD\tMAD\t_";
        try {
            /*String[] outputTokens = model.parseTokens(tokens);
            ConcurrentUtils.printTokens(model.parseSentences(sentences).get(0));*/
            outputGraph = model.parse(tokens);//model.parseSentences(sentences);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println( outputGraph.);
        System.out.println(outputGraph.getDependencyNode(1).getRightmostDescendant());
        System.out.println(outputGraph);


        //Malt malt=new Malt();
        //SingleMalt singleMalt=new SingleMalt();

        //singleMalt.parse();

        /*MaltParserService service=new MaltParserService();
        service.*/
        /*ContextFreeGrammar cfg = new ContextFreeGrammar(new TreeBank("C:\\Users\\haQQi\\Desktop\\FINISH HIM\\METUSABANCI_treebank_v-1.conll"));//LITERATURE\Simplification\Parsing\metu-treebank.xml
        SyntacticParser syntacticParser = new CYKParser();
        Corpus corpus = new Corpus("C:\\Users\\haQQi\\Desktop\\FINISH HIM\\tr_pud-ud-test.txt");
        System.out.println(syntacticParser.parse(cfg, corpus.getSentence(4)).get(0).toString());*/
    }
}


