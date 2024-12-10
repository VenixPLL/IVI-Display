package me.venixpll.ivi.core.openfm.rds.impl;

import me.venixpll.ivi.core.openfm.objects.RDSData;
import me.venixpll.ivi.core.openfm.rds.RDS;

public class RMF24RDS extends RDS {

    //https://www.rmfon.pl/stacje/playlista_190.json.txt

    public RMF24RDS() {
        super("rmf24");
    }

    @Override
    public RDSData readRDS() throws Exception {
        return null;
    }
}
