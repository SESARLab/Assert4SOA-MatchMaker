package eu.unimi.ematchmakerconfiguration;

import java.util.List;

/*******************************************************************
 * Copyright (c) - Universit� degli Studi di Milano (Crema)
 *
 * @author Jonatan Maggesi <jmaggesi@gmail.com>
 *
 ******************************************************************/

public class EMatchMakerSecurityProperty {
	private String generalSecurityProperty;
	private String SecurityPropertyName;
	private List<PropertyAttribute> SecurityPropertyAttribute;
	private String securityContext;
	
	
	public String getSecurityContext() {
		return securityContext;
	}

	public void setSecurityContext(String securityContext) {
		this.securityContext = securityContext;
	}

	public String getGeneralSecurityProperty() {
		return generalSecurityProperty;
	}

	public void setGeneralSecurityProperty(String generalSecurityProperty) {
		this.generalSecurityProperty = generalSecurityProperty;
	}

	/**
	 * @return the securityPropertyName
	 */
	public String getSecurityPropertyName() {
		return SecurityPropertyName;
	}
	
	/**
	 * @param securityPropertyName the securityPropertyName to set
	 */
	public void setSecurityPropertyName(String securityPropertyName) {
		SecurityPropertyName = securityPropertyName;
	}
	
	/**
	 * @return the securityPropertyAttribute
	 */
	public List<PropertyAttribute> getSecurityPropertyAttribute() {
		return SecurityPropertyAttribute;
	}
	
	/**
	 * @param securityPropertyAttribute the securityPropertyAttribute to set
	 */
	public void setSecurityPropertyAttribute(
			List<PropertyAttribute> securityPropertyAttribute) {
		SecurityPropertyAttribute = securityPropertyAttribute;
	}
	
	/**
	 * @param securityPropertyName
	 * @param securityPropertyAttribute
	 */
	public EMatchMakerSecurityProperty(String securityPropertyName,
			List<PropertyAttribute> securityPropertyAttribute) {
		super();
		SecurityPropertyName = securityPropertyName;
		SecurityPropertyAttribute = securityPropertyAttribute;
	}

}
