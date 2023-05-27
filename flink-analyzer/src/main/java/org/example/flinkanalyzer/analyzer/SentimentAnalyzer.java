package org.example.flinkanalyzer.analyzer;

import com.datastax.driver.core.utils.UUIDs;
import opennlp.tools.doccat.*;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.ScalarFunction;
import org.example.flinkanalyzer.helper.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.LocalDate;

import static org.apache.flink.table.api.Expressions.call;
import static org.apache.flink.table.api.Expressions.col;

/**
 * @author irfan.nagoo
 */
public class SentimentAnalyzer implements Analyzer<Table, Table>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentimentAnalyzer.class);

    private static final DoccatModel DOC_CATEGORY_MODEL;
    private static final TokenizerME TOKENIZER_ME;

    static {
        try {
            TOKENIZER_ME = getTokenizerME(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.analyzer.tokenFilePath"));
            DOC_CATEGORY_MODEL = getDoccatModel(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.analyzer.trainFilePath"));
        } catch (IOException e) {
            LOGGER.info("Exception occurred while initializing the Analyzer: ", e);
            throw new IllegalArgumentException("Invalid tokenizer or train file path", e);
        }
    }

    public SentimentAnalyzer(StreamTableEnvironment sTableEnvironment) {
        // register user defined functions
        registerUDFs(sTableEnvironment);
    }

    @Override
    public Table analyze(Table table) {
        LOGGER.info("Initializing SentimentAnalyzer");
        // select required columns, analyze and return
        return table.select(col("title"), col("link"),
                col("pubDate"), col("image_url").as("imageUrl"),
                call("getPartDate", col("pubDate")).as("partDate"),
                call("generateTimeUUID").as("timeUUID"),
                call("sentimentFinder", col("content")).as("sentiment"),
                call("currentTimestamp").as("createTimestamp"));
    }

    public String findSentiment(String input) {
        String[] tokens = TOKENIZER_ME.tokenize(input);
        DocumentCategorizerME documentCategorizerME = new DocumentCategorizerME(DOC_CATEGORY_MODEL);
        double[] result = documentCategorizerME.categorize(tokens);
        return documentCategorizerME.getBestCategory(result);
    }

    private static TokenizerME getTokenizerME(String tokenFilePath) throws IOException {
        return new TokenizerME(new TokenizerModel(getInputStream(tokenFilePath)));
    }

    private static DoccatModel getDoccatModel(String trainFilePath) throws IOException {
        // read plain text train input file
        InputStreamFactory isf = () -> getInputStream(trainFilePath);
        ObjectStream<DocumentSample> objStream = new DocumentSampleStream(new PlainTextByLineStream(isf,
                Charset.defaultCharset()));

        // customize doc category factory with feature generators for
        // processing words in a given input
        FeatureGenerator[] featureGenerators = new FeatureGenerator[]{new NGramFeatureGenerator(1, 1),
                new NGramFeatureGenerator(2, 3), new BagOfWordsFeatureGenerator()};

        // Training parameters-
        //   CUTOFF_PARAM - Minimum number of words to be considered in a given category
        //   ITERATIONS_PARAM - Number of iteration to perform
        TrainingParameters trainingParameters = TrainingParameters.defaultParams();
        trainingParameters.put(TrainingParameters.CUTOFF_PARAM, 1);
        trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, 10);
        return DocumentCategorizerME.train("en", objStream, trainingParameters, new DoccatFactory(featureGenerators));
    }

    private static InputStream getInputStream(String modelFilePath) {
        return SentimentAnalyzer.class.getClassLoader().getResourceAsStream(modelFilePath);
    }

    private void registerUDFs(StreamTableEnvironment sTableEnvironment) {
        sTableEnvironment.createTemporarySystemFunction("getPartDate", PartitionDate.class);
        sTableEnvironment.createTemporarySystemFunction("generateTimeUUID", TimeUUID.class);
        sTableEnvironment.createTemporarySystemFunction("sentimentFinder", this.new SentimentFinder());
    }

    public static class PartitionDate extends ScalarFunction {
        public LocalDate eval(Timestamp pubDate) {
            return pubDate.toLocalDateTime().toLocalDate();
        }
    }

    public static class TimeUUID extends ScalarFunction {
        public String eval() {
            return UUIDs.timeBased().toString();
        }
    }

    public class SentimentFinder extends ScalarFunction {
        public String eval(String input) {
            return findSentiment(input);
        }
    }


}
