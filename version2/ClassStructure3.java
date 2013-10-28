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
			MethodStructure3 tempMS = new MethodStructure3(methodNodes.get(i));
			allMethodStructures.add(tempMS);
			if(tempMS.getMethodName() == "<init>"){
				initMethod = tempMS;
			}else if(tempMS.getMethodName() == "<clinit>"){
				clinitMethod = tempMS;
			}else if(tempMS.getMethodName() == "main"){
				mainMethod = tempMS;
			}
		}
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
			MethodStructure3 tempMS = new MethodStructure3(methodNodes.get(i));
			allMethodStructures.add(tempMS);
			if(tempMS.getMethodName() == "<init>"){
				initMethod = tempMS;
			}else if(tempMS.getMethodName() == "<clinit>"){
				clinitMethod = tempMS;
			}else if(tempMS.getMethodName() == "main"){
				mainMethod = tempMS;
			}
		}
	}
	
	/**
	 * Returns Method Structures of Target Class in List<MethodNode> form
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList getMethodStructures(){
		return allMethodStructures;
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
	 * @return returns the MethodStructure associated with the main method of this class
	 */
	public MethodStructure3 getMainMethod(){
		return mainMethod;
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
	 * @return returns the MethodStructure associated with the clinit method of this class; if it exists (clinit may not be a method of this class)
	 */
	public MethodStructure3 getClinitMethod(){
		return clinitMethod;
	}
	
	/**
	 * 
	 * @return returns the fields of the class associated with this ClassStructure as an ArrayList<FieldNode>
	 */
	public ArrayList<FieldNode> getFields(){
		return (ArrayList<FieldNode>) targetClass.fields;
	}
	
	
	private ClassNode2 targetClass;
	private int depth;
	private ArrayList<MethodStructure3> allMethodStructures = new ArrayList<MethodStructure3>();
	private ArrayList<MethodStructure3> nonStandardMethodStructures = new ArrayList<MethodStructure3>();
	private MethodStructure3 mainMethod;
	private MethodStructure3 initMethod;
	private MethodStructure3 clinitMethod;
}
