package eu.unimi.it.data;

public class Result<T> {

	private T partialOrder;
	private int propertyCompleted;
	
	public Result(T partialOrder, int propertyCompleted) {
		this.partialOrder = partialOrder;
		this.propertyCompleted = propertyCompleted;
	}

	public Result() {
		this.propertyCompleted = 0;
	}

	public T getPartialOrder() {
		return partialOrder;
	}
	
	public void setPartialOrder(T partialOrder) {
		this.partialOrder = partialOrder;
	}
	
	public int getPropertyCompleted() {
		return propertyCompleted;
	}
	
	public void setPropertyCompleted(int propertyCompleted) {
		this.propertyCompleted = propertyCompleted;
	}
	
	public boolean isSetted(){
		if(this.propertyCompleted != 0)
			return true;
		else
			return false;
	}
}
