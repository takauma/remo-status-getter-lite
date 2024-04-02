package com.tk.next.app.bean.response;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * NatureRemoサーバー応答.
 * @author Soma Takahashi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class NatureRemoResponse {
	@JsonProperty("newest_events")
	private NewestEvents newestEvents;
	
	@Data
	public class NewestEvents {
		@JsonProperty
		private Map<String, Object> hu = new HashMap<>();
		@JsonProperty
		private Map<String, Object> il = new HashMap<>();
		@JsonProperty
		private Map<String, Object> mo = new HashMap<>();
		@JsonProperty
		private Map<String, Object> te = new HashMap<>();
	}
}
