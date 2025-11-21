package wisoft.tddstart.commerce.result;

public record PageCarrier<T> (T[] items, String continuationToken) {
}
