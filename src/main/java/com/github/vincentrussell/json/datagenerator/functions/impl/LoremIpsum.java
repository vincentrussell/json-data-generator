package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * lorem ipsum words or paragraphs
 */
@Function(name = "lorem")
@SuppressWarnings("checkstyle:linelength")
public class LoremIpsum {

    private static String[] LOREM_IPSUM_WORDS = new String[]{"lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "curabitur", "vel", "hendrerit", "libero", "eleifend", "blandit", "nunc", "ornare", "odio", "ut", "orci", "gravida", "imperdiet", "nullam", "purus", "lacinia", "a", "pretium", "quis", "congue", "praesent", "sagittis", "laoreet", "auctor", "mauris", "non", "velit", "eros", "dictum", "proin", "accumsan", "sapien", "nec", "massa", "volutpat", "venenatis", "sed", "eu", "molestie", "lacus", "quisque", "porttitor", "ligula", "dui", "mollis", "tempus", "at", "magna", "vestibulum", "turpis", "ac", "diam", "tincidunt", "id", "condimentum", "enim", "sodales", "in", "hac", "habitasse", "platea", "dictumst", "aenean", "neque", "fusce", "augue", "leo", "eget", "semper", "mattis", "tortor", "scelerisque", "nulla", "interdum", "tellus", "malesuada", "rhoncus", "porta", "sem", "aliquet", "et", "nam", "suspendisse", "potenti", "vivamus", "luctus", "fringilla", "erat", "donec", "justo", "vehicula", "ultricies", "varius", "ante", "primis", "faucibus", "ultrices", "posuere", "cubilia", "curae", "etiam", "cursus", "aliquam", "quam", "dapibus", "nisl", "feugiat", "egestas", "class", "aptent", "taciti", "sociosqu", "ad", "litora", "torquent", "per", "conubia", "nostra", "inceptos", "himenaeos", "phasellus", "nibh", "pulvinar", "vitae", "urna", "iaculis", "lobortis", "nisi", "viverra", "arcu", "morbi", "pellentesque", "metus", "commodo", "ut", "facilisis", "felis", "tristique", "ullamcorper", "placerat", "aenean", "convallis", "sollicitudin", "integer", "rutrum", "duis", "est", "etiam", "bibendum", "donec", "pharetra", "vulputate", "maecenas", "mi", "fermentum", "consequat", "suscipit", "aliquam", "habitant", "senectus", "netus", "fames", "quisque", "euismod", "curabitur", "lectus", "elementum", "tempor", "risus", "cras"};
    private static final int WORDS_PER_SENTENCE_LOW_END = 5;
    private static final int WORDS_PER_SENTENCE_HIGH_END = 35;

    private static final int SENTENCES_PER_PARAGRAPH_LOW_END = 2;
    private static final int SENTENCES_PER_PARAGRAPH_HIGH_END = 7;


    /**
     * lorem ipsum words or paragraphs
     * @param amountOfLoremIpsum count of either words or paragraphs
     * @param type "words" or "paragraphs"
     * @return the result
     */
    @FunctionInvocation
    public String getLorem(final String amountOfLoremIpsum, final String type) {
        return getLorem(Integer.valueOf(amountOfLoremIpsum), type);
    }

    private String getLorem(final Integer amountOfLoremIpsum, final String type) {
        if ("words".equals(type)) {
            return getWords(amountOfLoremIpsum, false);
        } else if ("paragraphs".equals(type)) {
            return getParagraphs(amountOfLoremIpsum);
        } else {
            throw new IllegalArgumentException(type + " not a valid type for the lorem function");
        }
    }

    private String getWords(final int count, final boolean titleCase) {

        if (count == 1) {
            return FunctionUtils.getRandomElementFromArray(LOREM_IPSUM_WORDS);
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (titleCase) {
            stringBuilder.append("L");
        } else {
            stringBuilder.append("l");
        }

        stringBuilder.append("orem ipsum");
        for (int i = 0; i < count - 2; i++) {
            stringBuilder.append(" " + FunctionUtils.getRandomElementFromArray(LOREM_IPSUM_WORDS));
        }

        return stringBuilder.toString();
    }

    private String getParagraphs(final int count) {
        StringBuilder paragraphs = new StringBuilder();
        for (int i = 0; i < count; i++) {
            StringBuilder paragraph = new StringBuilder();
            int totalSentences = FunctionUtils.getRandomInteger(SENTENCES_PER_PARAGRAPH_LOW_END, SENTENCES_PER_PARAGRAPH_HIGH_END);
            for (int j = 0; j < totalSentences; j++) {
                int wordsPerSentence = FunctionUtils.getRandomInteger(WORDS_PER_SENTENCE_LOW_END, WORDS_PER_SENTENCE_HIGH_END);
                String sentence = getWords(wordsPerSentence, true) + ".";
                paragraph.append(sentence);
            }
            paragraphs.append("\t").append(paragraph);
            if (i != count - 1) {
                paragraphs.append("\n\n");
            }
        }
        return paragraphs.toString();
    }

}
