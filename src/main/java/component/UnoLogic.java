package component;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.multiplayer.NetworkComponent;
import static com.almasb.fxgl.dsl.FXGL.*;
import GameSettings.GameFactory;
import GameSettings.GameFactory.EntityType;
import javafx.scene.input.MouseButton;
import component.MazoComponent;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


public class UnoLogic extends Component {

    private static final int MAX_CARTAS_POR_FILA = 10;
    private static final double ESPACIADO_HORIZONTAL = 55;
    private static final double ESPACIADO_VERTICAL = 30;
    private Entity cartaInicial;
    private List<Entity> cartasMovidas; 


    public UnoLogic(Entity cartaInicial) {
        this.cartaInicial = cartaInicial;
        this.cartasMovidas = new ArrayList<>(); 
    }
/*
    public static void mostrarCartas(Entity mazo, UnoLogic unoLogic) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        int cartasRepartidas = mazoComponent.getCartasRepartidas();

        List<Carta> cartasARepartir;

        if (cartasRepartidas == 0) {
            cartasARepartir = mazoComponent.repartirCartas();
        } else {
            cartasARepartir = mazoComponent.repartirCartas();
        }

        double startX = 50 + (cartasRepartidas % MAX_CARTAS_POR_FILA);
        double startY = 300 + (cartasRepartidas / MAX_CARTAS_POR_FILA) * ESPACIADO_VERTICAL;
        int i = cartasRepartidas;

        for (Carta carta : cartasARepartir) {
            Entity cartaEntity = entityBuilder()
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
                    .build();

            cartaEntity.setPosition(startX + (i % MAX_CARTAS_POR_FILA) * ESPACIADO_HORIZONTAL, startY + (i / MAX_CARTAS_POR_FILA) * ESPACIADO_VERTICAL);
            i++;

            cartaEntity.getViewComponent().getChildren().get(0).setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    unoLogic.moverCarta(cartaEntity); // Mover la carta
                    unoLogic.eliminarCartasAnteriores(); // Eliminar cartas previas
                }
            });

            getGameWorld().addEntity(cartaEntity);
        }
    }
    */

    private void moverCarta(Entity carta) {
        carta.setPosition(cartaInicial.getPosition());
        cartasMovidas.add(carta);
        System.out.println("");
    }

    private void eliminarCartasAnteriores() {
        if (cartasMovidas.size() > 1) {
            Entity cartaAEliminar = cartasMovidas.get(0);
            cartaAEliminar.removeFromWorld();
            cartasMovidas.remove(0);
        }
    }

    public static void mostrarMano(List<Carta> cartas, Connection<Bundle> conexion) {
        double startX = 50;
        double startY = 300;
        int i = 0;
        for (Carta carta : cartas) {
            Entity aux = entityBuilder() // luz/verde/5.png
                    .type(GameFactory.EntityType.CARTA)
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
                    .onClick(e -> {
                        Bundle mensaje = new Bundle("Carta a jugar");
                        mensaje.put("carta", (Serializable) carta);
                        conexion.send(mensaje);      
                    })
                    .build();

            // Coloca la carta en la nueva posición
            aux.setPosition(startX + (i % MAX_CARTAS_POR_FILA) * ESPACIADO_HORIZONTAL, startY + (i / MAX_CARTAS_POR_FILA) * ESPACIADO_VERTICAL);
            i++;

            getGameWorld().addEntity(aux);
        }
    }

    public static Entity iniciarJuego(Entity mazo) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        Carta primeraCarta = mazoComponent.getCartas().get(0);
        Entity cartaInicial = entityBuilder()
            .type(GameFactory.EntityType.CARTA_INICIAL)// luz/verde/5.png
            .with(new NetworkComponent())
            .view(texture(String.format("luz/%s/%s.png", primeraCarta.getColor(), primeraCarta.getId()), 60, 100))
            .at(700,300)
            .build();    
        return cartaInicial;  
    }

    public static void mostrarMazo(Entity mazo) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        List<Carta> cartas = mazoComponent.getCartas();

        double startX = 50;
        double startY = 300;
        int i = 0;
        for (Carta carta : cartas) {
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

    public static Entity jugarCarta(Carta carta) {
        Entity cartaJugada = entityBuilder()
            .type(GameFactory.EntityType.CARTA_INICIAL)// luz/verde/5.png
            .view(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
            .at(700,300)
            .build();

        return cartaJugada;
    }
}
