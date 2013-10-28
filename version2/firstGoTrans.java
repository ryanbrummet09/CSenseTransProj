package version3;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

import ASMModifiedSourceCode.*;

public class firstGoTrans {

	public static void main(String args[]) throws Exception{
		SourceStructure3 ss = new SourceStructure3(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/version3/Test2.class"));
		ArrayList<ClassStructure3> cs = ss.getClassStructures();
		for(int c = 0; c < cs.size(); c++){
			ClassStructure3 currentClass = cs.get(c);
			System.out.println("typedef " + currentClass.getClassName() + " {\n\n}");
			ArrayList<MethodStructure3> methodStructures = currentClass.getMethodStructures();
			indent = "     ";
			for(int m = 0; m < methodStructures.size(); m++){
				if(methodStructures.get(m).getMethodName().equals("main")){
					System.out.println("\ninit " + "{");
					printMethod(methodStructures.get(m));
					System.out.println("}");
				}
			}
		}
		
	}

	public static void printMethod(MethodStructure3 mn){
		ArrayList<BlockStructure3> blockStructures = mn.getBlockStructures();
		//System.out.println("proctype" + mn.getMethodName() + " {\n");
		AbstractInsnNode presentNode;
		Stack<String> stack = new Stack<String>();
		ArrayList<LocalVariableNode> localVariableNodes = mn.getLocalVariables();
		ArrayList<Integer> createdVars = new ArrayList<Integer>();
		for(int i = 0; i < blockStructures.size(); i++){
			String output = null;
			int temp = 0;
			for(int j = 0; j < blockStructures.get(i).getNumBlockInsn(); j++){
				presentNode = blockStructures.get(i).getBlockInsnNumber(j);
				temp++;
				if(presentNode.getType() == Constants.ABSTRACT_INSN){
					switch (presentNode.getOpcode()){
					
					//Integers
					case Constants.OPCODE_ICONST_0:
						stack.push("0");
						break;
					case Constants.OPCODE_ICONST_1:
						stack.push("1");
						break;
					case Constants.OPCODE_ICONST_2:
						stack.push("2");
						break;
					case Constants.OPCODE_ICONST_3:
						stack.push("3");
						break;
					case Constants.OPCODE_ICONST_4:
						stack.push("4");
						break;
					case Constants.OPCODE_ICONST_5:
						stack.push("5");
						break;
					case Constants.OPCODE_IALOAD:		//not handled 6
						break;
					case Constants.OPCODE_IASTORE:		//not handled 7
						break;
						
					case Constants.OPCODE_IADD:
						String i8a = (String) stack.pop();
						String i8b = (String) stack.pop();
						String i8Output = "(" + i8b + " + " + i8a + ")";
						stack.push(i8Output);
						break;	
						
					case Constants.OPCODE_ISUB:
						String i9a = (String) stack.pop();
						String i9b = (String) stack.pop();
						String i9Output = "(" + i9b + " - " + i9a + ")";
						stack.push(i9Output);
						break;
						
					case Constants.OPCODE_IMUL:
						String i10a = (String) stack.pop();
						String i10b = (String) stack.pop();
						String i10Output = "(" + i10b + " * " + i10a + ")";
						stack.push(i10Output);
						break;
						
					case Constants.OPCODE_IDIV:
						String i11a = (String) stack.pop();
						String i11b = (String) stack.pop();
						String i11Output = "(" + i11b + " / " + i11a + ")";
						stack.push(i11Output);
						break;
						
					case Constants.OPCODE_IREM:
						String i12a = (String) stack.pop();
						String i12b = (String) stack.pop();
						String i12Output = "(" + i12b + " % " + i12a + ")";
						stack.push(i12Output);
						break;
						
					case Constants.OPCODE_INEG:
						String i13 = (String) stack.pop();
						String i13Output = "(-" + i13 + ")";
						stack.push(i13Output);
						break;
						
					case Constants.OPCODE_ISHL:		//not handled 14
						break;
					case Constants.OPCODE_ISHR:		//not handled 15
						break;
					case Constants.OPCODE_IUSHR:		//not handled 16
						break;
					case Constants.OPCODE_IAND:		//not handled 17
						break;
					case Constants.OPCODE_IOR:		//not handled 18
						break; 
					case Constants.OPCODE_IXOR:		//not handled 19
						break;
					case Constants.OPCODE_I2L:		//not handled 20
						break;
					case Constants.OPCODE_I2F:		//not handled 21
						break;
					case Constants.OPCODE_I2D:		//not handled 22
						break;
					case Constants.OPCODE_I2B:		//not handled 23
						break;
					case Constants.OPCODE_I2C:		//not handled 24
						break;
					case Constants.OPCODE_I2S:		//not handled 25
						break;
					case Constants.OPCODE_IRETURN:		//not handled 26
						break;
						
					//Floats
					case Constants.OPCODE_FCONST_0:		//not handled 0
						break;
					case Constants.OPCODE_FCONST_1:		//not handled 1
						break;
					case Constants.OPCODE_FCONST_2:		//not handled 2
						break;
					case Constants.OPCODE_FALOAD:		//not handled 3
						break;		
					case Constants.OPCODE_FASTORE:		//not handled 4
						break;
					case Constants.OPCODE_FADD:		//not handled 5
						break;
					case Constants.OPCODE_FSUB:		//not handled 6
						break;
					case Constants.OPCODE_FMUL:		//not handled 7
						break;
					case Constants.OPCODE_FDIV:		//not handled 8
						break;
					case Constants.OPCODE_FREM:		//not handled 9
						break;
					case Constants.OPCODE_FNEG:		//not handled 10
						break;
					case Constants.OPCODE_F2I:		//not handled 11
						break;
					case Constants.OPCODE_F2L:		//not handled 12
						break;
					case Constants.OPCODE_F2D:		//not handled 13
						break;
					case Constants.OPCODE_FCMPL:		//not handled 14
						break;
					case Constants.OPCODE_FCMPG:		//not handled 15
						break;
					case Constants.OPCODE_FRETURN:		//not handled 16
						break;
						
					//Longs
					case Constants.OPCODE_LCONST_0:		//not handled 0
						break;
					case Constants.OPCODE_LCONST_1:		//not handled 1
						break;
					case Constants.OPCODE_LALOAD:		//not handled 2
						break;
					case Constants.OPCODE_LASTORE:		//not handled 3
						break;
					case Constants.OPCODE_LADD:		//not handled 4
						break;
					case Constants.OPCODE_LSUB:		//not handled 5
						break;
					case Constants.OPCODE_LMUL:		//not handled 6
						break;
					case Constants.OPCODE_LDIV:		//not handled 7
						break;
					case Constants.OPCODE_LREM:		//not handled 8
						break;
					case Constants.OPCODE_LNEG:		//not handled 9
						break;
					case Constants.OPCODE_LSHL:		//not handled 10
						break;
					case Constants.OPCODE_LSHR:		//not handled 11
						break;
					case Constants.OPCODE_LUSHR:		//not handled 12
						break;
					case Constants.OPCODE_LAND:		//not handled 13
						break;
					case Constants.OPCODE_LOR:		//not handled 14
						break;
					case Constants.OPCODE_LXOR:		//not handled 15
						break;
					case Constants.OPCODE_L2I:		//not handled 16
						break;
					case Constants.OPCODE_L2F:		//not handled 17
						break;
					case Constants.OPCODE_L2D:		//not handled 18
						break;
					case Constants.OPCODE_LCMP:		//not handled 19
						break;
					case Constants.OPCODE_LRETURN:		//not handled 20
						break;
						
					//Doubles
					case Constants.OPCODE_DCONST_0:		//not handled 0
						break;
					case Constants.OPCODE_DCONST_1:		//not handled 1
						break;
					case Constants.OPCODE_DALOAD:		//not handled 2
						break;
					case Constants.OPCODE_DASTORE:		//not handled 3
						break;
					case Constants.OPCODE_DADD:		//not handled 4
						break;
					case Constants.OPCODE_DSUB:		//not handled 5
						break;
					case Constants.OPCODE_DMUL:		//not handled 6
						break;
					case Constants.OPCODE_DDIV:		//not handled 7
						break;
					case Constants.OPCODE_DREM:		//not handled 8
						break;
					case Constants.OPCODE_DNEG:		//not handled 9
						break;
					case Constants.OPCODE_D2I:		//not handled 10
						break;
					case Constants.OPCODE_D2L:		//not handled 11
						break;
					case Constants.OPCODE_D2F:		//not handled 12
						break;
					case Constants.OPCODE_DCMPL:		//not handled 13
						break;
					case Constants.OPCODE_DCMPG:		//not handled 14
						break;
					case Constants.OPCODE_DRETURN:		//not handled 15
						break;
						 
					//Misc
					case Constants.OPCODE_NOP:		//not handled 0
						break;
					case Constants.OPCODE_ACONST_NULL:		//not handled 1
						break;
					case Constants.OPCODE_AALOAD:		//not handled 2
						break;
					case Constants.OPCODE_BALOAD:		//not handled 3
						break;
					case Constants.OPCODE_CALOAD:		//not handled 4
						break;
					case Constants.OPCODE_SALOAD:		//not handled 5
						break;
					case Constants.OPCODE_AASTORE:		//not handled 6
						break;
					case Constants.OPCODE_BASTORE:		//not handled 7
						break;
					case Constants.OPCODE_CASTORE:		//not handled 8
						break;
					case Constants.OPCODE_SASTORE:		//not handled 9
						break;
					case Constants.OPCODE_POP:		//not handled 10
						break;
					case Constants.OPCODE_POP2:		//not handled 11
						break;
					case Constants.OPCODE_DUP:		//not handled 12
						break;
					case Constants.OPCODE_DUP_X1:		//not handled 13
						break;
					case Constants.OPCODE_DUP_X2:		//not handled 14
						break;
					case Constants.OPCODE_DUP2:		//not handled 15
						break;
					case Constants.OPCODE_DUP2_X1:		//not handled 16
						break;
					case Constants.OPCODE_DUP2_X2:		//not handled 17
						break;
					case Constants.OPCODE_SWAP:		//not handled 18
						break;
					case Constants.OPCODE_ARETURN:		//not handled 19
						break;
					case Constants.OPCODE_RETURN:		//not handled 20
						break;
					case Constants.OPCODE_ARRAYLENGTH:		//not handled 21
						break;
					case Constants.OPCODE_ATHROW:		//not handled 22
						break;
					case Constants.OPCODE_MONITORENTER:		//not handled 23
						break;
					case Constants.OPCODE_MONITOREXIT:		//not handled 24
						break;
					}
						
				}else if(presentNode.getType() == Constants.ABSTRACT_INT_INSN){
					switch (presentNode.getOpcode()){
					case Constants.OPCODE_BIPUSH:
						String iInsn0Output = Integer.toString(((IntInsnNode) presentNode).operand);
						stack.push(iInsn0Output);
						break;
					case Constants.OPCODE_SIPUSH:		
						String iInsn1Output = Integer.toString(((IntInsnNode) presentNode).operand);
						stack.push(iInsn1Output);
						break;
					case Constants.OPCODE_NEWARRAY:		//not handled 2
						break;
					}
				}else if(presentNode.getType() == Constants.ABSTRACT_LDC_INSN){                      //PROBLEM MAY BE HERE IF THERE IS ONE
					String ldcOutput = String.valueOf(((LdcInsnNode) presentNode).cst);
					stack.push(ldcOutput);
				}else if(presentNode.getType() == Constants.ABSTRACT_IINC_INSN){
					IincInsnNode iincNode = ((IincInsnNode) presentNode);
					String iincVar = localVariableNodes.get(iincNode.var).name;
					int iinc = iincNode.incr;
					String iincOutput = "(" + iincVar + " + " + iinc + ")";
					stack.push(iincOutput);
				}else if(presentNode.getType() == Constants.ABSTRACT_VAR_INSN){
					switch (presentNode.getOpcode()){
					case Constants.OPCODE_ILOAD:
						String iLoad = localVariableNodes.get(((VarInsnNode) presentNode).var).name;
						stack.push(iLoad);
						break;
					case Constants.OPCODE_LLOAD:		//not handled 1
						break;
					case Constants.OPCODE_FLOAD:		//not handled 2
						break;
					case Constants.OPCODE_DLOAD:		//not handled 3
						break;
					case Constants.OPCODE_ALOAD:		//not handled 4
						break;
					case Constants.OPCODE_ISTORE:
						int varIStoreIndex = ((VarInsnNode) presentNode).var;
						String varIStore = localVariableNodes.get(varIStoreIndex).name;
						if(createdVars.contains(varIStoreIndex)){
							System.out.println(indent + varIStore + " = " + stack.pop() +";");
						} else {
							createdVars.add(varIStoreIndex);
							System.out.println(indent + "int " + varIStore + " = " + stack.pop() + ";");
						}
						
						output = null;
						break;
					case Constants.OPCODE_LSTORE:		//not handled 6
						break;
					case Constants.OPCODE_FSTORE:		//not handled 7
						break;
					case Constants.OPCODE_DSTORE:		//not handled 8
						break;
					case Constants.OPCODE_ASTORE:		//not handled 9
						break;
					case Constants.OPCODE_RET:		//not handled 10
						break;
					}
				}
			}			
		}	
	}
	private static String indent;
}
