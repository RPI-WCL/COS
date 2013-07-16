package rpiwcl.cos.runtime;

import java.io.Serializable;

public class RuntimeInfo implements Serializable {
    String id;
    String profile;
    String title;
    String user;
    String ipAddr;
    String cmd;
    String sshOption;
    String parent;

    public RuntimeInfo( String id, /* runtimeId */
                        String profile, String title, /* terminal */
                        String user, String ipAddr, String cmd /* ssh */,
                        String sshOption,
                        String parent /* for reply */ ) {
        this.id = id;
        this.profile = profile;
        this.title = title;
        this.user = user;
        this.ipAddr = ipAddr;
        this.cmd = cmd;
        this.sshOption = sshOption;
        this.parent = parent;
    }
    
    public void setId( String id ) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setProfile( String profile ) {
        this.profile = profile;
    }
    public String getProfile() {
        return profile;
    }

    public void setTitle( String title ) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setUser( String user ) {
        this.user = user;
    }
    public String getUser() {
        return user;
    }

    public void setIpAddr( String ipAddr ) {
        this.ipAddr = ipAddr;
    }
    public String getIpAddr() {
        return ipAddr;
    }

    public void setCmd( String cmd ) {
        this.cmd = cmd;
    }
    public String getCmd() {
        return cmd;
    }

    public void setSshOption( String sshOption ) {
        this.sshOption = sshOption;
    }
    public String getSshOption() {
        return sshOption;
    }

    public void setParent( String parent ) {
        this.parent = parent;
    }
    public String getParent() {
        return parent;
    }
}
    
