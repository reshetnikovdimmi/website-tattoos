=== ImageUtils ===
src/main/java/ru/tattoo/maxsim/util/ImageUtils.java:16:public class ImageUtils {
src/main/java/ru/tattoo/maxsim/service/impl/ImagesServiceImpl.java:18:import ru.tattoo.maxsim.util.ImageUtils;
src/main/java/ru/tattoo/maxsim/service/impl/ImagesServiceImpl.java:121:        List<List<Images>> objects = ImageUtils.partition(
src/main/java/ru/tattoo/maxsim/service/impl/UserServiceImpl.java:18:import ru.tattoo.maxsim.util.ImageUtils;
src/main/java/ru/tattoo/maxsim/service/impl/UserServiceImpl.java:78:        if (!StringUtils.isBlank(user.getAvatar()) && ImageUtils.existsImage(user.getAvatar())) {
src/main/java/ru/tattoo/maxsim/service/impl/UserServiceImpl.java:80:                ImageUtils.deleteImage(user.getAvatar());
src/main/java/ru/tattoo/maxsim/service/impl/UserServiceImpl.java:86:        String uniqueFileName = ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename());
src/main/java/ru/tattoo/maxsim/service/impl/UserServiceImpl.java:87:        ImageUtils.saveImage(fileImport, uniqueFileName);
src/main/java/ru/tattoo/maxsim/service/impl/ReviewServiceImpl.java:15:import ru.tattoo.maxsim.util.ImageUtils;
src/main/java/ru/tattoo/maxsim/service/impl/SettingWebsiteServiceImpl.java:15:import ru.tattoo.maxsim.util.ImageUtils;
src/main/java/ru/tattoo/maxsim/service/impl/SketchesServiceImpl.java:17:import ru.tattoo.maxsim.util.ImageUtils;
src/main/java/ru/tattoo/maxsim/service/impl/SketchesServiceImpl.java:79:        List<List<Sketches>> objects = ImageUtils.partition(
=== upload.directory ===
src/main/resources/application-test.properties:5:upload.directory=${java.io.tmpdir}/tattoo-test-${random.uuid}
src/main/resources/application-dev.properties:5:upload.directory=${java.io.tmpdir}/tattoo-uploads
src/main/resources/application-local.properties:5:upload.directory=./uploads-local
src/main/resources/application-prod.properties:5:upload.directory=/app/img/images
src/main/resources/application.properties:86:upload.directory=uploads/images
src/main/java/ru/tattoo/maxsim/config/AdditionalResourceWebConfiguration.java:16:    @Value("${upload.directory:/app/img/images}")
src/main/java/ru/tattoo/maxsim/config/StorageConfig.java:22:            @Value("${upload.directory:uploads/images}") String uploadPath) {
=== /app/img/images в Java ===
src/main/java/ru/tattoo/maxsim/config/AdditionalResourceWebConfiguration.java:16:    @Value("${upload.directory:/app/img/images}")
src/main/java/ru/tattoo/maxsim/util/ImageUtils.java:17:    private static final Path UPLOAD_DIRECTORY = Paths.get("/app/img/images");  // Путь внутри контейнера
=== partition ===
src/main/java/ru/tattoo/maxsim/util/ImageUtils.java:78:        return ListUtils.partition(list, size);
src/main/java/ru/tattoo/maxsim/service/impl/ImagesServiceImpl.java:121:        List<List<Images>> objects = ImageUtils.partition(
src/main/java/ru/tattoo/maxsim/service/impl/SketchesServiceImpl.java:79:        List<List<Sketches>> objects = ImageUtils.partition(
=== generateUniqueFileName ===
src/test/java/ru/tattoo/maxsim/service/impl/AbstractCRUDServiceTest.java:464:            when(imageStorage.generateUniqueFileName("test-image.jpg"))
src/test/java/ru/tattoo/maxsim/service/impl/AbstractCRUDServiceTest.java:486:            verify(imageStorage, times(1)).generateUniqueFileName("test-image.jpg");
src/test/java/ru/tattoo/maxsim/service/impl/AbstractCRUDServiceTest.java:544:            when(imageStorage.generateUniqueFileName(TEST_FILE_NAME)).thenReturn(UNIQUE_FILE_NAME);
src/test/java/ru/tattoo/maxsim/service/impl/AbstractCRUDServiceTest.java:596:            doReturn(UNIQUE_FILE_NAME).when(imageStorage).generateUniqueFileName(TEST_FILE_NAME);
src/test/java/ru/tattoo/maxsim/service/impl/AbstractCRUDServiceTest.java:678:            // Проверяем, что generateUniqueFileName НЕ вызывался (если он не используется)
src/test/java/ru/tattoo/maxsim/service/impl/AbstractCRUDServiceTest.java:679:            verify(imageStorage, never()).generateUniqueFileName(anyString());
src/test/java/ru/tattoo/maxsim/service/impl/AbstractCRUDServiceTest.java:731:            doReturn(generatedName).when(imageStorage).generateUniqueFileName(emptyName);
src/test/java/ru/tattoo/maxsim/service/impl/AbstractCRUDServiceTest.java:773:            doReturn(uniqueName).when(imageStorage).generateUniqueFileName(fileNameWithSpaces);
src/test/java/ru/tattoo/maxsim/service/impl/AbstractCRUDServiceTest.java:839:            doReturn("unique.jpg").when(imageStorage).generateUniqueFileName("test.jpg");
src/test/java/ru/tattoo/maxsim/storage/impl/InMemoryImageStorageTest.java:166:            String name = imageStorage.generateUniqueFileName("test.jpg");
src/test/java/ru/tattoo/maxsim/storage/impl/InMemoryImageStorageTest.java:191:                    () -> imageStorage.generateUniqueFileName(null));
src/main/java/ru/tattoo/maxsim/util/ImageUtils.java:59:     * @deprecated Используйте {@link ru.tattoo.maxsim.storage.ImageStorage#generateUniqueFileName}
src/main/java/ru/tattoo/maxsim/util/ImageUtils.java:62:    public static String generateUniqueFileName(String originalFileName) {
src/main/java/ru/tattoo/maxsim/service/impl/UserServiceImpl.java:86:        String uniqueFileName = ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename());
src/main/java/ru/tattoo/maxsim/service/impl/AbstractCRUDService.java:146:            // String uniqueFileName = getImageStorage().generateUniqueFileName(
src/main/java/ru/tattoo/maxsim/storage/ImageStorage.java:45:    String generateUniqueFileName(String originalFileName);
src/main/java/ru/tattoo/maxsim/storage/Impl/InMemoryImageStorage.java:26:        String uniqueName = generateUniqueFileName(fileName);
src/main/java/ru/tattoo/maxsim/storage/Impl/InMemoryImageStorage.java:51:    public String generateUniqueFileName(String originalFileName) {
src/main/java/ru/tattoo/maxsim/storage/Impl/FileSystemImageStorage.java:64:        String uniqueFileName = generateUniqueFileName(fileName);
src/main/java/ru/tattoo/maxsim/storage/Impl/FileSystemImageStorage.java:135:    public String generateUniqueFileName(String originalFileName) {
=== getImage ===
src/test/java/ru/tattoo/maxsim/storage/impl/InMemoryImageStorageTest.java:99:            byte[] retrieved = imageStorage.getImage(savedName);
src/test/java/ru/tattoo/maxsim/storage/impl/InMemoryImageStorageTest.java:113:                    () -> imageStorage.getImage("non-existent.jpg"));
=== UPLOAD_DIRECTORY ===
./project-code.txt:432:    private static final Path UPLOAD_DIRECTORY = Paths.get("/app/img/images");  // Путь внутри контейнера
./project-code.txt:442:        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./project-code.txt:454:        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./project-code.txt:466:        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./project-code.txt:3587:    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/img/images/";
./docker-compose.yml:21:      - UPLOAD_DIRECTORY=/app/img/images
./src/main/java/ru/tattoo/maxsim/util/ImageUtils.java:17:    private static final Path UPLOAD_DIRECTORY = Paths.get("/app/img/images");  // Путь внутри контейнера
./src/main/java/ru/tattoo/maxsim/util/ImageUtils.java:27:        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./src/main/java/ru/tattoo/maxsim/util/ImageUtils.java:39:        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./src/main/java/ru/tattoo/maxsim/util/ImageUtils.java:51:        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./all-code.txt:5665:    private static final Path UPLOAD_DIRECTORY = Paths.get("/app/img/images");  // Путь внутри контейнера
./all-code.txt:5675:        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./all-code.txt:5687:        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./all-code.txt:5699:        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./project_all_code_numbered.txt:8777:    12	    private static final Path UPLOAD_DIRECTORY = Paths.get("/app/img/images");  // Путь внутри контейнера
./project_all_code_numbered.txt:8787:    21	        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./project_all_code_numbered.txt:8799:    32	        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./project_all_code_numbered.txt:8811:    43	        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
./project_all_code_numbered.txt:12032:    29	    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/img/images/";
=== max-file-size / allowed-types ===
src/main/resources/application.properties:82:upload.max-file-size=10485760
src/main/resources/application.properties:83:upload.allowed-types=image/jpeg,image/png,image/gif,image/webp
