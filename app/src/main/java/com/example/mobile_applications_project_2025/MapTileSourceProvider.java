package com.example.mobile_applications_project_2025;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.MapTileIndex;

/**
 * Centralizovan izvor tajlova za mapu (MapTiler) umesto direktnog
 * TileSourceFactory.MAPNIK-a (tile.openstreetmap.org), koji ima strogu
 * politiku korišćenja i lako blokira aplikacije tokom razvoja/testiranja.
 *
 * Besplatan API key: https://cloud.maptiler.com/account/keys/
 */
public class MapTileSourceProvider {

    // TODO: pre produkcije premesti ovaj ključ u local.properties / BuildConfig,
    // ne drži ga hardkodiranog u izvornom kodu.
    private static final String MAPTILER_API_KEY = "wczedE1uhwIQ8Yt10HJa";

    private static final String BASE_URL = "https://api.maptiler.com/maps/streets-v2/256/";

    public static final XYTileSource MAPTILER_STREETS = new XYTileSource(
            "MapTilerStreets",
            0, 20, 256, ".png",
            new String[]{BASE_URL},
            "© MapTiler © OpenStreetMap contributors"
    ) {
        @Override
        public String getTileURLString(long pMapTileIndex) {
            return getBaseUrl()
                    + MapTileIndex.getZoom(pMapTileIndex) + "/"
                    + MapTileIndex.getX(pMapTileIndex) + "/"
                    + MapTileIndex.getY(pMapTileIndex)
                    + mImageFilenameEnding
                    + "?key=" + MAPTILER_API_KEY;
        }
    };
}