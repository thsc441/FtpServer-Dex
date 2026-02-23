
import java.util.ArrayList;
import java.util.List;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;public class ServerConfig  
{
    public static int port = 17021;
    
    public static String userName = "anonymous";
    public static String userHomeDirectory = "/";
    public static boolean canWrite = true;
    
    public static BaseUser getUser() {
        BaseUser user = new BaseUser();
        user.setName(ServerConfig.userName);
        user.setHomeDirectory(ServerConfig.userHomeDirectory);

        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);
        
        return user;
    }
}
