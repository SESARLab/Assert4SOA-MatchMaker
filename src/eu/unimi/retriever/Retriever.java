package eu.unimi.retriever;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import eu.assert4soa.datamodel.ASSERT;
import eu.assert4soa.datamodel.PartiallyOrderedSet;
import eu.assert4soa.datamodel.datamodelFactory;
import eu.unimi.ematchmakerconfiguration.EvidenceWeight;
import eu.unimi.ematchmakerconfiguration.ModelType;
import eu.unimi.ematchmakerconfiguration.ModelWeight;
import eu.unimi.ematchmakerconfiguration.QualityLevel;
import eu.unimi.ontologyquestioner.BaseXOntologyManager;
import eu.unimi.ontologyquestioner.IOntologyQuestioner;


public final class Retriever {
	
	private static final String CONCATENATOR = "_";
	private static final double PERCDENOMINATOR = 100.0;
	private static final String ATTACKCOVERAGE = "AttackCoverage";
	private static final double TESTMETRICSNUMBER = 5;
	private static final double ZERO = 0.0;
	
	private Retriever(){
		throw new AssertionError();
	}
	
	/**
	 * Return the Security Property Name
	 * @param assertDoc
	 * @return
	 */
	public static String getSecurityPropertyName(ASSERT assertDoc){
		if (assertDoc != null){
			//FIXME: eliminare il tolowercase e modificare la mia ontologia per rispecchiare quella dell'Ontology Manager ASSERT
			String result = assertDoc.evaluateXpathOnAssertDOM_AsString("//ASSERTTypeSpecific/ASSERT-E/Property/PropertyName").toLowerCase();
			//FIXME: PER INTEGRAZIONE
			//String result = assertDoc.evaluateXpathOnAssertDOM_AsString("//ASSERTTypeSpecific/ASSERT-E/Property/PropertyName");
			String[] results = result.split("#");
			if (results.length == 2){
				return results[1];
			}
		}
		return null;
	}
	
	/**
	 * Return the nameId of the service
	 * @param assertDoc
	 * @return
	 */
	public static String getNameID(ASSERT assertDoc){
		if(assertDoc != null){
			return assertDoc.evaluateXpathOnAssertDOM_AsString("//ASSERTCore/SecurityProperty/NameID");
		}
		return null;
	}
	
	/**
	 * Print the name id of the certificates
	 * @param assertList
	 */
	public static void printNameID(Set<ASSERT> assertList){
		if(assertList != null && assertList.size() > 0){
			for(ASSERT assertDoc : assertList){
				System.out.println(Retriever.getNameID(assertDoc));
			}
		}
	}
	
	/**
	 * Return the model type
	 * @param assertDoc
	 * @return
	 */
	public static String getModelType(ASSERT assertDoc){
		if (assertDoc != null){
			return assertDoc.evaluateXpathOnAssertDOM_AsString("//ASSERTTypeSpecific/ASSERT-E/ServiceModel/ModelType");
		}
		return null;
	}
	
	/**
	 * Return the context of the 
	 * @param assertDoc
	 * @return
	 */
	public static String getContext(ASSERT assertDoc){
		if(assertDoc != null){
			String completeContext = null;
			String context = null;
			String[] realCtx = null;
			ArrayList<Node> secContexts = assertDoc.evaluateXpathOnAssertDOM_AsNodeSet("//ASSERTCore/SecurityProperty/PropertyContexts/PropertyContext");
			if(secContexts != null){
				for(Node ctx : secContexts){ 
					if(ctx.getLocalName() == "PropertyContext"){
						context = ctx.getTextContent().toLowerCase();
						if(context != null && context != ""){
							realCtx = context.split("#");
						}
						if(completeContext == null || completeContext == ""){
							completeContext = realCtx[1].toLowerCase();
						} else {
							completeContext += CONCATENATOR + realCtx[1].toLowerCase();
						}
					}
				}
				return completeContext;
			}
		}
		return null;
	}
	
	/**
	 * Return the Test Category of the Assert
	 * @param assertDoc
	 * @return
	 */
	public static String getTestCategory(ASSERT assertDoc){
		if (assertDoc != null){
			return assertDoc.evaluateXpathOnAssertDOM_AsString("//ASSERTTypeSpecific/ASSERT-E/TestEvidence/TestCategory");
		}
		return null;
	}
	
	/**
	 * Return the Test Type of the Assert
	 * @param assertDoc
	 * @return
	 */
	public static String getTestType(ASSERT assertDoc){
		if (assertDoc != null){
			//FIXME: eliminare il tolowercase per eliminare i problemi nell'ontologia ASSERT, cambiare anche l'ontologia mia
			return assertDoc.evaluateXpathOnAssertDOM_AsString("//ASSERTTypeSpecific/ASSERT-E/TestEvidence/TestType").toLowerCase().replace(" ", "_").replaceAll("/", "_");
		}
		return null;
	}
	
	/**
	 * Return the Test Metrics of an Assert
	 * @param assertDoc
	 * @return
	 */
	public static HashMap<String, Double> getTestMetrics(ASSERT assertDoc){
		if (assertDoc != null){
			String name, tempValue;
			double value;
			ArrayList<Node> testMetrics = assertDoc.evaluateXpathOnAssertDOM_AsNodeSet("//ASSERTTypeSpecific/ASSERT-E/TestMetrics");
			
			// TODO: Aggiungere il controllo di dimensione e di diversità da null
			
			HashMap<String, Double> results = new HashMap<String, Double>(testMetrics.size());
			
			for(Node metric : testMetrics){
				for(Node met = metric.getFirstChild(); met != null;){
					
					Node nextMetric = met.getNextSibling();
					name = met.getLocalName();
					tempValue = met.getTextContent().replace("%", "");
					
					//TODO : AGGIUNGERE METODO DI CONTROLLO DELLA STRINGA DEL VALORE (SOLO NUMERI ACCETTATI)
					if(name != null && !tempValue.equals("NONE") && !tempValue.equals("") && name != "Others"){
						try{
						value = Double.parseDouble(tempValue);
						} catch (Exception e){
							value = 0.0;
						}
						if (value != 0.0){
							results.put(name, value/PERCDENOMINATOR);	
						}else{
							results.put(name, value);
						}
					}
					met = nextMetric;
				}
			}
			return results;
		}
		return null;
	}
	
