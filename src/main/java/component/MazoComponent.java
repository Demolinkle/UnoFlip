package component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class MazoComponent extends Component {

    private final String path = "src/main/resources/assets/textures/luz/";
    private List<Carta> cartas;

    // Constructor
    public MazoComponent() {
        this.cartas = new ArrayList<>();
        generarMazo();
    }

    private void template() {
        //Entity carta = getGameWorld().create("carta","carta_luz");
        //carta.getViewComponent().addChild(texture("luz/amarillo", 80, 130));
        //carta.addComponent(new BoundingBoxComponent());
    }

    public List<Carta> getCartas() {
        return cartas;
    }

    public void agregarCarta(Carta carta) {
        cartas.add(carta);
    }

    public void generarMazo() {
        // Se generan las cartas numericas del mazo
        for (int i = 1; i < 10; i++) {
            cartas.add(new Carta("amarillo", i, "data"));
            cartas.add(new Carta("azul", i, "data"));
            cartas.add(new Carta("rojo", i, "data"));
            cartas.add(new Carta("verde", i, "data"));
        }

        Collections.shuffle(cartas);
    }
}
