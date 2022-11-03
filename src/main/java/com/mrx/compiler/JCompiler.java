package com.mrx.compiler;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * 封装后的 JavaCompiler
 *
 * @author Mr.X
 * @since 2022-04-30-0030
 **/
@SuppressWarnings("unused")
public class JCompiler implements AutoCloseable {

    private final JavaFileManager fileManager;

    public JCompiler() {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<? super JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        fileManager = new ClassFileManager<>(javaCompiler.getStandardFileManager(
                diagnosticCollector, null, null)
        );
    }

    /**
     * 编译源码
     *
     * @param clazzName  完整的类名
     * @param sourceFile 源码文件
     * @return JCompiler 对象
     */
    public JCompiler compile(String clazzName, File sourceFile) {
        try {
            return compile(clazzName, Files.readString(sourceFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 编译源码
     *
     * @param clazzName  完整的类名
     * @param sourceCode 源码
     * @return JCompiler 对象
     */
    public JCompiler compile(String clazzName, String sourceCode) {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        List<JavaFileObject> javaFileObjectList = List.of(new CharSequenceJavaFileObject(clazzName, sourceCode));
        javaCompiler.getTask(
                null, fileManager, null, null, null, javaFileObjectList
        ).call();
        return this;
    }

    public byte[] getClassBytes() {
        return ((ClassFileManager<?>) fileManager).getClassBytes();
    }

    /**
     * 加载指定类并构造一个该类的实例
     *
     * @param clazzName      要加载的完整类名
     * @param targetClazz    目标类, 如果加载到的类不是其子类, 那就抛出造型异常
     * @param parameterTypes 要调用的构造方法的参数类型数组
     * @param args           要调用的构造方法的参数数组
     * @param <T>            目标类型
     * @return 构造完毕的对象
     */
    public <T> T loadAndGetInstance(String clazzName, Class<T> targetClazz, Class<?>[] parameterTypes, Object... args) {
        try {
            Class<T> clazz = loadAs(clazzName, targetClazz);
            return clazz.getConstructor(parameterTypes).newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载指定类为指定对象
     * 将动态编译的类加载为指定类的子类
     *
     * @param clazzName   要加载的完整类名
     * @param targetClazz 目标类, 如果加载到的类不是其子类, 那就抛出造型异常
     * @param <T>         要造型的类型
     * @return 加载完毕的 Class 对象
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> loadAs(String clazzName, Class<T> targetClazz) throws ClassNotFoundException {
        Class<?> compiledClazz = loadClass(clazzName);
        if (targetClazz.isAssignableFrom(compiledClazz)) {
            return (Class<T>) compiledClazz;
        }
        throw new ClassCastException(compiledClazz.getName() + " 无法被造型为 " + targetClazz.getName());
    }

    /**
     * 加载指定类
     *
     * @param clazzName 完整类名
     * @return 加载到的 Class 对象
     */
    public Class<?> loadClass(String clazzName) throws ClassNotFoundException {
        return fileManager.getClassLoader(null).loadClass(clazzName);
    }

    /**
     * 调用默认构造方法构造一个对象
     *
     * @param clazzName   要加载的完整类名
     * @param targetClazz 目标类, 如果加载到的类不是其子类, 那就抛出造型异常
     * @param <T>         目标类型
     * @return 构造好的对象
     */
    public <T> T loadAndGetInstance(String clazzName, Class<T> targetClazz) {
        try {
            Class<T> clazz = loadAs(clazzName, targetClazz);
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭 FileManager
     */
    @Override
    public void close() throws IOException {
        fileManager.close();
    }

}
