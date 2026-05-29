package co.airbnb.server.patterns.singleton.model;

public final class ConfigManager {

    private static final ConfigManager INSTANCE = new ConfigManager();

    private String databaseHost;
    private String paymentApiKey;
    private int maxGuests;

    private ConfigManager() {
    }

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }

    public String getPaymentApiKey() {
        return paymentApiKey;
    }

    public void setPaymentApiKey(String paymentApiKey) {
        this.paymentApiKey = paymentApiKey;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }
}