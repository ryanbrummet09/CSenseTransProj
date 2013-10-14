/**
 * @author ryanbrummet
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.objectweb.asm.util.*;

import ASMModifiedSourceCode.*;

 /**
 *Defines a BlockStructure where a block structure consists of all the instructions of
 *one line and the all the possible block structures that could be executed after a 
 *particular block (branches/leaves).  If a method is called inside a block the object
 *making the call (or the class the method is located in if the method is static) is stored
 *as well as the name of the method called.
 */
public class BlockStructure {

	/**
	 * Constructor for a BlockStructure
	 * @param list
	 */
	public BlockStructure(int startIndex, int endIndex, InsnList list){
		blockInsn = new ArrayList<AbstractInsnNode>();
		for(int i = startIndex; i < endIndex; i++){
			blockInsn.add(list.get(i));
		}
		endBlock = false;
		numMethodCalls = 0;
		branches = 0;
		numBlockInsn = blockInsn.size();
		blockId = list.get(0).toString();
		insnTypes = new ArrayList<Integer>();
		methodCalls = new ArrayList<MethodInsnNode>();
		nextBranchingBlocks = new ArrayList<LabelNode>();
		branchTest();
	}
	
	/**
	 * Tests whether this block has multiple possible Blocks that may follow this
	 * block.  If this block contains a return statement then this block is 
	 * considered an end block.
	 */
	private void branchTest(){
		int temp;
		for(int i = 0; i < blockInsn.size(); i++){
			temp = blockInsn.get(i).getType();
			if(!contains(temp)){
				insnTypes.add(temp);
			}
			if(temp == 5 || temp == 7 || temp == 11 || temp == 12){
				branches++;
				if(temp ==5){
					numMethodCalls++;
					methodCalls.add((MethodInsnNode) blockInsn.get(i));
				} else if ( temp == 7) {
					nextBranchingBlocks.add(blockInsn.get(i).getLabelNode());
				} else if (temp == 11) {
					//may need to account for more
					//this includes the default handler block, min key value, and max key value
					nextBranchingBlocks.addAll(blockInsn.get(i).getLabelNodeSet());
				} else if (temp == 12) {
					//may need to account for more
					//this includes the default handler block and the list of keys
					nextBranchingBlocks.addAll(blockInsn.get(i).getLabelNodeSet());
				}
			}
			temp = blockInsn.get(i).getOpcode();
			if(temp <= 177 && temp >= 172){
				endBlock = true;
			}
		}
	}
	
	/**
	 * Returns true if the insnTypes ArrayList of ints contains the given param
	 * @param num
	 * @return
	 */
	private boolean contains(int num){
		for(int i = 0; i < insnTypes.size(); i++){
			if(insnTypes.get(i) == num){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Converts a AbstractInsnNode into a string
	 * @param insn
	 * @return
	 */
	private String insnToString(AbstractInsnNode insn){
		insn.accept(tmv);
		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();
		return sw.toString().trim();
	}

	/**
	 * Returns whether this block is the last block to run in a method.  It 
	 * may be possible that there are multiple end blocks in a method
	 * @return
	 */
	public boolean isEndBlock(){
		return endBlock;
	}
	
	/**
	 * Returns the unique LabelNode that acks as the ID of this block
	 * @return
	 */
	public String getBlockId(){
		return blockId;
	}
	
	/**
	 * Returns the number of blocks in this BlockStructure
	 * @return
	 */
	public int getNumBlockInsn(){
		return numBlockInsn;
	}
	
	/**
	 * Returns a string representation of a BlockStructure
	 * @return
	 */
	public ArrayList<String> getBlockAsStrings(){
		ArrayList<String> temp = new ArrayList<String>();
		for(int i = 0; i < numBlockInsn; i++){
			temp.add(insnToString(blockInsn.get(i)));
		}
		return temp;
	}
	
	public ArrayList<AbstractInsnNode> getBlockInsn(){
		return blockInsn;
	}
	
	public ArrayList<LabelNode> getBranchingBlocks(){
		return nextBranchingBlocks;
	}
	
	//used to convert block to strings
	private Printer printer = new Textifier();
	private TraceMethodVisitor tmv = new TraceMethodVisitor(printer);
	
	//the number of branches and the branches themselves
	private int branches;
	private ArrayList<LabelNode> nextBranchingBlocks;
	
	//num of insn in a block and the insn themselves
	private int numBlockInsn;
	private ArrayList<AbstractInsnNode> blockInsn;
	
	//an arraylist containing a list of the different types of AbstractInsnNodes in the block
	private ArrayList<Integer> insnTypes;
	
	//this block's unique identifier
	private String blockId;
	
	//the number of method calls made in this block and the methods that are called
	private int numMethodCalls;
	private ArrayList<MethodInsnNode> methodCalls;
	
	//indicates whether this block contains a return insn
	private boolean endBlock;  	
	
}
