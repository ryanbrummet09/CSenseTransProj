import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.util.*;

/**
 * 
 * @author ryanbrummet
 *
 */


public class FieldStructure {

	
	public FieldStructure(FieldNode fn){
		
	}
	/*
	private static String fieldToString(FieldNode fNode){
		fNode.accept(tfv);
	}
	*/
	private static Printer printer = new Textifier();
	private static TraceFieldVisitor  tfv= new TraceFieldVisitor(printer);
}
