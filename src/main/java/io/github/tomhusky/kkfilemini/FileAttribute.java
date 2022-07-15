package io.github.tomhusky.kkfilemini;

import lombok.Data;

/**
 * <p>
 * 文件属性
 * <p/>
 *
 * @author luowj
 * @version 1.0
 * @since 2022/5/9 10:32
 */
@Data
public class FileAttribute {
    private FileType type;
    private String suffix;
    private String name;
    private String prefix;
    private String url;
    private String fileKey;

    public FileAttribute() {
    }

    public FileAttribute(FileType type, String prefix, String suffix, String name, String url) {
        this.type = type;
        this.suffix = suffix;
        this.prefix = prefix;
        this.name = name;
        this.url = url;
    }

}
