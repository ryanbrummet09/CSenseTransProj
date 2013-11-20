package version3;

import java.util.*;
import org.objectweb.asm.Type;

/**
 * 
 * @author ryanbrummet
 *
 */
public class MethodObject {

	public MethodObject(MethodStructure3 mn, int uniqueNumber){
		this.mn = mn;
		String raw = Arrays.toString(Type.getArgumentTypes(mn.getMethodNode().desc));
		paramTypes = new ArrayList<String>(Arrays.asList(raw.split(",[ ]*")));
		for(int i = 0; i < paramTypes.size(); i++){
			paramTypes.set(i, Constants.zeroString(paramTypes.get(i)));
		}
		if(mn.getMethodName().equals("<init>")){
			adjName = Constants.zeroString(mn.getClassName() + "INIT" + mn.getMethodDesc());
		} else {
			adjName = Constants.zeroString(mn.getMethodName() + mn.getClassName() + "USER" + uniqueNumber);
		}
	}
	
	/**
	 * 
	 * @return returns the adjusted name of the method associated with the MethodObject
	 */
	public String getAdjName(){
		return adjName;
	}
	
	/**
	 * 
	 * @return returns the paramTypes of the method associated with this MethodObject
	 */
	public ArrayList<String> getParamTypes(){
		return paramTypes;
	}
	
	private String adjName;
	private MethodStructure3 mn;
	private ArrayList<String> paramTypes;
}
