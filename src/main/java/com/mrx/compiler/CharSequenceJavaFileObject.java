package com.mrx.compiler;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * 字符串 java 源代码的 JavaFileObject 表示
 *
 * @author Mr.X
 * @since 2022-04-30-0030
 **/
public class CharSequenceJavaFileObject extends SimpleJavaFileObject {

    /**
     * 源码对象
     */
    private final CharSequence content;

    protected CharSequenceJavaFileObject(String className, String fileContent) {
        super(URI.create("string:///" + className.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE);
        content = fileContent;
    }

    /**
     * 获取需要编译的源代码
     *
     * @return 需要编译的源代码
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return content;
    }

}