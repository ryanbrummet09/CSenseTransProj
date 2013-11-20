package version3;

import java.util.ArrayList;

/**
 * 
 * @author ryanbrummet
 * Builds an object that will be used as a value in a hashmamp.  This object will contain the desired name and desired type of a variable.  The key of the
 * hashamp will be the raw name of a variable.  This object will contain information allowing differentiation between variables of the same type and name
 * based upon variable scope.
 */
public class ProcessedVariable {

	/**
	 * creates a ProcessedVariable for local Variables
	 * @param rawName
	 * @param rawType
	 * @param existNumber
	 * @param mn
	 * @param startOfBlockScope
	 * @param needsAdjustment
	 */
	public ProcessedVariable(String rawName, String rawType, int existNumber, MethodStructure3 mn, int startOfBlockScope, boolean needsAdjustment){
		this.rawName = rawName;
		this.rawType = rawType;
		this.existNumber = existNumber;
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
		this.mn = mn;
		this.needsAdjustment = needsAdjustment;
		this.startScope = startOfBlockScope;
		this.adjName = Constants.zeroString(rawName + mn.getMethodName() + "LOCAL" + existNumber);
		ArrayList<ScopeInterval> scopeIntervals = mn.getSortedScopeIntervals();
		ArrayList<ScopeInterval> tempScopeIntervals = new ArrayList<ScopeInterval>();
		for(int i = 0; i < scopeIntervals.size(); i++){
			if(scopeIntervals.get(i).getIntervalStart() <= startScope && scopeIntervals.get(i).getIntervalEnd() >= startScope){
				tempScopeIntervals.add(scopeIntervals.get(i));
			}
		}
		if(tempScopeIntervals.size() > 0){
			ScopeInterval temp = tempScopeIntervals.get(0);
			for(int i = 1; i < tempScopeIntervals.size(); i++){
				if(temp.getIntervalEnd() > tempScopeIntervals.get(i).getIntervalEnd()){
					temp = tempScopeIntervals.get(i);
				}
			}
			endScope = temp.getIntervalEnd();
		} else {
			endScope = mn.getBlockStructures().size() - 1;
		}

	}
	
	/**
	 * creates a ProcessedVariable for static variables
	 * @param rawName
	 * @param rawType
	 * @param existNumber
	 * @param cs
	 * @param needsAdjustment
	 */
	public ProcessedVariable(String rawName, String rawType, int existNumber, String className){
		this.rawName = rawName;
		this.existNumber = existNumber;
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
		this.adjName = Constants.zeroString(rawName + className + "STATIC" + existNumber);
		startScope = -1;
		endScope = -1;
	}
	
	/**
	 * 
	 * @return returns adjusted raw name
	 */
	public String getAdjName(){
		return adjName;
	}
	
	/**
	 * 
	 * @return returns adjusted raw type
	 */
	public String getAdjType(){
		return adjType;
	}
	
	/**
	 * 
	 * @return returns the start of this variable's scope
	 */
	public int getStartScope(){
		return startScope;
	}
	
	/**
	 * 
	 * @return returns the end of this variable's scope
	 */
	public int getEndScope(){
		return endScope;
	}
	
	/**
	 * 
	 * @return returns the raw type of the variable associated with this ProcessedVariable
	 */
	public String getRawType(){
		return rawType;
	}
	
	/**
	 * 
	 * @return returns whether or not this variable's scope has ended and needs to be readjusted
	 */
	public boolean adjustmentNeeded(){
		return needsAdjustment;
	}
	
	/**
	 * marks this processed variable for adjustment (scope has ended)
	 */
	public void markForAdjustment(){
		needsAdjustment = true;
	}
	
	/**
	 * 
	 * @param startScope
	 * Re-adjusts info to reflect the fact that this processed variable now points to a different variable of the same name as the last variable (Local)
	 */
	public void reAdjust(String rawType, MethodStructure3 mn, int startScope){
		this.startScope = startScope;
		this.rawType = rawType;
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
		this.mn = mn;
		existNumber++;
		needsAdjustment = false;
		adjName = Constants.zeroString(rawName + mn.getMethodName() + "LOCAL" + existNumber);
		ArrayList<ScopeInterval> scopeIntervals = mn.getSortedScopeIntervals();
		ArrayList<ScopeInterval> tempScopeIntervals = new ArrayList<ScopeInterval>();
		for(int i = 0; i < scopeIntervals.size(); i++){
			if(scopeIntervals.get(i).getIntervalStart() <= startScope && scopeIntervals.get(i).getIntervalEnd() >= startScope){
				tempScopeIntervals.add(scopeIntervals.get(i));
			}
		}
		if(tempScopeIntervals.size() > 0){
			ScopeInterval temp = tempScopeIntervals.get(0);
			for(int i = 1; i < tempScopeIntervals.size(); i++){
				if(temp.getIntervalEnd() > tempScopeIntervals.get(i).getIntervalEnd()){
					temp = tempScopeIntervals.get(i);
				}
			}
			endScope = temp.getIntervalEnd();
		} else {
			endScope = mn.getBlockStructures().size() - 1;
		}
	}

	
	
	private String rawName;
	private String rawType;
	private String adjName;
	private String adjType;
	private int existNumber;
	private MethodStructure3 mn;
	private int startScope;
	private int endScope;
	private boolean needsAdjustment;
}
