package form3test.model;


import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BeneficiaryParty {

    @Column(name="beneficiary_party_account_name")
    public  String accountName;
    @Column(name="beneficiary_party_account_number")
    public  String accountNumber;
    @Column(name="beneficiary_party_account_number_code")
    public  String accountNumberCode;
    public  long accountType;
    @Column(name="beneficiary_party_address")
    public  String address;
    @Column(name="beneficiary_party_bank_id")
    public  String bankId;
    @Column(name="beneficiary_party_bank_id_code")
    public  String bankIdCode;
    @Column(name="beneficiary_party_name")
    public  String name;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumberCode() {
        return accountNumberCode;
    }

    public void setAccountNumberCode(String accountNumberCode) {
        this.accountNumberCode = accountNumberCode;
    }

    public long getAccountType() {
        return accountType;
    }

    public void setAccountType(long accountType) {
        this.accountType = accountType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}