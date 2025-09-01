package backend.futurefinder.external;

import backend.futurefinder.model.media.FileData;
import backend.futurefinder.model.media.Media;

public interface ExternalFileClient {

    void uploadFile(FileData file, Media media);

    void removeFile(Media media);
}