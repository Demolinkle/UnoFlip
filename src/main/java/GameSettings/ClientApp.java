package GameSettings;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
//import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.net.Connection;

import component.Carta;

import com.almasb.fxgl.entity.SpawnData;

//import component.UnoLogic;

import com.almasb.fxgl.multiplayer.MultiplayerService;
import component.UnoLogic;
import javafx.scene.input.MouseButton;
import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.List;

public class ClientApp extends GameApplication {

    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    //multiplayer
    protected Connection<Bundle> conexion;
    private Input clientInput;
    //private SpawnData data;
    //private static final int MAX_CARTAS_POR_FILA = 10;
    //private static final double ESPACIADO_HORIZONTAL = 55;
    //private static final double ESPACIADO_VERTICAL = 30;
    double startX = 50;
    double startY = 300;
    
    

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Jugador");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameFactory(conexion));
        
        var client = getNetService().newTCPClient("localhost", 55555);
        client.setOnConnected(conn -> {
            conexion = conn;
            System.out.println(conexion);
            getExecutor().startAsyncFX(() -> onClient());
        });
        System.out.println("Cliente conectado");
        client.connectAsync();
    }

    private void onClient() {
        getService(MultiplayerService.class).addEntityReplicationReceiver(conexion, getGameWorld());
        getService(MultiplayerService.class).addInputReplicationSender(conexion, getInput());
        getService(MultiplayerService.class).addPropertyReplicationReceiver(conexion, getWorldProperties());

        // Manejar mensajes recibidos
        conexion.addMessageHandlerFX((conexion, bundle) -> {
            if (bundle.getName().equals("Mano inicial")) {
                @SuppressWarnings("unchecked")
                List<Carta> manoInicial = (List<Carta>) bundle.get("cartas");
                UnoLogic.mostrarMano(manoInicial);
            }
        });
    }

    @Override
    protected void initInput() {
        clientInput = new Input();

        onBtnDown(MouseButton.PRIMARY, () -> {
            System.out.println("Boton");
            Bundle bundle = new Bundle("Repartir");
            conexion.send(bundle);
        });
    }
}