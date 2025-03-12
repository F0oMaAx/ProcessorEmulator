import java.util.HashMap;
import java.util.Map;

public class Registers {
    private final Map<String, Long> registers = new HashMap<>();
    private final Map<String, Boolean> flags = new HashMap<>();

    public Registers() {
        // 1. General-purpose registers (GPR)
        String[] registers64 = {"RAX", "RBX", "RCX", "RDX", "RSI", "RDI", "RBP", "RSP", "R8", "R9", "R10", "R11", "R12", "R13", "R14", "R15"};
        String[] registers32 = {"EAX", "EBX", "ECX", "EDX", "ESI", "EDI", "EBP", "ESP", "R8D", "R9D", "R10D", "R11D", "R12D", "R13D", "R14D", "R15D"};
        String[] registers16 = {"AX", "BX", "CX", "DX", "SI", "DI", "BP", "SP", "R8W", "R9W", "R10W", "R11W", "R12W", "R13W", "R14W", "R15W"};
        String[] registers8L = {"AL", "BL", "CL", "DL", "SIL", "DIL", "BPL", "SPL", "R8B", "R9B", "R10B", "R11B", "R12B", "R13B", "R14B", "R15B"};
        String[] registers8H = {"AH", "BH", "CH", "DH"}; // Only for AX, BX, CX, DX

        // 2. Segment registers
        String[] segmentRegisters = {"CS", "DS", "SS", "ES", "FS", "GS"};

        // 3. Instruction pointer and flags
        String[] instructionPointer = {"RIP"};
        String[] flags = {"RFLAGS"};

        // 4. Control registers (CR)
        String[] controlRegisters = {"CR0", "CR2", "CR3", "CR4", "CR8"};

        // 5. Debug registers (DR)
        String[] debugRegisters = {"DR0", "DR1", "DR2", "DR3", "DR6", "DR7"};

        // 6. FPU registers
        String[] fpuRegisters = {"ST0", "ST1", "ST2", "ST3", "ST4", "ST5", "ST6", "ST7"};
        String[] fpuControlRegisters = {"FPU_Control", "FPU_Status", "FPU_Tag"};

        // 7. SIMD registers (MMX, SSE, AVX, AVX-512)
        String[] mmxRegisters = {"MM0", "MM1", "MM2", "MM3", "MM4", "MM5", "MM6", "MM7"};
        String[] xmmRegisters = {"XMM0", "XMM1", "XMM2", "XMM3", "XMM4", "XMM5", "XMM6", "XMM7",
                "XMM8", "XMM9", "XMM10", "XMM11", "XMM12", "XMM13", "XMM14", "XMM15"};
        String[] ymmRegisters = {"YMM0", "YMM1", "YMM2", "YMM3", "YMM4", "YMM5", "YMM6", "YMM7",
                "YMM8", "YMM9", "YMM10", "YMM11", "YMM12", "YMM13", "YMM14", "YMM15"};
        String[] zmmRegisters = {"ZMM0", "ZMM1", "ZMM2", "ZMM3", "ZMM4", "ZMM5", "ZMM6", "ZMM7",
                "ZMM8", "ZMM9", "ZMM10", "ZMM11", "ZMM12", "ZMM13", "ZMM14", "ZMM15"};

        // 8. Model-Specific Registers (MSR)
        String[] modelSpecificRegisters = {"MSR"};

        // Initialize all registers with value 0
        for (String reg : registers64) registers.put(reg, 0L);
        for (String reg : registers32) registers.put(reg, 0L);
        for (String reg : registers16) registers.put(reg, 0L);
        for (String reg : registers8L) registers.put(reg, 0L);
        for (String reg : registers8H) registers.put(reg, 0L);
        for (String reg : segmentRegisters) registers.put(reg, 0L);
        for (String reg : instructionPointer) registers.put(reg, 0L);
        for (String reg : flags) registers.put(reg, 0L);
        for (String reg : controlRegisters) registers.put(reg, 0L);
        for (String reg : debugRegisters) registers.put(reg, 0L);
        for (String reg : fpuRegisters) registers.put(reg, 0L);
        for (String reg : fpuControlRegisters) registers.put(reg, 0L);
        for (String reg : mmxRegisters) registers.put(reg, 0L);
        for (String reg : xmmRegisters) registers.put(reg, 0L);
        for (String reg : ymmRegisters) registers.put(reg, 0L);
        for (String reg : zmmRegisters) registers.put(reg, 0L);
        for (String reg : modelSpecificRegisters) registers.put(reg, 0L);
    }

    // Set register value
    public void setRegister(String reg, long value) {
        if (registers.containsKey(reg)) {
            registers.put(reg, value);
        } else {
            throw new IllegalArgumentException("Unknown register: " + reg);
        }
    }

    // Get register value
    public long getRegister(String reg) {
        if (registers.containsKey(reg)) {
            return registers.get(reg);
        } else {
            throw new IllegalArgumentException("Unknown register: " + reg);
        }
    }

    // Set flag value
    public void setFlag(String flag, boolean value) {
        flags.put(flag, value);
    }

    // Get flag value
    public boolean getFlag(String flag) {
        return flags.getOrDefault(flag, false);
    }

    // Print a dump of all registers
    public void dumpRegisters() {
        System.out.println("==== CPU Registers ====");
        for (Map.Entry<String, Long> entry : registers.entrySet()) {
            System.out.printf("%-10s: %016X%n", entry.getKey(), entry.getValue());
        }
    }
}
