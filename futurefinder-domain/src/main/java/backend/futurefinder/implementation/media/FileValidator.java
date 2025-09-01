package backend.futurefinder.implementation.media;

import backend.futurefinder.error.ConflictException;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.model.media.FileData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileValidator {

    public void validateFilesNameCorrect(List<FileData> files) {
        for (FileData file : files) {
            if (file.getName() == null || file.getName().isEmpty()) {
                throw new ConflictException(ErrorCode.FILE_NAME_INCORRECT);
            }
        }
    }

    public void validateFileNameCorrect(FileData file) {
        System.out.println("파일 이름: " + file.getName());
        if (file.getName() == null || file.getName().isEmpty()) {
            throw new ConflictException(ErrorCode.FILE_NAME_INCORRECT);
        }
    }
}
