import java.util.Arrays;

public class CPU {
    private final Registers registers = new Registers(); // Регистры процессора
    private final Memory memory; // ОЗУ процессора
    private int stackPointer; // Указатель стека (SP)
    private final int memorySize; // Размер памяти

    /**
     * Конструктор процессора.
     * @param memorySize Размер ОЗУ.
     * @param stackSize Размер выделенной области под стек.
     */
    public CPU(int memorySize, int stackSize) {
        this.memory = new Memory(memorySize);
        int stackBase = memorySize - stackSize; // Определяем начало стека (растёт вниз)
        registers.setRegister("SS", stackBase); // Устанавливаем регистр SS (Stack Segment)
        this.stackPointer = stackBase; // Инициализируем указатель стека
        this.memorySize = memorySize;
    }

    /**
     * Выполняет одну инструкцию, переданную в виде строки.
     * @param instruction Строка с инструкцией (например, "PUSH 10").
     */
    public void executeInstruction(String instruction) {
        execute(instruction);
    }

    /**
     * Выводит текущее состояние регистров.
     */
    public void dumpRegisters() {
        registers.dumpRegisters();
    }

    /**
     * Разбирает и выполняет переданную команду.
     * @param command Инструкция в виде строки.
     */
    private void execute(String command) {
        command = command.replace(",", ""); // Убираем запятые (если есть)
        String[] parts = command.split("\\s+"); // Разделяем строку на части

        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }

        String instruction = parts[0].toUpperCase(); // Опкод команды
        String[] operands = Arrays.copyOfRange(parts, 1, parts.length); // Операнды

        // Выполняем инструкцию
        switch (instruction) {
            case "MOV":
                if (operands.length != 2) throw new IllegalArgumentException("MOV требует 2 операнда");
                mov(operands[0], operands[1]);
                break;
            case "ADD":
                if (operands.length != 2) throw new IllegalArgumentException("ADD требует 2 операнда");
                add(operands[0], operands[1]);
                break;
            case "SUB":
                if (operands.length != 2) throw new IllegalArgumentException("SUB требует 2 операнда");
                sub(operands[0], operands[1]);
                break;
            case "AND":
                if (operands.length != 2) throw new IllegalArgumentException("AND требует 2 операнда");
                and(operands[0], operands[1]);
                break;
            case "OR":
                if (operands.length != 2) throw new IllegalArgumentException("OR требует 2 операнда");
                or(operands[0], operands[1]);
                break;
            case "MUL":
                if (operands.length != 2) throw new IllegalArgumentException("MUL требует 2 операнда");
                mul(operands[0], operands[1]);
                break;
            case "DIV":
                if (operands.length != 2) throw new IllegalArgumentException("DIV требует 2 операнда");
                div(operands[0], operands[1]);
                break;
            case "RDUMP":
                registers.dumpRegisters();
                break;
            case "PUSH":
                if (operands.length != 1) throw new IllegalArgumentException("PUSH требует 1 операнд");
                push(operands[0]);
                break;
            case "POP":
                if (operands.length != 1) throw new IllegalArgumentException("POP требует 1 операнд");
                pop(operands[0]);
                break;
            default:
                throw new IllegalArgumentException("Неизвестная инструкция: " + instruction);
        }
    }

    /**
     * MOV - копирует значение в регистр.
     */
    private void mov(String dest, String src) {
        if (!isRegister(dest)) {
            throw new IllegalArgumentException("Назначение должно быть регистром: " + dest);
        }
        registers.setRegister(dest, parseValue(src));
    }

    /**
     * Арифметические и логические операции.
     */
    private void add(String dest, String src) {
        registers.setRegister(dest, registers.getRegister(dest) + parseValue(src));
    }

    private void sub(String dest, String src) {
        registers.setRegister(dest, registers.getRegister(dest) - parseValue(src));
    }

    private void and(String dest, String src) {
        registers.setRegister(dest, registers.getRegister(dest) & parseValue(src));
    }

    private void or(String dest, String src) {
        registers.setRegister(dest, registers.getRegister(dest) | parseValue(src));
    }

    private void mul(String dest, String src) {
        registers.setRegister(dest, registers.getRegister(dest) * parseValue(src));
    }

    private void div(String dest, String src) {
        long divisor = parseValue(src);
        if (divisor == 0) {
            throw new ArithmeticException("Деление на ноль");
        }
        registers.setRegister(dest, registers.getRegister(dest) / divisor);
    }

    /**
     * Кладёт значение в стек.
     */
    private void push(String src) {
        long value = parseValue(src); // Получаем значение (число или из регистра)
        int ss = (int) registers.getRegister("SS"); // Получаем базовый адрес стека (SS)

        // Проверка на переполнение стека
        if (stackPointer - 8 < ss) {
            throw new IllegalStateException("Stack overflow");
        }

        // Записываем 8 байтов (64-битное значение) в память
        for (int i = 0; i < 8; i++) {
            memory.write(stackPointer - i, (byte) (value >> (i * 8)));
        }

        stackPointer -= 8; // Смещаем стек вниз
    }

    /**
     * Извлекает значение из стека и записывает его в регистр.
     */
    private void pop(String dest) {
        int ss = (int) registers.getRegister("SS"); // Получаем базовый адрес стека (SS)

        // Проверка на выход за границы стека
        if (stackPointer + 8 >= ss + memorySize) {
            throw new IllegalStateException("Stack underflow");
        }

        long value = 0;

        // Читаем 8 байтов из памяти и восстанавливаем 64-битное значение
        for (int i = 0; i < 8; i++) {
            value |= ((long) memory.read(stackPointer + i) & 0xFF) << (i * 8);
        }

        stackPointer += 8; // Смещаем стек вверх
        registers.setRegister(dest, value); // Записываем значение в регистр
    }

    /**
     * Преобразует операнд в числовое значение.
     */
    private long parseValue(String operand) {
        if (operand.startsWith("0x")) {
            return Long.parseUnsignedLong(operand.substring(2), 16); // Читаем 16-ричное число
        } else if (operand.matches("\\d+")) {
            return Long.parseLong(operand); // Число в десятичной системе
        } else if (isRegister(operand)) {
            return registers.getRegister(operand); // Значение из регистра
        } else {
            throw new IllegalArgumentException("Неизвестный операнд: " + operand);
        }
    }

    /**
     * Проверяет, является ли строка именем регистра.
     */
    private boolean isRegister(String operand) {
        return operand.matches("[A-Z]+[0-9]*");
    }
}
