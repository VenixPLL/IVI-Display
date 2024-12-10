package me.venixpll.ivi.core.javalin.handler.music;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import java.util.concurrent.atomic.AtomicInteger;
import me.venixpll.ivi.IVIDisplay;
import me.venixpll.ivi.core.javalin.JavalinHandler;
import org.eclipse.jetty.http.HttpStatus;

public class RadioRelay extends JavalinHandler {

    @Override
    public void addHandler(final Javalin javalin, final IVIDisplay rootApp) throws Exception {
        javalin.get("stations",ctx -> {
            final var stations = new JSONArray();

            final var index = new AtomicInteger(0);
            IVIDisplay.getInstance().getOpenFM().getRadioStations().forEach(radioStation -> {
                final var station = new JSONObject();

                station.put("index",index.getAndAdd(1));
                station.put("name",radioStation.getName());
                station.put("slug",radioStation.getSlug());
                station.put("logo",radioStation.getLogoUrl());
                station.put("stream",radioStation.getStreamUrl());

                if(radioStation.getRdsData() != null) {
                    final var rds = new JSONObject();
                    rds.put("artist", radioStation.getRdsData().getArtist());
                    rds.put("title", radioStation.getRdsData().getTitle());
                    station.put("rds", rds);
                }else{
                    final var rds = new JSONObject();
                    rds.put("artist", "Unknown Artist");
                    rds.put("title", "Unknown Title");
                    station.put("rds", rds);
                }

                if(radioStation.getStreamUrl() != null) {
                    if (!radioStation.getStreamUrl().endsWith(".m3u8"))
                        stations.add(station);
                }
            });

            ctx.contentType(ContentType.JSON);
            ctx.result(stations.toString());
            ctx.status(HttpStatus.OK_200);
        });

        javalin.get("stream",ctx -> {
           final var slug = ctx.queryParam("slug");

           final var station = IVIDisplay.getInstance().getOpenFM().getRadioStations().stream().filter(s -> s.getSlug().equals(slug)).findFirst();
           if(station.isPresent()){
               final var radioStation = station.get();
               IVIDisplay.getInstance().getOpenFM().fetchStream(radioStation);

               final var stationJson = new JSONObject();

               stationJson.put("name",radioStation.getName());
               stationJson.put("slug",radioStation.getSlug());
               stationJson.put("logo",radioStation.getLogoUrl());
               stationJson.put("stream",radioStation.getStreamUrl());

               if(radioStation.getStreamUrl() != null) {
                   if (radioStation.getStreamUrl().endsWith(".m3u8")) {
                       ctx.status(HttpStatus.NOT_FOUND_404);
                       ctx.contentType(ContentType.JSON);
                       ctx.result(stationJson.toString());
                       return;
                   }
               }

               if(radioStation.getRdsData() != null) {
                   final var rds = new JSONObject();
                   rds.put("artist", radioStation.getRdsData().getArtist());
                   rds.put("title", radioStation.getRdsData().getTitle());
                   stationJson.put("rds", rds);
               }

               ctx.contentType(ContentType.JSON);
               ctx.result(stationJson.toString());
               ctx.status(HttpStatus.OK_200);
           }
        });
    }
}
