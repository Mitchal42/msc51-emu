package ru.savushkin.emu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 24.03.2016.
 */
public class Msc51 {
    enum pswBit {
        P(0), //содержит признаки результата арифметических операций
        UD(1), //знаковое переполнение
        OV(2), //бит четности двоичного кода
        RS0(3), //номер активного регистрового банка
        RS1(4), //номер активного регистрового банка
        F0(5), //бит пользователя
        AC(6), //полуперенос
        C(7); //перенос, заём

        int num;

        pswBit(int i) {
            num = i;
        }

        public int getAddres() {
            return num;
        }
    }

    enum aluOp {
        NOP((byte)0),
        AND(),
        OR(),
        ADD(),
        ADDC(),
        SUBB();

        byte state;

        aluOp(byte i) {
            state = i;
        }

        aluOp() {

        }

        public void set() {
            this.state = 1;
        }

        public void clear() {
            this.state = 0;
        }
    }

    private DataType wrk1, wrk2; //Адресные и рабочие регистры
    private DataType pc; //16-разрядный программный счетчик
    private int ra, rb; //регистры временного хранения операндов
    private DataType ir; //регистр инструкций предназначен для хранения параметров команды

    private boolean intrFlag;
    private int intN;

    private List<DataType> data, //Оперативная память данных
            xData, //Постоянная память программ и констант
            code; //Постоянная память программ и констант

    private static short pswA = 0x50 + 0x80;
    private static short accA = 0x60 + 0x80;
    private static short bA = 0x70 + 0x80;
    private static short spA = 0x01 + 0x80;
    private static short dplA = 0x02 + 0x80;
    private static short dphA = 0x03 + 0x80;

    private DataType acc;
    private DataType psw;
    private DataType b;
    private DataType sp;
    private DataType dptr;

    private int cycle = 0;

    public DataType getAcc() {
        return data.get(accA);
    }
    public void setAcc(DataType acc) {
        data.set(accA, acc);
    }

    public DataType getPsw() {
        return data.get(pswA);
    }
    public void setPsw(DataType psw) {
        data.set(pswA, psw);
    }

    public DataType getB() {
        return data.get(bA);
    }
    public void setB(DataType b) {
        data.set(bA, b);
    }

    public DataType getSp() {
        return data.get(spA);
    }
    public void setSp(DataType sp) {
        data.set(spA, sp);
    }

    public DataType getDptr() {
        return new DataType(16, data.get(dplA).toNumber() + (data.get(dphA).toNumber() << 8));
    }
    public void setDptr(DataType dptr) {
        data.set(dplA, new DataType(8, dptr.toNumber() & 0xff));
        data.set(dphA, new DataType(8, dptr.toNumber() >> 8));
    }

    public Msc51() {
        data = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) data.add(new DataType(8, 0));
        xData = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) xData.add(new DataType(8, 0));
        code = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) code.add(new DataType(8, 0));

        wrk1 = new DataType(8, 0);
        wrk2 = new DataType(8, 0);
        ir = new DataType(8, 0); // TODO подумать над размерностью команд

        pc = new DataType(16, 0);
        sp = new DataType(8, 0x07);
    }

//    public boolean parity(DataType dataType) {
//        boolean res = false;
//        for(int i = 0; i < dataType.length(); i++) res ^= dataType.getOne(i).getValue() == 1;
//        return res;
//    }
//
//    private String convertPswToString() {
//        return new StringBuilder("[")
//                .append(psw.getOne(pswBit.P.getAddres()).getValue() == 1 ? "P" : "_")
//                .append(psw.getOne(pswBit.UD.getAddres()).getValue() == 1 ? "U" : "_")
//                .append(psw.getOne(pswBit.OV.getAddres()).getValue() == 1 ? "O" : "_")
//                .append(psw.getOne(pswBit.F0.getAddres()).getValue() == 1 ? "F" : "_")
//                .append(psw.getOne(pswBit.AC.getAddres()).getValue() == 1 ? "A" : "_")
//                .append(psw.getOne(pswBit.C.getAddres()).getValue() == 1 ? "C" : "_")
//                .append(" Bank#")
//                .append(psw.get(pswBit.RS0.getAddres(), pswBit.RS1.getAddres()).toNumber())
//                .append("]")
//                .toString();
//    }

}
