use std::collections::HashSet;

#[derive(Debug)]
pub struct Instruction {
    pub symbol: i32,
    pub operand1: i32,
    pub operand2: i32,
    pub result: i32,
}

#[derive(Eq, PartialEq, Debug)]
pub struct RegistersState {
    pub a: i32,
    pub b: i32,
    pub c: i32,
    pub d: i32,
}

impl RegistersState {
    pub fn eq(&self, other: &RegistersState) -> bool {
        return self.a == other.a &&
            self.b == other.b &&
            self.c == other.c &&
            self.d == other.d;
    }

    pub fn read(&self, n: i32) -> i32 {
        match n {
            0 => self.a,
            1 => self.b,
            2 => self.c,
            3 => self.d,
            _ => panic!("invalid register")
        }
    }

    pub fn write(&self, n: i32, val: i32) -> RegistersState {
        match n {
            0 => RegistersState { a: val, b: self.b, c: self.c, d: self.d },
            1 => RegistersState { a: self.a, b: val, c: self.c, d: self.d },
            2 => RegistersState { a: self.a, b: self.b, c: val, d: self.d },
            3 => RegistersState { a: self.a, b: self.b, c: self.c, d: val },
            _ => panic!("invalid register")
        }
    }
}

#[derive(Eq, PartialEq, Hash, Clone, Debug)]
pub enum Operation {
    Addr,
    Addi,
    Mulr,
    Muli,
    Banr,
    Bani,
    Borr,
    Bori,
    Setr,
    Seti,
    Gtir,
    Gtri,
    Gtrr,
    Eqir,
    Eqri,
    Eqrr,
}


fn addr(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    registers.write(
        instruction.result,
        registers.read(instruction.operand1) + registers.read(instruction.operand2),
    )
}

fn addi(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    registers.write(
        instruction.result,
        registers.read(instruction.operand1) + instruction.operand2,
    )
}

fn mulr(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    registers.write(
        instruction.result,
        registers.read(instruction.operand1) * registers.read(instruction.operand2),
    )
}

fn muli(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    registers.write(
        instruction.result,
        registers.read(instruction.operand1) * instruction.operand2,
    )
}

fn banr(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    registers.write(
        instruction.result,
        registers.read(instruction.operand1) & registers.read(instruction.operand2),
    )
}

fn bani(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    registers.write(
        instruction.result,
        registers.read(instruction.operand1) & instruction.operand2,
    )
}

fn borr(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    registers.write(
        instruction.result,
        registers.read(instruction.operand1) | registers.read(instruction.operand2),
    )
}

fn bori(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    registers.write(
        instruction.result,
        registers.read(instruction.operand1) | instruction.operand2,
    )
}

fn setr(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    registers.write(
        instruction.result,
        registers.read(instruction.operand1),
    )
}

fn seti(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    registers.write(
        instruction.result,
        instruction.operand1,
    )
}

fn gtir(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    if instruction.operand1 > registers.read(instruction.operand2) {
        return registers.write(instruction.result, 1);
    } else {
        return registers.write(instruction.result, 0);
    }
}

fn gtri(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    if registers.read(instruction.operand1) > instruction.operand2 {
        return registers.write(instruction.result, 1);
    } else {
        return registers.write(instruction.result, 0);
    }
}

fn gtrr(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    if registers.read(instruction.operand1) > registers.read(instruction.operand2) {
        return registers.write(instruction.result, 1);
    } else {
        return registers.write(instruction.result, 0);
    }
}

fn eqir(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    if instruction.operand1 == registers.read(instruction.operand2) {
        return registers.write(instruction.result, 1);
    } else {
        return registers.write(instruction.result, 0);
    }
}

fn eqri(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    if registers.read(instruction.operand1) == instruction.operand2 {
        return registers.write(instruction.result, 1);
    } else {
        return registers.write(instruction.result, 0);
    }
}

fn eqrr(registers: &RegistersState, instruction: &Instruction) -> RegistersState {
    if registers.read(instruction.operand1) == registers.read(instruction.operand2) {
        return registers.write(instruction.result, 1);
    } else {
        return registers.write(instruction.result, 0);
    }
}

pub fn find_operations_giving_result(instruction: &Instruction,
                                     initial: &RegistersState,
                                     after: &RegistersState) -> HashSet<Operation> {
    let mut matching = HashSet::new();

    if addr(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Addr);
    }
    if addi(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Addi);
    }
    if muli(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Muli);
    }
    if mulr(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Mulr);
    }
    if banr(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Banr);
    }
    if bani(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Bani);
    }
    if borr(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Borr);
    }
    if bori(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Bori);
    }
    if setr(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Setr);
    }
    if seti(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Seti);
    }
    if gtir(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Gtir);
    }
    if gtri(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Gtri);
    }
    if gtrr(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Gtrr);
    }
    if eqir(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Eqir);
    }
    if eqri(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Eqri);
    }
    if eqrr(&initial, &instruction).eq(&after) {
        matching.insert(Operation::Eqrr);
    }

    matching
}

pub fn execute_instruction(operation: &Operation, instruction: &Instruction, state: &RegistersState) -> RegistersState {
    match operation {
        Operation::Addr => addr(state, instruction),
        Operation::Addi => addi(state, instruction),
        Operation::Mulr => mulr(state, instruction),
        Operation::Muli => muli(state, instruction),
        Operation::Banr => banr(state, instruction),
        Operation::Bani => bani(state, instruction),
        Operation::Borr => borr(state, instruction),
        Operation::Bori => bori(state, instruction),
        Operation::Setr => setr(state, instruction),
        Operation::Seti => seti(state, instruction),
        Operation::Gtir => gtir(state, instruction),
        Operation::Gtri => gtri(state, instruction),
        Operation::Gtrr => gtrr(state, instruction),
        Operation::Eqir => eqir(state, instruction),
        Operation::Eqri => eqri(state, instruction),
        Operation::Eqrr => eqrr(state, instruction)
    }
}
