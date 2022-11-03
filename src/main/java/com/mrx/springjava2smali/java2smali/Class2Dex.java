package com.mrx.springjava2smali.java2smali;

import com.android.tools.r8.*;
import com.android.tools.r8.origin.Origin;
import com.mrx.springjava2smali.java2smali.util.XPromise;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Class2Dex {

    public static void class2dex(String classFile, XProgramConsumer consumer) {
        try {
            D8.run(D8Command.builder()
                    .addProgramFiles(Path.of(classFile))
                    .setOutput(Path.of("."), OutputMode.DexIndexed)
                    .setProgramConsumer(consumer)
                    .build());
        } catch (CompilationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void class2dex(byte[] classByte, XProgramConsumer consumer) {
        try {
            D8.run(D8Command.builder()
                    .addClassProgramData(classByte, Origin.unknown())
                    .setOutput(Path.of("."), OutputMode.DexIndexed)
                    .setProgramConsumer(consumer)
                    .build());
        } catch (CompilationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] class2dex(byte[] classByte) {
        XProgramPromise res = new XProgramPromise();
        class2dex(classByte, res);
        return res.get();
    }

    private static class XProgramPromise extends XPromise<byte[]> implements XProgramConsumer {

    }

    public interface XProgramConsumer extends DexIndexedConsumer, Consumer<byte[]> {

        @Override
        default void accept(int fileIndex, ByteDataView data, Set<String> descriptors, DiagnosticsHandler handler) {
            int length = data.getLength();
            byte[] bytes = new byte[length];
            System.arraycopy(data.getBuffer(), data.getOffset(), bytes, 0, length);
            accept(bytes);
        }

        @Override
        default void finished(DiagnosticsHandler diagnosticsHandler) {

        }

    }

}