	/**
	 * Return the Model indexes of an Assert
	 * @param assertDoc
	 * @return
	 */
	public static HashMap<String, Double> getModelIndexes(ASSERT assertDoc){
		if (assertDoc != null){
			ArrayList<Node> indexesList = assertDoc.evaluateXpathOnAssertDOM_AsNodeSet("//ASSERTTypeSpecific/ASSERT-E/ServiceModel/Index");
			if(indexesList != null){
				String name = null;
				double value;
				
				HashMap<String, Double> indexes = new HashMap<String, Double>();
				for(Node index : indexesList){
					for(Node elem = index.getFirstChild(); elem != null;){
						Node nextElement = elem.getNextSibling();
						if(elem.getLocalName() != null && elem.getLocalName().equals("Name")){
							name = elem.getTextContent();
						}
						
						// FIXME: Controllare se funziona per tutti i casi
						if(elem.getLocalName() != null && name != null && elem.getLocalName().equals("Value")){
							value = Double.parseDouble(elem.getTextContent());
							indexes.put(name, value);
							name = null;
							value = ZERO;
						}
						
						elem = nextElement;
					}
				}		
				return indexes;
			}
		}
		return null;
	}

	/**
	 * Return the Table with the index weight for every property
	 * @param weight
	 * @return
	 */
	public static Table<String, String, Double> getModelWeightTable(ModelWeight weight){
		Table<String, String, Double> modelIndexesTable = HashBasedTable.create();
		switch (weight) {
		case CONFIDENT:
			modelIndexesTable.put("Number of units", "weight", 0.13);
			modelIndexesTable.put("Number of nodes", "weight", 0.13);
			modelIndexesTable.put("Edges", "weight", 0.13);
			modelIndexesTable.put("Number of linearity independent path",
					"weight", 0.13);
			modelIndexesTable.put("Max path length", "weight", 0.13);
			modelIndexesTable.put("Mean rate branching", "weight", 0.13);
			modelIndexesTable.put("Mean rate of boolean conditions", "weight",
					0.13);
			modelIndexesTable.put("Number of cycles", "weight", 0.03);
			modelIndexesTable.put("Number of connected components", "weight",
					0.03);
			modelIndexesTable.put("Node variance", "weight", 0.03);
			break;
		case DIFFIDENT:
			modelIndexesTable.put("Number of units", "weight", 0.04);
			modelIndexesTable.put("Number of nodes", "weight", 0.04);
			modelIndexesTable.put("Edges", "weight", 0.04);
			modelIndexesTable.put("Number of linearity independent path",
					"weight", 0.04);
			modelIndexesTable.put("Max path length", "weight", 0.04);
			modelIndexesTable.put("Mean rate branching", "weight", 0.04);
			modelIndexesTable.put("Mean rate of boolean conditions", "weight",
					0.04);
			modelIndexesTable.put("Number of cycles", "weight", 0.24);
			modelIndexesTable.put("Number of connected components", "weight",
					0.24);
			modelIndexesTable.put("Node variance", "weight", 0.24);
			break;
		default:
			modelIndexesTable.put("Number of units", "weight", 0.1);
			modelIndexesTable.put("Number of nodes", "weight", 0.1);
			modelIndexesTable.put("Edges", "weight", 0.1);
			modelIndexesTable.put("Number of linearity independent path",
					"weight", 0.1);
			modelIndexesTable.put("Max path length", "weight", 0.1);
			modelIndexesTable.put("Mean rate branching", "weight", 0.1);
			modelIndexesTable.put("Mean rate of boolean conditions", "weight",
					0.1);
			modelIndexesTable.put("Number of cycles", "weight", 0.1);
			modelIndexesTable.put("Number of connected components", "weight",
					0.1);
			modelIndexesTable.put("Node variance", "weight", 0.1);
			break;
		}
		
		return modelIndexesTable;
		
	}

	/**
	 * Return the modelIndexTable for the ASSERTS in input
	 * @param assertList
	 * @param modelIndexTable
	 * @return
	 */
	public static Table<String, String, Double> getModelIndexTable(Set<ASSERT> assertList, Table<String, String, Double> modelIndexTable){
		HashMap<String, Double> modelIndexes = null;
		Double tempValue = null;
		String tempKey = null;
		
		for(ASSERT assertEl : assertList){
			modelIndexes = Retriever.getModelIndexes(assertEl);
			tempKey = null;
			
			for(Map.Entry<String, Double> entry : modelIndexes.entrySet()){
				// Se nella tabella è presente l'indice contenuto nel certificato
				tempKey = entry.getKey();
				if(modelIndexTable.containsRow(tempKey)){
					// Parte dei minimi
					tempValue = modelIndexTable.get(tempKey, "Min");
					if(tempValue == null || tempValue > entry.getValue()){
						modelIndexTable.put(tempKey, "Min", entry.getValue());
					}
					// Parte dei massimi
					tempValue = modelIndexTable.get(tempKey, "Max");
					if(tempValue == null || tempValue < entry.getValue()){
						modelIndexTable.put(tempKey, "Max", entry.getValue());
					}
				}
			}
		}
		
		return modelIndexTable;
	}

