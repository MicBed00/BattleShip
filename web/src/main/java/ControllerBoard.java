import DataConfig.Position;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ship.Ship;

@Controller
public class ControllerBoard {

    Ship ship1 = new Ship(2,2,2, Position.HORIZONTAL);

    @GetMapping("/")
    String getString(Model model) {
        return "game";
    }
}
