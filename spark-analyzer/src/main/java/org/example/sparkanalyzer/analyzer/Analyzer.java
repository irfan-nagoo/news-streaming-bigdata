package org.example.sparkanalyzer.analyzer;

/**
 * @author irfan.nagoo
 */
public interface Analyzer<I, O> {

    O analyze(I input);
}
