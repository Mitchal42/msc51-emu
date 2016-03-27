package ru.savushkin.emu;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Application {
    public static void main(String [] args)
    {
        String action;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Msc51 cpu = new Msc51();
        String menu = "Меню:\n" +
                "1 - Загрузить файл в сегмент code\n" +
                "2 - Отобразить сегменты\n" +
                "3 - Выполнить инструкцию на которую указывает регистр PS\n" +
                "4 - Выполнить инструкцию на которую указывает регистр PS пошагово\n" +
                "7 - Установить регистр\n" +
                "8 - Вывести состояние\n" +
                "9 - Выход\n" +
                "? - Вывести меню\n";
        System.out.print(menu);
        while (true) {
            System.out.print("$ ");
            try {
                action = br.readLine();
                switch (action) {
                    case "?":
                        System.out.print(menu);
                        break;
                    case "1":
                        System.out.println("Введите путь к файлу:");
                        String path = br.readLine();
                        if(new File(path).exists()) {
                            List<Long> code = new LinkedList<>();
                            BufferedReader reader = new BufferedReader(new FileReader(path));
                            try {
                                while (reader.ready()) {
                                    code.add(Long.decode(reader.readLine()));
                                }
                            } catch (NumberFormatException ex) {
                                System.out.println("Неверный формат входного файла");
                                continue;
                            }
                            cpu.setCode(code);
                        } else
                            System.out.println(path + " - не существует");
                        break;
                    case "2":
                        System.out.println("Выберите сегмент(code - 0, data - 1, xData - 3):");
                        switch (br.readLine()) {
                            case "0":
                                System.out.println("code:");
                                for(int i = 0; i < 256; i++)
                                    System.out.printf("0x%02X: %s (0x%02X)\n",
                                            i, cpu.getCode().get(i).toBinaryString(), cpu.getCode().get(i).toNumber());
                                break;
                            case "1":
                                System.out.println("data:");
                                for(int i = 0; i < 256; i++)
                                    System.out.printf("0x%02X: %s (0x%02X)\n",
                                            i, cpu.getData().get(i).toBinaryString(), cpu.getData().get(i).toNumber());
                                break;
                            case "2":
                                System.out.println("xData:");
                                for(int i = 0; i < 256; i++)
                                    System.out.printf("0x%02X: %s (0x%02X)\n",
                                            i, cpu.getxData().get(i).toBinaryString(), cpu.getxData().get(i).toNumber());
                                break;
                            default:
                                System.out.println("Неизвестный сегмент");
                                break;
                        }
                        break;
                    case "3":
                        System.out.println(cpu.disassemble((int)cpu.getPc().toNumber()));
                        do {
                            cpu.execute();
                        } while (cpu.getCycle() != 0);
                        break;
                    case "4":
                        System.out.println(cpu.disassemble((int)cpu.getPc().toNumber()));
                        cpu.execute();
                        break;
                    case "7":
                        System.out.println("Выберите регистр\n" +
                                "Acc - 0\n" +
                                "B - 1\n" +
                                "SP - 2\n" +
                                "PC - 3\n" +
                                "DPTR - 4\n");
                        try {
                            switch (br.readLine()) {
                                case "0":
                                    cpu.setAcc(cpu.getAcc().setBits(Long.decode(br.readLine())));
                                    break;
                                case "1":
                                    cpu.setB(cpu.getB().setBits(Long.decode(br.readLine())));
                                    break;
                                case "2":
                                    cpu.setSp(cpu.getSp().setBits(Long.decode(br.readLine())));
                                    break;
                                case "3":
                                    cpu.setPc(cpu.getPc().setBits(Long.decode(br.readLine())));
                                    break;
                                case "4":
                                    cpu.setDptr(cpu.getDptr().setBits(Long.decode(br.readLine())));
                                    break;
                                default:
                                    System.out.println("Неизвестный регистр");
                                    break;
                            }
                        } catch (NumberFormatException ex) {
                            System.out.println("Неверный формат числа");
                            continue;
                        }
                        break;
                    case "8":
                        System.out.printf("Внутренние регистры:\n%s\nОсновные регистры:\n%s\nБанк:\n%s",
                                cpu.internalRegsToString(),
                                cpu.regsToString(),
                                cpu.bankToString());
                        break;
                    case "9":
                        return;
                    default:
                        System.out.println("Неизвестная команда - " + action);
                        System.out.print(menu);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
