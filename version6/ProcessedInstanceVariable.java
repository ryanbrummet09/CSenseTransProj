package version3;

/**
 * 
 * @author ryanbrummet
 * Creates an object that represents an instance variable but adjusts the name and type for conversion to promela
 */
public class ProcessedInstanceVariable {

	public ProcessedInstanceVariable(String className, String rawName, String rawType){
		this.adjName = Constants.zeroString(rawName + className + "FIELD");
		this.className = className;
		if(rawType.equals("I")){
			this.adjType = "int";
		} else if(rawType.equals("Z")){
			this.adjType = "bool";
		} else if(rawType.equals("S")){
			this.adjType = "short";
		} else if(rawType.equals("B")){
			this.adjType = "byte";
		} else {	
			this.adjType = (Constants.zeroString(rawType)).substring(1);
		}
	}
	
	/**
	 * 
	 * @return returns the adjusted name of this instance variable
	 */
	public String getAdjName(){
		return adjName;
	}
	
	/**
	 * 
	 * @return returns the adjusted type of this instance variable
	 */
	public String getAdjType(){
		return adjType;
	}
	
	/**
	 * 
	 * @return returns the name of the class that this instance variable is associated with
	 */
	public String getClassName(){
		return className;
	}
	
	private String adjName;
	private String adjType;
	private String className;
}
