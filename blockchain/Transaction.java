package blockchain;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
    public String transactionId;  // this is also the hash of the transaction
    public PublicKey sender;    // senders address/public key
    public PublicKey recipient; // recipients address/public key
    public float value;
    public byte[] signature; // this is to prevent anybody else from spending funds

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0;

    // Constructor
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        sequence++;
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) + sequence
        );
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        this.signature = StringUtil.applyECDSA(privateKey, data); // Generar y asignar la firma
    }

    public boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction() {
        if (!verifiySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        for (TransactionInput i : inputs) {
            i.UTXO = JBlockChain.UTXOs.get(i.transactionOutputId);
        }

        if (getInputsValue() < JBlockChain.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            ;

        }

        float leftOver = getInputsValue() - value; //Get value of then left over change.
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        //Add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            JBlockChain.UTXOs.put(o.id, o);
        }

        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue;
            JBlockChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    //Returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;

        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue; //If transaction can't be found skip it
            total += i.UTXO.value;
        }

        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o: outputs){
            total += o.value;
        }

        return total;
    }
}
