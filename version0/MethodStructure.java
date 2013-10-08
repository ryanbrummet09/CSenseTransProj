/**
 * @author ryanbrummet
 */

/*
 * Takes a MethodNode and converts it into a usable data structure.  We 
 * are assuming that passed methods will not have attributes
 */

//DOES NOT CURRENTLY SUPPORT ANNOTATIONS, EXCEPTIONS, TRY/CATCH BLOCKS
//		OR LOCAL VARIABLES

//SUPPORTS NAME, DESC (DESCRIPTION), AND INSTRUCTIONS

import java.io.*;
import java.util.ArrayList;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

public class MethodStructure {

	/**
	 * Constructs a MethodStructure object
	 * @param mn
	 */
	public MethodStructure(MethodNode mn){
		
		methodName = mn.name;  
		methodDesc = mn.desc;  
		
		//doubly linked list of method insn
		InsnList insnList = mn.instructions;  
		
		ArrayList<Integer> lineIndexList = new ArrayList<Integer>();
		
		//holds string representation of insn
		String[] tempInsnStringArray = new String[insnList.size()]; 
		
		//temp variables to save on memory usage
		char c;
		String insnString;
		int maxInsnPerLine = 0;
		int maxInsnTemp = 0;
		
		for(int i = 0; i < insnList.size(); i++){
			
			//converts each insn to string and stores in String[]
			insnString = insnToString(insnList.get(i)).trim();  
			tempInsnStringArray[i] = insnString;
			
			//the first insn of every line of code in a method begins with 'L'
			//here we are checking if an insn is declaring a new line in a method
			if(insnString.charAt(0) == 'L'){

				//only a new line insn can have a length less than 3 
				if(insnString.length() > 2){
					c = insnString.charAt(2);
					
					//only a new line insn will have a number at index location 2 if the 
					//insn line number is greater than 9 (length greater than 2)
					if(c>='0' && c <= '9'){
					 	lineIndexList.add(i);
					 	if(maxInsnTemp > maxInsnPerLine){
					 		maxInsnPerLine = maxInsnTemp;
					 	}
					 	maxInsnTemp = 0;
				 	} else {
				 		maxInsnPerLine++;
				 	}
					
					//adds the insn index to the lineIndexList since the insn has length 
					//less than 3
				}else {
					lineIndexList.add(i);
					if(maxInsnTemp > maxInsnPerLine){
						maxInsnPerLine = maxInsnTemp;
					}
					maxInsnTemp = 0;
				}
				
				//We keep track of the max # of insn in any line so as to save memory
				//when we construct a 2D array to store the insn's by line
			} else {
				maxInsnPerLine++;
			}
		}
		
		//constructs the 2D array that stores the insn's by line
		numLines = lineIndexList.size();
		methodLineInsnSet = new String[lineIndexList.size()][maxInsnPerLine + 1];
		int index;
		
		//stores insn by line. That is, methodLineInsnSet[methodLine#][Insn#]
		for(int i = 0; i < lineIndexList.size(); i++){
			index = 0;
			if(i != lineIndexList.size() - 1){
				for(int j = lineIndexList.get(i) + 1; j < lineIndexList.get(i + 1); j++){
					methodLineInsnSet[i][index] = tempInsnStringArray[j];
					index++;
				}
				methodLineInsnSet[i][index] = "-1";
			} else {
				for(int j = lineIndexList.get(i) + 1; j < insnList.size(); j++){
					methodLineInsnSet[i][index] = tempInsnStringArray[j];
					index++;
				}
				methodLineInsnSet[i][index] = "-1";
			}
		}
	}
	
	/**
	 * converts a insn to a string and returns the string
	 * @param insn
	 * @return
	 */
	private static String insnToString(AbstractInsnNode insn){
		insn.accept(traceMethodVisitor);
		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();
		return sw.toString();
	}
	
	/**
	 * returns methodName
	 * @return
	 */
	public String getMethodName(){
		return methodName;
	}
	
	/**
	 * returns method Description
	 * @return
	 */
	public String getMethodDescription(){
		return methodDesc;
	}
	
	/**
	 * returns method Insn set
	 * @return
	 */
	public String[][] getMethodInsnSet(){
		return methodLineInsnSet;
	}
	
	/**
	 * returns the number of insn sets or lines in the method
	 * @return
	 */
	public int getNumMethodLines(){
		return numLines;
	}
	
	private static Printer printer = new Textifier();
	private static TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(printer); 
	private String[][] methodLineInsnSet;
	private String methodName;
	private String methodDesc;
	private int numLines;
}
