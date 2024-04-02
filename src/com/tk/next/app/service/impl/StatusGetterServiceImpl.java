package com.tk.next.app.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tk.next.app.bean.entity.NatureRemoStatusEntity;
import com.tk.next.app.bean.response.NatureRemoResponse;
import com.tk.next.app.service.StatusGetterService;

/**
 * NatureRemoステータス取得サービス実装.
 * @author Soma Takahashi
 */
public class StatusGetterServiceImpl implements StatusGetterService {
	/** ロガー. */
	private final Logger log = LogManager.getLogger(getClass());
	
	/** プロパティファイルディレクトリ. */
	public static final String APP_PROPERTIES_DIR = "/apps/settings/remo-status-getter-lite/app.properties";
	
	@Override
	public void execute() {
		log.info("NatureRemoステータス取得サービスを開始します。");
		
		// プロパティファイル取得.
		final Properties prop = new Properties();
		try (InputStream stream = new FileInputStream(APP_PROPERTIES_DIR)) {
			prop.load(stream);
		} catch (IOException e) {
			log.error("プロパティファイル取得に失敗しました。", e);
		}
		
		// NatureRemoサーバー通信.
		final HttpClient httpClient = HttpClient.newHttpClient();
		final HttpRequest request = HttpRequest
			.newBuilder(URI.create(prop.getProperty("nature.remo.server.url")))
			.GET()
			.timeout(Duration.ofSeconds(10L))
			.header("Authorization", "Bearer " + prop.getProperty("nature.remo.access.token"))
			.header("Accept", "application/json")
			.header("Content-Type", "application/json")
			.build();
		
		final HttpResponse<String> response;
		try {
			 response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			log.error("NatureRemoサーバーとの通信に失敗しました。", e);
			return;
		}
		
		final int httpStatus = response.statusCode();
		if (httpStatus != 200) {
			log.error("通信処理に失敗しました。ステータスコード:" + httpStatus);
			return;
		}
		
		// NatureRemoサーバーからの応答データ解析.
		final List<NatureRemoResponse> responseDataList;
		try {
			responseDataList = new ObjectMapper().readValue(response.body(), new TypeReference<List<NatureRemoResponse>>() {});
		} catch (JsonProcessingException e) {
			log.error("NatureRemoサーバーからの応答JSONデータの解析に失敗しました。", e);
			return;
		}
		
		// テーブルエンティティ作成.
		NatureRemoStatusEntity entity = new NatureRemoStatusEntity();
		entity.setTemperature(responseDataList.get(0).getNewestEvents().getTe().get("val").toString());
		entity.setHumidity(responseDataList.get(0).getNewestEvents().getHu().get("val").toString());
		entity.setBrightness(responseDataList.get(0).getNewestEvents().getIl().get("val").toString());
		entity.setHumanSensor(responseDataList.get(0).getNewestEvents().getMo().get("val").toString());
		
		// レコード登録.
		try (Connection connection = getConnection(prop)){
			try (PreparedStatement statement = getStatement(connection, entity)) {
				try {
					final int count = statement.executeUpdate();
					if (count != 1) {
						log.error("レコード登録に失敗しました。登録件数: " + count);
					}
				} catch (SQLException e) {
					log.error("NatureRemo状態テーブルのレコード登録に失敗しました。", e);
				}
			} catch (SQLException e) {
				log.error("NatureRemo状態テーブルのインサート文生成に失敗しました。", e);
			}
		} catch (SQLException e) {
			log.error("DB接続に失敗しました。", e);
			return;
		}
		
		log.info("NatureRemoステータス取得サービスを終了します。");
	}

	/**
	 * DBコネクションを取得します.
	 * @param prop プロパティ.
	 * @return DBコネクション.
	 * @throws SQLException DB接続失敗時.
	 */
	private Connection getConnection(Properties prop) throws SQLException {
		return DriverManager.getConnection(
			prop.getProperty("db.host") + ":" + prop.getProperty("db.port") + "/" + prop.getProperty("db.name"),
			prop.getProperty("db.user"),
			prop.getProperty("db.password")
		);
	}
	
	/**
	 * ステートメントを取得します.
	 * @param connection DBコネクション.
	 * @param entity NatureRemo状態テーブルエンティティ.
	 * @return ステートメント.
	 * @throws SQLException SQL文不正時.
	 */
	private PreparedStatement getStatement(Connection connection, NatureRemoStatusEntity entity) throws SQLException {
		final PreparedStatement statement = connection.prepareStatement(
			"INSERT INTO nature_remo_status(created_date,temperature,humidity,brightness,human_sensor) VALUES(CURRENT_TIMESTAMP,?,?,?,?)"
		);
		statement.setString(1, entity.getTemperature());
		statement.setString(2, entity.getHumidity());
		statement.setString(3, entity.getBrightness());
		statement.setString(4, entity.getHumanSensor());
	
		return statement;
	}
}
