package backend.futurefinder.model.media;

import backend.futurefinder.model.user.UserId;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Media {

    private final FileCategory category;
    private final String url;
    private final MediaType type;
    private final int index;
    private final String path;

    private Media(FileCategory category, String url, MediaType type, int index, String path) {
        this.category = category;
        this.url = url;
        this.type = type;
        this.index = index;
        this.path = path;
    }

    public static Media upload(
            String baseUrl,
            String bucketName,
            FileCategory category,
            UserId userId,
            String fileName,
            MediaType type
    ) {
        String randomId = UUID.randomUUID().toString();
        String path = String.format("%s/%s/%s/%s", category.name(), userId.getId(), randomId, fileName);
        String url = String.format("%s/%s/%s", baseUrl, bucketName, path);
        int index = extractIndexFromFileName(fileName);

        return new Media(category, url, type, index, path);
    }


    public static Media of(FileCategory category, String imagePath, int index, MediaType type) {
        return new Media(category, imagePath, type, index, "");
    }

    private static int extractIndexFromFileName(String fileName) {
        try {
            return Integer.parseInt(fileName.split("\\.")[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return 0; // 또는 예외 던지기
        }
    }
}
