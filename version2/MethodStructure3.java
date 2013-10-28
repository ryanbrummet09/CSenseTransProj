/**
 * @author ryanbrummet
 * NOTE THAT IT IS ASSUMED THAT NO GOTO OR CONTINUE STATEMENTS ARE USED IN THE TARGET FILE
 */

package version3;

import java.util.ArrayList;

import ASMModifiedSourceCode.*;

public class MethodStructure3 {

	public MethodStructure3(MethodNode mn){
		methodNode = mn;
		insnList = mn.instructions;
		methodName = mn.name;
		methodDesc = mn.desc;
		blockStructures = new ArrayList<BlockStructure3>();
		lineIndexList = new ArrayList<Integer>();
		breakBlockIndexes = new ArrayList<Integer>();
		dudBlockIndexes = new ArrayList<Integer>();
		tryCatchBlockIndexes = new ArrayList<Integer>();
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
		findLoopBlockIntervalsAndTypes();
		findSwitchAndCases();
		findBreaks();
		findDudBlocks();
		//findContinue();
		//findIfClassStmnts();
		
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
	 * @return returns an ArrayList<Integer> that is a multiple of three where every three elements grouped together corresponds
	 * to the start block, end block, and type of a loop, respectfully.
	 */
	public ArrayList<Integer> getLoopBlockIntervalsAndTypes(){
		return loopBlockIntervalsAndTypes;
	}
	
	/**
	 * 
	 * @return returns an ArrayList<Integer> that contains the indexes of the blocks in blockStructures that are continues
	 */
	/*public ArrayList<Integer> getContinueBlockIndexes(){
		return continueBlockIndexes;
	}*/
	
	/**
	 * 
	 * @return returns an ArrayList<Integer> containing all the block indexes of the beginning and end of switch statements.  These values
	 * are stroed in an ArrayList<Integer> where the first value corresponding to a single switch statement is the number of cases in the switch
	 * plus 2 (plus two for the start and end indexes).  The second value is the start index.  The following values after this are the indexes
	 * of the starts of the cases.  The start of a case is the end of the previous case unless the case in question is the first case. If this
	 * the case in question is the first case then the start of the case is the block following the start index for the switch statement.  The
	 * last element is the end index of the switch statement.  For example:
	 * #OfElementsUsedToDescribeThisSwitch, startIndex, case0, case1,...casen, endIndex.  Notice #OfElementsUsedToDescribeThisSwitch = n + 2.
	 */
	public ArrayList<Integer> getSwitchAndCasesBlockIndexes(){
		return switchAndCasesBlockIndexes;
	}
	
	/**
	 * 
	 * @return returns an ArrayList<Integer> containing the indexes in blockStructures that are break blocks
	 */
	public ArrayList<Integer> getBreakBlockIndexes(){
		return breakBlockIndexes;
	}
	
	/**
	 * 
	 * @return returns an ArrayList<Integer> that contains the blockStructure indexes of blocks that are of no use
	 */
	public ArrayList<Integer> getDudBlockIndexes(){
		return dudBlockIndexes;
	}
	
	/**
	 * 
	 * @return returns the local variables of the method associated with this MethodStructure as LocalVariableNodes
	 */
	public ArrayList<LocalVariableNode> getLocalVariables(){
		return (ArrayList<LocalVariableNode>) methodNode.localVariables;
	}
	
	/**
	 * 
	 * @return returns an ArrayList<Integer> that contains all the ASM types that occur in the method associated with this MethodStructure
	 */
	public ArrayList<Integer> getASMTypes(){
		ArrayList<Integer> typeReturnArray = new ArrayList<Integer>();
		for(BlockStructure3 bs: blockStructures){
			for(int i = 0; i < bs.getNumBlockInsn(); i++){
				if(!typeReturnArray.contains(bs.getBlockInsnNumber(i).getType())){
					typeReturnArray.add(bs.getBlockInsnNumber(i).getType());
				}
			}
		}
		return typeReturnArray;
	}
	
	/**
	 * Finds and stores block index of loop start, block index of loop stop, and loop type
	 */
	private void findLoopBlockIntervalsAndTypes(){

		/* there will always be a multiple of three elements in tempStart.
		 * That is, every 3 elements correspond to an individual loop.
		 * the first element is the blockStartIndex of the first potentially
		 * repeating block in a loop.  This is the block that the last block
		 * in the sequence has a JumpInsn to.  The second element is the
		 * blockStartIndex of the last block in the loop.  The third element
		 * is the type of loop.  We cannot accurately distinguish between
		 * for loops and while loops so they are treated as the same type of
		 * loop: FIRST_RUN_CONDITIONAL_LOOPS.  Because of the ambiguity of 
		 * determining the first block associated with a loop it should be noted
		 * that there maybe, and most likely are, an indeterminate number of
		 * blocks associated with each loop that are not included in the loop
		 * interval.  These blocks are directly "before" the start of the interval.
		 */
		BlockStructure3 endBlock;
		ArrayList<Integer> tempStart = new ArrayList<Integer>();
		ArrayList<Integer> loopsStartingAtI;
		for(int i = 0; i < blockStructures.size(); i++){
			loopsStartingAtI = loopDetector(i);
			for(Integer temp: loopsStartingAtI){
				loopDefined:
				if(temp >= 0){
					temp = lineIndexList.indexOf(temp);
					endBlock = blockStructures.get(temp);
					tempStart.add(i);
					tempStart.add(temp);
					//tests if loop is a for each loop
					if(endBlock.getInsnTypes().contains(Constants.ABSTRACT_METHOD_INSN)){ 
						for(int t = 0; t < endBlock.getNumBlockInsn(); t++){
							if(endBlock.getBlockInsnNumber(t).getType() == Constants.ABSTRACT_METHOD_INSN){
								MethodInsnNode potMethNode = (MethodInsnNode) endBlock.getBlockInsnNumber(t);
								if(potMethNode.owner.equals("java/util/Iterator") && potMethNode.name.equals("hasNext") && potMethNode.getOpcode() == Constants.OPCODE_INVOKEINTERFACE){
									tempStart.add(Constants.FOR_EACH_LOOP);
									break loopDefined;
								}
							}
						}
					} 
					
					//tests if loop is a do while loop
					boolean isDoWhileLoop = true;
					breakOutLoop0:
					for(int a = 0; a < i; a++){
						for(int b = 0; b < blockStructures.get(a).getNumBlockInsn(); b++){
							if(blockStructures.get(a).getBlockInsnNumber(b).getType() == Constants.ABSTRACT_JUMP_INSN){
								LabelNode lNode = ((JumpInsnNode) blockStructures.get(a).getBlockInsnNumber(b)).label;
								if(i - temp == 0){
									if(lNode == endBlock.getLabelNode()){
										isDoWhileLoop = false;
										break breakOutLoop0;
									}
								} else {
									for(int c = i + 1; c <= temp; c++){
										if(lNode == blockStructures.get(c).getLabelNode()){
											isDoWhileLoop = false;
											break breakOutLoop0;
										}
									}
								}
								
							}	
						}	
					}
					if(isDoWhileLoop){
						tempStart.add(Constants.DO_WHILE_LOOP);
						break loopDefined;
					}	
					
					//while loops and for loops are too similar at the byte code level to accurately differentiate between the two
					//therefore we define a for and while loops as the same class of loop: FIRST_RUN_CONDITIONAL_LOOP
					//if the loop is not a FOR_EACH or DO_WHILE loop then it must be a FIRST_RUN_CONDITIONAL_LOOP
					tempStart.add(Constants.FIRST_RUN_CONDITIONAL_LOOP);
				}
			}
		}	
		loopBlockIntervalsAndTypes = tempStart;
	}

	/**
	 * finds all the block indexes of all the continues in a method.  MUST be run AFTER findLoopBlockIntervalsAndTypes.  Continue stmnts
	 * CANNOT be identified in general.  There is a very high percentage change that continue stmnts will be located, but based on
	 * empirical evidence there is no 100% way to determine if a statement is a continue stmnt since certain break stmnts, and to an
	 * extent certain if class stmnts, can have the exact same set of insn in the block in question.
	 */	
	/*private void findContinue(){
		ArrayList<Integer> tempArray = new ArrayList<Integer>();
		for(int i = 0; i < loopBlockIntervalsAndTypes.size(); i = i + 3){
			System.out.println(loopBlockIntervalsAndTypes.get(i) + " " + loopBlockIntervalsAndTypes.get(i + 1));
			for(int j = loopBlockIntervalsAndTypes.get(i); j < loopBlockIntervalsAndTypes.get(i + 1); j++){
				for(int a = 0; a < blockStructures.get(j).getNumBlockInsn(); a++){
					if(blockStructures.get(j).getBlockInsnNumber(a).getType() == Constants.ABSTRACT_JUMP_INSN && blockStructures.get(j).getNumBlockInsn() == 3){
						if(blockStructures.get(j).getBlockInsnNumber(0).getType() == Constants.ABSTRACT_LABEL && blockStructures.get(j).getBlockInsnNumber(1).getType() == Constants.ABSTRACT_LINE && blockStructures.get(j).getBlockInsnNumber(2).getOpcode() == Constants.OPCODE_GOTO){
							if(labelToBlock(((JumpInsnNode) blockStructures.get(j).getBlockInsnNumber(a)).label).getMethodBlockNumber() <= blockStructures.get(loopBlockIntervalsAndTypes.get(i + 1)).getMethodBlockNumber()){
								if(!dudBlockIndexes.contains(j) && !breakBlockIndexes.contains(j) && !tempArray.contains(j)){
									tempArray.add(j);
								}
								
							}
						}
					}
				}
			}
		}
		continueBlockIndexes = tempArray;
	}*/
	
	/**
	 * finds all the block indexes of the beginning and end of switch statements.  These values are stored in an ArrayList<Integer> where
	 * the first value corresponding to a single switch statement is the number of cases in the switch plus 2 (plus two for the start and end
	 * indexes).  The second value is the start index.  The following values after this are the indexes of the starts of the cases.  The start 
	 * of a case is the end of the previous case unless the case in question is the first case.  If the case in question is the first case then
	 * the start of the case is the block following the start index for the switch statement.  The last element is the end index of the switch
	 * statement.  For example: #OfElementsUsedToDescribeThisSwitch, startIndex, case0, case1,...casen, endIndex.  Notice 
	 * #OfElementsUsedToDescribeThisSwitch = n + 2.
	 */
	private void findSwitchAndCases(){
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int i = 0; i < blockStructures.size(); i++){
			for(int j = 0; j < blockStructures.get(i).getNumBlockInsn(); j++){
				if(blockStructures.get(i).getBlockInsnNumber(j).getType() == Constants.ABSTRACT_TABLESWITCH_INSN){
					TableSwitchInsnNode tsiNode = (TableSwitchInsnNode) blockStructures.get(i).getBlockInsnNumber(j);
					int tsiTempIndex = temp.size();
					temp.add(tsiNode.labels.size() + 2);
					temp.add(i);
					int tsiBranchIndex = i + 1;
					for(int a = 0; a < tsiNode.labels.size(); a++){
						for(int b = tsiBranchIndex; b < blockStructures.size(); b++){
							if(blockStructures.get(b).getLabelNode() == tsiNode.labels.get(a)){
								temp.add(b);
								tsiBranchIndex = b;
								break;
							}
						}
					}
					breakOutLoop0:
					for(int a = temp.get(tsiTempIndex + 2); a < temp.get(tsiTempIndex + 3); a++){
						for(int b = 0; b < blockStructures.get(a).getNumBlockInsn(); b++){
							if(blockStructures.get(a).getBlockInsnNumber(b).getType() == Constants.ABSTRACT_JUMP_INSN){
								if(blockStructures.indexOf(labelToBlock(((JumpInsnNode) blockStructures.get(a).getBlockInsnNumber(b)).label)) > temp.get(tsiTempIndex + tsiNode.labels.size() + 1)){
									temp.add(blockStructures.indexOf(labelToBlock(((JumpInsnNode) blockStructures.get(a).getBlockInsnNumber(b)).label)));
									break breakOutLoop0;
								}
							}
						}
					}
				}
			}
		}
		switchAndCasesBlockIndexes = temp;
	}

