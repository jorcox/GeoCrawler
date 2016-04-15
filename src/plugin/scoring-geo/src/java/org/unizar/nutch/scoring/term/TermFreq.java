package org.unizar.nutch.scoring.term;

import java.io.IOException;

import uk.ac.shef.dcs.oak.jate.JATEException;
import uk.ac.shef.dcs.oak.jate.core.algorithm.TFIDFFeatureWrapper;
import uk.ac.shef.dcs.oak.jate.core.feature.FeatureBuilderCorpusTermFrequency;
import uk.ac.shef.dcs.oak.jate.core.feature.FeatureCorpusTermFrequency;
import uk.ac.shef.dcs.oak.jate.core.feature.indexer.GlobalIndexBuilderMem;
import uk.ac.shef.dcs.oak.jate.core.feature.indexer.GlobalIndexMem;
import uk.ac.shef.dcs.oak.jate.core.npextractor.CandidateTermExtractor;
import uk.ac.shef.dcs.oak.jate.core.npextractor.NounPhraseExtractorOpenNLP;
import uk.ac.shef.dcs.oak.jate.core.npextractor.WordExtractor;
import uk.ac.shef.dcs.oak.jate.model.Corpus;
import uk.ac.shef.dcs.oak.jate.model.CorpusCustom;
import uk.ac.shef.dcs.oak.jate.model.CorpusImpl;
import uk.ac.shef.dcs.oak.jate.model.Term;
import uk.ac.shef.dcs.oak.jate.test.AlgorithmTester;
import uk.ac.shef.dcs.oak.jate.util.control.Lemmatizer;
import uk.ac.shef.dcs.oak.jate.util.control.StopList;
import uk.ac.shef.dcs.oak.jate.util.counter.TermFreqCounter;
import uk.ac.shef.dcs.oak.jate.util.counter.WordCounter;

/**
 * @author Jorge Cancer
 */

public class TermFreq {

