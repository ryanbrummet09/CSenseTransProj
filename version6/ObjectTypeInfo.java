package version3;

public class ObjectTypeInfo {

	public ObjectTypeInfo(int numFields, String assignMethodName, String equalityMethodName){
		this.numParams = numParams;
		this.assignMethodName = assignMethodName;
		this.equalityMethodName = equalityMethodName;
	}
	
	/**
	 * 
	 * @return returns the number of fields of the object associated with this ObjectTypeInfo
	 */
	public int getNumFields(){
		return numParams;
	}
	
	/**
	 * 
	 * @return returns the name of the assignment inline of the object associated with this ObjectTypeInfo
	 */
	public String getAssignMethodName(){
		return assignMethodName;
	}
	
	/**
	 * 
	 * @return returns the name of the equality inline of the object associated with this ObjectTypeInfo
	 */
	public String getEqualityMethodName(){
		return equalityMethodName;
	}
	
	private int numParams;
	private String assignMethodName;
	private String equalityMethodName;
}
