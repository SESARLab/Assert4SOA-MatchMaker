package eu.unimi.matchmaker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import eu.assert4soa.datamodel.ASSERT;
import eu.assert4soa.datamodel.PartiallyOrderedSet;
import eu.assert4soa.datamodel.datamodelFactory;
import eu.unimi.ematchmakerconfiguration.ComparisonType;
import eu.unimi.ematchmakerconfiguration.EMatchMakerConfiguration;
import eu.unimi.ematchmakerconfiguration.EMatchMakerModel;
import eu.unimi.ematchmakerconfiguration.EMatchMakerSecurityProperty;
import eu.unimi.ematchmakerconfiguration.EMatchMakerTestEvidence;
import eu.unimi.ematchmakerconfiguration.EvidenceWeight;
import eu.unimi.ematchmakerconfiguration.ModelType;
import eu.unimi.ematchmakerconfiguration.ModelWeight;
import eu.unimi.ematchmakerconfiguration.OrderComparison;
import eu.unimi.ematchmakerconfiguration.QualityLevel;
import eu.unimi.ontologyquestioner.IOntologyQuestioner;
import eu.unimi.retriever.Retriever;

public class SlaveMatchMakerE {
	private static final int SIZEMAX = 1;
	private static final String TESTTYPE = "TestType";
	private static final String TESTCATEGORIES = "TestClass";
	private EMatchMakerConfiguration config = null;
	private IOntologyQuestioner ontology = null;
	private datamodelFactory dmf = datamodelFactory.eINSTANCE;
	Table<String, String, Double> modelIndexTable = null;
	private Multimap<QualityLevel, ASSERT> groupedQuality;
	private Multimap<QualityLevel, ASSERT> groupedByMetrics;
	
	/**
	 * Constructor
	 * @param config
	 * @param ontology
	 */
	public SlaveMatchMakerE(EMatchMakerConfiguration config, IOntologyQuestioner ontology){
		this.config = config;
		this.ontology = ontology;
	}
	
	public Multimap<QualityLevel, ASSERT> getGroupedByMetrics() {
		return groupedByMetrics;
	}

	public void setGroupedByMetrics(Multimap<QualityLevel, ASSERT> groupedByMetrics) {
		this.groupedByMetrics = groupedByMetrics;
	}

	public Multimap<QualityLevel, ASSERT> getGroupedQuality() {
		return groupedQuality;
	}

