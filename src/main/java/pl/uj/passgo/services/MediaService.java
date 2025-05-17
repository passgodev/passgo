package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
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

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MediaService {

    private final ProjectInfoAutoConfiguration projectInfoAutoConfiguration;
    private final TaskExecutionProperties taskExecutionProperties;
    @Value("${app.upload-dir}")
    private String imagesPath;
    private final static String EVENTS_FOLDER_NAME = "events";

    private final EventService eventService;
    private final EventRepository eventRepository;

    public String uploadImage(MultipartFile file, Long id) {
        Event event = eventService.getEventById(id);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(imagesPath, EVENTS_FOLDER_NAME, filename);

        try {
            file.transferTo(filepath);
            event.setImagePath(filepath.toString());
            eventRepository.save(event);
            return filepath.toString();
        }
        catch (IOException e) {
            log.error("Unexpected error while uploading image for event {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed");
        }

    }

    public ImageDto getEventsMainImage(Long id) {
        Event event = eventService.getEventById(id);
        String imgPath = event.getImagePath();

        System.out.println(event.getId() + ", path: " + event.getImagePath());

        if(imgPath == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found for event: " + id);

        Path path = Paths.get(imgPath);

        if(!Files.exists(path)) {
            log.error("Filepath: {} does not exist.", path);
            throw new FileSystemNotFoundException();
        }
        try {
            byte[] image = Files.readAllBytes(path);
            String imageType = Files.probeContentType(path);

            if (imageType == null) {
                imageType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return new ImageDto(image, imageType);

        }
        catch (IOException e){
            log.error("Unexpected error while fetching image for file path {}: {}", path, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while fetching image");
        }

    }
}
