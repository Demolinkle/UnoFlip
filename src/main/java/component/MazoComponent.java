package component;

import com.almasb.fxgl.entity.component.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class MazoComponent extends Component {

    private List<Carta> cartas;
    private int cartasRepartidas;

    // Constructor
    public MazoComponent() {
        this.cartas = new ArrayList<>();
        this.cartasRepartidas = 0;
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

    public List<Carta> repartirCartas(int cantidad) {
        List<Carta> cartasRepartidas = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            if (this.cartas.size() > 0) {
                cartasRepartidas.add(this.cartas.remove(0)); 
            }
        }
        this.cartasRepartidas += cantidad;
        return cartasRepartidas;
    }

    public int getCartasRepartidas() {
        return cartasRepartidas;
    }
}
