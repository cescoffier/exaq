package me.escoffier.exaq;

import org.junit.jupiter.api.Test;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// @NativeImageTest - ExaqIT.test » IllegalState Unable to determine the status of the running proce
class ExaqIT {

    @Test
    public void test() throws IOException, InterruptedException, TimeoutException {
        String exec = System.getProperty("native.image.path");
        System.out.println("Executing " + exec);
        System.out.println(new ProcessExecutor().command(exec, "-la")
                .readOutput(true).execute()
                .getOutput().getString()
        );
    }

}