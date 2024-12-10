package me.venixpll.ivi.core.openfm;

import com.alibaba.fastjson2.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import me.venixpll.ivi.core.error.InternalExceptionTracker;
import me.venixpll.ivi.core.openfm.objects.RadioStation;
import me.venixpll.ivi.core.openfm.rds.RDS;
import me.venixpll.ivi.core.openfm.rds.impl.AntyRadioRDS;
import me.venixpll.ivi.core.openfm.rds.impl.ChilliZetRDS;
import me.venixpll.ivi.core.openfm.rds.impl.MeloRadioRDS;
import me.venixpll.ivi.core.openfm.rds.impl.RadioZetRDS;
import me.venixpll.ivi.core.utils.HttpUtils;

public class OpenFMIntegration {

    private final String stationList = "https://open.fm/api/radio/stations"; // Filter by contentType == RADIO
    private final String stationStreamInfo = "https://open.fm/_next/data/3hdKfYabtEU2JraV4NW4G/stacje-radiowe/%s.json?params=%s"; // Replace %s with station slug id

    private final List<RDS> rdsInfo = new ArrayList<>();
    {
        this.rdsInfo.add(new AntyRadioRDS());
        this.rdsInfo.add(new ChilliZetRDS());
        this.rdsInfo.add(new MeloRadioRDS());
        this.rdsInfo.add(new RadioZetRDS());
    }

    @Getter
    private final List<RadioStation> radioStations = new ArrayList<>();

    public void queryAllRDS(){
        this.rdsInfo.forEach(rds -> {
            final var radioStation = this.radioStations.stream().filter(rs -> rs.getSlug().equals(rds.getRadioSlug())).findFirst();
            if(radioStation.isEmpty()) return;

            try {
                radioStation.get().setRdsData(rds.readRDS());
            } catch (final Exception e) {
                InternalExceptionTracker.handleException(e);
                radioStation.get().setRdsData(null);
            }
        });
    }

    public void queryRDS(final RadioStation radioStation) throws Exception {
        final var rdsOpt = this.rdsInfo.stream().filter(rds -> rds.getRadioSlug().equals(radioStation.getSlug())).findFirst();
        if(rdsOpt.isEmpty()) return;

        radioStation.setRdsData(rdsOpt.get().readRDS());
    }

    public void fetchStations() throws Exception {
        final var jsonOutput = HttpUtils.getJsonArray(this.stationList);
        jsonOutput.forEach((key,data) -> {
            final var station = (JSONObject)data;
            if(station.containsKey("contentType")){
                if(station.getString("contentType").equals("RADIO")){
                    final var stationName = station.getString("name");
                    final var stationSlug = station.getString("slug");
                    final var stationLogo = station.getString("logoUrl");

                    final var stationObj = new RadioStation(stationName,stationSlug,stationLogo);
                    try {
                        this.fetchStream(stationObj);
                    } catch (final Exception e) {
                        InternalExceptionTracker.handleException(e);
                    }
                    this.radioStations.add(stationObj);
                }
            }
        });

        System.out.println("Loaded " + this.radioStations.size() + " OpenFM radio stations!");
    }

    public void fetchStream(final RadioStation radioStation) throws Exception{
        final var streamInfoURL = String.format(this.stationStreamInfo,radioStation.getSlug(),radioStation.getSlug());
        final var streamInfo = HttpUtils.getJsonObject(streamInfoURL);

        final var pageProps = streamInfo.getJSONObject("pageProps");
        final var stationData = pageProps.getJSONObject("station");

        final var streamUrl = stationData.getString("streamUrl");
        radioStation.setStreamUrl(streamUrl);
    }
}
