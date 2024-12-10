package me.venixpll.ivi.core.openfm.rds.impl;

import com.alibaba.fastjson2.JSONObject;
import me.venixpll.ivi.core.openfm.objects.RDSData;
import me.venixpll.ivi.core.openfm.rds.RDS;
import me.venixpll.ivi.core.utils.HttpUtils;

public class MeloRadioRDS extends RDS {

    // https://rds.eurozet.pl/reader/var/zetgold.json?callback=rdsData

    public MeloRadioRDS() {
        super("meloradio");
    }

    @Override
    public RDSData readRDS() throws Exception {
        final var obj = HttpUtils.getRaw("https://rds.eurozet.pl/reader/var/zetgold.json?callback=rdsData");
        final var split = obj.split("rdsData\\(",2)[1].split("\\)",2)[0];
        final var json = JSONObject.parse(split);

        final var nowPlaying = json.getJSONObject("now");
        final var title = nowPlaying.getString("title");
        final var artist = nowPlaying.getString("artist");
        final var startDate = nowPlaying.getDate("startDate");
        final var endDate = nowPlaying.getDate("duration");

        final var rds = new RDSData(title,artist,startDate,endDate);
        return rds;
    }
}
