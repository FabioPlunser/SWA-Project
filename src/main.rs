// use rayon::prelude::*;
use serde::{Deserialize, Serialize};
use std::fmt::{Debug, Display, Formatter, Result};
use std::fs::File;
use std::io::Write;

const SIMULATION_DEPTH: u64 = 5;

#[derive(Clone, Copy, Debug)]
struct Card {
    difficulty: u64,
    repetitions: u64,
    repeat: bool,

    interval: u64,
    eFactor: f64,
    nextLearn: u64,

    oldInterval: u64,
    oldEFactor: f64,
    lastLearn: u64,
}

impl Card {
    // Learns the current Card
    // Returns true if the current card should be enqueued again.
    fn learn(&mut self, q: u64) {
        if q > 5 {
            panic!("Invalid difficulty!");
        }

        self.difficulty = q;
        self.oldInterval = self.interval;
        self.oldEFactor = self.eFactor;
        self.lastLearn = self.nextLearn;
        self.repetitions += 1;
        self.repeat = q < 4;

        if q > 2 {
            let n = self.repetitions;
            if n == 1 {
                self.interval = 1;
            } else if n == 2 {
                self.interval = 6;
            } else {
                self.interval = (self.interval as f64 * self.eFactor) as u64;
                self.eFactor =
                    self.eFactor - 0.8f64 + 0.28f64 * q as f64 - 0.02f64 * q as f64 * q as f64;
                if self.eFactor > 1.3 {
                    self.eFactor = 1.3;
                }
            }
        } else {
            self.interval = 1;
        }

        self.nextLearn += self.interval;
    }
}

impl Default for Card {
    fn default() -> Self {
        Card {
            difficulty: 0,
            repetitions: 0,
            interval: 0,
            eFactor: 2.5,
            oldInterval: 0,
            oldEFactor: 2.5,
            nextLearn: 0,
            lastLearn: 0,
            repeat: false,
        }
    }
}

impl Display for Card {
    fn fmt(&self, f: &mut Formatter<'_>) -> Result {
        write!(
            f,
            "Repetitions: {}, Interval: {}, EFactor: {}, LastLearn: {} => Difficulty: {} => Interval: {}, EFactor: {} NextLearn: {}, Repeat: {}",
            self.repetitions, self.oldInterval, self.oldEFactor, self.lastLearn, self.difficulty, self.interval, self.eFactor, self.nextLearn, self.repeat
        )
    }
}

fn work(depth: u64, card: Card) -> Vec<Card> {
    if depth >= SIMULATION_DEPTH {
        return vec![];
    }

    let depth = depth + 1;

    let mut newCards: Vec<Card> = (0..=5)
        .map(|i| {
            let mut card = card.clone();
            card.learn(i);
            card
        })
        .collect();

    let mut updatedCards: Vec<Card> = newCards
        .iter()
        .map(|card| work(depth, card.clone()))
        .flatten()
        .collect();

    newCards.extend(updatedCards);

    newCards
}

fn main() {
    let mut file = File::create("dump.log").unwrap();

    let starting_card = Card::default();
    writeln!(&mut file, "{:?}", starting_card);

    work(0, starting_card)
        .into_iter()
        .for_each(|x| writeln!(&mut file, "{}", x).unwrap());
}
