package pl.uj.passgo.models.DTOs.event;

public record ImageDto(
        byte[] data,
        String contentType
) {
}
