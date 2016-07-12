package eu.unimi.it.matchmaker;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.assert4soa.datamodel.PartiallyOrderedSet;
import eu.assert4soa.datamodel.datamodelFactory;
import eu.unimi.it.data.Certificate;
import eu.unimi.it.data.Configuration;
import eu.unimi.it.data.Match;
import eu.unimi.it.data.Order;
import eu.unimi.it.data.Result;
import eu.unimi.it.ematchmakerconfiguration.QualityLevel;
import eu.unimi.it.ontologyquestioner.IOntologyQuestioner;
import eu.unimi.it.retriever.Retriever;

/**
 * Class that we used to perform ranking of certificated based on the 
 * Ontology in the DB and the configuration in input
 * @author Jonatan Maggesi
 *
 */
public class SlaveMatchMaker {
	static Logger logger = LogManager
			.getLogger(SlaveMatchMaker.class.getName());

	private static final int NPROVE = 1;
	private String PATH = null; // = "/Users/jonny/Desktop/RESULT/Random-Shadow/TempResult/";
	private IOntologyQuestioner ontology = null;
	private datamodelFactory dmf = datamodelFactory.eINSTANCE;
	private Configuration config;
	private Multimap<QualityLevel, Certificate> groupedQuality;
	private Multimap<QualityLevel, Certificate> groupedByMetrics;

	/**
	 * Constructor of the class
	 * @param ontology the IOntologyQuestioner implementation to use in the process
	 * @param config The configuration to use to perform the ranking
	 * @param path The path to store the result
	 */
	public SlaveMatchMaker(IOntologyQuestioner ontology, Configuration config, String path) {
		this.ontology = ontology;
		this.config = config;
		this.PATH = path;
	}

	/**
	 * Get the result of the match and ordering operation
	 * 
	 * @param candidates the candidates to use in the ranking 
	 * @return the partial order of the certificates with the result
	 */
	public PartiallyOrderedSet<Set<Certificate>> getResults(
			Set<Certificate> candidates) {
		PartiallyOrderedSet<Set<Certificate>> partialResult = dmf
				.createPartialOrder();
		long[] match = new long[NPROVE];
		long[] comparison = new long[NPROVE];
		
		Result result = null;
		
		Object[] tempCandidates = candidates.toArray(); 
		for(int i = 0; i < NPROVE; i++){
			
			for(int j = 0; j < tempCandidates.length; j++){
				candidates.add((Certificate) tempCandidates[j]);
			}
			
			System.out.println("N° Candidates Beginning: " + candidates.size());
			
			long startTimeMatch = System.nanoTime();
			candidates = this.matching(candidates);
			long endTimeMatch = System.nanoTime();
			
			System.out.println("N° Candidates end: " + candidates.size());
			
			partialResult.add(candidates);
			
			long startTimeComparison = System.nanoTime();
			result = this.comparison(partialResult, candidates.size());
			//partialResult = this.comparison(partialResult);
			long endTimeComparison = System.nanoTime();
			
			partialResult = (PartiallyOrderedSet<Set<Certificate>>) result.getPartialOrder();
			
			System.out.println("Match: " + (endTimeMatch - startTimeMatch));
			match[i] = TimeUnit.MILLISECONDS.convert((endTimeMatch - startTimeMatch), TimeUnit.NANOSECONDS);
			
			System.out.println("MATCH TIME: " + match[i]);
			
			comparison[i] = TimeUnit.MILLISECONDS.convert((endTimeComparison - startTimeComparison), TimeUnit.NANOSECONDS);
//			
			System.out.println("COMPARISON TIME: " + comparison[i]);
//			System.out.println("");
			
//			long matchSingle = TimeUnit.MILLISECONDS.convert((endTimeMatch - startTimeMatch), TimeUnit.NANOSECONDS);
//			long compSingle = TimeUnit.MILLISECONDS.convert((endTimeComparison - startTimeComparison), TimeUnit.NANOSECONDS);
//			System.out.println("Matching time: " + matchSingle);
//			System.out.println("Comparison Time: " + compSingle);
//			System.out.println("Total Time: " + (compSingle + matchSingle));
		}
		
		double matchMedia = calculateAverage(match);
		double compMedia = calculateAverage(comparison);
		
		System.out.println("Match Media; " + matchMedia);
		System.out.println("Comparison Media " + compMedia);
		System.out.println("TOT MEDIA: " + (matchMedia + compMedia));
		
		printFile(PATH + "res(" + tempCandidates.length + "-" + config.getProperties().size() + ").txt", tempCandidates.length, config.getProperties().size(), matchMedia + compMedia, result.getPropertyCompleted());
		
		
		return partialResult;
	}

