package web.webApp;

import DataConfig.Position;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ship.Ship;


@org.springframework.stereotype.Controller
public class Controller {

    @GetMapping("/battle")
    String getString(Model model) {
        model.addAttribute("var", new Ship(1,1,1, Position.VERTICAL));
        return "battle";
    }
}
