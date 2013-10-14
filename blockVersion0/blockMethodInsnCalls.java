/**
 * @author ryanbrummet
 */

import ASMModifiedSourceCode.*;

public class blockMethodInsnCalls {
	
	/**
	 * Constructs a blockMethodInsnCalls.  This class is defined so that we can get info
	 * and prevent changes from being made to any MethodInsnNode
	 * @param node
	 */
	public blockMethodInsnCalls(MethodInsnNode node){
		className = node.getMethodOwner();
		methodName = node.getMethodName();
		methodDesc = node.getMethodDesc();
	}
	
	public String getClassName(){
		return className;
	}
	
	public String getMethodName(){
		return methodName;
	}
	
	public String getMethodDesc(){
		return methodDesc;
	}
	private String className;
	private String methodName;
	private String methodDesc;
}
