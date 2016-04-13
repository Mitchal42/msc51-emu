package ru.savushkin.msc51.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by ivan on 13.04.2016.
 */
public class Msc51Test {
    Msc51 msc51;

    @Before
    public void init() {
        msc51 = new Msc51();
    }

    @After
    public void after() {
        msc51 = null;
    }

    @Test
    public void nopeTest() {
        do {
            msc51.execute();
        } while (msc51.getCycle() != 0);
        assertEquals("PC is't correct", 1, msc51.getPc().toNumber());
    }

    @Test
    public void orlARjTest() {
        msc51.setBank(0);
        msc51.setCode(Arrays.asList(0x46L));
        msc51.setAcc(msc51.getAcc().setBits(0x52L));
        do {
            msc51.execute();
        } while (msc51.getCycle() != 0);
        assertEquals("PC is't correct", 1, msc51.getPc().toNumber());
        assertEquals("ACC is't correct", 0x58L, msc51.getAcc().toNumber());
    }
}
