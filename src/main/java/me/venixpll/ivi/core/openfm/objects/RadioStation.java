package me.venixpll.ivi.core.openfm.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class RadioStation {

    private final String name;
    private final String slug;
    private final String logoUrl;

    @Setter
    private String streamUrl;

    @Setter
    private RDSData rdsData = null;

}
