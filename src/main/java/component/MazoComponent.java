package component;

import com.almasb.fxgl.entity.component.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class MazoComponent extends Component implements Serializable{

    private List<Carta> cartas;
    private int cartasRepartidas;

    // Constructor
    public MazoComponent() {
        this.cartas = new ArrayList<>();
        generarMazo();
        this.cartasRepartidas = 0;
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

    public List<Carta> repartirCartas() {
        List<Carta> aux = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            if (!this.cartas.isEmpty()) {
                aux.add(this.cartas.remove(0)); 
            }
        }
        this.cartasRepartidas += 7;
        return aux;
    }

    public List<Carta> getSerializableCartas() {
        return new ArrayList<>(cartas);
    }

    public Carta robarCarta() {
        if (!this.cartas.isEmpty()) {
            this.cartasRepartidas++;
            return this.cartas.remove(0);
        }
        return null;
    }

    public int getCartasRepartidas() {
        return cartasRepartidas;
    }

}
