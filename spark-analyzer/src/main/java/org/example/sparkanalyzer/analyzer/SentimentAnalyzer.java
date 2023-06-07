package org.example.sparkanalyzer.analyzer;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import opennlp.tools.doccat.*;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Optional;

import static org.apache.spark.sql.functions.*;

/**
 * @author irfan.nagoo
 */
public class SentimentAnalyzer implements Analyzer<Dataset<Row>, Dataset<Row>>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentimentAnalyzer.class);

    private static DoccatModel doccatModel;

    private static TokenizerME tokenizerME;


    public SentimentAnalyzer(SparkSession sparkSession) {
        this(sparkSession, "opennlp/en-token.bin",
                "opennlp/en-sentiment.train");
    }

    public SentimentAnalyzer(SparkSession sparkSession, String tokenizerPath, String trainFilePath) {
        try {
            // register user defined functions
            registerUDFs(sparkSession);
            tokenizerME = Optional.ofNullable(tokenizerME)
                    .orElse(new TokenizerME(new TokenizerModel(getInputStream(tokenizerPath))));
            doccatModel = Optional.ofNullable(doccatModel)
                    .orElse(getDoccatModel(trainFilePath));
        } catch (IOException e) {
            LOGGER.info("Exception occurred while initializing the Analyzer: ", e);
            throw new IllegalArgumentException("Invalid tokenizer or train file path", e);
        }
    }

    @Override
    public Dataset<Row> analyze(Dataset<Row> dataset) {
        LOGGER.info("Initializing SentimentAnalyzer");
        return dataset.select(col("title"), col("link"), col("content"),
                        col("pubDate").as("pub_date"), col("image_url"))
                .withColumn("part_date", call_udf("getPartDate", col("pub_date")))
                .withColumn("id", call_udf("generateTimeUUID"))
                .withColumn("sentiment", call_udf("sentimentAnalyzer", col("content")))
                .withColumn("create_timestamp", current_timestamp())
                .drop(col("content"));
    }

    private String findSentiment(String input) {
        String[] tokens = tokenizerME.tokenize(input);
        DocumentCategorizerME documentCategorizerME = new DocumentCategorizerME(doccatModel);
        double[] result = documentCategorizerME.categorize(tokens);
        return documentCategorizerME.getBestCategory(result);
    }

    private DoccatModel getDoccatModel(String trainFilePath) throws IOException {
        // read plain text train input file
        InputStreamFactory isf = () -> getInputStream(trainFilePath);
        try (ObjectStream<DocumentSample> objStream = new DocumentSampleStream(new PlainTextByLineStream(isf,
                Charset.defaultCharset()))) {

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
    }

    private static InputStream getInputStream(String modelFilePath) throws IOException {
        return SentimentAnalyzer.class.getClassLoader().getResourceAsStream(modelFilePath);
    }

    private void registerUDFs(SparkSession sparkSession) {
        sparkSession.udf().register("getPartDate",
                udf((Timestamp pubDate) -> pubDate.toLocalDateTime().toLocalDate(), DataTypes.DateType));
        sparkSession.udf().register("generateTimeUUID",
                udf(() -> Uuids.timeBased().toString(), DataTypes.StringType));
        sparkSession.udf().register("sentimentAnalyzer",
                udf((String input) -> this.findSentiment(input), DataTypes.StringType));

    }

}
