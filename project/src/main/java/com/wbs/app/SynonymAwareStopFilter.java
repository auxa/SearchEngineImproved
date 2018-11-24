package com.wbs.app;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class SynonymAwareStopFilter extends StopFilter {
    /**
     * Constructs a filter which removes words from the input TokenStream that are
     * named in the Set.
     *
     * @param in        Input stream
     * @param stopWords A {@link CharArraySet} representing the stopwords.
     * @see #makeStopSet(String...)
     */

    private TypeAttribute typeAttribute = addAttribute(TypeAttribute.class);
    private PositionLengthAttribute positionLengthAttribute = addAttribute(PositionLengthAttribute.class);
    private int synonymSpans;

    public SynonymAwareStopFilter(TokenStream in, CharArraySet stopWords) {
        super(in, stopWords);
    }

    private boolean isSynonymToken() {
        return "SYNONYM".equals(typeAttribute.type());
    }

    protected boolean accept()
    {
        if (isSynonymToken()) {
            synonymSpans = positionLengthAttribute.getPositionLength() > 1
                    ? positionLengthAttribute.getPositionLength()
                    : 0;
            return true;
        }

        return (--synonymSpans > 0) || super.accept();
    }
}
