package at.ac.tuwien.sepm.assignment.individual.integration;

import at.ac.tuwien.sepm.assignment.individual.base.TestData;
import at.ac.tuwien.sepm.assignment.individual.endpoint.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OwnerEndpointTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PlatformTransactionManager txm;

    TransactionStatus txstatus;

    @Autowired
    OwnerService ownerService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setupDBTransaction() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        txstatus = txm.getTransaction(def);
        assumeTrue(txstatus.isNewTransaction());
        txstatus.setRollbackOnly();
    }

    @AfterEach
    public void tearDownDBData() {
        txm.rollback(txstatus);
    }

    // GET reuqest
    @Test
    void getOwnerById_var1() throws Exception {
        Owner owner = ownerService.add(TestData.getNewOwner());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/owners/" + owner.getId()))
            .andExpect(status().isOk());
    }

    // GET request
    @Test
    void getOwnerById_var2() throws Exception {
        Owner owner = ownerService.add(TestData.getNewOwner());

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/owners/" + owner.getId()))
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        OwnerDto ownerDto = objectMapper.readValue(response.getContentAsString(), new TypeReference<OwnerDto>() {
        });

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(owner.getName(), ownerDto.getName()),
            () -> assertEquals(owner.getId(), ownerDto.getId()),
            () -> assertEquals(owner.getUpdatedAt(), ownerDto.getUpdatedAt()),
            () -> assertEquals(owner.getCreatedAt(), ownerDto.getCreatedAt())
        );
    }

    // POST request example
    @Test
    void addOwner() throws Exception {
        OwnerDto ownerDto = new OwnerDto("ownerDto");
        String body = objectMapper.writeValueAsString(ownerDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/owners/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        OwnerDto ownerResponse = objectMapper.readValue(response.getContentAsString(), OwnerDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertNotNull(ownerResponse.getId()),
            () -> assertNotNull(ownerResponse.getName()),
            () -> assertNotNull(ownerResponse.getCreatedAt()),
            () -> assertNotNull(ownerResponse.getUpdatedAt())
        );
    }
}
