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
import uk.ac.shef.dcs.oak.jate.model.CorpusCustom;
import uk.ac.shef.dcs.oak.jate.model.Term;
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

		CandidateTermExtractor npextractor = new NounPhraseExtractorOpenNLP(stop, lemmatizer);
		// CandidateTermExtractor wordextractor = new WordExtractor(stop,
		// lemmatizer, false, 1);

		GlobalIndexBuilderMem builder = new GlobalIndexBuilderMem();
		GlobalIndexMem termDocIndex = builder.build(new CorpusCustom(content), npextractor);

		WordCounter wordcounter = new WordCounter();

		TermFreqCounter npcounter = new TermFreqCounter();
		FeatureCorpusTermFrequency termCorpusFreq = new FeatureBuilderCorpusTermFrequency(npcounter, wordcounter,
				lemmatizer).build(termDocIndex);

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
