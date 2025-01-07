package component;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.Input;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import static com.almasb.fxgl.dsl.FXGL.*;
import java.util.List;

public class UnoLogic extends Component {

    // Constantes para espaciado de cartas
    private static final int MAX_CARTAS_POR_FILA = 10;
    private static final double ESPACIADO_HORIZONTAL = 55;
    private static final double ESPACIADO_VERTICAL = 30;

    private Entity cartaInicial; // Entidad para la carta inicial

    // Constructor donde pasas la carta inicial
    public UnoLogic(Entity cartaInicial) {
        this.cartaInicial = cartaInicial;
    }

    public static void mostrarCartas(Entity mazo, Entity cartaInicial) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        int cartasRepartidas = mazoComponent.getCartasRepartidas();

        List<Carta> cartasARepartir;

        // Si es la primera vez que se hace clic, repartir 7 cartas
        if (cartasRepartidas == 0) {
            cartasARepartir = mazoComponent.repartirCartas(7);
        } else {
            // En los clics posteriores, repartir una sola carta
            cartasARepartir = mazoComponent.repartirCartas(1);
        }

        double startX = 50 + (cartasRepartidas % MAX_CARTAS_POR_FILA); 
        double startY = 400 + (cartasRepartidas / MAX_CARTAS_POR_FILA) * ESPACIADO_VERTICAL;
        int i = cartasRepartidas; 
        for (Carta carta : cartasARepartir) {
            Entity aux = entityBuilder() // luz/verde/5.png
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", carta.getColor(), carta.getId()), 60, 100))
                    .build();

            // Coloca la carta en la nueva posición
            aux.setPosition(startX + (i % MAX_CARTAS_POR_FILA) * ESPACIADO_HORIZONTAL, startY + (i / MAX_CARTAS_POR_FILA) * ESPACIADO_VERTICAL);
            i++;

           onBtnDown(MouseButton.PRIMARY, () -> {
                apilarCartas(aux);
            });
            getGameWorld().addEntity(aux);
        }
    }

    public void apilarCartas(Entity carta) {
    
        carta.setPosition(cartaInicial.getPosition());
    }
}
