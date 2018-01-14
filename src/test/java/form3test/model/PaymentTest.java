package form3test.model;

import form3test.Application;
import form3test.repositories.PaymentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test for the Payment entity, to ensure that the JPA Orm is set up correctly to allow it to be correctly saved and
 * retrieved from the database.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
public class PaymentTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void shouldBeAbleToCorrectlySaveAndRetrieveAPaymentEntityAndItsChildObjects(){

        //given: a Payment entity set up to exercise all object relational mappings:
        Payment payment = new Payment();
        Attributes attributes = new Attributes();
        payment.setAttributes( attributes );

        BeneficiaryParty beneficiaryParty = new BeneficiaryParty();
        beneficiaryParty.setName( "beneficiaryPartyName");
        attributes.setBeneficiaryParty( beneficiaryParty );

        SenderCharge senderCharge1 = new SenderCharge();
        senderCharge1.setCurrency("currency1");
        SenderCharge senderCharge2 = new SenderCharge();
        senderCharge2.setCurrency("currency2");
        List<SenderCharge> senderCharges =Arrays.asList( senderCharge1, senderCharge2);
        ChargesInformation chargesInformation = new ChargesInformation();
        chargesInformation.setSenderCharges( senderCharges );
        attributes.setChargesInformation( chargesInformation );

        DebtorParty debtorParty = new DebtorParty();
        debtorParty.setName( "debtorPartyName");
        attributes.setDebtorParty( debtorParty );

        Fx fx = new Fx();
        fx.setContractReference("fxContractReference");
        attributes.setFx( fx );

        SponsorParty sponsorParty = new SponsorParty();
        sponsorParty.setAccountNumber( "sponsorPartyAccountNumber");
        attributes.setSponsorParty( sponsorParty );

        //when: the payment is saved and flushed to the database:
        paymentRepository.saveAndFlush( payment);

        //then: the payment should be fetchable from the database:
        String id = payment.getId();
        Payment fetchedPayment = paymentRepository.findOne(id);

        //and it should have had its child attributes object persisted:
        Attributes fetchedAttributes = fetchedPayment.getAttributes();
        //and the child BeneficiaryParty should have been persisted:
        assertEquals(fetchedAttributes.getBeneficiaryParty().getName(), "beneficiaryPartyName" );
        //and the child ChargesInformation should have been persisted:
        assertEquals(fetchedAttributes.getChargesInformation().getSenderCharges().get(0).getCurrency(), "currency1" );
        assertEquals(fetchedAttributes.getChargesInformation().getSenderCharges().get(1).getCurrency(), "currency2" );
        //and the child DebtorParty should have been persisted:
        assertEquals(fetchedAttributes.getDebtorParty().getName(), "debtorPartyName" );
        //and the child Fx should have been persisted:
        assertEquals(fetchedAttributes.getFx().getContractReference(), "fxContractReference" );
        //and the child SponsorParty should have been persisted:
        assertEquals(fetchedAttributes.getSponsorParty().getAccountNumber(), "sponsorPartyAccountNumber" );


    }
}