	/**
	 * Return an Multimap with the association between the ASSERT and the level of Quality (GROUP BY Model Index)
	 * @param assertList
	 * @return
	 */
	public static Multimap<QualityLevel, ASSERT> groupByModelIndex(Set<ASSERT> assertList, ModelWeight weight, Table<String, String, Double> modelIndexTable){
		// TODO: Questa potrei passargliela come parametro ?
		//Table<String, String, Double> modelIndexTable = Retriever.getModelWeightTable(weight);
		if (modelIndexTable != null){
			//modelIndexTable = Retriever.getModelIndexTable(assertList, modelIndexTable);
			HashSet<String> negativeModelIndex = Retriever.getNegativeModelIndex();
			// TODO: VEDERE SE TENERE IL SECONDO PARAMETRO IN UN SET
			Multimap<QualityLevel, ASSERT> hierarchy = HashMultimap.create();
			//HashMap<ASSERT, QualityLevel> hierarchy = new HashMap<ASSERT, QualityLevel>(assertList.size());
			HashMap<String, Double> modelIndexes = null;
			Set<String> rowKeys = modelIndexTable.rowKeySet();
			QualityLevel qualityLevel;
			// Calcolo del quality level del singolo ASSERT
			for(ASSERT element : assertList){
				// Prendo tutti gli indici dell'ASSERT in Esame
				modelIndexes = Retriever.getModelIndexes(element);
				if(modelIndexes != null){
					// Calcolo i valori della media
					// Per ogni riga della tabella modelIndexTable(tabella con Pesi - Min - Max)
					double value = 0.0;
					double tempValue = 0.0;
					for(String row : rowKeys){
						if(modelIndexes.containsKey(row)){
							if(negativeModelIndex.contains(row)){
								// Calcolo indice negativo
								value += (1 - Retriever.calcNormalizedIndex(modelIndexes.get(row), modelIndexTable.get(row, "Min"), modelIndexTable.get(row, "Max"), modelIndexTable.get(row, "weight"))) * modelIndexTable.get(row, "weight");
							} else {
								// Calcolo indice positivo
								value += (Retriever.calcNormalizedIndex(modelIndexes.get(row), modelIndexTable.get(row, "Min"), modelIndexTable.get(row, "Max"), modelIndexTable.get(row, "weight"))) * modelIndexTable.get(row, "weight");
							}
						}
					}
					qualityLevel = Retriever.calculateQualityLevel(value);
					if(qualityLevel != null){
						//hierarchy.put(element, qualityLevel);
						hierarchy.put(qualityLevel, element);
						
					} else {
						hierarchy.put(qualityLevel, element);
					}
				} else {
					hierarchy.put(QualityLevel.LOW, element);
				}
			}
			return hierarchy;
			
		}
		return null;		
	}

	/**
	 * Group the assertList by the security property
	 * @param assertList
	 * @return
	 */
	public static Multimap<String, ASSERT> groupBySecurityProperty(Set<ASSERT> assertList){
		if(assertList != null && !assertList.isEmpty()){
			Multimap<String, ASSERT> hierarchy = HashMultimap.create();
			String completeSecurityProperty = null;
			// Per ogni ASSERT calcolo la proprietà di sicurezza e poi li raggruppo tutti per la singola proprietà
			for(ASSERT assertDoc : assertList){
				completeSecurityProperty = Retriever.getCompleteSecurityProperty(assertDoc);
				if(completeSecurityProperty != null){
					hierarchy.put(completeSecurityProperty, assertDoc);
				}
			}
			return hierarchy;
		}
		return null;
	}
	
	/**
	 * Return the complete security property of the ASSERT
	 * @param assertDoc
	 * @return
	 */
	public static String getCompleteSecurityProperty(ASSERT assertDoc){
		// TODO: Questa è una funzione da rivedere per capire bene come mappare la parte di proprietà di sicurezza 
		String completeSecurityProperty = null;
		String attribute = null;
		if(assertDoc != null){
			completeSecurityProperty = Retriever.getSecurityPropertyName(assertDoc);
			ArrayList<Node> attributesNode = assertDoc.evaluateXpathOnAssertDOM_AsNodeSet("//ASSERTTypeSpecific/ASSERT-E/Property/ClassAttribute/*");
			
			for(Node securityAttribute : attributesNode){
				//FIXME: PER DEMO
				if(securityAttribute.getLocalName() == "Name" || securityAttribute.getLocalName() == "Value"){
						attribute = securityAttribute.getTextContent();
						if(attribute != null && !attribute.equals("")){
							completeSecurityProperty += CONCATENATOR + securityAttribute.getTextContent();
							System.out.println("Complete Security Property: " + completeSecurityProperty);
						}
				}
				//FIXME: PER INTEGRAZIONE
//				if(securityAttribute.getLocalName() == "Value"){
//					attribute = securityAttribute.getTextContent();
//					if(attribute != null && !attribute.equals("")){
//						completeSecurityProperty += CONCATENATOR + securityAttribute.getTextContent();
//					}
//				}	
			}
			//FIXME: PER DEMO
			String context = Retriever.getContext(assertDoc);
			if(context != null){
				completeSecurityProperty += CONCATENATOR + context;
			}
			completeSecurityProperty = completeSecurityProperty.replace(" ", CONCATENATOR);
		}
		System.out.println(completeSecurityProperty);
		return completeSecurityProperty;
	}
	
	/**
	 * Group the set of ASSERT by the ModelType
	 * @param assertList
	 * @return
	 */
	public static Multimap<ModelType, ASSERT> groupByModelType(Set<ASSERT> assertList){
		if(assertList != null && !assertList.isEmpty()){
			Multimap<ModelType, ASSERT> hierarchy = HashMultimap.create();
			for(ASSERT assertDoc : assertList){
				hierarchy.put(ModelType.valueOf(Retriever.getModelType(assertDoc)), assertDoc);
			}
			return hierarchy;
		}
		return null;
	}
	
	/**
	 * Group by Test Category of the ASSERT
	 * @param assertList
	 * @return
	 */
	public static Multimap<String, ASSERT> groupByTestCategory(Set<ASSERT> assertList){
		if(assertList != null && !assertList.isEmpty()){
			Multimap<String, ASSERT> hierarchy = HashMultimap.create();
			for(ASSERT assertDoc : assertList){
				//FIXME: PER INTEGRAZIONE
				hierarchy.put(Retriever.getTestCategory(assertDoc), assertDoc);
				//FIXME: PER DEMO
				//hierarchy.put(Retriever.getTestCategory(assertDoc).toLowerCase(), assertDoc);
			}
			return hierarchy;
		}
		return null;
	}
	
