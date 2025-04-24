package folk.BankApplication.controller;

import folk.BankApplication.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        String email = request.email();
        String password = request.password();
        return userService.authenticate(email, password);
    }

    public record LoginRequest(String email, String password) {}

}
