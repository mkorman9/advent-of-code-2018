#[macro_use]
extern crate lazy_static;
extern crate regex;

use std::collections::HashMap;
use std::collections::HashSet;

mod instructions;
mod input;

fn main() {
    let input = input::parse_input("input.txt").unwrap();
    subtask1(&input);
    subtask2(&input);
}

fn subtask1(input: &input::Input) {
    let mut sum = 0;

    for example in input.examples.iter() {
        let matches: HashSet<instructions::Operation> = instructions::find_operations_giving_result(
            &example.instruction,
            &example.before,
            &example.after,
        );

        if matches.len() >= 3 {
            sum += 1;
        }
    }

    println!("Subtask #1: {}", sum);
}

fn subtask2(input: &input::Input) {
    let instruction_set: HashMap<i32, instructions::Operation> = deduct_instruction_set(input);
    let mut state = instructions::RegistersState { a: 0, b: 0, c: 0, d: 0 };

    for instruction in input.instructions.iter() {
        let operation = instruction_set.get(&instruction.symbol).unwrap();
        state = instructions::execute_instruction(operation, instruction, &state);
    }

    println!("Subtask #2: {}", state.a);
}

fn deduct_instruction_set(input: &input::Input) -> HashMap<i32, instructions::Operation> {
    let mut possible_meanings: HashMap<i32, HashSet<instructions::Operation>> = HashMap::new();
    let mut unique_meanings: HashMap<i32, instructions::Operation> = HashMap::new();

    for example in input.examples.iter() {
        let matches: HashSet<instructions::Operation> = instructions::find_operations_giving_result(
            &example.instruction,
            &example.before,
            &example.after,
        );

        if !possible_meanings.contains_key(&example.instruction.symbol) {
            possible_meanings.insert(example.instruction.symbol, matches);
        } else {
            let meanings: &HashSet<instructions::Operation> = possible_meanings.get(&example.instruction.symbol).unwrap();
            possible_meanings.insert(
                example.instruction.symbol,
                matches.intersection(meanings).cloned().collect(),
            );
        }
    }

    loop {
        for (opcode, possible_operations) in possible_meanings.iter() {
            if possible_operations.len() == 1 {
                unique_meanings.insert(*opcode, possible_operations.iter().cloned().last().unwrap());
            }
        }

        for (_, unique_operation) in unique_meanings.iter() {
            for (_, possible_operations) in possible_meanings.iter_mut() {
                let mut op = HashSet::new();
                op.insert(unique_operation.clone());
                *possible_operations = possible_operations.difference(&op).cloned().collect();
            }
        }

        if unique_meanings.len() == possible_meanings.len() {
            break;
        }
    }

    unique_meanings
}
