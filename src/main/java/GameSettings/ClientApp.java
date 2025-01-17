package GameSettings;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.net.Connection;
import java.util.List;
import component.Carta;
import java.util.ArrayList;
import java.io.Serializable;
import component.UnoLogic;

import com.almasb.fxgl.multiplayer.MultiplayerService;
import static com.almasb.fxgl.dsl.FXGL.*;


import javafx.scene.input.MouseButton;

public class ClientApp extends GameApplication {

    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    //multiplayer
    private Input clientInput;
    private Connection<Bundle> conexion;
    private List<Carta> manoJugador = new ArrayList<>();
    //private List<Carta> manoJugador = new List<>();

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Jugador");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGame() {
        var client = getNetService().newTCPClient("localhost", 55555);
        client.setOnConnected(conn -> {
            conexion = conn;
            getGameWorld().addEntityFactory(new GameFactory(conexion));
            getExecutor().startAsyncFX(() -> onClient());
        });
        System.out.println("Cliente conectado");
        client.connectAsync();
    }

    private void onClient() {
        getService(MultiplayerService.class).addEntityReplicationReceiver(conexion, getGameWorld());
        getService(MultiplayerService.class).addInputReplicationSender(conexion, getInput());
        getService(MultiplayerService.class).addPropertyReplicationReceiver(conexion, getWorldProperties());
        conexion.addMessageHandlerFX((conexion, bundle) -> {
            switch (bundle.getName()) {
                case "Mano inicial":
                    manoJugador = bundle.get("cartas");
                    UnoLogic.mostrarMano(manoJugador, conexion);
                    break;

                case "Carta robada":
                    Carta cartaRobada = (Carta) bundle.get("carta");
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_MAZO).forEach(Entity::removeFromWorld);
                    manoJugador.add(cartaRobada);
                    UnoLogic.mostrarMano(manoJugador, conexion);
                    break;
                    
                case "Carta inicial del juego":
                    Carta carta_inicial = (Carta) bundle.get("carta");
                    Entity carta = entityBuilder()
                    .type(GameFactory.EntityType.CARTA)
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", carta_inicial.getColor(), carta_inicial.getId()), 60, 100))
                    .at(700, 300)
                    .onClick(e -> {
                        Bundle mensaje = new Bundle("Carta a jugar");
                        mensaje.put("carta", (Serializable) carta_inicial);
                        conexion.send(mensaje);      
                    })
                    .build();
                    getGameWorld().addEntity(carta);
                    break;

                case "Nueva carta":
                    Carta aux = (Carta) bundle.get("carta");
                    Entity carta_nueva = entityBuilder()
                    .type(GameFactory.EntityType.CARTA)
                    .viewWithBBox(texture(String.format("luz/%s/%s.png", aux.getColor(), aux.getId()), 60, 100))
                    .at(700, 300)
                    .build();
                    getGameWorld().addEntity(carta_nueva);
                    break;    
            }
        });
    }
    @Override
    protected void initInput() {

        clientInput = new Input();

        onBtnDown(MouseButton.SECONDARY, () -> {
            System.out.println("Click derecho");
            Bundle bundle = new Bundle("Repartir");
            conexion.send(bundle);
        });

        //onEvent(clientInput.mockButtonPress(MouseButton.PRIMARY));
    }
}