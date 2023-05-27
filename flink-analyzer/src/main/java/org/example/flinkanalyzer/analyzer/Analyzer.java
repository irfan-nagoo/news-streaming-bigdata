package org.example.flinkanalyzer.analyzer;

/**
 * @author irfan.nagoo
 */
public interface Analyzer<I, O> {

    O analyze(I input);
}
