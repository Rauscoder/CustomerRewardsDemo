package charterDemo.customerTransaction.controller;


import charterDemo.customerTransaction.DTO.CustomerRewardDetailsDTO;
import charterDemo.customerTransaction.exception.TransactionNotFoundException;
import charterDemo.customerTransaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    // 1. Everything is working fine -> Expect Status 200 OK
    @Test
    public void getCustomerRewardsDetails_Success_Returns200() throws Exception {
        Map<String,BigDecimal> rewardPerMonth=new HashMap<>();
        rewardPerMonth.put("Jan-2026",new BigDecimal("150.00000"));
        CustomerRewardDetailsDTO mockResponse = new CustomerRewardDetailsDTO(
                "John Doe", "John12345",rewardPerMonth, new BigDecimal("150.00000").setScale(5, RoundingMode.DOWN)
        );

        when(transactionService.getRewardsByCustomer(eq("John12345"), any(), any()))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/getRewardsByCustomer/John12345")
                        .param("startDate", "01-01-2026 00:00:00")
                        .param("endDate", "31-01-2026 23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.customerTransactionId").value("John12345"))
                .andExpect(jsonPath("$.rewardPerMonth['Jan-2026']").value(150.00000))
                .andExpect(jsonPath("$.totalReward").value(150.00000));
    }

    // 2. One of the dates is null -> Expect Status 400 Bad Request
    @Test
    public void getCustomerRewardsDetails_OneDateNull_Returns400() throws Exception {
        when(transactionService.getRewardsByCustomer(eq("John12345"), any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Either one of startDate/endDate is empty..."));
        mockMvc.perform(get("/getRewardsByCustomer/John12345")
                        .param("startDate", "01-01-2026 00:00:00")) // Missing endDate
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorResponse['Error Reason']").exists());
    }

    // 3. Start date is greater than End date -> Expect Status 400 Bad Request
    @Test
    public void getCustomerRewardsDetails_StartDateGreaterThanEndDate_Returns400() throws Exception {
        when(transactionService.getRewardsByCustomer(eq("John12345"), any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate can not be greater then end Date"));

        mockMvc.perform(get("/getRewardsByCustomer/John12345")
                        .param("startDate", "31-01-2026 00:00:00")
                        .param("endDate", "01-01-2023 23:59:59"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorResponse['Error Reason']").exists());
    }

    // 4. Customer ID is not found -> Expect Status 404 Not Found (Based on your ControllerAdvice)
    @Test
    public void getCustomerRewardsDetails_CustomerNotFound_Returns404() throws Exception {
        when(transactionService.getRewardsByCustomer(eq("John12345"), any(), any()))
                .thenThrow(new TransactionNotFoundException("John12345 customerId not found"));

        mockMvc.perform(get("/getRewardsByCustomer/John12345"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorResponse['Error Reason']").exists());
    }

    // 5. Date format mismatch -> Expect Status 400 Bad Request
    @Test
    public void getCustomerRewardsDetails_DateFormatMismatch_Returns400() throws Exception {
        // Here we don't mock the service because Spring throws MethodArgumentTypeMismatchException
        // BEFORE it even reaches the controller method or service.
        mockMvc.perform(get("/getRewardsByCustomer/John12345")
                        .param("startDate", "2023/01/01")) // Wrong format, expects dd-MM-yyyy HH:mm:ss
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorResponse['Error Reason']").exists());
    }

    // 6. Internal Server Error for any other unhandled exception -> Expect Status 500
    @Test
    public void getCustomerRewardsDetails_InternalServerError_Returns500() throws Exception {
        when(transactionService.getRewardsByCustomer(eq("John12345"), any(), any()))
                .thenThrow(new RuntimeException("Some unexpected database or logic error occurred"));

        mockMvc.perform(get("/getRewardsByCustomer/John12345"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorResponse['Error Reason']").value("Internal Server Error"));
    }

}














