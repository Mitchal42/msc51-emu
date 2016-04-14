package ru.savushkin.msc51.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        System.out.println(msc51.disassemble((int) msc51.getPc().toNumber()));
        do {
            msc51.execute();
        } while (msc51.getCycle() != 0);
        System.out.println(msc51.regsToString());
        System.out.println(msc51.internalRegsToString());
        assertEquals("PC is't correct", 1, msc51.getPc().toNumber());
    }

    @Test
    public void orlARjTest() {
        msc51.setCode(Arrays.asList(0x46L));
        msc51.setAcc(msc51.getAcc().setBits(0x52));
        msc51.setData(0x0, new DataType(8, 0x6D));
        msc51.setData(0x6D, new DataType(8, 0x49));
        System.out.println(msc51.disassemble((int) msc51.getPc().toNumber()));
        do {
            msc51.execute();
        } while (msc51.getCycle() != 0);
        System.out.println(msc51.regsToString());
        System.out.println(msc51.internalRegsToString());
        assertEquals("PC is't correct", 1, msc51.getPc().toNumber());
        assertEquals("ACC is't correct", "0x5b", "0x" + Long.toHexString(msc51.getAcc().toNumber()));
    }
}
