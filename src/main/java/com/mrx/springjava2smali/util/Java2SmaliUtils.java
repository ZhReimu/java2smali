package com.mrx.springjava2smali.util;

import com.mrx.compiler.JCompiler;
import com.mrx.springjava2smali.java2smali.Class2Dex;
import com.mrx.springjava2smali.java2smali.Dex2Smali;

/**
 * @author Mr.X
 * @since 2022-11-03 20:22
 */
public class Java2SmaliUtils {

    public static String java2Smali(String className, String javaCode) {
        try (JCompiler compiler = new JCompiler()) {
            byte[] res = Class2Dex.class2dex(compiler.compile(className, javaCode).getClassBytes());
            byte[] smali = Dex2Smali.dex2smali(res);
            return new String(smali);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
