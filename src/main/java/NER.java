import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import zemberek.core.logging.Log;
import zemberek.ner.*;
import zemberek.ner.NerDataSet.AnnotationStyle;

//TODO FIX ZEMBEREK.TOKEN PROBLEM --> Fixed there was a version collision(update all poms to use zemberek version 0.17.0)
//todo test dosyasini gelistir
public class NER {

    private static Path trainPath = Paths.get("src/main/resources/NER/ner-train.txt");
    private static Path testPath = Paths.get("src/main/resources/NER/ner-test.txt");
    private static Path modelRoot = Paths.get("src/main/resources/NER/my-model");

    public static void main(String[] args) throws IOException {
        TrainModel();
    }

    private static void TrainModel() throws IOException {

        NerDataSet trainingSet = NerDataSet.load(trainPath, AnnotationStyle.ENAMEX);
        Log.info(trainingSet.info()); // prints information

        NerDataSet testSet = NerDataSet.load(testPath, AnnotationStyle.ENAMEX);
        Log.info(testSet.info());

        // Training occurs here. Result is a PerceptronNer instance.
        // There will be 7 iterations with 0.1 learning rate.
        PerceptronNer trainer = new PerceptronNerTrainer(MorphologicalAnalysis.morphology)
                .train(trainingSet, testSet, 13, 0.1f);

        Files.createDirectories(modelRoot);
        trainer.saveModelAsText(modelRoot);
    }

    public static List<String> NER(String sentence) throws IOException {

        List<String> NER = new ArrayList<String>();

        PerceptronNer loadner = PerceptronNer.loadModel(modelRoot, MorphologicalAnalysis.morphology);

        NerSentence result = loadner.findNamedEntities(sentence);

        List<NamedEntity> namedEntities = result.getNamedEntities();

        for (NamedEntity namedEntity : namedEntities) {
            NER.add(namedEntity.toString());
        }
        return NER;
    }

}
