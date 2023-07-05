//Problem 1: add jump functions
//(SOLVED) Problem 2: programm is reading directly from file, first fill memory from file, after exicute programm from memory
//Problem 3: exexute function must test if jump or Not write only 1 operand


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

public class CPU {
    private final Map<String, Byte> registers; // values

    private final Map<String, Byte> regs; // IDs
    private final Map<String, Byte> insts;
    private final Map<Character, Byte> oper_t;
    private final byte[] memory;
//    private final byte programCounter;

    public CPU() {
        // registers - values
        registers = new HashMap<>();
        registers.put("AYB", (byte) 0);
        registers.put("BEN", (byte) 0);
        registers.put("GIM", (byte) 0);
        registers.put("DA", (byte) 0);
        registers.put("EC", (byte) 0);
        registers.put("ZA", (byte) 0);


        // instructions - IDs
        insts = new HashMap<>();
        insts.put("MOV", (byte) 0);
        insts.put("ADD", (byte) 1);
        insts.put("SUB", (byte) 2);
        insts.put("MUL", (byte) 3);
        insts.put("DIV", (byte) 4);
        insts.put("AND", (byte) 5);
        insts.put("OR", (byte) 6);
        insts.put("XOR", (byte) 7);
        insts.put("CMP", (byte) 8);
        insts.put("JMP", (byte) 9);
        insts.put("JG", (byte) 10);
        insts.put("JL", (byte) 11);
        insts.put("JE", (byte) 12);
        insts.put("NOT", (byte) 13);

        // oper types in id
        oper_t = new HashMap<>();
        oper_t.put('R', (byte) 0);
        oper_t.put('A', (byte) 1);
        oper_t.put('L', (byte) 2);

        // regis
        regs = new HashMap<>();
        regs.put("AYB", (byte) 0);
        regs.put("BEN", (byte) 1);
        regs.put("GIM", (byte) 2);
        regs.put("DA", (byte) 3);
        regs.put("EC", (byte) 4);
        regs.put("ZA", (byte) 5);

        memory = new byte[32]; // Memory size of 32 bytes
//        programCounter = 0;
    }

    public static <T, E> T getKey(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String corrLValue(String in) {
        if (in.matches("[A-Z]+") && registers.containsKey(in)) {
            return "R:" + in;
        } else if (in.charAt(0) == '[' && in.charAt(in.length() - 1) == ']') {
            byte addr_byte = 34;
            try {
                in = in.substring(1, in.length() - 1);
                if (!in.matches("\\d+")) {
                    return null;
                }
                addr_byte = Byte.parseByte(in);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + in + " is not an address");
            }
            if (addr_byte > (byte) 4 && addr_byte <= (byte) 32) {
                return "A:" + addr_byte;
            } else {
                System.out.println("Invalid input: " + in + " is out of bounds");
            }
        } else if (in.matches("\\d+")) {
            return "L:" + in;
        }
        return null;
    }

