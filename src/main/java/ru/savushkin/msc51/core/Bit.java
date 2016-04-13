package ru.savushkin.msc51.core;

/**
 * Created by ivan on 24.03.2016.
 */
public class Bit {
    private boolean value;

    public Bit(Bit bit) {
        setValue(bit.getValue());
    }

    public Bit(byte value) {
        setValue(value);
    }

    public byte getValue() {
        return value?(byte)1:(byte)0;
    }

    public void setValue(byte value) {
        if(value == 0 || value == 1)
            this.value = value == 1;
        else
            throw new RuntimeException(String.format("Can't set Bit to value %d", value));
    }

    public void set() {
        value = true;
    }

    public void clear() {
        value = false;
    }
}