	/**
	 * Print a file with the time result
	 * @param file The path to use to the save
	 * @param nCert The number of cert used in the ranking operation
	 * @param nProp The number of property used in the ranking operation
	 * @param totalTime The total time 
	 * @param numPropertyToComplete The number of property that we used to have a perfect ranking
	 */
	private void printFile(String file, int nCert, int nProp, double totalTime, int numPropertyToComplete) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
			        new FileOutputStream(file, true), "UTF-8"));
			writer.write(nCert + " " + nProp + " " + totalTime + " " + numPropertyToComplete);
			//writer.write(nCert + " " + nProp + " " + totalTime);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private double calculateAverage(long[] values) {
		long sum = 0;
		for(long val : values){
			sum += val;
		}
		return (double)sum/(double)values.length;
	}

	/**
	 * Compare the element
	 * 
	 * @param partialResult
	 * @return
	 */
	private Result<PartiallyOrderedSet<Set<Certificate>>> comparison(
			PartiallyOrderedSet<Set<Certificate>> partialResult, int size) {
		Result result = null;
		
		// TODO: per la property bisogna ordinare facendo un ciclo
		// sulle proprietà contenute nella conf
		if (config.getOrder().equals(Order.PropertyFirst)) {
			//partialResult = this.propertyCompare(partialResult);
			//TODO: Disabilitato per i test
			//partialResult = this.compareByValue(partialResult);
			
			result = this.propertyCompare(partialResult, size);
		} else {
			//TODO: Disabilitato per i test
			//partialResult = this.compareByValue(partialResult);
			//partialResult = this.propertyCompare(partialResult);
			
			result = this.propertyCompare(partialResult, size);
		}
		return result;
	}

	/**
	 * Compare the property cycling on the queue
	 * 
	 * @param partialResult
	 * @return
	 */
	private Result propertyCompare(
			PartiallyOrderedSet<Set<Certificate>> partialResult, int size) {
		int count = 0;
		boolean isCompleted = false;
		Result result = new Result<PartiallyOrderedSet<Set<Certificate>>>();
		for (Match match : config.getProperties()) {
			count++;
			partialResult = this.compareByProperty(match, partialResult);
			// check se l'albero è già ordinato
			// in teoria sarebbe meglio fermare qua l'esecuzione se il partialOrder
			// è già ordinato
			isCompleted = isCompleted(partialResult, size);
			if(isCompleted && !result.isSetted()){
				result.setPartialOrder(partialResult);
				result.setPropertyCompleted(count);
			} else if (!isCompleted) {
				result.setPartialOrder(partialResult);
			}
		}
		return result;
	}

	/**
	 * Check if the partial order is ranked
	 * @param partialResult The partial order to check
	 * @param numCandidates the number of candidates
	 * @return true if is completely ordered
	 */
	private boolean isCompleted(
			PartiallyOrderedSet<Set<Certificate>> partialResult, int numCandidates) {
		int temp = 0;
		
		Iterator iter = partialResult.iterator();
		while(iter.hasNext()){
			iter.next();
			temp++;
		}
		
		if(temp < numCandidates)
			return false;
		else
			return true;
	}

	/**
	 * Match the element based on the configuration in input
	 * 
	 * @param candidates
	 * @return
	 */
	private Set<Certificate> matching(Set<Certificate> candidates) {
		//TODO: Disabilitato per i test
		//candidates = this.matchingValue(candidates, config.getValue());
		
		// FIXME: magari passare un array, così non perdo il riferimento
		Object[] properties = config.getProperties().toArray();
		if (properties != null) {
			for (int i = 0; i < properties.length; i++) {
				// FIXME: controllare che le properties della configurazione non
				// siano eliminate
				candidates = this.matchingProperty(candidates,
						(Match) properties[i]);
			}
		}
		return candidates;
	}

	/**
	 * Match based on the property
	 * 
	 * @param candidates
	 * @param match
	 * @return
	 */
	private Set<Certificate> matchingProperty(Set<Certificate> candidates,
			Match match) {
		if (candidates != null && match.getPropertyName() != ""
				&& match.getPropertyValue() != "") {
			// Trovo le sottoclassi da utilizzare per fare il match
			ArrayList<String> properties = ontology.getSubClasses(
					match.getPropertyName(), match.getPropertyValue(), false);
			HashSet<String> sec = null;
			if (properties != null) {
				sec = new HashSet<String>(properties);
			} else {
				sec = new HashSet<String>(1);
			}
			sec.add(match.getPropertyValue());

			// TODO: Faccio il match delle proprietà
			Iterator<Certificate> iter = candidates.iterator();
			int temp = 0;
			while (iter.hasNext()) {
				//System.out.println("TEMPO : "+ temp++);
				Certificate cert = iter.next();
				String certProperty = cert.getProperty(match.getPropertyName());
				if (certProperty != null) {
					// se il la proprietà contenuta nel cert è anche
					// nell'hashset allora lo tengo altrimenti lo rimuovo
					if (!sec.contains(certProperty)) {
						System.out.println("FILE: " + match.getPropertyName());
						System.out.println("Proprietà cert: " + certProperty);
						System.out.println("CERT eliminato: " + cert.getId());
						iter.remove();
					}
				} else {
					iter.remove();
				}
			}
		}
		return candidates;
	}

	/**
	 * 
	 * @param candidates
	 * @param value
	 * @return
	 */
	private Set<Certificate> matchingValue(Set<Certificate> candidates,
			int value) {
		if (candidates != null) {
			for (Iterator<Certificate> iter = candidates.iterator(); iter
					.hasNext();) {
				Certificate cand = iter.next();
				if (cand.getCode() < value) {
					iter.remove();
				}
			}
		}
		return candidates;
	}

	/**
	 * 
	 * @param partialResult
	 * @return
	 */
	public PartiallyOrderedSet<Set<Certificate>> compareByValue(
			PartiallyOrderedSet<Set<Certificate>> partialResult) {
		if (!partialResult.isEmpty() && partialResult != null) {
			PartiallyOrderedSet<Set<Certificate>> results = dmf
					.createPartialOrder();
			Set<Set<Certificate>> maxs = partialResult.getMaximelElements();
			for (Set<Certificate> max : maxs) {
				results = recursiveCompareByValue(max, partialResult, results);
			}
			return results;
		}
		return partialResult;
	}

	/**
	 * 
	 * @param max
	 * @param partialResult
	 * @param results
	 * @return
	 */
	private PartiallyOrderedSet<Set<Certificate>> recursiveCompareByValue(
			Set<Certificate> max,
			PartiallyOrderedSet<Set<Certificate>> partialResult,
			PartiallyOrderedSet<Set<Certificate>> results) {

		if (max != null) {
			HashSet<Set<Certificate>> childs = Retriever.getChildrenElement(
					max, partialResult);
			if (max.size() > 1) {
				Multimap<Integer, Certificate> grouped = HashMultimap.create();
				Multimap<Integer, Integer> hierarchy = HashMultimap.create();
				PartiallyOrderedSet<Set<Certificate>> tempPartiallyOrderedSet = dmf
						.createPartialOrder();
				grouped = Retriever.groupByValue(max);
				hierarchy = Retriever.getValueHierarchy(grouped, hierarchy);
				Multimap<Integer, Integer> newHier = Retriever.buildHierarchy(
						hierarchy, grouped.keySet());
				tempPartiallyOrderedSet = Retriever.<Integer> getPartialOrder(
						grouped, newHier);
				results = Retriever.mergePartialOrder3(max, partialResult,
						tempPartiallyOrderedSet);
			} else {
				results = partialResult;
			}

			if (childs != null) {
				for (Set<Certificate> child : childs) {
					results = recursiveCompareByValue(child, partialResult,
							results);
				}
			}
		}

		return results;
	}

	/**
	 * Method used to compare by property
	 * @param compare The property to use to rank
	 * @param partialResult The partial order to rank
	 * @return the partial order ranked
	 */
	public PartiallyOrderedSet<Set<Certificate>> compareByProperty(
			Match compare, PartiallyOrderedSet<Set<Certificate>> partialResult) {

		if (!partialResult.isEmpty() && partialResult != null) {
			PartiallyOrderedSet<Set<Certificate>> results = dmf
					.createPartialOrder();
			Set<Set<Certificate>> maxs = partialResult.getMaximelElements();
			for (Set<Certificate> max : maxs) {
				results = recursiveCompareByProperty(compare, max,
						partialResult, results);
			}
			return results;
		}
		return partialResult;
	}

	/**
	 * Recursive call to rank the partial order
	 * @param compare The property to use to rank
	 * @param max The element
	 * @param partialResult The partialResult
	 * @param results The results obtained at the moment
	 * @return the partial order ranked
	 */
	private PartiallyOrderedSet<Set<Certificate>> recursiveCompareByProperty(
			Match compare, Set<Certificate> max,
			PartiallyOrderedSet<Set<Certificate>> partialResult,
			PartiallyOrderedSet<Set<Certificate>> results) {

		if (max != null) {
			HashSet<Set<Certificate>> childs = Retriever.getChildrenElement(
					max, partialResult);

			if (max.size() > 1) {
				Multimap<String, Certificate> grouped = HashMultimap.create();
				Multimap<String, String> hierarchy = HashMultimap.create();
				PartiallyOrderedSet<Set<Certificate>> tempPartiallyOrderedSet = dmf
						.createPartialOrder();
				// TODO: mi serve la chiave della proprietà di sicurezza da
				// usare per cercare la proprietà
				// occhio anche alla getHierarchy e alla groupBySecurityProperty
				grouped = Retriever.groupBySecurityProperty(
						compare.getPropertyName(), max);
				// TODO: devo mettere anche la chiave della proprietà oltre al
				// nome della stessa
				hierarchy = Retriever.getHierarchy(compare.getPropertyName(),
						compare.getPropertyValue(), hierarchy, ontology);
				Multimap<String, String> newHier = Retriever.buildHierarchy(
						hierarchy, grouped.keySet());
				tempPartiallyOrderedSet = Retriever.<String> getPartialOrder(
						grouped, newHier);
				// results = Retriever.mergePartialOrder(max, partialResult,
				// tempPartiallyOrderedSet, results);
				results = Retriever.mergePartialOrder3(max, partialResult,
						tempPartiallyOrderedSet);
			} else {
				results = partialResult;
			}

			if (childs != null) {
				for (Set<Certificate> child : childs) {
					results = recursiveCompareByProperty(compare, child,
							partialResult, results);
				}
			}
		}
		return results;
	}

}
