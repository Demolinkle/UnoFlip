package GameSettings;

import static com.almasb.fxgl.dsl.FXGL.*;

import java.io.Serializable;
import java.util.List;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;

//import javafx.scene.input.MouseButton;
//import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.net.Connection;

import component.Carta;
import component.MazoComponent;
import component.UnoLogic;

public class ServerGameApp extends GameApplication implements Serializable{
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Entity mazo;
    //multiplayer
    private Connection<Bundle> conexion;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Mesa");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameFactory(conexion));

        var server = getNetService().newTCPServer(55555);
        server.setOnConnected(conn -> {
            conexion = conn;
            getExecutor().startAsyncFX(this::onServer);
        });

        System.out.println("Servidor creado");
        server.startAsync();
    }

    private void onServer() {
        mazo = spawn("mazo");
        Entity auxiliar = spawn("carta_inicial"); 
        // carta_inicial = spawn(cartainicial)
        getService(MultiplayerService.class).spawn(conexion, mazo, "mazo"); //TODO: se puede modificar para que unicamente sea una carta volteada
        getService(MultiplayerService.class).spawn(conexion, UnoLogic.iniciarJuego(mazo), "mazo");
        UnoLogic.mostrarMazo(mazo);
        
        // Manejar mensajes recibidos
        conexion.addMessageHandlerFX((connection, bundle) -> {
            switch (bundle.getName()) {
                case "Repartir":
                    System.out.println("Ejecutando repartir cartas");
                    List<Carta> temp = mazo.getComponent(MazoComponent.class).repartirCartas();
                    Bundle respuesta = new Bundle("Mano inicial");
                    respuesta.put("cartas", (Serializable) temp);
                    connection.send(respuesta);
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_MAZO).forEach(Entity::removeFromWorld);
                    UnoLogic.mostrarMazo(mazo);
                    break;
                
                case "Robar una carta":
                    System.out.println("Ejecutando robar carta");
                    Carta carta = mazo.getComponent(MazoComponent.class).robarCarta();  
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_MAZO).forEach(Entity::removeFromWorld);
                    UnoLogic.mostrarMazo(mazo);
                    Bundle respuestaRobar = new Bundle("Carta robada");
                    respuestaRobar.put("carta", (Serializable) carta);
                    connection.send(respuestaRobar);
                    break;
                
                case "Carta a jugar":
                    System.out.println("Ejecutando carta a jugar");
                    Carta carta_jugada = (Carta) bundle.get("carta"); //la carta jugada por el jugador al jugarla
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_INICIAL).forEach(Entity::removeFromWorld);
                    getService(MultiplayerService.class).spawn(conexion, UnoLogic.jugarCarta(carta_jugada), "carta_inicial");     
                    break;   
            }

        });
    }

    @Override
    protected void initInput() {
        /*
        onBtnDown(MouseButton.PRIMARY, () -> {
            System.out.println("Boton");
        });
        */
    }
    

}