import java.io.*;
import java.util.*;

public class ClassReaderTest1 {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception{
		
		//these two lines collect data and build data structure
		SourceStructure test1 = new SourceStructure(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/Test1.class"));
		ArrayList<MethodStructure> csMethodStructures;
		
		//checks that the structure is correct
		int index =0;
		ClassStructure cs;
		
		for(int s = 0; s < test1.getClassStructures().size(); s++){
			cs = test1.getClassStructures().get(s);
			csMethodStructures = cs.getMethodStructures();
			String[][] insn;
			index = 0;
			if(s == 0){
				System.out.println(cs.getClassName());
			} else {
				System.out.println("   " + cs.getClassName());
			}
			for(int i = 0; i < csMethodStructures.size(); i++){
				System.out.println("      " + csMethodStructures.get(i).getMethodName());
				insn = csMethodStructures.get(i).getMethodInsnSet();
				for(int j = 0; j < csMethodStructures.get(i).getNumMethodLines(); j++){
					index = 0;
					System.out.println("         L" + j);
					while(!insn[j][index].equals("-1")){
						System.out.println("            " + insn[j][index]);
						index++;
					}
				}
			}
		}
	}
}
