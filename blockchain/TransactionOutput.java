package blockchain;

import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey recipient; // also knows as the new owner of the coins.
    public float value;
    public String parentTransactionId;

    //Constructor
    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient));
    }

    public boolean isMine(PublicKey publickey) {
        return (publickey == recipient);
    }
}