	public void setGroupedQuality(Multimap<QualityLevel, ASSERT> groupedQuality) {
		this.groupedQuality = groupedQuality;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public EMatchMakerConfiguration getConfig() {
		return config;
	}

	/**
	 * 
	 * @param config
	 */
	public void setConfig(EMatchMakerConfiguration config) {
		this.config = config;
	}

	/**
	 * 
	 * @return
	 */
	public IOntologyQuestioner getOntology() {
		return ontology;
	}

	/**
	 * 
	 * @param ontology
	 */
	public void setOntology(IOntologyQuestioner ontology) {
		this.ontology = ontology;
	}

	/**
	 * 
	 * @param config
	 * @param ontology
	 * @return
	 */
	public PartiallyOrderedSet<Set<ASSERT>>getResults(EMatchMakerConfiguration config, IOntologyQuestioner ontology, Set<ASSERT> candidates){
		
		PartiallyOrderedSet<Set<ASSERT>> partialResult = dmf.createPartialOrder();
		candidates = this.matching(candidates);
		partialResult.add(candidates);
		partialResult = this.comparison(partialResult);
//		System.out.println("");
//		System.out.println("STAMPA DEL RISULTATO FINALE");
//		this.printPartialOrder(partialResult);
		return partialResult;
	}

	/**
	 * Comparison Function
	 * @param partialResult
	 * @return
	 */
	public PartiallyOrderedSet<Set<ASSERT>> comparison(PartiallyOrderedSet<Set<ASSERT>> partialResult) {
		//long startTime = System.currentTimeMillis();
		
		if(config.getOrderComparison().equals(OrderComparison.PF)){
			partialResult = this.compareByProperty(partialResult);
			partialResult = this.compareByModel(partialResult);
			//FIXME: X INTEGRAZIONE ADESSO DISABILITO
			//partialResult = this.compareByEvidence(partialResult);
		} else {
			partialResult = this.compareByModel(partialResult);
			//FIXME: X INTEGRAZIONE ADESSO DISABILITO
			//partialResult = this.compareByEvidence(partialResult);
			partialResult = this.compareByProperty(partialResult);
		}

		//long endTime = System.currentTimeMillis();
		//System.out.println("La durata del comparison è stata: " + (endTime - startTime));
		
		return partialResult;
	}

	public void printPartialOrder(PartiallyOrderedSet<Set<ASSERT>> partialOrder){
		Set<Set<ASSERT>> maxim = partialOrder.getMaximelElements();
		Set<Set<ASSERT>> minim = partialOrder.getMinimalElements();
		for(Set<ASSERT> max : maxim){
			System.out.println("Massimi del partial order: "); 
			Retriever.printNameID(max);
		}
		for(Set<ASSERT> min : minim){
			System.out.println("Minimi del partial order: "); 
			Retriever.printNameID(min);
		}
		
		HashMap<Set<ASSERT>, HashSet<Set<ASSERT>>> greater = partialOrder.getGreaterRelations();
		for(Map.Entry<Set<ASSERT>, HashSet<Set<ASSERT>>> entry : greater.entrySet()){
			System.out.println();
			System.out.println("Chiave del greater: ");
			Retriever.printNameID(entry.getKey());
			HashSet<Set<ASSERT>> values = entry.getValue();
			
			
			for(Set<ASSERT> value : values){
				System.out.println("Valori del Greater: ");
				Retriever.printNameID(value);
			}
		}
	}
	
	/**
	 * Compare by Evidence
	 * @param partialResult
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PartiallyOrderedSet<Set<ASSERT>> compareByEvidence(
			PartiallyOrderedSet<Set<ASSERT>> partialResult) {
		
		if(!partialResult.isEmpty() && partialResult != null){
			PartiallyOrderedSet<Set<ASSERT>> results = dmf.createPartialOrder();
			Set<Set<ASSERT>> maxs = partialResult.getMaximelElements();
			for(Set<ASSERT> max : maxs){
				results = recursiveCompareByTestEvidence(max, partialResult, results);
			}
			
			//FIXME: PER INTEGRAZIONE
//			partialResult = null;
//			partialResult = this.compareByTestType(results);
//			
//			partialResult = null;
//			partialResult = this.compareByCardinality(results);
//			
//			results = null;
//			results = this.compareByTestMetrics(partialResult);
//			
//			return results;
			
			//FIXME: X DEMO ASSERT
			partialResult = null;
			partialResult = this.compareByTestType(results);
			
			results = null;
			results = this.compareByCardinality(partialResult);
			
			partialResult = null;
			partialResult = this.compareByTestMetrics(results);
			
			return partialResult;
			
		}
		return partialResult;
	}

	private PartiallyOrderedSet<Set<ASSERT>> compareByCardinality(
			PartiallyOrderedSet<Set<ASSERT>> partialResult) {
		if(!partialResult.isEmpty() && partialResult != null){
			PartiallyOrderedSet<Set<ASSERT>> results = dmf.createPartialOrder();
			Set<Set<ASSERT>> maxs = partialResult.getMaximelElements();
			for(Set<ASSERT> max : maxs){
				results = recursiveCompareByCardinality(max, partialResult, results);
			}
			return results;
		}
		return partialResult;
	}

	private PartiallyOrderedSet<Set<ASSERT>> recursiveCompareByCardinality(
			Set<ASSERT> max, PartiallyOrderedSet<Set<ASSERT>> partialResult, PartiallyOrderedSet<Set<ASSERT>> results) {
		
		if(max != null ){
			HashSet<Set<ASSERT>> childs = Retriever.getChildrenElement(max, partialResult);
			if(max.size() > 1){
				Multimap<Integer, ASSERT> grouped = HashMultimap.create();
				Multimap<Integer, Integer> hierarchy = HashMultimap.create();
				PartiallyOrderedSet<Set<ASSERT>> tempPartiallyOrderedSet = dmf.createPartialOrder();
				grouped = Retriever.groupByCardinality(max);
				hierarchy = Retriever.getCardHierarchy(grouped, hierarchy);
				Multimap<Integer, Integer> newHier = Retriever.buildHierarchy(hierarchy, grouped.keySet());
				tempPartiallyOrderedSet = Retriever.<Integer>getPartialOrder(grouped, newHier);
				results = Retriever.mergePartialOrder3(max, partialResult, tempPartiallyOrderedSet);
			} else {
				results = partialResult;
			}
			
			if(childs != null){
				for(Set<ASSERT> child : childs){
					results = recursiveCompareByCardinality(child, partialResult, results);
				}
			}
		}

		return results;
	}

	private PartiallyOrderedSet<Set<ASSERT>> recursiveCompareByTestEvidence(
			Set<ASSERT> max, PartiallyOrderedSet<Set<ASSERT>> partialResult, PartiallyOrderedSet<Set<ASSERT>> results) {
		//&& max.size() > 1
		if(max != null ){
			HashSet<Set<ASSERT>> childs = Retriever.getChildrenElement(max, partialResult);
			if(max.size() > 1){
				Multimap<String, ASSERT> grouped = HashMultimap.create();
				Multimap<String, String> hierarchy = HashMultimap.create();
				PartiallyOrderedSet<Set<ASSERT>> tempPartiallyOrderedSet = dmf.createPartialOrder();
				grouped = Retriever.groupByTestCategory(max);
				hierarchy = Retriever.getHierarchy(TESTCATEGORIES, hierarchy, ontology);
				Multimap<String, String> newHier = Retriever.buildHierarchy(hierarchy, grouped.keySet());
				tempPartiallyOrderedSet = Retriever.<String>getPartialOrder(grouped, newHier);

				results = Retriever.mergePartialOrder3(max, partialResult, tempPartiallyOrderedSet);
			} else {
				results = partialResult;
			}
			
			if(childs != null){
				for(Set<ASSERT> child : childs){
					results = recursiveCompareByTestEvidence(child, partialResult, results);
				}
			}
		}
		return results;
	}

	private PartiallyOrderedSet<Set<ASSERT>> compareByTestType(
			PartiallyOrderedSet<Set<ASSERT>> partialResult) {
		
		if(!partialResult.isEmpty() && partialResult != null){
			PartiallyOrderedSet<Set<ASSERT>> results = dmf.createPartialOrder();
			Set<Set<ASSERT>> maxs = partialResult.getMaximelElements();
			for(Set<ASSERT> max : maxs){
				results = recursiveCompareByTestType(max, partialResult, results);
			}
			return results;
		}
		return partialResult;
	}

	private PartiallyOrderedSet<Set<ASSERT>> recursiveCompareByTestType(
			Set<ASSERT> max, PartiallyOrderedSet<Set<ASSERT>> partialResult, PartiallyOrderedSet<Set<ASSERT>> results) {
		if(max != null){
			HashSet<Set<ASSERT>> childs = Retriever.getChildrenElement(max, partialResult);
			if(max.size() > 1){
				Multimap<String, ASSERT> grouped = HashMultimap.create();
				Multimap<String, String> hierarchy = HashMultimap.create();
				PartiallyOrderedSet<Set<ASSERT>> tempPartiallyOrderedSet = dmf.createPartialOrder();
				grouped = Retriever.groupByTestType(max);
				hierarchy = Retriever.getHierarchy(TESTTYPE, hierarchy, ontology);
				Multimap<String, String> newHier = Retriever.buildHierarchy(hierarchy, grouped.keySet());
				tempPartiallyOrderedSet = Retriever.<String>getPartialOrder(grouped, newHier);

				results = Retriever.mergePartialOrder3(max, partialResult, tempPartiallyOrderedSet);
//				System.out.println("");
//				System.out.println("Stampo Risultato Test Evidence:");
//				this.printPartialOrder(results);
			} else {
				results = partialResult;
			}
			
			if(childs != null){
				for(Set<ASSERT> child : childs){
					results = recursiveCompareByTestType(child, partialResult, results);
				}
			}
		}
		return results;
	}

	private PartiallyOrderedSet<Set<ASSERT>> compareByTestMetrics(
			PartiallyOrderedSet<Set<ASSERT>> partialResult) {
		
		if(!partialResult.isEmpty() && partialResult != null){
			PartiallyOrderedSet<Set<ASSERT>> results = dmf.createPartialOrder();
			Set<Set<ASSERT>> maxs = partialResult.getMaximelElements();
			for(Set<ASSERT> max : maxs){
				results = recursiveCompareByTestMetrics(max, partialResult, results);
			}
			return results;
		}
		return partialResult;
	}

	private PartiallyOrderedSet<Set<ASSERT>> recursiveCompareByTestMetrics(
			Set<ASSERT> max, PartiallyOrderedSet<Set<ASSERT>> partialResult, PartiallyOrderedSet<Set<ASSERT>> results) {
		if(max != null ){
			HashSet<Set<ASSERT>> childs = Retriever.getChildrenElement(max, partialResult);
			
			if(max.size() > 1){
				Multimap<QualityLevel, ASSERT> grouped = HashMultimap.create();
				Multimap<QualityLevel, QualityLevel> hierarchy = HashMultimap.create();
				PartiallyOrderedSet<Set<ASSERT>> tempPartiallyOrderedSet = dmf.createPartialOrder();
				if(this.config.getTestEvidence().getWeight() != null){
					grouped = Retriever.groupByTestMetrics(max, this.config.getTestEvidence().getWeight());
				} else {
					grouped = Retriever.groupByTestMetrics(max, EvidenceWeight.COVERAGE);
				}
				
				hierarchy = Retriever.getQualityLevelIndex();
				Multimap<QualityLevel, QualityLevel> newHier = Retriever.buildHierarchy(hierarchy, grouped.keySet());
				tempPartiallyOrderedSet = Retriever.<QualityLevel>getPartialOrder(grouped, newHier);
				results = Retriever.mergePartialOrder3(max, partialResult, tempPartiallyOrderedSet);
			} else {
				results = partialResult;
			}
			
			if(childs != null){
				for(Set<ASSERT> child : childs){
					results = recursiveCompareByTestMetrics(child, partialResult, results);
				}
			}
			
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public PartiallyOrderedSet<Set<ASSERT>> compareByModel(
			PartiallyOrderedSet<Set<ASSERT>> partialResult) {
		
		if(!partialResult.isEmpty() && partialResult != null){
			PartiallyOrderedSet<Set<ASSERT>> results = dmf.createPartialOrder();
			Set<Set<ASSERT>> maxs = partialResult.getMaximelElements();
			for(Set<ASSERT> max : maxs){
				results = recursiveCompareByModel(max, partialResult, results);
			}
			//System.out.println("Stampo Risultati");
			//this.printPartialOrder(results);
				
		partialResult = null;
		partialResult = this.compareByModelIndex(results);
		
		return partialResult;
		
//		if(config.getComparisonType().equals(ComparisonType.ADVANCED)){
//			partialResult = this.compareByModelIndex(results);
//			
//			return partialResult;
//		}
//		return results;
		}
		
		return partialResult;
	}

	private PartiallyOrderedSet<Set<ASSERT>> recursiveCompareByModel(
			Set<ASSERT> max, PartiallyOrderedSet<Set<ASSERT>> partialResult, PartiallyOrderedSet<Set<ASSERT>> results) {
		//&& max.size() > 1
		if(max != null){
			HashSet<Set<ASSERT>> childs = Retriever.getChildrenElement(max, partialResult);
			
			if(max.size() > 1){
				Multimap<ModelType, ASSERT> grouped = HashMultimap.create();
				Multimap<ModelType, ModelType> hierarchy = HashMultimap.create();
				
				PartiallyOrderedSet<Set<ASSERT>> tempPartiallyOrderedSet = dmf.createPartialOrder();
				grouped = Retriever.groupByModelType(max);
				hierarchy = Retriever.getModelHierarchy();
				Multimap<ModelType, ModelType> newHier = Retriever.buildHierarchy(hierarchy, grouped.keySet());
				tempPartiallyOrderedSet = Retriever.<ModelType>getPartialOrder(grouped, newHier);
				
				results = Retriever.mergePartialOrder3(max, partialResult, tempPartiallyOrderedSet);
				
			}else {
				results = partialResult;
			}
			
			if(childs != null){
				for(Set<ASSERT> child : childs){
					results = recursiveCompareByModel(child, partialResult, results);
				}
			}
		} 
		
		
		return results;
	}

	private PartiallyOrderedSet<Set<ASSERT>> compareByModelIndex(
			PartiallyOrderedSet<Set<ASSERT>> partialResult) {
	
		if(!partialResult.isEmpty() && partialResult != null){
			PartiallyOrderedSet<Set<ASSERT>> results = dmf.createPartialOrder();
			Set<Set<ASSERT>> maxs = partialResult.getMaximelElements();
			for(Set<ASSERT> max : maxs){
				results = recursiveCompareByModelIndex(max, partialResult, results);
			}
			return results;
		}
		return partialResult;
		
	}

	private PartiallyOrderedSet<Set<ASSERT>> recursiveCompareByModelIndex(
			Set<ASSERT> max, PartiallyOrderedSet<Set<ASSERT>> partialResult, PartiallyOrderedSet<Set<ASSERT>> results) {
		
		if(max != null ){
			
			HashSet<Set<ASSERT>> childs = Retriever.getChildrenElement(max, partialResult);
			
			if(max.size() > 1){
				Multimap<QualityLevel, ASSERT> grouped = HashMultimap.create();
				Multimap<QualityLevel, QualityLevel> hierarchy = HashMultimap.create();
				PartiallyOrderedSet<Set<ASSERT>> tempPartiallyOrderedSet = dmf.createPartialOrder();
				grouped = Retriever.groupByModelIndex(max, this.config.getModel().getModelWeight(), this.modelIndexTable);
				hierarchy = Retriever.getQualityLevelIndex();
				Multimap<QualityLevel, QualityLevel> newHier = Retriever.buildHierarchy(hierarchy, grouped.keySet());
				tempPartiallyOrderedSet = Retriever.<QualityLevel>getPartialOrder(grouped, newHier);

				results = Retriever.mergePartialOrder3(max, partialResult, tempPartiallyOrderedSet);

			} else {
				results = partialResult;
			}
			
			if(childs != null){
				Iterator<Set<ASSERT>> iter = childs.iterator();
				while(iter.hasNext()){
					Set<ASSERT> child = iter.next();
					results = recursiveCompareByModelIndex(child, partialResult, results);
				}
			}
		}
		return results;
	}

	/**
	 * Compare by Property
	 * @param partialResult
	 * @return
	 */
	public PartiallyOrderedSet<Set<ASSERT>> compareByProperty(
			PartiallyOrderedSet<Set<ASSERT>> partialResult) {

		if(!partialResult.isEmpty() && partialResult != null){
			PartiallyOrderedSet<Set<ASSERT>> results = dmf.createPartialOrder();
			Set<Set<ASSERT>> maxs = partialResult.getMaximelElements();
			for(Set<ASSERT> max : maxs){
				results = recursiveCompareByProperty(max, partialResult, results);
			}
			return results;
		}
		return partialResult;
		
//		while(iter.hasNext()){
//			Object temp = iter.next();
//			if(temp instanceof Set<?>){
//				if(((Set<?>) temp).size() > SIZEMAX){
//					grouped = Retriever.groupBySecurityProperty((Set<ASSERT>)iter);
//					hierarchy = Retriever.getHierarchy(config.getSecurityPropertyComplete(), hierarchy);
//					tempPartiallyOrderedSet = Retriever.<String>getPartialOrder(grouped, hierarchy);
//					partialResult = Retriever.mergePartialOrder((Set<ASSERT>)iter, partialResult, tempPartiallyOrderedSet);
//				}
//			}
//		}
		//return partialResult;
	}

	private PartiallyOrderedSet<Set<ASSERT>> recursiveCompareByProperty(
			Set<ASSERT> max, PartiallyOrderedSet<Set<ASSERT>> partialResult, PartiallyOrderedSet<Set<ASSERT>> results) {
		
		if(max != null){
			HashSet<Set<ASSERT>> childs = Retriever.getChildrenElement(max, partialResult);
			
			if(max.size() > 1){
				Multimap<String, ASSERT> grouped = HashMultimap.create();
				Multimap<String, String> hierarchy = HashMultimap.create();
				PartiallyOrderedSet<Set<ASSERT>> tempPartiallyOrderedSet = dmf.createPartialOrder();
				grouped = Retriever.groupBySecurityProperty(max);
				hierarchy = Retriever.getHierarchy(config.getSecurityPropertyComplete(), hierarchy, ontology);
				Multimap<String, String> newHier = Retriever.buildHierarchy(hierarchy, grouped.keySet());
				tempPartiallyOrderedSet = Retriever.<String>getPartialOrder(grouped, newHier);
				//results = Retriever.mergePartialOrder(max, partialResult, tempPartiallyOrderedSet, results);
				results = Retriever.mergePartialOrder3(max, partialResult, tempPartiallyOrderedSet);
			} else {
				results = partialResult;
			}
			
			if(childs != null){
				for(Set<ASSERT> child : childs){
					results = recursiveCompareByProperty(child, partialResult, results);
				}
			}
		}
		return results;
	}

	/**
	 * General matching function
	 * @param candidates
	 * @return
	 */
	public Set<ASSERT> matching(Set<ASSERT> candidates) {
		//long startTime = System.currentTimeMillis();
		
		// Match the Security property
		candidates = this.matchProperty(candidates, config.getSecurityPropertyComplete());
		// Match the Model property
		candidates = this.matchModel(candidates, config.getModel(), config.getComparisonType());
		
		//FIXME: x INTEGRAZIONE adesso disabilito
		
		// Match the Test Evidence
		//candidates = this.matchEvidence(candidates, config.getTestEvidence(), config.getComparisonType());
		
		//long endTime = System.currentTimeMillis();
		//System.out.println("The total time of matching is: " + (endTime-startTime));
	
		return candidates;
		
	}

	/**
	 * Match based on the evidence
	 * @param candidates
	 * @param testEvidence
	 * @param comparisonType
	 * @return
	 */
	public Set<ASSERT> matchEvidence(Set<ASSERT> candidates,
			EMatchMakerTestEvidence testEvidence, ComparisonType comparisonType) {
		if(testEvidence != null && !candidates.isEmpty() && candidates != null){
			Set<ASSERT> results = new HashSet<ASSERT>();
			if(testEvidence.getTestCategory() != null && testEvidence.getTestCategory() != ""){
				//FIXME: PER INTEGRAZIONE
//				String testCategories = testEvidence.getTestCategory();
				//FIXME: PER DEMO
				String testCategories = testEvidence.getTestCategory().toLowerCase();
				Multimap<String, ASSERT> grouped = Retriever.groupByTestCategory(candidates);
				if(grouped.containsKey(testCategories)){
					results.addAll(grouped.get(testCategories));
				}
			} else {
				results = candidates;
			}
			
			if(testEvidence.getTestType() != null && testEvidence.getTestType() != ""){
				Set<ASSERT> resultsTesType = new HashSet<ASSERT>();
				ArrayList<String> testTypes = this.ontology.getSubClasses(testEvidence.getTestType().toLowerCase(), false);
				testTypes.add(testEvidence.getTestType().toLowerCase());
				Multimap<String, ASSERT> groupedByTestType = Retriever.groupByTestType(results);
				for(String testType : testTypes){
					if(groupedByTestType.containsKey(testType)){
						resultsTesType.addAll(groupedByTestType.get(testType));
					}
				}
				results = resultsTesType;
			}
			
			if(testEvidence.getCardinality() > 0){
				Set<ASSERT> resultCard = new HashSet<ASSERT>();
				Multimap<Integer, ASSERT> groupedByCardinality = Retriever.groupByCardinality(results);
				if(groupedByCardinality != null && !groupedByCardinality.isEmpty()){
					for(Integer card : groupedByCardinality.keySet()){
						if(card != null && card >= testEvidence.getCardinality()){
							resultCard.addAll(groupedByCardinality.get(card));
						}
					}
				}
				results = resultCard;
			}
			
			if(testEvidence.getWeight() != null){
				Set<ASSERT> resultsTestMetrics = new HashSet<ASSERT>();
				// TODO: X DEMO
				groupedByMetrics = Retriever.groupByTestMetrics(results, testEvidence.getWeight());
				if(results.size() > 0){
					System.out.println("Result size: " + results.size());
				} else {
					System.out.println("Result is 0 in groupByTestMetrics");
				}
				
				System.out.println("GroupMetrics size: " + groupedByMetrics.size());
				//TODO: X INTEGRAZIONE ASSERT CON I LIVELLI DI TEST DA SALVARE ANCHE IN STRUTTURA ESTERNA PER DEMO
				//Multimap<QualityLevel, ASSERT> groupedByMetrics = Retriever.groupByTestMetrics(results, testEvidence.getWeight());
				ArrayList<QualityLevel> levels = Retriever.getQualityLevelSubClasses(testEvidence.getTestEvidenceQualityLevel(), false);
				for(QualityLevel level : levels){
					if(getGroupedByMetrics().containsKey(level)){
						resultsTestMetrics.addAll(getGroupedByMetrics().get(level));
					}
				}
				results = resultsTestMetrics;
			}
			
			return results;
			
//			if(comparisonType.equals(ComparisonType.ADVANCED)){
//				if(testEvidence.getWeight() != null){
//					Set<ASSERT> resultsTestMetrics = new HashSet<ASSERT>();
//					Multimap<QualityLevel, ASSERT> groupedByMetrics = Retriever.groupByTestMetrics(results, testEvidence.getWeight());
//					ArrayList<QualityLevel> levels = Retriever.getQualityLevelSubClasses(testEvidence.getTestEvidenceQualityLevel(), false);
//					for(QualityLevel level : levels){
//						if(groupedByMetrics.containsKey(level)){
//							resultsTestMetrics.addAll(groupedByMetrics.get(level));
//						}
//					}
//					results = resultsTestMetrics;
//				}
//			}
//			return results;
		}
		return candidates;
	}

	/**
	 * Matching by the security property
	 * @param candidates
	 * @param securityProperty
	 * @return
	 */
	public Set<ASSERT> matchProperty(Set<ASSERT> candidates, String securityProperty) {
		if(securityProperty != null && securityProperty != ""){
			ArrayList<String> securityProperties = this.ontology.getSubClasses(securityProperty, false);
			HashSet<String> sec = null;
			if(securityProperties != null){
				sec = new HashSet<String>(securityProperties);
			} else {
				sec = new HashSet<String>(1);
			}
			sec.add(securityProperty);
			Multimap<String, ASSERT> grouped = Retriever.groupBySecurityProperty(candidates);
			if(grouped != null){
				Set<ASSERT> results = new HashSet<ASSERT>(grouped.values().size());
				
//				for(Map.Entry<String, ASSERT> entry : grouped.entries()){
//					if(sec.contains(entry.getKey())){
//						results.addAll(grouped.get(entry.getKey()));
//					}
//				}
				
				for(String groupKey : grouped.keySet()){
					System.out.println("GRUPPO: " + groupKey);
				}
				
				for(String securityProp : sec){
					System.out.println("SOTTOCLASSI: " + securityProp);
					if(grouped.containsKey(securityProp)){
						results.addAll(grouped.get(securityProp));
					}
				}
				return results;
			}
		}
		return candidates;
	}
	
	/**
	 * Matching by the Model Type
	 * @param candidates
	 * @param model
	 * @param comparisonType
	 * @return
	 */
	public Set<ASSERT> matchModel(Set<ASSERT> candidates, EMatchMakerModel model, ComparisonType comparisonType){
		// Calcolo la tabella dei massimi e minimi e dei pesi
		// se non c'è la preferenza dei pesi, uso il default
		if(model.getModelWeight() != null){
			this.modelIndexTable = Retriever.getModelWeightTable(model.getModelWeight());
			this.modelIndexTable = Retriever.getModelIndexTable(candidates, modelIndexTable);
		} else {
			this.modelIndexTable = Retriever.getModelWeightTable(ModelWeight.AVERAGE);
			this.modelIndexTable = Retriever.getModelIndexTable(candidates, modelIndexTable);
		}
		
//		if(model != null && model.getModelName() != null){
		if(model != null){
			if(model.getModelName() == null){
				model.setModelName(ModelType.WSDL);
			}
			// FIXME: mettere un metodo nell'ontologia che chiama il retriever con questo metodo
			ArrayList<ModelType> modelTypes = Retriever.getModelTypeSubClasses(model.getModelName(), false);
			Multimap<ModelType, ASSERT> grouped = Retriever.groupByModelType(candidates);
			Set<ASSERT> results = new HashSet<ASSERT>();
			//TODO: X DEMO EL to INT
			groupedQuality = HashMultimap.create();
			if(grouped != null && !grouped.isEmpty()){
				for(ModelType modelType : modelTypes ){
					//TODO: X DEMO ELIMINARE X INTEGRAZIONE
						Multimap<QualityLevel, ASSERT> group2 = Retriever.groupByModelIndex((Set<ASSERT>) grouped.get(modelType), model.getModelWeight(), this.modelIndexTable);
						for(Map.Entry<QualityLevel, ASSERT> entries : group2.entries()){
							groupedQuality.put(entries.getKey(), entries.getValue());
						}
					
					

					if(grouped.containsKey(modelType)){
						// If the multimap contains the securityProperty and the securityProperty is the security
						// property searched check the model index
						if(modelType == model.getModelName() && comparisonType.equals(ComparisonType.ADVANCED)){
														//TODO: X INTEGRAZIONE
							//Multimap<QualityLevel, ASSERT> groupedQuality = Retriever.groupByModelIndex((Set<ASSERT>) grouped.get(modelType), model.getModelWeight(), this.modelIndexTable);
							ArrayList<QualityLevel> qualityLevels = Retriever.getQualityLevelSubClasses(model.getModelLevel(), false);
							for(QualityLevel qualityLevel : qualityLevels){
								if(getGroupedQuality().containsKey(qualityLevel)){
									results.addAll(getGroupedQuality().get(qualityLevel));
								}
							}
						} else {
							results.addAll(grouped.get(modelType));
						}
					}
				}
				return results;
			}
		}
		return candidates;
	}


}
