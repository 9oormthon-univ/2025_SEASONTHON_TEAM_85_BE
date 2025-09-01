package backend.futurefinder.implementation.media;

import backend.futurefinder.external.ExternalFileClient;
import backend.futurefinder.model.media.Media;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileRemover {

    private final ExternalFileClient externalFileClient;

    public void removeFile(Media media) {
        externalFileClient.removeFile(media);
    }
}