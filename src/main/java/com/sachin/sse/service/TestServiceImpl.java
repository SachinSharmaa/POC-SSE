package com.sachin.sse.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service("TestService")
public class TestServiceImpl implements TestService {

	final ConcurrentMap<SseEmitter,Integer> emitters = new ConcurrentHashMap<SseEmitter,Integer>();

	ObjectMapper mapper = new ObjectMapper();

	public final ApplicationEventPublisher eventPublisher;

	public TestServiceImpl(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void triggerNumber(int triggeredNumber,ConcurrentMap<SseEmitter,Integer> emitters) throws Exception {
		emitters.forEach((emitter,number) -> {
			if(number==triggeredNumber)
			{
				ObjectNode object = mapper.createObjectNode();
				object.put("number", number) ;
				this.eventPublisher.publishEvent(object);
			}
		});
	}

}
