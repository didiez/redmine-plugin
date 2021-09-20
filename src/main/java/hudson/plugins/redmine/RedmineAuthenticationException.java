package hudson.plugins.redmine;

import org.acegisecurity.AuthenticationException;

/**
 *
 * @author Yasuyuki Saito
 *
 */
public class RedmineAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public RedmineAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public RedmineAuthenticationException(String msg) {
        super(msg);
    }
}
