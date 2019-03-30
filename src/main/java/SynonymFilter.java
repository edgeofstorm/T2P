import WordNet.WordNet;
import WordNet.SynSet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//bozdum bunu siktir et useless anyways.

public class SynonymFilter {//TODO PERFORMANCE ANALYSIS OF FILTERING FUNCTIONS
    public static void main(){
        WordNet turkish = new WordNet();
        //turkish.check(null);

        //TODO GET 1.SENSE AND MATCHWHOLEWORD ONLY
        ArrayList<SynSet> Synsets = turkish.getSynSetsWithLiteral("ahmak"); //todo use other func which returns SynSet this is just for all senses of this word
        SynSet FirstSenseSynset = turkish.getSynSetWithLiteral("ahmak", 1);
        int literalSize = FirstSenseSynset.getSynonym().literalSize();
        System.out.println("literal size : " + literalSize);
        ArrayList<String> path = turkish.findPathToRoot(FirstSenseSynset);
        List<String> freqsInputList = new ArrayList<String>();

        //ONLY FIRST SENSE
        for (int i = 0; i < literalSize; i++) {
            String lit = FirstSenseSynset.getSynonym().getLiteral(i).toString();//literals are in this form:"makine 3" or "araba 1" with their senses attached to them.
            int size = lit.length();
            int target = lit.length() - 2;
            if (lit.substring(target, size).contains("1")) // eliminating other senses apart from sense1
                freqsInputList.add(lit.substring(0, target));
        }

        //FOR ALL SENSES
        /*for(SynSet s : Synsets){
            int index = s.getSynonym().literalSize();
            for(int i=0;i<index;i++){ //foreach not applicable bc getliteral(index i)
                String lit=s.getSynonym().getLiteral(i).toString();//literals are in this form:"makine 3" or "araba 1" with their senses attached to them.
                int size = lit.length();
                int target = lit.length()-2;
                if(lit.substring(target,size).contains("1")) // eliminating other senses apart from sense1
                    freqsInputList.add(lit.substring(0,target));
            }
            s.getRelation(1).getName();
            s.containsRelationType("HYPONYM");
            System.out.println("Synoyms: "+s.getSynonym().toString()+"\t ID:"+s.getId());

        }*/
        System.out.println("Hypernym ID: " + path);
        System.out.println("sense 1 Synyoyms as List<String> and size: " + freqsInputList.toString() + freqsInputList.size());
        String highestFreq = SynonymFilter("src/main/resources/freq-100k.txt", freqsInputList);
        System.out.println("highest freq of all synonyms: " + highestFreq);
        //FilterTxt("src/main/resources/top-20K-words.txt");
        //DeletingDuplicates();
        turkish.saveAsLmf("turkish.lmf");
    }

    //TODO EXCEPTION EKLE synset olarak almayi dene.
    //SYNONYM FILTER FUNC. --> bi kelimenin tum es anlamlilarini freq-100k.txt le filtreliyor(converted func from List<String> to String because when searching through freq-100k it always grabs highest freq word first)
    public static String SynonymFilter(String fileName, List<String> searchStr) {
        Scanner scan = null;
        String target = "kelime freq-100k.txt dosyasinda bulunmuyor.";
        try {
            scan = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scan.hasNext()) {
            String line = scan.nextLine().toLowerCase().toString();
            for (String s : searchStr) {
                if (line.contains(s.toLowerCase().toString())) {
                    target = line.substring(0, line.indexOf(" "));
                    return target;
                }
            }
        }
        return target;
    }

    public static void FilterTxt(String SrcfileName) {
        Scanner scan = null;
        WordNet turkish = new WordNet();
        List<String> freqsInputList = new ArrayList<String>();
        String line = "";
        String[] puncs = new String[]{"?", ",", ".", ";", ":", "..."};
        Boolean puncDetected = false;
        try {
            scan = new Scanner(new File(SrcfileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter write_line = null;
        try {
            FileWriter writer = new FileWriter("src/main/resources/output.txt", false);
            write_line = new PrintWriter(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (scan.hasNextLine()) {
            /*if (scan.nextLine().toLowerCase().toString().equals(line) && scan.hasNextLine()) {
                continue;
            } else {*/
            line = scan.nextLine().toLowerCase().toString();
            for (String s : puncs) {
                if (line.contains(s)){
                    puncDetected = true;break;}
            }
            SynSet currSynset = turkish.getSynSetWithLiteral(line, 1);
            if (currSynset == null) {
                continue;
            }
            int literalSize = currSynset.getSynonym().literalSize();
            if (!puncDetected && literalSize > 1) {//literalSize does not matter what matters is freqsInputList
                for (int i = 0; i < literalSize; i++) {
                    String lit = currSynset.getSynonym().getLiteral(i).toString();//literals are in this form:"makine 3" or "araba 1" with their senses attached to them.
                    int size = lit.length();
                    int target = lit.length() - 2;
                    if (lit.substring(target, size).contains("1")) // eliminating other senses apart from sense1
                        freqsInputList.add(lit.substring(0, target));
                }
            } else {
                freqsInputList.add(line);

            }
            if (SynonymFilter("src/main/resources/freq-100k.txt", freqsInputList) != "kelime freq-100k.txt dosyasinda bulunmuyor." && freqsInputList.size() > 1) {//yerlerini degistir
                String currWrite = SynonymFilter("src/main/resources/freq-100k.txt", freqsInputList);
                write_line.println(currWrite);
            } else {
                write_line.println(line);
            }
            puncDetected=false;
            freqsInputList.clear();
            /*for (String s : freqsInputArr) {
                if (line.contains(s)) {
                    target = line.substring(0, line.indexOf(" "));
                    return target;
                }
            }*/

        }

    }

    //TODO PERFORMANCE(NOT CHECK AL OF IT JUST ARKA ARKAYA GELENLERI)
    static public void DeletingDuplicates() {
        // PrintWriter object for output.txt
        PrintWriter pw = null;
        BufferedReader br1 = null;
        try {
            pw = new PrintWriter("src/main/resources/outputfinal.txt");
            br1 = new BufferedReader(new FileReader("src/main/resources/output.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // BufferedReader object for input.txt

        String line1 = null;
        try {
            line1 = br1.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // loop for each line of input.txt
        while (line1 != null) {
            boolean flag = false;

            // BufferedReader object for output.txt
            BufferedReader br2 = null;
            try {
                br2 = new BufferedReader(new FileReader("src/main/resources/outputfinal.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String line2 = null;
            try {
                line2 = br2.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // loop for each line of output.txt
            while (line2 != null) {

                if (line1.equals(line2)) {
                    flag = true;
                    break;
                }

                try {
                    line2 = br2.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            // if flag = false
            // write line of input.txt to output.txt
            if (!flag) {
                pw.println(line1);

                // flushing is important here
                pw.flush();
            }

            try {
                line1 = br1.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        // closing resources
        try {
            br1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pw != null) {
            pw.close();
        }
        System.out.println("File operation performed successfully");
    }

}
