package eu.unimi.it.data;

import java.util.Queue;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Class used to represents the configuration that the slaveMatchmaker needs to use to perform the ranking
 * @author Jonatan Maggesi
 *
 */
@XStreamAlias("Configuration")
public class Configuration {
	 
	private int value;
	
	@XStreamImplicit
	private Queue<Match> properties;
	
	private Order order;
	
	public Configuration(){}

	/**
	 * Constructor of the Configuration class
	 * @param value The integer value tha we use to filter the certificates
	 * @param properties The properties that we need to use to perform the rank
	 * @param order The order that we need to follow to perform the rank
	 */
	public Configuration(int value, Queue<Match> properties, Order order) {
		super();
		this.value = value;
		this.properties = properties;
		this.order = order;
	}

	/**
	 * Return the value
	 * @return the integer value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Set the value
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Get the properties
	 * @return the properties The properties to perform the rank
	 */
	public Queue<Match> getProperties() {
		return properties;
	}

	/**
	 * Set the properties
	 * @param properties the properties to follow to perform the rank
	 */
	public void setProperties(Queue<Match> properties) {
		this.properties = properties;
	}
	
	/**
	 * Get the order in the Configuration
	 * @return the configuration
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Set the order
	 * @param order
	 */
	public void setOrder(Order order) {
		this.order = order;
	}
	
}
