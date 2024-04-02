package com.tk.next.app.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tk.next.app.service.StatusGetterService;
import com.tk.next.app.service.impl.StatusGetterServiceImpl;

/**
 * アプリケーションメイン.
 * @author Soma Takahashi
 */
public class ApplicationMain {
	/** ロガー. */
	private static final Logger log = LogManager.getLogger(ApplicationMain.class);
	
	/**
	 * アプリケーションを開始します.
	 * @param args 実行引数.
	 */
	public static void main(String[] args) {
		log.info("NatureRemoステータス取得アプリケーションを開始します。");
		
		// NatureRemoステータス取得サービス実行.
		final StatusGetterService statusGetterService = new StatusGetterServiceImpl();
		statusGetterService.execute();
		
		log.info("NatureRemoステータス取得アプリケーションを終了します。");
	}
}
