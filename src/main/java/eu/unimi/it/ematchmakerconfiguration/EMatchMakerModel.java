package eu.unimi.it.ematchmakerconfiguration;



/*******************************************************************
 * Copyright (c) - Università degli Studi di Milano (Crema)
 *
 * @author Jonatan Maggesi <jmaggesi@gmail.com>
 *
 ******************************************************************/

/**
 * @author Jonatan
 *
 */
public class EMatchMakerModel {
	
	private ModelType modelName;
	private ModelWeight modelWeight;
	private QualityLevel modelLevel;
	
	/**
	 * @return the modelName
	 */
	public ModelType getModelName() {
		return modelName;
	}
	
	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(ModelType modelName) {
		this.modelName = modelName;
	}
	
	/**
	 * @return the modelWeight
	 */
	public ModelWeight getModelWeight() {
		return modelWeight;
	}
	
	/**
	 * @param modelWeight the modelWeight to set
	 */
	public void setModelWeight(ModelWeight modelWeight) {
		this.modelWeight = modelWeight;
	}

	/**
	 * @return the modelLevel
	 */
	public QualityLevel getModelLevel() {
		return modelLevel;
	}

	/**
	 * @param modelLevel the modelLevel to set
	 */
	public void setModelLevel(QualityLevel modelLevel) {
		this.modelLevel = modelLevel;
	}

	/**
	 * @param modelName
	 * @param modelWeight
	 * @param modelLevel
	 */
	public EMatchMakerModel(ModelType modelName, ModelWeight modelWeight,
			QualityLevel modelLevel) {
		super();
		this.modelName = modelName;
		this.modelWeight = modelWeight;
		this.modelLevel = modelLevel;
	}
	
	public EMatchMakerModel(){}
}
