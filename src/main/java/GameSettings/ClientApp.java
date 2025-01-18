package GameSettings;

import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.input.Input;

import javafx.scene.input.MouseButton;
import java.util.ArrayList;
import java.util.List;

import component.GameFactory;
import component.UnoLogic;
import component.Carta;

public class ClientApp extends GameApplication {

    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    //multiplayer
    //private Input clientInput;
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
            //Agregue aqui el gameFactory para el boton de recargar
            getGameWorld().addEntityFactory(new GameFactory(conexion));
            getExecutor().startAsyncFX(() -> onClient());
            System.out.println("Cliente conectado");
        });
        client.connectAsync();
    }

    private void onClient() {
        //UnoLogic.getJuego(conexion);
        // manejo de mensajes recibidos
        Entity mazo_recarga = spawn("mazo_recarga");
        getService(MultiplayerService.class).spawn(conexion, mazo_recarga, "mazo_recarga");
        conexion.addMessageHandlerFX((conexion, bundle) -> {
            switch (bundle.getName()) {
                case "Mano inicial":
                    manoJugador = bundle.get("carta");
                    UnoLogic.mostrarMano(manoJugador, conexion);
                    break;

                case "Carta robada":
                    Carta cartaRobada = (Carta) bundle.get("carta");
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_MAZO).forEach(Entity::removeFromWorld);
                    manoJugador.add(cartaRobada);
                    UnoLogic.mostrarMano(manoJugador, conexion);
                    break;

                case "Nueva carta del servidor":
                    Carta carta_inicial = (Carta) bundle.get("carta");
                    UnoLogic.mostrarCarta(carta_inicial);
                    break;

                case "Nueva carta":
                    Carta aux = (Carta) bundle.get("carta");
                    UnoLogic.mostrarCarta(aux);
                    break;

                case "Eliminar carta":
                    Carta carta = (Carta) bundle.get("carta");
                    manoJugador = UnoLogic.removerCarta(manoJugador, carta);
                    UnoLogic.mostrarMano(manoJugador, conexion);
                    break;
            }
        });
    }
    @Override
    protected void initInput() {
        //clientInput = new Input();

        onBtnDown(MouseButton.SECONDARY, () -> {
            System.out.println("Click derecho");
            Bundle bundle = new Bundle("Repartir");
            conexion.send(bundle);
        });

        //onEvent(clientInput.mockButtonPress(MouseButton.PRIMARY));
    }
}