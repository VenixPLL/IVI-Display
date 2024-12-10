package me.venixpll.ivi.core.openfm.objects;

import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class RDSData {

    private final String title;
    private final String artist;
    private final Date startDate;
    private final Date endDate;


}
