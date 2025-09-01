package backend.futurefinder.implementation.media;

import backend.futurefinder.model.media.FileCategory;
import backend.futurefinder.model.media.FileData;
import backend.futurefinder.model.media.Media;
import backend.futurefinder.model.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileGenerator {

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Media generateMedia(FileData file, UserId userId, FileCategory category) {
        return Media.upload(baseUrl, bucket, category, userId, file.getName(), file.getContentType());
    }

    public List<Map.Entry<FileData, Media>> generateMedias(List<FileData> files, UserId userId, FileCategory category) {
        return files.stream()
                .map(file -> new AbstractMap.SimpleEntry<>(
                        file,
                        Media.upload(baseUrl, bucket, category, userId, file.getName(), file.getContentType())
                ))
                .collect(Collectors.toList());
    }
}