package com.sachin.sse.service;

import java.util.concurrent.ConcurrentMap;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface TestService {
	
		public void triggerNumber(int triggeredNumber,ConcurrentMap<SseEmitter,Integer> emitters) throws Exception;

}
