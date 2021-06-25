package me.escoffier.exaq;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class ExaqTest {

    // Can't use pioneer's stdout support with QuarkusTest - https://junit-pioneer.org/docs/standard-input-output/

    // Any because of the qualifier
    @Inject
    @Any
    Exaq exaq;

    @BeforeEach
    public void reset() {
        exaq.all = false;
        exaq.extended = false;
        exaq.oneLine = false;
    }

    @Test
    @DisplayName("exaq -l -a")
    public void testLa() {
        exaq.all = true;
        exaq.extended = true;
        exaq.run();
    }

    @Test
    @DisplayName("exaq")
    public void test() {
        exaq.run();
    }

    @Test
    @DisplayName("exaq -la -1")
    @StdIo
    public void testLa1(StdOut out) {
        exaq.all = true;
        exaq.extended = true;
        exaq.oneLine = true;

        exaq.run();
    }

}