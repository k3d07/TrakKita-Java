package trakkita.service;

public class BreakEvenCalculator {
    /**
     * Same formulas as the C version:
     * fuel = (distance / efficiency) x price; total = fuel + boundary + maintenance + wages + other;
     * gross = passengers x fare; breakEvenPax = ceil(total / fare); coverage = gross / total x 100.
     */
    public BreakEvenResult calculate(BreakEvenInput in) {
        double fuelCost = (in.getRouteDistance() / in.getFuelEfficiency()) * in.getFuelPrice();
        double totalCost = fuelCost + in.getBoundaryFee() + in.getMaintenance()
                + in.getDriverWages() + in.getOtherCosts();
        int totalPax = in.getTripsPerDay() * in.getPaxPerTrip();
        double gross = totalPax * in.getFarePerPax();
        double net = gross - totalCost;
        int bePax = (int) Math.ceil(totalCost / in.getFarePerPax());
        int bePerTrip = (int) Math.ceil((double) bePax / in.getTripsPerDay());
        double coverage = totalCost == 0 ? 100.0 : gross / totalCost * 100.0;
        return new BreakEvenResult(fuelCost, totalCost, totalPax, gross, net, bePax, bePerTrip, coverage);
    }
}
