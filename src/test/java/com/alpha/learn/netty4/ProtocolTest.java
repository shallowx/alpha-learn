package com.alpha.learn.netty4;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ProtocolTest {

    @Test
    public void test() {
        int magic = 0x1ACE;
        int type = 1;
        int cmd = 5;

        int header = (magic << 16) | (type << 15) | (cmd & 0xFF);

        int magic1 = (header >>> 16) & 0xFFFF;
        int type1  = (header >>> 15) & 0x1;
        int cmd1   = header & 0xFFF;

        log.info("magic1: {}, type1: {}, cmd1: {}", magic1, type1, cmd1);
    }
}
