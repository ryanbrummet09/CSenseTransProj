package version3;

import java.io.File;
import java.util.ArrayList;

import ASMModifiedSourceCode.*;

public class ClassReader4 {
	
	public static void main(String args[]) throws Exception{
		SourceStructure3 test1 = new SourceStructure3(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/Version3/MethodStructure3.class"));
		ArrayList<MethodStructure3> csMethodStructures;
		ClassStructure3 cs;
		int numBlocks;
		BlockStructure3 abTempBlock;
		ArrayList<BlockStructure3> blocks;
		for(int c = 0; c < test1.getClassStructures().size(); c++){
			cs = test1.getClassStructures().get(c);
			csMethodStructures = cs.getMethodStructures();
			if(c == 0){
				System.out.println(cs.getClassName());
			} else {
				System.out.println("   " + cs.getClassName());
			}
			for(int m = 0; m < csMethodStructures.size(); m++){
				ArrayList<LabelNode> labels = new ArrayList<LabelNode>();
				System.out.println("      " + csMethodStructures.get(m).getMethodName());
				blocks = csMethodStructures.get(m).getBlockStructures();
				numBlocks = csMethodStructures.get(m).getNumUniqueBlocks();
				for(int b = 0; b < numBlocks; b++){
					abTempBlock = blocks.get(b);
					labels.add((LabelNode) abTempBlock.getBlockInsnNumber(0));
					System.out.println("         " + abTempBlock.getBlockInsnNumber(0));
					for(int i = 1; i < abTempBlock.getNumBlockInsn(); i++){
						//System.out.println("            " + abTempBlock.getBlockInsnNumber(i) + "  &&  Type: " +  abTempBlock.getBlockInsnNumber(i).getType() + "  &&  OpCode: " + abTempBlock.getBlockInsnNumber(i).getOpcode());
						if(abTempBlock.getBlockInsnNumber(i).getType() == Constants.ABSTRACT_METHOD_INSN){
							MethodInsnNode methodInsnNode = (MethodInsnNode) abTempBlock.getBlockInsnNumber(i);
						//	System.out.println("           $" + methodInsnNode.owner + " | " + methodInsnNode.name);
						}
					}
					ArrayList<LabelNode> nextBranches = abTempBlock.getNextBranches();
					for(int br = 0; br < nextBranches.size(); br++){
						if(labelExistTest(nextBranches.get(br), csMethodStructures.get(m).getInsnList())){
						//	System.out.print("EXISTS");
						} else {
						//	System.out.print("FAILED");
						}
						//System.out.println("         " + nextBranches.get(br));
					}
				}
				labelUniqueTest(labels);
				int tempInc = 0;
				ArrayList<Integer> loopStartAndStops = csMethodStructures.get(m).getLoopStartAndStops();
				String loopType = new String();
				for(int lss = 0; lss < loopStartAndStops.size(); lss = lss + 3){
					switch(loopStartAndStops.get(lss + 2)){
					case Constants.FOR_LOOP:
						loopType = "FOR LOOP";
						break;
					case Constants.FOR_EACH_LOOP:
						loopType = "FOR EACH LOOP";
						break;
					case Constants.WHILE_LOOP:
						loopType = "WHILE LOOP";
						break;
					case Constants.DO_WHILE_LOOP:
						loopType = "DO WHILE LOOP";
						break;
					}
					System.out.println("      LOOP: " + tempInc + "  " + labels.get(loopStartAndStops.get(lss)) + "  TO  " + labels.get(loopStartAndStops.get(lss + 1)) + " TYPE: " + loopType);
					tempInc++;
				}
				System.out.println("\n");
			}
		}
	}
	
	private static void labelUniqueTest(ArrayList<LabelNode> labels){
		boolean temp = false;
		for(int i = 0; i < labels.size(); i++){
			for(int j = i + 1; j < labels.size(); j++){
				if(labels.get(i) == labels.get(j)){
					temp = true;
				}
			}
		}
		if(temp){
			System.out.println("UNIQUENESS FAILED!!!!!!!");
		} else {
			System.out.println("PASSED");
		}
	}
	
	private static boolean labelExistTest(LabelNode label, InsnList insnList){
		for(int i = 0; i < insnList.size(); i++){
			if(label == insnList.get(i)){
				return true;
			}
		}
		return false;
	}
}
