package hudson.plugins.redmine;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/**
 * @since 0.14
 * @author ljader
 *
 */
public class RedmineWebsiteConfig extends AbstractDescribableImpl<RedmineWebsiteConfig> {

    private String name;
    private String baseUrl;
    private String versionNumber;
    private String referencingKeywords;

    RedmineLinkAnnotator.LinkMarkup[] linkMarkups;

    /**
     * Constructor; params shouldnt be null
     *
     * @param name
     * @param baseUrl
     * @param versionNumber
     * @param referencingKeywords
     */
    @DataBoundConstructor
    public RedmineWebsiteConfig(String name, String baseUrl, String versionNumber, String referencingKeywords) {
        this.name = name;
        this.baseUrl = baseUrl;
        if (!this.baseUrl.endsWith("/")) {
            this.baseUrl += '/';
        }
        this.versionNumber = versionNumber;
        this.referencingKeywords = referencingKeywords;

        this.linkMarkups = RedmineLinkAnnotator.buildLinkMarkups(versionNumber, referencingKeywords);
    }

    public String getName() {
        return name;
    }

    @DataBoundSetter
    public void setName(String name) {
        this.name = name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    @DataBoundSetter
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    @DataBoundSetter
    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
        this.linkMarkups = RedmineLinkAnnotator.buildLinkMarkups(versionNumber, referencingKeywords);
    }

    public String getReferencingKeywords() {
        return referencingKeywords;
    }

    @DataBoundSetter
    public void setReferencingKeywords(String referencingKeywords) {
        this.referencingKeywords = referencingKeywords;
        this.linkMarkups = RedmineLinkAnnotator.buildLinkMarkups(versionNumber, referencingKeywords);
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<RedmineWebsiteConfig> {

        @Override
        public String getDisplayName() {
            return "";
        }

        public FormValidation doCheckName(@QueryParameter String name) {
            if (name == null || name.trim().length() < 1) {
                return FormValidation.error("Name can't be empty!");
            }

            return FormValidation.ok();
        }

        public FormValidation doCheckBaseUrl(@QueryParameter String baseUrl) {
            if (baseUrl == null || baseUrl.trim().length() < 1) {
                return FormValidation.error("Url can't be empty!");
            }

            return FormValidation.ok();
        }

        public FormValidation doCheckVersionNumber(@QueryParameter String versionNumber) {
            if (versionNumber == null || versionNumber.trim().length() < 1) {
                return FormValidation.ok();
            }

            if (versionNumber.split("\\.").length != 3) {
                return FormValidation.error("Version number must be X.Y.Z form!");
            }

            return FormValidation.ok();
        }
    }
}
