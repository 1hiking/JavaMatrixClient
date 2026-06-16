package org.hik.examples;

import org.hik.api.MatrixAPIClient;
import org.hik.constants.ChronologicalDirectionEvent;
import org.hik.dtos.payloads.QueryParametersMessages;
import org.hik.dtos.payloads.events.*;
import org.hik.networking.HttpTransport;

import java.lang.System.Logger;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Example {

    private static final Logger logger = System.getLogger(Example.class.getName());

    static void main() {
        var roomId = "!fslCrggPzCliBLCmgo:kde.org";
        MatrixAPIClient matrixAPIClient = MatrixAPIClient.createAsync("https://kde.org", "1hik", "mat_HLDVh2GMImcPxO29hS0l8UXUYDRUFB_0PTNNP").join();
        HttpTransport r = new HttpTransport();

        MatrixEvent textEvent = new MatrixText("Test");

        var id = matrixAPIClient.publishRoomMessage(roomId, textEvent).join();
        logger.log(Logger.Level.INFO, id);
        Path image = Path.of("/home/user/Storage/Imágenes/FROM LAPTOP/Wallpapers/ciym5tjtnke51.jpg");
        var event = matrixAPIClient.uploadResource(image).thenCompose(mxc -> {
                    MatrixEvent imageEvent = new MatrixImage("Image caption", image.getFileName().toString(), URI.create(mxc));
                    return matrixAPIClient.publishRoomMessage(roomId, imageEvent);
                }
        );

        Path file = Path.of("/home/user/Descargas/ankercore.txt");
        var event2 = matrixAPIClient.uploadResource(file).thenCompose(mxc -> {
            MatrixEvent fileEvent = new MatrixFile("File caption", file.getFileName().toString(), URI.create(mxc));
            return matrixAPIClient.publishRoomMessage(roomId, fileEvent);

        });

        Path audio = Path.of("/home/user/Descargas/jokesong.mp3");
        var event3 = matrixAPIClient.uploadResource(audio).thenCompose(mxc -> {
            MatrixEvent fileEvent = new MatrixAudio("Audio caption", audio.getFileName().toString(), URI.create(mxc));
            return matrixAPIClient.publishRoomMessage(roomId, fileEvent);
        });

        CompletableFuture<List<String>> posts = CompletableFuture.allOf(event, event2, event3)
                .thenApply(v -> {
                    List<String> list = new ArrayList<>();
                    list.add(event.join());
                    list.add(event2.join());
                    list.add(event3.join());
                    return list;
                });
        logger.log(Logger.Level.INFO, String.valueOf(posts.join()));

        var messages = matrixAPIClient.getListOfMessages(roomId, ChronologicalDirectionEvent.REVERSE_CHRONOLOGICAL_ORDER, QueryParametersMessages.defaultParams()).join();


        logger.log(Logger.Level.INFO, messages.toString());

    }
}
