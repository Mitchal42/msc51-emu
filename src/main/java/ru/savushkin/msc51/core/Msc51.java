package ru.savushkin.msc51.core;

import java.util.ArrayList;
import java.util.List;

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

    private DataType wrk1, wrk2; //Адресные и рабочие регистры
    private DataType pc; //16-разрядный программный счетчик
    private int ra, rb; //регистры временного хранения операндов
    private DataType ir; //регистр инструкций предназначен для хранения параметров команды

//    private boolean intrFlag; прерываний нет :)
//    private int intN;

    private List<DataType> data, //Оперативная память данных
            xData, //Постоянная память программ и констант
            code; //Постоянная память программ и констант

    private static short pswA = 0x50 + 0x80;
    private static short accA = 0x60 + 0x80;
    private static short bA = 0x70 + 0x80;
    private static short spA = 0x01 + 0x80;
    private static short dplA = 0x02 + 0x80;
    private static short dphA = 0x03 + 0x80;

//    private DataType acc;
//    private DataType psw;
//    private DataType b;
//    private DataType sp;
//    private DataType dptr;

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

    public DataType getPc() {
        return pc;
    }
    public void setPc(DataType pc) {
        this.pc = pc;
    }

    public int getCycle() {
        return cycle;
    }

    public List<DataType> getData() {
        return data;
    }
    public void setData(int pos, DataType val) {
        data.set(pos, val);
    }

    public List<DataType> getCode() {
        return code;
    }
    public void setCode(List<Long> code) {
        for (int i = 0; i < 256; i++)
            if(code.size() <= i)
                break;
            else
                this.code.set(i, this.code.get(i).setBits(code.get(i)));
    }

    public List<DataType> getxData() {
        return xData;
    }

    public void setBank(long bankNum) {
        DataType psw = this.getPsw();

        psw.set(pswBit.RS0.getAddres(), pswBit.RS1.getAddres(), new DataType(2, bankNum));

        setPsw(psw);
    }

    public Msc51() {
        data = new ArrayList<>(256);
        for (int i = 0; i < 256; i++)
            data.add(new DataType(8, 0));
        xData = new ArrayList<>(256);
        for (int i = 0; i < 256; i++)
            xData.add(new DataType(8, 0));
        code = new ArrayList<>(256);
        for (int i = 0; i < 256; i++)
            code.add(new DataType(8, 0));

        wrk1 = new DataType(8, 0);
        wrk2 = new DataType(8, 0);
        ir = new DataType(8, 0);

        pc = new DataType(16, 0);
        setSp(new DataType(8, 0x07));
    }

    public void execute() {
        if(cycle == 0) {
            ir.setBits(code.get((int) pc.returnAndInc()).toNumber());
            cycle++;
        } else {
            switch ((int) ir.toNumber()) {
                case 0x00:
                    nop();
                    break;

                case 0x46:
                case 0x47:
                    orlARj();
                    break;

                case 0x44:
                    orlImmediate();
                    break;

                case 0xd4:
                    daa();
                    break;

                case 0x85:
                    movdirdir();
                    break;

                case 0x86:
                case 0x87:
                    movdRn();
                    break;

                case 0xb6:
                case 0xb7:
                    cjneRiimrel();
                    break;

            }

            setPsw(getPsw().setOne(pswBit.P.getAddres(), (byte) (parity(getAcc()) ? 1 : 0)));
        }
    }

    long ir1Bit() {
        return (getPsw().get(3, 4).toNumber() << 3) + ir.getOne(0).getValue();
    }

    void nop() {
        cycle = 0;
    }

    void orlARj() { // orl a, @Rj
        switch (cycle)
        {
            // BusA=Bank+IR[0];BusB8=Data[BusA];Wrk1Src=BusB;SetWrk1;Romm++;
            case 1:
                wrk1.setBits(data.get((int) ir1Bit()).toNumber());
                cycle++;
                break;

            // BusA=Wrk1;BusB8=Data[BusA];BusC8=Acc;BusB->RA;BusC->RB;Romm++
            case 2:
                ra = (int) data.get((int) wrk1.toNumber()).toNumber();
                rb = (int) getAcc().toNumber();
                cycle++;
                break;

            // AluOp=OR;Alu->BusB;BusB->Acc;Goto;Romm=@Next
            case 3:
                setAcc(getAcc().setBits((ra | rb) & 0xff));
                cycle = 0;
                break;
        }
    }

    void orlImmediate()
    {
        switch (cycle)
        {
            // BusB8=Acc;BusC16=Code;PC++;BusB->RA;BusC->RB;Romm++
            case 1:
                ra = (int) getAcc().toNumber();
                rb = (int) code.get((int) pc.returnAndInc()).toNumber();
                cycle++;
                break;

            // AluOp=OR;Alu->BusB;BusB->Acc;Goto;Romm=@Next
            case 2:
                setAcc(getAcc().setBits(ra | rb));
                cycle=0;
                break;

        }
    }

    int CR() // Correction value for decimal addiction
    {
        int R = ((getAcc().toNumber() & 0x0f) > 0x09 || (getPsw().getOne(6).getValue() == 1)) ? 0x06 : 0x00;
        R += ((getAcc().toNumber() & 0xf0) > 0x90 || (getPsw().getOne(7).getValue() == 1)) ? 0x60 : 0x00;

        return R;
    }

    int CRPsw() // Correction value for decimal addiction
    {
        return (getPsw().get(0, 6).toNumber() + ra + rb > 0xff ? 0x80 : 0);
    }

    void daa() // Decimal Addiction
    {
        switch (cycle)
        {
            // BusB8=Acc;BusC8=CR;BusB->RA;BusC->RB;Romm++
            case 1:
                ra = (int) getAcc().toNumber();
                rb = CR();
                cycle++;
                break;

            // AluOp=Add;Alu->BusB;BusB->Acc;Romm++
            case 2:
                setAcc(getAcc().setBits(ra + rb));
                cycle++;
                break;

            // AluOp=Add;BusC8=CRPsw;Wrk1Src=BusC;SetWrk1;Romm++
            case 3:
                wrk1.setBits(CRPsw());
                cycle++;
                break;

            case 4:
                setPsw(getPsw().setBits(wrk1.toNumber()));
                cycle = 0;
                break;

        }
    }

    void movdirdir() // mov dest_direct, src_direct
    {
        switch (cycle)
        {
            // BusC16=Code;PC++;Wrk1Src=BusC;SetWrk1;Romm++
            case 1:
                wrk1.setBits(code.get((int) pc.returnAndInc()).toNumber());
                cycle++;
                break;

            // BusA=Wrk1;BusB8=Data[BusA];Wrk2Src=BusB;SetWrk2;Romm++
            case 2:
                wrk2.setBits(data.get((int) wrk1.toNumber()).toNumber());
                cycle++;
                break;

            // BusC16=Code;PC++;Wrk1Src=BusC;SetWrk1;Romm++
            case 3:
                wrk1.setBits(code.get((int) pc.returnAndInc()).toNumber());
                cycle++;
                break;

            // BusA=Wrk1;BusB8=Wrk2;BusB->Data[BusA];Goto;Romm=@Next
            case 4:
                data.set((int) wrk1.toNumber(), wrk2);
                cycle = 0;
                break;
        }
    }

    void movdRn() // mov direct, @Ri
    {
        switch (cycle)
        {
            //BusA=Bank+IR[0];BusB8=Data[BusA];Wrk1Src=BusB;SetWrk1;Romm++
            case 1:
                wrk1.setBits(data.get((int) ir1Bit()).toNumber());
                cycle++;
                break;

            // BusA=Wrk1;BusB8=Data[BusA];Wrk2Src=BusB;SetWrk2;Romm++
            case 2:
                wrk2.setBits(data.get((int) wrk1.toNumber()).toNumber());
                cycle++;
                break;

            // BusC16=Code;PC++;Wrk1Src=BusC;SetWrk1;Romm++
            case 3:
                wrk1.setBits(code.get((int) pc.returnAndInc()).toNumber());
                cycle++;
                break;

            // BusA=Wrk1;BusB8=Wrk2;BusB->Data[BusA];Goto;Romm=@Next
            case 4:
                data.set((int) wrk1.toNumber(), wrk2);
                cycle = 0;
                break;

        }
    }

    void cjneRiimrel()
    {
        switch (cycle)
        {
            //BusA=Bank+IR[2..0];BusB8=Data[BusA];Wrk1Src=BusB;SetWrk1;Romm++
            case 1:
                wrk1.setBits(data.get((int) ir1Bit()).toNumber());
                cycle++;
                break;

            // BusA=Wrk1;BusB8=Data[Wrk1];BusC16=Code;PC++;BusB->RA;BusC->RB;Goto;Romm=@CJNE_Compare
            case 2:
                ra = (int) data.get((int) wrk1.toNumber()).toNumber();
                rb = (int) code.get((int) pc.returnAndInc()).toNumber();
                cycle++;
                break;

            // AluOp=suba;Alu->BusB;Wrk1Src=BusB;SetWrk1;Romm++
            case 3:
                wrk1.setBits(Math.abs(ra - rb) & 0xff);// TODO проверить выполнение три раза
                cycle++;
                break;

            //If=Wrk1Z;Goto;Romm=@CJNE_IO_Equal
            case 4:
                if (wrk1.toNumber() == 0)
                    cycle = 6;
                else
                    cycle++;
                break;

            // Goto;Romm=@$80
            case 5:
                cycle = 7;
                break;

            // PC++;Goto;Romm=@Next
            case 6:
                pc.returnAndInc();
                cycle = 0;
                break;

            //BusC16=Code;PC++;Wrk1Src=BusC;SetWrk1;BusB8=Psw;Wrk2Src=BusB;SetWrk2;Romm++
            case 7:
                wrk1.setBits(code.get((int) pc.toNumber()).toNumber());
                wrk2.setBits(getPsw().toNumber());
                cycle++;
                break;

            // BusC8=Wrk1;BusB16=PCL;BusB->RA;BusC->RB;Romm++
            case 8:
                ra = (int) wrk1.toNumber();
                rb = (int) pc.toNumber() & 0xff;
                cycle++;
                break;

            // AluOp=Add;Alu->BusB;BusB->PCL;Romm++
            case 9:
                pc.setBits(pc.toNumber() & 0xff00);
                pc.setBits(pc.toNumber() + ((ra + rb) & 0xff));
                cycle++;
                break;

            // AluOp=Add;AluPsw->BusB;BusB->PSW;Romm++
            case 10:
                setPsw(getPsw().setOne(7, (byte) ((ra + rb) > 0xff ? 1 : 0)));
                cycle++;
                break;

            // If=Wrk1.7;Goto;Romm=@SJMP_IO_Lower
            case 11:
                if ((wrk1.toNumber() & 0x80) > 0)
                    cycle = 15;
                else
                    cycle++;
                break;

            // BusC16=PCH;BusB->RA;BusC->RB;Romm++
            case 12:
                ra = 0;
                rb = (int) (pc.toNumber() >> 8);
                cycle++;
                break;

            // AluOp=Add;+Psw.cf;Alu->BusB;Wrk1Src=BusB;SetWrk1;Romm++
            case 13:
                wrk1.setBits(ra + rb + getPsw().getOne(7).getValue());
                cycle++;
                break;

            // BusC8=Wrk1;BusC->PCH;BusB8=Wrk2;BusB->PSW;Goto;Romm=@Next
            case 14:
                pc.setBits(pc.toNumber() & 0xff);
                pc.setBits(pc.toNumber() + (wrk1.toNumber() << 8));
                setPsw(getPsw().setBits(wrk2.toNumber()));
                cycle=0;
                break;

            // BusC16=PCH;BusBC=0xff;BusB->RA;BusC->RB;Romm++
            case 15:
                ra = 0xff;
                rb = (int) (pc.toNumber() >> 8);
                cycle++;
                break;

            // AluOp=Add;+Psw.cf;Alu->BusB;Wrk1Src=BusB;SetWrk1;Romm++
            case 16:
                wrk1.setBits(ra + rb + getPsw().getOne(7).getValue());
                cycle++;
                break;

            // BusC8=Wrk1;BusC->PCH;BusB8=Wrk2;BusB->PSW;Goto;Romm=@Next
            case 17:
                pc.setBits(pc.toNumber() & 0xff);
                pc.setBits(pc.toNumber() + (wrk1.toNumber() << 8));
                setPsw(getPsw().setBits(wrk2.toNumber()));
                cycle = 0;
                break;
        }
    }

    public String disassemble(int address) {
        int instruction = (int) code.get(address).toNumber();
        switch (instruction) {
            case 0x00: return String.format("%02X: nop", instruction);
            case 0xd4: return String.format("%02X: da a", instruction);
            case 0x46:  // orl a, ri
            case 0x47: return String.format("%02X: orl a, @R%d", instruction, instruction%2);
            case 0x44: return String.format("%02X: orl a, #0x%02X", instruction, code.get(address + 1).toNumber());
            case 0x85: return String.format("%02X: mov 0x%02X, 0x%02X", instruction, code.get(address + 2).toNumber(), code.get(address + 1).toNumber());
            case 0x86:
            case 0x87: return String.format("%02X: mov 0x%02X, @R%d}", instruction, code.get(address + 1).toNumber(), instruction % 2);
            case 0xb6:
            case 0xb7: return String.format("%02X: cjne @R%d, #0x%02X, 0x%04X", instruction, instruction % 2, code.get(address + 1).toNumber(), code.get(address + 2).toNumber()+pc.toNumber()+2);
            default:
                return String.format("%02X: unknown opcode", instruction);
        }
    }

    public boolean parity(DataType dataType) {
        boolean res = false;
        for(int i = 0; i < dataType.length(); i++) res ^= dataType.getOne(i).getValue() == 1;
        return res;
    }

    private String pswToString() {
        return "[" +
                (getPsw().getOne(pswBit.P.getAddres()).getValue() == 1 ? "P" : "_") +
                (getPsw().getOne(pswBit.UD.getAddres()).getValue() == 1 ? "U" : "_") +
                (getPsw().getOne(pswBit.OV.getAddres()).getValue() == 1 ? "O" : "_") +
                (getPsw().getOne(pswBit.F0.getAddres()).getValue() == 1 ? "F" : "_") +
                (getPsw().getOne(pswBit.AC.getAddres()).getValue() == 1 ? "A" : "_") +
                (getPsw().getOne(pswBit.C.getAddres()).getValue() == 1 ? "C" : "_") +
                " Bank#" +
                getPsw().get(pswBit.RS0.getAddres(), pswBit.RS1.getAddres()).toNumber() +
                "]";
    }

    public String regsToString() {
        return String.format("Acc = 0x%02X\nB = 0x%02X\nSP = 0x%02X\nPsw = %s\nPC = 0x%04X\nDPTR = 0x%04X",
                getAcc().toNumber(), getB().toNumber(), getSp().toNumber(), pswToString(), pc.toNumber(), getDptr().toNumber());
    }

    public String internalRegsToString() {
        return String.format("Wrk1 = 0x%02X\nWrk2 = 0x%02X\nIR = 0x%02X\nRA = 0x%02X\nRB = 0x%02X",
                wrk1.toNumber(), wrk2.toNumber(), ir.toNumber(), ra, rb);
    }

    public String bankToString() {
        StringBuilder stringBuilder = new StringBuilder();
        int bank = (int) getPsw().get(pswBit.RS0.getAddres(), pswBit.RS1.getAddres()).toNumber();

        for(int i = 0; i < 8; i++) {
            stringBuilder.append(String.format("R%d = 0x%02X ", i, data.get(bank * 8 + i).toNumber()));
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
