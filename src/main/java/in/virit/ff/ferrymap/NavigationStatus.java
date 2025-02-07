package in.virit.ff.ferrymap;

public enum NavigationStatus {
    UNDER_WAY_USING_ENGINE(0, "Under way using engine"),
    AT_ANCHOR(1, "At anchor"),
    NOT_UNDER_COMMAND(2, "Not under command"),
    RESTRICTED_MANEUVERABILITY(3, "Restricted manoeuverability"),
    CONSTRAINED_BY_DRAUGHT(4, "Constrained by her draught"),
    MOORED(5, "Moored"),
    AGROUND(6, "Aground"),
    ENGAGED_IN_FISHING(7, "Engaged in Fishing"),
    UNDER_WAY_SAILING(8, "Under way sailing"),
    RESERVED_HSC(9, "Reserved for future amendment of Navigational Status for HSC"),
    RESERVED_WIG(10, "Reserved for future amendment of Navigational Status for WIG"),
    RESERVED_11(11, "Reserved for future use"),
    RESERVED_12(12, "Reserved for future use"),
    RESERVED_13(13, "Reserved for future use"),
    AIS_SART_ACTIVE(14, "AIS-SART is active"),
    NOT_DEFINED(15, "Not defined (default)");

    private final int code;
    private final String description;

    NavigationStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static NavigationStatus fromCode(int code) {
        for (NavigationStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown NavigationStatus code: " + code);
    }
}