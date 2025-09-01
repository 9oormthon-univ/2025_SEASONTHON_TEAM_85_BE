package backend.futurefinder.implementation.media;

import backend.futurefinder.external.ExternalFileClient;
import backend.futurefinder.model.media.FileData;
import backend.futurefinder.model.media.Media;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileAppender {

    private final ExternalFileClient externalFileClient;

    public void appendFile(FileData file, Media media) {
        externalFileClient.uploadFile(file, media);
    }
}