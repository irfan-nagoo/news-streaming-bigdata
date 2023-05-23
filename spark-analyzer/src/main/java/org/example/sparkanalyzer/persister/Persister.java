package org.example.sparkanalyzer.persister;

/**
 * @author irfan.nagoo
 */
public interface Persister<I> {

    void persist(I input);
}
