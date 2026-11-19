package trakkita.service;

import java.util.ArrayList;
import java.util.List;
import trakkita.model.ModernJeepney;
import trakkita.model.ProvincialBus;
import trakkita.model.PUV;
import trakkita.model.TraditionalJeepney;
import trakkita.model.Tricycle;
import trakkita.model.UVExpress;

/** Builds the list of vehicle types. A List<PUV> holds different subclasses (polymorphism). */
public final class PUVCatalog {
    private PUVCatalog() { }

    public static List<PUV> all() {
        List<PUV> list = new ArrayList<>();
        list.add(new TraditionalJeepney());
        list.add(new ModernJeepney());
        list.add(new UVExpress());
        list.add(new ProvincialBus());
        list.add(new Tricycle());
        return list;
    }
}