	/**
	 * Finds the indexes of blockStructures that are break blocks
	 */
	private void findBreaks(){
		ArrayList<Integer> tempIntervals = new ArrayList<Integer>();
		ArrayList<Integer> sortedIntervals = new ArrayList<Integer>();
		int switchIndex = 0;
		for(int i = 0; i < switchAndCasesBlockIndexes.size(); i = i + switchIndex + 1){
			switchIndex = switchAndCasesBlockIndexes.get(i);
			for(int j = i + 2; j < i + switchIndex; j++){
				tempIntervals.add(switchAndCasesBlockIndexes.get(j));
				tempIntervals.add(switchAndCasesBlockIndexes.get(j + 1));
			}
		}
		int test = tempIntervals.size()/2;
		for(int i = 0; i < test; i++){
			int min = blockStructures.size();
			int saveValue = -1;
			for(int j = 0; j < tempIntervals.size(); j = j + 2){
				if(tempIntervals.get(j) < min){
					min = tempIntervals.get(j);
					saveValue = j;
				}
			}
			sortedIntervals.add(tempIntervals.remove(saveValue));
			sortedIntervals.add(tempIntervals.remove(saveValue));
		}
		for(int i = 0; i < sortedIntervals.size(); i = i + 2){
			for(int j = sortedIntervals.get(i); j < sortedIntervals.get(i + 1); j++){
				for(int a = 0; a < blockStructures.get(j).getNumBlockInsn(); a++){
					if(blockStructures.get(j).getBlockInsnNumber(a).getType() == Constants.ABSTRACT_JUMP_INSN){
						if((labelToBlock(((JumpInsnNode) (blockStructures.get(j).getBlockInsnNumber(a))).label)).getMethodBlockNumber() > sortedIntervals.get(i + 1) - 1){
							breakBlockIndexes.add(j);
						}
					}
				}
			}
		}
		tempIntervals = new ArrayList<Integer>();
		sortedIntervals = new ArrayList<Integer>();
		for(int i = 0; i < loopBlockIntervalsAndTypes.size(); i = i + 3){
			tempIntervals.add(loopBlockIntervalsAndTypes.get(i));
			tempIntervals.add(loopBlockIntervalsAndTypes.get(i + 1));
		}
		test = tempIntervals.size()/2;
		for(int i = 0; i < test; i++){
			int min = blockStructures.size();
			int saveValue = -1;
			for(int j = 0; j < tempIntervals.size(); j = j + 2){
				if(tempIntervals.get(j) < min){
					min = tempIntervals.get(j);
					saveValue = j;
				}
			}
			sortedIntervals.add(tempIntervals.remove(saveValue));
			sortedIntervals.add(tempIntervals.remove(saveValue));
		}
		for(int i = 0; i < sortedIntervals.size(); i = i + 2){
			for(int j = sortedIntervals.get(i); j < sortedIntervals.get(i + 1); j++){
				for(int a = 0; a < blockStructures.get(j).getNumBlockInsn(); a++){
					if(blockStructures.get(j).getBlockInsnNumber(a).getType() == Constants.ABSTRACT_JUMP_INSN){
						if((labelToBlock(((JumpInsnNode) (blockStructures.get(j).getBlockInsnNumber(a))).label)).getMethodBlockNumber() > sortedIntervals.get(i + 1)){
							breakBlockIndexes.add(j);
						}
					}
				}
			}
		}
	}

