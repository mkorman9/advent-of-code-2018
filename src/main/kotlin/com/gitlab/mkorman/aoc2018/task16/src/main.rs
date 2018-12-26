#[macro_use]
extern crate lazy_static;
extern crate regex;

use std::collections::HashMap;
use std::fs::File;
use std::io::BufRead;
use std::io::BufReader;
use std::io::Error;

use regex::Regex;
use std::collections::HashSet;

mod instructions;

lazy_static! {
    static ref BEFORE_LINE_REGEX: Regex = Regex::new(r"^Before:\s*\[(\d+), (\d+), (\d+), (\d+)]$").unwrap();
    static ref INSTRUCTION_LINE_REGEX: Regex = Regex::new(r"^(\d+) (\d+) (\d+) (\d+)$").unwrap();
    static ref AFTER_LINE_REGEX: Regex = Regex::new(r"^After:\s*\[(\d+), (\d+), (\d+), (\d+)]$").unwrap();
}

#[derive(Debug)]
struct Example {
    instruction: instructions::Instruction,
    before: instructions::RegistersState,
    after: instructions::RegistersState,
}

#[derive(Debug)]
struct Input {
    examples: Vec<Example>,
    instructions: Vec<instructions::Instruction>,
}

fn main() {
    let input = parse_input().unwrap();
    subtask1(&input);
    subtask2(&input);
}

fn subtask1(input: &Input) {
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

fn subtask2(input: &Input) {
    let instruction_set: HashMap<i32, instructions::Operation> = deduct_instruction_set(input);
    let mut state = instructions::RegistersState { a: 0, b: 0, c: 0, d: 0 };

    for instruction in input.instructions.iter() {
        let operation = instruction_set.get(&instruction.symbol).unwrap();
        state = instructions::execute_instruction(operation, instruction, &state);
    }

    println!("Subtask #2: {}", state.a);
}

fn parse_input() -> Result<Input, Error> {
    let file = File::open("input.txt").expect("cannot open input.txt");
    let mut reader = BufReader::new(&file);

    let mut examples: Vec<Example> = Vec::new();
    let mut instructions: Vec<instructions::Instruction> = Vec::new();

    loop {
        let mut line = String::new();
        match reader.read_line(&mut line) {
            Ok(size) => {
                if size == 0 {
                    break;
                }
            }
            Err(e) => {
                return Err(e);
            }
        }

        line = String::from(line.trim());

        if line.is_empty() {
            continue;
        }

        if line.starts_with("Before:") {
            let mut instruction_line = String::new();
            reader.read_line(&mut instruction_line).unwrap();

            let mut after_line = String::new();
            reader.read_line(&mut after_line).unwrap();

            let example = parse_example(
                line.trim(),
                instruction_line.trim(),
                after_line.trim(),
            );

            examples.push(example);
        } else {
            let instruction = parse_instruction(line);
            instructions.push(instruction);
        }
    }

    Ok(Input {
        examples: examples,
        instructions: instructions,
    })
}

fn parse_example(before_line: &str, instruction_line: &str, after_line: &str) -> Example {
    let mut before_state = instructions::RegistersState { a: 0, b: 0, c: 0, d: 0 };
    let mut instruction = instructions::Instruction { symbol: 0, operand1: 0, operand2: 0, result: 0 };
    let mut after_state = instructions::RegistersState { a: 0, b: 0, c: 0, d: 0 };

    for capture in BEFORE_LINE_REGEX.captures_iter(before_line) {
        before_state = instructions::RegistersState {
            a: capture[1].parse().unwrap(),
            b: capture[2].parse().unwrap(),
            c: capture[3].parse().unwrap(),
            d: capture[4].parse().unwrap(),
        }
    }

    for capture in INSTRUCTION_LINE_REGEX.captures_iter(instruction_line) {
        instruction = instructions::Instruction {
            symbol: capture[1].parse().unwrap(),
            operand1: capture[2].parse().unwrap(),
            operand2: capture[3].parse().unwrap(),
            result: capture[4].parse().unwrap(),
        }
    }

    for capture in AFTER_LINE_REGEX.captures_iter(after_line) {
        after_state = instructions::RegistersState {
            a: capture[1].parse().unwrap(),
            b: capture[2].parse().unwrap(),
            c: capture[3].parse().unwrap(),
            d: capture[4].parse().unwrap(),
        }
    }

    Example {
        instruction: instruction,
        before: before_state,
        after: after_state,
    }
}

fn parse_instruction(line: String) -> instructions::Instruction {
    let mut instruction = instructions::Instruction { symbol: 0, operand1: 0, operand2: 0, result: 0 };

    for capture in INSTRUCTION_LINE_REGEX.captures_iter(line.as_str()) {
        instruction = instructions::Instruction {
            symbol: capture[1].parse().unwrap(),
            operand1: capture[2].parse().unwrap(),
            operand2: capture[3].parse().unwrap(),
            result: capture[4].parse().unwrap(),
        }
    }

    instruction
}

fn deduct_instruction_set(input: &Input) -> HashMap<i32, instructions::Operation> {
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
