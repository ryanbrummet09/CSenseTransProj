
import java.io.File;
import java.util.ArrayList;

import ASMModifiedSourceCode.*;

public class ClassReader2 {

	public static void main(String args[]) throws Exception{
		
		SourceStructure2 test1 = new SourceStructure2(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/Test1.class"));
		ArrayList<MethodStructure2> csMethodStructures; 
		
		//checks that the structure is correct
		ClassStructure2 cs;
		int numBlocks;
		BlockStructure abTempBlock;
		ArrayList<String> tempStringBlock;
		ArrayList<BlockStructure> blocks;
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
					tempStringBlock = blocks.get(j).getBlockAsStrings();
					for(int b = 0; b < abTempBlock.getNumBlockInsn(); b++){
						if(b == 0){
							System.out.println("\n         " + tempStringBlock.get(b) + "  &&  Type: " + abTempBlock.getBlockInsn().get(b).getType() + "  &&  Opcode: " + abTempBlock.getBlockInsn().get(b).getOpcode());
						} else {
							System.out.println("            " + tempStringBlock.get(b) + "  &&  Type: " + abTempBlock.getBlockInsn().get(b).getType() + "  &&  Opcode: " + abTempBlock.getBlockInsn().get(b).getOpcode());
						}
						if(testContents(insnTypesPresent, abTempBlock.getBlockInsn().get(b).getType())){
							insnTypesPresent.add(abTempBlock.getBlockInsn().get(b).getType());
						}
					}
				}
			}
		}
		sortIntArray();
		System.out.print("\nTypes Present:  ");
		for(int i = 0; i < insnTypesPresent.size(); i++){
			System.out.print(insnTypesPresent.get(i) + "  ");
		}
		System.out.print("\nTypes Absent:  ");
		for(int i = 0; i < 16; i++){
			if(testContents(insnTypesPresent, i)){
				System.out.print(i + "  ");
			}
		}
	}
	
	private static boolean testContents(ArrayList<Integer> al, int type){
		for(int i = 0; i < al.size(); i++){
			if(al.get(i) == type){
				return false;
			}
		}
		return true;
	}
	
	private static void sortIntArray(){
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int i = 0; i < 16; i++){
			if(insnTypesPresent.indexOf(i) != -1){
				temp.add(i);
			}
		}
		insnTypesPresent = temp;
	}
	
	private static ArrayList<Integer> insnTypesPresent = new ArrayList<Integer>();
}
