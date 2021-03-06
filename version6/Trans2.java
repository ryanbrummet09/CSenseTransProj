package version3;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;
import org.objectweb.asm.Type;
import ASMModifiedSourceCode.*;

/**
 * 
 * @author ryanbrummet
 * 1) NOTICE THAT MULTI D ARRAYS ARE NOT ACCOUNTED FOR.
 * 2) MUST NOT USE VARIABLE WITH NAME "className_THIS".  
 * 3) WITH THE EXCEPTION OF THE CONSTRUCTOR, NO METHOD SHOULD ACT ON AN OBJECT OF THE CLASS TO WHICH THE METHOD BELONGS UNLESS THAT OBJECT IS 
 *    "thisSourceObject", or "this" in java.  
 * 4) WE ARE MAKING THE ASSUMPTION THAT ALL PROMELA PROGRAMS WILL START WITH THE INIT METHOD (THERE ARE NOT ACTIVE PROCTYPES; PROCTYPES MUST 
 *    BE STARTED BY INIT).  
 * 5) ALL TRY CATCH STMNTS MUST HAVE A FINALLY CLAUSE (WITHOUT IT, THE END OF THE CATCH PORTION OF THE STMNT CAN NOT BE DETERMINED).  HOWEVER, 
 *    THE FINALLY CLAUSE CANNOT BE UTILIZED BY A USER NOR CAN A VARIABLE BE INSTANTIATED IN THE CLAUSE.
 */

public class Trans2 {

