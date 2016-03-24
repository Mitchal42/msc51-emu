package ru.savushkin.emu;

import java.util.concurrent.CompletionException;

/**
 * Created by ivan on 24.03.2016.
 */
public class Bit {
    private boolean value;

    public Bit(Bit bit) throws Exception {
        setValue(bit.getValue());
    }

    public Bit(byte value) throws Exception {
        setValue(value);
    }

    public byte getValue() {
        return value?(byte)0:(byte)1;
    }

    public void setValue(byte value) throws Exception {
        if(value == 0 || value == 1)
            this.value = value == 0;
        else
            throw new Exception(String.format("Can't set Bit to value %d", value));
    }

    public void set() {
        value = true;
    }

    public void clear() {
        value = false;
    }
}
