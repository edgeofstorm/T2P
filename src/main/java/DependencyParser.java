import ContextFreeGrammar.ContextFreeGrammar;
import Corpus.Corpus;
import Dictionary.Pos;
import WordNet.*;
import org.maltparser.concurrent.*;
import org.maltparser.concurrent.graph.ConcurrentDependencyEdge;
import org.maltparser.concurrent.graph.ConcurrentGraphException;
import org.maltparser.concurrent.graph.dataformat.ColumnDescription;
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

    public static ConcurrentDependencyGraph Parse2(String[] tokens){
        ConcurrentDependencyGraph outputGraph = null;

        // Loading the Swedish model swemalt-mini
        ConcurrentMaltParserModel model = null;
        try {
            URL swemaltMiniModelURL = new File("src\\main\\resources\\test.mco").toURI().toURL();
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

        System.out.println(outputGraph);
        return outputGraph;
    }

    public static void main(String[] Args) throws ConcurrentGraphException {

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

        /*tokens[2]="3\toyun\toyna\tVerb\tVerb\tPast|A3sg";
        tokens[3] = "4\t.\t.\tPunc\tPunc\t_";*/

        //zemberekten bu modda getirt
        /*String[] tokens = new String[7];
        tokens[0] = "1\tKız\tkız\tNoun\tNoun\tA3sg";
        tokens[1] = "2\toyun\toyun\tNoun\tNoun\tA3sg";
        tokens[2] = "3\t_\toyna\tVerb\tVerb\tPos|Aor";
        tokens[3] = "4\toynarken\t_\tAdv\tAdv\tWhile";
        tokens[4] = "5\tyere\tyer\tNoun\tNoun\tA3sg|Dat";
        tokens[5] = "6\tdüştü\tdüş\tVerb\tVerb\tPos|Past|A3sg";
        tokens[6] = "7\t.\t.\tPunc\tPunc\t_";*/

        String[] tokens = new String[6];
        tokens[0] = "1\tOyun\toyun\tNoun\tNoun\tA3sg";
        tokens[1] = "2\t_\toyna\tVerb\tVerb\tPos|Aor";
        tokens[2] = "3\toynarken\t_\tAdv\tAdv\tWhile";
        tokens[3] = "4\tyere\tyer\tNoun\tNoun\tA3sg|Dat";
        tokens[4] = "5\tdüştü\tdüş\tVerb\tVerb\tPos|Past|A3sg";
        tokens[5] = "6\t.\t.\tPunc\tPunc\t_";

        /*String[] tokens = new String[7];
        tokens[0] = "1\tAyşe\tAyşe\tNoun\tProp\tA3sg|Pnon|Nom";
        tokens[1] = "2\tbasketbolu\tbasketbol\tNoun\tNoun\tA3sg|P3sg|Nom";
        tokens[2] = "3\t,\t,\tPunc\tPunc\t_";
        tokens[3] = "4\tMehmet\tMehmet\tNoun\tProp\tA3sg|Pnon|Nom";
        tokens[4] = "5\tfutbolu\tfutbol\tNoun\tNoun\tA3sg|P3sg|Nom";
        tokens[5] = "6\tsever\tsev\tVerb\tVerb\tPos|Aor|A3sg";
        tokens[6] = "7\t.\t.\tPunc\tPunc\t_";*/

        /*String[] tokens = new String[5];
        tokens[0] = "1\tAhmet\tahmet\tNoun\tProp\tA3sg|Pnon|Nom";
        tokens[1] = "2\tgazozun\tgazoz\tNoun\tNoun\tA3sg|Pnon|Gen";
        tokens[2] = "3\tkapağını\tkapağ\tNoun\tNoun\tA3sg|P3sg|Acc";
        tokens[3] = "4\taçtı\taç\tVerb\tVerb\tPos|Verb+Past+A3sg";
        tokens[4] = "5\t.\t.\tPunc\tPunc\t_";*/

        /*String[] tokens = new String[7];
        tokens[0] = "1\tKız\tkız\tNoun\tNoun\tA3sg";
        tokens[1] = "2\tarabaya\taraba\tNoun\tNoun\tA3sg|Dat";
        tokens[2] = "3\t_\tbin\tVerb\tVerb\tPos";
        tokens[3] = "4\tbinip\t_\tAdv\tAdv\tAfterDoingSo";
        tokens[4] = "5\tiçeriye\tiçeri\tNoun\tNoun\tA3sg|Pnon|Dat";
        tokens[5] = "6\tgitti\tgit\tVerb\tVerb\tPos|Past|A3sg";
        tokens[6] = "7\t.\t.\tPunc\tPunc\t_";*/

        /*String[] tokens = new String[7];
        tokens[0] = "1\tayşe\tayşe\tNoun,Prop\tNoun\tA3sg";
        tokens[1] = "2\tve\tve\tConj\tConj\t_";
        tokens[2] = "3\t_\toyna\tVerb\tVerb\tPos|Aor";
        tokens[3] = "4\toynarken\t_\tAdv\tAdv\tWhile";
        tokens[4] = "5\tyere\tyer\tNoun\tNoun\tA3sg|Dat";
        tokens[5] = "6\tdüştü\tdüş\tVerb\tVerb\tPos|Past|A3sg";
        tokens[6] = "7\t.\t.\tPunc\tPunc\t_";*/
       /* tokens[0] = "﻿1\tPeşreve\tpeşrev\tNoun\tNoun\tA3sg|Pnon|Dat";//	2	OBJECT	_	_2	başlamalı	başla	Verb	Verb	Pos|Neces|A3sg
        tokens[1] = "2\tbaşlamalı\tbaşla\tVerb\tVerb\tPos|Neces|A3sg";
        tokens[2] = "3\t.\t.\tPunc\tPunc\t_";*/
        /*tokens[0] = "1\tannemin	\tanne\tNoun\tNoun\tA3sg|P1sg|Gen";
        tokens[1] = "2\tşartları\tşart\tNoun\tNoun\tA3pl|P3sg|Nom";
        tokens[2] = "3\tvardı\tvar\tVerb\tVerb\tPos|Past|A3sg";
        tokens[3] = "4\t.\t.\tPunc\tPunc\t_";*/
        List<String[]> sentences = new ArrayList<>();
        sentences.add(tokens);
        try {
            /*String[] outputTokens = model.parseTokens(tokens);
            ConcurrentUtils.printTokens(model.parseSentences(sentences).get(0));*/
            outputGraph = model.parse(tokens);//model.parseSentences(sentences);
            String a= outputGraph.getDependencyIndices().stream().toString();
            //outputGraph.nodes[1].getHead();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println(outputGraph.nodes[1].getHeadIndex());
        //System.out.println( outputGraph.);
        for(int i =1;i<outputGraph.nTokenNodes();i++){
            //System.out.println(outputGraph.getTokenNode(i).getHeadIndex());
            System.out.println(outputGraph.getTokenNode(i).getHeadEdge().getTarget());
            //System.out.println(outputGraph.getTokenNode(i).getLabel("DEPREL"));
        }
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


