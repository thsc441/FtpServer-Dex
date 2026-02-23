import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.ftpserver.ipfilter.SessionFilter;

public class Main {
	public static void main(String[] args) throws FtpException, InterruptedException {
		System.out.println("Hello World!");
        MyFtpServerFactory serverFactory = new MyFtpServerFactory();
                
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(ServerConfig.port);
        serverFactory.addListener("default", listenerFactory.createListener());

        //serverFactory.setMessageResource(new ZipMessageResource(null, null));
        
        CommandFactoryFactory commandFactoryFactory = new CommandFactoryFactory();
        commandFactoryFactory.addCommand("LIST", new ForcedListCommand());
        commandFactoryFactory.addCommand("NLST", new ForcedNlstCommand());
        commandFactoryFactory.addCommand("MLSD", new ForcedMlsdCommand());
        commandFactoryFactory.addCommand("STAR", new ForcedStatCommand());
		commandFactoryFactory.addCommand("STOPSERVER", new STOP());
        serverFactory.setCommandFactory(commandFactoryFactory.createCommandFactory());
        
        serverFactory.getUserManager().save(ServerConfig.getUser());
                
        final FtpServer ftpServer = serverFactory.createServer();
        
        new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        ftpServer.start();
                    } catch (FtpException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
		Thread.sleep(Long.MAX_VALUE);
    }
}
