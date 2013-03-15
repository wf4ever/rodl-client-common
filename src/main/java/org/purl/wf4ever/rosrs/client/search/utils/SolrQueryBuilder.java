package org.purl.wf4ever.rosrs.client.search.utils;

/**
 * Utils for solr/Lucene query.
 * 
 * @author pejot
 * 
 */
public final class SolrQueryBuilder {

    /**
     * Hidden constructor.
     */
    private SolrQueryBuilder() {

    }


    /**
     * Escape string from solr special characters.
     * 
     * @param queryString
     *            string
     * @return escaped string
     */
    public static String escapeString(String queryString) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < queryString.length(); i++) {
            char c = queryString.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^'
                    || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?'
                    || c == '|' || c == '&' || c == ';') {
                sb.append('\\');
            }
            if (Character.isWhitespace(c)) {
                sb.append(" \\ ");
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
