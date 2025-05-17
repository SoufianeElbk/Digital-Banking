package ma.enset.digitalbanking.exceptions;

public class BalanceAccountInsufficient extends Exception {
    public BalanceAccountInsufficient(String message) {
        super(message);
    }
}
