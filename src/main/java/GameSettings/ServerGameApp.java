package GameSettings;

import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
//import com.almasb.fxgl.entity.SpawnData;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.entity.Entity;

//import javafx.scene.input.MouseButton;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import component.GameFactory;
import component.UnoLogic;
import component.Carta;

public class ServerGameApp extends GameApplication implements Serializable{
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private List<Carta> mazo;
    //multiplayer
    private Connection<Bundle> conexion;
    private Carta carta_del_servidor;
    @SuppressWarnings("rawtypes")
    private List<Connection> conexiones = new ArrayList<>();


    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Mesa");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGame() {
        mazo = UnoLogic.generarMazo();
        carta_del_servidor = UnoLogic.robarCarta(mazo);
        var server = getNetService().newTCPServer(55555);
        server.setOnConnected(conn -> {
            conexion = conn;
            getExecutor().startAsyncFX(this::onServer);
            //Hay que probar gamefactory
            //getGameWorld().addEntityFactory(new GameFactory(conexion));
        });
        
        System.out.println("Servidor creado");
        server.startAsync();
    }
   
    public void onServer() {
        UnoLogic.mostrar_Carta_del_servidor(carta_del_servidor, conexion);
        UnoLogic.mostrarMazo(mazo);

        conexion.addMessageHandlerFX((connection, bundle) -> {
            switch (bundle.getName()) {
                case "Repartir":
                    System.out.println("Ejecutando repartir cartas");
                    UnoLogic.repartirCartas(mazo, connection);
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_MAZO).forEach(Entity::removeFromWorld);
                    break;

                case "Robar una carta":
                    System.out.println("Ejecutando robar carta");
                    Carta carta = UnoLogic.robarCarta(mazo);
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.CARTA_MAZO).forEach(Entity::removeFromWorld);
                    UnoLogic.mostrarMazo(mazo);
                    UnoLogic.enviarMensaje("Carta robada", carta, connection);
                    break;

                case "Carta a jugar":
                    Carta carta_del_jugador = (Carta) bundle.get("carta");
                    carta_del_servidor = UnoLogic.jugarCarta(carta_del_servidor, carta_del_jugador, connection);
                    UnoLogic.mostrar_Carta_del_servidor(carta_del_servidor, connection);
                    // confia
                    for (Connection conn : conexiones) {
                        UnoLogic.enviarMensaje("Nueva carta del servidor", carta_del_servidor, conn);
                    }
                    break;
            }
        });

        // Agregar la conexión a la lista de conexiones
        conexiones.add(conexion);
    }
    
    public  Connection<Bundle> getConexion(){
        return conexion;
    }
}