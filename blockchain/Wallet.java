package blockchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try{
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            // initialize the key generator and generator keyPair
            keygen.initialize(ecSpec, random); // 256 bytes provides acceptable security level
            KeyPair keyPair = keygen.generateKeyPair();

            // set the public and private keys from keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public float getBalance() {
        float total = 0;

        for(Map.Entry<String, TransactionOutput> item: JBlockChain.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)){ //If output belongs to me (if coin belong to me)
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.value;
            }
        }

        return total;
    }

    public Transaction sendFunds(PublicKey _recipient, float value) {
        if(getBalance() < value){ //Gather balance and check funds
            System.out.println("#Not Enough funds to send transaction. Transaction discarded");
            return null;
        }

        //Create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();

            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input: inputs) {
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }
}
