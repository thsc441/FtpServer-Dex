import java.io.IOException;
import org.apache.ftpserver.command.impl.LIST;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.DefaultFtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import java.lang.reflect.Field;
import org.apache.ftpserver.command.impl.NLST;

public class ForcedNlstCommand extends NLST {

    @Override
    public void execute(FtpIoSession address, FtpServerContext dataConnection, FtpRequest request) throws IOException, FtpException {
        String argument = request.getArgument();
        if (argument == null || !argument.trim().startsWith("-a")) {
            argument = "-a " + ((argument != null) ? argument : "");
        }

        Class<?> clazz = request.getClass();
        try {
            Field field = clazz.getDeclaredField("argument");
            field.setAccessible(true);
            field.set(request, argument);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            
        }
        super.execute(address, dataConnection, request);
    }
}

