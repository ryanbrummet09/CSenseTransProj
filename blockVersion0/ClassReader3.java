import java.io.File;
import java.util.ArrayList;

import ASMModifiedSourceCode.*;

public class ClassReader3 {

	public static void main(String args[]) throws Exception{
		SourceStructure2 test1 = new SourceStructure2(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/ASMTestGroup2/InvaderGameVersion2.class"));
		ArrayList<MethodStructure2> csMethodStructures; 
		
		//checks that the structure is correct
		ClassStructure2 cs;
		int numBlocks;
		BlockStructure2 abTempBlock;
		ArrayList<String> tempStringBlock;
		ArrayList<BlockStructure2> blocks;
		ArrayList<Integer> blockIndexes;
		for(int s = 0; s < test1.getClassStructures().size(); s++){
			cs = test1.getClassStructures().get(s);
			csMethodStructures = cs.getMethodStructures();
			if(s == 0){
				System.out.println(cs.getClassName());
			} else {
				System.out.println("   " + cs.getClassName());
			}
			for(int i = 0; i < csMethodStructures.size(); i++){
				System.out.println("      " + csMethodStructures.get(i).getMethodName());
				blocks = csMethodStructures.get(i).getBlockStructures();
				numBlocks = csMethodStructures.get(i).getNumUniqueBlocks();
				for(int j = 0; j < numBlocks; j++){
					abTempBlock = blocks.get(j);
					blockIndexes = blocks.get(j).getBranchIndexes();
					int index = 0;
					for(int br = 0; br < blocks.get(j).getNumBlockInsn(); br++){
						if(index == 0){
							System.out.println("         " + csMethodStructures.get(i).getIndvInsnToString(blocks.get(j).getStartIndex()));
							index++;
						} else {
							System.out.println("            " + csMethodStructures.get(i).getIndvInsnToString(blocks.get(j).getStartIndex() + br));
						}
						
						/*
						System.out.println(blocks.get(j).getBranchingBlocks().get(br).getLabelNode());
						System.out.println(blockIndexes.get(br));
						System.out.println(csMethodStructures.get(i).getIndvInsn(blockIndexes.get(br)));
						*/
					}
					for(int mk = 0; mk < blocks.get(j).getBranchIndexes().size(); mk++){
						System.out.println("@@@@@@@@@@@@@@@" + csMethodStructures.get(i).getIndvInsnToString(abTempBlock.getBranchIndexes().get(mk)));
					}
				}
			}
		}
		
	}
	
}
