package version3;

/**
 * 
 * @author ryanbrummet
 *
 */
public class VariableKey {

	public VariableKey(String className, String rawName){
		this.className = className;
		this.rawName = rawName;
	}
	
	/**
	 * 
	 * @param obj
	 * @return returns whether or not two hashmap keys for a variable in the translator (two VariableKeys) are in fact different since two variables
	 * may have the same name but be in different classes.
	 */
	public boolean equals(Object obj){
		if(obj instanceof VariableKey){
			VariableKey variableKey = (VariableKey) obj;
			if(this.getClassName().equals(variableKey.getClassName()) && this.getRawName().equals(variableKey.getRawName())){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * @return returns a hashcode number for this object
	 */
	public int hashCode(){
		int i = className.hashCode() * 7;
		i += rawName.hashCode() * 11;
		return i;
	}
	
	/**
	 * 
	 * @return returns the class that this variable is associated with
	 */
	public String getClassName(){
		return className;
	}
	
	/**
	 * 
	 * @return returns the raw name of this variable before modificaiton
	 */
	public String getRawName(){
		return rawName;
	}
	
	private String className;
	private String rawName;
}
