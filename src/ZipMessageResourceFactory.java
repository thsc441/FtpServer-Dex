import java.io.File;
import java.util.List;
import org.apache.ftpserver.message.MessageResource;
import org.apache.ftpserver.message.impl.DefaultMessageResource;

public class ZipMessageResourceFactory {
    /** The supported languages */
    private List<String> languages;

    /** The directory where the message are stored */
    private File customMessageDirectory;

    /**
     * Create a MessageResourceFactory instance
     */
    public ZipMessageResourceFactory() {
        // Nothing to do
    }

    /**
     * Create an {@link MessageResource} based on the configuration on this factory
     *
     * @return The {@link MessageResource} instance
     */
    public MessageResource createMessageResource() {
        return new ZipMessageResource(languages, customMessageDirectory);
    }

    /**
     * The languages for which messages are available
     *
     * @return The list of supported languages
     */
    public List<String> getLanguages() {
        return languages;
    }

    /**
     * Set the languages for which messages are supported
     *
     * @param languages The list of supported languages
     */
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    /**
     * The directory where custom message bundles can be located
     *
     * @return The {@link File} denoting the directory with message bundles
     */
    public File getCustomMessageDirectory() {
        return customMessageDirectory;
    }

    /**
     * Set the directory where custom message bundles can be located
     *
     * @param customMessageDirectory The {@link File} denoting the directory with message bundles
     */
    public void setCustomMessageDirectory(File customMessageDirectory) {
        this.customMessageDirectory = customMessageDirectory;
    }
}

