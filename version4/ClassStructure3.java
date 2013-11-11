/**
 * @author ryanbrummet
 */

package version3;

/*
 * We are assuming that any input .class file will not have 
 * any attributes.  We are also assuming that only .class files
 * will be passed to the constructor for this class.
 */

//DOES NOT CURRENTLY SUPPORT ANNOTATIONS,  POLYMORPHISM MAY CREATE
//		ERRORS

//SUPPORTS FIELDS, METHODS, CLASS NAME, INTERFACES
//		SOURCE FILE IF APPLICABLE, SUPER CLASS NAME
//		AND ONE LEVEL OF NESTED INNER CLASSES

import java.io.*;
import java.util.*;
import org.objectweb.asm.ClassReader;
import ASMModifiedSourceCode.*;

public class ClassStructure3 {

	/**
	 * create a ClassStructure using a ClassNode2 object
	 * @param targetClass
	 * @param depth
	 */
	public ClassStructure3(ClassNode2 targetClass, int depth){	
		
		//stores class name and nesting depth
		this.targetClass = targetClass;
		this.depth = depth;
		
		//gets class methods
		List<MethodNode> methodNodes = targetClass.methods;
		
		//get the insn set of every method in the class
		for(int i = 0; i < methodNodes.size(); i++){
			MethodStructure3 tempMS = new MethodStructure3(methodNodes.get(i), getClassName());
			methodStructures.add(tempMS);
			if(tempMS.getMethodName() == "<init>"){
				initMethod = tempMS;
			}
		}
		staticFields = new ArrayList<FieldInsnNode>();
		instanceFields = new ArrayList<FieldInsnNode>();
		findFieldTypes();
	}
	
	/**
	 * Creates a ClassStructure using the string name of the class
	 * See above constructor
	 * @param targetClassName
	 * @param depth
	 * @throws IOException
	 */
	public ClassStructure3(String targetClassName, int depth) throws IOException{
		this.targetClass = new ClassNode2();
		this.depth = depth;
		ClassReader reader = new ClassReader(targetClassName);
		reader.accept(targetClass,0);
		List<MethodNode> methodNodes = targetClass.methods;
		for(int i = 0; i < methodNodes.size(); i++){
			MethodStructure3 tempMS = new MethodStructure3(methodNodes.get(i), getClassName());
			methodStructures.add(tempMS);
			if(tempMS.getMethodName() == "<init>"){
				initMethod = tempMS;
			}
		}
		staticFields = new ArrayList<FieldInsnNode>();
		instanceFields = new ArrayList<FieldInsnNode>();
		findFieldTypes();
	}
	
	/**
	 * Returns Method Structures of Target Class in List<MethodNode> form
	 * @return
	 */
	public ArrayList<MethodStructure3> getMethodStructures(){
		return methodStructures;
	}
	
	/**
	 * Returns the name of the Target Class in String form
	 * @return
	 */
	public String getClassName(){
		return targetClass.name;
	}
	
	/**
	 * Returns the interfaces of the Target Class in List<String> form if applicable
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getInterfaces(){
		return targetClass.interfaces;
	}
	
	/**
	 * Returns the name of the Target Class's Super Class
	 * @return
	 */
	public String getSuperClass(){
		return targetClass.superName;
	}
	
	/**
	 * Returns the name of the Target Class's Outer Class
	 * @return
	 */
	public String getOuterClass(){
		return targetClass.outerClass;
	}
	
	/**
	 * Returns the name of the method of the Outer Class this class
	 * is contatin in (if it's in a method)
	 * @return
	 */
	public String getEnclosingMethodOfOuterClass(){
		return targetClass.outerMethod;
	}
	
	/**
	 * Returns the depth of this Class (nested depth, 0 is source class)
	 * @return
	 */
	public int getDepth(){
		return depth;
	}
	
	/**
	 * 
	 * @return returns the MethodStructure associated with the init method of this class
	 */
	public MethodStructure3 getInitMethod(){
		return initMethod;
	}

	/**
	 * 
	 * @return returns the fields of the class associated with this ClassStructure as an ArrayList<FieldNode>
	 */
	public ArrayList<FieldNode> getFields(){
		return (ArrayList<FieldNode>) targetClass.fields;
	}
	
	/**
	 * 
	 * @return returns the static fields of the class associated with this ClassStructure
	 */
	public ArrayList<FieldInsnNode> getStaticFields(){
		return staticFields;
	}
	
	/**
	 * 
	 * @return returns the instance fields of the class associated with this ClassStructure
	 */
	public ArrayList<FieldInsnNode> getInstanceFields(){
		return instanceFields;
	}
	
	/**
	 * finds all the field Types of the class associated with this ClassStructure
	 */
	private void findFieldTypes(){
		for(int m = 0; m < methodStructures.size(); m++){
			for(int i = 0; i < methodStructures.get(m).getNumUniqueBlocks(); i++){
				for(int j = 0; j < methodStructures.get(m).getBlockStructures().get(i).getNumBlockInsn(); j++){
					if(methodStructures.get(m).getBlockStructures().get(i).getBlockInsnNumber(j).getOpcode() == Constants.OPCODE_PUTSTATIC){
						if(!staticFields.contains(((FieldInsnNode) methodStructures.get(m).getBlockStructures().get(i).getBlockInsnNumber(j)))){
							staticFields.add(((FieldInsnNode) methodStructures.get(m).getBlockStructures().get(i).getBlockInsnNumber(j)));
						}
					} else if(methodStructures.get(m).getBlockStructures().get(i).getBlockInsnNumber(j).getOpcode() == Constants.OPCODE_PUTFIELD){
						if(!instanceFields.contains(((FieldInsnNode) methodStructures.get(m).getBlockStructures().get(i).getBlockInsnNumber(j)))){
							instanceFields.add(((FieldInsnNode) methodStructures.get(m).getBlockStructures().get(i).getBlockInsnNumber(j)));
						}
					}
				}
			}
		}
	}
	
	
	private ClassNode2 targetClass;
	private int depth;
	private ArrayList<MethodStructure3> methodStructures = new ArrayList<MethodStructure3>();
	private MethodStructure3 initMethod;
	private ArrayList<FieldInsnNode> staticFields;
	private ArrayList<FieldInsnNode> instanceFields;
}