	/**
	 * Group by Test Type of the ASSERT
	 * @param assertList
	 * @return
	 */
	public static Multimap<String, ASSERT> groupByTestType(Set<ASSERT> assertList){
		if(assertList != null && !assertList.isEmpty()){
			Multimap<String, ASSERT> hierarchy = HashMultimap.create();
			for(ASSERT assertDoc : assertList){
				hierarchy.put(Retriever.getTestType(assertDoc), assertDoc);
			}
			return hierarchy;
		}
		return null;
	}
	
	/**
	 * Return the relation with connection between elements even if they aren't connected directly
	 * @param hierarchy
	 * @param grouped
	 * @return
	 */
	public static <E> Multimap<E, E> getRelations(Multimap<E, E> hierarchy, Multimap<E, ASSERT> grouped){
		if(grouped != null && !grouped.isEmpty()){
			if(hierarchy != null && !hierarchy.isEmpty()){
				Multimap<E, E> relations = HashMultimap.create();
				for(E key : grouped.keySet()){
					E tempKey = key;
					relations = Retriever.searchRelations(key, tempKey, hierarchy, grouped, relations);
				}
				return relations;
			}
		}
		return null;
	}
	
	/**
	 * Search the relations between elements
	 * @param key
	 * @param tempKey
	 * @param hierarchy
	 * @param groupedString
	 * @param relations
	 * @return
	 */
	private static <E> Multimap<E, E> searchRelations(E key,
			E tempKey, Multimap<E, E> hierarchy,
			Multimap<E, ASSERT> groupedString,
			Multimap<E, E> relations) {
		if(hierarchy.containsKey(tempKey)){
			Collection<E> values = hierarchy.get(tempKey);
			for(E value : values){
				if(value == null){
					relations.put(key, null);
				} else if (groupedString.containsKey(value)) {
					relations.put(key, value);
				} else {
					Retriever.searchRelations(key, value, hierarchy, groupedString, relations);
				}
			}
		}
		return relations;
	}

	/**
	 * Method used to create partial order with hierarchy and grouped ASSERT
	 * @param grouped
	 * @param hierarchy
	 * @return
	 */
	public static <E> PartiallyOrderedSet<Set<ASSERT>> getPartialOrder(Multimap<E, ASSERT> grouped, Multimap<E,E> hierarchy){
		datamodelFactory dmf = datamodelFactory.eINSTANCE;
		PartiallyOrderedSet<Set<ASSERT>> assertOrder = dmf.createPartialOrder();
		//for(Map.Entry<E, ASSERT> entry : grouped.entries()){
		for(E entry : grouped.keySet()){	
			assertOrder = Retriever.createPartialOrder(null, entry, grouped, hierarchy, assertOrder);
		}
		return assertOrder;
	}
	
	/**
	 * Build the partial hierarchy based on the key in the set keys
	 * @param hierarchy
	 * @param keys
	 * @return
	 */
	public static <E> Multimap<E,E> buildHierarchy(Multimap<E,E> hierarchy, Set<E> keys){
		Multimap<E,E> newHierarchy = HashMultimap.create();
		if(hierarchy != null && !hierarchy.isEmpty()){
			for(E key : keys){
				newHierarchy = Retriever.recursiveBuildHierarchy(key, null, keys, hierarchy, newHierarchy);
			}
		}
		
		return newHierarchy;
		
	}
	
	/**
	 * Build the hierarchy with the recursive function
	 * @param key
	 * @param parent
	 * @param keys
	 * @param hierarchy
	 * @param newHierarchy
	 * @return
	 */
	public static <E> Multimap<E,E> recursiveBuildHierarchy(E key, E parent, Set<E> keys, Multimap<E,E> hierarchy, Multimap<E,E> newHierarchy){
		if(hierarchy.containsKey(key)){
			if(hierarchy.get(key).contains(null)){
				if(parent != null){
					newHierarchy.put(parent, null);
				} else {
					newHierarchy.put(key, null);
				}
				return newHierarchy;
			} else {
				Collection<E> values = hierarchy.get(key);
				if(values != null){
					for(E value : values){
						if(keys.contains(value)){
							if(parent == null){
								newHierarchy.put(key, value);
							} else {
								newHierarchy.put(parent, value);
							}
						} else {
							if(parent != null){
								newHierarchy = Retriever.recursiveBuildHierarchy(value, parent, keys, hierarchy, newHierarchy);
							} else {
								newHierarchy = Retriever.recursiveBuildHierarchy(value, key, keys, hierarchy, newHierarchy);
							}
						}
					}
				}	
			}
		}
		return newHierarchy;
	}
	
	
	/**
	 * Recursive call to create the partial order
	 * @param tempKey
	 * @param key
	 * @param grouped
	 * @param hierarchy
	 * @param order
	 * @return
	 */
	private static <E> PartiallyOrderedSet<Set<ASSERT>> createPartialOrder(E tempKey, E key, Multimap<E,ASSERT> grouped, Multimap<E, E> hierarchy, PartiallyOrderedSet<Set<ASSERT>> order){
//		if(hierarchy.containsKey(key)){
//			order.add((Set<ASSERT>) grouped.get(key));
//			Collection<E> values = hierarchy.get(key);
//			if(tempKey != null){
//				order.addRelation((Set<ASSERT>)grouped.get(key), (Set<ASSERT>)grouped.get(tempKey));
//			}
//			for(E value : values){
//				Retriever.createPartialOrder(key, value, grouped, hierarchy, order);
//			}
//		}
		
		if(hierarchy.containsKey(key)){
			Collection<E> values = hierarchy.get(key);
			if(values == null){
				order.add((Set<ASSERT>)grouped.get(key));
				return order;
			} else {
				for(E value : values){
					if(tempKey != null && !grouped.get(key).isEmpty() && grouped.get(key).size() > 0 ){
						order.addRelation((Set<ASSERT>)grouped.get(key), (Set<ASSERT>)grouped.get(tempKey));
						//System.out.println("Aggiunta Relazione tra padre: " + key + " e figlio: " + tempKey);
					} else {
						if(!grouped.get(key).isEmpty() && grouped.get(key).size() > 0){
							order.add((Set<ASSERT>)grouped.get(key));
							//System.out.println("Aggiunto elemento: " + key);
						}
					}
					order = Retriever.createPartialOrder(key, value, grouped, hierarchy, order);
				}
			}
		}
		return order;
	}
	
	
	public static PartiallyOrderedSet<Set<String>> getPartialOrder2(Multimap<String, String> grouped, Multimap<String,String> hierarchy){
		datamodelFactory dmf = datamodelFactory.eINSTANCE;
		PartiallyOrderedSet<Set<String>> assertOrder = dmf.createPartialOrder();
		for(String entry : grouped.keySet()){
			assertOrder = Retriever.createPartialOrder2(null, entry, grouped, hierarchy, assertOrder);
		}
		return assertOrder;
	}
	
