/**
 * @author ryanbrummet
 */

package version3;

import java.io.*;
import java.util.*;
import org.objectweb.asm.*;
import ASMModifiedSourceCode.*;

public class SourceStructure3 {
	
	/**
	 * Creates a SourceStructure using an input file
	 * @param file
	 * @throws Exception
	 */
	public SourceStructure3(File file) throws Exception{
		
		//Stores the file location
		this.file = file;
		
		//Reads the file
		InputStream in = new FileInputStream(this.file);
		
		//Constructs a reader to get data from class owner of file
		reader = new ClassReader(in);
		sourceClass = new ClassNode2();
		reader.accept(sourceClass, 0);
		
		//gets a ClassStructure representation of the class owner of thefile
		//The first ClassStructure in the classStructures ArrayList is always
		//the class owner of the file
		classStructures.add(new ClassStructure3(sourceClass,0));
		findNestedInnerClasses(sourceClass,0);
		ArrayList<MethodStructure3> ms3 = classStructures.get(0).getMethodStructures();
		for(MethodStructure3 ms: ms3){
			if(ms.getMethodName() == "main"){
				main = ms;
			}else if(ms.getMethodName() == "<init>"){
				init = ms;
			}else if(ms.getMethodName() == "<clinit>"){
				clinit = ms;
			}
		}
		sourceGlobalVariables = classStructures.get(0).getFields();
	}
	
	/**
	 * It is assumed that ALL CLASSES regardless of nesting, have unique names
	 * In addition, it is assumed that there is not multi-level nesting A SOURCE
	 * STRUCTURE OBJECT CAN ONLY HANDLE ONE LEVEL OF CLASS NESTING
	 * @param targetClass
	 * @param depth
	 * @throws IOException
	 */
	private void findNestedInnerClasses(ClassNode2 targetClass, int depth) throws IOException{
		
		//saves the depth of a particular inner class
		depth++;
		
		//get the inner classes of a class
		List<InnerClassNode2> innerClassNodes = targetClass.innerClasses;
		
		//returns if there are no inner classes
		if(innerClassNodes == null){
			return;
		} else {
			
			//if there are inner classes, each inner class is converted to a ClassStructure
			//object and stored in an ArrayList
			for(int i = 0; i < innerClassNodes.size(); i++){
				classStructures.add(new ClassStructure3(innerClassNodes.get(i).name, depth));
			}
		}
	}
	
	/**
	 * returns the class structures
	 * @return
	 */
	public ArrayList<ClassStructure3> getClassStructures(){
		return classStructures;
	}
	
	/**
	 * returns the source file
	 * @return
	 */
	public File getSourceFile(){
		return file;
	}
	
	/**
	 * returns the source class
	 * @return
	 */
	public ClassStructure3 getSourceClassStructure(){
		return classStructures.get(0);
	}
	
	/**
	 * 
	 * @return returns the MethodStructure associated with the main method of this SourceStructure, if it exists;
	 */
	public MethodStructure3 getMainMethod(){
		return main;
	}
	
	/**
	 * 
	 * @return returns the MethodStructure associated with the constructor of this SourceStructure
	 */
	public MethodStructure3 getInitMethod(){
		return init;
	}
	
	/**
	 * 
	 * @return returns the MethodStructure associated with the static constructor (?, <clinit>) of this SourceStructure, if it exists
	 */
	public MethodStructure3 getClinitMethod(){
		return clinit;
	}
	
	public ArrayList<FieldNode> getSourceFields(){
		return sourceGlobalVariables;
	}
	
	private File file;
	private ClassReader reader;
	private ClassNode2 sourceClass;
	private MethodStructure3 main;
	private MethodStructure3 init;
	private MethodStructure3 clinit;
	private ArrayList<FieldNode> sourceGlobalVariables;

	//first Class Structure in the ArrayList is always the Source Class Structure
	private ArrayList<ClassStructure3> classStructures = new ArrayList<ClassStructure3>();
}