	private void findTryCatchBlockIndexes(){
		ArrayList<TryCatchBlockNode> inBuiltTryCatchList = (ArrayList<TryCatchBlockNode>) methodNode.tryCatchBlocks;
		for(TryCatchBlockNode tc: inBuiltTryCatchList){
			
		}
	}
	private void findIfClassStmnts(){
		ArrayList<Integer> jNodeBlocks = new ArrayList<Integer>();
		ArrayList<Integer> targetJNodes = new ArrayList<Integer>();
		jNodeBlocks.addAll(breakBlockIndexes);
		for(int i = 0; i < loopBlockIntervalsAndTypes.size(); i = i + 3){
			jNodeBlocks.add(loopBlockIntervalsAndTypes.get(i + 1));
		}
		jNodeBlocks.addAll(dudBlockIndexes);
		for(int i = 0; i < blockStructures.size(); i++){
			if(!jNodeBlocks.contains(blockStructures.get(i).getMethodBlockNumber())){
				for(int j = 0; j < blockStructures.get(i).getNumBlockInsn(); j++){
					if(blockStructures.get(i).getBlockInsnNumber(j).getType() == Constants.ABSTRACT_JUMP_INSN){
						targetJNodes.add(i);
					}
				}
			}
		}
		
	}
	
	/**
	 * Finds all the blocks that have JumpInsn into either switch stmnts or loops.  Since we can identify loop types, this information
	 * can be discarded making finding if class stmnts easier
	 */
	private void findDudBlocks(){
		for(int i = 0; i < loopBlockIntervalsAndTypes.size(); i = i + 3){
			for(int j = 0; j < loopBlockIntervalsAndTypes.get(i); j++){
				for(int a = 0; a < blockStructures.get(j).getNumBlockInsn(); a++){
					if(blockStructures.get(j).getBlockInsnNumber(a).getType() == Constants.ABSTRACT_JUMP_INSN){
						JumpInsnNode jNode = (JumpInsnNode) blockStructures.get(j).getBlockInsnNumber(a);
						int temp = labelToBlock(jNode.label).getMethodBlockNumber();
						if(temp > loopBlockIntervalsAndTypes.get(i) && temp <= loopBlockIntervalsAndTypes.get(i + 1)){
							dudBlockIndexes.add(j);
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param blockStructureIndex
	 * @return Returns the starting index in insnList of the "last" sequentially linear (one run of the loop before repeating) block in a loop
	 */
	private ArrayList<Integer> loopDetector(int blockStructureIndex){
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int i = blockStructures.size() - 1; i >= blockStructureIndex; i--){
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
	
	private ArrayList<Integer> lineIndexList;  //lineIndexList gives the labelNodes associated with blocks such that ((LabelNode) insnList.get(lineIndexList.get(i))) == blockStructures.get(i).getLabelNode()
	private ArrayList<Integer> loopBlockIntervalsAndTypes;  //block index loop start, block index loop end, loop type
	private ArrayList<Integer> breakBlockIndexes;
	private ArrayList<Integer> dudBlockIndexes;
	private ArrayList<Integer> tryCatchBlockIndexes;
	//private ArrayList<Integer> continueBlockIndexes;
	private ArrayList<Integer> switchAndCasesBlockIndexes;
	private ArrayList<BlockStructure3> blockStructures;
	private InsnList insnList;
	private String methodName;
	private String methodDesc;
	private MethodNode methodNode;
}
