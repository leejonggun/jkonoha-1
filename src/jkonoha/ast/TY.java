package jkonoha.ast;

import jkonoha.KClass;

public interface TY {
	int CLASS_newid = -1;
	int unknown = -2;
	//TODO
	int VOID = 0;
	int VAR = 1;
	int OBJECT = 2;
	int BOOLEAN = 3;
	int INT = 4;
	//int TEXT = 0;
	int TYPE = 5;
	int ARRAY = 6;
	int PARAM = 7;
	int METHOD = 8;
	int SYSTEM = 9;
	int T0 = 10;
	int T = T0;
	
	KClass[] toClass = {
			KClass.voidClass,
			KClass.varClass,
			KClass.objectClass,
			KClass.booleanClass,
			KClass.intClass,
			/* follow is TODO */
			null,
			null,
			null,
			KClass.methodClass,
			KClass.systemClass,
			null,
	};
}
