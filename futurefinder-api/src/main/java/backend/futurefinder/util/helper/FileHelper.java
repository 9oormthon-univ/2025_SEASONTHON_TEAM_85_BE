package backend.futurefinder.util.helper;

import backend.futurefinder.error.AuthorizationException;
import backend.futurefinder.error.ConflictException;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.model.media.FileData;
import backend.futurefinder.model.media.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class FileHelper {

    private FileHelper() {
        // 인스턴스화 방지
    }

    public static FileData convertMultipartFileToFileData(MultipartFile file)
            throws IOException, ConflictException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new ConflictException(ErrorCode.FILE_NAME_COULD_NOT_EMPTY);
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            throw new ConflictException(ErrorCode.NOT_SUPPORT_FILE_TYPE);
        }

        MediaType mediaType = MediaType.fromType(contentType);
        if (mediaType == null) {
            throw new ConflictException(ErrorCode.NOT_SUPPORT_FILE_TYPE);
        }

        return FileData.of(
                file.getInputStream(),
                mediaType,
                originalFilename,
                file.getSize()
        );
    }

    public static List<FileData> convertMultipartFileToFileDataList(List<MultipartFile> files)
            throws IOException, AuthorizationException {
        return files.stream()
                .map(file -> {
                    try {
                        return convertMultipartFileToFileData(file);
                    } catch (IOException | ConflictException e) {
                        // 여기서 바로 RuntimeException으로 감싸거나 다시 던질 수 있음
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}