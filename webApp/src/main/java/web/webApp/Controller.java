package web.webApp;

import org.springframework.web.bind.annotation.GetMapping;


@org.springframework.stereotype.Controller
public class Controller {

    @GetMapping("/battle")
    String getString() {
        return "battle";
    }
}
