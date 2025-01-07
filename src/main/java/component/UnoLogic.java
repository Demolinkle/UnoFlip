package component;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.input.MouseButton;
import static com.almasb.fxgl.dsl.FXGL.*;
import java.util.List;

import java.util.ArrayList;


public class UnoLogic extends Component {

    private static final int MAX_CARTAS_POR_FILA = 10;
    private static final double ESPACIADO_HORIZONTAL = 55;
    private static final double ESPACIADO_VERTICAL = 30;

    private Entity cartaInicial;
    private List<Entity> cartasMovidas;  // Lista de cartas movidas

    public UnoLogic(Entity cartaInicial) {
        this.cartaInicial = cartaInicial;
        this.cartasMovidas = new ArrayList<>(); // Inicializamos la lista de cartas movidas
    }

    public static void mostrarCartas(Entity mazo, UnoLogic unoLogic) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        int cartasRepartidas = mazoComponent.getCartasRepartidas();

        List<Carta> cartasARepartir;

        if (cartasRepartidas == 0) {
            cartasARepartir = mazoComponent.repartirCartas(7);
        } else {
            cartasARepartir = mazoComponent.repartirCartas(1);
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

    private void moverCarta(Entity carta) {
        // Mover la carta a la posición de carta_inicial
        carta.setPosition(cartaInicial.getPosition());

        // Asegurar que esta carta sea la última movida y se agregue a la lista de cartas movidas
        cartasMovidas.add(carta);
    }

    private void eliminarCartasAnteriores() {
        // Eliminar la carta más baja en la lista (la más antigua)
        if (cartasMovidas.size() > 1) {
            Entity cartaAEliminar = cartasMovidas.get(0);
            cartaAEliminar.removeFromWorld();
            cartasMovidas.remove(0); // Eliminamos la carta de la lista de cartas movidas
        }
    }
}
