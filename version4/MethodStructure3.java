/**
 * @author ryanbrummet
 * NOTE THAT IT IS ASSUMED THAT NO GOTO OR CONTINUE STATEMENTS ARE USED IN THE TARGET FILE
 * IF THERE IS A PROBLEM CHECK TRY CATCH FIRST, IF CLASS NEXT, SWITCH NEXT, LOOP NEXT, ALL ELSE
 * EVERYTHING THAT IS DEFINED USING A SCOPE WAS BE DEFINED ON ONE LINE IN THE TARGET FILE (NO SOURCE FORMATING)
 */

package version3;

import java.util.ArrayList;

import ASMModifiedSourceCode.*;

public class MethodStructure3 {

	public MethodStructure3(MethodNode mn, String owner){
		className = owner;
		methodNode = mn;
		insnList = mn.instructions;
		methodName = mn.name;
		methodDesc = mn.desc;
		blockStructures = new ArrayList<BlockStructure3>();
		lineIndexList = new ArrayList<Integer>();
		breakBlockIndexes = new ArrayList<Integer>();
		dudBlockIndexes = new ArrayList<Integer>();
		tryCatchBlockIndexes = new ArrayList<Integer>();
		ifClassStmntBlockIndexes = new ArrayList<Integer>();
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
		findTryCatchBlocks();
		//findContinue();
		findIfClassStmnts();
		
	}
	
