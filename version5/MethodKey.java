package version3;

/**
 * 
 * @author ryanbrummet
 *
 */
public class MethodKey {

	public MethodKey(String className, String methodName, String methodDescription){
		this.className = className;
		this.methodName = methodName;
		this.methodDescription = methodDescription;
	}
	
	/**
	 * 
	 * @param obj
	 * @return returns whether or not two hashmap keys for a method in the translator (two MethodKeys) are in fact different since two methods
	 * may have the same name but be in different classes.
	 */
	public boolean equals(Object obj){
		if(obj instanceof MethodKey){
			MethodKey methodKey = (MethodKey) obj;
			if(this.getClassName().equals(methodKey.getClassName()) && this.getMethodName().equals(methodKey.getMethodName()) && this.getMethodDescription().equals(methodKey.getMethodDescription())){
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
		int i = className.hashCode() * 5;
		i += methodName.hashCode() * 29;
		i += methodDescription.hashCode() * 11;
		return i;
	}
	
	/**
	 * 
	 * @return returns the name of the class that the method in question is a part of
	 */
	public String getClassName(){
		return className;
	}
	
	/**
	 * 
	 * @return returns the name of the method that this MethodKey is associated with
	 */
	public String getMethodName(){
		return methodName;
	}
	
	/**
	 * 
	 * @return returns the method description of the method that this MethodKey is associated with
	 */
	public String getMethodDescription(){
		return methodDescription;
	}
	
	private String className;
	private String methodName;
	private String methodDescription;
}
