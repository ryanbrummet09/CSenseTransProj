package version3;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import org.objectweb.asm.Type;
import ASMModifiedSourceCode.*;

/**
 * 
 * @author ryanbrummet
 * NOTICE THAT MULTI D ARRAYS ARE NOT ACCOUNTED FOR.  NOTICE THAT ALL VARIABLE NAMES MUST BE UNIQUE!!!!.  NOTICE THAT VARIABLE NAMES MUST NOT HAVE NAMES
 * OF THE FORM "className_MethodName_Number".  MUST NOT USE VARIABLE WITH NAME "className_THIS".  WITH THE EXCEPTION OF THE CONSTRUCTOR, NO METHOD
 * SHOULD ACT ON AN OBJECT OF THE CLASS TO WHICH THE METHOD BELONGS UNLESS THAT OBJECT IS "thisSourceObject", or "this" in java.  WE ARE MAKING THE
 * ASSUMPTION THAT ALL PROMELA PROGRAMS WILL START WITH THE INIT METHOD (THERE ARE NOT ACTIVE PROCTYPES; PROCTYPES MUST BE STARTED BY INIT).
 */

public class firstGoTrans {
    
	public static void main(String args[]) throws Exception{
		SourceStructure3 ss = new SourceStructure3(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/version3/Test2.class"));
		ArrayList<ClassStructure3> cs = ss.getClassStructures();
		thisSourceType = cs.get(0).getClassName();		//the type of objects that this source instantiates
		thisSourceObject = zeroString(thisSourceType) + "THIS";		//the object associated with a particular source
		sourceTypes = new HashMap<String, String>();		//a hashmap of the types that exist in this source and the standardized versions of these types
		createdMethods = new HashMap<String, Integer>();		//a hashmap of the methods that have been created and the number of params each has
		createdConstructors = new HashMap<String, Integer>();		//a hashmap of the constructors in the promela translation and the number of params each has
		sourceTypes.put("I", "int");
		sourceTypes.put(thisSourceType, zeroString(thisSourceType));
		unAccountedForStaticFields = cs.get(0).getStaticFields();
		boolean clinitExists = false;
		for(int b = 0; b < cs.get(0).getMethodStructures().size(); b++){
			if(cs.get(0).getMethodStructures().get(b).getMethodName().equals("<clinit>")){
				clinitExists = true;
			}
		}
		if(!clinitExists){
			for(FieldInsnNode s: unAccountedForStaticFields){
				System.out.println(getAndAdjustSourceTypes(zeroString(s.desc)) + " " + zeroString(s.name) + ";\n");
			}
		}
		for(int c = 0; c < cs.size(); c++){
			unAccountedForInstanceFields = cs.get(c).getInstanceFields();
			for(int m = 0; m < cs.get(c).getMethodStructures().size(); m++){
				//System.exit(0);
				if(c == 0 && cs.get(c).getMethodStructures().get(m).getMethodName().equals("<clinit>")){
					printMethod(cs.get(c).getMethodStructures().get(m), cs.get(c), true, false, false);
				} else if(cs.get(c).getMethodStructures().get(m).getMethodName().equals("<init>")){
					createdConstructors.put(zeroString(cs.get(c).getClassName()), getNumMethodParams(cs.get(c).getMethodStructures().get(m)));
					printMethod(cs.get(c).getMethodStructures().get(m), cs.get(c), false, true, false);
				} else if(c == 0 && cs.get(c).getMethodStructures().get(m).getMethodName().equals("main")){
					printMethod(cs.get(c).getMethodStructures().get(m), cs.get(c), false, false, true);
				} else {
					printMethod(cs.get(c).getMethodStructures().get(m), cs.get(c), false, false, false);
				}
			}
		}
	}


	public static void printMethod(MethodStructure3 mn, ClassStructure3 cs, boolean clinit, boolean init, boolean main) throws NotSupportedException{
		ArrayList<BlockStructure3> blockStructures = mn.getBlockStructures();
		AbstractInsnNode presentNode;
		int numParams = getNumMethodParams(mn);		//the numberof params of a method
		Stack<String> stack = new Stack<String>();		//a representation of what is on the execuation stack at any particular instance of a method execuation
		ArrayList<Integer> createdVars = new ArrayList<Integer>();		//a list of the variables that have been instantiated at any particular instance of a method execution
		ArrayList<String> orderedMethodParamTypes = new ArrayList<String>();		//an ordered list of the types of the params associated with this MethodStructure
		ArrayList<LocalVariableNode> localVariableNodes = mn.getLocalVariables();		//ArrayList of LocalVariableNodes associated with this MethodStructure
		ArrayList<String> originalLocalVariableNodeNames = new ArrayList<String>();		//the original names of localvariables before being changed
		ArrayList<String> typeVariables = new ArrayList<String>();    //the list of attributes of a typedef include constructor params and fields
		HashMap<String, String[]> nonPrimitiveInitParams = new HashMap<String, String[]>();		//a hashmap of object names and the params needed to construct each
		boolean isStatic = false;
		/*if(!(getAndAdjustSourceTypes(localVariableNodes.get(0).desc).equals(getAndAdjustSourceTypes(cs.getClassName())))){
			isStatic = true;
		}*/
		for(int i = 0; i < localVariableNodes.size(); i++){
			originalLocalVariableNodeNames.add(localVariableNodes.get(i).name);
		}
		if(!init){
			createdMethods.put(mn.getMethodName(), numParams);
		}
		if(init){
			if(numParams > 0){
				System.out.println("typedef " + zeroString(mn.getClassName()) + " {");
				indent = "    ";
				for(int i = 1; i <= numParams; i++){
					System.out.println(indent + getAndAdjustSourceTypes(localVariableNodes.get(i).desc) + " " + zeroString(localVariableNodes.get(i).name) + ";");
					typeVariables.add(zeroString(localVariableNodes.get(i).name));
				}
				for(int i = 0; i < unAccountedForInstanceFields.size(); i++){
					if(!typeVariables.contains(unAccountedForInstanceFields.get(i).name)){
						System.out.println(indent + getAndAdjustSourceTypes(unAccountedForInstanceFields.get(i).desc) + " " + zeroString(unAccountedForInstanceFields.get(i).name)+ ";" );
					}
				}
				System.out.println("}\n");
			} else {
				System.out.println("typedef " + zeroString(mn.getClassName()) + " {}\n");
			}
			indent = "";
			System.out.print("inline " + zeroString(mn.getClassName()) + "Init(" + thisSourceObject);
			indent = "    ";
			for(int i = 1; i <= numParams; i++){
				System.out.print(", " + zeroString(localVariableNodes.get(i).name + "temp"));
				createdVars.add(i);
			}
			System.out.println(") {");
			for(int i = 1; i <= numParams; i++){
				System.out.println(indent + getAndAdjustSourceTypes(localVariableNodes.get(i).desc) + " " + zeroString(cs.getClassName() + mn.getMethodName() + i) + " = " + localVariableNodes.get(i).name + "temp;");
				localVariableNodes.get(i).name = zeroString(cs.getClassName() + mn.getMethodName() + i);
			}
			createdConstructors.put(zeroString(mn.getClassName()) + "Init", numParams);
		} else if(main){
			System.out.println("init {");
			indent = "    ";
		} else if(!clinit){
			if(numParams > 0){
				if(isStatic){
					System.out.print("inline " + zeroString(mn.getMethodName()) + "(" + zeroString(localVariableNodes.get(1).name));
					createdVars.add(1);
					orderedMethodParamTypes.add(getAndAdjustSourceTypes(localVariableNodes.get(1).desc));
					for(int i = 2; i <= numParams; i++){
						System.out.print(", " + zeroString(localVariableNodes.get(i).name));
						createdVars.add(i);
						orderedMethodParamTypes.add(getAndAdjustSourceTypes(localVariableNodes.get(i).desc));
					}
				} else {
					System.out.print("inline " + zeroString(mn.getMethodName()) + "("+ thisSourceObject);
					for(int i = 1; i <= numParams; i++){
						System.out.print(", " + zeroString(localVariableNodes.get(i).name));
						createdVars.add(i);
						orderedMethodParamTypes.add(getAndAdjustSourceTypes(localVariableNodes.get(i).desc));
					}
				}
				System.out.println(") {");
			} else {
				System.out.println("inline " + zeroString(mn.getMethodName()) + "() {");
			}
			indent = "    ";
		}
		for(int i = 0; i < blockStructures.size(); i++){
			for(int j = 0; j < blockStructures.get(i).getNumBlockInsn(); j++){
				int objectsOnStackFromBlock = 0;
				presentNode = blockStructures.get(i).getBlockInsnNumber(j);
				if(presentNode.getType() == Constants.ABSTRACT_INSN){
					switch (presentNode.getOpcode()){
					
					//Integers
					case Constants.OPCODE_ICONST_0:
						stack.push("0");
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_ICONST_1:
						stack.push("1");
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_ICONST_2:
						stack.push("2");
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_ICONST_3:
						stack.push("3");
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_ICONST_4:
						stack.push("4");
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_ICONST_5:
						stack.push("5");
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_IALOAD:
						String i6a = (String) stack.pop();
						String i6b = (String) stack.pop();
						String i6Output = i6b + "[" + i6a + "]";
						stack.push(i6Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_IASTORE:
						String i7a = (String) stack.pop();
						String i7b = (String) stack.pop();
						String i7c = (String) stack.pop();
						System.out.println(indent + i7c + "[" + i7b + "]" + " = " + i7a + ";");
						objectsOnStackFromBlock = objectsOnStackFromBlock - 3;
						break;
						
					case Constants.OPCODE_IADD:
						String i8a = (String) stack.pop();
						String i8b = (String) stack.pop();
						String i8Output = "(" + i8b + " + " + i8a + ")";
						stack.push(i8Output);
						objectsOnStackFromBlock--;
						break;	
						
					case Constants.OPCODE_ISUB:
						String i9a = (String) stack.pop();
						String i9b = (String) stack.pop();
						String i9Output = "(" + i9b + " - " + i9a + ")";
						stack.push(i9Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_IMUL:
						String i10a = (String) stack.pop();
						String i10b = (String) stack.pop();
						String i10Output = "(" + i10b + " * " + i10a + ")";
						stack.push(i10Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_IDIV:
						String i11a = (String) stack.pop();
						String i11b = (String) stack.pop();
						String i11Output = "(" + i11b + " / " + i11a + ")";
						stack.push(i11Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_IREM:
						String i12a = (String) stack.pop();
						String i12b = (String) stack.pop();
						String i12Output = "(" + i12b + " % " + i12a + ")";
						stack.push(i12Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_INEG:
						String i13 = (String) stack.pop();
						String i13Output = "(-" + i13 + ")";
						stack.push(i13Output);
						break;
						
					case Constants.OPCODE_ISHL:	
						String i14Shift = (String) stack.pop();
						String i14 = (String) stack.pop();
						String i14Output = "(" + i14 + " << " + i14Shift + ")";
						stack.push(i14Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_ISHR:
						String i15Shift = (String) stack.pop();
						String i15 = (String) stack.pop();
						String i15Output = "(" + i15 + " >> " + i15Shift + ")";
						stack.push(i15Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_IUSHR:
						throw new NotSupportedException("IUSHR is not supported");
						
					case Constants.OPCODE_IAND:
						String i16a = (String) stack.pop();
						String i16b = (String) stack.pop();
						String i16Output = "(" + i16b + " & " + i16a + ")";
						stack.push(i16Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_IOR:		
						String i17a = (String) stack.pop();
						String i17b = (String) stack.pop();
						String i17Output = "(" + i17b + " | " + i17a + ")";
						stack.push(i17Output);
						objectsOnStackFromBlock--;
						break; 
						
					case Constants.OPCODE_IXOR:
						String i18a = (String) stack.pop();
						String i18b = (String) stack.pop();
						String i18Output = "(" + i18b + " ^ " + i18a + ")";
						stack.push(i18Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_I2L:		
						throw new NotSupportedException("I2L is not supported");
					case Constants.OPCODE_I2F:		
						throw new NotSupportedException("I2F is not supported");
					case Constants.OPCODE_I2D:		
						throw new NotSupportedException("I2D is not supported");
						
					case Constants.OPCODE_I2B:		//not handled 23
						break;
					case Constants.OPCODE_I2C:		//not handled 24
						break;
					case Constants.OPCODE_I2S:		//not handled 25
						break;
					case Constants.OPCODE_IRETURN:		//not handled 26
						break;
						
					//Floats
					case Constants.OPCODE_FCONST_0:		
						throw new NotSupportedException("FCONST_0 is not supported");
					case Constants.OPCODE_FCONST_1:		
						throw new NotSupportedException("FCONST_1 is not supported");
					case Constants.OPCODE_FCONST_2:
						throw new NotSupportedException("FCONST_2 is not supported");
					case Constants.OPCODE_FALOAD:		
						throw new NotSupportedException("FALOAD is not supported");		
					case Constants.OPCODE_FASTORE:		
						throw new NotSupportedException("FASTORE is not supported");
					case Constants.OPCODE_FADD:		
						throw new NotSupportedException("FADD is not supported");
					case Constants.OPCODE_FSUB:		
						throw new NotSupportedException("FSUB is not supported");
					case Constants.OPCODE_FMUL:		
						throw new NotSupportedException("FMUL is not supported");
					case Constants.OPCODE_FDIV:		
						throw new NotSupportedException("FDIV is not supported");
					case Constants.OPCODE_FREM:		
						throw new NotSupportedException("FREM is not supported");
					case Constants.OPCODE_FNEG:		
						throw new NotSupportedException("FNEG is not supported");
					case Constants.OPCODE_F2I:		
						throw new NotSupportedException("F2I is not supported");
					case Constants.OPCODE_F2L:		
						throw new NotSupportedException("F2L is not supported");
					case Constants.OPCODE_F2D:		
						throw new NotSupportedException("F2D is not supported");
					case Constants.OPCODE_FCMPL:		
						throw new NotSupportedException("FCMPL is not supported");
					case Constants.OPCODE_FCMPG:		
						throw new NotSupportedException("FCMPG is not supported");
					case Constants.OPCODE_FRETURN:		
						throw new NotSupportedException("FRETURN is not supported");
						
					//Longs
					case Constants.OPCODE_LCONST_0:		
						throw new NotSupportedException("LCONST_0 is not supported");
					case Constants.OPCODE_LCONST_1:		
						throw new NotSupportedException("LCONST_1 is not supported");
					case Constants.OPCODE_LALOAD:		
						throw new NotSupportedException("LALOAD is not supported");
					case Constants.OPCODE_LASTORE:		
						throw new NotSupportedException("LASTORE is not supported");
					case Constants.OPCODE_LADD:		
						throw new NotSupportedException("LADD is not supported");
					case Constants.OPCODE_LSUB:		
						throw new NotSupportedException("LSUB is not supported");
					case Constants.OPCODE_LMUL:		
						throw new NotSupportedException("LMUL is not supported");
					case Constants.OPCODE_LDIV:		
						throw new NotSupportedException("LDIV is not supported");
					case Constants.OPCODE_LREM:		
						throw new NotSupportedException("LREM is not supported");
					case Constants.OPCODE_LNEG:		
						throw new NotSupportedException("LNEG is not supported");
					case Constants.OPCODE_LSHL:		
						throw new NotSupportedException("LSHL is not supported");
					case Constants.OPCODE_LSHR:		
						throw new NotSupportedException("LSHR is not supported");
					case Constants.OPCODE_LUSHR:		
						throw new NotSupportedException("LUSHR is not supported");
					case Constants.OPCODE_LAND:		
						throw new NotSupportedException("LAND is not supported");
					case Constants.OPCODE_LOR:		
						throw new NotSupportedException("LOR is not supported");
					case Constants.OPCODE_LXOR:		
						throw new NotSupportedException("LXOR is not supported");
					case Constants.OPCODE_L2I:		
						throw new NotSupportedException("L2I is not supported");
					case Constants.OPCODE_L2F:		
						throw new NotSupportedException("L2F is not supported");
					case Constants.OPCODE_L2D:		
						throw new NotSupportedException("L2D is not supported");
					case Constants.OPCODE_LCMP:		
						throw new NotSupportedException("LCMP is not supported");
					case Constants.OPCODE_LRETURN:		
						throw new NotSupportedException("LRETURN is not supported");
						
					//Doubles
					case Constants.OPCODE_DCONST_0:		
						throw new NotSupportedException("DCONST_0 is not supported");
					case Constants.OPCODE_DCONST_1:		
						throw new NotSupportedException("DCONST_1 is not supported");
					case Constants.OPCODE_DALOAD:		
						throw new NotSupportedException("DALOAD is not supported");
					case Constants.OPCODE_DASTORE:		
						throw new NotSupportedException("DASTORE is not supported");
					case Constants.OPCODE_DADD:		
						throw new NotSupportedException("DADD is not supported");
					case Constants.OPCODE_DSUB:		
						throw new NotSupportedException("DSUB is not supported");
					case Constants.OPCODE_DMUL:		
						throw new NotSupportedException("DMUL is not supported");
					case Constants.OPCODE_DDIV:		
						throw new NotSupportedException("DDIV is not supported");
					case Constants.OPCODE_DREM:		
						throw new NotSupportedException("DREM is not supported");
					case Constants.OPCODE_DNEG:		
						throw new NotSupportedException("DNEG is not supported");
					case Constants.OPCODE_D2I:		
						throw new NotSupportedException("D2I is not supported");
					case Constants.OPCODE_D2L:		
						throw new NotSupportedException("D2L is not supported");
					case Constants.OPCODE_D2F:		
						throw new NotSupportedException("D2F is not supported");
					case Constants.OPCODE_DCMPL:		
						throw new NotSupportedException("DCMPL is not supported");
					case Constants.OPCODE_DCMPG:		
						throw new NotSupportedException("DCMPG is not supported");
					case Constants.OPCODE_DRETURN:		
						throw new NotSupportedException("DRETURN is not supported");
						 
					//Misc
					case Constants.OPCODE_NOP:			//THIS MAY CAUSE A PROBLEM
						stack.pop();
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_ACONST_NULL:  //IF THERE ARE PROBLEMS WITH NULL VALUES IT IS PROBABLY HERE
						stack.push("skip");
						break;
						
					case Constants.OPCODE_AALOAD:
						String m2a = (String) stack.pop();
						String m2b = (String) stack.pop();
						String m2Output = m2b + "[" + m2a + "]";
						stack.push(m2Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_BALOAD:
						String m3a = (String) stack.pop();
						String m3b = (String) stack.pop();
						String m3Output = m3b + "[" + m3a + "]";
						stack.push(m3Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_CALOAD:
						String m4a = (String) stack.pop();
						String m4b = (String) stack.pop();
						String m4Output = m4b + "[" + m4a + "]";
						stack.push(m4Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_SALOAD:
						String m5a = (String) stack.pop();
						String m5b = (String) stack.pop();
						String m5Output = m5b + "[" + m5a + "]";
						stack.push(m5Output);
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_AASTORE:
						String m6a = (String) stack.pop();
						String m6b = (String) stack.pop();
						String m6c = (String) stack.pop();
						System.out.println(indent + m6c + "[" + m6b + "]" + " = " + m6a + ";");
						objectsOnStackFromBlock = objectsOnStackFromBlock - 3;
						break;
						
					case Constants.OPCODE_BASTORE:
						String m7a = (String) stack.pop();
						String m7b = (String) stack.pop();
						String m7c = (String) stack.pop();
						System.out.println(indent + m7c + "[" + m7b + "]" + " = " + m7a + ";");
						objectsOnStackFromBlock = objectsOnStackFromBlock - 3;
						break;
						
					case Constants.OPCODE_CASTORE:
						String m8a = (String) stack.pop();
						String m8b = (String) stack.pop();
						String m8c = (String) stack.pop();
						System.out.println(indent + m8c + "[" + m8b + "]" + " = " + m8a + ";");
						objectsOnStackFromBlock = objectsOnStackFromBlock - 3;
						break;
						
					case Constants.OPCODE_SASTORE:
						String m9a = (String) stack.pop();
						String m9b = (String) stack.pop();
						String m9c = (String) stack.pop();
						System.out.println(indent + m9c + "[" + m9b + "]" + " = " + m9a + ";");
						objectsOnStackFromBlock = objectsOnStackFromBlock - 3;
						break;
						
					case Constants.OPCODE_POP:		//MAY BE PROBLEM WITH ANY/ALL DUP* BECUASE OF LOGICAL ERROR OR NOT REFLECTED IN PROMELA
						stack.pop();
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_POP2:		
						stack.pop();
						stack.pop();
						objectsOnStackFromBlock = objectsOnStackFromBlock - 2;
						break;
						
					case Constants.OPCODE_DUP:		
						String m12 = (String) stack.pop();
						stack.push(m12);
						stack.push(m12);
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_DUP_X1:	
						String m13a = (String) stack.pop();
						String m13b = (String) stack.pop();
						stack.push(m13a);
						stack.push(m13b);
						stack.push(m13a);
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_DUP_X2:
						String m14a = (String) stack.pop();
						String m14b = (String) stack.pop();
						String m14c = (String) stack.pop();
						stack.push(m14a);
						stack.push(m14c);
						stack.push(m14b);
						stack.push(m14a);
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_DUP2:
						String m15a = (String) stack.pop();
						String m15b = (String) stack.pop();
						stack.push(m15b);
						stack.push(m15a);
						stack.push(m15b);
						stack.push(m15a);
						objectsOnStackFromBlock = objectsOnStackFromBlock + 2;
						break;
						
					case Constants.OPCODE_DUP2_X1:
						String m16a = (String) stack.pop();
						String m16b = (String) stack.pop();
						String m16c = (String) stack.pop();
						stack.push(m16b);
						stack.push(m16a);
						stack.push(m16c);
						stack.push(m16b);
						stack.push(m16a);
						objectsOnStackFromBlock = objectsOnStackFromBlock + 2;
						break;
						
					case Constants.OPCODE_DUP2_X2:
						String m17a = (String) stack.pop();
						String m17b = (String) stack.pop();
						String m17c = (String) stack.pop();
						String m17d = (String) stack.pop();
						stack.push(m17b);
						stack.push(m17a);
						stack.push(m17d);
						stack.push(m17c);
						stack.push(m17b);
						stack.push(m17a);
						objectsOnStackFromBlock = objectsOnStackFromBlock + 2;
						break;
						
					case Constants.OPCODE_SWAP:
						String m18a = (String) stack.pop();
						String m18b = (String) stack.pop();
						stack.push(m18a);
						stack.push(m18b);
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
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_SIPUSH:		
						String iInsn1Output = Integer.toString(((IntInsnNode) presentNode).operand);
						stack.push(iInsn1Output);
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_NEWARRAY:		//HIGH CHANCE IF THERE IS A PROBLEM WITH ARRAYS IT IS HERE
						String a3Size = (String) stack.pop();
						int a3Operand = ((IntInsnNode) presentNode).operand;
						int varAAStoreIndex = ((VarInsnNode) blockStructures.get(i).getBlockInsnNumber(blockStructures.get(i).getNumBlockInsn() - 1)).var;
						String a3Name = zeroString(localVariableNodes.get(varAAStoreIndex).name);
						stack.push(a3Name);
						objectsOnStackFromBlock++;
						switch (a3Operand){
							
						case Constants.ARRAY_BOOLEAN:
							System.out.println(indent + "bool " + a3Name + "[" + a3Size + "];" );
							break;
						case Constants.ARRAY_CHAR:		//DOES NOT APPEAR TO SUPPORT, BUT COULD BE WRONG  WILL LEAVE BLANK FOR NOW
							break;
						case Constants.ARRAY_BYTE:
							System.out.println(indent + "byte " + a3Name + "[" + a3Size + "];" );
							break;
						case Constants.ARRAY_SHORT:
							System.out.println(indent + "short " + a3Name + "[" + a3Size + "];" );
							break;
						case Constants.ARRAY_INT:
							System.out.println(indent + "int " + a3Name + "[" + a3Size + "];" );
							break;
							
						case Constants.ARRAY_FLOAT:
							throw new NotSupportedException("Arrays of Type float are not supported");
						case Constants.ARRAY_LONG:
							throw new NotSupportedException("Arrays of Type Long are not supported");
						case Constants.ARRAY_DOUBLE:
							throw new NotSupportedException("Arrays of Type Double are not supported");
						}
						
						break;
					}
				}else if(presentNode.getType() == Constants.ABSTRACT_LDC_INSN){                      //PROBLEM MAY BE HERE IF THERE IS ONE
					String ldcOutput = String.valueOf(((LdcInsnNode) presentNode).cst);
					stack.push(ldcOutput);
					objectsOnStackFromBlock++;
				}else if(presentNode.getType() == Constants.ABSTRACT_IINC_INSN){
					IincInsnNode iincNode = ((IincInsnNode) presentNode);
					String iincVar = localVariableNodes.get(iincNode.var).name;
					int iinc = iincNode.incr;
					String iincOutput = "(" + iincVar + " + " + iinc + ")";
					stack.push(iincOutput);
					objectsOnStackFromBlock++;
				}else if(presentNode.getType() == Constants.ABSTRACT_VAR_INSN){
					switch (presentNode.getOpcode()){
					
					case Constants.OPCODE_ILOAD:
						String iLoad = localVariableNodes.get(((VarInsnNode) presentNode).var).name;
						stack.push(iLoad);
						objectsOnStackFromBlock++;
						break;
						
					case Constants.OPCODE_LLOAD:
						throw new NotSupportedException("LLOAD is not supported");
					case Constants.OPCODE_FLOAD:
						throw new NotSupportedException("FLOAD is not supported");
					case Constants.OPCODE_DLOAD:
						throw new NotSupportedException("DLOAD is not supported");
						
					case Constants.OPCODE_ALOAD:
						String aLoad = localVariableNodes.get(((VarInsnNode) presentNode).var).name;
						stack.push(aLoad);
						objectsOnStackFromBlock++;
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
						objectsOnStackFromBlock--;
						break;
						
					case Constants.OPCODE_LSTORE:		
						throw new NotSupportedException("LSTORE is not supported");
					case Constants.OPCODE_FSTORE:
						throw new NotSupportedException("FSTORE is not Supported");
					case Constants.OPCODE_DSTORE:
						throw new NotSupportedException("DSTORE is not Supported");
						
					case Constants.OPCODE_ASTORE:	
						int varAStoreIndex = ((VarInsnNode) presentNode).var;
						String v9name = localVariableNodes.get(varAStoreIndex).name;
						if(createdVars.contains(varAStoreIndex)){
							System.out.println(indent + v9name + " = " + stack.pop() + ";");
							objectsOnStackFromBlock--;
						} else {
							createdVars.add(varAStoreIndex);
							String[] typeParams = nonPrimitiveInitParams.get(zeroString(localVariableNodes.get(varAStoreIndex).desc.substring(1)));
							System.out.println(indent + getAndAdjustSourceTypes((localVariableNodes.get(varAStoreIndex)).desc) + " " + v9name + ";");
							System.out.print(indent + getAndAdjustSourceTypes((localVariableNodes.get(varAStoreIndex)).desc) + "Init(" + v9name);
							for(int k = typeParams.length - 1; k >= 0; k--){
								System.out.print(", " + typeParams[k]);
							}
							System.out.println(");");
						}
						break;
						
					case Constants.OPCODE_RET:		//not handled 10
						break;
					}
				} else if(presentNode.getType() == Constants.ABSTRACT_TYPE_INSN){
					switch(presentNode.getOpcode()){
					case Constants.OPCODE_NEW:
						stack.push(getAndAdjustSourceTypes(((TypeInsnNode) presentNode).desc));
						objectsOnStackFromBlock++;
						break;
					case Constants.OPCODE_A_NEW_ARRAY:		//not handled 1
						break;
					case Constants.OPCODE_CHECKCAST:		//not handled 2
						break;
					case Constants.OPCODE_INSTANCEOF:		//not handled 3
						break;
					}
				} else if(presentNode.getType() == Constants.ABSTRACT_FIELD_INSN){
					if(clinit){
						switch(presentNode.getOpcode()){
						case Constants.OPCODE_GETSTATIC:
							stack.push(zeroString(((FieldInsnNode) presentNode).name));
							objectsOnStackFromBlock++;
							break;
						case Constants.OPCODE_PUTSTATIC:
							String fi1 = (String) stack.pop();
							objectsOnStackFromBlock--;
							if(unAccountedForStaticFields.contains(((FieldInsnNode) presentNode).name)){
								unAccountedForStaticFields.remove(unAccountedForStaticFields.indexOf(((FieldInsnNode) presentNode).name));
								System.out.println(getAndAdjustSourceTypes(zeroString(((FieldInsnNode) presentNode).desc)) + " " + zeroString(((FieldInsnNode) presentNode).name) + " = " + fi1 + ";");
							}
							break;
						case Constants.OPCODE_GETFIELD:		//most likely will cause trouble since at the time of running clinit, fields should not exist
							stack.push(zeroString(((FieldInsnNode) presentNode).name));
							objectsOnStackFromBlock++;
							break;
						case Constants.OPCODE_PUTFIELD:		//Doesn't need to be considered since the clinit shouldn't deal with putfield (init does that)
							break;
						}
					} else if(init) {
						switch(presentNode.getOpcode()){
						case Constants.OPCODE_GETSTATIC:
							stack.push(zeroString(((FieldInsnNode) presentNode).name));
							objectsOnStackFromBlock++;
							break;
						case Constants.OPCODE_PUTSTATIC:
							String fi5 = (String) stack.pop();
							objectsOnStackFromBlock--;
							System.out.println(indent + zeroString(((FieldInsnNode) presentNode).name) + " = " + fi5 + ";");
							break;
						case Constants.OPCODE_GETFIELD:
							stack.push(zeroString(((FieldInsnNode) presentNode).name));
							objectsOnStackFromBlock++;
							break;
						case Constants.OPCODE_PUTFIELD:
							String fi7 = (String) stack.pop();
							objectsOnStackFromBlock--;
							if(unAccountedForInstanceFields.contains(((FieldInsnNode) presentNode))){
								unAccountedForInstanceFields.remove(unAccountedForInstanceFields.indexOf(((FieldInsnNode) presentNode)));
								System.out.println(indent + thisSourceObject + "." + zeroString(((FieldInsnNode) presentNode).name) + " = " + fi7 + ";");
							}
							break;
						}
					} else {
						switch(presentNode.getOpcode()){
						case Constants.OPCODE_GETSTATIC:
							stack.push(zeroString(((FieldInsnNode) presentNode).name));
							objectsOnStackFromBlock++;
							break;
						case Constants.OPCODE_PUTSTATIC:
							String fi9 = (String) stack.pop();
							objectsOnStackFromBlock--;
							System.out.println(indent + zeroString(((FieldInsnNode) presentNode).name) + " = " + fi9 + ";");
							break;
						case Constants.OPCODE_GETFIELD:
							stack.push(zeroString(((FieldInsnNode) presentNode).name));
							objectsOnStackFromBlock++;
							break;
						case Constants.OPCODE_PUTFIELD:
							String fi11 = (String) stack.pop();
							objectsOnStackFromBlock--;
							System.out.println(indent + zeroString(((FieldInsnNode) presentNode).name) + " = " + fi11 + ";");
							break;
						}
					}
				} else if(presentNode.getType() == Constants.ABSTRACT_METHOD_INSN){
					switch(presentNode.getOpcode()){
					case Constants.OPCODE_INVOKE_VIRTUAL:		//not handled 0
						break;
					case Constants.OPCODE_INVOKE_SPECIAL:  //inheritance will cause an error to be thrown in this code segment
						String specialMethodName = ((MethodInsnNode) presentNode).owner;
						if(((MethodInsnNode) presentNode).name.equals("<init>") && !((MethodInsnNode) presentNode).owner.equals("java/lang/Object")){
							if(createdConstructors.get(zeroString(specialMethodName)) != null){
								int specialMethodNumParams = createdConstructors.get(zeroString(specialMethodName));
								String[] nonPrimitiveTypeParams = new String[specialMethodNumParams];
								for(int k = 0; k < specialMethodNumParams; k++){
									nonPrimitiveTypeParams[k] = stack.pop();
								}
								stack.clear();
								objectsOnStackFromBlock = 0;
								nonPrimitiveInitParams.put(zeroString(specialMethodName), nonPrimitiveTypeParams);
							} else {
								throw new NotSupportedException("Constructor for " + specialMethodName + " has not been instantiated");
							}
						} //else if a private method
						
						break;
					case Constants.OPCODE_INVOKE_STATIC:		//not handled 2
						break;
					case Constants.OPCODE_INVOKE_INTERFACE:		//not handled 3
						break;
					}
				} else if(presentNode.getType() == Constants.ABSTRACT_JUMP_INSN){    //may not need JumpInsnNode because of prep; we will skip for now
					
				} else if(presentNode.getType() == Constants.ABSTRACT_TABLESWITCH_INSN){
					
				} else if(presentNode.getType() == Constants.ABSTRACT_LOOKUPSWITCH_INSN){
					
				}
			}			
		}	
		if(init){
			for(int i = 1; i <= numParams; i++){
				System.out.println(indent + thisSourceObject + "." + zeroString(originalLocalVariableNodeNames.get(i)) + " = " + zeroString(localVariableNodes.get(i).name) + ";");
			}
			System.out.println("}");
		} else if(clinit){
			for(FieldInsnNode s: unAccountedForStaticFields){
				System.out.println(getAndAdjustSourceTypes(zeroString(s.desc)) + " " + zeroString(s.name) + ";");
				System.out.println(s.desc);
			}
		} else {
			System.out.println("}");
		}
		System.out.println();
	}
	
	
	/**
	 * 
	 * @param rawString
	 * @return takes a string and returns the same string with all non alphabet or non numbers removed
	 */
	public static String zeroString(String rawString){
		return rawString.replaceAll("[^a-zA-Z0-9]", "");
	}
	
	/**
	 * 
	 * @param m
	 * @return takes a MethodStructures and calculates and returns the number parameters the associated method has
	 */
	public static int getNumMethodParams(MethodStructure3 m){
		String raw = Arrays.toString(Type.getArgumentTypes(m.getMethodNode().desc));
		return (Arrays.asList(raw.split(",[ ]*"))).size();
	}
	
	public static String getAndAdjustSourceTypes(String input){
		if(sourceTypes.get(input) == null){
			String temp = zeroString(input);
			sourceTypes.put(input, temp.substring(1));
			return sourceTypes.get(input);
		} else {
			return sourceTypes.get(input);
		}
	}
	
	private static HashMap<String, Integer> createdConstructors;
	private static HashMap<String, Integer> createdMethods;
	private static String indent;
	public static HashMap<String, String> sourceTypes;
	private static String thisSourceType;
	private static String thisSourceObject;
	private static ArrayList<FieldInsnNode> unAccountedForStaticFields;
	private static ArrayList<FieldInsnNode> unAccountedForInstanceFields;
}
