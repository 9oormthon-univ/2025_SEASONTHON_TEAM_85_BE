package backend.futurefinder.implementation.media;


import backend.futurefinder.model.media.FileCategory;
import backend.futurefinder.model.media.FileData;
import backend.futurefinder.model.media.Media;
import backend.futurefinder.model.media.MediaType;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.util.AsyncJobExecutor;
import ch.qos.logback.core.joran.sanity.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class FileHandler {

    private final FileAppender fileAppender;
    private final FileRemover fileRemover;
    private final FileGenerator fileGenerator;
    private final FileValidator fileValidator;
    private final AsyncJobExecutor asyncJobExecutor;

    public List<Media> handleNewFiles(UserId userId, List<FileData> files, FileCategory category) {
        fileValidator.validateFilesNameCorrect(files);
        List<Map.Entry<FileData, Media>> mediaWithFiles = fileGenerator.generateMedias(files, userId, category);
        try {
            asyncJobExecutor.executeAsyncJobs(mediaWithFiles, pair ->
                    fileAppender.appendFile(pair.getKey(), pair.getValue()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mediaWithFiles.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public Media handleNewFile(UserId userId, FileData file, FileCategory category) {
        fileValidator.validateFileNameCorrect(file);
        Media media = fileGenerator.generateMedia(file, userId, category);
        try {
            asyncJobExecutor.executeAsyncJob(media, item -> fileAppender.appendFile(file, media));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return media;
    }

    public void handleOldFile(Media media) {
        if (!MediaType.IMAGE_BASIC.equals(media.getType())) {
            try {
                asyncJobExecutor.executeAsyncJob(media, fileRemover::removeFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleOldFiles(List<Media> medias) {
        try {
            asyncJobExecutor.executeAsyncJobs(medias, fileRemover::removeFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}