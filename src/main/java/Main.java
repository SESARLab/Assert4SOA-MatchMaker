import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.Queue;
import java.util.Set;

import com.thoughtworks.xstream.XStream;

import eu.assert4soa.datamodel.PartiallyOrderedSet;
import eu.unimi.it.data.Certificate;
import eu.unimi.it.data.Configuration;
import eu.unimi.it.data.Match;
import eu.unimi.it.matchmaker.SlaveMatchMaker;
import eu.unimi.it.ontologyquestioner.BaseXOntologyManager;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		BaseXOntologyManager ontology = new BaseXOntologyManager("localhost", 1984, "developer", "developer", "Ontology");
	
		
		//TODO: args[0] = SET_CERTIFICATE
		//TODO: args[1] = CONFIG_FOLDER
		//TODO: args[2] = FILE_TO_SAVE
		
		//String setCertificates = args[0];
		//String configFile = args[1];
		//String path = args[2];
		
		String path = "/Users/jonny/Desktop/NEWTEST/Random/TempResult3/";
		String configFile = "/Users/jonny/Desktop/CONF/coda5.xml";
		String setCertificates = "/Users/jonny/Desktop/NEWTEST/Random/BATCH10/SET2";
		
		
		//Set<Certificate> certs = loadFromDirectory("/Users/jonny/Desktop/RESULT/Random-Shadow/SET9");
		
		Set<Certificate> certs = loadFromDirectory(setCertificates);
		
		//Configuration conf = loadConfig("/Users/jonny/Desktop/CONF/");
		
		Configuration conf = loadConfig(configFile);
		
		SlaveMatchMaker matchmaker = new SlaveMatchMaker(ontology, conf, path);
		PartiallyOrderedSet<Set<Certificate>> results = matchmaker.getResults(certs);
		
		long startTimeAuction = System.nanoTime();
		PartialAuction pa = new PartialAuction(results);
		int winner = pa.getWinner(); // executes the auction 
		long endTimeAuction = System.nanoTime();
		long timeAuction= TimeUnit.MILLISECONDS.convert((endTimeAuction - startTimeAuction), TimeUnit.NANOSECONDS);
		System.out.println("Auction time (millisec): " + timeAuction);
		System.out.println("Auction winner: " + winner);
	}
	
	private static Configuration loadConfig(String configFile) {
		File confFile = new File(configFile);
		XStream xstream = new XStream();
		xstream.alias("Configuration", Configuration.class);
		xstream.alias("Match", Match.class);
		xstream.alias("properties", Queue.class, LinkedList.class);
		return (Configuration) xstream.fromXML(confFile);
	}

	/**
	 * Metodo per caricare i certificati dalla cartella
	 * @param folder Cartella da caricare
	 * @return
	 */
	private static Set<Certificate> loadFromDirectory(String folder) {
		if (folder != null && !folder.isEmpty()) {
			Set<Certificate> candidates = new HashSet<Certificate>();
			File directory = new File(folder);
			if (directory.canRead() && directory.isDirectory()) {
				File[] files = directory.listFiles(new FilenameFilter() {
				    public boolean accept(File dir, String name) {
				        return name.toLowerCase().endsWith(".xml");
				    }
				});
				if (files != null) {
					XStream xstream = new XStream();
					xstream.alias("Certificate", Certificate.class);
					int length = files.length;
					for (int i = 0; i < length; i++) {
						Certificate cert = (Certificate) xstream.fromXML(files[i]);
						candidates.add(cert);
					}
					return candidates;
				}
			}
		}
		return null;
	}

}
