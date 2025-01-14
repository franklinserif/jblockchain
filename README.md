# Simple Blockchain in Java

This project is a basic implementation of a blockchain written in Java, inspired by the tutorial ["Create Simple Blockchain - Java Tutorial From Scratch"](https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa). The primary goal is to understand the fundamentals of how a blockchain works.

## 🚀 Features

- Basic blockchain implementation in Java.
- Block validation using hash functions.
- Ability to store custom data in blocks.
- Designed for easy learning and experimentation.

## 📋 Prerequisites

Make sure you have the following installed on your system:

- **Java Development Kit (JDK)**: Version 8 or higher.
- **Maven** (optional): For building and managing dependencies.
- A code editor or IDE, such as IntelliJ IDEA or Eclipse.

## 🛠️ Installation

1. Clone this repository:
   ```bash
    git clone https://github.com/franklinsrr/jblockchain
   ```
2. Navigate to the project directory:
    ```bash
    cd simple-blockchain-java
    ```
3. Open the project in your preferred Java IDE.

## Usage
1. Run the main method in the Blockchain class.
2. Experiment by adding blocks with custom data and observe the behavior of the blockchain.
3. Modify the code to add more features or enhance the functionality.


## 📝 How It Works
- Each block contains:
- - Index: The block's position in the chain.
- - Timestamp: When the block was created.
- - Data: Custom data stored in the block.
- - Previous Hash: The hash of the previous block.
- - Hash: The block's unique identifier, calculated based on its contents.
- Blocks are linked together, forming a chain, and the integrity of the chain is ensured by validating hashes.

## 📚 Resources
- [original tutorial](https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa)
- [java Documentation](https://docs.oracle.com/javase/)