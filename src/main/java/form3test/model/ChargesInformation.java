package form3test.model;

import javax.persistence.*;
import java.util.List;


/**
 * Created by rhall on 12/01/2018.
 */
@Embeddable
public class ChargesInformation {

    public  String bearerCode;
    @OneToMany( cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @OrderColumn(name="id")
    public List<SenderCharge> senderCharges;
    public  String receiverChargesAmount;
    public  String receiverChargesCurrency;

    public String getBearerCode() {
        return bearerCode;
    }

    public void setBearerCode(String bearerCode) {
        this.bearerCode = bearerCode;
    }

    public List<SenderCharge> getSenderCharges() {
        return senderCharges;
    }

    public void setSenderCharges(List<SenderCharge> senderCharges) {
        this.senderCharges = senderCharges;
    }

    public String getReceiverChargesAmount() {
        return receiverChargesAmount;
    }

    public void setReceiverChargesAmount(String receiverChargesAmount) {
        this.receiverChargesAmount = receiverChargesAmount;
    }

    public String getReceiverChargesCurrency() {
        return receiverChargesCurrency;
    }

    public void setReceiverChargesCurrency(String receiverChargesCurrency) {
        this.receiverChargesCurrency = receiverChargesCurrency;
    }
}
