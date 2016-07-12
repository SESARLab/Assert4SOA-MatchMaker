package eu.unimi.ontologyquestioner;
import java.util.ArrayList;


public interface IOntologyQuestioner {
	public ArrayList<String> getSubClasses(String element, boolean direct);
	
	public ArrayList<String> getSuperClasses(String element, boolean direct);

	public boolean checkExistence(String element);
	
	public String nearestCommonAncestor(String firstEl, String secondEl);
}
