package version3;

public class Constants {
	
	//AbstractInsnNode Types
	public static final int ABSTRACT_INSN = 0;
	public static final int ABSTRACT_INT_INSN = 1;
	public static final int ABSTRACT_VAR_INSN = 2;
	public static final int ABSTRACT_TYPE_INSN = 3;
	public static final int ABSTRACT_FIELD_INSN = 4;
	public static final int ABSTRACT_METHOD_INSN = 5;
	public static final int ABSTRACT_INVOKE_DYNAMIC_INSN = 6;
	public static final int ABSTRACT_JUMP_INSN = 7;
	public static final int ABSTRACT_LABEL = 8;
	public static final int ABSTRACT_LDC_INSN = 9;
	public static final int ABSTRACT_IINC_INSN = 10;
	public static final int ABSTRACT_TABLESWITCH_INSN = 11;
	public static final int ABSTRACT_LOOKUPSWITCH_INSN = 12;
	public static final int ABSTRACT_MULTIANEWARRAY_INSN = 13;
	public static final int ABSTRACT_FRAME = 14;
	public static final int ABSTRACT_LINE = 15;
	
	//Scope Interval Constants 
	public static final int SCOPE_FOR_EACH_LOOP = 0;
	public static final int SCOPE_DO_WHILE_LOOP = 1;
	public static final int SCOPE_FIRST_RUN_CONDITIONAL_LOOP = 2;
	public static final int SCOPE_SWITCH = 3;
	public static final int SCOPE_TRY_CATCH = 4;
	public static final int SCOPE_IF_CLASS = 5;
	
	//Java built primitive array types
	public static final int ARRAY_BOOLEAN = 4;
	public static final int ARRAY_CHAR = 5;
	public static final int ARRAY_BYTE = 8;
	public static final int ARRAY_SHORT = 9;
	public static final int ARRAY_INT = 10;
	public static final int ARRAY_FLOAT = 6;
	public static final int ARRAY_LONG = 11;
	public static final int ARRAY_DOUBLE = 7;
	
	//field types
	public static final int TYPE_VOID = 0;
	public static final int TYPE_BOOLEAN = 1;
	public static final int TYPE_CHAR = 2;
	public static final int TYPE_BYTE = 3;
	public static final int TYPE_SHORT = 4;
	public static final int TYPE_INT = 5;
	public static final int TYPE_FLOAT = 6;
	public static final int TYPE_LONG = 7;
	public static final int TYPE_DOUBLE = 8;
	public static final int TYPE_ARRAY = 9;
	public static final int TYPE_OBJECT = 10;
	public static final int TYPE_METHOD = 11;
	
	
	//INSN OPCODES
	//int
	public static final int OPCODE_ICONST_0 = 3;
	public static final int OPCODE_ICONST_1 = 4;
	public static final int OPCODE_ICONST_2 = 5;
	public static final int OPCODE_ICONST_3 = 6;
	public static final int OPCODE_ICONST_4 = 7;
	public static final int OPCODE_ICONST_5 = 8;
	public static final int OPCODE_IADD = 96;
	public static final int OPCODE_ISUB = 100;
	public static final int OPCODE_IMUL = 104;
	public static final int OPCODE_IDIV = 108;
	public static final int OPCODE_IREM = 112;
	public static final int OPCODE_INEG = 116;
	public static final int OPCODE_ISHL = 120;
	public static final int OPCODE_ISHR = 122;
	public static final int OPCODE_IUSHR = 124;
	public static final int OPCODE_IAND = 126;
	public static final int OPCODE_IOR = 128;
	public static final int OPCODE_IXOR = 130;
	public static final int OPCODE_I2L = 133;
	public static final int OPCODE_I2F = 134;
	public static final int OPCODE_I2D = 135;
	public static final int OPCODE_I2B = 145;
	public static final int OPCODE_I2C = 146;
	public static final int OPCODE_I2S = 147;
	public static final int OPCODE_IASTORE = 79;
	public static final int OPCODE_IALOAD = 46;
	
	//floats
	public static final int OPCODE_FCONST_0 = 11;
	public static final int OPCODE_FCONST_1 = 12;
	public static final int OPCODE_FCONST_2 = 13;
	public static final int OPCODE_FADD = 98;
	public static final int OPCODE_FSUB = 102;
	public static final int OPCODE_FMUL = 106;
	public static final int OPCODE_FDIV = 110;
	public static final int OPCODE_FREM = 114;
	public static final int OPCODE_FNEG = 118;
	public static final int OPCODE_F2I = 139;
	public static final int OPCODE_F2L = 140;
	public static final int OPCODE_F2D = 141;
	public static final int OPCODE_FCMPL = 149;
	public static final int OPCODE_FCMPG = 150;
	public static final int OPCODE_FASTORE = 81;
	public static final int OPCODE_FALOAD = 48;

	//long
	public static final int OPCODE_LCONST_0 = 9;
	public static final int OPCODE_LCONST_1 = 10;
	public static final int OPCODE_LADD = 97;
	public static final int OPCODE_LSUB = 101;
	public static final int OPCODE_LMUL = 105;
	public static final int OPCODE_LDIV = 109;
	public static final int OPCODE_LREM = 113;
	public static final int OPCODE_LNEG = 117;
	public static final int OPCODE_LSHL = 121;
	public static final int OPCODE_LSHR = 123;
	public static final int OPCODE_LUSHR = 125;
	public static final int OPCODE_LAND = 127;
	public static final int OPCODE_LOR = 129;
	public static final int OPCODE_LXOR = 131;
	public static final int OPCODE_L2I = 136;
	public static final int OPCODE_L2F = 137;
	public static final int OPCODE_L2D = 138;
	public static final int OPCODE_LCMP = 148;
	public static final int OPCODE_LASTORE = 80;
	public static final int OPCODE_LALOAD = 47;
	
