package eu.unimi.ematchmakerconfiguration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;



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
@JsonAutoDetect
public class EMatchMakerConfiguration {
	private EMatchMakerSecurityProperty securityProperty;
	private EMatchMakerModel model;
	private EMatchMakerTestEvidence testEvidence;
	private OrderComparison orderComparison;
	private ComparisonType comparisonType;
	private String securityPropertyComplete;
	
	/**
	 * @return the securityProperty
	 */
	public EMatchMakerSecurityProperty getSecurityProperty() {
		return securityProperty;
	}
	
	/**
	 * @param securityProperty the securityProperty to set
	 */
	public void setSecurityProperty(EMatchMakerSecurityProperty securityProperty) {
		this.securityProperty = securityProperty;
	}
	
	/**
	 * @return the model
	 */
	public EMatchMakerModel getModel() {
		return model;
	}
	
	/**
	 * @param model the model to set
	 */
	public void setModel(EMatchMakerModel model) {
		this.model = model;
	}
	
	/**
	 * @return the testEvidence
	 */
	public EMatchMakerTestEvidence getTestEvidence() {
		return testEvidence;
	}
	/**
	 * @param testEvidence the testEvidence to set
	 */
	public void setTestEvidence(EMatchMakerTestEvidence testEvidence) {
		this.testEvidence = testEvidence;
	}
	
	/**
	 * @return the orderComparison
	 */
	public OrderComparison getOrderComparison() {
		return orderComparison;
	}
	
	/**
	 * @param orderComparison the orderComparison to set
	 */
	public void setOrderComparison(OrderComparison orderComparison) {
		this.orderComparison = orderComparison;
	}
	
	/**
	 * @return the comparisonType
	 */
	public ComparisonType getComparisonType() {
		return comparisonType;
	}
	
	/**
	 * @param comparisonType the comparisonType to set
	 */
	public void setComparisonType(ComparisonType comparisonType) {
		this.comparisonType = comparisonType;
	}
	
	/**
	 * @param securityProperty
	 * @param model
	 * @param testEvidence
	 * @param orderComparison
	 * @param comparisonType
	 */
	
	public EMatchMakerConfiguration(
			EMatchMakerSecurityProperty securityProperty, String securityPropertyComplete,
			EMatchMakerModel model, EMatchMakerTestEvidence testEvidence,
			OrderComparison orderComparison, ComparisonType comparisonType) {
		super();
		this.securityProperty = securityProperty;
		this.model = model;
		this.testEvidence = testEvidence;
		this.orderComparison = orderComparison;
		this.comparisonType = comparisonType;
		this.securityPropertyComplete = securityPropertyComplete;
	}

	/**
	 * 
	 * @return
	 */
	public String getSecurityPropertyComplete() {
		return securityPropertyComplete;
	}

	/**
	 * 
	 * @param securityPropertyComplete
	 */
	public void setSecurityPropertyComplete(String securityPropertyComplete) {
		this.securityPropertyComplete = securityPropertyComplete;
	}

	public EMatchMakerConfiguration() {
		super();
		// TODO Auto-generated constructor stub
	}
}
