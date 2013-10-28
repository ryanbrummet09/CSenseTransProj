package version3;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.objectweb.asm.util.*;

import ASMModifiedSourceCode.*;

public class ClassReader4 {
	
	public static void main(String args[]) throws Exception{
		SourceStructure3 test1 = new SourceStructure3(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/version3/Test1.class"));
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
					//System.out.println("         " + abTempBlock.getMethodBlockNumber());
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
				ArrayList<Integer> loopStartAndStops = csMethodStructures.get(m).getLoopBlockIntervalsAndTypes();
				String loopType = new String();
				for(int lss = 0; lss < loopStartAndStops.size(); lss = lss + 3){
					switch(loopStartAndStops.get(lss + 2)){
					case Constants.FOR_EACH_LOOP:
						loopType = "FOR EACH LOOP";
						break;
					case Constants.DO_WHILE_LOOP:
						loopType = "DO WHILE LOOP";
						break;
					case Constants.FIRST_RUN_CONDITIONAL_LOOP:
						loopType = "FIRST_RUN_CONDITIONAL_LOOP";
						break;
					}
					System.out.println("       LOOP " + tempInc + ":  [" + blocks.get(loopStartAndStops.get(lss)).getMethodBlockNumber() + "," + blocks.get(loopStartAndStops.get(lss + 1)).getMethodBlockNumber() + "] TYPE: " + loopType);
					tempInc++;
				}
				tempInc = 0;
				/*ArrayList<Integer> continueBlockIndexes = csMethodStructures.get(m).getContinueBlockIndexes();
				for(int cbi = 0; cbi < continueBlockIndexes.size(); cbi++){
					System.out.println("   CONTINUE " + tempInc + ": " + blocks.get(continueBlockIndexes.get(cbi)).getMethodBlockNumber());
					tempInc++;
				}*/
				ArrayList<Integer> switchAndCasesBlockIndexes = csMethodStructures.get(m).getSwitchAndCasesBlockIndexes();
				int switchIndex = 0;
				int size = 1;;
				tempInc = 0;
				for(int i = 0; i < switchAndCasesBlockIndexes.size(); i = switchIndex){
					System.out.print("     SWITCH " + tempInc + ", SIZE " + switchAndCasesBlockIndexes.get(i) + ": ");
					while(size <= switchAndCasesBlockIndexes.get(i)){
						System.out.print(blocks.get(switchAndCasesBlockIndexes.get(i + size)).getMethodBlockNumber() + " ");
						size++;
					}
					switchIndex = switchIndex + switchAndCasesBlockIndexes.get(i) + 1;
					System.out.println();
					tempInc++;
					size = 1;
				}
				tempInc = 0;
				ArrayList<Integer> breakBlockIndexes = csMethodStructures.get(m).getBreakBlockIndexes();
				for(int i = 0; i < breakBlockIndexes.size(); i++){
					System.out.println("      BREAK " + tempInc + ": " + breakBlockIndexes.get(i));
					tempInc++;
				}
				tempInc = 0;
				ArrayList<Integer> dudeBlockIndexes = csMethodStructures.get(m).getDudBlockIndexes();
				for(int i = 0; i < dudeBlockIndexes.size(); i++){
					System.out.println("        DUD " + tempInc + ": " + dudeBlockIndexes.get(i));
					tempInc++;
				}
				
				
				
				System.out.print("      TYPES: ");
				for(Integer g: csMethodStructures.get(m).getASMTypes()){
					System.out.print(g + " ");
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
			System.out.println("      UNIQUENESS FAILED!!!!!!!");
		} else {
			System.out.println("      PASSED");
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
	
	private static String insnToString(AbstractInsnNode insn){
        insn.accept(traceMethodVisitor);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
	}
	private static Printer printer = new Textifier();
    private static TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(printer); 
}
