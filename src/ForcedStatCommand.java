import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.ftpserver.command.impl.STAT;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;

public class ForcedStatCommand extends STAT 
{
    @Override
    public void execute(FtpIoSession address, FtpServerContext dataConnection, FtpRequest request) throws IOException {
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
