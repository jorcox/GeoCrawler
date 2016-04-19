package org.unizar.nutch.scoring.term;

import uk.ac.shef.dcs.oak.jate.JATEException;
import uk.ac.shef.dcs.oak.jate.core.algorithm.AbstractFeatureWrapper;
import uk.ac.shef.dcs.oak.jate.core.algorithm.Algorithm;
import uk.ac.shef.dcs.oak.jate.core.algorithm.TFIDFFeatureWrapper;
import uk.ac.shef.dcs.oak.jate.model.Term;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CustomAlgorithm implements Algorithm {

	@Override
	public Term[] execute(AbstractFeatureWrapper store) throws JATEException {
		if (!(store instanceof TFIDFFeatureWrapper))
			throw new JATEException("" + "Required: TFIDFFeatureWrapper");
		TFIDFFeatureWrapper corpus = (TFIDFFeatureWrapper) store;
		Set<Term> result = new HashSet<>();

		for (String s : corpus.getTerms()) {
			/*
			 * if(tfidfFeatureStore.getTermFreqInCorpus(s)==0 ||
			 * tfidfFeatureStore.getDocFreq(s)==0){ System.out.println("ZERO: "
			 * +s+"-tf:"+tfidfFeatureStore.getTermFreqInCorpus(s)+", df:"
			 * +tfidfFeatureStore.getDocFreq(s)); }
			 */
			double tf = (double) corpus.getTermFreq(s) / ((double) corpus.getTotalTermFreq()+1.0);
			double df_i = (double) corpus.getDocFreq(s) == 0 ? 1 : (double) corpus.getDocFreq(s);
			result.add(new Term(s, tf / df_i));
		}

		Term[] all = result.toArray(new Term[0]);
		Arrays.sort(all);
		return all;
	}
}
