#NatureRemo状態 テーブル作成.

CREATE TABLE IF NOT EXISTS
	nature_remo_status(
		created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
		temperature VARCHAR(10) COMMENT '温度',
		humidity VARCHAR(10) COMMENT '湿度',
		brightness VARCHAR(10) COMMENT '明度',
		human_sensor VARCHAR(10) COMMENT '人感'
	) COMMENT = 'NatureRemo状態'
;
