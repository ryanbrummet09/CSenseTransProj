/**
 * @author ryanbrummet
 */

package version3;

import java.util.ArrayList;
import ASMModifiedSourceCode.*;

public class BlockStructure3 {

	public BlockStructure3(int blockStartIndex, int blockEndIndex, InsnList insnList, int methodBlockNumber){
		
		this.methodBlockNumber = methodBlockNumber;
		this.blockStartIndex = blockStartIndex;
		this.blockEndIndex = blockEndIndex;
		this.insnList = insnList;
		numBlockInsn = 0;
		methodEndBlock = false;
		insnTypes = new ArrayList<Integer>();
		blockBranches = false;
		nextBranches = new ArrayList<LabelNode>();
		methodCalls = new ArrayList<MethodInsnNode>();
		methodCallsInsnListIndex = new ArrayList<Integer>();
		int temp = 0;
		for(int i = blockStartIndex; i < blockEndIndex; i++){
			numBlockInsn++;
			temp = insnList.get(i).getType();
			if(!insnTypesContains(temp)){
				insnTypes.add(temp);
			}
			if(temp == Constants.ABSTRACT_METHOD_INSN || temp == Constants.ABSTRACT_JUMP_INSN || temp == Constants.ABSTRACT_TABLESWITCH_INSN || temp == Constants.ABSTRACT_LOOKUPSWITCH_INSN){
				
				if(temp == Constants.ABSTRACT_METHOD_INSN){
					methodCalls.add((MethodInsnNode) insnList.get(i));
					methodCallsInsnListIndex.add(i);
				} else if (temp == Constants.ABSTRACT_JUMP_INSN) {
					nextBranches.add(((JumpInsnNode) insnList.get(i)).label);
					blockBranches = true;
				} else if (temp == Constants.ABSTRACT_TABLESWITCH_INSN) {
					nextBranches.addAll(((TableSwitchInsnNode) insnList.get(i)).labels);
					blockBranches = true;
				} else if (temp == Constants.ABSTRACT_LOOKUPSWITCH_INSN) {
					nextBranches.addAll(((LookupSwitchInsnNode) insnList.get(i)).labels);
					blockBranches = true;
				}
			}
			temp = insnList.get(i).getOpcode();
			if((temp <= 177 && temp >= 172) || blockEndIndex == insnList.size() - 1){
				methodEndBlock = true;
			}
		}
		if(!methodEndBlock && blockEndIndex != insnList.size() - 1){
			nextBranches.add((LabelNode) insnList.get(blockEndIndex));
		}
	}
	
	/**
	 * 
	 * @return returns the identifying label node associated with this block
	 */
	public LabelNode getLabelNode(){
		return (LabelNode) insnList.get(blockStartIndex);
	}
	
	/**
	 * 
	 * @return returns true if this block has more than one potential successive block
	 */
	public boolean doesBlockMulitBranch(){
		return blockBranches;
	}
	
	/**
	 * 
	 * @return returns the starting index of this block; inclusive
	 */
	public int getBlockStartIndex(){
		return blockStartIndex;
	}
	
	/**
	 * 
	 * @return returns the ending index of this block; exclusive
	 */
	public int getBlockEndIndex(){
		return blockEndIndex;
	}
	
	/**
	 * 
	 * @return returns the insnList that this block is a part of
	 */
	public InsnList getInsnList(){
		return insnList;
	}
	
	/**
	 * 
	 * @return returns a list of the insn types in this block
	 */
	public ArrayList<Integer> getInsnTypes(){
		return insnTypes;
	}
	
	/**
	 * 
	 * @return returns the MethodInsnNodes in this block
	 */
	public ArrayList<MethodInsnNode> getMethodCalls(){
		return methodCalls;
	}
	
	/**
	 * 
	 * @return returns true if this block contains a return insn
	 */
	public boolean isMethodReturnBlock(){
		return methodEndBlock;
	}
	
	/**
	 * 
	 * @return returns the number of insn in this block
	 */
	public int getNumBlockInsn(){
		return numBlockInsn;
	}
	
	/**
	 * 
	 * @return returns the LabelNodes that this block may branch to
	 */
	public ArrayList<LabelNode> getNextBranches(){
		return nextBranches;
	}
	
	/**
	 * 
	 * @param number the index of the instruction in the block to be returned not the 
	 * index of the instruction to be returned from insnList
	 * @return returns the AbstractInsnNode at blockStartIndex + number of the insnList
	 */
	public AbstractInsnNode getBlockInsnNumber(int number){
		if((number < getNumBlockInsn() && number > 0) || number == 0){
			return insnList.get(blockStartIndex + number);
		} else {
			System.out.println("BlockStructure3/getBlockInsnNumber error");
			return null;
		}
	}
	
	/**
	 * 
	 * @return returns the position of this block in a methodStructure
	 */
	public int getMethodBlockNumber(){
		return methodBlockNumber;
	}
	
	/**
	 * Tests if the int param is contained in insnTypes
	 * @param num
	 * @return
	 */
	private boolean insnTypesContains(int num){
		for(int i = 0; i < insnTypes.size(); i++){
			if(insnTypes.get(i) == num){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * inclusive start of this block
	 */
	private int blockStartIndex;
	
	/**
	 * exclusive end of this block
	 */
	private int blockEndIndex;
	
	/**
	 * complete list of AbstractInsnNodes from Method associated with this block
	 */
	private InsnList insnList;
	
	/**
	 * true if this block contains a return insn or is the last block in the method
	 */
	private boolean methodEndBlock;
	
	/**
	 * true if this block has more than one potential successive block
	 */
	private boolean blockBranches;

	/**
	 * contains the types of insn's in this block
	 */
	private ArrayList<Integer> insnTypes;
	
	/**
	 * List of successive Blocks after this block
	 */
	private ArrayList<LabelNode> nextBranches;
	
	/**
	 * List of methods called in this block
	 */
	private ArrayList<MethodInsnNode> methodCalls;
	
	/**
	 * index i corresponds to index i of methodCalls;
	 */
	private ArrayList<Integer> methodCallsInsnListIndex;
	
	/**
	 * the number of insn in this block
	 */
	private int numBlockInsn;
	
	/**
	 * determines the placement of this block relative to the other blocks in a MethodStructure
	 */
	private int methodBlockNumber;
	
	/**
	 * AbstractInsnNode type constants
	 */	
}
