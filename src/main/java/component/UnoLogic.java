package component;

import com.almasb.fxgl.multiplayer.NetworkComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.core.serialization.Bundle;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.control.ChoiceDialog;
import java.util.*;
import java.io.Serializable;

public class UnoLogic extends Component implements Serializable {

    private static final double ESPACIADO_HORIZONTAL = 55;
    private static final double ESPACIADO_VERTICAL = 30;
    private static final int MAX_CARTAS_POR_FILA = 10;

    public static List<Carta> generarMazo() {
        List<Carta> cartas = new ArrayList<>();
        for (int i = 1; i < 15; i++) { // Se generan las cartas numericas del mazo
            cartas.add(new Carta("amarillo", i, "data"));
            cartas.add(new Carta("azul", i, "data"));
            cartas.add(new Carta("rojo", i, "data"));
            cartas.add(new Carta("verde", i, "data"));
        }

         do { // la primera carta no puede ser especial
            Collections.shuffle(cartas);
        } while (cartas.get(0).getId() > 9);

        return cartas;
    }

    public static void repartirCartas(List<Carta> mazo, Connection<Bundle> conexion) {
        List<Carta> aux = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            if (!mazo.isEmpty()) {
                aux.add(mazo.get(0));
                mazo.remove(0);
            }
        }
        enviarMensaje("Mano inicial", aux, conexion);
    }

    public static void enviarMensaje(String titulo, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle(titulo);
        conexion.send(bundle);
    }

    public static void enviarMensaje(String titulo, Carta carta, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle(titulo);
        bundle.put("carta", (Serializable) carta);
        conexion.send(bundle);
    }

    public static void enviarMensaje(String titulo, List<Carta> cartas, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle(titulo);
        bundle.put("carta", (Serializable) cartas);
        conexion.send(bundle);
    }

    public static void enviarMensaje(String titulo, String mensaje, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle(titulo);
        bundle.put("carta", (Serializable) mensaje); //aqui
        conexion.send(bundle);
    }
  
    public static void mostrarMano(List<Carta> cartas, Connection<Bundle> conexion) {
        getGameWorld().getEntitiesByType(GameFactory.EntityType.MANO).forEach(Entity::removeFromWorld);
        double startX = 50;
        double startY = 300;
        int i = 0;
        for (Carta carta : cartas) {
            Entity aux = entityBuilder() // luz/verde/5.png
                    .type(GameFactory.EntityType.MANO)
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
                    .onClick(e -> {
                        if (carta.getId() == 14) { // un cambia color
                            carta.setColor(mostrarBotonesColor());
                        }
                        enviarMensaje("Carta a jugar", carta, conexion);
                    })
                    .build();
            // Coloca la carta en la nueva posición
            aux.setPosition(startX + (i % MAX_CARTAS_POR_FILA) * ESPACIADO_HORIZONTAL, startY + (i / MAX_CARTAS_POR_FILA) * ESPACIADO_VERTICAL);
            i++;
            getGameWorld().addEntity(aux);
        }
    }

    public static void mostrarMazo(List<Carta> mazo) {
        double startX = 50;
        double startY = 300;
        int i = 0;
        for (Carta carta : mazo) {
            Entity aux = entityBuilder()
                    .type(GameFactory.EntityType.CARTA_MAZO)// luz/verde/5.png
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
                    .build();
            // Coloca la carta en la nueva posición
            aux.setPosition(startX + (i % MAX_CARTAS_POR_FILA) * ESPACIADO_HORIZONTAL, startY + (i / MAX_CARTAS_POR_FILA) * ESPACIADO_VERTICAL);
            i++;
            getGameWorld().addEntity(aux);
        }
    }

    public static void mostrarCarta(Carta carta) {
        if (carta.getId() == 14) { // para cuando se cambie el color se notifique cual es
            showMessage("El nuevo color es: " + carta.getColor()); // el metodo es llamado 2 veces por algun motivo
        }
        Entity aux = entityBuilder()
                .type(GameFactory.EntityType.CARTA)
                .viewWithBBox(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
                .at(700, 300)
                .build();
        getGameWorld().addEntity(aux);
    }

    public static void mostrar_Carta_del_servidor(Carta carta, Connection<Bundle> conexion) {
        getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_INICIAL).forEach(Entity::removeFromWorld);
        Entity cartaServidor = entityBuilder()
            .type(GameFactory.EntityType.CARTA_INICIAL)// luz/verde/5.png
            .view(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
            .at(700,300)
            .build();
        getGameWorld().addEntity(cartaServidor);
        enviarMensaje("Nueva carta", carta, conexion);
    }

    public static boolean esValido(Carta carta_del_servidor, Carta carta_del_jugador) {
        if (carta_del_servidor.getColor().equals(carta_del_jugador.getColor()) || 
            carta_del_servidor.getId() == carta_del_jugador.getId() || 
            carta_del_jugador.getColor().equals("especial")) 
            {
            return true;
        }
        else if(carta_del_jugador.getId() >= 14) { // id de las cartas que se juegan en cualquier momento
            return true;
        }
        return false;
    }

    private static String mostrarBotonesColor() {
       List<String> colores = Arrays.asList("amarillo", "azul", "rojo", "verde");
        //usa un dialogo de seleccion de javafx
        ChoiceDialog<String> dialog = new ChoiceDialog<>("amarillo", colores);
        dialog.setTitle("Seleccionar Color");
        dialog.setHeaderText("Selecciona un color");
        dialog.setContentText("Color:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse("amarillo"); // Valor por defecto si no se selecciona nada
    }
    // se ejecuta del lado del servidor
    public static Carta jugarCarta(Carta carta_del_servidor, Carta carta_del_jugador, Connection<Bundle> conexion) {
        if (esValido(carta_del_servidor, carta_del_jugador)) {
            getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_INICIAL).forEach(Entity::removeFromWorld);
            carta_del_servidor = carta_del_jugador;
            Entity carta_nueva = entityBuilder()
                .type(GameFactory.EntityType.CARTA_INICIAL)
                .with(new NetworkComponent())
                .view(texture(String.format("luz/%s/%s.png", carta_del_servidor.getColor(), carta_del_servidor.getId()), 60, 100))
                .at(700, 300)
                .build();
            getGameWorld().addEntity(carta_nueva);
            enviarMensaje("Nueva carta", carta_del_servidor, conexion);
            enviarMensaje("Eliminar carta", carta_del_jugador, conexion);

        }  else {
            enviarMensaje("No puedes jugar esa carta", conexion);
        }
        return carta_del_servidor;
    }

    public static List<Carta> removerCarta(List<Carta> cartas, Carta carta) {
        for (int i = 0; i < cartas.size(); i++) {
            if (cartas.get(i).getColor().equals(carta.getColor()) && 
                cartas.get(i).getId() == carta.getId() && 
                cartas.get(i).getTipo().equals(carta.getTipo())) {
                cartas.remove(i);
                break;
            }
        }
        return cartas;
    }

    public static Carta robarCarta(List<Carta> mazo2) {
        if (!mazo2.isEmpty()) {
            return mazo2.remove(0);
        }
        return null;
    }
}
