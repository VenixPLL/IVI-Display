package me.venixpll.ivi.core.openfm.rds;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.venixpll.ivi.core.openfm.objects.RDSData;

@RequiredArgsConstructor
public abstract class RDS {

    @Getter
    private final String radioSlug;

    public abstract RDSData readRDS() throws Exception;

}
