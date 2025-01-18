package component;

import java.io.Serializable;
import java.util.Objects;

public class Carta implements Serializable {
    private static final long serialVersionUID = 1L;
    private String color;
    private int id;
    private String tipo;

    // Constructor
    public Carta(String color, int id, String tipo) {
        this.color = color;
        this.id = id;
        this.tipo = tipo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int valor) {
        this.id = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
