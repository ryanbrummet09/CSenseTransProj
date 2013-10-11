/**
 * @author ryanbrummet
 */

import java.util.ArrayList;
import ASMModifiedSourceCode.*;

 /**
 *Defines a BlockStructure where a block structure consists of all the instructions of
 *one line and the all the possible block structures that could be executed after a 
 *particular block (branches/leaves).  If a method is called inside a block the object
 *making the call (or the class the method is located in if the method is static) is stored
 *as well as the name of the method called.
 */
public class BlockStructure {

	public BlockStructure(AbstractInsnNode[] ain){
		endBlock = false;
		blockInsn = ain;
		numMethodCalls = 0;
		branches = 0;
		branchTest();
	}
	
	private void branchTest(){
		int temp;
		for(int i = 0; i < blockInsn.length; i++){
			temp = blockInsn[i].getType();
			if(!contains(temp)){
				insnTypes.add(temp);
			}
			if(temp == 5 || temp == 7 || temp == 11 || temp == 12){
				branches++;
				if(temp ==5){
					numMethodCalls++;
					methodCalls.add((MethodInsnNode) blockInsn[i]);
				} else if ( temp == 7) {
					nextBranchingBlocks.add(blockInsn[i].getLabelNode());
				} else if (temp == 11) {
					//may need to account for more
					//this includes the default handler block, min key value, and max key value
					nextBranchingBlocks.addAll(blockInsn[i].getLabelNodeSet());
				} else if (temp == 12) {
					//may need to account for more
					//this includes the default handler block and the list of keys
					nextBranchingBlocks.addAll(blockInsn[i].getLabelNodeSet());
				}
			}
			temp = blockInsn[i].getOpcode();
			if(temp <= 177 && temp >= 172){
				endBlock = true;
			}
		}
	}
	
	private boolean contains(int num){
		for(int i = 0; i < insnTypes.size(); i++){
			if(insnTypes.get(i) == num){
				return true;
			}
		}
		return false;
	}

	public boolean isEndBlock(){
		return endBlock;
	}
	
	private int branches;
	private ArrayList<LabelNode> nextBranchingBlocks;
	private int blockNumInsn;
	private AbstractInsnNode[] blockInsn;
	private ArrayList<Integer> insnTypes;
	private int blockID;
	private int numMethodCalls;
	private ArrayList<MethodInsnNode> methodCalls;
	private boolean endBlock;  //indicates whether this block contains a return insn
	
	
}
