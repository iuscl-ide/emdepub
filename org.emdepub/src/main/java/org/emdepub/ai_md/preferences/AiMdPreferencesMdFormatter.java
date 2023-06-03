package org.emdepub.ai_md.preferences;

import org.emdepub.common.utils.CU;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(Include.NON_NULL)
@FieldNameConstants(asEnum = true, innerTypeName = "AiMdPreferencesMdFormatterField", level = AccessLevel.PUBLIC)
public class AiMdPreferencesMdFormatter {

	@JsonProperty("Format Right Margin Wrap")
	Boolean formatRightMarginWrap;

	@JsonProperty("Format Right Margin Columns")
	Integer formatRightMarginColumns;

	@JsonProperty("Format Collapse Line Whitespace")
	Boolean formatCollapseLineWhitespace;

	@JsonProperty("Format Collapse Empty Lines")
	Boolean formatCollapseEmptyLines;

	@JsonProperty("Format Collapse Trailing Empty Lines")
	Boolean formatCollapseTrailingEmptyLines;

	private AiMdPreferencesMdFormatter() {
		super();
	}

	public static AiMdPreferencesMdFormatter create() {
		
		AiMdPreferencesMdFormatter aiMdPreferencesMdFormatter = new AiMdPreferencesMdFormatter();
		aiMdPreferencesMdFormatter.reset();
		
		return aiMdPreferencesMdFormatter;
	}

	public Boolean findDefaultFormatRightMarginWrap() {
		return false;
	}

	public Integer findDefaultFormatRightMarginColumns() {
		return 80;
	}

	public Boolean findDefaultFormatCollapseLineWhitespace() {
		return false;
	}

	public Boolean findDefaultFormatCollapseEmptyLines() {
		return true;
	}

	public Boolean findDefaultFormatCollapseTrailingEmptyLines() {
		return true;
	}

	public void reset() {

		AiMdPreferences.reset(this, AiMdPreferencesMdFormatterField.values());
	}

	public void load(String preferencesFileName) {
		
		if (!CU.findIfFileExists(preferencesFileName)) {
			reset();

			return;
		}
		
		AiMdPreferencesMdFormatter deserialized = CU.yamlDeserializeFromFile(preferencesFileName, AiMdPreferencesMdFormatter.class);

		AiMdPreferences.load(this, AiMdPreferencesMdFormatterField.values(), deserialized);
	}
	
	public void save(String preferencesFileName) {
		
		AiMdPreferencesMdFormatter serialized = new AiMdPreferencesMdFormatter();
		
		AiMdPreferences.save(this, AiMdPreferencesMdFormatterField.values(), serialized, preferencesFileName);
	}
}
