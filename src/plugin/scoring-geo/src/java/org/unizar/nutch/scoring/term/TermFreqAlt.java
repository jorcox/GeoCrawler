package org.unizar.nutch.scoring.term;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class TermFreqAlt {

	private final static Logger LOG = LoggerFactory.getLogger(TermFreqAlt.class);
	private final static String STOPLIST_FILE = "stoplist.txt";
	private ArrayList<String> stopWords = new ArrayList<>();

	public TermFreqAlt() {
		loadStopWords();
	}

	/**
	 * Loading stopwords from STOPLIST_FILE
	 */
	private void loadStopWords() {
		try {
			URL url = getClass().getResource(STOPLIST_FILE);
			System.out.println("URL soptlist -> " + url);
			Scanner sc = new Scanner(new File(STOPLIST_FILE));
			while (sc.hasNextLine()) {
				String stopWord = sc.nextLine();
				if (stopWord.length() > 0 && !stopWord.substring(0, 1).equals("#")) {
					stopWords.add(stopWord);
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			LOG.error("Error loading stop list");
		}
	}

	public String filterStopWords(String word) {
		return stopWords.contains(word) ? "" : word;
	}

	public LinkedHashMap<String, Float> extractTerms(String text) {
		LinkedHashMap<String, Integer> terms = new LinkedHashMap<>();
		/* Pre filtering */
		/* Deleting all unnecesary symbols */
		String textNS = deleteSymbols(text);

		textNS = textNS.toLowerCase();
		/* Replacing 2 or more whitespaces with only one */
		textNS = textNS.replaceAll(" +", " ");

		/* Get word list */
		String[] words = textNS.split(" ");
		int wordsRepeatedNumber = words.length;
		int uniqueWordsNumber = 0;
		for (String word : words) {
			/* Post filtering */
			word = deleteSymbols(word);
			if (word.length() >= 2) {
				if (terms.containsKey(word) && !stopWords.contains(word)) {
					int ac = terms.get(word);
					terms.put(word, ++ac);
				} else if (!stopWords.contains(word)) {
					uniqueWordsNumber++;
					terms.put(word, 1);
				}
			}
		}
		Map<String, Integer> shortedTerms = sortByValue(terms);
		// System.out.println(shortedTerms.toString());
		return scoreTerm(shortedTerms, uniqueWordsNumber, wordsRepeatedNumber);
	}

	private LinkedHashMap<String, Float> scoreTerm(Map<String, Integer> shortedTerms, int uniqueWordsNumber,
			int wordsRepeatedNumber) {
		LinkedHashMap<String, Float> scoreMap = new LinkedHashMap<>();
		for (Entry<String, Integer> entry : shortedTerms.entrySet()) {
			scoreMap.put(entry.getKey(), ((float) entry.getValue() / uniqueWordsNumber) * wordsRepeatedNumber);
		}
		return scoreMap;
	}

	public static Map<String, Integer> sortByValue(Map<String, Integer> map) {
		List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<String, Integer> result = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public String deleteSymbols(String text) {

		//return text.replaceAll("[\\.|\\,|\\:|\\+|\\\"|\\'|\\-|\\{|\\}|\\?|\\¿|\\/|\\€|\\$|\\!|\\¡|\\;|"
		//		+ "\\”|\\“|\\&|\\(|\\)|\\[|\\]|\\º|\\ª|\\@|\\¬|\\|\\=|\\_|\\*|\\%|[1-9]+]", "");
		return text.replaceAll("[\\.,:\\+\"'\\-\\{\\}\\?¿/€\\$!¡;&\\”\\“\\(\\)\\[\\]ºª@¬\\=_\\*%1-9]", "");

	}

	/*
	 * private String deleteAccent(String text) { String[] accentLetters = {
	 * "á", "é", "í", "ó", "ú", "à", "è", "ì", "ò", "ù" }; String[] letters = {
	 * "a", "e", "i", "o", "u", "a", "e", "i", "o", "u" }; for (int i = 0; i <
	 * accentLetters.length; i++) { text = text.replace(accentLetters[i],
	 * letters[i]); } return text; }
	 */

	public static void main(String[] args) {
		String texto = "EL ESPAÑOL - Diario digital, plural, libre, indomable, tuyo cerrar El Español para Android Ver Cerrar Login Perfil de usuario Favoritos Suscríbete Cerrar sesión España Mundo Economía Prodigios Podium Miradas Jaleos Ocio Coliseo El Blog S&D La edición Zona Ñ Buscar Cerrar Buscar {Ñ} El Español La edición Suscríbete Zona Ñ La Edición En Portada El Río España Mundo Economía Prodigios Podium Miradas Jaleos Ocio Coliseo El Blog S&D El Río España Detenida la cúpula de Manos Limpias Carlota Guindal Manos Limpias Facebook Twitter Google + LinkedIn Menéame Whatsapp Fútbol Champions: City-Real Madrid y Atlético-Bayern Mario Díaz Champions League Facebook Twitter Google + LinkedIn Menéame Whatsapp Salud La Coca-Cola como auxiliar de la 'quimio' Ainhoa Iriberri Oncología Facebook Twitter Google + LinkedIn Menéame Whatsapp Libros Cervantes hace pellas: \"Nos da vergüenza leerlo\" Lorena G. Maldonado IV Centenario Cervantes Facebook Twitter Google + LinkedIn Menéame Whatsapp Anterior Siguiente 1 de 4 José Manuel Soria en el que era su escaño J.C. Hidalgo Efe Soria dimite por el escándalo de sus sociedades offshore Renuncia como ministro de Industria, a la presidencia del PP canario y a su escaño en el Congreso. Rajoy está \"muy preocupado y decepcionado\" con él porque \"ha mentido\". Ya anunció que hoy no acudiría al Consejo de Ministros. Ana I. Gracia José Manuel Soria Facebook Twitter Google + LinkedIn Menéame Whatsapp La cronología del caso Soria: la semana más difícil del exministro La dimisión del ya extitular de Industria calma la tensión en Génova, que ha vivido \"horas críticas\" tras las informaciones sobre su entramado societario. Gonzalo Araluce José Manuel Soria Facebook Twitter Google + LinkedIn Menéame Whatsapp Las empresas de Soria en el extranjero no presentan cuentas desde 1998 Es imposible conocer el montante de fondos que fue traspasado por la red opaca a paraísos fiscales. Tampoco hay información contable sobre los negocios de la familia. Daniel Montero Gonzalo Araluce Papeles de Panamá Facebook Twitter Google + LinkedIn Menéame Whatsapp Cuatro sombras (y una luz) de Soria como ministro Nadie derramará una lágrima por Soria Pedro J. Ramírez José Manuel Soria Facebook Twitter Google + LinkedIn Menéame Whatsapp La porra tuitera: ¿acabará Soria en Repsol? Patricia Morales Gobierno de España Facebook Twitter Google + LinkedIn Menéame Whatsapp Efe Las cinco dimisiones de Rajoy Desde que el PP llegó al poder, son cinco los ministros que han dimitido. El presidente no ha cesado a ninguno. Ana I. Gracia Mariano Rajoy Brey Facebook Twitter Google + LinkedIn Menéame Whatsapp Rivera pide a Rajoy que explique en el Congreso la dimisión de Soria El líder de C's le dice al presidente catalán Carles Puigdemont que la independencia es una \"vía muerta\". J.S. / Agencias Papeles de Panamá Facebook Twitter Google + LinkedIn Menéame Whatsapp La sintaxis de Panamá David Álvarez José Manuel Soria Facebook Twitter Google + LinkedIn Menéame Whatsapp Rajoy ante los Alpes Ferrer Molina Mariano Rajoy Brey Facebook Twitter Google + LinkedIn Menéame Whatsapp Arnaldo Otegi durante su rueda de prensa este viernes Luis Tejido Efe Otegi: \"Voy a ser el lehendakari más peligroso para el Estado\" Afirma que la izquierda abertzale ya ha reconocido el daño causado \"varias veces\", aunque no niega que tengan que hacer \"más cosas\". J. O. | Agencias Arnaldo Otegi Facebook Twitter Google + LinkedIn Menéame Whatsapp J. J. Guillén Efe Detienen a los presidentes de Manos Limpias y Ausbanc por extorsión La detención de Miguel Bernard, del sindicato Manos Limpias, pone en jaque su acusación en el 'caso Nóos'. Carlota Guindal Manos Limpias Facebook Twitter Google + LinkedIn Menéame Whatsapp Efe El presidente de Ausbanc saca pecho en Twitter tras anunciarse su detención En la jungla : Luis Pineda cree \"que la Policía ha engañado a su hijo y le ha arrebatado las llaves\". Diego González Detenciones Facebook Twitter Google + LinkedIn Menéame Whatsapp El PP entregará la cabeza del alcalde de Granada para retener la Alcaldía Los 'populares' de Génova 13 y los andaluces se han unido y comprometido con Ciudadanos a que el alcalde se aparte para evitar una moción de censura. Ana I. Gracia José Torres Hurtado Facebook Twitter Google + LinkedIn Menéame Whatsapp Efe Camps ataca a la Abogacía del Estado: “Ustedes están para controlar que se hacen bien las cosas” Sostiene que desconocía que Iñaki Urdangarin estaba detrás de Nóos. Carlota Guindal Caso Nóos Facebook Twitter Google + LinkedIn Menéame Whatsapp Camps pidió a Urdangarin que le colocase de ponente en Davos El expresidente valenciano solicitó estar en  la más prestigiosa cita económica mundial como contraprestación mientras le daba contratos públicos. Esteban Urreiztieta Daniel Montero Caso Nóos Facebook Twitter Google + LinkedIn Menéame";
		TermFreqAlt tf = new TermFreqAlt();
		tf.loadStopWords();
		LinkedHashMap<String, Float> terms = tf.extractTerms(texto);
		System.out.println(terms);
	}
}
