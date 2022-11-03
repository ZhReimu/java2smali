package com.mrx.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * 存储编译后的字节码
 *
 * @author Mr.X
 * @since 2022-04-30-0030
 */
public class JavaClassObject extends SimpleJavaFileObject {

    /**
     * Compiler 编译后的 byte 数据会存在这个 ByteArrayOutputStream 对象中，
     * 后面可以取出，加载到 JVM 中。
     */
    private final ByteArrayOutputStream clazzByteStream;

    public JavaClassObject(String className, Kind kind) {
        super(URI.create("string:///" + className.replaceAll("\\.", "/") + kind.extension), kind);
        clazzByteStream = new ByteArrayOutputStream();
    }

    /**
     * 重写父类 SimpleJavaFileObject 的方法。
     * 该方法提供给编译器结果输出的 OutputStream。
     * 编译器完成编译后，会将编译结果输出到该 OutputStream 中，我们随后需要使用它获取编译结果
     *
     * @return 编译后的 byteArrayOutputStream
     */
    @Override
    public OutputStream openOutputStream() {
        return clazzByteStream;
    }

    /**
     * FileManager 会使用该方法获取编译后的 byte, 然后将类加载到 JVM
     */
    public byte[] getBytes() {
        return clazzByteStream.toByteArray();
    }

}
