package tech.theaha.log.shield.bc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.theaha.log.shield.ShieldProcessor;
import tech.theaha.log.shield.ShieldProcessorLoader;

/**
 * 生成Object对应的脱敏对象
 * @author inthendsun
 */
public class OffSensitiveHandlerDump implements Opcodes {
	
	private static Logger logger = LoggerFactory.getLogger(OffSensitiveHandlerDump.class);

	/**
	 * 动态加载生成好的脱敏类。使用当前线程的类加载器。
	 * @param b 字节数组
	 * @param className 脱敏类名
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private static Class<?> loadClass(byte[] b, String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> clazz = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class<?> cls = Class.forName("java.lang.ClassLoader");
        java.lang.reflect.Method method = cls.getDeclaredMethod("defineClass", new Class[] {
                String.class, byte[].class, int.class, int.class });
        method.setAccessible(true);
        try {
            Object[] args = new Object[] { className, b, new Integer(0), new Integer(b.length) };
            clazz = (Class<?>) method.invoke(loader, args);
        } finally {
            method.setAccessible(false);
        }
        return clazz;
    }
	/**
	 * 生成指定类的脱敏handler。
	 * @param qulifiedClassName 如 "red.psyche.XX"
	 * @param domainObjectType 指定的类，如 UserDO.class
	 * @param meta
	 * @param outFolder 指定生成的class的输出路径
	 * @return
	 * @throws Exception
	 */
    public static <T> OffSensitiveHandler<T> get(String qulifiedClassName,
    											 Class<T> domainObjectType,
    											 ObjectShieldMeta meta,
    											 String outFolder) throws Exception {
    	String classInternaleName = qulifiedClassName.replaceAll("\\.", "/");
		byte[] bytes = dump(classInternaleName,domainObjectType,meta);
		
		if(outFolder != null) {
			writeToFile(outFolder,classInternaleName.substring(classInternaleName.lastIndexOf("/")+1) +".class",bytes);
		}
        
        @SuppressWarnings("unchecked")
		Class<OffSensitiveHandler<T>> clz = (Class<OffSensitiveHandler<T>>) loadClass(bytes, qulifiedClassName);
        OffSensitiveHandler<T> crypto =  clz.newInstance();
        return crypto;
    }
    
    public static String getGenericDescriptor(Class<?> clazz,Class<?> genericClazz) {
		String clazzDesc = Type.getDescriptor(clazz);
		return clazzDesc.substring(0,clazzDesc.length()-1) + "<"+Type.getDescriptor(genericClazz)+">;";
	}

    
	public static byte[] dump(String internaleName,
							 Class<?> domainObjectType,
							 ObjectShieldMeta meta) throws Exception {
		
		List<StringShieldField> stringShieldList = meta.getStringShieldFields();
		if(stringShieldList == null) {
			stringShieldList = new ArrayList<StringShieldField>();
		}
		List<NShieldField> nShieldFields = meta.getNestedShieldFields();
		if(nShieldFields == null) {
			nShieldFields = new ArrayList<NShieldField>();
		}
		//如：javaFileName的名字是UserDOCrypto.java
		String javaFileName = internaleName.substring(internaleName.lastIndexOf("/")+1) +".java";
		
		//自动计算Frames max
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
		cw.visit(
				V1_6,
				ACC_PUBLIC + ACC_SUPER,
				internaleName,
				Type.getDescriptor(Object.class) + getGenericDescriptor(OffSensitiveHandler.class, domainObjectType),
				Type.getInternalName(Object.class),
				new String[] { Type.getInternalName(OffSensitiveHandler.class)});

		cw.visitSource(javaFileName, null);
		
		generateInit(cw);
		generateShield(cw,stringShieldList,domainObjectType,internaleName);
		generateNShield(cw,domainObjectType,nShieldFields,internaleName);
		generateRestore(cw,domainObjectType,stringShieldList,internaleName);
		bridgeResore(cw,domainObjectType,internaleName);
		bridgeShield(cw,domainObjectType,internaleName);
		bridgeGetNShieldFields(cw,domainObjectType,internaleName);
		
		cw.visitEnd();

		return cw.toByteArray();
	}

	
	/**
	 * 生成构造方法
	 * @param cw
	 */
	private static void generateInit(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
				"()V");
		mv.visitInsn(RETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		
	}
	
	/**
	 * 
	 * @param cw
	 * @param domainObjectType
	 * @param nShieldFields
	 * @param internaleName
	 */
	private static void generateNShield(ClassWriter cw, 
										Class<?> domainObjectType,
										List<NShieldField> nShieldFields, 
										String internaleName) {

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "getNShieldFields", "("+Type.getDescriptor(domainObjectType)+")Ljava/util/List;", 
											"("+Type.getDescriptor(domainObjectType)+")Ljava/util/List<Ljava/lang/Object;>;", null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, "java/util/ArrayList");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
		mv.visitVarInsn(ASTORE, 2);

