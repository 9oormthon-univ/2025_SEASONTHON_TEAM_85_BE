package backend.futurefinder.external;

import backend.futurefinder.error.ConflictException;
import backend.futurefinder.error.ErrorCode;
import backend.futurefinder.model.media.FileData;
import backend.futurefinder.model.media.Media;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalFileClientImpl implements ExternalFileClient {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public void uploadFile(FileData file, Media media) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(media.getType().value());

            amazonS3.putObject(bucket, media.getPath(), file.getInputStream(), metadata);
        } catch (AmazonServiceException e) {
            log.error("S3 업로드 실패 - path: {}, error: {}", media.getPath(), e.getErrorMessage(), e);
            throw new ConflictException(ErrorCode.FILE_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("파일 업로드 중 예외 발생 - path: {}", media.getPath(), e);
            throw new ConflictException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void removeFile(Media media) {
        try {
            amazonS3.deleteObject(bucket, media.getPath());
        } catch (AmazonServiceException e) {
            log.error("S3 삭제 실패 - path: {}, error: {}", media.getPath(), e.getErrorMessage(), e);
            throw new ConflictException(ErrorCode.FILE_DELETE_FAILED);
        } catch (Exception e) {
            log.error("파일 삭제 중 예외 발생 - path: {}", media.getPath(), e);
            throw new ConflictException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    public String getPublicUrl(Media media) {
        return amazonS3.getUrl(bucket, media.getPath()).toString();
    }
}
