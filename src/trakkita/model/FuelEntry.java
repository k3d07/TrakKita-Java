package trakkita.model;

import java.time.LocalDate;
import trakkita.util.Money;
import trakkita.util.Validator;

public class FuelEntry extends LogEntry {
    private double liters;
    private double pricePerLiter;

    public FuelEntry(LocalDate date, double liters, double pricePerLiter) {
        super(date);
        setLiters(liters);
        setPricePerLiter(pricePerLiter);
    }

    public double getLiters() { return liters; }
    public double getPricePerLiter() { return pricePerLiter; }

    public void setLiters(double v) { this.liters = Validator.requirePositive(v, "Liters"); }
    public void setPricePerLiter(double v) { this.pricePerLiter = Validator.requirePositive(v, "Price per liter"); }

    public double getTotalCost() { return liters * pricePerLiter; }

    @Override public String getType() { return "FUEL"; }

    @Override protected String fieldsToCsv() { return liters + "," + pricePerLiter; }

    @Override
    public String summary() {
        return getDate() + " | " + String.format("%.2f", liters) + " L @ " + Money.peso(pricePerLiter)
                + " | total " + Money.peso(getTotalCost());
    }
}
