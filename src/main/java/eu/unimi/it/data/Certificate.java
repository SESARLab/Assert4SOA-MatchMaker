package eu.unimi.it.data;
import java.util.HashMap;

/**
 * Class that represents the fake certificate
 * @author Jonatan Maggesi
 *
 */
public class Certificate {
	private String id;
	private int code;
	private HashMap<String, String> properties;
	
	public Certificate() {}

	/**
	 * Constructor of the class
	 * @param id The id of the certificate
	 * @param code The value of the certificate(used to filter)
	 * @param properties The set of properties associated to the Certificate
	 */
	public Certificate(String id, int code, HashMap<String, String> properties) {
		super();
		this.id = id;
		this.code = code;
		this.properties = properties;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}
	/**
	 * Get a single property value from the set of properties of the certificate
	 * @param propertyName The propertyName
	 * @return the property value
	 */
	public String getProperty(String propertyName){
		if(propertyName != null && this.properties != null){
			if(properties.containsKey(propertyName))
				return properties.get(propertyName);
			else 
				return null;
		} else {
			return null;
		}
	}
	
	/**
	 * Set a single property value in the set of properties of the certificates
	 * @param propertyName the property name
	 * @param propertyValue the property value
	 */
	public void setProperty(String propertyName, String propertyValue){
		if(propertyName != null && propertyValue != null){
			if(properties == null)
				this.properties = new HashMap<String,String>();
			properties.put(propertyName, propertyValue);
		}
	}
	
}
