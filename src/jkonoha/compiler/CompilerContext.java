package jkonoha.compiler;

import java.io.*;
import java.util.*;

import jkonoha.CTX;
import jkonoha.KClass;
import jkonoha.ast.Block;
import jkonoha.ast.KonohaClass;
import jkonoha.ast.KonohaMethod;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class CompilerContext {
	
	private final CTX ctx;
	private final Map<String, KonohaClass> classMap = new HashMap<String, KonohaClass>();
	
	private class KClassLoader extends ClassLoader {
		@Override public Class<?> findClass(String name) {
			KonohaClass klass = classMap.get(name);
			if(klass == null) {
				throw new RuntimeException("class not found: " + name);
			}
			byte[] b = genBytecode(klass);
			return defineClass(name, b, 0, b.length);
		}
	}
	
	public CompilerContext(CTX ctx) {
		this.ctx = ctx;
		KonohaClass klass = ctx.scriptClass;
		classMap.put(klass.getName(), klass);
	}
	
	private byte[] genBytecode(KonohaClass klass) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		klass.accept(cw);
		return cw.toByteArray();
	}
	
	public void evalBlock(Block b) {
		KonohaClass klass = ctx.scriptClass;
		KonohaMethod mtd = new KonohaMethod(klass, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, 
				"main", KClass.objectClass, new String[0], new KClass[0]);
		klass.addMethod(mtd);
		evalBlock(mtd, b);
	}
	
	public void evalBlock(KonohaMethod mtd, Block b) {
		Compiler cg = new Compiler(ctx, this, mtd);
		cg.asmBlock(b, 0);
		cg.close();	
	}
	
	public void addClass(KonohaClass k) {
		classMap.put(k.getName(), k);
	}
	
	public KonohaClass getClass(String name) {
		if(classMap.containsKey(name)) {
			return classMap.get(name);
		} else {
			throw new CodeGenException("class not found: " + name);
		}
	}
	
	public ClassLoader createClassLoader() {
		return new KClassLoader();
	}
	
	public void writeClassFile(String dir) throws IOException {
		for(KonohaClass klass : classMap.values()) {
			byte[] bc = genBytecode(klass);
			File file = new File(dir, klass.getName() + ".class");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				fos.write(bc);
			} finally {
				if(fos != null) fos.close();
			}
		}
	}
	
}
