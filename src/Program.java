import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.FakeTimeLimiter;

import org.w3c.dom.Document;
import eu.assert4soa.datamodel.ASSERT;
import eu.assert4soa.datamodel.CandidateService;
import eu.assert4soa.datamodel.PartiallyOrderedSet;
import eu.assert4soa.datamodel.datamodelFactory;
import eu.unimi.ematchmakerconfiguration.ComparisonType;
import eu.unimi.ematchmakerconfiguration.EMatchMakerConfiguration;
import eu.unimi.ematchmakerconfiguration.EMatchMakerModel;
import eu.unimi.ematchmakerconfiguration.EMatchMakerTestEvidence;
import eu.unimi.ematchmakerconfiguration.EvidenceWeight;
import eu.unimi.ematchmakerconfiguration.ModelType;
import eu.unimi.ematchmakerconfiguration.ModelWeight;
import eu.unimi.ematchmakerconfiguration.OrderComparison;
import eu.unimi.ematchmakerconfiguration.QualityLevel;
import eu.unimi.matchmaker.SlaveMatchMakerE;
import eu.unimi.ontologyquestioner.BaseXOntologyManager;
import eu.unimi.retriever.Retriever;

/*******************************************************************
 * Copyright (c) - Università degli Studi di Milano (Crema)
 *
 * @author Jonatan Maggesi <jmaggesi@gmail.com>
 *
 ******************************************************************/

/**
 * @author Jonatan Maggesi
 *
 */
public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BaseXOntologyManager match = new BaseXOntologyManager("localhost", 1984, "developer", "developer", "Ontology");

		ArrayList<String> results = new ArrayList<String>();
		datamodelFactory dmf = datamodelFactory.eINSTANCE;
		//ASSERT test = dmf.createASSERT("Test");
		
		Set<ASSERT> asserts = new HashSet<ASSERT>();
		
		System.out.println("");
		System.out.println("ASSERT Presenti nel DB");
		System.out.println("");
		
		results = match.getAllAssert();
		if (results != null){
			System.out.println("Numero di assert presenti nel db: " + results.size());
			
			for(String result : results) {
				asserts.add(dmf.createASSERT(result));
			}
		}
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_algo_DES_key_112_intransit_instorage");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_algo_DES_key_112_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_algo_DES_key_168_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
		
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_algo_DES_key_168_intransit_instorage");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSCL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.PF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");

//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.Implementation, ModelWeight.CONFIDENT, QualityLevel.LOW);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.PF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.equivalence_partitioning", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.equivalence_partitioning", QualityLevel.HIGH, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 600);
//		config.setOrderComparison(OrderComparison.PF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
		config.setComparisonType(ComparisonType.ADVANCED);
		config.setSecurityPropertyComplete("confidentiality_intransit");
		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
		config.setOrderComparison(OrderComparison.PF);
		config.setModel(model);
		config.setTestEvidence(evidence);
		
		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
		//System.out.println("Finished");

		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_algo_DES_key_168_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.Implementation, ModelWeight.CONFIDENT, QualityLevel.LOW);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_algo_DES_key_112_intransit_instorage");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSCL, ModelWeight.CONFIDENT, QualityLevel.LOW);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.PF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_algo_DES_key_112_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.HIGH, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSCL, ModelWeight.CONFIDENT, QualityLevel.LOW);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_algo_DES_key_112_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.MEDIUM);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.HIGH, EvidenceWeight.ATTACK, 600);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
//		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
//		config.setComparisonType(ComparisonType.ADVANCED);
//		config.setSecurityPropertyComplete("confidentiality_intransit");
//		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSCL, ModelWeight.CONFIDENT, QualityLevel.MEDIUM);
//		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.MEDIUM, EvidenceWeight.ATTACK, 200);
//		config.setOrderComparison(OrderComparison.MF);
//		config.setModel(model);
//		config.setTestEvidence(evidence);
//		
//		SlaveMatchMakerE slave = new SlaveMatchMakerE(config, match);
//		PartiallyOrderedSet<Set<ASSERT>> resultPart = slave.getResults(config, match, asserts);
//		System.out.println("Finished");
		
		
		
	}
	
	public static PartiallyOrderedSet<Set<String>> fakeCompare(PartiallyOrderedSet<Set<String>> p1){
		if(!p1.isEmpty() && p1 != null){
			Set<Set<String>> maxs = p1.getMaximelElements();
			for(Set<String> max : maxs){
				p1 = fakeCompareSpecific(max, p1);
			}
		}
		return p1;
	}

	private static PartiallyOrderedSet<Set<String>> fakeCompareSpecific(Set<String> max,
			PartiallyOrderedSet<Set<String>> p1) {
		if(max != null && max.size() > 1){
			if(max.contains("Prova")){
				// Creo il fake partial Order da mergiare
				datamodelFactory dmf = datamodelFactory.eINSTANCE;
				PartiallyOrderedSet<Set<String>> testMerge = dmf.createPartialOrder();
				HashSet<String> primo = new HashSet<String>();
				primo.add("Test");
				HashSet<String> secondo = new HashSet<String>();
				secondo.add("Prova");
		
				testMerge.add(primo);
				testMerge.add(secondo);
				testMerge.addRelation(primo, secondo);
				// QUI VA FATTO IL GROUPED E POI CERCATA LA GERARCHIA
				// QUINDI CI SI FA RESTITUIRE UN PARTIAL ORDER DA GROUPED E HIERARCHY
				// E POI SI FA IL MERGE
				
				p1 = Retriever.mergePartialOrder2(max, p1, testMerge);
				
				
				
				
//				HashSet<Set<String>> childs = Retriever.getChildrenElement2(max, p1);
//				if(childs != null){
//					for(Set<String> child : childs){
//						fakeCompareSpecific(child, p1);
//					}
//				}
			}
		}
		
		HashSet<Set<String>> childs = Retriever.getChildrenElement2(max, p1);
		if(childs != null){
			for(Set<String> child : childs){
				p1 = fakeCompareSpecific(child, p1);
			}
		}
		
		return p1;
		
	}
	

}
