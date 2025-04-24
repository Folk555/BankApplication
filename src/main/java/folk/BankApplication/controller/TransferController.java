package folk.BankApplication.controller;

import folk.BankApplication.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<String> transfer(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @RequestParam Long toAccountId,
            @RequestParam String amount) {

        accountService.transferMoney(authToken, toAccountId, new BigDecimal(amount));
        return ResponseEntity.ok("Transfer successful");
    }
}
