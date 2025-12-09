package fpl.domain.service;

import fpl.api.FplApiEndPoints;
import fpl.api.dto.EntryInfo;

import java.net.URI;
import java.util.List;

public final class LinkService {

    private LinkService() {}

    public static List<URI> collectTeamEndpoints(List<EntryInfo> entries, int event) {
        return entries.stream()
                .map(team -> FplApiEndPoints.getUri(
                        FplApiEndPoints.PICKS,
                        team.entry(),
                        event
                ))
                .toList();
    }

    public static List<URI> collectTeamTransfersEndpoints(List<EntryInfo> entries) {
        return entries.stream()
                .map(team -> FplApiEndPoints.getUri(
                        FplApiEndPoints.TRANSFERS,
                        team.entry()
                ))
                .toList();
    }
}
