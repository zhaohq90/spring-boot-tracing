package in.aprilfish.tracing.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
public class IndexController {

    @GetMapping
    public String index() {
        log.info("hello world");

        return "index";
    }


    @PostMapping("/info")
    public Info info(@RequestBody Info info) {
        info.setCode(100);
        info.setMessage("success");

        return info;
    }

    @Data
    public static class Info{
        Integer code;
        String message;
    }

}
