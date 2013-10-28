package version3;

import java.util.ArrayList;

public class CurrentStackAndLocalVars {

	public CurrentStackAndLocalVars(String stackOwner){
		this.stackOwner = stackOwner;
		localVariables = new ArrayList<Object>();
		currentStack = new ArrayList<Object>();
	}
	
	public void popStack(){
		currentStack.remove(currentStack.size() - 1);
	}
	
	public void pushStack(Object newObj){
		currentStack.add(newObj);
	}
	
	public ArrayList<Object> getCurrentStack(){
		return currentStack;
	}
	
	public void newLocalVariable(Object variable){
		localVariables.add(variable);
	}
	
	public ArrayList<Object> getPresentLocalVariables(){
		return localVariables;
	}
	
	public String getStackOwner(){
		return stackOwner;
	}
	
	private String stackOwner;
	private ArrayList<Object> localVariables;
	private ArrayList<Object> currentStack;
}
