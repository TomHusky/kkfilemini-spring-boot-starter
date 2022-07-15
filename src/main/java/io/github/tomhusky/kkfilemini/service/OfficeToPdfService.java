package io.github.tomhusky.kkfilemini.service;

import io.github.tomhusky.kkfilemini.OfficeDocumentConverter;
import io.github.tomhusky.kkfilemini.OfficePluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author yudian-it
 */
@Component
public class OfficeToPdfService {

    private static final Logger logger = LoggerFactory.getLogger(OfficeToPdfService.class);
    private final OfficePluginManager officePluginManager;

    public OfficeToPdfService(OfficePluginManager officePluginManager) {
        this.officePluginManager = officePluginManager;
    }

    public void openOfficeToPDF(String inputFilePath, String outputFilePath) {
        office2pdf(inputFilePath, outputFilePath);
    }


    public static void converterFile(File inputFile, String outputFilePathEnd, OfficeDocumentConverter converter) {
        File outputFile = new File(outputFilePathEnd);
        // 假如目标路径不存在,则新建该路径
        if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
            logger.error("创建目录【{}】失败，请检查目录权限！",outputFilePathEnd);
        }
        converter.convert(inputFile, outputFile);
    }


    public void office2pdf(String inputFilePath, String outputFilePath) {
        OfficeDocumentConverter converter = officePluginManager.getDocumentConverter();
        if (null != inputFilePath) {
            File inputFile = new File(inputFilePath);
            // 判断目标文件路径是否为空
            if (null == outputFilePath) {
                // 转换后的文件路径
                String outputFilePathEnd = getOutputFilePath(inputFilePath);
                if (inputFile.exists()) {
                    // 找不到源文件, 则返回
                    converterFile(inputFile, outputFilePathEnd,converter);
                }
            } else {
                if (inputFile.exists()) {
                    // 找不到源文件, 则返回
                    converterFile(inputFile, outputFilePath, converter);
                }
            }
        }
    }

    public static String getOutputFilePath(String inputFilePath) {
        return inputFilePath.replaceAll("."+ getPostfix(inputFilePath), ".pdf");
    }

    public static String getPostfix(String inputFilePath) {
        return inputFilePath.substring(inputFilePath.lastIndexOf(".") + 1);
    }

}
