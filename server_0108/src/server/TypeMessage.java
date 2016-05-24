package server;

/**
 * Enum per rappresentare il tipo di file
 *
 * @author Francesco Taioli
 *
 */
public enum TypeMessage {
    FILE("FILE"),
    AUDIO("AUDIO"),
    MESSAGGIO("MESSAGGIO"),
    FOTO("FOTO");

    private final String text;

    /**
     * @param text
     */
    private TypeMessage(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
