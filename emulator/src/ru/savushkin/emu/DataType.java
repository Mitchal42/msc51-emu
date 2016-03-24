package ru.savushkin.emu;

/**
 * Created by ivan on 24.03.2016.
 */
public class DataType {
    private Bit[] bits;
    private int count;

    public DataType(int count, long data) throws Exception {
        this.count = count;
        bits = new Bit[count];

        for(int i = 0; i < this.count; i++) {
            bits[i] = new Bit((byte)(data%2));
            data /= 2;
        }
    }

    public long toNumber() {
        long result = 0;

        for(int i = count-1; i >= 0; i--) {
            result *= 2;
            result += bits[i].getValue();
        }

        return result;
    }

    public Bit getOne(int index) {
        if(index >= count)
            throw new ArrayIndexOutOfBoundsException();
        else
            return bits[index];
    }

    public void setOne(int index, byte value) throws Exception {
        if(index >= count)
            throw new ArrayIndexOutOfBoundsException();
        else
            bits[index].setValue(value);
    }

    public DataType get(int from, int to) throws Exception {
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

    public void set(int from, int to, DataType value) throws Exception {
        if(from  > to) {
            int temp = from; from = to; to = temp;
        }

        if(from < 0 || from >= count || to < 0 || from >= count)
            throw new ArrayIndexOutOfBoundsException();

        for(int i = 0; i < to - from; i++)
            this.setOne(i + from, value.getOne(i + from).getValue());
    }

    public int length() {
        return count;
    }
}
