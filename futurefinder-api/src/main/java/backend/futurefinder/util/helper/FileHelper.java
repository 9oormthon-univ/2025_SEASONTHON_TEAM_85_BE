package backend.futurefinder.util.helper;

import backend.futurefinder.error.ConflictException;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.error.FileException;
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

    public static List<FileData> convertMultipartFileToFileDataList(List<MultipartFile> files) {
        return files.stream()
                .map(file -> {
                    try {
                        return convertMultipartFileToFileData(file);
                    } catch (IOException e) {
                        throw new FileException(ErrorCode.FILE_CONVERT_FAILED, e);
                    } catch (ConflictException e) {
                        throw e;
                    }
                })
                .collect(Collectors.toList());
    }
}