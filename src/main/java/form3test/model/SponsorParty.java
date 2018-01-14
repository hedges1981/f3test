package form3test.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by rhall on 12/01/2018.
 */
@Embeddable
public class SponsorParty {

    @Column(name="sponsor_party_account_number")
    public  String accountNumber;
    @Column(name="sponsor_party_bank_id")
    public  String bankId;
    @Column(name="sponsor_party_bank_id_code")
    public  String bankIdCode;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankIdCode() {
        return bankIdCode;
    }

    public void setBankIdCode(String bankIdCode) {
        this.bankIdCode = bankIdCode;
    }
}
