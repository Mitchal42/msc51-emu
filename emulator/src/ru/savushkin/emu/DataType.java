package ru.savushkin.emu;

/**
 * Created by ivan on 24.03.2016.
 */
public class DataType {
    private Bit[] bits;
    private int count;

    public DataType(int count, long data) {
        this.count = count;
        bits = new Bit[count];

        setBits(data);
    }

    public long returnAndInc() {
        long store = this.toNumber();

        setBits(this.toNumber()+1);
        return store;
    }

    public long toNumber() {
        long result = 0;

        for(int i = count-1; i >= 0; i--) {
            result *= 2;
            result += bits[i].getValue();
        }

        return result;
    }

    public String toBinaryString() {
        StringBuilder res = new StringBuilder();
        if(count % 2 == 0)
            for(int i = 0; i < count; i+=2) {
                res.append(bits[i].getValue());
                res.append(bits[i+1].getValue());
                res.append(" ");
            }
        else
            for(int i = 0; i < count; i++) {
                res.append(bits[i].getValue());
            }
        return res.toString();
    }

    public Bit getOne(int index) {
        if(index >= count)
            throw new ArrayIndexOutOfBoundsException();
        else
            return bits[index];
    }

    public DataType setOne(int index, byte value) {
        if(index >= count)
            throw new ArrayIndexOutOfBoundsException();
        else
            bits[index].setValue(value);
        return this;
    }

    public DataType get(int from, int to) {
        if(from  > to) {
            int temp = from; from = to; to = temp;
        }

        if(from < 0 || from >= count || to < 0 || from >= count)
            throw new ArrayIndexOutOfBoundsException();

        DataType res = new DataType(to - from, 0);
        for(int i = 0; i < to - from; i++)
            res.setOne(i, this.getOne(i + from).getValue());

        return res;
    }

    public void set(int from, int to, DataType value) {
        if(from  > to) {
            int temp = from; from = to; to = temp;
        }

        if(from < 0 || from >= count || to < 0 || from >= count)
            throw new ArrayIndexOutOfBoundsException();

        for(int i = 0; i < to - from; i++)
            this.setOne(i + from, value.getOne(i + from).getValue());
    }

    public DataType setBits(long data) {
        for(int i = 0; i < count; i++) {
            bits[i] = new Bit((byte)(data%2));
            data /= 2;
        }
        return this;
    }

    public int length() {
        return count;
    }
}