	//Double
	public static final int OPCODE_DCONST_0 = 14;
	public static final int OPCODE_DCONST_1 = 15;
	public static final int OPCODE_DADD = 99;
	public static final int OPCODE_DSUB = 103;
	public static final int OPCODE_DMUL = 107;
	public static final int OPCODE_DDIV = 111;
	public static final int OPCODE_DREM = 115;
	public static final int OPCODE_DNEG = 119;
	public static final int OPCODE_D2I = 142;
	public static final int OPCODE_D2L = 143;
	public static final int OPCODE_D2F = 144;
	public static final int OPCODE_DCMPL = 151;
	public static final int OPCODE_DCMPG = 152;
	public static final int OPCODE_DASTORE = 82;
	public static final int OPCODE_DALOAD = 49;
	
	//misc
	public static final int OPCODE_NOP = 0;
	public static final int OPCODE_ACONST_NULL = 1;
	public static final int OPCODE_AALOAD = 50;
	public static final int OPCODE_BALOAD = 51;
	public static final int OPCODE_CALOAD = 52;
	public static final int OPCODE_SALOAD = 53;
	public static final int OPCODE_AASTORE = 83;
	public static final int OPCODE_BASTORE = 84;
	public static final int OPCODE_CASTORE = 85;
	public static final int OPCODE_SASTORE = 86;
	public static final int OPCODE_POP = 87;
	public static final int OPCODE_POP2 = 88;
	public static final int OPCODE_DUP = 89;
	public static final int OPCODE_DUP_X1 = 90;
	public static final int OPCODE_DUP_X2 = 91;
	public static final int OPCODE_DUP2 = 92;
	public static final int OPCODE_DUP2_X1 = 93;
	public static final int OPCODE_DUP2_X2 = 94;
	public static final int OPCODE_SWAP = 95;
	public static final int OPCODE_IRETURN = 172;
	public static final int OPCODE_LRETURN = 173;
	public static final int OPCODE_FRETURN = 174;
	public static final int OPCODE_DRETURN = 175;
	public static final int OPCODE_ARETURN = 176;
	public static final int OPCODE_RETURN = 177;
	public static final int OPCODE_ARRAYLENGTH = 190;
	public static final int OPCODE_ATHROW = 191;
	public static final int OPCODE_MONITORENTER = 194;
	public static final int OPCODE_MONITOREXIT = 195;
	
	
	//INT_INSN
	public static final int OPCODE_BIPUSH = 16;
	public static final int OPCODE_SIPUSH = 17;
	public static final int OPCODE_NEWARRAY = 188;
	
	//VAR_INSN
	public static final int OPCODE_ILOAD = 21;
	public static final int OPCODE_LLOAD = 22;
	public static final int OPCODE_FLOAD = 23;
	public static final int OPCODE_DLOAD = 24;
	public static final int OPCODE_ALOAD = 25;
	public static final int OPCODE_ISTORE = 54;
	public static final int OPCODE_LSTORE = 55;
	public static final int OPCODE_FSTORE = 56;
	public static final int OPCODE_DSTORE = 57;
	public static final int OPCODE_ASTORE = 58;
	public static final int OPCODE_RET = 169;
	
	
	//TYPE_INSN
	public static final int OPCODE_NEW = 187;
	public static final int OPCODE_A_NEW_ARRAY = 189;
	public static final int OPCODE_CHECKCAST = 192;
	public static final int OPCODE_INSTANCEOF = 193;
	
	
	//FIELD_INSN
	public static final int OPCODE_GETSTATIC = 178;
	public static final int OPCODE_PUTSTATIC = 179;
	public static final int OPCODE_GETFIELD = 180;
	public static final int OPCODE_PUTFIELD = 181;
	
	
	//METHOD_INSN_NODE
	public static final int OPCODE_INVOKE_VIRTUAL = 182;
	public static final int OPCODE_INVOKE_SPECIAL = 183;
	public static final int OPCODE_INVOKE_STATIC = 184;
	public static final int OPCODE_INVOKE_INTERFACE = 185;
	
	
	//JUMP_INSN
	public static final int OPCODE_IFEQ = 153;
	public static final int OPCODE_IFNE = 154;
	public static final int OPCODE_IFLT = 155;
	public static final int OPCODE_IFGE = 156;
	public static final int OPCODE_IFGT = 157;
	public static final int OPCODE_IFLE = 158;
	public static final int OPCODE_IF_ICMPEQ = 159;
	public static final int OPCODE_IF_ICMPNE = 160;
	public static final int OPCODE_IF_ICMPLT = 161;
	public static final int OPCODE_IF_ICMPGE = 162;
	public static final int OPCODE_IF_ICMPGT = 163;
	public static final int OPCODE_IF_ICMPLE = 164;
	public static final int OPCODE_IF_ACMPEQ = 165;
	public static final int OPCODE_IF_ACMPNE = 166;
	public static final int OPCODE_GOTO = 167;
	public static final int OPCODE_JSR = 168;
	public static final int OPCODE_IFNULL = 198;
	public static final int OPCODE_IFNONNULL = 199;
	
	/**
	 * 
	 * @param rawString
	 * @return takes a string and returns the same string with all non alphabet or non numbers removed
	 */
	public static String zeroString(String rawString){
		return rawString.replaceAll("[^a-zA-Z0-9]", "");
	}
}