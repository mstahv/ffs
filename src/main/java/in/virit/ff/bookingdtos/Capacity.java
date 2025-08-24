package in.virit.ff.bookingdtos;

public record Capacity(
        Passenger passenger,
        Cargo cargo
) {
    public record Passenger(
            boolean can_book,
            int current,
            int max
    ) {}

    public record Cargo(
            boolean can_book,
            boolean can_autoload,
            int current,
            int max_autoload,
            int max
    ) {}
}