	private static PartiallyOrderedSet<Set<String>> createPartialOrder2(String tempKey, String key, Multimap<String,String> grouped, Multimap<String, String> hierarchy, PartiallyOrderedSet<Set<String>> order){
		if(hierarchy.containsKey(key)){
			order.add((Set<String>) grouped.get(key));
			Collection<String> values = hierarchy.get(key);
			if(tempKey != null){
				order.addRelation((Set<String>)grouped.get(key), (Set<String>)grouped.get(tempKey));
			}
			for(String value : values){
				Retriever.createPartialOrder2(key, value, grouped, hierarchy, order);
			}
		}
		return order;
	}
	
	/**
	 * Merge partial order changing the element in input
	 * @param element
	 * @param parentPartialOrder
	 * @param childPartialOrder
	 * @return
	 */
	public static PartiallyOrderedSet<Set<ASSERT>> mergePartialOrder(Set<ASSERT> element, PartiallyOrderedSet<Set<ASSERT>> parentPartialOrder, PartiallyOrderedSet<Set<ASSERT>> childPartialOrder, PartiallyOrderedSet<Set<ASSERT>> results){
		// TODO: parentPatialOrder != null e children posso eliminarle
		if(element != null && parentPartialOrder != null && childPartialOrder != null){
			HashSet<Set<ASSERT>> parents = Retriever.getParentElement(element, parentPartialOrder);
			HashSet<Set<ASSERT>> childs = Retriever.getChildrenElement(element, parentPartialOrder);
			datamodelFactory dmf = datamodelFactory.eINSTANCE;
			//PartiallyOrderedSet<Set<ASSERT>> results = dmf.createPartialOrder();
			//parentPartialOrder.remove(element);
			Set<Set<ASSERT>> maxs = childPartialOrder.getMaximelElements();
			if(maxs != null && maxs.size() > 0){
				for(Set<ASSERT> max : maxs){
					Retriever.recursiveMerging(max, parents, childs, parentPartialOrder, childPartialOrder, results);
				}
			}
			return results;
		}
		return parentPartialOrder;
	}
	
	private static PartiallyOrderedSet<Set<ASSERT>> recursiveMerging(
			Set<ASSERT> element, Set<Set<ASSERT>> parents,
			Set<Set<ASSERT>> childs,
			PartiallyOrderedSet<Set<ASSERT>> parentPartialOrder,
			PartiallyOrderedSet<Set<ASSERT>> childPartialOrder, 
			PartiallyOrderedSet<Set<ASSERT>> results) {
		if(childPartialOrder != null && !childPartialOrder.isEmpty()){
					if(parents != null){
						results.addRelation(parents, element);
						//parentPartialOrder.addRelation(parents, element);
					} else {
						results.add(element);
						//parentPartialOrder.add(element);
					}
					HashSet<Set<ASSERT>> elementChilds = Retriever.getChildrenElement(element, childPartialOrder);
					//childPartialOrder.remove(max);
					if(elementChilds != null && !elementChilds.isEmpty()){
						HashSet<Set<ASSERT>> maxParent = new HashSet<Set<ASSERT>>();
						maxParent.add(element);
						for(Set<ASSERT> elementChild : elementChilds){
							results = Retriever.recursiveMerging(elementChild, maxParent, childs, parentPartialOrder, childPartialOrder, results);
							//parentPartialOrder = Retriever.recursiveMerging(elementChild, maxParent, childs, parentPartialOrder, childPartialOrder, results);
						}
						return results;
						//return parentPartialOrder;
					} else if(element != null && childs != null){
						results.addRelation(element, childs);
						//TODO: Aggiungere ricorsione sui figli dei figli
						
						
						//parentPartialOrder.addRelation(element, childs);
					}
					//TODO: Vedere se qua va inserito qualche elemento, VA ELIMINATO QUESTO IF
//					if(element != null){
//						parentPartialOrder.addRelation(element, childs);
//					}
				//}
			//}
			// TODO: Vedere se va inserito qualche elemento
		} else if(element != null){
			//parentPartialOrder.add(element);
			//parentPartialOrder.addRelation(element, childs);
			results.addRelation(element, childs);
		}
		return results;
		//return parentPartialOrder;
	}

	public static PartiallyOrderedSet<Set<String>> mergePartialOrder2(Set<String> element, PartiallyOrderedSet<Set<String>> parentPartialOrder, PartiallyOrderedSet<Set<String>> childPartialOrder){
		if(element != null && parentPartialOrder != null && childPartialOrder != null){
			HashSet<Set<String>> parents = Retriever.getParentElement2(element, parentPartialOrder);
			HashSet<Set<String>> childs = Retriever.getChildrenElement2(element, parentPartialOrder);
			parentPartialOrder.remove(element);
			parentPartialOrder = Retriever.recursiveMerging2(null, parents, childs, parentPartialOrder, childPartialOrder);
		}
		return parentPartialOrder;
	}
	
