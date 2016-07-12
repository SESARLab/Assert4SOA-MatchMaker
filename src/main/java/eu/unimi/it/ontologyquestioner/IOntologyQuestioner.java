package eu.unimi.it.ontologyquestioner;
import java.util.ArrayList;

/**
 * Interface to implements the ontologyQuestioner
 * @author Jonatan Maggesi
 *
 */
public interface IOntologyQuestioner {
	public ArrayList<String> getSubClasses(String keyName, String element, boolean direct);
	
	public ArrayList<String> getSuperClasses(String element, boolean direct);

	public boolean checkExistence(String element);
	
	public String nearestCommonAncestor(String firstEl, String secondEl);
}
