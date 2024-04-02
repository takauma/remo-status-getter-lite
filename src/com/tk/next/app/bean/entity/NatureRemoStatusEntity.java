package com.tk.next.app.bean.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * NatureRemo状態テーブルエンティティ.
 * @author Soma Takahashi
 */
@Data
public class NatureRemoStatusEntity {
	/** 作成日時. */
	private LocalDateTime createdDate;
	/** 温度. */
	private String temperature;
	/** 湿度. */
	private String humidity;
	/** 明度. */
	private String brightness;
	/** 人感. */
	private String humanSensor;
}
