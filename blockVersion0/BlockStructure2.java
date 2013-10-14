import ASMModifiedSourceCode.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

public class BlockStructure2 {

	public BlockStructure2(int start, int end, InsnList list){
		
		this.startIndex = start;
		this.endIndex = end;
		this.insnList = list;
		
		methodCalls = new ArrayList<MethodInsnNode>();
		endBlock = false;
		insnTypes = new ArrayList<Integer>();
		nextBranchingBlocks = new ArrayList<LabelNode>();
		branches = 0;
		int temp = 0;
		branchIndexes = new ArrayList<Integer>();
		numMethodCalls = 0;
		for(int i = startIndex; i < endIndex; i++){
			temp = list.get(i).getType();
			if(!contains(temp)){
				insnTypes.add(temp);
			}
			if(temp == 5 || temp == 7 || temp == 11 || temp == 12){
				if(temp ==5){
					numMethodCalls++;
					methodCalls.add((MethodInsnNode) insnList.get(i));
				} else if ( temp == 7) {
					branches++;
					nextBranchingBlocks.add(insnList.get(i).getLabelNode());
					branchIndexes.add(i);
				} else if (temp == 11) {
					//may need to account for more
					//this includes the default handler block, min key value, and max key value
					branches += insnList.get(i).getLabelNodeSet().size();
					nextBranchingBlocks.addAll(insnList.get(i).getLabelNodeSet());
					branchIndexes.add(i);
				} else if (temp == 12) {
					//may need to account for more
					//this includes the default handler block and the list of keys
					branches += insnList.get(i).getLabelNodeSet().size();
					nextBranchingBlocks.addAll(insnList.get(i).getLabelNodeSet());
					branchIndexes.add(i);
				}
				
			}
			temp = insnList.get(i).getOpcode();
			if(temp <= 177 && temp >= 172){
				endBlock = true;
			}
		}
		System.out.println(branches + "  ***  " + nextBranchingBlocks.size());
		/*
		branchIndexes = new ArrayList<Integer>();
		for(LabelNode ln: nextBranchingBlocks){
			for(int i = 0; i < list.size(); i++){
				if(list.get(i).equals(ln)){
					branchIndexes.add(i);
				}
			}
		}
		*/
	}
	
	private boolean contains(int num){
		for(int i = 0; i < insnTypes.size(); i++){
			if(insnTypes.get(i) == num){
				return true;
			}
		}
		return false;
	}
	
	private String insnToString(AbstractInsnNode insn){
		insn.accept(tmv);
		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();
		return sw.toString().trim();
	} 
	
	public ArrayList<String> blockToString(){
		ArrayList<String> temp = new ArrayList<String>();
		for(int i = startIndex; i < endIndex; i++){
			temp.add(insnToString(insnList.get(i)));
		}
		return temp;
	}
	
	public boolean isEndBlock(){
		return endBlock;
	}
	
	public int getBlockId(){
		return startIndex;
	}
	
	public int getNumBlockInsn(){
		return endIndex - startIndex;
	}
	
	public ArrayList<LabelNode> getBranchingBlocks(){
		return nextBranchingBlocks;
	}
	
	public ArrayList<Integer> getBranchIndexes(){
		return branchIndexes;
	}
	
	public int getNumBranches(){
		return branches;
	}
	
	public int getStartIndex(){
		return startIndex;
	}
	
	public int getEndIndex(){
		return endIndex;
	}
	
	//used to convert block to strings
	private Printer printer = new Textifier();
	private TraceMethodVisitor tmv = new TraceMethodVisitor(printer);
	
	private int startIndex;
	private int endIndex;
	private InsnList insnList;
	private boolean endBlock;
	private ArrayList<Integer> insnTypes;
	private int branches;
	private ArrayList<LabelNode> nextBranchingBlocks;
	private int numMethodCalls;
	private ArrayList<MethodInsnNode> methodCalls;
	private ArrayList<Integer> branchIndexes;
}
