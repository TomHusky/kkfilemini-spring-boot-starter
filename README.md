# 工程简介

> 介绍：基于开源项目KkFileView源码提取出,封装成仅用于 Office文档预览(格式转换) 功能的一个通用组件; 原理是把Word转成PDF,PPT转成PDF,Excel转成HTML;
> 利用浏览器可以直接打开PDF和HTML的特点实现在线预览;
>
> 环境安装: 目前支持OpenOffice或LibreOffice实现文档格式转换,请自行安装(推荐使用LibreOffice,转出的格式相对较好)
> 安装 LibreOffice教程: https://www.cnblogs.com/lwjQAQ/p/16505854.html
>
> LibreOffice下载:  https://zh-cn.libreoffice.org/
>
> OpenOffice下载: http://www.openoffice.org/
>
> 项目运行环境: `jdk1.8`  `SpringBoot2.0`
>
>
> ****在线试用地址：https://tools.go996.cn/docView/****
> 
> ****Demo地址：https://github.com/TomHusky/kkfilemini-spring-boot-starter-demo****

**maven依赖坐标**

目前暂未上传maven中央仓库，需要自行下载源码install之后使用

```xml
<dependency>
    <groupId>io.github.tomhusky</groupId>
    <artifactId>office-preview-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

**配置说明**

| 配置名称  | 解释  | 默认值|
| ------------ | ------------ | ------------ |
| office.plugin.home  | office组件的安装路径 | 默认为LibreOffice / OpenOffice的安装位置 |
| office.plugin.task.timeout  | 文件转换的超时时间，超过这个时间则自动转换失败 |  5m |
| office.plugin.server.ports  |  office组件占用的端口 | 2001,2002 |
| office.cache.enabled | 开启缓存 (true/false) | true |
| office.cache.type  |  缓存类型 default(RocksDB),redis(Redisson),jdk(Map) | default |
| office.cache.file.dir |  文档转换的缓存路径,可以填绝对路径，相对路径是项目根路径 | 相对路径office-file文件夹 |
| office.cache.clean.cron | 清理缓存的定时任务表达式，仅在开启缓存时生效 | 0 0 1 * * * |

***yml***

```yml
office:
  plugin:
    home: C:\\Program Files\\LibreOffice
    task:
      timeout: 1M
    server:
      ports: 2001,2002
  cache:
    enabled: true
    type: default
    file:
      dir: D:\project\demo
    clean:
      cron: 0 0 1 * * *
```

***properties***

```properties
office.plugin.home=C:\\Program Files\\LibreOffice
office.plugin.task.timeout=1M
office.plugin.server.ports=2001,2002
office.cache.enabled=true
office.cache.type=default
office.cache.file.dir=officeFile
office.cache.clean.cron=0 0 1 * * *
```

**快速使用**

直接注入

```java
@Resource
private KKFileViewComponent kkFileViewComponent;
```

OfficeFileViewComponent中提供的方法

| 方法名称  | 解释  |
| ------------ | ------------ |
| File convertViewFile(File file)  | 直接转换成可以预览的文件 word,ppt转成pdf excel转成html|
| String addFileToCache(File file)   | 添加文件到缓存，添加的时候直接转换，返回可以预览的文件编号 | 
| void addFileToCache(File file, String fileNo)  |  添加文件到缓存，指定文件编号（保证唯一） | 
| boolean cacheExistFile(String fileNo) | 判断文件编号是否存在缓存的文件 |
| void viewCacheFile(HttpServletResponse response, String fileNo)  |  根据文件编号预览文件，直接使用response输出流；并且response的header加入file-type字段用于判断文件类型（pdf/html）|

代码示例

```java

@Service
public class FileServiceImpl implements FileService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private KKFileViewComponent fileViewComponent;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, FileCacheVo> tempFilePathCache = new ConcurrentHashMap<>();

    private static final Set<String> ALLOW_FILE_TYPE = CollUtil.set(false, ".doc", ".docx", ".pdf", ".xlsx", ".xls", ".pptx", ".ppt");

    private static final List<FileInfoRespVo> FILE_SET = new CopyOnWriteArrayList<>();

    @Override
    public FileInfoRespVo uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;

        String fileType = originalFilename.substring(originalFilename.lastIndexOf('.'));
        if (!ALLOW_FILE_TYPE.contains(fileType)) {
            throw new RuntimeException("不支持的文件类型！");
        }
        try {
            String fileNo = IdUtil.getSnowflakeNextIdStr();
            File tempFile = File.createTempFile(fileNo, fileType);
            FileUtil.writeFromStream(file.getInputStream(), tempFile, true);

            FileInfoRespVo fileInfoRespVo = new FileInfoRespVo();
            fileInfoRespVo.setFileNo(fileNo);
            fileInfoRespVo.setFileName(originalFilename);
            fileInfoRespVo.setFileSuffix(fileType);
            fileInfoRespVo.setCreateTime(new Date());

            FILE_SET.add(fileInfoRespVo);

            FileCacheVo fileCacheVo = new FileCacheVo();
            fileCacheVo.setFileNo(fileNo);
            fileCacheVo.setFileSuffix(fileType);
            fileCacheVo.setFileName(fileNo + fileType);
            fileCacheVo.setOriginalName(originalFilename);
            fileCacheVo.setPath(tempFile.getPath());

            // 添加预览文档到缓存
            fileViewComponent.addFileToCache(tempFile, fileNo);

            tempFilePathCache.put(fileNo, fileCacheVo);

            return fileInfoRespVo;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("上传文件失败");
        }
    }

    @Override
    public void viewFile(String fileNo, HttpServletResponse response) {
        response.addHeader("access-control-allow-methods", "GET");
        response.addHeader("access-control-allow-headers", "Content-Type");
        FileCacheVo fileCacheVo = tempFilePathCache.get(fileNo);
        if (fileCacheVo == null) {
            responseFailure("文件不存在！", response);
            return;
        }
        boolean existFile = fileViewComponent.cacheExistFile(fileNo);
        if (!existFile) {
            responseFailure("文件已经不存在！", response);
            return;
        }
        // 预览文档
        fileViewComponent.viewCacheFile(response, fileNo);
    }

    private void responseFailure(String msg, HttpServletResponse response) {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        try (PrintWriter pw = response.getWriter()) {
            String result = objectMapper.writeValueAsString(R.fail(msg));
            pw.write(result);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
```

**前端使用方式**

注意：viewFile是接入的后端服务自己接口，本组件不直接提供接口

```javascript
 axios.get("http://localhost:8081/officePreview/viewFile?fileNo=" + item.fileNo, {responseType: "blob"}).then(res => {
    let type = res.headers['file-type'];
    let blob;
    if (type === "html") {
        blob = new Blob([res.data], {type: 'text/html'});
    } else {
        blob = new Blob([res.data], {type: 'application/pdf'})
    }
    let pdfSrc = window.URL.createObjectURL(blob)
    window.open(pdfSrc)//新窗口打开，借用浏览器查看文档
}).catch(error => {
    alert("预览失败！");
})
```