    public String corrRValue(String in) {
        if (in.matches("[A-Z]+") && registers.containsKey(in)) {
            return "R:" + in;
        } else if (in.charAt(0) == '[' && in.charAt(in.length() - 1) == ']') {
            byte addr_byte = 34;
            try {
                in = in.substring(1, in.length() - 1);
                if (!in.matches("\\d+")) {
                    return null;
                }
                addr_byte = Byte.parseByte(in);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + in + " is not an address");
            }
            if (addr_byte > (byte) 4 && addr_byte <= (byte) 32) {
                return "A:" + addr_byte;
            } else {
                System.out.println("Invalid input: " + in + " is out of bounds");
            }
        } else if (in.matches("\\d+")) {
            return "L:" + in;
        }
        return null;
    }

    public void dumpMemory() {
        System.out.println("Memory dump:");
        for (int i = 0; i < memory.length; i++) {
            System.out.println("Address " + i + ": " + memory[i]);
        }
    }

    public void printRegisters() {
        System.out.println("Registers:");
        for (Map.Entry<String, Byte> entry : registers.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public void writeMemory(byte address, byte value) {
        if (address > 4 && address < memory.length) {
            memory[address] = value;
        } else {
            System.out.println("Invalid memory address: " + address);
        }
    }

    public byte readMemory(byte address) {
        if (address > 4 && address < memory.length) {
            return memory[address];
        } else {
            System.out.println("Invalid memory address: " + address);
            return 0;
        }
    }

    public boolean MOV(byte l_t, byte l_v, byte r_t, byte r_v) {

        switch (l_t) {
            case 0: // register
                String l_key = getKey(regs, l_v);
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    registers.put(l_key, registers.get(r_key));
                    return registers.get(l_key) == registers.get(r_key);
                }
                if (r_t == 1) {// addr
                    registers.put(l_key, readMemory(r_v));
                    return registers.get(l_key) == readMemory(r_v);
                }
                if (r_t == 2) { // literal
                    registers.put(l_key, r_v);
                    return registers.get(l_key) == r_v;
                }
                break;
            case 1: // address
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    writeMemory(l_v, registers.get(r_key));
                    return readMemory(l_v) == registers.get(r_key);
                }
                if (r_t == 1) {// addr

                    writeMemory(l_v, readMemory(r_v));
                    return readMemory(l_v) == readMemory(r_v);
                }
                if (r_t == 2) { // literal
                    writeMemory(l_v, r_v);
                    return readMemory(l_v) == r_v;
                }
                break;
        }
        return false;
    }

    public boolean ADD(byte l_t, byte l_v, byte r_t, byte r_v) {
        byte res;
        switch (l_t) {
            case 0: // register
                String l_key = getKey(regs, l_v);
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (registers.get(l_key) + registers.get(r_key));
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (registers.get(l_key) + readMemory(r_v));
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (registers.get(l_key) + r_v);
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                break;
            case 1: // address
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (readMemory(l_v) + registers.get(r_key));
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (readMemory(l_v) + readMemory(r_v));
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (readMemory(l_v) + r_v);
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                break;
        }
        return false;
    }

    public boolean SUB(byte l_t, byte l_v, byte r_t, byte r_v) {
        byte res;
        switch (l_t) {
            case 0: // register
                String l_key = getKey(regs, l_v);
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (registers.get(l_key) - registers.get(r_key));
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (registers.get(l_key) - readMemory(r_v));
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (registers.get(l_key) - r_v);
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                break;
            case 1: // address
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (readMemory(l_v) - registers.get(r_key));
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (readMemory(l_v) - readMemory(r_v));
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (readMemory(l_v) - r_v);
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                break;
        }
        return false;
    }

    public boolean MUL(byte l_t, byte l_v, byte r_t, byte r_v) {
        byte res;
        switch (l_t) {
            case 0: // register
                String l_key = getKey(regs, l_v);
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (registers.get(l_key) * registers.get(r_key));
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (registers.get(l_key) * readMemory(r_v));
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (registers.get(l_key) * r_v);
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                break;
            case 1: // address
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (readMemory(l_v) * registers.get(r_key));
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (readMemory(l_v) * readMemory(r_v));
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (readMemory(l_v) * r_v);
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                break;
        }
        return false;
    }

    public boolean DIV(byte l_t, byte l_v, byte r_t, byte r_v) {
        byte res;
        switch (l_t) {
            case 0: // register
                String l_key = getKey(regs, l_v);
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (registers.get(l_key) / registers.get(r_key));
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (registers.get(l_key) / readMemory(r_v));
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (registers.get(l_key) / r_v);
                    registers.put(l_key, res);
                    registers.put("AYB", res);
                    return registers.get(l_key) == res;
                }
                break;
            case 1: // address
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (readMemory(l_v) / registers.get(r_key));
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (readMemory(l_v) / readMemory(r_v));
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (readMemory(l_v) / r_v);
                    writeMemory(l_v, res);
                    registers.put("AYB", res);
                    return readMemory(l_v) == res;
                }
                break;
        }
        return false;
    }

    public boolean AND(byte l_t, byte l_v, byte r_t, byte r_v) {
        byte res;
        switch (l_t) {
            case 0: // register
                String l_key = getKey(regs, l_v);
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (registers.get(l_key) & registers.get(r_key));
                    registers.put(l_key, res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (registers.get(l_key) & readMemory(r_v));
                    registers.put(l_key, res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (registers.get(l_key) & r_v);
                    registers.put(l_key, res);
                    return registers.get(l_key) == res;
                }
                break;
            case 1: // address
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (readMemory(l_v) & registers.get(r_key));
                    writeMemory(l_v, res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (readMemory(l_v) & readMemory(r_v));
                    writeMemory(l_v, res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (readMemory(l_v) & r_v);
                    writeMemory(l_v, res);
                    return readMemory(l_v) == res;
                }
                break;
        }
        return false;
    }

    public boolean OR(byte l_t, byte l_v, byte r_t, byte r_v) {
        byte res;
        switch (l_t) {
            case 0: // register
                String l_key = getKey(regs, l_v);
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (registers.get(l_key) | registers.get(r_key));
                    registers.put(l_key, res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (registers.get(l_key) | readMemory(r_v));
                    registers.put(l_key, res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (registers.get(l_key) | r_v);
                    registers.put(l_key, res);
                    return registers.get(l_key) == res;
                }
                break;
            case 1: // address
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (readMemory(l_v) | registers.get(r_key));
                    writeMemory(l_v, res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (readMemory(l_v) | readMemory(r_v));
                    writeMemory(l_v, res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (readMemory(l_v) | r_v);
                    writeMemory(l_v, res);
                    return readMemory(l_v) == res;
                }
                break;
        }
        return false;
    }

    public boolean XOR(byte l_t, byte l_v, byte r_t, byte r_v) {
        byte res;
        switch (l_t) {
            case 0: // register
                String l_key = getKey(regs, l_v);
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (registers.get(l_key) ^ registers.get(r_key));
                    registers.put(l_key, res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (registers.get(l_key) ^ readMemory(r_v));
                    registers.put(l_key, res);
                    return registers.get(l_key) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (registers.get(l_key) ^ r_v);
                    registers.put(l_key, res);
                    return registers.get(l_key) == res;
                }
                break;
            case 1: // address
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    res = (byte) (readMemory(l_v) ^ registers.get(r_key));
                    writeMemory(l_v, res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 1) {// addr
                    res = (byte) (readMemory(l_v) ^ readMemory(r_v));
                    writeMemory(l_v, res);
                    return readMemory(l_v) == res;
                }
                if (r_t == 2) { // literal
                    res = (byte) (readMemory(l_v) ^ r_v);
                    writeMemory(l_v, res);
                    return readMemory(l_v) == res;
                }
                break;
        }
        return false;
    }

    public boolean CMP(byte l_t, byte l_v, byte r_t, byte r_v) {
        switch (l_t) {
            case 0: // register
                if (r_t == 0) {
                    String l_key = getKey(regs, l_v);
                    String r_key = getKey(regs, r_v);
                    if (registers.get(l_key) > registers.get(r_key)) {
                        registers.put("DA", (byte) 1);
                    }
                    if (registers.get(l_key) < registers.get(r_key)) {
                        registers.put("DA", (byte) -1);
                    }
                    if (registers.get(l_key) == registers.get(r_key)) {
                        registers.put("DA", (byte) 0);
                    }
                    return true;

                }
                if (r_t == 1) {
                    String l_key = getKey(regs, l_v);

                    if (registers.get(l_key) > readMemory(r_v)) {
                        registers.put("DA", (byte) 1);
                    }
                    if (registers.get(l_key) < readMemory(r_v)) {
                        registers.put("DA", (byte) -1);
                    }
                    if (registers.get(l_key) == readMemory(r_v)) {
                        registers.put("DA", (byte) 0);
                    }
                    return true;

                }
                if (r_t == 2) {
                    String l_key = getKey(regs, l_v);

                    if (registers.get(l_key) > r_v) {
                        registers.put("DA", (byte) 1);
                    }
                    if (registers.get(l_key) < r_v) {
                        registers.put("DA", (byte) -1);
                    }
                    if (registers.get(l_key) == r_v) {
                        registers.put("DA", (byte) 0);
                    }
                    return true;

                }
                break;

            case 1: // address
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    if (readMemory(l_v) > registers.get(r_key)) {
                        registers.put("DA", (byte) 1);
                    }
                    if (readMemory(l_v) < registers.get(r_key)) {
                        registers.put("DA", (byte) -1);
                    }
                    if (readMemory(l_v) == registers.get(r_key)) {
                        registers.put("DA", (byte) 0);
                    }
                    return true;

                }
                if (r_t == 1) {
                    if (readMemory(l_v) > readMemory(r_v)) {
                        registers.put("DA", (byte) 1);
                    }
                    if (readMemory(l_v) < readMemory(r_v)) {
                        registers.put("DA", (byte) -1);
                    }
                    if (readMemory(l_v) == readMemory(r_v)) {
                        registers.put("DA", (byte) 0);
                    }
                    return true;

                }
                if (r_t == 2) {
                    if (readMemory(l_v) > r_v) {
                        registers.put("DA", (byte) 1);
                    }
                    if (readMemory(l_v) < r_v) {
                        registers.put("DA", (byte) -1);
                    }
                    if (readMemory(l_v) == r_v) {
                        registers.put("DA", (byte) 0);
                    }
                    return true;

                }
                break;
            case 2: // literal
                if (r_t == 0) {
                    String r_key = getKey(regs, r_v);
                    if (l_v > registers.get(r_key)) {
                        registers.put("DA", (byte) 1);
                    }
                    if (l_v < registers.get(r_key)) {
                        registers.put("DA", (byte) -1);
                    }
                    if (l_v == registers.get(r_key)) {
                        registers.put("DA", (byte) 0);
                    }
                    return true;

                }
                if (r_t == 1) {
                    if (l_v > readMemory(r_v)) {
                        registers.put("DA", (byte) 1);
                    }
                    if (l_v < readMemory(r_v)) {
                        registers.put("DA", (byte) -1);
                    }
                    if (l_v == readMemory(r_v)) {
                        registers.put("DA", (byte) 0);
                    }
                    return true;

                }
                if (r_t == 2) {
                    if (l_v > r_v) {
                        registers.put("DA", (byte) 1);
                    }
                    if (l_v < r_v) {
                        registers.put("DA", (byte) -1);
                    }
                    if (l_v == r_v) {
                        registers.put("DA", (byte) 0);
                    }
                    return true;
                }
                break;
        }

        return false;
    }

    public void JMP(int l_i) throws IOException{
        Main.reader.reset();
        for(int i = 1; i < l_i-1; ++i){
            Main.reader.readLine();
        }
            decode(Main.reader.readLine());
    }

    public void JL(int l_i) throws IOException{
        if (registers.get("DA") == -1){
            JMP(l_i);
        }
    }

    public void JG(int l_i) throws IOException{
        if (registers.get("DA") == 1){
            JMP(l_i);
        }
    }

    public void JE(int l_i) throws IOException{
        if (registers.get("DA") == -0){
            JMP(l_i);
        }
    }


    public boolean decode(String line) {

        String[] parts = line.split(" ");
        String inst = parts[0];
        String L_oper = corrLValue(parts[1]);
        if (insts.get(inst) == null || L_oper == null) {
            System.out.println("Invalid instruction!");
            return false;
        }

        char L_type = L_oper.charAt(0);
        String L_value = L_oper.substring(2);

        //werite instruction to memory
        memory[0] = insts.get(inst); // instruction

        memory[1] = oper_t.get(L_type); // left type
        switch (L_type) { // left value
            case 'R':
                memory[2] = regs.get(L_value);
                break;
            case 'A':
            case 'L':
                memory[2] = Byte.parseByte(L_value);
                break;
        }

        if (parts.length == 2 && memory[0] >= 9 && memory[0] <= 13) {
            memory[3] = (byte) 127;
            memory[4] = (byte) 127;
            return true;
        } else if (parts.length == 3) {

            String R_oper = corrRValue(parts[2]);
            char R_type = R_oper.charAt(0);
            String R_value = R_oper.substring(2);

            memory[3] = oper_t.get(R_type); // right type
            switch (R_type) { // right value
                case 'R':
                    memory[4] = regs.get(R_value);
                    break;
                case 'A':
                case 'L':
                    memory[4] = Byte.parseByte(R_value);
                    break;
            }
            return true;
        } else {
            System.out.println("Invalid Instruction");
            return false;
        }
    }

    public void execute() throws IOException {
        byte inst = memory[0];
        byte l_type = memory[1];
        byte l_value = memory[2];
        byte r_type = memory[3];
        byte r_value = memory[4];

        if (r_type == r_value && r_value == 127 && l_type == 2) { //inst length == 2

            switch (inst) {
                case 9: // JMP
                    JMP(l_value);
                    break;
                case 10:// JG
                    JG(l_value);
                    break;
                case 11:// JL
                    JL(l_value);
                    break;
                case 12:// JE
                    JE(l_value);
                    break;
                default://
                    break;
            }
        }

        else {

            switch (inst) { //inst length == 3
                case 0: // MOV
                    MOV(l_type, l_value, r_type, r_value);
                    break;
                case 1: // ADD
                    ADD(l_type, l_value, r_type, r_value);
                    break;
                case 2: // SUB
                    SUB(l_type, l_value, r_type, r_value);
                    break;
                case 3: // MUL
                    MUL(l_type, l_value, r_type, r_value);
                    break;
                case 4: // DIV
                    DIV(l_type, l_value, r_type, r_value);
                    break;
                case 5: // AND
                    AND(l_type, l_value, r_type, r_value);
                    break;
                case 6: // OR
                    OR(l_type, l_value, r_type, r_value);
                    break;
                case 7: // XOR
                    XOR(l_type, l_value, r_type, r_value);
                    break;
                case 8: // CMP
                    CMP(l_type, l_value, r_type, r_value);
                    break;
                default:
                    break;
            }
        }
    }
} // end of class
