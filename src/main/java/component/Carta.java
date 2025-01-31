package component;

import java.io.Serializable;

public class Carta implements Serializable {
    private String color;
    private int id;
    private String tipo;
    //oscuridad
    private String backupColor;
    private String backupTipo;
    private int backupId;

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

    public void flip() {
        String tempColor = this.color;
        String tempTipo = this.tipo;
        int tempId = this.id;

        this.color = this.backupColor;
        this.tipo = this.backupTipo;
        this.id = this.backupId;

        this.backupColor = tempColor;
        this.backupTipo = tempTipo;
        this.backupId = tempId;
    }

    public void setBackupColor(String color) {
        this.backupColor = color;
    }

    public void setBackupTipo(String tipo) {
        this.backupTipo = tipo;
    }

    public void setBackupId(int id) {
        this.backupId = id;
    }
}
