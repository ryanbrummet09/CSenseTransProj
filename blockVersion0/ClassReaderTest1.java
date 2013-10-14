import java.io.*;
import java.util.*;

import ASMModifiedSourceCode.*;

public class ClassReaderTest1 {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception{
		
		//these two lines collect data and build data structure
		SourceStructure test1 = new SourceStructure(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/Test1.class"));
		ArrayList<MethodStructure> csMethodStructures;
		
		//checks that the structure is correct
		int index =0;
		ClassStructure cs;
		String[][] insn;
		String[] line;
		AbstractInsnNode[] abLine;
		AbstractInsnNode[][] abInsn;
		for(int s = 0; s < test1.getClassStructures().size(); s++){
			cs = test1.getClassStructures().get(s);
			csMethodStructures = cs.getMethodStructures();
			index = 0;
			if(s == 0){
				System.out.println(cs.getClassName());
			} else {
				System.out.println("   " + cs.getClassName());
			}
			for(int i = 0; i < csMethodStructures.size(); i++){
				System.out.println("      " + csMethodStructures.get(i).getMethodName());
				insn = csMethodStructures.get(i).getMethodLineInsnSetAsStrings();
				line = csMethodStructures.get(i).getMethodLineSetAsStrings();
				abInsn = csMethodStructures.get(i).getMethodInsnSet();
				abLine = csMethodStructures.get(i).getMethodLineSet();
				for(int j = 0; j < csMethodStructures.get(i).getNumMethodLines(); j++){
					index = 0;
					System.out.println("\n         " + line[j] + "  &&  Type: " + abLine[j].getType() + "  &&  Opcode: " + abLine[j].getOpcode() + "  &&  INFO: " + abLine[j]);
					//System.out.println("         L" + j);
					while(!(insn[j][index] == null)){
						if(testContents(insnTypesPresent, abInsn[j][index].getType())){
							insnTypesPresent.add(abInsn[j][index].getType());
						}
						System.out.println("            " + insn[j][index] + "  &&  Type: " + abInsn[j][index].getType() + "  &&  Opcode: " + abInsn[j][index].getOpcode());
						index++;
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
