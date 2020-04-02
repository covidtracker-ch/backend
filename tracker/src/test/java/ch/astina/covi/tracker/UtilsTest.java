package ch.astina.covi.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest
{
    private final Utils utils = new Utils("test");

    @Test
    void anonymizeIp()
    {
        assertEquals("1.1.1.0", utils.anonymizeIp("1.1.1.1"));
        assertEquals("123.234.123.0", utils.anonymizeIp("123.234.123.234"));
    }

    @Test
    void hashIp()
    {
        assertEquals("5c76e3e2a7c351db9bf5675e1844b33c632a93d68a5a45608fc2e43db3162d64", utils.hashIp("1.1.1.1"));
    }
}