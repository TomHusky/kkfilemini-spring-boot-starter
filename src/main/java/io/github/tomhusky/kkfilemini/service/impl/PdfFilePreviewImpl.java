package io.github.tomhusky.kkfilemini.service.impl;

import io.github.tomhusky.kkfilemini.FileAttribute;
import io.github.tomhusky.kkfilemini.service.FileHandlerService;
import io.github.tomhusky.kkfilemini.service.FilePreview;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * <p>
 * pdf直接不处理，可以直接预览
 * <p/>
 *
 * @since 2022/7/7 16:26
 */
@Service
public class PdfFilePreviewImpl implements FilePreview {

    private final FileHandlerService fileHandlerService;

    public PdfFilePreviewImpl(FileHandlerService fileHandlerService) {
        this.fileHandlerService = fileHandlerService;
    }

    @Override
    public File convertToViewFile(File file, FileAttribute fileAttribute) {
        return file;
    }

    @Override
    public File filePreviewHandleToFile(File file, FileAttribute fileAttribute) {
        return file;
    }
}
