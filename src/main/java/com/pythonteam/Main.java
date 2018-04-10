package com.pythonteam;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import jPicUsb.iface;

public class Main{

    public static byte cmd_FW = 99;
    public static byte cmd_LED = 88;

    public static void main(String[] args ){
        try {
            iface.load();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //configuramos el vid_pid del dispositivo asi podemos usar las funciones "rapidas" de la interfaz
        iface.set_vidpid("vid_04d8&pid_000b");

        //configuramos la instancia del dispositivo asi podemos usar las funciones "rapidas" de la interfaz
        iface.set_instance(0);

        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(io.vertx.core.http.HttpMethod.GET)
                .allowedMethod(io.vertx.core.http.HttpMethod.POST)
                .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
                .allowedHeader("Access-Control-Request-Method")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Content-Type"));

        router.route().handler(BodyHandler.create());

        router.route("/prender").handler(Main::prender);

        router.route("/checar").handler(Main::checar);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);

    }

    private static void prender(RoutingContext routingContext) {
        encender_led(routingContext.getBodyAsJson());
        try {
            routingContext.response()
                    .setStatusCode(200)
                    .end("ok");
        } catch (Exception e) {
            routingContext.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .setStatusCode(404).end("{\"error\":\""+e.getMessage()+"\"}");
        }
    }

    public static void send_command(byte command) {
        byte[] out = {command};
        iface.QWrite(out, 1, 1000);
    }

    public static void send_command(byte command, byte param1) {
        byte[] out = {command, param1};
        iface.QWrite(out, 2, 1000);
    }

    public static byte[] read_response(int maxbytes) {
        return iface.QRead(maxbytes, 500);
    }

    private void encender_leds(byte LED) {
        send_command(cmd_LED, LED);
    }

    private static void encender_led(JsonObject bodyAsJson) {
        Led led = bodyAsJson.mapTo(Led.class);

        byte LED = 0x00;
        if (led.isLed1()) {
            LED |= 0x01;
        }
        if (led.isLed2()) {
            LED |= 0x02;
        }
        if (led.isLed3()) {
            LED |= 0x04;
        }
        if (led.isLed4()) {
            LED |= 0x08;
        }
        if (led.isLed5()) {
            LED |= 0x10;
        }
        if (led.isLed6()) {
            LED |= 0x20;
        }
        if (led.isLed7()) {
            LED |= 0x40;
        }
        if (led.isLed8()) {
            LED |= 0x80;
        }
        send_command(cmd_LED, LED);
    }
    private static void checar(RoutingContext routingContext) {
        try {
            send_command(cmd_FW);
            String response = new String(read_response(64), "utf-8");
            //verificamos que se hallan recibido datos
            if (response.length() > 0) {
                routingContext.response()
                        .setStatusCode(200)
                        .end(Json.encode(response));
            } else {
                routingContext.response()
                        .setStatusCode(404)
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end("{\"error\":\"Dispositivo no conectado\"}");
            }
        } catch (Exception ex) {
            routingContext.response()
                    .setStatusCode(404)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end("{\"error\":\""+ex.getMessage()+"\"}");
        }
    }

}