	private static PartiallyOrderedSet<Set<String>> recursiveMerging2(
			Set<String> element, Set<Set<String>> parents,
			Set<Set<String>> childs,
			PartiallyOrderedSet<Set<String>> parentPartialOrder,
			PartiallyOrderedSet<Set<String>> childPartialOrder) {

		if(childPartialOrder != null && !childPartialOrder.isEmpty()){
			Set<Set<String>> maxs = childPartialOrder.getMaximelElements();
			if(maxs != null && !maxs.isEmpty()){
				for(Set<String> max : maxs){
					//parentPartialOrder.add(max);
					if(parents != null){
						parentPartialOrder.addRelation(parents, max);
					} else {
						parentPartialOrder.add(max);
					}
					
					HashSet<Set<String>> elementChilds = Retriever.getChildrenElement2(max, childPartialOrder);
					childPartialOrder.remove(max);
					if(elementChilds != null && !elementChilds.isEmpty()){
						HashSet<Set<String>> maxParent = new HashSet<Set<String>>();
						maxParent.add(max);
						for(Set<String> elementChild : elementChilds){
							Retriever.recursiveMerging2(elementChild, maxParent, childs, parentPartialOrder, childPartialOrder);
						}
					}
					//TODO: Vedere se qua va inserito qualche elemento
					//parentPartialOrder.add(element);
					if(element != null){
						parentPartialOrder.addRelation(element, childs);
					}
				}
			}
			// TODO: Vedere se va inserito qualche elemento
		} else if(element != null){
			//parentPartialOrder.add(element);
			parentPartialOrder.addRelation(element, childs);
		}
		return parentPartialOrder;
	}
	
	
	
	
	public static PartiallyOrderedSet<Set<ASSERT>> mergePartialOrder3(Set<ASSERT> element, PartiallyOrderedSet<Set<ASSERT>> parentPartialOrder, PartiallyOrderedSet<Set<ASSERT>> childPartialOrder){
		if(element != null && parentPartialOrder != null && childPartialOrder != null){
			HashSet<Set<ASSERT>> parents = Retriever.getParentElement(element, parentPartialOrder);
			HashSet<Set<ASSERT>> childs = Retriever.getChildrenElement(element, parentPartialOrder);
			datamodelFactory dmf = datamodelFactory.eINSTANCE;
			//PartiallyOrderedSet<Set<ASSERT>> results = dmf.createPartialOrder();
			parentPartialOrder.remove(element);
			Set<Set<ASSERT>> maxs = childPartialOrder.getMaximelElements();
			if(maxs != null && maxs.size() > 0){
				for(Set<ASSERT> max : maxs){
					Retriever.recursiveMerging3(max, parents, childs, parentPartialOrder, childPartialOrder);
				}
			}
			return parentPartialOrder;
			//parentPartialOrder = Retriever.recursiveMerging(null, parents, childs, parentPartialOrder, childPartialOrder);
		}
		return parentPartialOrder;
		
//		if(element != null && parentPartialOrder != null && childPartialOrder != null){
//			HashSet<Set<ASSERT>> parents = Retriever.getParentElement(element, parentPartialOrder);
//			HashSet<Set<ASSERT>> childs = Retriever.getChildrenElement(element, parentPartialOrder);
//			parentPartialOrder.remove(element);
//			parentPartialOrder = Retriever.recursiveMerging3(null, parents, childs, parentPartialOrder, childPartialOrder);
//		}
//		return parentPartialOrder;
	}
	
	private static PartiallyOrderedSet<Set<ASSERT>> recursiveMerging3(
			Set<ASSERT> element, Set<Set<ASSERT>> parents,
			Set<Set<ASSERT>> childs,
			PartiallyOrderedSet<Set<ASSERT>> parentPartialOrder,
			PartiallyOrderedSet<Set<ASSERT>> childPartialOrder) {

		if(childPartialOrder != null && !childPartialOrder.isEmpty()){
			//parentPartialOrder.add(max);
			if(parents != null){
				parentPartialOrder.addRelation(parents, element);
			} else {
				parentPartialOrder.add(element);
			}

			HashSet<Set<ASSERT>> elementChilds = Retriever.getChildrenElement(element, childPartialOrder);
			//childPartialOrder.remove(element);
			if(elementChilds != null && !elementChilds.isEmpty()){
				HashSet<Set<ASSERT>> maxParent = new HashSet<Set<ASSERT>>();
				maxParent.add(element);
				for(Set<ASSERT> elementChild : elementChilds){
					Retriever.recursiveMerging3(elementChild, maxParent, childs, parentPartialOrder, childPartialOrder);
				}
			}
			//TODO: Vedere se qua va inserito qualche elemento
			//parentPartialOrder.add(element);
			if(element != null && elementChilds == null && childs != null){
				//if(max != null && childs != null){
				parentPartialOrder.addRelation(element, childs);
			}
			// TODO: Vedere se va inserito qualche elemento
		} else if(element != null){
			//parentPartialOrder.add(element);
			parentPartialOrder.addRelation(element, childs);
		}
		return parentPartialOrder;
	}
	
	public static HashSet<Set<String>> getParentElement2(Set<String> element, PartiallyOrderedSet<Set<String>> parentPartialOrder){
		if(element != null && parentPartialOrder != null){
			HashMap<Set<String>, HashSet<Set<String>>> greaterRelations = parentPartialOrder.getLesserRelations();
			if(greaterRelations.containsKey(element)){
				HashSet<Set<String>> parents = greaterRelations.get(element);
				return parents;
			}
		}
		return null;
	}
	
