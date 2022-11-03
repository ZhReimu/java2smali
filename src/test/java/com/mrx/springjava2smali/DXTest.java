package com.mrx.springjava2smali;

import com.android.tools.r8.D8;
import com.mrx.springjava2smali.java2smali.Class2Dex;
import com.mrx.springjava2smali.java2smali.Dex2Smali;
import com.mrx.springjava2smali.java2smali.util.IOUtils;
import org.junit.jupiter.api.Test;

/**
 * @author Mr.X
 * @since 2022-11-02 20:31
 */
public class DXTest {

    public static void main(String[] args) {
        byte[] res = Class2Dex.class2dex(IOUtils.readBytes("1.class"));
        IOUtils.writeToFile("1.dex", res);
        byte[] smali = Dex2Smali.dex2smali(res);
        IOUtils.writeToFile("test.smali", smali);
    }

    @Test
    public void java2Dex() {
        D8.main(new String[]{"--output", ".", "1.class"});
    }

}
