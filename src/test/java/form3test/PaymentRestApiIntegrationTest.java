package form3test;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import form3test.model.Payment;
import form3test.repositories.PaymentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rhall on 12/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentRestApiIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    @Qualifier("halJacksonHttpMessageConverter")
    private TypeConstrainedMappingJackson2HttpMessageConverter halConverter;

    private String paymentsUrl;

    private String testPaymentAsJson = TestUtil.readFileAsString("testPaymentAsJson.json");

    private Payment testPaymentAsObject;

    private Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Before
    public void setUp() {
        paymentsUrl = "http://localhost:" + port + "/payments";

        //set up the restTemplate to be able to process HAL JSON:
        restTemplate.getRestTemplate().getMessageConverters().add(halConverter);

        //clear down data:
        paymentRepository.deleteAll();

        //set this up before each test, as it is modified during the tests:
        testPaymentAsObject = gson.fromJson(testPaymentAsJson, Payment.class);
    }

    @Test
    public void serverStarts() {
        //would fail here if server didn't start.
    }

    @Test
    public void shouldCreateAPaymentViaPost() throws Exception {

        //When: a new payment is created via a post request:
        ResponseEntity<String> response = postTestPaymentAndGetResponse();

        //then: the status should be 'created'
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        //and: the location header should contain a link to the newly created resource that includes its id:
        String locationHeader = getLocationHeader(response);
        assertTrue(locationHeader.contains("/payments/"));
    }

    @Test
    public void shouldGive400ResponseCodeIfTryToCreatePaymentWithInvalidDataViaPost() throws Exception {

        //given: some invalid representation of a payment object:
        String invalidData = "invalid data";
        //when: we try to create the payment via post:
        HttpEntity<String> entity = getHttpEntityForTestRequest(invalidData);
        ResponseEntity<String> response = restTemplate.postForEntity(paymentsUrl, entity, String.class);
        //then: the response status should be 400
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldRetrieveAPaymentViaGet() throws Exception {

        //given an existing payment:
        ResponseEntity<String> response = postTestPaymentAndGetResponse();
        String location = getLocationHeader(response);

        //when: the payment is fetched via get:
        ResponseEntity<Payment> getResponse = restTemplate.getForEntity(location, Payment.class);
        //then: the response status code should be OK:
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        //and: the data on the response should be correct, e.g. check the orgainsation_id field:
        Payment payment = getResponse.getBody();
        assertEquals(testPaymentAsObject.getOrganisationId(), payment.getOrganisationId());
    }

    @Test
    public void shouldGiveNotFoundResponseCodeIfTryToRetrieveUnknownPayment() throws Exception {

        //when: an attempt is made to retrieve a payment with an unknown id:
        ResponseEntity<Payment> response = restTemplate.getForEntity("/payments/unKnownId", Payment.class);
        //then: the response code should be 'not found'
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldRetrieveACollectionOfPayments() throws Exception {

        //given: there are at least  10 existing payments
        // ( because this test runs against a running server, they may be others on there from previous tests)
        for (int i = 0; i < 10; i++) {
            postTestPaymentAndGetResponse();
        }

        //when: the collection of payments is fetched:
        ResponseEntity response = restTemplate.exchange(paymentsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<Payment>>() {
        });
        //then: there should be at least 10 payments in the collection ( remember there may be more there from a previous test ):
        Collection<Payment> payments = ((PagedResources) response.getBody()).getContent();
        assertTrue(payments.size() >= 10);
        //and: the response status code should be OK:
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldUpdatePayment() throws Exception {

        //given an existing payment:
        ResponseEntity<String> response = postTestPaymentAndGetResponse();
        String location = getLocationHeader(response);

        //and: a modified version of that payment:
        String newOrganisationId = "newOrganisationId";
        testPaymentAsObject.setOrganisationId(newOrganisationId);
        String newJson = gson.toJson(testPaymentAsObject);

        //when: the payment is updated:
        HttpEntity<String> putEntity = getHttpEntityForTestRequest(newJson);
        ResponseEntity putResponse = restTemplate.exchange(location, HttpMethod.PUT, putEntity, String.class);
        //then: the response status code should be 'no content'
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        //when: the payment is fetched following the update:
        Payment updatedPayment = restTemplate.getForEntity(location, Payment.class).getBody();

        //then: the data should have been modified:
        assertEquals("newOrganisationId", updatedPayment.getOrganisationId());
    }

    @Test
    public void shouldGive400ResponseCodeIfTryToUpdatePaymentWithInvalidDataViaPut() throws Exception {
        //given: a pre existing payment:
        ResponseEntity<String> response = postTestPaymentAndGetResponse();
        String location = getLocationHeader(response);

        //and: some invalid representation of a payment object:
        HttpEntity<String> entity = getHttpEntityForTestRequest("invalid content");
        //when: we try to create the payment via put:
        ResponseEntity putResponse = restTemplate.exchange(location, HttpMethod.PUT, entity, String.class);
        //then: the response status should be 400
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
    }

    @Test
    public void shouldPartiallyUpdateAPaymentViaPatch() throws Exception {

        //given: a pre existing payment:
        ResponseEntity<String> response = postTestPaymentAndGetResponse();
        String location = getLocationHeader(response);

        //when: just the organisation_id is updated:
        HttpEntity entity = getHttpEntityForTestRequest("{\"organisation_id\":\"newOrganisationId\"}");
        RestTemplate restTemplateForPatch = getRestTemplateForPatch();
        ResponseEntity<String> patchResponse = restTemplateForPatch.exchange(location, HttpMethod.PATCH, entity, String.class);
        //then: a 200 ok status code is returned:
        assertEquals(HttpStatus.OK, patchResponse.getStatusCode());

        //when: the payment is fetched again:
        Payment updatedPayment = restTemplate.getForEntity(location, Payment.class).getBody();
        //then: the data on the response should be correct, e.g. check the orgainsation_id field:
        assertEquals("newOrganisationId", updatedPayment.getOrganisationId());
    }

    @Test
    public void shouldGive404NotFoundIfAttemptIsMadeToPatchNonExistentPayment() throws Exception {

        //when: an attempt is made to patch a non existent payment:
        RestTemplate restTemplateForPatch = getRestTemplateForPatch();
        HttpEntity entity = getHttpEntityForTestRequest("{\"organisation_id\":\"newOrganisationId\"}");
        HttpClientErrorException _404Exception = null;
        try {
            restTemplateForPatch.exchange(paymentsUrl + "/nonExistentPayment", HttpMethod.PATCH, entity, String.class);
        } catch (HttpClientErrorException e){
            _404Exception = e;
        }
        //then: a 404 response code is returned:
        assertNotNull( "404 exception should have been thrown", _404Exception);
        assertEquals(HttpStatus.NOT_FOUND, _404Exception.getStatusCode());
    }

    @Test
    public void shouldGive400ResponseCodeIfTryToPatchPaymentWithInvalidData() throws Exception {
        //given: a pre existing payment:
        ResponseEntity<String> response = postTestPaymentAndGetResponse();
        String location = getLocationHeader(response);

        //when: a patch is made with invalid content:
        HttpEntity entity = getHttpEntityForTestRequest("invalid content");

        HttpClientErrorException _400Exception = null;
        RestTemplate restTemplateForPatch = getRestTemplateForPatch();
        try {
            restTemplateForPatch.exchange(location,  HttpMethod.PATCH, entity, String.class);
        } catch (HttpClientErrorException e){
            _400Exception = e;
        }
        //then: a 400 response code is returned:
        assertNotNull( "400 exception should have been thrown", _400Exception);
        assertEquals(HttpStatus.BAD_REQUEST, _400Exception.getStatusCode());
    }

    @Test
    public void shouldDeletePayment() throws Exception {

        //given: a pre existing payment:
        ResponseEntity<String> response = postTestPaymentAndGetResponse();
        String location = getLocationHeader(response);

        //when: the payment is deleted:
        ResponseEntity<String> deleteResponse = restTemplate.exchange(location, HttpMethod.DELETE, null, String.class);
        //then: the response should be 'no content'
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        //when: an attempt is made to retrieve the deleted payment:
        ResponseEntity<String> getResponse = restTemplate.getForEntity(location, String.class);
        //then: the status code should be 404 not found:
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    public void shouldGive404ResponseCodeIfAttemptIsMadeToDeleteNonExistentPayment() throws Exception {

        //when: an attempt is made to delete a non existent payment:
        ResponseEntity<String> deleteResponse = restTemplate.exchange(paymentsUrl + "/nonexistentPayment", HttpMethod.DELETE, null, String.class);
        //then: the response should be 404 'not found':
        assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());
    }

    /** Helper methods follow: **/

    /* bug with restTemplate doing patch requests ( see: https://github.com/spring-cloud/spring-cloud-netflix/issues/1777)
        so added this method as a workaround:*/
    private RestTemplate getRestTemplateForPatch() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        return new RestTemplate(requestFactory);
    }

    //helper method to avoid code duplication:
    private HttpEntity<String> getHttpEntityForTestRequest(String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(requestBody, headers);
    }

    private ResponseEntity<String> postTestPaymentAndGetResponse() {
        HttpEntity<String> entity = getHttpEntityForTestRequest(testPaymentAsJson);
        return restTemplate.postForEntity(paymentsUrl, entity, String.class);
    }

    private String getLocationHeader(ResponseEntity<String> response) {
        return response.getHeaders().get("Location").get(0);
    }


}
