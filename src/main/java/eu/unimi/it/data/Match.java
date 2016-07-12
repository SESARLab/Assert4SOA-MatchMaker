package eu.unimi.it.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Match")
public class Match {
	private String propertyName;
	private String propertyValue;
	
	public Match() {}
	
	public Match(String propertyName, String propertyValue) {
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getPropertyValue() {
		return propertyValue;
	}
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
}
