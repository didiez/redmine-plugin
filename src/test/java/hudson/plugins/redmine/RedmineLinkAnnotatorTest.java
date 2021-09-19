package hudson.plugins.redmine;

import hudson.MarkupText;
import junit.framework.TestCase;

public class RedmineLinkAnnotatorTest extends TestCase {

    private static final String REDMINE_URL = "http://local.redmine/";

    public void testWikiLinkSyntax() {
        assertAnnotatedTextEquals("Nothing here.", "Nothing here.");
        assertAnnotatedTextEquals("Text with WikiLink.", "Text with <a href='" + REDMINE_URL + "wiki/WikiLink'>WikiLink</a>.");
        assertAnnotatedTextEquals("#42", "<a href='" + REDMINE_URL + "issues/42'>#42</a>");
        assertAnnotatedTextEquals("IssueID 22", "<a href='" + REDMINE_URL + "issues/22'>IssueID 22</a>");
        assertAnnotatedTextEquals("fixes 10,11,12", 
        						  "<a href='" + REDMINE_URL + "issues/10'>fixes 10</a>," +
        						  "<a href='" + REDMINE_URL + "issues/11'>11</a>," +
        						  "<a href='" + REDMINE_URL + "issues/12'>12</a>");
        assertAnnotatedTextEquals("references 110,111,112,113", 
        						  "<a href='" + REDMINE_URL + "issues/110'>references 110</a>," +
        						  "<a href='" + REDMINE_URL + "issues/111'>111</a>," +
        						  "<a href='" + REDMINE_URL + "issues/112'>112</a>," +
        						  "<a href='" + REDMINE_URL + "issues/113'>113</a>");
        assertAnnotatedTextEquals("closes 210, 211", 
        						  "<a href='" + REDMINE_URL + "issues/210'>closes 210</a>, " +
        						  "<a href='" + REDMINE_URL + "issues/211'>211</a>");
        assertAnnotatedTextEquals("closes 210 211", 
        						  "<a href='" + REDMINE_URL + "issues/210'>closes 210</a> " +
				                  "<a href='" + REDMINE_URL + "issues/211'>211</a>");
        assertAnnotatedTextEquals("refs 310, 11, 4, 4120", 
        						  "<a href='" + REDMINE_URL + "issues/310'>refs 310</a>, " +
        						  "<a href='" + REDMINE_URL + "issues/11'>11</a>, " +
        						  "<a href='" + REDMINE_URL + "issues/4'>4</a>, " +
        						  "<a href='" + REDMINE_URL + "issues/4120'>4120</a>");
        assertAnnotatedTextEquals("refs 1&11&111&1111", 
        						  "<a href='" + REDMINE_URL + "issues/1'>refs 1</a>&amp;" +
        						  "<a href='" + REDMINE_URL + "issues/11'>11</a>&amp;" +
        						  "<a href='" + REDMINE_URL + "issues/111'>111</a>&amp;" +
        						  "<a href='" + REDMINE_URL + "issues/1111'>1111</a>");
        assertAnnotatedTextEquals("IssueID 21&11&100", 
        						  "<a href='" + REDMINE_URL + "issues/21'>IssueID 21</a>&amp;" +
        						  "<a href='" + REDMINE_URL + "issues/11'>11</a>&amp;" +
        						  "<a href='" + REDMINE_URL + "issues/100'>100</a>");
        assertAnnotatedTextEquals("refs #1,#11,#111,#1111", 
			                      "<a href='" + REDMINE_URL + "issues/1'>refs #1</a>," +
				  				  "<a href='" + REDMINE_URL + "issues/11'>#11</a>," +
				                  "<a href='" + REDMINE_URL + "issues/111'>#111</a>," +
				                  "<a href='" + REDMINE_URL + "issues/1111'>#1111</a>");
        assertAnnotatedTextEquals("refs #1, #11, #111,#1111", 
                                  "<a href='" + REDMINE_URL + "issues/1'>refs #1</a>, " +
				                  "<a href='" + REDMINE_URL + "issues/11'>#11</a>, " +
                                  "<a href='" + REDMINE_URL + "issues/111'>#111</a>," +
                                  "<a href='" + REDMINE_URL + "issues/1111'>#1111</a>");
        assertAnnotatedTextEquals("refs #1", 
        						  "<a href='" + REDMINE_URL + "issues/1'>refs #1</a>");
        assertAnnotatedTextEquals("closes #1&#11", 
                                  "<a href='" + REDMINE_URL + "issues/1'>closes #1</a>&amp;" +
                                  "<a href='" + REDMINE_URL + "issues/11'>#11</a>");
        assertAnnotatedTextEquals("closes #1", 
                			      "<a href='" + REDMINE_URL + "issues/1'>closes #1</a>");
        assertAnnotatedTextEquals("IssueID #1 #11", 
                                  "<a href='" + REDMINE_URL + "issues/1'>IssueID #1</a> " +
                                  "<a href='" + REDMINE_URL + "issues/11'>#11</a>");
        
        assertAnnotatedTextEquals("Fixes #123", "<a href='" + REDMINE_URL + "issues/123'>Fixes #123</a>");  // ignore case
        
        assertAnnotatedTextEquals("fixes #123 This is a commit message.", "<a href='" + REDMINE_URL + "issues/123'>fixes #123</a> This is a commit message.");
        assertAnnotatedTextEquals("This is a commit message. fixes #123", "This is a commit message. <a href='" + REDMINE_URL + "issues/123'>fixes #123</a>");
        assertAnnotatedTextEquals("This is a commit message with fixes #123 in between.", "This is a commit message with <a href='" + REDMINE_URL + "issues/123'>fixes #123</a> in between.");
    }

    private void assertAnnotatedTextEquals(String originalText, String expectedAnnotatedText) {
        MarkupText markupText = new MarkupText(originalText);
        for (RedmineLinkAnnotator.LinkMarkup markup : RedmineLinkAnnotator.buildLinkMarkups(null, null)) {
            markup.process(markupText, REDMINE_URL, null);
        }

        System.out.println(markupText.toString(true));
        assertEquals(expectedAnnotatedText, markupText.toString(true));
    }
}