	public static Term[] getTerms(String content) {
		try {
			return executor(content);
		} catch (IOException | JATEException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Term[] executor(String content) throws IOException, JATEException {
		StopList stop = new StopList(true);
		Lemmatizer lemmatizer = new Lemmatizer();

		// Use an instance of CandidateTermExtractor to extract
		// candidate terms.
		// Three types of CandidateTermExtractorare implemented. Choose
		// the most appropriate one for your needs.
		// 1. An OpenNLP noun phrase extractor that extracts noun
		// phrases as candidate terms. If terms in your
		// domain are mostly NPs, use this one. If OpenNLP isnt suitable
		// for your domain, you may plug-in
		// your own NLP tools.
		CandidateTermExtractor npextractor = new NounPhraseExtractorOpenNLP(stop, lemmatizer);

		// 2. A generic N-gram extractor that extracts n(default is 5,
		// see the property file) grams. This doesn't
		// depend on tagging or parsing, but generates more much more
		// candidates than NounPhraseExtractorOpenNLP
		// CandidateTermExtractor npextractor = new NGramExtractor(stop,
		// lemmatizer);

		// 3. A word extractor that extracts single words as candidate
		// terms. If you task extracts single words as terms,
		// you could use this instead.
		// CandidateTermExtractor wordextractorTerm = new
		// WordExtractor(stop, lemmatizer);

		// An instance of WordExtractor is also needed to build word
		// frequency data, which are required by some algorithms.
		// See javadoc for regarding the parameters for the constructor.
		CandidateTermExtractor wordextractor = new WordExtractor(stop, lemmatizer, false, 1);

		// Next start building candidate term and word data and index
		// them.
		GlobalIndexBuilderMem builder = new GlobalIndexBuilderMem();
		GlobalIndexMem wordDocIndex = builder.build(new CorpusCustom(content), wordextractor);
		GlobalIndexMem termDocIndex = builder.build(new CorpusCustom(content), npextractor);

		// Optionally, you can save the index data as HSQL databases on
		// file system
		// GlobalIndexWriterHSQL.persist(wordDocIndex,
		// "D:/output/worddb");
		// GlobalIndexWriterHSQL.persist(termDocIndex,
		// "D:/output/termdb");

		// ##########################################################
		// # Step 2. Build various statistical features #
		// # used by term extraction algorithms. This will #
		// # need the indexes built above, and counting the #
		// # frequencies of terms #
		// ##########################################################

		// A WordCounter instance is required to count number of words
		// in corpora/documents. This
		// will be used as features by some algorithms.
		WordCounter wordcounter = new WordCounter();

		// Next we need to count frequencies of candidate terms. There
		// are different kinds of term frequency
		// data that are required by different algorithms. In JATE these
		// are called (statistical) "features",
		// see "uk.ac.shef.dcs.oak.jate.core.feature.AbstractFeature"
		// and its implementations.
		// These features must be built using a feature builder, see
		// "uk.ac.shef.dcs.oak.jate.core.feature.AbstractFeatureBuilder"
		// and its simplementations. Generally, each kind of
		// "AbstractFeature" will have an implementation of the
		// "AbstractFeatureBuilder".
		// The building of these features typically requires counting
		// term frequencies in certain ways, which is
		// a computationally extensive process. Two alternative sets of
		// "AbstractFeatureBuilder" classes are
		// implemented. The first set contains classes named as
		// "FeatureBuilderXYZMultiThread", which splits the corpus
		// into segments and run several threads are run in parallel
		// then aggregate results; the second set contain
		// classes named as "FeatureBuilderXYZ" which are the single
		// threaded options.
		// Also note that counting in JATE is case-sensitive: JATE
		// creats a one-to-many mapping from canonical term forms to
		// variants found in a corpus. When counting, each variant is
		// searched in the corpus, and the frequency adds up to a sum
		// as the total frequency for the canonical form. Scoring and
		// ranking are based on the canonical forms also.

		/*
		 * Option 1: Due to use of multi-threading, this can significantly
		 * occupy your CPU and memory resources. It is better to use this way on
		 * dedicated server machines, and only for very large corpus.
		 * 
		 * NOTE: YOU DO NOT need all the following features for every single
		 * algorithm. This class creates all for the purpose of showcase all
		 * algorithms.
		 */
		/*
		 * FeatureCorpusTermFrequency wordFreq = new
		 * FeatureBuilderCorpusTermFrequencyMultiThread(wordcounter,
		 * lemmatizer).build(wordDocIndex); FeatureDocumentTermFrequency
		 * termDocFreq = new
		 * FeatureBuilderDocumentTermFrequencyMultiThread(wordcounter,
		 * lemmatizer).build(termDocIndex); FeatureTermNest termNest = new
		 * FeatureBuilderTermNestMultiThread().build(termDocIndex);
		 * FeatureRefCorpusTermFrequency bncRef = new
		 * FeatureBuilderRefCorpusTermFrequency(args[1]).build(null);
		 * FeatureCorpusTermFrequency termCorpusFreq = new
		 * FeatureBuilderCorpusTermFrequencyMultiThread(wordcounter,
		 * lemmatizer).build(termDocIndex);
		 */

		/* Option #2 */
		// If you use single-threaded feature builders, you need to
		// create ONE instance of TermFreqCounter.
		// Before with multi-threaded feature builders, one instance of
		// TermFreqCounter is created for each
		// thread.
		System.out.println("Carga de terminos");
		TermFreqCounter npcounter = new TermFreqCounter();
		FeatureCorpusTermFrequency termCorpusFreq = new FeatureBuilderCorpusTermFrequency(npcounter, wordcounter,
				lemmatizer).build(termDocIndex);
		Corpus f = new CorpusImpl();

		// ##########################################################
		// # Step 3. For each algorithm you want to test #
		// # create an instance of the algorithm class, #
		// # and also an instance of its feature wrapper. #
		// ##########################################################
		AlgorithmTester tester = new AlgorithmTester();

		// NOTE that each algorithm will need its own "FeatureWrapper"
		// (in uk.ac.shef.dcs.oak.jate.core.algorithm)
		// Each feature wrapper may require different kinds of
		// "AbstractFeature". You can find details of these in
		// the javadoc. The purpose of "FeatureWrapper" is to
		// encapsulate the underlying feature (stores) and provide
		// access to only the features required by the corresponding
		// algorithm
		System.out.println("Empiezan los algoritmos");
		// tester.registerAlgorithm(new TFIDFAlgorithm(), new
		// TFIDFFeatureWrapper(termCorpusFreq));
		// tester.registerAlgorithm(new RIDFAlgorithm(), new
		// RIDFFeatureWrapper(termCorpusFreq));
		// tester.registerAlgorithm(new AverageCorpusTFAlgorithm(), new
		// AverageCorpusTFFeatureWrapper(termCorpusFreq));
		// tester.registerAlgorithm(new FrequencyAlgorithm(), new
		// FrequencyFeatureWrapper(termCorpusFreq));
		// tester.registerAlgorithm(new ChiSquareAlgorithm(stop, lemmatizer,
		// wordextractor), new ChiSquareFeatureWrapper(corpus, tester));
		// tester.registerAlgorithm(new JustesonAlgorithm(), new
		// AverageCorpusTFFeatureWrapper(termCorpusFreq));
		// tester.registerAlgorithm(new NCValueAlgorithm(null, stop,
		// lemmatizer), new NCValueFeatureWrapper(f, null, tester));

		// execute(termDocIndex, args[2]);
		// Term[] terms = new TFIDFAlgorithm().execute(new
		// TFIDFFeatureWrapper(termCorpusFreq));
		Term[] terms = new CustomAlgorithm().execute(new TFIDFFeatureWrapper(termCorpusFreq));

		return terms;
	}

	public static void main(String[] args) {
		getTerms(
				"EL ESPAÑOL - Diario digital, plural, libre, indomable, tuyo cerrar El Español para Android Ver Cerrar Login Perfil de usuario Favoritos Suscríbete Cerrar sesión España Mundo Economía Prodigios Podium Miradas Jaleos Ocio Coliseo El Blog S&D La edición Zona Ñ Buscar Cerrar Buscar {Ñ} El Español La edición Suscríbete Zona Ñ La Edición En Portada El Río España Mundo Economía Prodigios Podium Miradas Jaleos Ocio Coliseo El Blog S&D El Río España Detenida la cúpula de Manos Limpias Carlota Guindal Manos Limpias Facebook Twitter Google + LinkedIn Menéame Whatsapp Fútbol Champions: City-Real Madrid y Atlético-Bayern Mario Díaz Champions League Facebook Twitter Google + LinkedIn Menéame Whatsapp Salud La Coca-Cola como auxiliar de la 'quimio' Ainhoa Iriberri Oncología Facebook Twitter Google + LinkedIn Menéame Whatsapp Libros Cervantes hace pellas: \"Nos da vergüenza leerlo\" Lorena G. Maldonado IV Centenario Cervantes Facebook Twitter Google + LinkedIn Menéame Whatsapp Anterior Siguiente 1 de 4 José Manuel Soria en el que era su escaño J.C. Hidalgo Efe Soria dimite por el escándalo de sus sociedades offshore Renuncia como ministro de Industria, a la presidencia del PP canario y a su escaño en el Congreso. Rajoy está \"muy preocupado y decepcionado\" con él porque \"ha mentido\". Ya anunció que hoy no acudiría al Consejo de Ministros. Ana I. Gracia José Manuel Soria Facebook Twitter Google + LinkedIn Menéame Whatsapp La cronología del caso Soria: la semana más difícil del exministro La dimisión del ya extitular de Industria calma la tensión en Génova, que ha vivido \"horas críticas\" tras las informaciones sobre su entramado societario. Gonzalo Araluce José Manuel Soria Facebook Twitter Google + LinkedIn Menéame Whatsapp Las empresas de Soria en el extranjero no presentan cuentas desde 1998 Es imposible conocer el montante de fondos que fue traspasado por la red opaca a paraísos fiscales. Tampoco hay información contable sobre los negocios de la familia. Daniel Montero Gonzalo Araluce Papeles de Panamá Facebook Twitter Google + LinkedIn Menéame Whatsapp Cuatro sombras (y una luz) de Soria como ministro Nadie derramará una lágrima por Soria Pedro J. Ramírez José Manuel Soria Facebook Twitter Google + LinkedIn Menéame Whatsapp La porra tuitera: ¿acabará Soria en Repsol? Patricia Morales Gobierno de España Facebook Twitter Google + LinkedIn Menéame Whatsapp Efe Las cinco dimisiones de Rajoy Desde que el PP llegó al poder, son cinco los ministros que han dimitido. El presidente no ha cesado a ninguno. Ana I. Gracia Mariano Rajoy Brey Facebook Twitter Google + LinkedIn Menéame Whatsapp Rivera pide a Rajoy que explique en el Congreso la dimisión de Soria El líder de C's le dice al presidente catalán Carles Puigdemont que la independencia es una \"vía muerta\". J.S. / Agencias Papeles de Panamá Facebook Twitter Google + LinkedIn Menéame Whatsapp La sintaxis de Panamá David Álvarez José Manuel Soria Facebook Twitter Google + LinkedIn Menéame Whatsapp Rajoy ante los Alpes Ferrer Molina Mariano Rajoy Brey Facebook Twitter Google + LinkedIn Menéame Whatsapp Arnaldo Otegi durante su rueda de prensa este viernes Luis Tejido Efe Otegi: \"Voy a ser el lehendakari más peligroso para el Estado\" Afirma que la izquierda abertzale ya ha reconocido el daño causado \"varias veces\", aunque no niega que tengan que hacer \"más cosas\". J. O. | Agencias Arnaldo Otegi Facebook Twitter Google + LinkedIn Menéame Whatsapp J. J. Guillén Efe Detienen a los presidentes de Manos Limpias y Ausbanc por extorsión La detención de Miguel Bernard, del sindicato Manos Limpias, pone en jaque su acusación en el 'caso Nóos'. Carlota Guindal Manos Limpias Facebook Twitter Google + LinkedIn Menéame Whatsapp Efe El presidente de Ausbanc saca pecho en Twitter tras anunciarse su detención En la jungla : Luis Pineda cree \"que la Policía ha engañado a su hijo y le ha arrebatado las llaves\". Diego González Detenciones Facebook Twitter Google + LinkedIn Menéame Whatsapp El PP entregará la cabeza del alcalde de Granada para retener la Alcaldía Los 'populares' de Génova 13 y los andaluces se han unido y comprometido con Ciudadanos a que el alcalde se aparte para evitar una moción de censura. Ana I. Gracia José Torres Hurtado Facebook Twitter Google + LinkedIn Menéame Whatsapp Efe Camps ataca a la Abogacía del Estado: “Ustedes están para controlar que se hacen bien las cosas” Sostiene que desconocía que Iñaki Urdangarin estaba detrás de Nóos. Carlota Guindal Caso Nóos Facebook Twitter Google + LinkedIn Menéame Whatsapp Camps pidió a Urdangarin que le colocase de ponente en Davos El expresidente valenciano solicitó estar en  la más prestigiosa cita económica mundial como contraprestación mientras le daba contratos públicos. Esteban Urreiztieta Daniel Montero Caso Nóos Facebook Twitter Google + LinkedIn Menéame");
	}

}
