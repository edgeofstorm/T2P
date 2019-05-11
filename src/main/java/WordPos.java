import Dictionary.Pos;
import zemberek.morphology.morphotactics.TurkishMorphotactics;

/*public enum WordPos {

    Noun("NOUN"),//guncelle  Noun(Pos.NOUN),
    NounTime("NOUN"),
    Adj("ADJECTIVE"),
    Adv("ADVERB"),
    Conj("CONJUNCTION"),
    Interj("INTERJECTION"),
    Verb("VERB"),
    Pron("PRONOUN"),
    PronQuant("PRONOUN"),
    PronPers("PRONOUN"),
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
}*/
public enum WordPos {

    Noun(Pos.NOUN),//guncelle  Noun(Pos.NOUN),
    NounTime(Pos.NOUN),
    NounProp(Pos.NOUN),
    Adj(Pos.ADJECTIVE),
    Adv(Pos.ADVERB),
    Conj(Pos.CONJUNCTION),
    Interj(Pos.INTERJECTION),
    Verb(Pos.VERB),
    Pron(Pos.PRONOUN),
    PronQuant(Pos.PRONOUN),
    PronPers(Pos.PRONOUN),
    PronQues(Pos.PRONOUN),
    Num(Pos.NOUN),
    Det(Pos.NOUN),
    PostP(Pos.NOUN),
    Ques(Pos.NOUN),
    Dup(Pos.NOUN),
    Punc(Pos.NOUN),
    Unk(Pos.NOUN),
    UnkUnk(Pos.NOUN),
    PronDemons(Pos.PRONOUN),
    NumCard(Pos.NOUN),
    NumDist(Pos.NOUN),
    PostpPCAbl(Pos.NOUN),
    PostpPCDat(Pos.NOUN),
    PostpPCNom(Pos.NOUN),
    NounAbbrv(Pos.NOUN),
    PronReflex(Pos.PRONOUN);
    //TurkishMorphotactics

    private Pos WordnetForm;

    public String getwordnetForm() {
        return this.WordnetForm.toString();
    }

    private WordPos(Pos wordnetForm) {
        this.WordnetForm = wordnetForm;
    }
}

