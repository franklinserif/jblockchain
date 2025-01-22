package blockchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class JBlockChain {
    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
    public static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;
    public static float minimumTransaction = 0;
    public static Transaction genesisTransaction;


    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        System.out.println("Proveedor registrado: " + Security.getProvider("BC"));

        // Initializer wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        // Create genesis transaction, which sends 100 JBlocks to walletA;
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.transactionId = "0"; // manually set the transaction id;
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and Mining Genesis block...");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        // testing
        Block block1 = new Block(genesis.hash);
        System.out.println("\n WalletA balance is: " + walletA.getBalance());
        System.out.println("\n WalletA balance is Attempting to send funds (40) to WalletB");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);

        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it hash...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);

        Block block3 = new Block(block2.hash);
        System.out.println("\nWalletB is Attempting to send funds (20) to walletA");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
        System.out.println("\nWalletA balance is : "+ walletA.getBalance());
        System.out.println("\nWalletB balance is : " + walletB.getBalance());

        isChainValid();
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        // loop through blockchain to check hashes
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            // compare registered hash and  calculated hash

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current hashes not equal");

                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous hashes not equal");
                return false;
            }

            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }

            // Loop through blockchains transactions:
            TransactionOutput tempOut;

            for(int t=0; t < currentBlock.transactions.size(); t++){
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifiySignature()){
                    System.out.println("#Signature on Transaction (" + t + ") is Invalid");
                    return false;
                }

                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()){
                    System.out.println("#Inputs are not equal to outputs on Transaction ( " + t + ") ");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs){
                    tempOut = tempUTXOs.get(input.transactionOutputId);

                    if(tempOut == null){
                        System.out.println("#Referenced input on Transaction (" + t + ") is missing.");
                        return false;
                    }

                    if(input.UTXO.value != tempOut.value){
                        System.out.println("#Referenced input Transaction (" + t + ") value is invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput ouput: currentTransaction.outputs){
                    tempUTXOs.put(ouput.id, ouput);
                }

                if(currentTransaction.outputs.get(0).recipient != currentTransaction.recipient){
                    System.out.println("#Transaction (" + t + ") output recipient is not who it should be");
                    return false;
                }

                if(currentTransaction.outputs.get(1).recipient != currentTransaction.sender){
                    System.out.println("#Transaction (" + t + ") output 'change' is not sender.");
                    return false;
                }

            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}