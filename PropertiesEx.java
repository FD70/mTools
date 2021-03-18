package tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesEx extends Properties {

    private static final String TAG = "PropertiesEx";
    private static final String D_EXCEPTION_MESSAGE = "<%s: Exception in inner method>".formatted(TAG);

    private static final Logger logger = LoggerFactory.getLogger(TAG);

    private Path path;
    private String filePrefix;

    private   void      setPath(Path path)              { this.path = path;         }
    public void      setFilePrefix(String prefix)    { this.filePrefix = prefix; }
    public String    getFilePrefix()                 { return this.filePrefix;   }

    public Path getPath() {
        return this.path;
    }

    /**
     * Экранированное получение значение переменной из файла *.properties
     * кидает NPE исключение, если нет значения по запрашиваемому ключу */
    public String getValue (String key) {
        if (this.getProperty(key)!= null) {
            return this.getProperty(key);
        } else {
            String exMessage = "Key: %s is null\nprpPath: %s".formatted(key, this.path.toString());
            throw new NullPointerException(exMessage);
        }
    }

    /**
     * Переписывает файл пропертей измененными даннымы (если они изменялись) <br>
     * например, если они изменялись методом: <br>
     * super.setProperty()
     * @see Properties
     * */
    public void rewritePropertiesFile() throws IOException {
        this.store(new FileOutputStream(String.valueOf(this.path)), null);
    }

    /**
     *  Пытается найти файл .property
     * @param propertiesName приставка файла пропертей
     * @return возвращает АБСОЛЮТНЫЙ путь до файла пропертей
     * */
    private static Path getPropertiesPath(String propertiesName) {
        // в список можно добавить пути поиска файла пропертей
        ArrayList<String> propertiesFindPaths = new ArrayList<>(List.of(
                //"%s/%s.properties".formatted(PROJECT_TAG, propertiesName),
                "src/main/resources/%s.properties".formatted(propertiesName),
                "%s.properties".formatted(propertiesName)
        ));

        // Строка выведется, если не будет найден файл по указанным путям
        StringBuilder errorFilePathsAnnouncer = new StringBuilder();
        errorFilePathsAnnouncer.append("File %s.properties not found by that paths:\n".formatted(propertiesName));

        for (String _way: propertiesFindPaths) {
            Path _path = Paths.get("").toAbsolutePath().resolve(_way);
            if (_path.toFile().exists()) {
                return _path;
            } else {
                errorFilePathsAnnouncer.append("%s\n".formatted(_path.toString()));
            }
        }
        System.out.println(errorFilePathsAnnouncer.toString());
        return null;
    }

    /**
     * Ищет и возвращает объект класса PropertiesEx
     * Если ввести существующее имя пропертей
     * @param propertiesName приставка файла .properties
     * */
    public static PropertiesEx getByName(@Nonnull String propertiesName) throws NullPointerException {
        String _tag = "getByName";
        Path _path = getPropertiesPath(propertiesName);
        if (_path != null) {
            PropertiesEx propEx = new PropertiesEx();
            try {
                InputStream in = new FileInputStream(_path.toFile());
                propEx.load(in);
            } catch (IOException e) {
                logger.error("%s > %s()".formatted(D_EXCEPTION_MESSAGE, _tag));
                for (StackTraceElement ste: e.getStackTrace()) logger.error(ste.toString());
            }
            propEx.setPath(_path);
            propEx.setFilePrefix(propertiesName);
            return propEx;
        } else {
            String exMessage = "%s \n> %s() > _path for name '%s' null".formatted(D_EXCEPTION_MESSAGE, _tag, propertiesName);
            throw new NullPointerException(exMessage);
        }
    }
}