	/**
	 * Method used to retrieve the children of the element in a Partial Order
	 * @param element
	 * @param parentPartialOrder
	 * @return
	 */
	public static HashSet<Set<String>> getChildrenElement2(Set<String> element, PartiallyOrderedSet<Set<String>> parentPartialOrder){
		if(element != null && parentPartialOrder != null){
			HashMap<Set<String>, HashSet<Set<String>>> lesserRelations = parentPartialOrder.getGreaterRelations();
			if(lesserRelations.containsKey(element)){
				HashSet<Set<String>> childrens = lesserRelations.get(element);
				return childrens;
			}
		}
		return null;
	}
	
	
	/**
	 * Method used to retrieve the parent of the element in a Partial Order
	 * @param element
	 * @param parentPartialOrder
	 * @return
	 */
	private static HashSet<Set<ASSERT>> getParentElement(Set<ASSERT> element, PartiallyOrderedSet<Set<ASSERT>> parentPartialOrder){
		if(element != null && parentPartialOrder != null){
			HashMap<Set<ASSERT>, HashSet<Set<ASSERT>>> greaterRelations = parentPartialOrder.getLesserRelations();
			if(greaterRelations.containsKey(element)){
				HashSet<Set<ASSERT>> parents = greaterRelations.get(element);
				return parents;
			}
		}
		return null;
	}
	
	/**
	 * Method used to retrieve the children of the element in a Partial Order
	 * @param element
	 * @param parentPartialOrder
	 * @return
	 */
	public static HashSet<Set<ASSERT>> getChildrenElement(Set<ASSERT> element, PartiallyOrderedSet<Set<ASSERT>> parentPartialOrder){
		if(element != null && parentPartialOrder != null){
			HashMap<Set<ASSERT>, HashSet<Set<ASSERT>>> lesserRelations = parentPartialOrder.getGreaterRelations();
			if(lesserRelations.containsKey(element)){
				HashSet<Set<ASSERT>> childrens = new HashSet<Set<ASSERT>>(lesserRelations.get(element));
				return childrens;
			}
		}
		return null;
	}
	
	/**
	 * Return the value of the index
	 * @param val
	 * @param min
	 * @param max
	 * @param weight
	 * @return
	 */
	private static double calcNormalizedIndex(double val, double min, double max, double weight){
		double index = 0.0;
		double num = 0.0;
		double den = 0.0;
		
		if(!Double.isNaN(val) && !Double.isNaN(min)){
			num = val - min;
		}
		
		if(!Double.isNaN(min) && !Double.isNaN(max)){
			den = max - min;
		}
		
		if(num != 0.0 && den != 0.0){
			index = num/den;
		}
		
		return index;
	}
	
	/**
	 * Return the value of the quality level
	 * @param level
	 * @return
	 */
	public static QualityLevel calculateQualityLevel(double level){
		if(level <= 0.3){
			return QualityLevel.LOW;
		} else if (level > 0.3 && level <= 0.7){
			return QualityLevel.MEDIUM;
		} else if (level > 0.7){
			return QualityLevel.HIGH;
		}
		return null;
	}
	
	/**
	 * Get the Negative Index for the model
	 * @return
	 */
	private static HashSet<String> getNegativeModelIndex(){
		HashSet<String> negativeIndex = new HashSet<String>();
		negativeIndex.add("Node variance");
		negativeIndex.add("Number of connected components");
		negativeIndex.add("Number of cycles");
		return negativeIndex;
	}
	
	/**
	 * Return the hierarchy of the Quality Level (Model - Test Evidence)
	 * @return
	 */
	public static Multimap<QualityLevel, QualityLevel> getQualityLevelIndex(){
		Multimap<QualityLevel, QualityLevel> hierarchy = HashMultimap.create();
		hierarchy.put(QualityLevel.HIGH, null);
		hierarchy.put(QualityLevel.MEDIUM, QualityLevel.HIGH);
		hierarchy.put(QualityLevel.LOW, QualityLevel.MEDIUM);
		return hierarchy;
	}
	
	/**
	 * Return the hierarchy of the Model
	 * @return
	 */
	public static Multimap<ModelType, ModelType> getModelHierarchy(){
		Multimap<ModelType, ModelType> hierarchy = HashMultimap.create();
		hierarchy.put(ModelType.Implementation, null);
		hierarchy.put(ModelType.WSCL, ModelType.Implementation);
		hierarchy.put(ModelType.WSDL, ModelType.WSCL);
		return hierarchy;
	}

	public static ArrayList<ModelType> getModelTypeSubClasses(ModelType model, boolean direct){
		ArrayList<ModelType> results = new ArrayList<ModelType>();
		if(model != null){
			switch (model){
				case Implementation: 
					results.add(ModelType.Implementation);
					return results;
				case WSCL:
					results.add(ModelType.WSCL);
					results.add(ModelType.Implementation);
					return results;
				case WSDL:
					if(direct == true){
						results.add(ModelType.WSDL);
						results.add(ModelType.WSCL);
					}else{
						results.add(ModelType.Implementation);
						results.add(ModelType.WSCL);
						results.add(ModelType.WSDL);
					}
					
					return results;
			default:
				return results;
			}
		}
		return results;
	}
	
	public static ArrayList<QualityLevel> getQualityLevelSubClasses(QualityLevel level, boolean direct){
		ArrayList<QualityLevel> results = new ArrayList<QualityLevel>();
		// TODO: CHECK IF IS CORRECT
		if(level != null){
			switch (level){
				case HIGH: 					
					results.add(null);
					results.add(QualityLevel.HIGH);
					return results;
				case MEDIUM:
					if(direct == true){
						results.add(QualityLevel.HIGH);
					} else {
						results.add(QualityLevel.HIGH);
						results.add(QualityLevel.MEDIUM);
					}
					return results;
				case LOW:
					if(direct == true){
						results.add(QualityLevel.MEDIUM);
					} else {
						results.add(QualityLevel.HIGH);
						results.add(QualityLevel.MEDIUM);
						results.add(QualityLevel.LOW);
					}
					return results;
			default:
				return results;
			}
		}
		return results;
	}
	
//	public static ArrayList<ModelType> getModelTypeSubClasses(ModelType model, boolean direct){
//		ArrayList<ModelType> results = new ArrayList<ModelType>();
//		if (model != null){
//			Multimap<ModelType, ModelType> hierarchy = Retriever.getModelHierarchy();
//			if(direct == true){
//				for(Map.Entry<ModelType, ModelType> cursor : hierarchy.entries()){
//					if(cursor.getValue() == model){
//						results.add(cursor.getKey());
//					}
//				}
//			} else {
//				for(Map.Entry<ModelType, ModelType> cursor : hierarchy.entries()){
//					
//					results = Retriever.recursModelType(model, hierarchy);
//				}
//				
//			}
//		}
//		return results;
//	}
	
//	private static ArrayList<ModelType> recursModelType(ModelType model,
//			Multimap<ModelType, ModelType> hierarchy) {
//		for(Map.Entry<ModelType, ModelType> cursor : hierarchy.entries()){
//			
//		}
//		return null;
//	}

