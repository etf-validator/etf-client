/**
 * Copyright 2019-2020 interactive instruments GmbH
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package de.interactive_instruments.etf.client.internal;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import de.interactive_instruments.etf.client.EtfCollection;
import de.interactive_instruments.etf.client.ItemMetadata;
import de.interactive_instruments.etf.client.ReferenceError;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class TranslationTemplateBundle implements ItemMetadata {

    // Translation interface
    private interface Translation {

        Pattern replacementPattern = Pattern.compile("(\\{[^}]*})");

        static Translation createFrom(final String text) {
            final Matcher m = replacementPattern.matcher(text);
            if (m.find()) {
                int previousEnd = 0;
                final List<String> texts = new ArrayList<>();
                final List<String> parameters = new ArrayList<>();

                final int firstPatternStart = m.start();
                texts.add(text.substring(previousEnd, firstPatternStart));
                previousEnd = m.end();
                parameters.add(text.substring(firstPatternStart, previousEnd));

                while (m.find()) {
                    final int patternStart = m.start();
                    texts.add(text.substring(previousEnd, patternStart));
                    previousEnd = m.end();
                    parameters.add(text.substring(patternStart, previousEnd));
                }
                texts.add(text.substring(previousEnd));

                return new TranslationWithParameters(texts, parameters);
            } else {
                return new SimpleTranslation(text);
            }
        }

        String translate(final Map<String, String> translationParameters);
    }

    // Translation with parameters
    private final static class TranslationWithParameters implements Translation {
        private final String[] textAndParameters;
        private final int initialLength;

        private TranslationWithParameters(final List<String> texts, final List<String> parameters) {
            this.textAndParameters = new String[texts.size() + parameters.size()];
            int length = 0;
            int index = 0;
            for (int i = 0; i < texts.size(); i++) {
                this.textAndParameters[index++] = texts.get(i);
                length += texts.get(i).length() + 8;
                if (i < parameters.size()) {
                    final String param = parameters.get(i);
                    this.textAndParameters[index++] = param.substring(1, param.length() - 1);
                }
            }
            initialLength = length;
        }

        @Override
        public String translate(final Map<String, String> translationParameters) {
            final StringBuilder sb = new StringBuilder(initialLength);
            final int length = textAndParameters.length;
            for (int i = 0; i < length; i++) {
                // add text
                sb.append(textAndParameters[i]);
                // add translation replacement if available
                if (++i < length) {
                    final String translationName = textAndParameters[i];
                    final String translation = translationParameters.get(translationName);
                    sb.append(translation != null ? translation : "");
                }
            }
            return sb.toString();
        }
    }

    // Translation without parameters
    private final static class SimpleTranslation implements Translation {
        private final String text;

        private SimpleTranslation(final String text) {
            this.text = text;
        }

        @Override
        public String translate(final Map<String, String> translationParameters) {
            return this.text;
        }
    }

    private final String eid;
    private final String parentRef;
    private final EtfCollection<TranslationTemplateBundle> callback;

    // names to language to translations mappings
    private final Map<String, Map<String, Translation>> translations = new LinkedHashMap<>();

    TranslationTemplateBundle(final JSONObject jsonObject, final EtfCollection<TranslationTemplateBundle> callback) {
        this.eid = jsonObject.getString("id");
        this.parentRef = jsonObject.has("parent") ? jsonObject.getJSONObject("parent").getString("ref") : null;
        final Collection<JSONObject> langTranslationTemplateCollection = new JSONObjectOrArray(
                jsonObject.getJSONObject("translationTemplateCollections")).get("LangTranslationTemplateCollection");
        for (final JSONObject translationTemplateJson : langTranslationTemplateCollection) {
            final Collection<JSONObject> ttJ = new JSONObjectOrArray(translationTemplateJson.getJSONObject(
                    "translationTemplates")).get("TranslationTemplate");
            for (final JSONObject t : ttJ) {
                add(t);
            }
        }
        this.callback = callback;
    }

    @Override
    public String eid() {
        return this.eid;
    }

    @Override
    public String label() {
        return this.eid;
    }

    @Override
    public String description() {
        return this.eid;
    }

    private String parentRef() {
        return parentRef;
    }

    private void add(final JSONObject translationTemplate) {
        final String language = translationTemplate.getString("language");
        final String name = translationTemplate.getString("name");
        if (translationTemplate.has("$")) {
            final Translation translation = Translation.createFrom(translationTemplate.getString("$"));
            final Map<String, Translation> languageToTranslationsMap = translations.computeIfAbsent(
                    name, k -> new LinkedHashMap<>());
            languageToTranslationsMap.put(language, translation);
        }
    }

    String translate(final String language, final String name, final Map<String, String> parameters) {
        final Map<String, Translation> languageToTranslationsMap = this.translations.get(name);
        if (languageToTranslationsMap == null) {
            TranslationTemplateBundle nextTranslationTemplate = this;
            while (nextTranslationTemplate.parentRef() != null) {
                final String parentRef = nextTranslationTemplate.parentRef();

                final Optional<TranslationTemplateBundle> n = callback.itemById(parentRef);
                if (n.isEmpty()) {
                    throw new ReferenceError("Referenced parent Translation Template Bundle '" +
                            parentRef +
                            "' not found. Please contact the administrator of the service to ensure "
                            + "that the Translation Template Bundle is installed correctly. "
                            + "If the problem persists, the Executable Test Suite developer should be "
                            + "notified of the missing translation.");
                }
                nextTranslationTemplate = n.get();
                final String translatedTextFromParent = nextTranslationTemplate.translate(language, name, parameters);
                if (translatedTextFromParent != null) {
                    return translatedTextFromParent;
                }
            }
            return null;
        }
        // Language search order: the passed language, english or the first value
        final Translation translation = languageToTranslationsMap.getOrDefault(language,
                languageToTranslationsMap.getOrDefault("en",
                        languageToTranslationsMap.values().iterator().next()));
        return translation.translate(parameters);
    }
}
