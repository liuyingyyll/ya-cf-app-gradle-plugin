package io.pivotal.services.plugin;

/**
 * Holds proxy settings
 *
 * @author Andreas Schilling
 */
public class CfProxySettings {
    private String proxyHost;
    private Integer proxyPort;
    private String proxyUser;
    private String proxyPassword;

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CfProxySettings{");
        sb.append("proxyHost='").append(proxyHost).append('\'');
        sb.append(", proxyPort=").append(proxyPort);
        sb.append(", proxyUser='").append(proxyUser).append('\'');
        sb.append(", proxyPassword='").append("***").append('\'');
        sb.append('}');
        return sb.toString();
    }
}
