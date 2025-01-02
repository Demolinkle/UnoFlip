package GameSettings;

import static com.almasb.fxgl.dsl.FXGL.*;
import component.UnoLogic;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;

public class ServerGameApp extends GameApplication {
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Entity mazo;
    private Entity mazo2;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new GameFactory());
        mazo = spawn("mazo");
        mazo2 = spawn("mazo");
        UnoLogic.mostrarCartas(mazo);
        UnoLogic.repartirPrimeras7Cartas(mazo, mazo2);
    }

    @Override
    protected void initInput() {
    }
}
