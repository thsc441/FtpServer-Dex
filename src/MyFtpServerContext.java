import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpStatistics;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.ftpletcontainer.FtpletContainer;
import org.apache.ftpserver.ftpletcontainer.impl.DefaultFtpletContainer;
import org.apache.ftpserver.impl.DefaultFtpServerContext;
import org.apache.ftpserver.impl.DefaultFtpStatistics;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.message.MessageResource;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyFtpServerContext implements FtpServerContext 
{
	private final Logger LOG = LoggerFactory.getLogger(DefaultFtpServerContext.class);
	
	private MessageResource messageResource = new ZipMessageResourceFactory().createMessageResource();
	
	private UserManager userManager = new PropertiesUserManagerFactory().createUserManager();

    /** The file system factory */
    private FileSystemFactory fileSystemManager = new NativeFileSystemFactory();

    /** The FtpLet container */
    private FtpletContainer ftpletContainer = new DefaultFtpletContainer();

    /** The FTP statistics */
    private FtpStatistics statistics = new DefaultFtpStatistics();

    /** The comand factory */
    private CommandFactory commandFactory = new CommandFactoryFactory().createCommandFactory();

    /** The connection configuration */
    private ConnectionConfig connectionConfig = new ConnectionConfigFactory().createConnectionConfig();

    /** The declared listeners for this context */
    private Map<String, Listener> listeners = new HashMap<>();

    /** Thelist of admin authorities */
    private static final List<Authority> ADMIN_AUTHORITIES = new ArrayList<>();

    /** The list of anonymous authorities */
    private static final List<Authority> ANON_AUTHORITIES = new ArrayList<>();

    /**
     * The thread pool executor to be used by the server using this context
     */
    private ThreadPoolExecutor threadPoolExecutor = null;

    static {
        ADMIN_AUTHORITIES.add(new WritePermission());
        ANON_AUTHORITIES.add(new ConcurrentLoginPermission(20, 2));
        ANON_AUTHORITIES.add(new TransferRatePermission(4800, 4800));
    }

    /**
     * Create an instance
     */
    public MyFtpServerContext() {
        // create the default listener
        listeners.put("default", new ListenerFactory().createListener());
    }

    /**
     * Create default users:
     * <ul>
     *  <li>Admin</li>
     *  <li>Anonymous</li>
     * </ul>
     *
     * @throws Exception If the users creation failed
     */
    public void createDefaultUsers() throws Exception {
        UserManager userManager = getUserManager();

        // create admin user
        String adminName = userManager.getAdminName();

        if (!userManager.doesExist(adminName)) {
            LOG.info("Creating user : {}", adminName);
            BaseUser adminUser = new BaseUser();
            adminUser.setName(adminName);
            adminUser.setPassword(adminName);
            adminUser.setEnabled(true);
            adminUser.setAuthorities(ADMIN_AUTHORITIES);
            adminUser.setHomeDirectory("./res/home");
            adminUser.setMaxIdleTime(0);
            userManager.save(adminUser);
        }

        // create anonymous user
        if (!userManager.doesExist(UserManager.ANONYMOUS)) {
            LOG.info("Creating user : {}", UserManager.ANONYMOUS);
            BaseUser anonUser = new BaseUser();
            anonUser.setName(UserManager.ANONYMOUS);
            anonUser.setPassword("");

            anonUser.setAuthorities(ANON_AUTHORITIES);

            anonUser.setEnabled(true);

            anonUser.setHomeDirectory("./res/home");
            anonUser.setMaxIdleTime(300);
            userManager.save(anonUser);
        }
    }

    /**
     * Get user manager.
     * {@inheritDoc}
     */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * Get file system manager.
     * {@inheritDoc}
     */
    public FileSystemFactory getFileSystemManager() {
        return fileSystemManager;
    }

    /**
     * Get message resource.
     * {@inheritDoc}
     */
    public MessageResource getMessageResource() {
        return messageResource;
    }

    /**
     * Get ftp statistics.
     * {@inheritDoc}
     */
    public FtpStatistics getFtpStatistics() {
        return statistics;
    }

    /**
     * Set the FTP server statistics
     *
     * @param statistics The FTP server statistics
     */
    public void setFtpStatistics(FtpStatistics statistics) {
        this.statistics = statistics;
    }

    /**
     * {@inheritDoc}
     */
    public FtpletContainer getFtpletContainer() {
        return ftpletContainer;
    }

    /**
     * {@inheritDoc}
     */
    public CommandFactory getCommandFactory() {
        return commandFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ftplet getFtplet(String name) {
        return ftpletContainer.getFtplet(name);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        listeners.clear();
        ftpletContainer.getFtplets().clear();

        if (threadPoolExecutor != null) {
            LOG.debug("Shutting down the thread pool executor");
            threadPoolExecutor.shutdown();

            try {
                threadPoolExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            } finally {
                // TODO: how to handle?
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Listener getListener(String name) {
        return listeners.get(name);
    }

    /**
     * Set a listener. It does pretty much what addListener does.
     *
     * @param name The listener's name
     * @param listener The listener
     */
    public void setListener(String name, Listener listener) {
        listeners.put(name, listener);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Listener> getListeners() {
        return listeners;
    }

    /**
     * Set the listeners
     *
     * @param listeners The listeners
     */
    public void setListeners(Map<String, Listener> listeners) {
        this.listeners = listeners;
    }

    /**
     * Add a listener
     *
     * @param name The added listener's name
     * @param listener The added listener
     */
    public void addListener(String name, Listener listener) {
        listeners.put(name, listener);
    }

    /**
     * Remove a listener
     *
     * @param name The listener name
     * @return The removed listener
     */
    public Listener removeListener(String name) {
        return listeners.remove(name);
    }

    /**
     * Set the command factory
     *
     * @param commandFactory The command factory
     */
    public void setCommandFactory(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * Set the file system manager
     *
     * @param fileSystemManager The file system manager
     */
    public void setFileSystemManager(FileSystemFactory fileSystemManager) {
        this.fileSystemManager = fileSystemManager;
    }

    /**
     * Set the FtpLet container
     *
     * @param ftpletContainer The FtpLet container
     */
    public void setFtpletContainer(FtpletContainer ftpletContainer) {
        this.ftpletContainer = ftpletContainer;
    }

    /**
     * Set the message resource
     *
     * @param messageResource The message resource
     */
    public void setMessageResource(MessageResource messageResource) {
        this.messageResource = messageResource;
    }

    /**
     * Set the user manager
     *
     * @param userManager The user manager
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * {@inheritDoc}
     */
    public ConnectionConfig getConnectionConfig() {
        return connectionConfig;
    }

    /**
     * Set the connection configuration
     *
     * @param connectionConfig The connection configuration
     */
    public void setConnectionConfig(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    /**
     * Get the ThreadPool executor configured with the maxThreads parameter,
     * or the maxLogins parameter if maxThreads isn't defined, or 16 threads.
     *
     * @return An Ordered Thread Pool executor
     */
    public synchronized ThreadPoolExecutor getThreadPoolExecutor() {
        if (threadPoolExecutor == null) {
            int maxThreads = connectionConfig.getMaxThreads();

            if (maxThreads < 1) {
                int maxLogins = connectionConfig.getMaxLogins();

                if (maxLogins > 0) {
                    maxThreads = maxLogins;
                } else {
                    maxThreads = 16;
                }
            }

            LOG.debug("Intializing shared thread pool executor with max threads of {}", maxThreads);
            threadPoolExecutor = new OrderedThreadPoolExecutor(maxThreads);
        }

        return threadPoolExecutor;
    }
}
