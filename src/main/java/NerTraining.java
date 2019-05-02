import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import zemberek.core.logging.Log;
import zemberek.morphology.TurkishMorphology;
import zemberek.ner.*;
import zemberek.ner.NerDataSet.AnnotationStyle;
import zemberek.tokenization.*;

//TODO FIX ZEMBEREK.TOKEN PROBLEM --> Fixed there was a version collision(update all poms to use zemberek version 0.17.0)
//todo test dosyasini gelistir
public class NerTraining {
    public static void main(String[] args) throws IOException {

        Path trainPath = Paths.get("src/main/resources/NER/ner-train.txt");
        Path testPath = Paths.get("src/main/resources/NER/ner-test.txt");
        Path modelRoot = Paths.get("src/main/resources/NER/my-model");

        NerDataSet trainingSet = NerDataSet.load(trainPath, AnnotationStyle.ENAMEX);
        Log.info(trainingSet.info()); // prints information

        NerDataSet testSet = NerDataSet.load(testPath, AnnotationStyle.ENAMEX);
        Log.info(testSet.info());

        TurkishMorphology morphology = TurkishMorphology.createWithDefaults();

        // Training occurs here. Result is a PerceptronNer instance.
        // There will be 7 iterations with 0.1 learning rate.
        PerceptronNer trainer = new PerceptronNerTrainer(morphology)
                .train(trainingSet, testSet, 13, 0.1f);

        Files.createDirectories(modelRoot);
        trainer.saveModelAsText(modelRoot);

    }

    public static List<String> NER(String sentence) throws IOException {
        Path modelRoot = Paths.get("src/main/resources/NER/my-model");

        List<String> NER=new ArrayList<String>();
        TurkishMorphology morphology = TurkishMorphology.createWithDefaults();

        PerceptronNer loadner = PerceptronNer.loadModel(modelRoot, morphology);

        NerSentence result = loadner.findNamedEntities(sentence);

        List<NamedEntity> namedEntities = result.getNamedEntities();

        for (NamedEntity namedEntity : namedEntities) {
            NER.add(namedEntity.toString());
        }
        return NER;
    }

}
