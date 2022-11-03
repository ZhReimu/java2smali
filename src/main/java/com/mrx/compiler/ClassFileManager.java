package com.mrx.compiler;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.security.SecureClassLoader;

/**
 * 输出字节码到 JavaClassFile
 *
 * @author Mr.X
 * @since 2022-04-30-0030
 */
public class ClassFileManager<M extends JavaFileManager> extends ForwardingJavaFileManager<M> {

    /**
     * 存储编译后的代码数据
     */
    private JavaClassObject classJavaFileObject;

    protected ClassFileManager(M fileManager) {
        super(fileManager);
    }

    /**
     * 编译后加载类
     * 返回一个匿名的 SecureClassLoader:
     * 加载由 JavaCompiler 编译后，保存在 ClassJavaFileObject 中的 byte 数组。
     */
    @Override
    public ClassLoader getClassLoader(Location location) {
        return new SecureClassLoader() {
            @Override
            protected Class<?> findClass(String name) {
                byte[] bytes = classJavaFileObject.getBytes();
                return super.defineClass(name, bytes, 0, bytes.length);
            }
        };
    }

    /**
     * 给编译器提供 JavaClassObject, 编译器会将编译结果写进去
     */
    @Override
    public JavaFileObject getJavaFileForOutput(
            Location location,
            String className,
            JavaFileObject.Kind kind,
            FileObject sibling
    ) {
        classJavaFileObject = new JavaClassObject(className, kind);
        return classJavaFileObject;
    }

    public byte[] getClassBytes() {
        return classJavaFileObject.getBytes();
    }

}
