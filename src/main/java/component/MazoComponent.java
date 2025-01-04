package component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import static com.almasb.fxgl.dsl.FXGL.*;
import static component.UnoLogic.mostrarCartas;

public class MazoComponent extends Component {

    private final String path = "src/main/resources/assets/textures/luz/";
    private List<Carta> cartas;

    // Constructor
    public MazoComponent() {
        this.cartas = new ArrayList<>();
        generarMazo();
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
            cartas.add(new Carta("amarillo", i, "placeholder"));
            cartas.add(new Carta("azul", i, "data"));
            cartas.add(new Carta("rojo", i, "data"));
            cartas.add(new Carta("verde", i, "data"));
        }

        Collections.shuffle(cartas);
    }

    public void mostrarMazo() {
        double startX = 50;
        double startY = 50;
        double spacing = 70;
        int i = 0;

        for (Carta carta : cartas) {
            Entity aux = entityBuilder() //luz/verde/5.png
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
                    .build();

            aux.setPosition((startX + i * spacing), startY);
            i++;

            if (i % 16 == 0) {
                startY += 120;
                i=0;
            }

            getGameWorld().addEntity(aux);
        }
    }
}