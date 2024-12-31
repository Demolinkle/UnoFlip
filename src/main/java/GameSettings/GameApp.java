package GameSettings;

import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.List;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.entity.SpawnData;

public class GameApp extends GameApplication {
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Entity mazo;
    private Entity mazoRecarga;
    private final int limiteCartas = 7;
    private final int limiteMazoRecarga = 50;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new GameFactory());
        mazo = spawn("mazo");

        for (int i = 0; i < limiteCartas; i++) {
            Entity carta = spawn("carta_luz"); 
            agregarCartaAlMazo(mazo, carta); 
        }
        mostrarCartas(mazo);
    }

    public void agregarCartaAlMazo(Entity mazo, Entity carta) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        mazoComponent.agregarCarta(carta);
    }

    public void mostrarCartas(Entity mazo) {
        MazoComponent mazoComponent = mazo.getComponent(MazoComponent.class);
        List<Entity> cartas = mazoComponent.getCartas();
        double startX = 100;
        double startY = 300;
        double spacing = 90;

        for (int i = 0; i < cartas.size(); i++) {
            Entity carta = cartas.get(i);
            carta.setPosition(startX + i * spacing, startY);
            getGameWorld().addEntity(carta); 
        }
    }

    @Override
    protected void initInput() {
    }

    public static void main(String[] args) {
        launch(args);
    }
}
