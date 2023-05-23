package org.example.sparkanalyzer.consumer;

/**
 * @author irfan.nagoo
 */
public interface Consumer<O> {

    O consume();
}
