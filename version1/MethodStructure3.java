/**
 * @author ryanbrummet
 */

package version3;

import java.util.ArrayList;
import ASMModifiedSourceCode.*;

public class MethodStructure3 {

	public MethodStructure3(MethodNode mn){
		
		insnList = mn.instructions;
		methodName = mn.name;
		methodDesc = mn.desc;
		blockStructures = new ArrayList<BlockStructure3>();
		lineIndexList = new ArrayList<Integer>();
		
		for(int i = 0; i < insnList.size(); i++){
			if(insnList.get(i) instanceof LabelNode){
				lineIndexList.add(i);
			}
		}
		
		for(int i = 0; i < lineIndexList.size(); i++){
			if(i !=  lineIndexList.size() - 1){
				blockStructures.add(new BlockStructure3(lineIndexList.get(i), lineIndexList.get(i + 1), insnList, i));
			} else {
				blockStructures.add(new BlockStructure3(lineIndexList.get(i), insnList.size() - 1, insnList, i));
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
		return insnList.size();
	}
	
	/**
	 * Returns the number of unique blocks.  That is, blocks that are not repeated
	 * due to jumps
	 * @return
	 */
	public int getNumUniqueBlocks(){
		return lineIndexList.size();
	}
	
	/**
	 * 
	 * @return returns the blockStructures of this MethodStructure
	 */
	public ArrayList<BlockStructure3> getBlockStructures(){
		return blockStructures;
	}
	
	/**
	 * 
	 * @return returns the insnList of this MethodStructure
	 */
	public InsnList getInsnList(){
		return insnList;
	}
	
	/**
	 * 
	 * @return returns an ArrayList containing the start and stop indexes of each loop
	 * in a MethodStructure followed by an int describing the loop type
	 */
	public ArrayList<Integer> getLoopStartAndStops(){
		/* there will always be a multiple of three elements in tempStart.
		 * the first two elements correspond to the first block used by the loop
		 * (the first element) and the "last" block used by the loop (the second
		 * element).  The last element is the type of the loop.
		 */
		BlockStructure3 endBlock;
		ArrayList<Integer> tempStart = new ArrayList<Integer>();
		ArrayList<Integer> loopsStartingAtI;
		for(int i = 0; i < blockStructures.size(); i++){
			loopsStartingAtI = loopDetector2(i);
			for(Integer temp: loopsStartingAtI){
				loopDefined:
				if(temp >= 0){
					temp = lineIndexList.indexOf(temp);
					endBlock = blockStructures.get(temp);
					BlockStructure3 potForInitBlock = blockStructures.get(i - 1);
					if(potForInitBlock.getInsnTypes().contains(Constants.ABSTRACT_JUMP_INSN)){
						int intHolder = 0;
						exitThisLoop:
						for(int a = 0; a < potForInitBlock.getNumBlockInsn(); a++){
							if(potForInitBlock.getBlockInsnNumber(a).getType() == Constants.ABSTRACT_JUMP_INSN){
								intHolder = a;
								break exitThisLoop;
							}
						}
						int intHolder2 = 0;
						exitThisLoop2:
						for(int a = 0; a < endBlock.getNumBlockInsn(); a++){
							if(endBlock.getBlockInsnNumber(a).getType() == Constants.ABSTRACT_JUMP_INSN){
								intHolder2 = a;
								break exitThisLoop2;
							}
						}
						if(((JumpInsnNode) potForInitBlock.getBlockInsnNumber(intHolder)).label == endBlock.getLabelNode() && ((JumpInsnNode) endBlock.getBlockInsnNumber(intHolder2)).label == blockStructures.get(i).getLabelNode()){
							// for loop => block previous to end block of loop contains an iinc insn
							potForInitBlock = blockStructures.get(i - 2);
							if(potForInitBlock.getInsnTypes().contains(Constants.ABSTRACT_VAR_INSN) && insnList.get(endBlock.getBlockStartIndex() - 1).getType() == Constants.ABSTRACT_IINC_INSN){ 
								IincInsnNode iincNode = ((IincInsnNode) insnList.get(endBlock.getBlockStartIndex() - 1));
								for(int t = 0; t < potForInitBlock.getNumBlockInsn(); t++){
									if(potForInitBlock.getBlockInsnNumber(t).getOpcode() == Constants.OPCODE_ISTORE){
										if(((VarInsnNode) potForInitBlock.getBlockInsnNumber(t)).var == iincNode.var){
											tempStart.add(i - 2);
											tempStart.add(temp);
											tempStart.add(Constants.FOR_LOOP);
											break loopDefined;
										}
									}
								}
							}
						}
					}
					//for each loop => endBlock contains invokevirtual on iterator.hasNext()
					if(endBlock.getInsnTypes().contains(Constants.ABSTRACT_METHOD_INSN)){ 
						for(int t = 0; t < endBlock.getNumBlockInsn(); t++){
							if(endBlock.getBlockInsnNumber(t).getType() == Constants.ABSTRACT_METHOD_INSN){
								MethodInsnNode potMethNode = (MethodInsnNode) endBlock.getBlockInsnNumber(t);
								if(potMethNode.owner.equals("java/util/Iterator") && potMethNode.name.equals("hasNext") && potMethNode.getOpcode() == Constants.OPCODE_INVOKEINTERFACE){
									tempStart.add(i - 1);
									tempStart.add(temp);
									tempStart.add(Constants.FOR_EACH_LOOP);
									break loopDefined;
								}
							}
						}
					//must be while loop; while loop => block directly before loop block contains only line# and goto insn
					} 
					BlockStructure3 whileStartBlock = blockStructures.get(i - 1);
					if(whileStartBlock.getInsnTypes().contains(Constants.ABSTRACT_JUMP_INSN)){
						int intHolder = 0;
						exitThisLoop:
						for(int a = 0; a < whileStartBlock.getNumBlockInsn(); a++){
							if(whileStartBlock.getBlockInsnNumber(a).getType() == Constants.ABSTRACT_JUMP_INSN){
								intHolder = a;
								break exitThisLoop;
							}
						}
						int intHolder2 = 0;
						exitThisLoop2:
						for(int a = 0; a < endBlock.getNumBlockInsn(); a++){
							if(endBlock.getBlockInsnNumber(a).getType() == Constants.ABSTRACT_JUMP_INSN){
								intHolder2 = a;
								break exitThisLoop2;
							}
						}
						if(((JumpInsnNode) whileStartBlock.getBlockInsnNumber(intHolder)).label == endBlock.getLabelNode() && ((JumpInsnNode) endBlock.getBlockInsnNumber(intHolder2)).label == blockStructures.get(i).getLabelNode()){
							tempStart.add(i - 1);
							tempStart.add(temp);
							tempStart.add(Constants.WHILE_LOOP);
							break loopDefined;
						}
					}		
					// must be do while loop
					tempStart.add(i);
					tempStart.add(temp);
					tempStart.add(Constants.DO_WHILE_LOOP);
					break loopDefined;
				}
			}
			
		}
		return tempStart;
	}
	
	/**
	 * 
	 * @param blockStructureIndex
	 * @return Returns the starting index in insnList of the "last" sequentially linear (one run of the loop before repeating) block in a loop
	 */
	private ArrayList<Integer> loopDetector2(int blockStructureIndex){
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int i = blockStructures.size() - 1; i > blockStructureIndex; i--){
			for(int j = 0; j < blockStructures.get(i).getNumBlockInsn(); j++){
				if(blockStructures.get(i).getBlockInsnNumber(j).getType() == Constants.ABSTRACT_JUMP_INSN){
					JumpInsnNode jNode = (JumpInsnNode) blockStructures.get(i).getBlockInsnNumber(j);
					if(jNode.label == blockStructures.get(blockStructureIndex).getLabelNode()){
						temp.add(blockStructures.get(i).getBlockStartIndex());
					}
				}
			}
		}
		if(temp.size() == 0){
			temp.add(-1);
		}
		return temp;
	}
	
	/**
	 * 
	 * @param label
	 * @return takes a label and returns the corresponding BlockStructure
	 */
	private BlockStructure3 labelToBlock(LabelNode label){
		int temp = -1;
		outerloop:
		for(Integer i: lineIndexList){
			if(((LabelNode) insnList.get(i)) == label){
				temp = i;
				break outerloop;
			}
		}
		if(temp == -1){
			System.out.println("THERE IS SOMETHING WRONG IN labelToBlock METHOD IN MethodStructure3");
		}
		return blockStructures.get(lineIndexList.indexOf(temp));
	}
	
	private ArrayList<Integer> lineIndexList;
	private ArrayList<BlockStructure3> blockStructures;
	private InsnList insnList;
	private String methodName;
	private String methodDesc;
}
