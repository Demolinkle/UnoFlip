package GameSettings;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class MazoComponent extends Component {

    private List<Entity> cartas;

    public MazoComponent() {
        this.cartas = new ArrayList<>();
    }

    public List<Entity> getCartas() {
        return cartas;
    }

    public void agregarCarta(Entity carta) {
        cartas.add(carta);
    }
}
