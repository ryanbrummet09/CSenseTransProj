
import java.io.*;
import java.util.ArrayList;

public class HelloWorldTranslater {

	public static void main(String[] args) throws Exception {
		
		SourceStructure helloWorldClass = new SourceStructure(new File("/Users/ryanbrummet/Documents/cSenseProj/ASMTest/HelloWorld.class"));
		
		ClassStructure sourceClass = helloWorldClass.getSourceClassStructure();
		@SuppressWarnings("unchecked")
		ArrayList<MethodStructure> methodStructures = sourceClass.getMethodStructures();
		PrintWriter printer = new PrintWriter("/Users/ryanbrummet/Documents/HelloWorld.pml");
		MethodStructure currentMethodStructure;
		for(int i = 0; i < methodStructures.size(); i++){
			System.out.println(i);
			currentMethodStructure = methodStructures.get(i);
			for(int j = 0; j < currentMethodStructure.getNumMethodLines(); j++){
				//this is a very specific implementation so even though we are iterating
				//through each insn we will not consider them since we know what we 
				//want
				if(j == 0 && i == 0){
					printer.println("init {");
					
				} else if (j == 1 && i == 0){
					printer.println(tabSpaces + "run main()");
					printer.println("}\n");
				}
				
				if(i == 1 && j == 0){
					printer.println("proctype main () {");
					printer.println(tabSpaces + "int var" + " = " + 0 + ";" );
					nextVarNum++;
				}else if (i == 1 && j == 1){
					printer.println(tabSpaces + "printf(\"0\");");
				}else if (i == 1 && j == 2){
					printer.println(tabSpaces + "printf(\"Hello World! \")");
				}
				else if (i == 1 && j == currentMethodStructure.getNumMethodLines() - 1){
					printer.println("}");
				}
			}
		}
		printer.close();
	}
	private int here2;
	private static int here;
	private static String tabSpaces = "     ";
	private static int nextVarNum = 0;
}
