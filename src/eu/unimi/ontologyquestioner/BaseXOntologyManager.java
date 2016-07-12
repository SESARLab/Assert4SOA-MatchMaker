package eu.unimi.ontologyquestioner;

import java.io.IOException;
import java.util.ArrayList;

import eu.unimi.basexClient.BaseXClient;

/*******************************************************************
 * Copyright (c) - Università degli Studi di Milano (Crema)
 * 
 * @author Jonatan Maggesi <jmaggesi@gmail.com>
 * 
 ******************************************************************/

public class BaseXOntologyManager implements IOntologyQuestioner {

	private final int PORT;
	private final String PASSWORD;
	private final String USERNAME;
	private final String ADDRESS;
	private final String DBNAME;
	
	// private BaseXClient session;

	public BaseXOntologyManager(String address, int port, String username,
			String password, String dbName) {
		this.ADDRESS = address;
		this.PORT = port;
		this.USERNAME = username;
		this.PASSWORD = password;
		this.DBNAME = dbName;
	}

	private BaseXClient getOpenConnection() {
		try {
			BaseXClient session = new BaseXClient(this.ADDRESS, this.PORT,
					this.USERNAME, this.PASSWORD);
			return session;
		} catch (IOException e) {
			return null;
		}
	}

	public ArrayList<String> getAbstract() {
		BaseXClient client = this.getOpenConnection();

		if (client != null) {
			ArrayList<String> result = new ArrayList<String>();
			String input = "for $n in db:list('" + this.DBNAME +"') return $n";

			try {
				final BaseXClient.Query query = client.query(input);
				// query.bind("$element", element);

				while (query.more()) {
					result.add(query.next().replace(".xml", ""));
				}
				query.close();
				// client.close();
				result.remove("TestCategory");
				result.remove("TestType");
				result.remove("TestTypeOntology");
				return result;

			} catch (IOException e) {
				return null;
			} finally {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public ArrayList<String> getSubClasses(String element, boolean direct) {
		BaseXClient client = this.getOpenConnection();

		if (client != null) {
			ArrayList<String> result = new ArrayList<String>();
			String input = null;

			if (direct == true) {
				// input = "declare variable $element external;" +
				// "for $n in db:open('Ontology')//*[parent::$element] return $n/name()";
				input = "for $n in db:open('" + this.DBNAME +"')//*[parent::" + element
						+ "] return $n/name()";
			} else {
				// input = "declare variable $element external;" +
				// "for $n in db:open('Ontology')//*[ancestor::$element] return $n/name()";
				input = "for $n in db:open('" + this.DBNAME +"')//*[ancestor::" + element
						+ "] return $n/name()";
			}

			try {
				final BaseXClient.Query query = client.query(input);
				// query.bind("$element", element);

				while (query.more()) {
					result.add(query.next());
				}
				query.close();
				// client.close();

				return result;

			} catch (IOException e) {
				return null;
			} finally {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Find nearest common ancestor
	 * @param firstEl
	 * @param secondEl
	 * @return
	 */
	public String nearestCommonAncestor(String firstEl, String secondEl) {
		BaseXClient client = this.getOpenConnection();

		if (client != null) {
			ArrayList<String> queryResult = new ArrayList<String>();
			String result = null;
			String input = null;

//			input = "for $q in db:open('Ontology') //" + firstEl + "/ancestor-or-self::* " +
//					"intersect //" + secondEl + "/ancestor-or-self::*[1] return $q/name()";

			input = "for $n in db:open('" + this.DBNAME +"') //"+firstEl +"/ancestor-or-self::*[count(. | //" + secondEl + "/ancestor-or-self::*) = count(//" + secondEl +"/ancestor-or-self::*)][1] return $n/name()"; 
			//input = "for $q in db:open('Ontology') //" + firstEl + "/ancestor-or-self::* intersect //" + secondEl + "/ancestor-or-self::*[1] return $q/name()";
			try {
				final BaseXClient.Query query = client.query(input);
				// query.bind("$element", element);
				//result = query.execute();
				
				while (query.more()) {
					queryResult.add(query.next());
				}
				query.close();
				// client.close();
				
				if(queryResult != null){
					result = queryResult.get(0);
				}
				return result;

			} catch (IOException e) {
				return null;
			} finally {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public ArrayList<String> getSuperClasses(String element, boolean direct) {
		BaseXClient client = this.getOpenConnection();
		if (client != null) {
			ArrayList<String> result = new ArrayList<String>();
			String input = null;

			if (direct == true) {
				// input = "declare variable $element external; " +
				// "for $n in db:open('Ontology')//*[child::$element] return $n/name()";
				input = "for $n in db:open('" + this.DBNAME +"')//*[child::" + element
						+ "] return $n/name()";
			} else {
				// input = "declare variable $element external;" +
				// "for $n in db:open('Ontology')//*[descendant::$element] return $n/name()";
				input = "for $n in db:open('" + this.DBNAME +"')//*[descendant::"
						+ element + "] return $n/name()";
			}

			try {
				final BaseXClient.Query query = client.query(input);
				query.bind("$element", element);
				while (query.more()) {
					result.add(query.next());
				}
				query.close();
				// client.close();

				return result;

			} catch (IOException e) {
				return null;
			} finally {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public boolean checkExistence(String element) {
		BaseXClient client = this.getOpenConnection();
		boolean result = false;
		int cont = 0;
		if (client != null) {
			String input = "for $n in db:open('" + this.DBNAME +"')//" + element
					+ " return $n/name()";

			try {
				final BaseXClient.Query query = client.query(input);
				query.bind("$element", element);
				while (query.more()) {
					query.next();
					cont++;
				}
				query.close();
				// client.close();
				if (cont > 0) {
					result = true;
				}
				return result;

			} catch (IOException e) {
				return false;
			} finally {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public ArrayList<String> getAllAssert() {
		BaseXClient client = this.getOpenConnection();
		ArrayList<String> results = new ArrayList<String>();

		if (client != null) {
			// String input =
			// "declare namespace ns5='urn:assert4soa:assert:2.0'; for $n in db:open('Certificates')/ns5:ASSERT return $n";
			String input = "declare namespace ns2='urn:oasis:names:tc:SAML:2.0:assertion'; for $n in db:open('Certificates')/ns2:Assertion return $n";

			try {
				final BaseXClient.Query query = client.query(input);
				// query.bind("$element", element);

				while (query.more()) {
					results.add(query.next());
				}
				query.close();
				// client.close();

				return results;

			} catch (IOException e) {
				return null;
			} finally {
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return results;
	}
}