	public static void main(String args[]){
		try {
			SourceStructure3 ss = new SourceStructure3(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/version3/Test2.class"));
			ArrayList<ClassStructure3> cs = ss.getClassStructures();
			uniqueNumber = 0;
			availableMethods = new HashMap<MethodKey, MethodObject>();
			staticVariables = new HashMap<VariableKey, ProcessedVariable>();
			instanceVariables = new HashMap<VariableKey, ProcessedInstanceVariable>();
			standardVariables = new HashMap<VariableKey, ProcessedVariable>();
			objectInitInfo = new HashMap<String, ObjectTypeInfo>();
			staticFields = cs.get(0).getStaticFields();
			boolean clinitExists = false;
			if(cs.get(0).getMethodStructures().get(0).getMethodName().equals("<clinit>")){
				clinitExists = true;
			}
			if(!clinitExists){
				VariableKey variableKey;
				for(FieldInsnNode s: staticFields){
					variableKey = new VariableKey(cs.get(0).getClassName(), s.name);
					staticVariables.put(variableKey, new ProcessedVariable(s.name, s.desc, 0, cs.get(0).getClassName(), 0));
					System.out.println(staticVariables.get(variableKey).getAdjType() + " " + staticVariables.get(variableKey).getAdjName() + ";");
				}
				System.out.println();
				staticFields = new ArrayList<FieldInsnNode>();
			}
			boolean typeDefCreated;
			int numOfMains = 0;
			indent = "";
			for(int c = 0; c < cs.size(); c++){
				typeDefCreated = false;
				instanceFields = cs.get(c).getInstanceFields();
				for(int m = 0; m < cs.get(c).getMethodStructures().size(); m++){
					if(numOfMains > 1){
						throw new NotSupportedException("There can only be one main method");
					}
					if(c == 0 && cs.get(c).getMethodStructures().get(m).getMethodName().equals("<clinit>")){
						printMethod(cs.get(c).getMethodStructures().get(m), true, false, false, typeDefCreated);
					} else if(cs.get(c).getMethodStructures().get(m).getMethodName().equals("<init>")){
						printMethod(cs.get(c).getMethodStructures().get(m), false, true, false, typeDefCreated);
						typeDefCreated = true;
					} else if(c == 0 && cs.get(c).getMethodStructures().get(m).getMethodName().equals("main")){
						printMethod(cs.get(c).getMethodStructures().get(m), false, false, true, typeDefCreated);
						numOfMains++;
					} else {
						printMethod(cs.get(c).getMethodStructures().get(m), false, false, false, typeDefCreated);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printMethod(MethodStructure3 mn, boolean clinit, boolean init, boolean main, boolean typeDefCreated) throws NotSupportedException{
		MethodKey methodKey = new MethodKey(mn.getClassName(), mn.getMethodName(), mn.getMethodDesc());
		availableMethods.put(methodKey, new MethodObject(mn, uniqueNumber));
		uniqueNumber++;
		ArrayList<BlockStructure3> blockStructures = mn.getBlockStructures();
		AbstractInsnNode presentNode;
		stack = new Stack<String>();
		ArrayList<LocalVariableNode> localVariableNodes = mn.getLocalVariables();
		VariableKey variableKeyTemp;
		ProcessedVariable pvTemp;
		boolean isStatic = mn.isStatic();
		int numParams = availableMethods.get(methodKey).getParamTypes().size();
		if(init){
			if(!typeDefCreated){
				objectInitInfo.put(mn.getClassName(), new ObjectTypeInfo(numParams, mn.getClassName() + "ASSIGN" + uniqueNumber, mn.getClassName() + "EQUALITY" + uniqueNumber));
				uniqueNumber++;
				ArrayList<ProcessedInstanceVariable> instanceVariableList = new ArrayList<ProcessedInstanceVariable>();
				for(int i = 0; i < instanceFields.size(); i++){
					ProcessedInstanceVariable pvInstanceTemp = new ProcessedInstanceVariable(mn.getClassName(), instanceFields.get(i).name, instanceFields.get(i).desc);
					instanceVariables.put(new VariableKey(mn.getClassName(), instanceFields.get(i).name), pvInstanceTemp);
					instanceVariableList.add(pvInstanceTemp);
				}
				if(numParams > 0){
					System.out.println("typedef " + Constants.zeroString(mn.getClassName()) + " {");
					indent = "    ";
					for(int i = 0; i < instanceFields.size(); i++){
						variableKeyTemp = new VariableKey(mn.getClassName(), instanceFields.get(i).name);
						System.out.println(indent + instanceVariables.get(variableKeyTemp).getAdjType() + " " + instanceVariables.get(variableKeyTemp).getAdjName() + ";");
					}
					System.out.println("}\n");
				} else {
					System.out.println("typedef " + Constants.zeroString(mn.getClassName()) + " {}\n");
				}
				indent = "";
				System.out.println("inline " + Constants.zeroString(objectInitInfo.get(mn.getClassName()).getAssignMethodName()) + "(" + Constants.zeroString(mn.getClassName()) + "LATEMP, " + Constants.zeroString(mn.getClassName()) + "RATEMP) {");
				indent = "    ";
				for(ProcessedInstanceVariable piv: instanceVariableList){
					if(piv.getAdjType().equals("int") || piv.getAdjType().equals("bool") || piv.getAdjType().equals("short") || piv.getAdjType().equals("byte")){
						System.out.println(indent + Constants.zeroString(mn.getClassName()) + "LATEMP." + piv.getAdjName() + " = " + Constants.zeroString(mn.getClassName()) + "RATEMP." + piv.getAdjName() + ";");
					} else if(objectInitInfo.get(piv.getClassName()) != null){
						System.out.println(objectInitInfo.get(indent + piv.getClassName()).getAssignMethodName() + "(" + mn.getClassName() + "LATEMP." + piv.getAdjName() + ", " + mn.getClassName() + "RATEMP." + piv.getAdjName() + ");");
					} else {
						throw new NotSupportedException("Constructor for type " + piv.getClassName() + " has not been created");
					}
				}
				indent = "";
				System.out.println("}\n");
//NEED TO CREATE EQUALITY INLINE FOR OBJECTS STILL
			}
			variableKeyTemp = new VariableKey(mn.getClassName(), localVariableNodes.get(0).name);
			standardVariablesUpdate(variableKeyTemp, localVariableNodes.get(0).name, localVariableNodes.get(0).desc, mn, 0);
			System.out.print("inline " + availableMethods.get(methodKey).getAdjName() + "(" + standardVariables.get(variableKeyTemp).getAdjName());
			indent = "    ";
			for(int i = 1; i <= numParams; i++){
				variableKeyTemp = new VariableKey(mn.getClassName(), localVariableNodes.get(i).name);
				standardVariablesUpdate(variableKeyTemp, localVariableNodes.get(i).name, localVariableNodes.get(i).desc, mn, 0);
				System.out.print(", " + Constants.zeroString(localVariableNodes.get(i).name + "TEMP"));
			}
			System.out.println(") {");
			for(int i = 1; i <= numParams; i++){
				variableKeyTemp = new VariableKey(mn.getClassName(), localVariableNodes.get(i).name);
				if(standardVariables.get(variableKeyTemp).getAdjType().equals("int") || standardVariables.get(variableKeyTemp).getAdjType().equals("bool") || standardVariables.get(variableKeyTemp).getAdjType().equals("short") || standardVariables.get(variableKeyTemp).getAdjType().equals("byte")){
					System.out.println(indent + standardVariables.get(variableKeyTemp).getAdjType() + " " + standardVariables.get(variableKeyTemp).getAdjName() + " = " + Constants.zeroString(localVariableNodes.get(i).name) + "TEMP;");
				} else {
					System.out.println(indent + Constants.zeroString(objectInitInfo.get(mn.getClassName()).getAssignMethodName()) + "(" + standardVariables.get(variableKeyTemp).getAdjName() + ", " + Constants.zeroString(localVariableNodes.get(i).name) + "TEMP" + ");");
				}
			}
		}else if(main){
			System.out.println("init {");
			indent = "    ";
		}else if(!clinit){
			if(numParams > 0){
				if(isStatic){
					variableKeyTemp = new VariableKey(mn.getClassName(), localVariableNodes.get(0).name);
					standardVariablesUpdate(variableKeyTemp, localVariableNodes.get(0).name, localVariableNodes.get(0).desc, mn, 0);
					System.out.print("inline " + availableMethods.get(methodKey).getAdjName() + "(" + standardVariables.get(variableKeyTemp).getAdjName());
					for(int i = 1; i < numParams; i++){
						variableKeyTemp = new VariableKey(mn.getClassName(), localVariableNodes.get(i).name);
						standardVariablesUpdate(variableKeyTemp, localVariableNodes.get(i).name, localVariableNodes.get(i).desc, mn, 0);
						System.out.print(", " + Constants.zeroString(localVariableNodes.get(i).name + "TEMP"));
					} 
					System.out.println(") {");
					indent = "    ";
					for(int i = 1; i < numParams; i++){
						variableKeyTemp = new VariableKey(mn.getClassName(), localVariableNodes.get(i).name);
						if(standardVariables.get(variableKeyTemp).getAdjType().equals("int") || standardVariables.get(variableKeyTemp).getAdjType().equals("bool") || standardVariables.get(variableKeyTemp).getAdjType().equals("short") || standardVariables.get(variableKeyTemp).getAdjType().equals("byte")){
							System.out.println(indent + standardVariables.get(variableKeyTemp).getAdjType() + " " + standardVariables.get(variableKeyTemp).getAdjName() + " = " + Constants.zeroString(localVariableNodes.get(i).name) + "TEMP;");
						} else {
							System.out.println(indent + Constants.zeroString(objectInitInfo.get(mn.getClassName()).getAssignMethodName()) + "(" + standardVariables.get(variableKeyTemp).getAdjName() + ", " + Constants.zeroString(localVariableNodes.get(i).name) + "TEMP" + ");");
						}
					}
				} else {
					variableKeyTemp = new VariableKey(mn.getClassName(), localVariableNodes.get(0).name);
					standardVariablesUpdate(variableKeyTemp, localVariableNodes.get(0).name, localVariableNodes.get(0).desc, mn, 0);
					System.out.print("inline " + availableMethods.get(methodKey).getAdjName() + "(" + standardVariables.get(variableKeyTemp).getAdjName());
					for(int i = 1; i <= numParams; i++){
						variableKeyTemp = new VariableKey(mn.getClassName(), localVariableNodes.get(i).name);
						standardVariablesUpdate(variableKeyTemp, localVariableNodes.get(i).name, localVariableNodes.get(i).desc, mn, 0);
						System.out.print(", " + Constants.zeroString(localVariableNodes.get(i).name + "TEMP"));
					}
					System.out.println(") {");
					indent = "    ";
					for(int i = 1; i <= numParams; i++){
						variableKeyTemp = new VariableKey(mn.getClassName(), localVariableNodes.get(i).name);
						if(standardVariables.get(variableKeyTemp).getAdjType().equals("int") || standardVariables.get(variableKeyTemp).getAdjType().equals("bool") || standardVariables.get(variableKeyTemp).getAdjType().equals("short") || standardVariables.get(variableKeyTemp).getAdjType().equals("byte")){
							System.out.println(indent + standardVariables.get(variableKeyTemp).getAdjType() + " " + standardVariables.get(variableKeyTemp).getAdjName() + " = " + Constants.zeroString(localVariableNodes.get(i).name) + "TEMP;");
						} else {
							System.out.println(indent + Constants.zeroString(objectInitInfo.get(mn.getClassName()).getAssignMethodName()) + "(" + standardVariables.get(variableKeyTemp).getAdjName() + ", " + Constants.zeroString(localVariableNodes.get(i).name) + "TEMP" + ");");
						}
					}
				}
				indent = "";
			} else {
				System.out.println("inline " + availableMethods.get(methodKey).getAdjName() + "() {");
			}
			indent = "    ";
		}
		for(int i = 0; i < blockStructures.size(); i++){
			for(VariableKey vk: standardVariables.keySet()){
				pvTemp = standardVariables.get(vk);
				if(pvTemp.getEndScope() < i){
					pvTemp.markForAdjustment();
					standardVariables.put(vk, pvTemp);
				}
			}
			for(int j = 0; j < blockStructures.get(i).getNumBlockInsn(); j++){
				presentNode = blockStructures.get(i).getBlockInsnNumber(j);
				if(presentNode.getType() == Constants.ABSTRACT_INSN){
					processZeroOperandInsn(presentNode);
				}else if(presentNode.getType() == Constants.ABSTRACT_INT_INSN){
					processIntInsn(presentNode, i, mn);
				}else if(presentNode.getType() == Constants.ABSTRACT_LDC_INSN){  
					processLDCInsn(presentNode);
				}else if(presentNode.getType() == Constants.ABSTRACT_IINC_INSN){
					processIincInsn(presentNode, i, mn);
				}else if(presentNode.getType() == Constants.ABSTRACT_VAR_INSN){
					processVarInsn(presentNode, i, mn);
				}else if(presentNode.getType() == Constants.ABSTRACT_TYPE_INSN){
					processTypeInsn(presentNode, i, mn);
				}else if(presentNode.getType() == Constants.ABSTRACT_FIELD_INSN){
					processFieldInsn(presentNode, mn, clinit);
				}else if(presentNode.getType() == Constants.ABSTRACT_METHOD_INSN){
					processMethodInsn(presentNode);
				}else if(presentNode.getType() == Constants.ABSTRACT_JUMP_INSN){
					processJumpInsn(presentNode);
				}else if(presentNode.getType() == Constants.ABSTRACT_TABLESWITCH_INSN){
					processTableSwitchInsn(presentNode);
				}else if(presentNode.getType() == Constants.ABSTRACT_LOOKUPSWITCH_INSN){
					processLookUpSwitchInsn(presentNode);
				}
			}
		}
		if(clinit){
			for(int i = 0; i < staticFields.size(); i++){
				VariableKey variableKey = new VariableKey(mn.getClassName(), staticFields.get(i).name);
				staticVariables.put(variableKey, new ProcessedVariable(staticFields.get(i).name, staticFields.get(i).desc, 0, mn.getClassName(), 0));
				System.out.println(staticVariables.get(variableKey).getAdjType() + " " + staticVariables.get(variableKey).getAdjName() + ";");
			}
			System.out.println();
			staticFields = new ArrayList<FieldInsnNode>();
		}
		indent = "";
		if(!clinit){
			System.out.println("}\n");
		}
		for(VariableKey vk: standardVariables.keySet()){
			pvTemp = standardVariables.get(vk);
			pvTemp.markForAdjustment();
			standardVariables.put(vk, pvTemp);
		}
	}
	
	/**
	 * execute a zero operand INSN
	 */
	public static void processZeroOperandInsn(AbstractInsnNode presentNode) throws NotSupportedException{
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
			
		case Constants.OPCODE_IALOAD:
			String i6a = (String) stack.pop();
			String i6b = (String) stack.pop();
			String i6Output = i6b + "[" + i6a + "]";
			stack.push(i6Output);
			break;
			
		case Constants.OPCODE_IASTORE:
			String i7a = (String) stack.pop();
			String i7b = (String) stack.pop();
			String i7c = (String) stack.pop();
			System.out.println(indent + i7c + "[" + i7b + "]" + " = " + i7a + ";");
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
			
		case Constants.OPCODE_ISHL:	
			String i14Shift = (String) stack.pop();
			String i14 = (String) stack.pop();
			String i14Output = "(" + i14 + " << " + i14Shift + ")";
			stack.push(i14Output);
			break;
			
		case Constants.OPCODE_ISHR:
			String i15Shift = (String) stack.pop();
			String i15 = (String) stack.pop();
			String i15Output = "(" + i15 + " >> " + i15Shift + ")";
			stack.push(i15Output);
			break;
			
		case Constants.OPCODE_IUSHR:
			throw new NotSupportedException("IUSHR is not supported");
			
		case Constants.OPCODE_IAND:
			String i16a = (String) stack.pop();
			String i16b = (String) stack.pop();
			String i16Output = "(" + i16b + " & " + i16a + ")";
			stack.push(i16Output);
			break;
			
		case Constants.OPCODE_IOR:		
			String i17a = (String) stack.pop();
			String i17b = (String) stack.pop();
			String i17Output = "(" + i17b + " | " + i17a + ")";
			stack.push(i17Output);
			break; 
			
		case Constants.OPCODE_IXOR:
			String i18a = (String) stack.pop();
			String i18b = (String) stack.pop();
			String i18Output = "(" + i18b + " ^ " + i18a + ")";
			stack.push(i18Output);
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
			break;
			
		case Constants.OPCODE_ACONST_NULL:  //IF THERE ARE PROBLEMS WITH NULL VALUES IT IS PROBABLY HERE
			stack.push("skip");
			break;
			
		case Constants.OPCODE_AALOAD:
			String m2a = (String) stack.pop();
			String m2b = (String) stack.pop();
			String m2Output = m2b + "[" + m2a + "]";
			stack.push(m2Output);
			break;
			
		case Constants.OPCODE_BALOAD:
			String m3a = (String) stack.pop();
			String m3b = (String) stack.pop();
			String m3Output = m3b + "[" + m3a + "]";
			stack.push(m3Output);
			break;
			
		case Constants.OPCODE_CALOAD:
			String m4a = (String) stack.pop();
			String m4b = (String) stack.pop();
			String m4Output = m4b + "[" + m4a + "]";
			stack.push(m4Output);
			break;
			
		case Constants.OPCODE_SALOAD:
			String m5a = (String) stack.pop();
			String m5b = (String) stack.pop();
			String m5Output = m5b + "[" + m5a + "]";
			stack.push(m5Output);
			break;
			
		case Constants.OPCODE_AASTORE:
			String m6a = (String) stack.pop();
			String m6b = (String) stack.pop();
			String m6c = (String) stack.pop();
			System.out.println(indent + m6c + "[" + m6b + "]" + " = " + m6a + ";");
			break;
			
		case Constants.OPCODE_BASTORE:
			String m7a = (String) stack.pop();
			String m7b = (String) stack.pop();
			String m7c = (String) stack.pop();
			System.out.println(indent + m7c + "[" + m7b + "]" + " = " + m7a + ";");
			break;
			
		case Constants.OPCODE_CASTORE:
			String m8a = (String) stack.pop();
			String m8b = (String) stack.pop();
			String m8c = (String) stack.pop();
			System.out.println(indent + m8c + "[" + m8b + "]" + " = " + m8a + ";");
			break;
			
		case Constants.OPCODE_SASTORE:
			String m9a = (String) stack.pop();
			String m9b = (String) stack.pop();
			String m9c = (String) stack.pop();
			System.out.println(indent + m9c + "[" + m9b + "]" + " = " + m9a + ";");
			break;
			
		case Constants.OPCODE_POP:		//MAY BE PROBLEM WITH ANY/ALL DUP* BECUASE OF LOGICAL ERROR OR NOT REFLECTED IN PROMELA
			stack.pop();
			break;
			
		case Constants.OPCODE_POP2:		
			stack.pop();
			stack.pop();
			break;
			
		case Constants.OPCODE_DUP:		
			String m12 = (String) stack.pop();
			stack.push(m12);
			stack.push(m12);
			break;
			
		case Constants.OPCODE_DUP_X1:	
			String m13a = (String) stack.pop();
			String m13b = (String) stack.pop();
			stack.push(m13a);
			stack.push(m13b);
			stack.push(m13a);
			break;
			
		case Constants.OPCODE_DUP_X2:
			String m14a = (String) stack.pop();
			String m14b = (String) stack.pop();
			String m14c = (String) stack.pop();
			stack.push(m14a);
			stack.push(m14c);
			stack.push(m14b);
			stack.push(m14a);
			break;
			
		case Constants.OPCODE_DUP2:
			String m15a = (String) stack.pop();
			String m15b = (String) stack.pop();
			stack.push(m15b);
			stack.push(m15a);
			stack.push(m15b);
			stack.push(m15a);
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
	}
	
	/**
	 * executes an INIT_INSN
	 */
	public static void processIntInsn(AbstractInsnNode presentNode, int blockNumber, MethodStructure3 mn) throws NotSupportedException{
		switch (presentNode.getOpcode()){
		
		case Constants.OPCODE_BIPUSH:
			String iInsn0Output = Integer.toString(((IntInsnNode) presentNode).operand);
			stack.push(iInsn0Output);
			break;
			
		case Constants.OPCODE_SIPUSH:		
			String iInsn1Output = Integer.toString(((IntInsnNode) presentNode).operand);
			stack.push(iInsn1Output);
			break;
			
		case Constants.OPCODE_NEWARRAY:		//HIGH CHANCE IF THERE IS A PROBLEM WITH ARRAYS IT IS HERE
			String a3Size = (String) stack.pop();
			int a3Operand = ((IntInsnNode) presentNode).operand;
			int varAAStoreIndex = ((VarInsnNode) mn.getBlockStructures().get(blockNumber).getBlockInsnNumber(mn.getBlockStructures().get(blockNumber).getNumBlockInsn() - 1)).var;
			VariableKey variableKeyTemp = new VariableKey(mn.getClassName(), mn.getLocalVariables().get(varAAStoreIndex).name);
			standardVariablesUpdate(variableKeyTemp, mn.getLocalVariables().get(varAAStoreIndex).name,mn.getLocalVariables().get(varAAStoreIndex).desc, mn, blockNumber);
			String a3Name = standardVariables.get(variableKeyTemp).getAdjName();
			stack.push(a3Name);
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
	}
	
	/**
	 * executes a LDC_INSN
	 */
	public static void processLDCInsn(AbstractInsnNode presentNode){
		String ldcOutput = String.valueOf(((LdcInsnNode) presentNode).cst);
		stack.push(ldcOutput);
	}
	
	/**
	 * executes an IINC_INSN.  Notice that iinc instructions are only performed on local variables.  Since type variables (aka complex variables)
	 * cannot be incremented by an integer we only need to account for int variables(
	 */
	public static void processIincInsn(AbstractInsnNode presentNode, int blockNumber, MethodStructure3 mn){
		IincInsnNode iincNode = ((IincInsnNode) presentNode);
		VariableKey variableKeyTemp = new VariableKey(mn.getClassName(), mn.getLocalVariables().get(iincNode.var).name);
		standardVariablesUpdate(variableKeyTemp, mn.getLocalVariables().get(iincNode.var).name, mn.getLocalVariables().get(iincNode.var).desc, mn, blockNumber);
		String iincVar = standardVariables.get(variableKeyTemp).getAdjName();
		int iinc = iincNode.incr;
		String iincOutput = "(" + iincVar + " + " + iinc + ")";
		stack.push(iincOutput);
	}
	
	/**
	 * executes a VAR_INSN
	 */
	public static void processVarInsn(AbstractInsnNode presentNode, int blockNumber, MethodStructure3 mn) throws NotSupportedException{
		VariableKey variableKeyTemp = new VariableKey(mn.getClassName(), mn.getLocalVariables().get(((VarInsnNode) presentNode).var).name);       
		switch (presentNode.getOpcode()){
		
		case Constants.OPCODE_ILOAD:
			String iLoad = standardVariables.get(variableKeyTemp).getAdjName();
			stack.push(iLoad);
			break;
			
		case Constants.OPCODE_LLOAD:
			throw new NotSupportedException("LLOAD is not supported");
		case Constants.OPCODE_FLOAD:
			throw new NotSupportedException("FLOAD is not supported");
		case Constants.OPCODE_DLOAD:
			throw new NotSupportedException("DLOAD is not supported");
			
		case Constants.OPCODE_ALOAD:
			String aLoad = standardVariables.get(variableKeyTemp).getAdjName();
			stack.push(aLoad);
			break;
			
		case Constants.OPCODE_ISTORE:
			String varIStore;
			String output = stack.pop();
			if(standardVariables.get(variableKeyTemp) == null || standardVariables.get(variableKeyTemp).adjustmentNeeded()){
				standardVariablesUpdate(variableKeyTemp, mn.getLocalVariables().get(((VarInsnNode) presentNode).var).name, mn.getLocalVariables().get(((VarInsnNode) presentNode).var).desc, mn, blockNumber);
				varIStore = standardVariables.get(variableKeyTemp).getAdjName();
				if(standardVariables.get(variableKeyTemp).getAdjType().equals("bool")){
					if(output.equals("0")){
						System.out.println(indent + standardVariables.get(variableKeyTemp).getAdjType() + " " + varIStore + " = false;");
					}else{
						System.out.println(indent + standardVariables.get(variableKeyTemp).getAdjType() + " " + varIStore + " = true;");
					}
				} else {
					System.out.println(indent + standardVariables.get(variableKeyTemp).getAdjType() + " " + varIStore + " = " + output + ";");
				}
			} else {
				varIStore = standardVariables.get(variableKeyTemp).getAdjName();
				if(standardVariables.get(variableKeyTemp).getAdjType().equals("bool")){
					if(output.equals("0")){
						System.out.println(indent + varIStore + " = false;");
					} else {
						System.out.println(indent + varIStore + " = true;");
					}
				} else {
					System.out.println(indent + varIStore + " = " + output +";");
				}
			}
			break;
			
		case Constants.OPCODE_LSTORE:		
			throw new NotSupportedException("LSTORE is not supported");
		case Constants.OPCODE_FSTORE:
			throw new NotSupportedException("FSTORE is not Supported");
		case Constants.OPCODE_DSTORE:
			throw new NotSupportedException("DSTORE is not Supported");
			
		case Constants.OPCODE_ASTORE:	
			String desc = (mn.getLocalVariables().get(((VarInsnNode) presentNode).var).desc);
			desc = desc.substring(1, desc.length() - 1);
			if(standardVariables.get(variableKeyTemp) == null || standardVariables.get(variableKeyTemp).adjustmentNeeded()){
				standardVariablesUpdate(variableKeyTemp, (mn.getLocalVariables().get(((VarInsnNode) presentNode).var).name), (mn.getLocalVariables().get(((VarInsnNode) presentNode).var).desc), mn, blockNumber);
				System.out.println(indent + standardVariables.get(variableKeyTemp).getAdjType() + " " + standardVariables.get(variableKeyTemp).getAdjName() + ";");
			}
			if(objectInitInfo.get(desc) != null){
				System.out.println(indent + Constants.zeroString(objectInitInfo.get(desc).getAssignMethodName()) + "(" + standardVariables.get(variableKeyTemp).getAdjName() + ", " + stack.pop() + ");");
			} else {
				throw new NotSupportedException("Constructor for type " + standardVariables.get(variableKeyTemp).getRawType() + " has not been created");
			}
			break;
			
		case Constants.OPCODE_RET:		//not handled 10
			break;
		}
	}
	
	/**
	 * executes a TypeInsn
	 */
	public static void processTypeInsn(AbstractInsnNode presentNode, int blockNumber, MethodStructure3 mn){
		switch(presentNode.getOpcode()){
		case Constants.OPCODE_NEW:
			System.out.println(indent + Constants.zeroString(((TypeInsnNode) presentNode).desc) + " " + "TEMP" + uniqueNumber + ";");
			stack.push("TEMP" + uniqueNumber);
			uniqueNumber++;
			break;
		case Constants.OPCODE_A_NEW_ARRAY:		//not handled 1
			break;
		case Constants.OPCODE_CHECKCAST:		//not handled 2
			break;
		case Constants.OPCODE_INSTANCEOF:		//not handled 3
			break;
		}
	}
	
	/**
	 * executes a FIELD_INSN
	 */
	public static void processFieldInsn(AbstractInsnNode presentNode, MethodStructure3 mn, boolean clinit) throws NotSupportedException{
		FieldInsnNode fin = (FieldInsnNode) presentNode;
		VariableKey variableKeyStatic = new VariableKey(mn.getClassName(), fin.name);
		if(clinit){
			boolean notInstantiated;
			if(staticFields.contains(fin)){
				staticFields.remove(fin);
				staticVariables.put(variableKeyStatic, new ProcessedVariable(fin.name, fin.desc, 0, mn.getClassName(), 0));
				notInstantiated = true;
			} else {
				notInstantiated = false;
			}
			switch(presentNode.getOpcode()){
			case Constants.OPCODE_GETSTATIC:
				if(notInstantiated){
					throw new NotSupportedException("Static Variable " + staticVariables.get(variableKeyStatic).getAdjName() + " was placed on stack, but this variable has not been instantiated");
				} else {
					stack.push(staticVariables.get(variableKeyStatic).getAdjName());
				}
				break;
			case Constants.OPCODE_PUTSTATIC:
				String fi1 = (String) stack.pop();
				if(staticVariables.get(variableKeyStatic).getAdjType().equals("bool")){
					if(fi1.equals("0")){ 
						if(notInstantiated){
							System.out.println(indent + staticVariables.get(variableKeyStatic).getAdjType() + " " + staticVariables.get(variableKeyStatic).getAdjName() + " = false;");
						} else {
							System.out.println(indent + staticVariables.get(variableKeyStatic).getAdjName() + " = false;");
						}
					} else {
						if(notInstantiated){
							System.out.println(indent + staticVariables.get(variableKeyStatic).getAdjType() + " " + staticVariables.get(variableKeyStatic).getAdjName() + " = true;");
						} else {
							System.out.println(indent + staticVariables.get(variableKeyStatic).getAdjName() + " = true;");
						}
					}
				} else {
					if(notInstantiated){
						System.out.println(indent + staticVariables.get(variableKeyStatic).getAdjType() + " " + staticVariables.get(variableKeyStatic).getAdjName() + " = " + fi1 + ";");
					} else {
						System.out.println(indent + staticVariables.get(variableKeyStatic).getAdjName() + " = " + fi1 + ";");
					}
				}
				break;
			//field values should not be manipulated at this point since only static values should be handled in clinit
			}
		} else {
			switch(presentNode.getOpcode()){
			case Constants.OPCODE_GETSTATIC:
				stack.push(staticVariables.get(variableKeyStatic).getAdjName());
				break;
			case Constants.OPCODE_PUTSTATIC:
				String fi5 = (String) stack.pop();
				if(staticVariables.get(variableKeyStatic).getAdjType().equals("bool")){
					if(fi5.equals("0")){
						System.out.println(indent + staticVariables.get(variableKeyStatic).getAdjName() + " = false;");
					} else {
						System.out.println(indent + staticVariables.get(variableKeyStatic).getAdjName() + " = true;");
					}
				} else {
					System.out.println(indent + staticVariables.get(variableKeyStatic).getAdjName() + " = " + fi5 + ";");
				}
				break;
			case Constants.OPCODE_GETFIELD:
				String fi6 = (String) stack.pop();
				VariableKey fi6Key = new VariableKey(fin.owner, fin.name);
				stack.push(fi6 + "." + instanceVariables.get(fi6Key).getAdjName());
				break;
			case Constants.OPCODE_PUTFIELD:
				String fi7a = (String) stack.pop();
				String fi7b = (String) stack.pop();
				VariableKey fi7Key = new VariableKey(fin.owner, fin.name);
				if(instanceVariables.get(fi7Key).getAdjType().equals("bool")){
					if(fi7a.equals("0")){
						System.out.println(indent + fi7b + "." + instanceVariables.get(fi7Key).getAdjName() + " = false;");
					} else {
						System.out.println(indent + fi7b + "." + instanceVariables.get(fi7Key).getAdjName() + " = true;");
					}
				} else {
					System.out.println(indent + fi7b + "." + instanceVariables.get(fi7Key).getAdjName() + " = " + fi7a + ";");
				}
				break;
			}
		} 
	}
	
	/**
	 * executes a METHOD_INSN
	 */
	public static void processMethodInsn(AbstractInsnNode presentNode){
		
	}
	
	/**
	 * executes a JUMP_INSN
	 */
	public static void processJumpInsn(AbstractInsnNode presentNode){
		
	}
	
	/**
	 * executes a TABLESWITCH_INSN
	 */
	public static void processTableSwitchInsn(AbstractInsnNode presentNode){
		
	}
	
	/**
	 * executes a LOOKUPSWITCH_INSN
	 */
	public static void processLookUpSwitchInsn(AbstractInsnNode presentNode){
		
	}
	
	public static void standardVariablesUpdate(VariableKey variableKey, String rawName, String rawType, MethodStructure3 mn, int startIndex) {
		if(standardVariables.get(variableKey) == null){
			standardVariables.put(variableKey, new ProcessedVariable(rawName, rawType, 0, mn, startIndex, false, 0));
		} else {
			ProcessedVariable pvTemp = standardVariables.get(variableKey);
			if(pvTemp.adjustmentNeeded()){
				pvTemp.reAdjust(rawType, mn, startIndex, 0);
				standardVariables.put(variableKey,pvTemp);
			} else {
				try {
					throw new NotSupportedException("Key is mapped but adjustment not needed");
				} catch (NotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static int uniqueNumber;
	private static String indent;
	private static HashMap<MethodKey, MethodObject> availableMethods;  //methods that have been created in promela
	private static HashMap<VariableKey, ProcessedVariable> staticVariables;  //self explanatory 
	private static HashMap<VariableKey, ProcessedVariable> standardVariables;  //variables that have been instantiated inside methods
	private static HashMap<VariableKey, ProcessedInstanceVariable> instanceVariables;  //field variables of available objects
	private static HashMap<String, ObjectTypeInfo> objectInitInfo;
	private static Stack<String> stack;
	private static ArrayList<FieldInsnNode> staticFields;
	private static ArrayList<FieldInsnNode> instanceFields;
}
