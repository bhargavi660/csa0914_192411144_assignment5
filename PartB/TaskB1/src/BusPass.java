package assignment5.b1;

public class BusPass {
    private final String passId;
    private final String registerNumber;
    private final String name;
    private final String routeNumber;
    private final String boardingStop;
    private final String validityDate;
    private final double amountPaid;

    public BusPass(String passId, String registerNumber, String name, String routeNumber,
                   String boardingStop, String validityDate, double amountPaid) {
        this.passId = passId;
        this.registerNumber = registerNumber;
        this.name = name;
        this.routeNumber = routeNumber;
        this.boardingStop = boardingStop;
        this.validityDate = validityDate;
        this.amountPaid = amountPaid;
    }

    public String getPassId() { return passId; }
    public String getRegisterNumber() { return registerNumber; }
    public String getName() { return name; }
    public String getRouteNumber() { return routeNumber; }
    public String getBoardingStop() { return boardingStop; }
    public String getValidityDate() { return validityDate; }
    public double getAmountPaid() { return amountPaid; }

    public String toDelimitedString() {
        return String.join("|", passId, registerNumber, name, routeNumber,
                boardingStop, validityDate, String.format(java.util.Locale.ROOT, "%.2f", amountPaid));
    }

    @Override
    public String toString() {
        return toDelimitedString();
    }
}
