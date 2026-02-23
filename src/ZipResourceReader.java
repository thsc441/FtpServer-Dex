import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourceReader {
    private static ZipFile zipFile;
    private static boolean isInitiativedSuccess = false;

    static {
        Path zipPath = Paths.get("resource.zip").toAbsolutePath();

        // 检查 ZIP 文件是否存在
        if (!Files.exists(zipPath)) {
            System.err.println("ZIP 文件不存在: " + zipPath);
        }
        try {
            zipFile = new ZipFile(zipPath.toFile());
            if (zipFile != null) {
                isInitiativedSuccess = true;
            }
        } catch (IOException e) {
            System.err.println("读取 ZIP 文件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 从当前目录下的 resource.zip 中读取指定路径的文件，并返回其字节数组
     *
     * @param filePath 在 ZIP 包内的文件路径，例如 "config/settings.json"
     * @return 文件的字节数组，如果未找到则返回 null
     */
    public static byte[] readFileBytes(String filePath) {
        try {
            InputStream inputStream = readFileInputStream(filePath);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] tempBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(tempBuffer)) != -1) {
                buffer.write(tempBuffer, 0, bytesRead);
            }

            return buffer.toByteArray();
        } catch (IOException e) {
            System.err.println("读取 ZIP 文件时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream readFileInputStream(String filePath) {
        try {
            // 获取 ZIP 中的指定条目
            ZipEntry entry = zipFile.getEntry(filePath);

            if (entry == null) {
                System.err.println("ZIP 中未找到文件: " + filePath);
                return null;
            }

            // 读取文件内容为字节数组
            return zipFile.getInputStream(entry);
        } catch (IOException e) {
            System.err.println("读取 ZIP 文件时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isInitiativedSuccess() {
        return isInitiativedSuccess;
    }
}


