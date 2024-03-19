package entity.datatypes;

public enum Sex {
    M,
    F;

    public static Sex getSex(String sex) {
        return switch (sex) {
            case "M" -> M;
            case "F" -> F;
            default -> null;
        };
    }
}
