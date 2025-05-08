package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.Building;
import pl.uj.passgo.models.DTOs.event.ImageDto;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.repos.EventRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MediaService {

    @Value("${app.upload-dir}")
    private String imagesPath;
    private static String folderName = "events";

    private final EventService eventService;
    private final EventRepository eventRepository;

    public String uploadImage(MultipartFile file, Long id) {
        Event event = eventService.getEventById(id);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(imagesPath, folderName, filename);

        try {
            file.transferTo(filepath);
            event.setImagePath(filepath.toString());
            eventRepository.save(event);
            return filepath.toString();
        }
        catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed");
        }

    }

    public ImageDto getEventsMainImage(Long id) {
        Event event = eventService.getEventById(id);

        System.out.println("MY DEBUG: " +event.getImagePath());

        Path path = Paths.get(event.getImagePath());

        if(!Files.exists(path))
            throw new FileSystemNotFoundException();
        try {
            byte[] image = Files.readAllBytes(path);
            String imageType = Files.probeContentType(path);

            if (imageType == null) {
                imageType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return new ImageDto(image, imageType);

        }
        catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while fetching image");
        }

    }
}
