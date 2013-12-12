package version3;

import java.util.ArrayList;
/**
 * 
 * @author ryanbrummet
 * Constructs an object that finds all the possible scoping intervals of a loop, switch, if, or try catch stmnt
 */
public class ScopeObject {

	public ScopeObject(ArrayList<Integer> data, int type){
		this.data = data;
		this.type = type;
		scopeIntervals = new ArrayList<ScopeInterval>();
		if(type == Constants.SCOPE_DO_WHILE_LOOP || type == Constants.SCOPE_FIRST_RUN_CONDITIONAL_LOOP || type == Constants.SCOPE_FOR_EACH_LOOP){
			globalStart = data.get(0);
			globalEnd = data.get(1) + 1;
			scopeIntervals.add(new ScopeInterval(globalStart, globalEnd));
		} else if(type == Constants.SCOPE_IF_CLASS){
			globalStart = data.get(1);
			globalEnd = data.get(data.size() - 1);
			for(int i = 1; i < data.get(0); i++){
				scopeIntervals.add(new ScopeInterval(data.get(i),data.get(i + 1)));
			}
		} else if(type == Constants.SCOPE_SWITCH){
			globalStart = data.get(2);
			globalEnd = data.get(data.size() - 1);
			for(int i = 2; i < data.get(0); i++){
				scopeIntervals.add(new ScopeInterval(data.get(i),data.get(i + 1)));
			}
		}else if(type == Constants.SCOPE_TRY_CATCH){
			globalStart = data.get(1);
			globalEnd = data.get(data.size() - 1);
			scopeIntervals.add(new ScopeInterval(data.get(1), data.get(2)));
			for(int i = 3; i < data.get(0); i++){
				scopeIntervals.add(new ScopeInterval(data.get(i),data.get(i + 1)));
			}
		}
	}
	
	public ArrayList<ScopeInterval> getScopeIntervals(){
		return scopeIntervals;
	}
	
	private ArrayList<Integer> data;
	private int type;
	private ArrayList<ScopeInterval> scopeIntervals;
	private int globalStart;  //inclusive
	private int globalEnd;  //exclusive
	
}
