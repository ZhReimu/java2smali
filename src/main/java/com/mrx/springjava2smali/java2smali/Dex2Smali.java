package com.mrx.springjava2smali.java2smali;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.jf.baksmali.Adaptors.ClassDefinition;
import org.jf.baksmali.Baksmali;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.baksmali.DisassembleCommand;
import org.jf.baksmali.formatter.BaksmaliWriter;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Dex2Smali {

    public static void dex2smali(byte[] dexBytes, Consumer<byte[]> consumer) {
        try {
            new MemBakSmali(consumer).disassembleDexFile(new DexBackedDexFile(null, dexBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class MemBakSmali {

        private final Consumer<byte[]> consumer;

        public MemBakSmali(Consumer<byte[]> consumer) {
            this.consumer = consumer;
        }

        public boolean disassembleDexFileByBakSmali(DexBackedDexFile dexFile) {
            XDisassembleCommand cmd = new XDisassembleCommand(dexFile);
            return Baksmali.disassembleDexFile(dexFile, new File("test"), Runtime.getRuntime().availableProcessors(), cmd.getOptions(), null);
        }

        public boolean disassembleDexFile(DexBackedDexFile dexFile) {
            List<? extends ClassDef> classDefs = Ordering.natural().sortedCopy(dexFile.getClasses());
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<Boolean>> tasks = Lists.newArrayList();
            XDisassembleCommand cmd = new XDisassembleCommand(dexFile);
            for (final ClassDef classDef : classDefs) {
                tasks.add(executor.submit(() -> disassembleClass(classDef, cmd.getOptions())));
            }
            boolean errorOccurred = false;
            try {
                for (Future<Boolean> task : tasks) {
                    while (true) {
                        try {
                            errorOccurred = !task.get();
                        } catch (InterruptedException ex) {
                            continue;
                        } catch (ExecutionException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    }
                }
            } finally {
                executor.shutdown();
            }
            return !errorOccurred;
        }

        private boolean disassembleClass(ClassDef classDef, BaksmaliOptions options) {
            String classDescriptor = classDef.getType();
            // validate that the descriptor is formatted like we expect
            if (classDescriptor.charAt(0) != 'L' ||
                    classDescriptor.charAt(classDescriptor.length() - 1) != ';') {
                System.err.println("Unrecognized class descriptor - " + classDescriptor + " - skipping class");
                return false;
            }
            BaksmaliWriter writer = null;
            ByteArrayOutputStream ous = new ByteArrayOutputStream();
            try {
                // create and initialize the top level string template
                ClassDefinition classDefinition = new ClassDefinition(options, classDef);
                BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(ous, StandardCharsets.UTF_8));
                // write the disassembly
                writer = new BaksmaliWriter(bufWriter, options.implicitReferences ? classDef.getType() : null);
                classDefinition.writeTo(writer);
            } catch (Exception ex) {
                System.err.println("\n\nError occurred while disassembling class " + classDescriptor.replace('/', '.') + " - skipping class");
                ex.printStackTrace();
                return false;
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                        consumer.accept(ous.toByteArray());
                    } catch (Throwable ex) {
                        System.err.println("\n\nError occurred while closing file");
                        ex.printStackTrace();
                    }
                }
            }
            return true;
        }

        private static class XDisassembleCommand extends DisassembleCommand {

            public XDisassembleCommand(DexBackedDexFile dexFile) {
                super(Collections.emptyList());
                this.dexFile = dexFile;
            }

            @Override
            public BaksmaliOptions getOptions() {
                return super.getOptions();
            }

        }

    }

}