	/**
	 * Return the hierarchy based on the property
	 * @param property
	 * @return
	 */
	public static Multimap<String, String> getHierarchy(String property, Multimap<String, String> hierarchy, IOntologyQuestioner slave){
		//Multimap<String, String> hierarchy = HashMultimap.create();
		if (property != null){
			// TODO: Passare lo slaveMatchMaker come parametro in ingresso
			//BaseXOntologyManager slave = new BaseXOntologyManager("localhost", 1984, "developer", "developer");
			ArrayList<String> properties = slave.getSubClasses(property, true);
			
			if(!properties.isEmpty()){
				if(properties != null){
					hierarchy.putAll(property, properties);
					for(String cursor : properties){
						//hierarchy = Retriever.getHierarchy(cursor, hierarchy);
						hierarchy = Retriever.getHierarchy(cursor, hierarchy, slave);
					}
					
//					for (String cursor : allSecurityProperty) {
//						hierarchy.put(securityProperty, cursor);
//					}
//					ArrayList<String> result = new ArrayList<String>();
//					for (String element : allSecurityProperty) {
//						result = slave.getSubClasses(element, true);
//						if (result != null) {
//							for (String res : result) {
//								hierarchy.put(element, res);
//							}
//						}
//					}
				}	
			} else if(slave.checkExistence(property)){
				hierarchy.put(property, null);
			}
		
		}
		return hierarchy;
	}
	
	/**
	 * Return a Multimap with the assert grouped by the Quality Level of the Test Metrics
	 * @param assertList
	 * @param weight
	 * @return
	 */
	public static Multimap<QualityLevel, ASSERT> groupByTestMetrics(Set<ASSERT> assertList, EvidenceWeight weight){
		if(assertList != null && !assertList.isEmpty()){
			QualityLevel testMetricsIndex;
			HashMap<String, Double> testMetricsProperty = null;
			Multimap<QualityLevel, ASSERT> hierarchy = HashMultimap.create();
			for(ASSERT assertDoc : assertList){
				testMetricsIndex = QualityLevel.LOW;
				testMetricsProperty = Retriever.getTestMetrics(assertDoc);
				if(testMetricsProperty != null && !testMetricsProperty.isEmpty()){
					testMetricsIndex = Retriever.calculateTestMetrixIndex(testMetricsProperty, weight); 
					if(testMetricsIndex != null){
						hierarchy.put(testMetricsIndex, assertDoc);
					}
				} else {
					hierarchy.put(QualityLevel.LOW, assertDoc);
				}
			}
			return hierarchy;	
		}
		return null;
	}

	/**
	 * Calculate the quality Level of the Assert and return is quality level
	 * @param testMetricsProperty
	 * @param weight
	 * @return
	 */
	public static QualityLevel calculateTestMetrixIndex(
			HashMap<String, Double> testMetricsProperty, EvidenceWeight weight) {
		switch(weight){
		case ATTACK:
			if(testMetricsProperty.containsKey(ATTACKCOVERAGE)){
				double value = testMetricsProperty.get(ATTACKCOVERAGE);
				return Retriever.calculateQualityLevel(value);
			} else {
				return QualityLevel.LOW;
			}
		default:
			// TODO: rimuovere la metrica di attack dai valori in ingresso
			Collection<Double> values = testMetricsProperty.values();
			if(!values.isEmpty()){
				double tempValue = ZERO;
				for(Double value : values){
					if(value != ZERO){
						tempValue += value/TESTMETRICSNUMBER;
					} 
				}
				return Retriever.calculateQualityLevel(tempValue);
			}
			return QualityLevel.LOW;
		}
	}

	public static Multimap<Integer, ASSERT> groupByCardinality(Set<ASSERT> assertList) {
		if(assertList != null && !assertList.isEmpty()){
			Multimap<Integer, ASSERT> hierarchy = HashMultimap.create();
			for(ASSERT assertDoc : assertList){
				hierarchy.put(Retriever.getCardinality(assertDoc), assertDoc);
			}
			return hierarchy;
		}
		return null;
	}

	public static Integer getCardinality(ASSERT assertDoc) {
		if (assertDoc != null){
			ArrayList<Node> results = assertDoc.evaluateXpathOnAssertDOM_AsNodeSet("//ASSERTTypeSpecific/ASSERT-E/TestEvidence/TestAttribute");
			if(results != null){
				String name = null;
				double value;
				
				for(Node index : results){
					for(Node elem = index.getFirstChild(); elem != null;){
						Node nextElement = elem.getNextSibling();
						if(elem.getLocalName() != null && elem.getLocalName().equals("Value")){
							try {
								Integer intero = Integer.parseInt(elem.getTextContent());
								return intero;
							} catch (Exception e) { 
								return 0; 
							}
						}
						elem = nextElement;
					}
				}		
				//return indexes;
			}
		}
		return 0;
	}

	public static Multimap<Integer, Integer> getCardHierarchy(
			Multimap<Integer, ASSERT> grouped,
			Multimap<Integer, Integer> hierarchy) {
		
		
		if(grouped != null && !grouped.isEmpty()){
			ArrayList<Integer> orderedKeys = new ArrayList<Integer>(grouped.keySet().size());
			orderedKeys.addAll(grouped.keySet());
			Collections.sort(orderedKeys);
			Collections.reverse(orderedKeys);
			Integer temp = null;
			for(Integer element : orderedKeys){
					hierarchy.put(element, temp);
					temp = element;
			}
		}
		return hierarchy;
	}
	
	//public static HashMap<>

}
