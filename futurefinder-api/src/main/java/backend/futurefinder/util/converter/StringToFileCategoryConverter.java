package backend.futurefinder.util.converter;

import backend.futurefinder.model.media.FileCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToFileCategoryConverter implements Converter<String, FileCategory> {

    @Override
    public FileCategory convert(String source) {
        return FileCategory.valueOf(source.toUpperCase());
    }
}