use crate::instructions;
use std::fs::File;
use std::io::BufReader;
use std::io::BufRead;
use std::io::Error;

use regex::Regex;

lazy_static! {
    static ref BEFORE_LINE_REGEX: Regex = Regex::new(r"^Before:\s*\[(\d+), (\d+), (\d+), (\d+)]$").unwrap();
    static ref INSTRUCTION_LINE_REGEX: Regex = Regex::new(r"^(\d+) (\d+) (\d+) (\d+)$").unwrap();
    static ref AFTER_LINE_REGEX: Regex = Regex::new(r"^After:\s*\[(\d+), (\d+), (\d+), (\d+)]$").unwrap();
}

#[derive(Debug)]
pub struct Example {
    pub instruction: instructions::Instruction,
    pub before: instructions::RegistersState,
    pub after: instructions::RegistersState,
}

#[derive(Debug)]
pub struct Input {
    pub examples: Vec<Example>,
    pub instructions: Vec<instructions::Instruction>,
}

pub fn parse_input(file: &str) -> Result<Input, Error> {
    let file = File::open(file).expect("cannot open input file");
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

            let example = _parse_example(
                line.trim(),
                instruction_line.trim(),
                after_line.trim(),
            );

            examples.push(example);
        } else {
            let instruction = _parse_instruction(line);
            instructions.push(instruction);
        }
    }

    Ok(Input {
        examples: examples,
        instructions: instructions,
    })
}

fn _parse_example(before_line: &str, instruction_line: &str, after_line: &str) -> Example {
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

fn _parse_instruction(line: String) -> instructions::Instruction {
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
