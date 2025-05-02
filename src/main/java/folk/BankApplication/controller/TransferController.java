package folk.BankApplication.controller;

import folk.BankApplication.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransferController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<String> transfer(
            @RequestParam Long toAccountId,
            @RequestParam String amount) {
        Long userId = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        accountService.transferMoney(userId, toAccountId, new BigDecimal(amount));
        return ResponseEntity.ok("Перевод выполнен успешно");
    }
}
