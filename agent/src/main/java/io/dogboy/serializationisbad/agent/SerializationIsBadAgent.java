package io.dogboy.serializationisbad.agent;

import io.dogboy.serializationisbad.core.SerializationIsBad;

import java.io.File;
import java.lang.instrument.Instrumentation;

public class SerializationIsBadAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        SerializationIsBad.init(new File("."));
        inst.addTransformer(new SIBTransformer());
    }

}
