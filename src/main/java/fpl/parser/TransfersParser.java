package fpl.parser;

import fpl.api.dto.TransferDto;

import java.net.URI;
import java.util.List;

public class TransfersParser {

    public static List<TransferDto> parse(URI uri, int event) throws Exception {
        return JsonUtils.loadList(uri, TransferDto.class)
                .stream()
                .filter(transferDto -> transferDto.event() == event)
                .toList();
    }
}
