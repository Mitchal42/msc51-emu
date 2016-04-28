package ru.savushkin.msc51.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

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
        System.out.println();
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
        msc51.setCode(Collections.singletonList(0x46L));
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

    @Test
    public void orlADirectTest() {
        msc51.setCode(Collections.singletonList(0x45L));
        msc51.setAcc(msc51.getAcc().setBits(0x84));
        msc51.setPsw(new DataType(8, 0xc2));
        System.out.println(msc51.disassemble((int) msc51.getPc().toNumber()));
        do {
            msc51.execute();
        } while (msc51.getCycle() != 0);
        System.out.println(msc51.regsToString());
        System.out.println(msc51.internalRegsToString());
        assertEquals("PC is't correct", 1, msc51.getPc().toNumber());
        assertEquals("ACC is't correct", "0xc6", "0x" + Long.toHexString(msc51.getAcc().toNumber()));
    }

    @Test
    public void daAFirstTest() {
        msc51.setCode(Collections.singletonList(0xd4L));
        msc51.setAcc(msc51.getAcc().setBits(0xbd));
        msc51.setPsw(msc51.getPsw().setOne(Msc51.pswBit.C.getAddres(), (byte) 1));
        System.out.println(msc51.disassemble((int) msc51.getPc().toNumber()));
        do {
            msc51.execute();
        } while (msc51.getCycle() != 0);
        System.out.println(msc51.regsToString());
        System.out.println(msc51.internalRegsToString());
        assertEquals("PC is't correct", 1, msc51.getPc().toNumber());
        assertEquals("ACC is't correct", "0x23", "0x" + Long.toHexString(msc51.getAcc().toNumber()));
    }

    @Test
    public void daASecondTest() {
        msc51.setCode(Collections.singletonList(0xd4L));
        msc51.setAcc(msc51.getAcc().setBits(0xc9));
        msc51.setPsw(msc51.getPsw().setOne(Msc51.pswBit.C.getAddres(), (byte) 0));
        System.out.println(msc51.disassemble((int) msc51.getPc().toNumber()));
        do {
            msc51.execute();
        } while (msc51.getCycle() != 0);
        System.out.println(msc51.regsToString());
        System.out.println(msc51.internalRegsToString());
        assertEquals("PC is't correct", 1, msc51.getPc().toNumber());
        assertEquals("ACC is't correct", "0x29", "0x" + Long.toHexString(msc51.getAcc().toNumber()));
    }

    @Test
    public void movDirDirTest() {
        final long SOURCE = 0x45L, DEST = 0x48L;

        msc51.setCode(Arrays.asList(0x85L, SOURCE, DEST));
        msc51.setData((int) SOURCE, new DataType(8, 0x33));

        System.out.println(msc51.disassemble((int) msc51.getPc().toNumber()));
        do {
            msc51.execute();
        } while (msc51.getCycle() != 0);
        System.out.println(msc51.regsToString());
        System.out.println(msc51.internalRegsToString());
        assertEquals("PC is't correct", 3, msc51.getPc().toNumber());
        assertEquals("DATA cell is't correct", "0x33", "0x" + Long.toHexString(msc51.getData().get((int) DEST).toNumber()));
    }

    @Test
    public void movdRnTest() {
        final long SOURCE = 0x49L, DEST = 0x51L;

        msc51.setCode(Arrays.asList(0x87L, DEST));
        msc51.setData(1, new DataType(8, SOURCE));
        msc51.setData((int) SOURCE, new DataType(8, 0xE3));

        System.out.println(msc51.disassemble((int) msc51.getPc().toNumber()));
        do {
            msc51.execute();
        } while (msc51.getCycle() != 0);
        System.out.println(msc51.regsToString());
        System.out.println(msc51.internalRegsToString());
        assertEquals("PC is't correct", 2, msc51.getPc().toNumber());
        assertEquals("DATA cell is't correct", "0xe3", "0x" + Long.toHexString(msc51.getData().get((int) DEST).toNumber()));
    }

    @Test
    public void cjneRiImRelTest() {
        msc51.setCode(Arrays.asList(0xb7L, 0x29L, 0x42L));

        msc51.setData(0, new DataType(8, 0x41));
        msc51.setData(0x41, new DataType(8, 0x57));
        msc51.setPsw(msc51.getPsw().setOne(Msc51.pswBit.C.getAddres(), (byte) 1));

        System.out.println(msc51.disassemble((int) msc51.getPc().toNumber()));
        do {
            msc51.execute();
        } while (msc51.getCycle() != 0);
        System.out.println(msc51.regsToString());
        System.out.println(msc51.internalRegsToString());
        assertEquals("PC is't correct", 68, msc51.getPc().toNumber());
    }
}
