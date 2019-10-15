package com.sachin.sse.controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sachin.sse.service.TestService;

@RestController
@RequestMapping("/v1")
public class TestController {
	
	@Autowired
	TestService service;
	
	private final ConcurrentMap<SseEmitter,Integer> emitters = new ConcurrentHashMap<SseEmitter,Integer>();
	
	
	@RequestMapping(value = "/testEmitter/{number}", method = RequestMethod.GET)
	  public SseEmitter handle(@PathVariable int number) {
	    SseEmitter emitter = new SseEmitter();
	    System.out.println("Adding emitter for number : " + number);
	    this.emitters.put(emitter,number) ;
	    emitter.onCompletion(() -> this.emitters.remove(emitter));
	    emitter.onTimeout(() -> this.emitters.remove(emitter));

	    return emitter;
	  }
	  
	  @RequestMapping(value = "/testTrigger/{triggeredNumber}", method = RequestMethod.GET)
	  public int triggerEvent(@PathVariable int triggeredNumber) {
		  try {
			service.triggerNumber(triggeredNumber,this.emitters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		  return triggeredNumber ;
	  }

	  @EventListener
	  public void onEvent(ObjectNode object) {
	    this.emitters.forEach((emitter,number) -> {
	    	if(object.get("number").asInt()==number){
	    		try {
	    	        emitter.send(object);
	    	      }
	    	      catch (Exception e) {
	    	    	  System.out.println("Removing emitter for number : " + number);
	    	    	  this.emitters.remove(emitter) ;
	    	      }
	    	}
	    });
	  }
	}