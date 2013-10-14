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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import ASMModifiedSourceCode.*;

public class MethodStructure2 {
	
	/**
	 * Constructor for a MethodStructure.  Creates and stores an ArrayList of
	 * BlockStructures.
	 * @param mn
	 */
	public MethodStructure2(MethodNode mn){
		
		methodName = mn.name;
		methodDesc = mn.desc;
		numLines = 0;
		
		//doubly linked list of method insn
		insnList = mn.instructions;
		
		lineIndexList = new ArrayList<Integer>();
		
		for(int i = 0; i < insnList.size(); i++){
			if(insnList.get(i) instanceof LabelNode){
				lineIndexList.add(i);
			} else if (insnList.get(i) instanceof LineNumberNode){
				numLines++;
			}
		}
		numUniqueBlocks = lineIndexList.size();
		blockStructures = new ArrayList<BlockStructure2>();
		//ArrayList<AbstractInsnNode> tempArrayList;
		for(int i = 0; i < lineIndexList.size(); i++){
			/*tempArrayList = new ArrayList<AbstractInsnNode>();
			if(i != lineIndexList.size() - 1){
				for(int j = lineIndexList.get(i); j < lineIndexList.get(i + 1); j++){
					tempArrayList.add(insnList.get(j));
				}
			} else {
				for(int j = lineIndexList.get(i); j < insnList.size(); j++){
					tempArrayList.add(insnList.get(j));
				}
			}*/
			if(i != lineIndexList.size() - 1){
				blockStructures.add(new BlockStructure2(lineIndexList.get(i),lineIndexList.get(i + 1), insnList));
			} else {
				blockStructures.add(new BlockStructure2(lineIndexList.get(i),insnList.size(), insnList));
			}	
		}
	}
	
	/**
	 * returns the name of the method associated with this MethodStructure
	 * @return
	 */
	public String getMethodName(){
		return methodName;
	}
	
	/**
	 * Returns the method description associated with this MethodStructure
	 * @return
	 */
	public String getMethodDesc(){
		return methodDesc;
	}
	
	/**
	 * Returns the number of lines in the given method.  This number may not be
	 * the same as the number of unique blocks.
	 * @return
	 */
	public int getNumLines(){
		return numLines;
	}
	
	/**
	 * Returns the number of unique blocks.  That is, blocks that are not repeated
	 * due to jumps
	 * @return
	 */
	public int getNumUniqueBlocks(){
		return numUniqueBlocks;
	}
	
	public ArrayList<BlockStructure2> getBlockStructures(){
		return blockStructures;
	}
	
	public String getIndvInsnToString(int num){
		return insnToString(insnList.get(num));
	}
	
	private String insnToString(AbstractInsnNode insn){
		insn.accept(tmv);
		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();
		return sw.toString().trim();
	} 
	
	private Printer printer = new Textifier();
	private TraceMethodVisitor tmv = new TraceMethodVisitor(printer);
	
	private ArrayList<Integer> lineIndexList;
	private ArrayList<BlockStructure2> blockStructures;
	private InsnList insnList;
	private String methodName;
	private String methodDesc;
	private int numLines;
	private int numUniqueBlocks;
}
