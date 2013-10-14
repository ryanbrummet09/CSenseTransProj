/**
 * @author ryanbrummet
 */

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

public class ClassStructure {

	/**
	 * create a ClassStructure using a ClassNode2 object
	 * @param targetClass
	 * @param depth
	 */
	public ClassStructure(ClassNode2 targetClass, int depth){	
		
		//stores class name and nesting depth
		this.targetClass = targetClass;
		this.depth = depth;
		
		//gets class methods
		List<MethodNode> methodNodes = targetClass.methods;
		
		//get the insn set of every method in the class
		for(int i = 0; i < methodNodes.size(); i++){
			MethodStructure tempMS = new MethodStructure(methodNodes.get(i));
			methodStructures.add(tempMS);
		}
	}
	
	/**
	 * Creates a ClassStructure using the string name of the class
	 * See above constructor
	 * @param targetClassName
	 * @param depth
	 * @throws IOException
	 */
	public ClassStructure(String targetClassName, int depth) throws IOException{
		this.targetClass = new ClassNode2();
		this.depth = depth;
		ClassReader reader = new ClassReader(targetClassName);
		reader.accept(targetClass,0);
		List<MethodNode> methodNodes = targetClass.methods;
		for(int i = 0; i < methodNodes.size(); i++){
			MethodStructure tempMS = new MethodStructure(methodNodes.get(i));
			methodStructures.add(tempMS);
		}
	}
	
	/**
	 * Returns Fields of Target Class in List<FieldNode> form
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getFields(){
		return targetClass.fields;
	}
	
	/**
	 * Returns Method Structures of Target Class in List<MethodNode> form
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList getMethodStructures(){
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
	
	private ClassNode2 targetClass;
	private int depth;
	private ArrayList<MethodStructure> methodStructures = new ArrayList<MethodStructure>();
}

