package version3;
/**
 * 
 * @author ryanbrummet
 * creates an object that defines an interval of the form [,)
 */
public class ScopeInterval {

	public ScopeInterval(int start, int end){
		this.startInterval = start;
		this.endInterval = end;
	}
	
	public int getIntervalStart(){
		return startInterval;
	}
	
	public int getIntervalEnd(){
		return endInterval;
	}
	
	private int startInterval; //inclusive
	private int endInterval; //inclusive
}