	/**
	 * 
	 * @return returns the methodNode associated with this MethodStructure
	 */
	public MethodNode getMethodNode(){
		return methodNode;
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
	 * 
	 * @return returns the name of the class that the method associated with this MethodStructure is part of
	 */
	public String getClassName(){
		return className;
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
	 * are stored in an ArrayList<Integer> where the first value corresponding to a single switch statement is the number of cases in the switch
	 * plus 2 (plus two for the start and end indexes).  The second value is the start index.  The following values after this are the indexes
	 * of the starts of the cases.  The start of a case is the end of the previous case unless the case in question is the first case. If
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
	 * 
	 * @return returns an ArrayList<Integer> where each trycatch is described by an indeterminant number of elements.  The first element
	 * gives the number of elements following said element that are used to describe a particular trycatch.  The second element is the 
	 * block index of the start of the handler's scope.  The third element is the end of the handlers scope.  The fourth element is the 
	 * start of the handler's code.  Every subsequent element after this excluding the last two elements if the last element isn't -1
	 * otherwise excluding only the last element which consequently must be -1, are the block indexes of handler code sequences that exist
	 * concurrently with the "main" code handler sequence.  if the last element isn't negative one then the next to last element is the block
	 * index of the start of a finally section of a tryCatch, and the last element is the exclusive ending block of the finally section.  If
	 * the last element is negative one then there is no finally section (and thus no start and stop block indexes of a finally section).
	 */
	public ArrayList<Integer> getTryCatchBlockIndexes(){
		return tryCatchBlockIndexes;
	}
	
	/**
	 * 	
	 * @return returns an ArrayList<Integer> where each if class series is described by an indeterminant number of elements.  The first element
	 * gives the number of elements following said element that are used to describe a particular if class series.  Each subsequent element is 
	 * the start/check block associated with each stmnt of the if class series (if, else if, else) with the exception of the last element.  The
	 * last element is the ending block of the previous stmnt.  The scope of each stmnt is defined by the inclusive previous stmnt start index
	 * and the exclusive next stmnt index.  For example given 3 4 5 6, we know there are two if class stmnts, 4 and 5, and the scope of these stmnts
	 * are  [4,5) and [5,6), respectfully.
	 */
	public ArrayList<Integer> getIfClassStmntBlockIndexes(){
		return ifClassStmntBlockIndexes;
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
								if(potMethNode.owner.equals("java/util/Iterator") && potMethNode.name.equals("hasNext") && potMethNode.getOpcode() == Constants.OPCODE_INVOKE_INTERFACE){
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

	/**
	 * finds all the blocks associated with tryCatches in a method.  Try catches given in asm are combined since some try catches are in fact
	 * "finally"'s and also because some try catches exist solely to add additional catch statements to a try.  The information is stored in
	 * an ArrayList<Integer> where the number of elements corresponding to a particular tryCatch is indeterminant.  The first element is the
	 * number of elements succeeding but not counting the first element that are associated with the same tryCatch.  The second element is
	 * the start of the hanler's scope, inclusive.  The third element is the end of the handler's scope, exclusive.  The fourth element is 
	 * the start of the handler's code.  Each subsequent element after this excluding the last two elements if the last element isn't -1 
	 * otherwise excluding only the last element which consequently must be -1, are the block indexes of handler code sequences that exist
	 * concurrently with the "main" code handler sequence.  If the last element isn't negative one then the next to last element is the block
	 * index of the start of a finally section of a tryCatch, and the last element is the exclusive ending block of the finally section.  If
	 * the last element is negative one then there is no finally section (and thus not start and stop block indexes of a finally section).
	 */
	private void findTryCatchBlocks(){
		ArrayList<TryCatchBlockNode> inBuiltTryCatchList = (ArrayList<TryCatchBlockNode>) methodNode.tryCatchBlocks;
		ArrayList<TryCatchBlockNode> multiCatchList;
		TryCatchBlockNode finallyBlock;
		while(0 < inBuiltTryCatchList.size()){
			multiCatchList = new ArrayList<TryCatchBlockNode>();
			finallyBlock = null;
			for(int j = 1; j < inBuiltTryCatchList.size(); j++){
				if(inBuiltTryCatchList.get(0).start == inBuiltTryCatchList.get(j).start && inBuiltTryCatchList.get(0).end == inBuiltTryCatchList.get(j).end){
					multiCatchList.add(inBuiltTryCatchList.remove(j));
					j--;
				}
			}
			for(int j = 1; j < inBuiltTryCatchList.size(); j++){
				if(inBuiltTryCatchList.get(0).start == inBuiltTryCatchList.get(j).start  && inBuiltTryCatchList.get(j).type == null && labelToBlock(inBuiltTryCatchList.get(j).end).getMethodBlockNumber() > (labelToBlock(inBuiltTryCatchList.get(0).handler)).getMethodBlockNumber()){
					finallyBlock = inBuiltTryCatchList.remove(j);
					break;
				}
			}
			if(finallyBlock != null){
				tryCatchBlockIndexes.add(5 + multiCatchList.size());
				tryCatchBlockIndexes.add((labelToBlock(inBuiltTryCatchList.get(0).start)).getMethodBlockNumber());
				tryCatchBlockIndexes.add((labelToBlock(inBuiltTryCatchList.get(0).end)).getMethodBlockNumber());
				tryCatchBlockIndexes.add((labelToBlock(inBuiltTryCatchList.remove(0).handler)).getMethodBlockNumber());
				for(TryCatchBlockNode tcbn : multiCatchList){
					tryCatchBlockIndexes.add((labelToBlock(tcbn.handler)).getMethodBlockNumber());
				}
				tryCatchBlockIndexes.add((labelToBlock(finallyBlock.end)).getMethodBlockNumber());
				//IF THERE IS A PROBLEM THERE IS A GOOD CHANCE IT IS IN THE CODE SEGMENT BELOW
				breakOuterLoop0:
				for(int i = (labelToBlock(finallyBlock.end)).getMethodBlockNumber(); i < blockStructures.size(); i++){
					for(int j = 0; j < blockStructures.get(i).getNumBlockInsn(); j++){
						if(blockStructures.get(i).getBlockInsnNumber(j).getOpcode() == Constants.OPCODE_GOTO){
							for(int a = 0; a < blockStructures.get(i + 1).getNumBlockInsn(); a++){
								if(blockStructures.get(i + 1).getBlockInsnNumber(a).getOpcode() == Constants.OPCODE_ASTORE){
									tryCatchBlockIndexes.add(i + 2);
									break breakOuterLoop0;
								}
							}
						}
					}
				}
			} else {
				tryCatchBlockIndexes.add(4 + multiCatchList.size());
				tryCatchBlockIndexes.add((labelToBlock(inBuiltTryCatchList.get(0).start)).getMethodBlockNumber());
				tryCatchBlockIndexes.add((labelToBlock(inBuiltTryCatchList.get(0).end)).getMethodBlockNumber());
				tryCatchBlockIndexes.add((labelToBlock(inBuiltTryCatchList.remove(0).handler)).getMethodBlockNumber());
				for(TryCatchBlockNode tcbn : multiCatchList){
					tryCatchBlockIndexes.add((labelToBlock(tcbn.handler)).getMethodBlockNumber());
				}
				tryCatchBlockIndexes.add(-1);
			}
		}
	}
	
	/**
	 * Finds all the blocks used to define the scope of an if series.  Stores each value into ifClassStmntBlockIndexes where each if class series
	 * is described by an indeterminant number of elements.  The first element gives the number of elements following said element that are used
	 * to describe a particular if class series.  Each subsequent element is the start/check block associated with each stmnt of the if class
	 * series (if, else if, else) with the exception of the last element.  The last element is the ending block of the previous stmnt.  The scope
	 * of each stmnt is defined by the inclusive previous stmnt start index and he exclusive next stmnt index.  For example given 3 4 5 6, we know
	 * there are two if class stmnts, 4 and 5, and the scope of these stmnts are [4,5) and [5,6), respectfully.  IF YOU ARE GETTING SCOPE PROBLEMS
	 * THERE IS A VERY HIGH CHANCE THAT THE PROBLEM IS WITH THIS METHOD
	 */
	private void findIfClassStmnts(){
		ArrayList<Integer> jNodeBlocks = new ArrayList<Integer>();
		ArrayList<Integer> targetJNodes = new ArrayList<Integer>();
		jNodeBlocks.addAll(breakBlockIndexes);
		for(int i = 0; i < loopBlockIntervalsAndTypes.size(); i = i + 3){
			jNodeBlocks.add(loopBlockIntervalsAndTypes.get(i + 1));
		}
		jNodeBlocks.addAll(dudBlockIndexes);
		
		//If there is a problem there is a good chance it is in the tryCatch handling part below
		if(tryCatchBlockIndexes.size() > 0){
			int size;
			for(int i = 0; i < tryCatchBlockIndexes.size(); i = i + size + 1){
				size = tryCatchBlockIndexes.get(i);
				int catchCase = size - 3;
				for(int j = i + 2; j < i + 2 + catchCase; j++){
					jNodeBlocks.add(tryCatchBlockIndexes.get(j));
				}
				if(tryCatchBlockIndexes.get(i + size) != -1){
					jNodeBlocks.add(tryCatchBlockIndexes.get(i + 3 + catchCase) - 2);   //just added maybe a problem
				}
			}
		}
		
		for(int i = 0; i < blockStructures.size(); i++){
			if(!jNodeBlocks.contains(blockStructures.get(i).getMethodBlockNumber())){
				for(int j = 0; j < blockStructures.get(i).getNumBlockInsn(); j++){
					if(blockStructures.get(i).getBlockInsnNumber(j).getType() == Constants.ABSTRACT_JUMP_INSN && j == blockStructures.get(i).getNumBlockInsn() - 1){
						targetJNodes.add(i);
					}
				}
			}
		}
		JumpInsnNode presentJNode = null;
		int presentJNodeBlockDest;
		ArrayList<Integer> tempHolder;
		boolean hasElse;
		int presentJNodeBlock;
		int endBlock;
		while(targetJNodes.size() > 0){
			presentJNodeBlock = -1;
			endBlock = -1;
			tempHolder = new ArrayList<Integer>();
			for(int j = 0; j < blockStructures.get(targetJNodes.get(0)).getNumBlockInsn(); j++){
				if(blockStructures.get(targetJNodes.get(0)).getBlockInsnNumber(j).getType() == Constants.ABSTRACT_JUMP_INSN && j == blockStructures.get(targetJNodes.get(0)).getNumBlockInsn() - 1){
					presentJNodeBlock = targetJNodes.get(0);
					presentJNode = (JumpInsnNode) blockStructures.get(presentJNodeBlock).getBlockInsnNumber(j);
					break;
				}
			}
			presentJNodeBlockDest = (labelToBlock(presentJNode.label)).getMethodBlockNumber();
			hasElse = false;
			for(int j = 0; j < blockStructures.get(presentJNodeBlockDest).getNumBlockInsn(); j++){
				if(blockStructures.get(presentJNodeBlockDest).getBlockInsnNumber(j).getType() == Constants.ABSTRACT_JUMP_INSN){
					hasElse = true;
					break;
				}
			}
			boolean temp = false;
			for(int i = presentJNodeBlock; i < presentJNodeBlockDest; i++){
				for(int j = 0; j < blockStructures.get(i).getNumBlockInsn(); j++){
					if(blockStructures.get(i).getBlockInsnNumber(j).getOpcode() == Constants.OPCODE_GOTO){
						if((labelToBlock(((JumpInsnNode) blockStructures.get(i).getBlockInsnNumber(j)).label)).getMethodBlockNumber() > presentJNodeBlockDest){
							if(!temp){
								endBlock = (labelToBlock(((JumpInsnNode) blockStructures.get(i).getBlockInsnNumber(j)).label)).getMethodBlockNumber();
							}
							temp = true;
							if(targetJNodes.indexOf(i) != -1){
								targetJNodes.remove(targetJNodes.indexOf(i));
							}	
							break;
						}
					}
				}
			}
			if(hasElse){
				if(temp){
					tempHolder.add(targetJNodes.remove(targetJNodes.indexOf(presentJNodeBlock)));
					while(hasElse){
						hasElse = false;
						temp = false;
						if(targetJNodes.indexOf(presentJNodeBlockDest) != -1){
							for(int i = 0; i < blockStructures.get(presentJNodeBlockDest).getNumBlockInsn(); i++){
								if(blockStructures.get(targetJNodes.get(targetJNodes.indexOf(presentJNodeBlockDest))).getBlockInsnNumber(i).getType() == Constants.ABSTRACT_JUMP_INSN && i == blockStructures.get(targetJNodes.get(targetJNodes.indexOf(presentJNodeBlockDest))).getNumBlockInsn() - 1){
									presentJNodeBlock = targetJNodes.get(targetJNodes.indexOf(presentJNodeBlockDest));
									presentJNode = (JumpInsnNode) blockStructures.get(presentJNodeBlock).getBlockInsnNumber(i);
									tempHolder.add(targetJNodes.remove(targetJNodes.indexOf(presentJNodeBlock)));
									hasElse = true;
									break;
								}
							}
						}
						presentJNodeBlockDest = (labelToBlock(presentJNode.label)).getMethodBlockNumber();
						for(int i = presentJNodeBlock; i < presentJNodeBlockDest; i++){
							for(int j = 0; j < blockStructures.get(i).getNumBlockInsn(); j++){
								if(blockStructures.get(i).getBlockInsnNumber(j).getOpcode() == Constants.OPCODE_GOTO){
									if((labelToBlock(((JumpInsnNode) blockStructures.get(i).getBlockInsnNumber(j)).label)).getMethodBlockNumber() == endBlock && (labelToBlock(((JumpInsnNode) blockStructures.get(i).getBlockInsnNumber(j)).label)).getMethodBlockNumber() > presentJNodeBlockDest) {
										temp = true;
										if(targetJNodes.indexOf(i) != -1){
											targetJNodes.remove(targetJNodes.indexOf(i));
										}
										break;
									}
								}
							}
						}
						if(hasElse){
							if(temp){
								continue;
							} else {
								ifClassStmntBlockIndexes.add(tempHolder.size() + 1);
								ifClassStmntBlockIndexes.addAll(tempHolder);
								ifClassStmntBlockIndexes.add(endBlock);
								break;
							}
						} else {
							if(temp){
								ifClassStmntBlockIndexes.add(tempHolder.size() + 2);
								ifClassStmntBlockIndexes.addAll(tempHolder);
								ifClassStmntBlockIndexes.add(presentJNodeBlockDest);
								ifClassStmntBlockIndexes.add(endBlock);
								break;
							} else {
								ifClassStmntBlockIndexes.add(tempHolder.size() + 1);
								ifClassStmntBlockIndexes.addAll(tempHolder);
								ifClassStmntBlockIndexes.add(endBlock);
								break;
							}
						}
					}
				} else {
					ifClassStmntBlockIndexes.add(2);
					ifClassStmntBlockIndexes.add(targetJNodes.remove(targetJNodes.indexOf(presentJNodeBlock)));
					ifClassStmntBlockIndexes.add(presentJNodeBlockDest);
				}
			} else {
				if(temp){
					ifClassStmntBlockIndexes.add(3);
					ifClassStmntBlockIndexes.add(targetJNodes.remove(targetJNodes.indexOf(presentJNodeBlock)));
					ifClassStmntBlockIndexes.add(presentJNodeBlockDest);
					ifClassStmntBlockIndexes.add(endBlock);
				}else{
					ifClassStmntBlockIndexes.add(2);
					ifClassStmntBlockIndexes.add(targetJNodes.remove(targetJNodes.indexOf(presentJNodeBlock)));
					ifClassStmntBlockIndexes.add(presentJNodeBlockDest);
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
	private ArrayList<Integer> ifClassStmntBlockIndexes;
	private ArrayList<BlockStructure3> blockStructures;
	private InsnList insnList;
	private String methodName;
	private String methodDesc;
	private String className;
	private MethodNode methodNode;
}
