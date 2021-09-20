package hudson.plugins.redmine;

import hudson.model.Action;

/**
 * @author gaooh
 * @date 2008/10/26
 */
public class RedmineLinkAction implements Action {

    private final RedmineProjectProperty prop;

    public RedmineLinkAction(RedmineProjectProperty prop) {
        this.prop = prop;
    }

    @Override
    public String getIconFileName() {
        RedmineWebsiteConfig redmineConfig = prop.getRedmineWebsite();
        if (redmineConfig == null) {
            return null;
        } else {
            return "/plugin/redmine/redmine-logo.png"; // redmine logo instead ruby
        }
    }

    @Override
    public String getDisplayName() {
        return "Redmine - " + prop.projectName;
    }

    @Override
    public String getUrlName() {
        RedmineWebsiteConfig redmineConfig = prop.getRedmineWebsite();
        if (redmineConfig == null) {
            return null;
        } else {
            return redmineConfig.getBaseUrl() + "projects/" + prop.projectName;
        }
    }
}
