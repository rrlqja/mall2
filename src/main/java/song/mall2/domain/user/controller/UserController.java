package song.mall2.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import song.mall2.domain.user.dto.SignupDto;
import song.mall2.domain.user.service.UserService;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<Object> postSaveUser(@RequestBody SignupDto signupDto) {
        userService.saveUser(signupDto);

        return ResponseEntity.ok().build();
    }
}