package hudson.plugins.redmine;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.CopyOnWriteList;
import hudson.util.FormValidation;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.collect.Sets;
import java.util.Collections;

/**
 * Property for {@link AbstractProject} that stores the associated Redmine website URL.
 *
 * @author gaooh
 * @date 2008/10/13
 */
public class RedmineProjectProperty extends JobProperty<Job<?, ?>> {

    private final String redmineWebsiteName;
    public final String projectName;  // actually a project identifier - project names in Redmine can have spaces

    @DataBoundConstructor
    public RedmineProjectProperty(String redmineWebsiteName, String projectName) {
        this.redmineWebsiteName = redmineWebsiteName;
        this.projectName = projectName;
    }

    @Override
    public void setOwner(Job<?, ?> owner) {
        // we have to add the action due to some kinds of jobs not using getJobActions(job) (p.e: WorkflowJob)
        if (owner != null && !(owner instanceof AbstractProject)) {
            owner.addAction(new RedmineLinkAction(this));
        }
        this.owner = owner;
    }

    @Override
    public Collection<? extends Action> getJobActions(Job<?, ?> job) {
        return Collections.singletonList(new RedmineLinkAction(this));
    }

    public RedmineWebsiteConfig getRedmineWebsite() {
        if (redmineWebsiteName == null) {
            return null;
        }

        RedmineWebsiteConfig foundRedmine = null;
        for (RedmineWebsiteConfig redmineConfig : DESCRIPTOR.getRedmineWebsites()) {
            if (redmineConfig.getName().equals(redmineWebsiteName)) {
                foundRedmine = redmineConfig;
                break;
            }
        }
        return foundRedmine;
    }

    @Override
    public JobPropertyDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends JobPropertyDescriptor {

        private final CopyOnWriteList<RedmineWebsiteConfig> redmineWebsites = new CopyOnWriteList<RedmineWebsiteConfig>();

        public DescriptorImpl() {
            super(RedmineProjectProperty.class);
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Associated Redmine website";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            List<RedmineWebsiteConfig> redmineSites = req.bindJSONToList(RedmineWebsiteConfig.class,
                    formData.get("redmineWebsites"));
            CollectionUtils.filter(redmineSites, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return StringUtils.isNotBlank(((RedmineWebsiteConfig) object).getName())
                            && StringUtils.isNotBlank(((RedmineWebsiteConfig) object).getBaseUrl());
                }
            });
            CollectionUtils.filter(redmineSites, new Predicate() {
                Set<String> redmineNames = Sets.newHashSet();

                @Override
                public boolean evaluate(Object object) {
                    String examinedName = ((RedmineWebsiteConfig) object).getName();
                    if (redmineNames.contains(examinedName)) {
                        return false;
                    }
                    redmineNames.add(examinedName);
                    return true;
                }
            });

            this.redmineWebsites.replaceBy(redmineSites);

            save();
            return super.configure(req, formData);
        }

        @Override
        public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            if (formData.containsKey("redmine")) {
                JSONObject redmineJson = formData.getJSONObject("redmine");
                if (!StringUtils.isBlank(redmineJson.optString("redmineWebsiteName"))
                        && !StringUtils.isBlank(redmineJson.optString("projectName"))) {
                    return req.bindJSON(RedmineProjectProperty.class, redmineJson);
                }
            }

            return null;
        }

        public List<RedmineWebsiteConfig> getRedmineWebsites() {
            return this.redmineWebsites.getView();
        }

        public FormValidation doCheckProjectName(@QueryParameter String projectName) {
            if (projectName == null || projectName.trim().length() < 1) {
                return FormValidation.error("Project name can't be empty!");
            }

            //We dont validate existence yet
            return FormValidation.ok();
        }

    }
}