		for (NShieldField offField : nShieldFields) {
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(domainObjectType), offField.getGetMethodName(), "()" + Type.getDescriptor(offField.getReturnType()));
			Label ifNullLabel = new Label();
			mv.visitJumpInsn(IFNULL, ifNullLabel);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(domainObjectType), offField.getGetMethodName(), "()" + Type.getDescriptor(offField.getReturnType()));
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
			mv.visitInsn(POP);
			mv.visitLabel(ifNullLabel);
		}
	
		mv.visitVarInsn(ALOAD, 2);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 3);
		mv.visitEnd();

	}
	/**
	 * 生成恢复变量值方法
	 * @param cw
	 * @param domainObjectType
	 * @param stringShieldList
	 * @param internaleName
	 */
	private static void generateRestore(ClassWriter cw, 
										Class<?> domainObjectType,
										List<StringShieldField> stringShieldList,
										String internaleName) {

		MethodVisitor mv = cw.visitMethod(
				ACC_PUBLIC,
				"restore",
				"("+Type.getDescriptor(domainObjectType)+"Ljava/util/Map;)V",
				"("+Type.getDescriptor(domainObjectType)+"Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V",
				null);
		
		mv.visitCode();
		
		//如果不需要恢复，直接返回
		if(stringShieldList == null || stringShieldList.size() == 0) {
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 3);
			mv.visitEnd();
			return;
		}
		
		//访问第二个参数——map 的keySet方法，存储至本地第3个变量
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEINTERFACE, 
						  "java/util/Map", 
						  "keySet",
						  "()Ljava/util/Set;");
		mv.visitVarInsn(ASTORE, 3);
		
		//加载第3个变量，访问iterator方法
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator",
				"()Ljava/util/Iterator;");
		
		//将Iterator变量存储到底5个变量
		mv.visitVarInsn(ASTORE, 5);
		Label l2 = new Label();
        //总是跳转到for循环里
		mv.visitJumpInsn(GOTO, l2);
		Label l3 = new Label();
		mv.visitLabel(l3);
		
		//访问第5个变量——Iterator的next方法,存储到第4个变量——setMethodName
		mv.visitVarInsn(ALOAD, 5);
		mv.visitMethodInsn(INVOKEINTERFACE, 
						  "java/util/Iterator", 
						  "next",
						  "()Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, "java/lang/String");
		mv.visitVarInsn(ASTORE, 4);
		
		//判断第4个变量值是否等于指定方法名
		for (int i = 0; i < stringShieldList.size(); i++) {
			StringShieldField gSetter = stringShieldList.get(i);
			mv.visitLdcInsn(gSetter.getSetMethodName());
			mv.visitVarInsn(ALOAD, 4);
			mv.visitMethodInsn(INVOKEVIRTUAL, 
							  "java/lang/String", 
							  "equals",
							  "(Ljava/lang/Object;)Z");
			
			Label l5 = null;
			if(i == stringShieldList.size() -1 ) {
                //如果是最后一个 且 不相等，跳出循环
				mv.visitJumpInsn(IFEQ, l2);
			} else {
				l5 = new Label();
                //如果不是最后一个 且 不是相等，continue
				mv.visitJumpInsn(IFEQ, l5);
			}
			//相等的话,从map中取值，设置到第1个变量
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 4);
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get","(Ljava/lang/Object;)Ljava/lang/Object;");
			mv.visitTypeInsn(CHECKCAST, "java/lang/String");
			mv.visitMethodInsn(INVOKEVIRTUAL, 
							   Type.getInternalName(domainObjectType),
							  gSetter.getSetMethodName(), "(Ljava/lang/String;)V");
			
			mv.visitJumpInsn(GOTO, l2);
			
			if(i == stringShieldList.size() -1) {
                //打点
				mv.visitLabel(l2);
			} else {
                //打点
				mv.visitLabel(l5);
			}
		}
		
		//调用iterator变量的hasNext方法
		mv.visitVarInsn(ALOAD, 5);
		mv.visitMethodInsn(INVOKEINTERFACE, 
						  "java/util/Iterator",
						  "hasNext", "()Z");
        //如果map还有下一个值
		mv.visitJumpInsn(IFNE, l3);
		
		//返回
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 6);
		mv.visitEnd();
	}
	/**
	 * 
	 * @param cw
	 * @param stringShieldList
	 * @param domainObjectType
	 * @param internaleName
	 */
	private static void generateShield(ClassWriter cw, 
									  List<StringShieldField> stringShieldList, 
									  Class<?> domainObjectType, 
									  String internaleName) {
		MethodVisitor mv = cw.visitMethod(
				ACC_PUBLIC,
				"shield",
				"("+Type.getDescriptor(domainObjectType)+")Ljava/util/Map;",
				"("+Type.getDescriptor(domainObjectType)+")Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;",
				null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, "java/util/HashMap");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>","()V");
		mv.visitVarInsn(ASTORE, 2);
		
		//start  原值put 到map 
		for (StringShieldField gSetter : stringShieldList) {
			mv.visitVarInsn(ALOAD, 2);
			mv.visitLdcInsn(gSetter.getSetMethodName());
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, 
							  Type.getInternalName(domainObjectType),
							  gSetter.getGetMethodName(), "()Ljava/lang/String;");
			mv.visitMethodInsn(INVOKEINTERFACE, 
							  "java/util/Map", 
							  "put",
							  "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
			mv.visitInsn(POP);
		}
		//end
		
		// set 替换之
		/*
		 20180914  
		 for (StringShieldField gSetter : stringShieldList) {
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, 
							  Type.getInternalName(domainObjectType),
							  gSetter.getGetMethodName(), 
							  "()Ljava/lang/String;");
			mv.visitMethodInsn(INVOKESTATIC,
							  Type.getInternalName(ShieldCommon.class), 
							  ShieldEnumConverter.getMethodName(gSetter.getShield().type()),
							 "(Ljava/lang/String;)Ljava/lang/String;");
			mv.visitMethodInsn(INVOKEVIRTUAL,  
							  Type.getInternalName(domainObjectType),
							  gSetter.getSetMethodName(), "(Ljava/lang/String;)V");
		}
		*/
		
		 for (StringShieldField gSetter : stringShieldList) {
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitLdcInsn(gSetter.getShield().type());
				mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(ShieldProcessorLoader.class), "get", "(Ljava/lang/String;)L"+Type.getInternalName(ShieldProcessor.class)+";");
				if(gSetter.getShield().typeVal().length()==0) {
					mv.visitInsn(ACONST_NULL);
				} else {
					mv.visitLdcInsn(gSetter.getShield().typeVal());
				}
				
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(domainObjectType), gSetter.getGetMethodName(), "()Ljava/lang/String;");
				mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(ShieldProcessor.class), "shield", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
				mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(domainObjectType), gSetter.getSetMethodName(), "(Ljava/lang/String;)V");
		}
		
		mv.visitVarInsn(ALOAD, 2);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(3, 3);
		mv.visitEnd();
	}
	
	private static void bridgeGetNShieldFields(ClassWriter cw, Class<?> domainObjectType, String internaleName) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, 
							"getNShieldFields", "(Ljava/lang/Object;)Ljava/util/List;", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, Type.getInternalName(domainObjectType));
		mv.visitMethodInsn(INVOKEVIRTUAL, internaleName, 
						"getNShieldFields","("+Type.getDescriptor(domainObjectType)+")Ljava/util/List;");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}
	
	private static void bridgeShield(ClassWriter cw, Class<?> domainObjectType, String internaleName) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC,
				"shield", "(Ljava/lang/Object;)Ljava/util/Map;", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, Type.getInternalName(domainObjectType));
		mv.visitMethodInsn(INVOKEVIRTUAL,internaleName,"shield",
				"("+Type.getDescriptor(domainObjectType)+")Ljava/util/Map;");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}
	
	private static void bridgeResore(ClassWriter cw, 
									 Class<?> domainObjectType, 
									 String internaleName) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC,
				"restore", "(Ljava/lang/Object;Ljava/util/Map;)V", null,
				null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, Type.getInternalName(domainObjectType));
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL,internaleName,"restore",
				"("+Type.getDescriptor(domainObjectType)+"Ljava/util/Map;)V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 3);
		mv.visitEnd();
	}
	
	private static void writeToFile(String outFolder, String qulifiedClassName,byte[] bytes) {
    	FileOutputStream fout = null;
		try{
			File folder = new File(outFolder);
			if(!folder.exists()) {
				folder.mkdirs();
			}
			fout = new FileOutputStream(new File(outFolder,qulifiedClassName));
			fout.write(bytes);
		} catch(Exception e) {
			logger.error("输出文件异常,忽略",e);
		} finally {
			if(fout != null) {
				try{
					fout.flush();
					fout.close();
				} catch(IOException e) {
					logger.error("关闭流异常",e);
				}
			}
		}
	}
}
